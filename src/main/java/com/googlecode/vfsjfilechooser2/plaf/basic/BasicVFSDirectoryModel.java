/*
 *
 * Copyright (C) 2008-2009 Yves Zoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package com.googlecode.vfsjfilechooser2.plaf.basic;


import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants;
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileSystemView;
import com.googlecode.vfsjfilechooser2.plaf.metal.MetalVFSFileChooserUI;
import com.googlecode.vfsjfilechooser2.utils.FileObjectComparatorFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;


/**
 * The DirectoryModel implementation based on Swing BasicDirectoryModel
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>, Jason Harrop <jasonharrop at users.sourceforge.net>
 * @version 0.0.1
 */
@SuppressWarnings("serial")
public class BasicVFSDirectoryModel extends AbstractListModel
    implements PropertyChangeListener
{
    private static final Comparator<FileObject> fileNameComparator = FileObjectComparatorFactory.newFileNameComparator(true);
    private VFSJFileChooser filechooser = null;
    private final List<FileObject> fileCache = new ArrayList<FileObject>();
    private ReadWriteLock aLock = new ReentrantReadWriteLock(true);
    private volatile Future<?> loadThread = null;
    private ExecutorService executor;
    private List<FileObject> files = null;
    private List<FileObject> directories = null;
    private int fetchID = 0;
    private PropertyChangeSupport changeSupport;
    private boolean busy = false;

    /**
     *
     * @param filechooser
     */
    public BasicVFSDirectoryModel(VFSJFileChooser filechooser)
    {
        this.filechooser = filechooser;
        this.executor = Executors.newCachedThreadPool();
        validateFileCache();
    }

    /**
     *
     * @param e
     */
    public void propertyChange(PropertyChangeEvent e)
    {
        String prop = e.getPropertyName();

        if ((prop.equals(VFSJFileChooserConstants.DIRECTORY_CHANGED_PROPERTY)) ||
                (prop.equals(
                    VFSJFileChooserConstants.FILE_VIEW_CHANGED_PROPERTY)) ||
                (prop.equals(
                    VFSJFileChooserConstants.FILE_FILTER_CHANGED_PROPERTY)) ||
                (prop.equals(
                    VFSJFileChooserConstants.FILE_HIDING_CHANGED_PROPERTY)) ||
                (prop.equals(
                    VFSJFileChooserConstants.FILE_SELECTION_MODE_CHANGED_PROPERTY)))
        {
            validateFileCache();
        }
        else if ("UI".equals(prop))
        {
            Object old = e.getOldValue();

            if (old instanceof BasicVFSFileChooserUI)
            {
                BasicVFSFileChooserUI ui = (BasicVFSFileChooserUI) old;
                BasicVFSDirectoryModel model = ui.getModel();

                if (model != null)
                {
                    model.invalidateFileCache();
                }
            }
        }
        else if ("JFileChooserDialogIsClosingProperty".equals(prop))
        {
            invalidateFileCache();
        }
    }

    /**
     * This method is used to interrupt file loading thread.
     */
    public void invalidateFileCache()
    {
        if (loadThread != null)
        {
            loadThread.cancel(true);
            loadThread = null;
        }
    }

    /**
     *
     * @return
     */
    public List<FileObject> getFiles()
    {
        aLock.readLock().lock();

        try
        {
            if (files != null)
            {
                return files;
            }

            files = new CopyOnWriteArrayList<FileObject>();
            directories = new CopyOnWriteArrayList<FileObject>();

            FileObject currentDir = filechooser.getCurrentDirectoryObject();
            AbstractVFSFileSystemView v = filechooser.getFileSystemView();
            directories.add(v.createFileObject(currentDir, ".."));

            for (FileObject f : fileCache)
            {
                if (filechooser.isTraversable(f))
                {
                    directories.add(f);
                }
                else
                {
                    files.add(f);
                }
            }

            return files;
        }
        finally
        {
            aLock.readLock().unlock();
        }
    }

    /**
     *
     */
    public void validateFileCache()
    {
        FileObject currentDirectory = filechooser.getCurrentDirectoryObject();

        if (currentDirectory == null)
        {
            return;
        }

        try
        {
            currentDirectory.refresh();
        }
        catch (FileSystemException ex)
        {
        }

        if (loadThread != null)
        {
            loadThread.cancel(true);
        }

        setBusy(true, ++fetchID);

        loadThread = executor.submit(new LoadFilesThread(currentDirectory,
                    fetchID));
    }

    /**
     * Renames a file in the underlying file system.
     *
     * @param oldFile a <code>File</code> object representing
     *        the existing file
     * @param newFile a <code>File</code> object representing
     *        the desired new file name
     * @return <code>true</code> if rename succeeded,
     *        otherwise <code>false</code>
     * @since 1.4
     */
    public boolean renameFile(FileObject oldFile, FileObject newFile)
    {
        aLock.writeLock().lock();

        try
        {
            oldFile.moveTo(newFile);
            validateFileCache();

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            aLock.writeLock().unlock();
        }
    }

    /**
     *
     */
    public void fireContentsChanged()
    {
        fireContentsChanged(this, 0, getSize() - 1);
    }

    public int getSize()
    {
        return fileCache.size();
    }

    /**
     *
     * @param o
     * @return
     */
    public boolean contains(Object o)
    {
        return fileCache.contains(o);
    }

    /**
     * @param o
     * @return
     */
    public int indexOf(Object o)
    {
        return fileCache.indexOf(o);
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        return fileCache.get(index);
    }

    /**
     * @param comparator
     */
    public void sort(Comparator<FileObject> comparator)
    {
        Collections.sort(fileCache, comparator);
    }

    /**
     *
     * @param v
     */
    protected void sort(List<FileObject> v)
    {
        Collections.sort(v, fileNameComparator);
    }

    /**
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class.
     * <p>
     * If <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param    listener  the property change listener to be added
     *
     * @see #removePropertyChangeListener
     * @see #getPropertyChangeListeners
     *
     * @since 1.6
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if (changeSupport == null)
        {
            changeSupport = new PropertyChangeSupport(this);
        }

        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * <p>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be removed
     *
     * @see #addPropertyChangeListener
     * @see #getPropertyChangeListeners
     *
     * @since 1.6
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (changeSupport != null)
        {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * Returns an array of all the property change listeners
     * registered on this component.
     *
     * @return all of this component's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered
     *
     * @see      #addPropertyChangeListener
     * @see      #removePropertyChangeListener
     * @see      java.beans.PropertyChangeSupport#getPropertyChangeListeners
     *
     * @since 1.6
     */
    public PropertyChangeListener[] getPropertyChangeListeners()
    {
        if (changeSupport == null)
        {
            return new PropertyChangeListener[0];
        }

        return changeSupport.getPropertyChangeListeners();
    }

    /**
     * Support for reporting bound property changes for boolean properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     *
     * @since 1.6
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
        Object newValue)
    {
        if (changeSupport != null)
        {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Set the busy state for the model. The model is considered
     * busy when it is running a separate (interruptable)
     * thread in order to load the contents of a directory.
     */
    private void setBusy(final boolean busy, int fid)
    {
        aLock.writeLock().lock();

        try
        {
            if (fid == fetchID)
            {
                boolean oldValue = this.busy;
                this.busy = busy;

                if ((changeSupport != null) && (busy != oldValue))
                {
                    Runnable r = (new Runnable()
                        {
                            public void run()
                            {
                                firePropertyChange("busy", !busy, busy);
                            }
                        });

                    if (SwingUtilities.isEventDispatchThread())
                    {
                        r.run();
                    }
                    else
                    {
                        SwingUtilities.invokeLater(r);
                    }
                }
            }
        }
        finally
        {
            aLock.writeLock().unlock();
        }
    }

    class LoadFilesThread implements Runnable
    {
        private int fid;
        private Queue<DoChangeContents> runnables = new ConcurrentLinkedQueue<DoChangeContents>();

        public LoadFilesThread(FileObject currentDirectory, int fid)
        {
            this.fid = fid;
        }

        private void invokeLater(DoChangeContents runnable)
        {
            runnables.add(runnable);

            if (SwingUtilities.isEventDispatchThread())
            {
                runnable.run();
            }
            else
            {
                SwingUtilities.invokeLater(runnable);
            }
        }

        public void run()
        {
            run0();
            setBusy(false, fid);
        }

        public void run0()
        {
            AbstractVFSFileSystemView fileSystem = filechooser.getFileSystemView();

            FileObject cwd = filechooser.getCurrentDirectoryObject();

            // fix a bug here when the filesystem changes, the directories list needs to be notified
            if (!contains(cwd))
            {
                MetalVFSFileChooserUI ui = (MetalVFSFileChooserUI) filechooser.getUI();
                ui.getCombo().setSelectedItem(cwd);
            }

            FileObject[] list = fileSystem.getFiles(cwd,
                    filechooser.isFileHidingEnabled());

            List<FileObject> acceptsList = new ArrayList<FileObject>(list.length);

            if ((loadThread != null) && loadThread.isCancelled())
            {
                return;
            }

            // run through the file list, add directories and selectable files to fileCache
            for (FileObject aFileObject : list)
            {
                if (filechooser.accept(aFileObject))
                {
                    acceptsList.add(aFileObject);
                }
            }

            if ((loadThread != null) && loadThread.isCancelled())
            {
                cancelRunnables();

                return;
            }

            // First sort alphabetically by filename
            sort(acceptsList);

            final int mid = acceptsList.size() >> 1;

            List<FileObject> newDirectories = new ArrayList<FileObject>(mid);
            List<FileObject> newFiles = new ArrayList<FileObject>(mid);

            // run through list grabbing directories in chunks of ten
            for (FileObject f : acceptsList)
            {
                boolean isTraversable = filechooser.isTraversable(f);

                if (isTraversable)
                {
                    newDirectories.add(f);
                }
                else
                {
                    newFiles.add(f);
                }

                if ((loadThread != null) && loadThread.isCancelled())
                {
                    cancelRunnables();

                    return;
                }
            }

            List<FileObject> newFileCache = new ArrayList<FileObject>(newDirectories);
            newFileCache.addAll(newFiles);

            int newSize = newFileCache.size();
            int oldSize = fileCache.size();

            if (newSize > oldSize)
            {
                //see if interval is added
                int start = oldSize;
                int end = newSize;

                for (int i = 0; i < oldSize; i++)
                {
                    if (!newFileCache.get(i).equals(fileCache.get(i)))
                    {
                        start = i;

                        for (int j = i; j < newSize; j++)
                        {
                            if (newFileCache.get(j).equals(fileCache.get(i)))
                            {
                                end = j;

                                break;
                            }
                        }

                        break;
                    }
                }

                if ((start >= 0) && (end > start) &&
                        newFileCache.subList(end, newSize)
                                        .equals(fileCache.subList(start, oldSize)))
                {
                    if ((loadThread != null) && loadThread.isCancelled())
                    {
                        cancelRunnables();

                        return;
                    }

                    invokeLater(new DoChangeContents(newFileCache.subList(
                                start, end), start, null, 0, fid));
                    newFileCache = null;
                }
            }
            else if (newSize < oldSize)
            {
                //see if interval is removed
                int start = -1;
                int end = -1;

                for (int i = 0; i < newSize; i++)
                {
                    if (!newFileCache.get(i).equals(fileCache.get(i)))
                    {
                        start = i;
                        end = (i + oldSize) - newSize;

                        break;
                    }
                }

                if ((start >= 0) && (end > start) &&
                        fileCache.subList(end, oldSize)
                                     .equals(newFileCache.subList(start, newSize)))
                {
                    if ((loadThread != null) && loadThread.isCancelled())
                    {
                        cancelRunnables(runnables);

                        return;
                    }

                    invokeLater(new DoChangeContents(null, 0,
                            new ArrayList<FileObject>(fileCache.subList(start,
                                    end)), start, fid));
                    newFileCache = null;
                }
            }

            if ((newFileCache != null) && !fileCache.equals(newFileCache))
            {
                if ((loadThread != null) && loadThread.isCancelled())
                {
                    cancelRunnables(runnables);

                    return;
                }

                invokeLater(new DoChangeContents(newFileCache, 0, fileCache, 0,
                        fid));
            }
        }

        public void cancelRunnables(Queue<DoChangeContents> runnables)
        {
            DoChangeContents runnable = null;

            while ((runnable = runnables.poll()) != null)
            {
                runnable.cancel();
            }
        }

        public void cancelRunnables()
        {
            cancelRunnables(runnables);
        }
    }

    class DoChangeContents implements Runnable
    {
        private List<FileObject> addFiles;
        private List<FileObject> remFiles;
        private boolean doFire = true;
        private int fid;
        private int addStart = 0;
        private int remStart = 0;

        public DoChangeContents(List<FileObject> addFiles, int addStart,
            List<FileObject> remFiles, int remStart, int fid)
        {
            this.addFiles = addFiles;
            this.addStart = addStart;
            this.remFiles = remFiles;
            this.remStart = remStart;
            this.fid = fid;
        }

        void cancel()
        {
            aLock.writeLock().lock();

            try
            {
                doFire = false;
            }
            finally
            {
                aLock.writeLock().unlock();
            }
        }

        public void run()
        {
            if ((fetchID == fid) && doFire)
            {
                int remSize = (remFiles == null) ? 0 : remFiles.size();
                int addSize = (addFiles == null) ? 0 : addFiles.size();

                aLock.writeLock().lock();

                try
                {
                    if (remSize > 0)
                    {
                        fileCache.removeAll(remFiles);
                    }

                    if (addSize > 0)
                    {
                        fileCache.addAll(addStart, addFiles);
                    }

                    files = null;
                    directories = null;
                }
                finally
                {
                    aLock.writeLock().unlock();
                }

                if ((remSize > 0) && (addSize == 0))
                {
                    fireIntervalRemoved(BasicVFSDirectoryModel.this, remStart,
                        (remStart + remSize) - 1);
                }
                else if ((addSize > 0) && (remSize == 0) &&
                        (fileCache.size() > addSize))
                {
                    fireIntervalAdded(BasicVFSDirectoryModel.this, addStart,
                        (addStart + addSize) - 1);
                }
                else
                {
                    fireContentsChanged();
                }
            }
        }
    }
}
