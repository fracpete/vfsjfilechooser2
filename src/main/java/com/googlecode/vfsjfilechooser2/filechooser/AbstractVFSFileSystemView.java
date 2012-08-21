/*
 * The implementation using commons-vfs based on Swing FileSystemView
 *
 * Copyright (C) 2005-2008 Yves Zoundi
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
package com.googlecode.vfsjfilechooser2.filechooser;


import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.DecoratedFileObject;
import org.apache.commons.vfs2.provider.local.LocalFile;

import com.googlecode.vfsjfilechooser2.utils.SwingCommonsUtilities;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.text.MessageFormat;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;


/**
 * The implementation using commons-vfs based on Swing FileSystemView
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public abstract class AbstractVFSFileSystemView
{
    //static FileSystemView macFileSystemView = null;
    static AbstractVFSFileSystemView genericFileSystemView = null;
    static boolean useSystemExtensionsHiding = false;
    FileObject[] localRoots = new FileObject[0];

    public static AbstractVFSFileSystemView getFileSystemView()
    {
        useSystemExtensionsHiding = UIManager.getDefaults()
                                             .getBoolean("FileChooser.useSystemExtensionHiding");
        UIManager.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent e)
                {
                    if (e.getPropertyName().equals("lookAndFeel"))
                    {
                        useSystemExtensionsHiding = UIManager.getDefaults()
                                                             .getBoolean("FileChooser.useSystemExtensionHiding");
                    }
                }
            });

        if (genericFileSystemView == null)
        {
            genericFileSystemView = new GenericFileSystemView();
        }

        return genericFileSystemView;
    }

    /**
     * Determines if the given file is a root in the navigatable tree(s).
     * Examples: Windows 98 has one root, the Desktop folder. DOS has one root
     * per drive letter, <code>C:\</code>, <code>D:\</code>, etc. Unix has one root,
     * the <code>"/"</code> directory.
     *
     * The default implementation gets information from the <code>ShellFolder</code> class.
     *
     * @param f a <code>File</code> object representing a directory
     * @return <code>true</code> if <code>f</code> is a root in the navigatable tree.
     * @see #isFileSystemRoot
     */
    public boolean isRoot(FileObject f)
    {
        return VFSUtils.isRoot(f);
    }

    /**
     * Returns true if the file (directory) can be visited.
     * Returns false if the directory cannot be traversed.
     *
     * @param f the <code>File</code>
     * @return <code>true</code> if the file/directory can be traversed, otherwise <code>false</code>
     * @see JFileChooser#isTraversable
     * @see FileView#isTraversable
     * @since 1.4
     */
    public Boolean isTraversable(FileObject f)
    {
        return Boolean.valueOf(VFSUtils.isDirectory(f));
    }

    /**
     * Name of a file, directory, or folder as it would be displayed in
     * a system file browser. Example from Windows: the "M:\" directory
     * displays as "CD-ROM (M:)"
     *
     * The default implementation gets information from the ShellFolder class.
     *
     * @param f a <code>File</code> object
     * @return the file name as it would be displayed by a native file chooser
     * @see JFileChooser#getName
     * @since 1.4
     */
    public String getSystemDisplayName(FileObject f)
    {
        String name = null;

        if (f != null)
        {
            name = f.getName().getBaseName();

            if (!name.trim().equals(""))
            {
                name = VFSUtils.getFriendlyName(f.getName() + "");
            }
        }

        return name;
    }

    /**
     * Type description for a file, directory, or folder as it would be displayed in
     * a system file browser. Example from Windows: the "Desktop" folder
     * is desribed as "Desktop".
     *
     * Override for platforms with native ShellFolder implementations.
     *
     * @param f a <code>File</code> object
     * @return the file type description as it would be displayed by a native file chooser
     * or null if no native information is available.
     * @see JFileChooser#getTypeDescription
     * @since 1.4
     */
    public String getSystemTypeDescription(FileObject f)
    {
        return VFSUtils.getFriendlyName(f.getName().toString());
    }

    /**
     * Icon for a file, directory, or folder as it would be displayed in
     * a system file browser. Example from Windows: the "M:\" directory
     * displays a CD-ROM icon.
     *
     * The default implementation gets information from the ShellFolder class.
     *
     * @param f a <code>File</code> object
     * @return an icon as it would be displayed by a native file chooser
     * @see JFileChooser#getIcon
     * @since 1.4
     */
    public Icon getSystemIcon(FileObject f)
    {
        if (f != null)
        {
            return UIManager.getIcon(VFSUtils.isDirectory(f)
                ? "FileView.directoryIcon" : "FileView.fileIcon");
        }
        else
        {
            return null;
        }
    }

    /**
     * On Windows, a file can appear in multiple folders, other than its
     * parent directory in the filesystem. Folder could for example be the
     * "Desktop" folder which is not the same as file.getParentFile().
     *
     * @param folder a <code>File</code> object repesenting a directory or special folder
     * @param file a <code>File</code> object
     * @return <code>true</code> if <code>folder</code> is a directory or special folder and contains <code>file</code>.
     * @since 1.4
     */
    public boolean isParent(FileObject folder, FileObject file)
    {
        return VFSUtils.isParent(folder, file);
    }

    /**
     *
     * @param parent a <code>File</code> object repesenting a directory or special folder
     * @param fileName a name of a file or folder which exists in <code>parent</code>
     * @return a File object. This is normally constructed with <code>new
     * File(parent, fileName)</code> except when parent and child are both
     * special folders, in which case the <code>File</code> is a wrapper containing
     * a <code>ShellFolder</code> object.
     * @since 1.4
     */
    public FileObject getChild(FileObject parent, String fileName)
    {
        return createFileObject(parent, fileName);
    }

    /**
     * Checks if <code>f</code> represents a real directory or file as opposed to a
     * special folder such as <code>"Desktop"</code>. Used by UI classes to decide if
     * a folder is selectable when doing directory choosing.
     *
     * @param f a <code>File</code> object
     * @return <code>true</code> if <code>f</code> is a real file or directory.
     * @since 1.4
     */
    public boolean isFileSystem(FileObject f)
    {
        return true;
    }

    /**
     * Creates a new folder with a default folder name.
     * @param containingDir
     * @return
     * @throws org.apache.commons.vfs.FileSystemException
     */
    public abstract FileObject createNewFolder(FileObject containingDir)
        throws FileSystemException;

    /**
     * Returns whether a file is hidden or not.
     * @param f
     * @return
     */
    public boolean isHiddenFile(FileObject f)
    {
        return VFSUtils.isHiddenFile(f);
    }

    /**
     * Is dir the root of a tree in the file system, such as a drive
     * or partition. Example: Returns true for "C:\" on Windows 98.
     *
     * @param dir a <code>File</code> object representing a directory
     * @return <code>true</code> if <code>f</code> is a root of a filesystem
     * @see #isRoot
     * @since 1.4
     */
    public boolean isFileSystemRoot(FileObject dir)
    {
        return VFSUtils.isRoot(dir);
    }

    /**
     * Used by UI classes to decide whether to display a special icon
     * for drives or partitions, e.g. a "hard disk" icon.
     *
     * The default implementation has no way of knowing, so always returns false.
     *
     * @param dir a directory
     * @return <code>false</code> always
     * @since 1.4
     */
    public boolean isDrive(FileObject dir)
    {
        return VFSUtils.getParentDirectory(dir) == null;
    }

    /**
     * Used by UI classes to decide whether to display a special icon
     * for a floppy disk. Implies isDrive(dir).
     *
     * The default implementation has no way of knowing, so always returns false.
     *
     * @param dir a directory
     * @return <code>false</code> always
     * @since 1.4
     */
    public boolean isFloppyDrive(FileObject dir)
    {
        return false;
    }

    /**
     * Used by UI classes to decide whether to display a special icon
     * for a computer node, e.g. "My Computer" or a network server.
     *
     * The default implementation has no way of knowing, so always returns false.
     *
     * @param dir a directory
     * @return <code>false</code> always
     * @since 1.4
     */
    public boolean isComputerNode(FileObject dir)
    {
        return false;
    }

    /**
     * Returns all root partitions on this system. For example, on
     * Windows, this would be the "Desktop" folder, while on DOS this
     * would be the A: through Z: drives.
     * @param fo
     * @return
     */
    public FileObject[] getRoots(FileObject fo)
    {
        if (fo instanceof DecoratedFileObject)
        {
            fo = ((DecoratedFileObject) fo).getDecoratedFileObject();
        }

        if (fo instanceof LocalFile)
        {
            File[] roots = File.listRoots();
            final int count = roots.length;
            localRoots = new FileObject[roots.length];

            for (int i = 0; i < count; i++)
            {
                localRoots[i] = VFSUtils.toFileObject(roots[i]);
            }

            return localRoots.clone();
        }

        // Don't cache this array, because filesystem might change
        else
        {
            FileObject p = VFSUtils.getRootFileSystem(fo);

            return new FileObject[] { p };
        }
    }

    // Providing default implementations for the remaining methods
    // because most OS file systems will likely be able to use this
    // code. If a given OS can't, override these methods in its
    // implementation.
    /**
     *
     * @return
     */
    public FileObject getHomeDirectory()
    {
        return SwingCommonsUtilities.getVFSFileChooserDefaultDirectory();
    }

    /**
     * Return the user's default starting directory for the file chooser.
     *
     * @return a <code>File</code> object representing the default
     *         starting folder
     * @since 1.4
     */
    public FileObject getDefaultDirectory()
    {
        return createFileObject(System.getProperty("user.dir"));
    }

    /**
     * Returns a File object constructed in dir from the given filename.
     * @param dir
     * @param filename
     * @return
     */
    public FileObject createFileObject(FileObject dir, String filename)
    {
        if (dir == null)
        {
            return VFSUtils.createFileObject(filename);
        }
        else
        {
            return VFSUtils.resolveFileObject(dir, filename);
        }
    }

    /**
     * Returns a File object constructed from the given path string.
     * @param path
     * @return
     */
    public FileObject createFileObject(String path)
    {
        return VFSUtils.resolveFileObject(path);
    }

    /**
     * Gets the list of shown (i.e. not hidden) files.
     * @param dir
     * @param useFileHiding
     * @return
     */
    public FileObject[] getFiles(FileObject dir, boolean useFileHiding)
    {
        return VFSUtils.getFiles(dir, useFileHiding);
    }

    /**
     * Returns the parent directory of <code>dir</code>.
     * @param dir the <code>File</code> being queried
     * @return the parent directory of <code>dir</code>, or
     *   <code>null</code> if <code>dir</code> is <code>null</code>
     */
    public FileObject getParentDirectory(FileObject dir)
    {
        if ((dir != null) && VFSUtils.exists(dir))
        {
            FileObject parentDir = VFSUtils.getParentDirectory(dir);

            if (parentDir == null)
            {
                return dir;
            }
            else
            {
                return parentDir;
            }
        }

        return null;
    }

    /**
     * Creates a new <code>File</code> object for <code>f</code> with correct
     * behavior for a file system root directory.
     *
     * @param f a <code>File</code> object representing a file system root
     *          directory, for example "/" on Unix or "C:\" on Windows.
     * @return a new <code>File</code> object
     * @since 1.4
     */
    protected FileObject createFileSystemRoot(FileObject f)
    {
        return VFSUtils.createFileSystemRoot(f);
    }

    /**
     * Fallthrough FileSystemView in case we can't determine the OS.
     */
    static class GenericFileSystemView extends AbstractVFSFileSystemView
    {
        private static final String newFolderString = VFSResources.getMessage(
                "VFSJFileChooser.other.newFolder");
        private static final String newFolderNextString = VFSResources.getMessage(
                "VFSJFileChooser.other.newFolder.subsequent");

        /**
         * Creates a new folder with a default folder name.
         */
        public FileObject createNewFolder(FileObject containingDir)
            throws FileSystemException
        {
            if (containingDir == null)
            {
                throw new FileSystemException(
                    "Trying to create a new folder into a non existing folder");
            }

            FileObject newFolder = null;

            // Using NT's default folder name
            newFolder = createFileObject(containingDir, newFolderString);

            // avoid creating a folder called New Folder so we loop as in the 
            // Windows FileSystemView
            for (int i = 1; newFolder.exists(); i++)
            {
                newFolder = createFileObject(containingDir,
                        MessageFormat.format(newFolderNextString,
                            new Object[] { i }));
            }

            // if the folder already exists throw an exception
            if (newFolder.exists())
            {
                throw new FileSystemException("Directory already exists:" +
                    newFolder.getName().getURI());
            }
            else
            {
                // create the folder 
                newFolder.createFolder();
            }

            // return the created folder if no exception is thrown
            return newFolder;
        }
    }
}
