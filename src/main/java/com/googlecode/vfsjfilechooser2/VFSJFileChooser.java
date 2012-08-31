/*
 * The filechooser class with commons-VFS abstraction layer based on JFileChooser
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
package com.googlecode.vfsjfilechooser2;

import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.ACCESSORY_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.APPROVE_SELECTION;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.CANCEL_SELECTION;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.DIALOG_TITLE_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.DIALOG_TYPE_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.DIRECTORY_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.FILE_FILTER_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.FILE_HIDING_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.FILE_SELECTION_MODE_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.FILE_SYSTEM_VIEW_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.FILE_VIEW_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.MULTI_SELECTION_ENABLED_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.SELECTED_FILES_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.SELECTED_FILE_CHANGED_PROPERTY;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.SHOW_HIDDEN_PROP;
import static com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants.uiClassID;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileObject;

import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileFilter;
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileSystemView;
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileView;
import com.googlecode.vfsjfilechooser2.plaf.AbstractVFSFileChooserUI;
import com.googlecode.vfsjfilechooser2.plaf.metal.MetalVFSFileChooserUI;
import com.googlecode.vfsjfilechooser2.utils.DefaultFileObjectConverter;
import com.googlecode.vfsjfilechooser2.utils.FileObjectConverter;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

/**
 * The filechooser class with commons-VFS abstraction layer based on JFileChooser
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
@SuppressWarnings("serial")
public class VFSJFileChooser extends JComponent implements Accessible
{
    private static final Frame SHARED_FRAME = new Frame();
    private static final FileObject[] EMPTY_FILEOBJECT_ARRAY = new FileObject[]{};

    // ******************************
    // ***** instance variables *****
    // ******************************
    private String dialogTitle = null;
    private String approveButtonText = null;
    private String approveButtonToolTipText = null;
    private int approveButtonMnemonic = 0;
    private List<AbstractVFSFileFilter> filters = new CopyOnWriteArrayList<AbstractVFSFileFilter>();
    private JDialog dialog = null;
    private DIALOG_TYPE dialogType = DIALOG_TYPE.OPEN;
    private RETURN_TYPE returnValue = RETURN_TYPE.ERROR;
    private JComponent accessory = null;
    private AbstractVFSFileView fileView = null;

    // uiFileView is not serialized, as it is initialized
    // by updateUI() after deserialization
    private transient AbstractVFSFileView uiFileView = null;
    private boolean controlsShown = true;
    private boolean useFileHiding = true;

    // Listens to changes in the native setting for showing hidden files.
    // The Listener is removed and the native setting is ignored if
    // setFileHidingEnabled() is ever called.
    private transient PropertyChangeListener showFilesListener = null;
    private SELECTION_MODE fileSelectionMode = SELECTION_MODE.FILES_ONLY;
    private boolean multiSelectionEnabled = false;
    private boolean useAcceptAllFileFilter = true;
    private boolean dragEnabled = false;
    private AbstractVFSFileFilter fileFilter = null;
    private AbstractVFSFileSystemView fileSystemView = null;
    private FileObject currentDirectory = null;
    private FileObject selectedFile = null;
    private FileObject[] selectedFiles;

    // for converting files
    protected FileObjectConverter fileObjectConverter = new DefaultFileObjectConverter();
    
    // Accessibility support 
    protected AccessibleContext m_accessibleContext = null;

    // Pluggable L&F  
    private MetalVFSFileChooserUI defaultUI;

    /**
     * Constructs a <code>VFSJFileChooser</code> pointing to the user's
     * default directory. This default depends on the operating system.
     * It is typically the "My Documents" folder on Windows, and the
     * user's home directory on Unix.
     */
    public VFSJFileChooser()
    {
        this((FileObject) null, (AbstractVFSFileSystemView) null);
    }

    /**
     * Constructs a <code>VFSJFileChooser</code> using the given path.
     * Passing in a <code>null</code>
     * string causes the file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * @param currentDirectoryPath  a <code>String</code> giving the path
     *                          to a file or directory
     */
    public VFSJFileChooser(String currentDirectoryPath)
    {
        this(currentDirectoryPath, (AbstractVFSFileSystemView) null);
    }

    /**
     * Constructs a <code>VFSJFileChooser</code> using the given <code>File</code>
     * as the path. Passing in a <code>null</code> file
     * causes the file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * @param currentDirectory  a <code>File</code> object specifying
     *                          the path to a file or directory
     */
    public VFSJFileChooser(File currentDirectory)
    {
      this(VFSUtils.toFileObject(currentDirectory));
    }

    /**
     * Constructs a <code>VFSJFileChooser</code> using the given <code>File</code>
     * as the path. Passing in a <code>null</code> file
     * causes the file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * @param currentDirectory  a <code>File</code> object specifying
     *                          the path to a file or directory
     */
    public VFSJFileChooser(FileObject currentDirectory)
    {
        this(currentDirectory, (AbstractVFSFileSystemView) null);
    }

    /**
     * Constructs a <code>VFSJFileChooser</code> using the given
     * <code>FileSystemView</code>.
     */
    public VFSJFileChooser(AbstractVFSFileSystemView fsv)
    {
        this((FileObject) null, fsv);
    }

    /**
     * Constructs a <code>VFSJFileChooser</code> using the given current directory
     * and <code>FileSystemView</code>.
     */
    public VFSJFileChooser(FileObject currentDirectory,
        AbstractVFSFileSystemView fsv)
    {
        setup(fsv);
        setCurrentDirectoryObject(currentDirectory);
    }

    /**
     * Constructs a <code>VFSJFileChooser</code> using the given current directory
     * path and <code>FileSystemView</code>.
     * @param currentDirectoryPath
     * @param fsv
     */
    public VFSJFileChooser(String currentDirectoryPath,
        AbstractVFSFileSystemView fsv)
    {
        setup(fsv);

        if (currentDirectoryPath == null)
        {
            setCurrentDirectory(null);
        }
        else
        {
            setCurrentDirectoryObject(fileSystemView.createFileObject(
                    currentDirectoryPath));
        }
    }

    public JPanel getNavigationButtonsPanel()
    {
        return defaultUI.getNavigationButtonsPanel();
    }

    public JButton getUpFolderButton()
    {
        return defaultUI.getUpFolderButton();
    }

    public JButton getHomeFolderButton()
    {
        return defaultUI.getHomeFolderButton();
    }

    public JButton getNewFolderButton()
    {
        return defaultUI.getNewFolderButton();
    }

    /**
     * Performs common constructor initialization and setup.
     * @param view
     */
    protected void setup(AbstractVFSFileSystemView view)
    {
        installShowFilesListener();

        if (view == null)
        {
            view = AbstractVFSFileSystemView.getFileSystemView();
        }

        setFileSystemView(view);
        updateUI();

        if (isAcceptAllFileFilterUsed())
        {
            setFileFilter(getAcceptAllFileFilter());
        }

        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    private void installShowFilesListener()
    {
        // Track native setting for showing hidden files
        Toolkit tk = Toolkit.getDefaultToolkit();
        Object showHiddenProperty = tk.getDesktopProperty(SHOW_HIDDEN_PROP);

        if (showHiddenProperty instanceof Boolean)
        {
            useFileHiding = !((Boolean) showHiddenProperty).booleanValue();
            showFilesListener = new WeakPCL(this);
            tk.addPropertyChangeListener(SHOW_HIDDEN_PROP, showFilesListener);
        }
    }

    /**
     * Sets the <code>dragEnabled</code> property,
     * which must be <code>true</code> to enable
     * automatic drag handling (the first part of drag and drop)
     * on this component.
     * The <code>transferHandler</code> property needs to be set
     * to a non-<code>null</code> value for the drag to do
     * anything.  The default value of the <code>dragEnabled</code>
     * property
     * is <code>false</code>.
     *
     * <p>
     *
     * When automatic drag handling is enabled,
     * most look and feels begin a drag-and-drop operation
     * whenever the user presses the mouse button over an item
     * and then moves the mouse a few pixels.
     * Setting this property to <code>true</code>
     * can therefore have a subtle effect on
     * how selections behave.
     *
     * <p>
     *
     * Some look and feels might not support automatic drag and drop;
     * they will ignore this property.  You can work around such
     * look and feels by modifying the component
     * to directly call the <code>exportAsDrag</code> method of a
     * <code>TransferHandler</code>.
     *
     * @param b the value to set the <code>dragEnabled</code> property to
     * @exception HeadlessException if
     *            <code>b</code> is <code>true</code> and
     *            <code>GraphicsEnvironment.isHeadless()</code>
     *            returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see #getDragEnabled
     * @see #setTransferHandler
     * @see TransferHandler
     * @since 1.4
     *
     * @beaninfo
     *  description: determines whether automatic drag handling is enabled
     *        bound: false
     */
    public void setDragEnabled(boolean b)
    {
        if (b && GraphicsEnvironment.isHeadless())
        {
            throw new HeadlessException();
        }

        dragEnabled = b;
    }

    /**
     * Gets the value of the <code>dragEnabled</code> property.
     *
     * @return  the value of the <code>dragEnabled</code> property
     * @see #setDragEnabled
     * @since 1.4
     */
    public boolean getDragEnabled()
    {
        return dragEnabled;
    }

    /**
     * Returns the selected file. This can be set either by the
     * programmer via <code>setSelectedFile</code> or by a user action, such as
     * either typing the filename into the UI or selecting the
     * file from a list in the UI.
     *
     * @see #setSelectedFile
     * @return the selected file
     */
    public File getSelectedFile()
    {
        return fileObjectConverter.convertFileObject(selectedFile);
    }

    /**
     * Returns the selected file. This can be set either by the
     * programmer via <code>setSelectedFile</code> or by a user action, such as
     * either typing the filename into the UI or selecting the
     * file from a list in the UI.
     *
     * @see #setSelectedFileObject
     * @return the selected file
     */
    public FileObject getSelectedFileObject()
    {
        return selectedFile;
    }

    /**
     * Sets the selected file. If the file's parent directory is
     * not the current directory, changes the current directory
     * to be the file's parent directory.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     *
     * @see #getSelectedFile
     *
     * @param file the selected file
     */
    public void setSelectedFile(File file)
    {
      setSelectedFileObject(VFSUtils.toFileObject(file));
    }

    /**
     * Sets the selected file. If the file's parent directory is
     * not the current directory, changes the current directory
     * to be the file's parent directory.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     *
     * @see #getSelectedFileObject
     *
     * @param file the selected file
     */
    public void setSelectedFileObject(FileObject file)
    {
        FileObject oldValue = selectedFile;
        selectedFile = file;

        if (selectedFile != null)
        {
            if (!getFileSystemView().isParent(getCurrentDirectoryObject(), selectedFile))
            {
                setCurrentDirectoryObject(VFSUtils.getParentDirectory(selectedFile));
            }

            if (!isMultiSelectionEnabled() || (selectedFiles == null) ||
                    (selectedFiles.length == 1))
            {
                ensureFileIsVisible(selectedFile);
            }
        }

        firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, oldValue,
            selectedFile);
    }

    /**
     * Returns a list of selected files if the file chooser is
     * set to allow multiple selection.
     */
    public File[] getSelectedFiles()
    {
      FileObject[] objects = getSelectedFileObjects();
      File[] files = new File[objects.length];
      for (int i = 0; i < objects.length; i++)
	files[i] = fileObjectConverter.convertFileObject(objects[i]);
      return files;
    }

    /**
     * Returns a list of selected files if the file chooser is
     * set to allow multiple selection.
     */
    public FileObject[] getSelectedFileObjects()
    {
        if (selectedFiles == null)
        {
            return new FileObject[0];
        }
        else
        {
            return (FileObject[]) selectedFiles.clone();
        }
    }

    /**
     * Sets the list of selected files if the file chooser is
     * set to allow multiple selection.
     *
     * @beaninfo
     *       bound: true
     * description: The list of selected files if the chooser is in multiple selection mode.
     */
    public void setSelectedFiles(File[] selectedFiles)
    {
      FileObject[] objects = new FileObject[selectedFiles.length];
      for (int i = 0; i < selectedFiles.length; i++)
	objects[i] = VFSUtils.toFileObject(selectedFiles[i]);
      setSelectedFileObjects(objects);
    }

    /**
     * Sets the list of selected files if the file chooser is
     * set to allow multiple selection.
     *
     * @beaninfo
     *       bound: true
     * description: The list of selected files if the chooser is in multiple selection mode.
     */
    public void setSelectedFileObjects(FileObject[] selectedFiles)
    {
        FileObject[] oldValue = this.selectedFiles;

        if ((selectedFiles == null) || (selectedFiles.length == 0))
        {
            selectedFiles = null;
            this.selectedFiles = null;
            setSelectedFileObject(null);
        }
        else
        {
            this.selectedFiles = selectedFiles.clone();
            setSelectedFileObject(this.selectedFiles[0]);
        }

        firePropertyChange(SELECTED_FILES_CHANGED_PROPERTY, oldValue,
            selectedFiles);
    }

    /**
     * Returns the current directory.
     *
     * @return the current directory
     * @see #setCurrentDirectory
     */
    public File getCurrentDirectory()
    {
      return fileObjectConverter.convertFileObject(currentDirectory);
    }

    /**
     * Returns the current directory.
     *
     * @return the current directory
     * @see #setCurrentDirectoryObject
     */
    public FileObject getCurrentDirectoryObject()
    {
        return currentDirectory;
    }

    /**
     * Sets the current directory. Passing in <code>null</code> sets the
     * file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * If the file passed in as <code>currentDirectory</code> is not a
     * directory, the parent of the file will be used as the currentDirectory.
     * If the parent is not traversable, then it will walk up the parent tree
     * until it finds a traversable directory, or hits the root of the
     * file system.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The directory that the VFSJFileChooser is showing files of.
     *
     * @param dir the current directory to point to
     * @see #getCurrentDirectory
     */
    public void setCurrentDirectory(File dir)
    {
      setCurrentDirectoryObject(VFSUtils.toFileObject(dir));
    }

    /**
     * Sets the current directory. Passing in <code>null</code> sets the
     * file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * If the file passed in as <code>currentDirectory</code> is not a
     * directory, the parent of the file will be used as the currentDirectory.
     * If the parent is not traversable, then it will walk up the parent tree
     * until it finds a traversable directory, or hits the root of the
     * file system.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The directory that the VFSJFileChooser is showing files of.
     *
     * @param dir the current directory to point to
     * @see #getCurrentDirectoryObject
     */
    public void setCurrentDirectoryObject(FileObject dir)
    {
        FileObject oldValue = currentDirectory;

        if ((dir != null) && !VFSUtils.exists(dir))
        {
            dir = currentDirectory;
        }

        if (dir == null)
        {
            dir = getFileSystemView().getDefaultDirectory();
        }

        if (currentDirectory != null)
        {
            /* Verify the toString of object */
            if (this.currentDirectory.equals(dir))
            {
                return;
            }
        }

        FileObject prev = null;

        while (!isTraversable(dir) && (prev != dir))
        {
            prev = dir;
            dir = getFileSystemView().getParentDirectory(dir);
        }

        currentDirectory = dir;

        firePropertyChange(DIRECTORY_CHANGED_PROPERTY, oldValue,
            currentDirectory);
    }

    /**
     * Changes the directory to be set to the parent of the
     * current directory.
     *
     * @see #getCurrentDirectory
     */
    public void changeToParentDirectory()
    {
        selectedFile = null;

        FileObject oldValue = getCurrentDirectoryObject();
        setCurrentDirectoryObject(getFileSystemView().getParentDirectory(oldValue));
    }

    /**
     * Tells the UI to rescan its files list from the current directory.
     */
    public void rescanCurrentDirectory()
    {
        getUI().rescanCurrentDirectory(this);
    }

    /**
     * Makes sure that the specified file is viewable, and
     * not hidden.
     *
     * @param fileObject  a File object
     */
    public void ensureFileIsVisible(FileObject f)
    {
        getUI().ensureFileIsVisible(this, f);
    }

    // **************************************
    // ***** VFSJFileChooser Dialog methods *****
    // **************************************

    /**
     * Pops up an "Open File" file chooser dialog. Note that the
     * text that appears in the approve button is determined by
     * the L&F.
     *
     * @param    parent  the parent component of the dialog,
     *                  can be <code>null</code>;
     *                  see <code>showDialog</code> for details
     * @return   the return state of the file chooser on popdown:
     * <ul>
     * <li>RETURN_TYPE.CANCEL_OPTION
     * <li>RETURN_TYPE.APPROVE_OPTION
     * <li>RETURN_TYPE.ERROR_OPTION if an error occurs or the
     *                  dialog is dismissed
     * </ul>
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see #showDialog
     */
    public RETURN_TYPE showOpenDialog(Component parent)
        throws HeadlessException
    {
        setDialogType(DIALOG_TYPE.OPEN);

        return showDialog(parent, null);
    }

    /**
     * Pops up a "Save File" file chooser dialog. Note that the
     * text that appears in the approve button is determined by
     * the L&F.
     *
     * @param    parent  the parent component of the dialog,
     *                  can be <code>null</code>;
     *                  see <code>showDialog</code> for details
     * @return   the return state of the file chooser on popdown:
     * <ul>
     * <li>RETURN_TYPE.CANCEL_OPTION
     * <li>RETURN_TYPE.APPROVE_OPTION
     * <li>RETURN_TYPE.ERROR_OPTION if an error occurs or the
     *                  dialog is dismissed
     * </ul>
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see #showDialog
     */
    public RETURN_TYPE showSaveDialog(Component parent)
        throws HeadlessException
    {
        setDialogType(DIALOG_TYPE.SAVE);

        return showDialog(parent, null);
    }

    /**
     * Pops a custom file chooser dialog with a custom approve button.
     * For example, the following code
     * pops up a file chooser with a "Run Application" button
     * (instead of the normal "Save" or "Open" button):
     * <pre>
     * filechooser.showDialog(parentFrame, "Run Application");
     * </pre>
     *
     * Alternatively, the following code does the same thing:
     * <pre>
     *    VFSJFileChooser chooser = new VFSJFileChooser(null);
     *    chooser.setApproveButtonText("Run Application");
     *    chooser.showDialog(parentFrame, null);
     * </pre>
     *
     * <p>
     *
     * The <code>parent</code> argument determines two things:
     * the frame on which the open dialog depends and
     * the component whose position the look and feel
     * should consider when placing the dialog.  If the parent
     * is a <code>Frame</code> object (such as a <code>JFrame</code>)
     * then the dialog depends on the frame and
     * the look and feel positions the dialog
     * relative to the frame (for example, centered over the frame).
     * If the parent is a component, then the dialog
     * depends on the frame containing the component,
     * and is positioned relative to the component
     * (for example, centered over the component).
     * If the parent is <code>null</code>, then the dialog depends on
     * no visible window, and it's placed in a
     * look-and-feel-dependent position
     * such as the center of the screen.
     *
     * @param   parent  the parent component of the dialog;
     *                  can be <code>null</code>
     * @param   approveButtonText the text of the <code>ApproveButton</code>
     * @return  the return state of the file chooser on popdown:
     * <ul>
     * <li>VFSJFileChooser.CANCEL_OPTION
     * <li>VFSJFileChooser.APPROVE_OPTION
     * <li>JFileCHooser.ERROR_OPTION if an error occurs or the
     *                  dialog is dismissed
     * </ul>
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public RETURN_TYPE showDialog(Component parent, String approveButtonText)
        throws HeadlessException
    {
        if (approveButtonText != null)
        {
            setApproveButtonText(approveButtonText);
            setDialogType(DIALOG_TYPE.CUSTOM);
        }

        dialog = createDialog(parent);
        dialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    returnValue = RETURN_TYPE.CANCEL;
                }
            });
        returnValue = RETURN_TYPE.ERROR;
        rescanCurrentDirectory();

        dialog.setVisible(true);
        firePropertyChange("JFileChooserDialogIsClosingProperty", dialog, null);
        dialog.dispose();
        dialog = null;

        return returnValue;
    }

    /**
     * Creates and returns a new <code>JDialog</code> wrapping
     * <code>this</code> centered on the <code>parent</code>
     * in the <code>parent</code>'s frame.
     * This method can be overriden to further manipulate the dialog,
     * to disable resizing, set the location, etc. Example:
     * <pre>
     *     class MyFileChooser extends VFSJFileChooser {
     *         protected JDialog createDialog(Component parent) throws HeadlessException {
     *             JDialog dialog = super.createDialog(parent);
     *             dialog.setLocation(300, 200);
     *             dialog.setResizable(false);
     *             return dialog;
     *         }
     *     }
     * </pre>
     *
     * @param   parent  the parent component of the dialog;
     *                  can be <code>null</code>
     * @return a new <code>JDialog</code> containing this instance
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @since 1.4
     */
    protected JDialog createDialog(Component parent) throws HeadlessException
    {
        String title = getUI().getDialogTitle(this);
        putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY,
            title);

        Window window = null;

        try
        {
            window = SwingUtilities.getWindowAncestor(parent);
        }
        catch (Exception ex)
        {
        }

        if (window == null)
        {
            if (parent instanceof Window)
            {
                window = (Window) parent;
            }
            else
            {
                window = SHARED_FRAME;
            }

            dialog = new JDialog((Frame) window, title, true);
        }

        else if (window instanceof Frame)
        {
            dialog = new JDialog((Frame) window, title, true);
        }
        else
        {
            dialog = new JDialog((Dialog) window, title, true);
        }

        dialog.setComponentOrientation(this.getComponentOrientation());

        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);

        if (JDialog.isDefaultLookAndFeelDecorated())
        {
            boolean supportsWindowDecorations = UIManager.getLookAndFeel()
                                                         .getSupportsWindowDecorations();

            if (supportsWindowDecorations)
            {
                dialog.getRootPane()
                      .setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
            }
        }

        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        return dialog;
    }

    /**
     * Returns the value of the <code>controlButtonsAreShown</code>
     * property.
     *
     * @return   the value of the <code>controlButtonsAreShown</code>
     *     property
     *
     * @see #setControlButtonsAreShown
     * @since 1.3
     */
    public boolean getControlButtonsAreShown()
    {
        return controlsShown;
    }

    /**
     * Sets the property
     * that indicates whether the <i>approve</i> and <i>cancel</i>
     * buttons are shown in the file chooser.  This property
     * is <code>true</code> by default.  Look and feels
     * that always show these buttons will ignore the value
     * of this property.
     * This method fires a property-changed event,
     * using the string value of
     * <code>CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY</code>
     * as the name of the property.
     *
     * @param b <code>false</code> if control buttons should not be
     *    shown; otherwise, <code>true</code>
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets whether the approve & cancel buttons are shown.
     *
     * @see #getControlButtonsAreShown
     * @see #CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY
     * @since 1.3
     */
    public void setControlButtonsAreShown(boolean b)
    {
        if (controlsShown == b)
        {
            return;
        }

        boolean oldValue = controlsShown;
        controlsShown = b;
        firePropertyChange(CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY,
            oldValue, controlsShown);
    }

    /**
     * Returns the type of this dialog.  The default is
     * <code>VFSJFileChooser.OPEN_DIALOG</code>.
     *
     * @return   the type of dialog to be displayed:
     * <ul>
     * <li>VFSJFileChooser.OPEN_DIALOG
     * <li>VFSJFileChooser.SAVE_DIALOG
     * <li>VFSJFileChooser.CUSTOM_DIALOG
     * </ul>
     *
     * @see #setDialogType
     */
    public DIALOG_TYPE getDialogType()
    {
        return dialogType;
    }

    /**
     * Sets the type of this dialog. Use <code>OPEN_DIALOG</code> when you
     * want to bring up a file chooser that the user can use to open a file.
     * Likewise, use <code>SAVE_DIALOG</code> for letting the user choose
     * a file for saving.
     * Use <code>CUSTOM_DIALOG</code> when you want to use the file
     * chooser in a context other than "Open" or "Save".
     * For instance, you might want to bring up a file chooser that allows
     * the user to choose a file to execute. Note that you normally would not
     * need to set the <code>VFSJFileChooser</code> to use
     * <code>CUSTOM_DIALOG</code>
     * since a call to <code>setApproveButtonText</code> does this for you.
     * The default dialog type is <code>VFSJFileChooser.OPEN_DIALOG</code>.
     *
     * @param dialogType the type of dialog to be displayed:
     * <ul>
     * <li>VFSJFileChooser.OPEN_DIALOG
     * <li>VFSJFileChooser.SAVE_DIALOG
     * <li>VFSJFileChooser.CUSTOM_DIALOG
     * </ul>
     *
     * @exception IllegalArgumentException if <code>dialogType</code> is
     *                          not legal
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The type (open, save, custom) of the VFSJFileChooser.
     *        enum:
     *              OPEN_DIALOG VFSJFileChooser.OPEN_DIALOG
     *              SAVE_DIALOG VFSJFileChooser.SAVE_DIALOG
     *              CUSTOM_DIALOG VFSJFileChooser.CUSTOM_DIALOG
     *
     * @see #getDialogType
     * @see #setApproveButtonText
     */
    public void setDialogType(DIALOG_TYPE dialogType)
    {
        if (this.dialogType == dialogType)
        {
            return;
        }

        if (!((dialogType == DIALOG_TYPE.OPEN) ||
                (dialogType == DIALOG_TYPE.SAVE) ||
                (dialogType == DIALOG_TYPE.CUSTOM)))
        {
            throw new IllegalArgumentException("Incorrect Dialog Type: " +
                dialogType);
        }

        DIALOG_TYPE oldValue = this.dialogType;
        this.dialogType = dialogType;

        if ((dialogType == DIALOG_TYPE.OPEN) ||
                (dialogType == DIALOG_TYPE.SAVE))
        {
            setApproveButtonText(null);
        }

        firePropertyChange(DIALOG_TYPE_CHANGED_PROPERTY, oldValue, dialogType);
    }

    /**
     * Sets the string that goes in the <code>VFSJFileChooser</code> window's
     * title bar.
     *
     * @param dialogTitle the new <code>String</code> for the title bar
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The title of the VFSJFileChooser dialog window.
     *
     * @see #getDialogTitle
     *
     */
    public void setDialogTitle(String dialogTitle)
    {
        String oldValue = this.dialogTitle;
        this.dialogTitle = dialogTitle;

        if (dialog != null)
        {
            dialog.setTitle(dialogTitle);
        }

        firePropertyChange(DIALOG_TITLE_CHANGED_PROPERTY, oldValue, dialogTitle);
    }

    /**
     * Gets the string that goes in the <code>VFSJFileChooser</code>'s titlebar.
     *
     * @see #setDialogTitle
     */
    public String getDialogTitle()
    {
        return dialogTitle;
    }

    /**
     * Sets the tooltip text used in the <code>ApproveButton</code>.
     * If <code>null</code>, the UI object will determine the button's text.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The tooltip text for the ApproveButton.
     *
     * @param toolTipText the tooltip text for the approve button
     * @see #setApproveButtonText
     * @see #setDialogType
     * @see #showDialog
     */
    public void setApproveButtonToolTipText(String toolTipText)
    {
        if ((this.approveButtonToolTipText == toolTipText) ||
                ((this.approveButtonToolTipText != null) &&
                this.approveButtonToolTipText.equals(toolTipText)))
        {
            return;
        }

        String oldValue = approveButtonToolTipText;
        approveButtonToolTipText = toolTipText;
        firePropertyChange(APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY,
            oldValue, approveButtonToolTipText);
    }

    /**
     * Returns the tooltip text used in the <code>ApproveButton</code>.
     * If <code>null</code>, the UI object will determine the button's text.
     *
     * @return the tooltip text used for the approve button
     *
     * @see #setApproveButtonText
     * @see #setDialogType
     * @see #showDialog
     */
    public String getApproveButtonToolTipText()
    {
        return approveButtonToolTipText;
    }

    /**
     * Returns the approve button's mnemonic.
     * @return an integer value for the mnemonic key
     *
     * @see #setApproveButtonMnemonic
     */
    public int getApproveButtonMnemonic()
    {
        return approveButtonMnemonic;
    }

    /**
     * Sets the approve button's mnemonic using a numeric keycode.
     *
     * @param mnemonic  an integer value for the mnemonic key
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The mnemonic key accelerator for the ApproveButton.
     *
     * @see #getApproveButtonMnemonic
     */
    public void setApproveButtonMnemonic(int mnemonic)
    {
        if (approveButtonMnemonic == mnemonic)
        {
            return;
        }

        int oldValue = approveButtonMnemonic;
        approveButtonMnemonic = mnemonic;
        firePropertyChange(APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY, oldValue,
            approveButtonMnemonic);
    }

    /**
     * Sets the approve button's mnemonic using a character.
     * @param mnemonic  a character value for the mnemonic key
     *
     * @see #getApproveButtonMnemonic
     */
    public void setApproveButtonMnemonic(char mnemonic)
    {
        int vk = (int) mnemonic;

        if ((vk >= 'a') && (vk <= 'z'))
        {
            vk -= ('a' - 'A');
        }

        setApproveButtonMnemonic(vk);
    }

    /**
     * Sets the text used in the <code>ApproveButton</code> in the
     * <code>FileChooserUI</code>.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The text that goes in the ApproveButton.
     *
     * @param approveButtonText the text used in the <code>ApproveButton</code>
     *
     * @see #getApproveButtonText
     * @see #setDialogType
     * @see #showDialog
     */

    // PENDING(jeff) - have ui set this on dialog type change
    public void setApproveButtonText(String approveButtonText)
    {
        if ((this.approveButtonText == approveButtonText) ||
                ((this.approveButtonText != null) &&
                this.approveButtonText.equals(approveButtonText)))
        {
            return;
        }

        String oldValue = this.approveButtonText;
        this.approveButtonText = approveButtonText;
        firePropertyChange(APPROVE_BUTTON_TEXT_CHANGED_PROPERTY, oldValue,
            approveButtonText);
    }

    /**
     * Returns the text used in the <code>ApproveButton</code> in the
     * <code>FileChooserUI</code>.
     * If <code>null</code>, the UI object will determine the button's text.
     *
     * Typically, this would be "Open" or "Save".
     *
     * @return the text used in the <code>ApproveButton</code>
     *
     * @see #setApproveButtonText
     * @see #setDialogType
     * @see #showDialog
     */
    public String getApproveButtonText()
    {
        return approveButtonText;
    }

    /**
     * Gets the list of user choosable file filters.
     *
     * @return a <code>FileFilter</code> array containing all the choosable
     *         file filters
     *
     * @see #addChoosableFileFilter
     * @see #removeChoosableFileFilter
     * @see #resetChoosableFileFilters
     */
    public AbstractVFSFileFilter[] getChoosableFileFilters()
    {
        return filters.toArray(new AbstractVFSFileFilter[filters.size()]);
    }

    /**
     * Adds a filter to the list of user choosable file filters.
     * For information on setting the file selection mode, see
     * {@link #setFileSelectionMode setFileSelectionMode}.
     *
     * @param filter the <code>FileFilter</code> to add to the choosable file
     *               filter list
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Adds a filter to the list of user choosable file filters.
     *
     * @see #getChoosableFileFilters
     * @see #removeChoosableFileFilter
     * @see #resetChoosableFileFilters
     * @see #setFileSelectionMode
     */
    public void addChoosableFileFilter(AbstractVFSFileFilter filter)
    {
        if ((filter != null) && !filters.contains(filter))
        {
            AbstractVFSFileFilter[] oldValue = getChoosableFileFilters();
            filters.add(filter);
            firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY,
                oldValue, getChoosableFileFilters());

            if ((fileFilter == null) && (filters.size() == 1))
            {
                setFileFilter(filter);
            }
        }
    }

    /**
     * Removes a filter from the list of user choosable file filters. Returns
     * true if the file filter was removed.
     *
     * @see #addChoosableFileFilter
     * @see #getChoosableFileFilters
     * @see #resetChoosableFileFilters
     */
    public boolean removeChoosableFileFilter(AbstractVFSFileFilter f)
    {
        if (filters.contains(f))
        {
            if (getFileFilter() == f)
            {
                setFileFilter(null);
            }

            AbstractVFSFileFilter[] oldValue = getChoosableFileFilters();
            filters.remove(f);
            firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY,
                oldValue, getChoosableFileFilters());

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Resets the choosable file filter list to its starting state. Normally,
     * this removes all added file filters while leaving the
     * <code>AcceptAll</code> file filter.
     *
     * @see #addChoosableFileFilter
     * @see #getChoosableFileFilters
     * @see #removeChoosableFileFilter
     */
    public void resetChoosableFileFilters()
    {
        AbstractVFSFileFilter[] oldValue = getChoosableFileFilters();
        setFileFilter(null);
        filters.clear();

        if (isAcceptAllFileFilterUsed())
        {
            addChoosableFileFilter(getAcceptAllFileFilter());
        }

        firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY, oldValue,
            getChoosableFileFilters());
    }

    /**
     * Returns the <code>AcceptAll</code> file filter.
     * For example, on Microsoft Windows this would be All Files (*.*).
     */
    public AbstractVFSFileFilter getAcceptAllFileFilter()
    {
        AbstractVFSFileFilter filter = null;

        if (getUI() != null)
        {
            filter = getUI().getAcceptAllFileFilter(this);
        }

        return filter;
    }

    /**
     * Returns whether the <code>AcceptAll FileFilter</code> is used.
     * @return true if the <code>AcceptAll FileFilter</code> is used
     * @see #setAcceptAllFileFilterUsed
     * @since 1.3
     */
    public boolean isAcceptAllFileFilterUsed()
    {
        return useAcceptAllFileFilter;
    }

    /**
     * Determines whether the <code>AcceptAll FileFilter</code> is used
     * as an available choice in the choosable filter list.
     * If false, the <code>AcceptAll</code> file filter is removed from
     * the list of available file filters.
     * If true, the <code>AcceptAll</code> file filter will become the
     * the actively used file filter.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets whether the AcceptAll FileFilter is used as an available choice in the choosable filter list.
     *
     * @see #isAcceptAllFileFilterUsed
     * @see #getAcceptAllFileFilter
     * @see #setFileFilter
     * @since 1.3
     */
    public void setAcceptAllFileFilterUsed(boolean b)
    {
        boolean oldValue = useAcceptAllFileFilter;
        useAcceptAllFileFilter = b;

        if (!b)
        {
            removeChoosableFileFilter(getAcceptAllFileFilter());
        }
        else
        {
            removeChoosableFileFilter(getAcceptAllFileFilter());
            addChoosableFileFilter(getAcceptAllFileFilter());
        }

        firePropertyChange(ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY,
            oldValue, useAcceptAllFileFilter);
    }

    /**
     * Returns the accessory component.
     *
     * @return this VFSJFileChooser's accessory component, or null
     * @see #setAccessory
     */
    public JComponent getAccessory()
    {
        return accessory;
    }

    /**
     * Sets the accessory component. An accessory is often used to show a
     * preview image of the selected file; however, it can be used for anything
     * that the programmer wishes, such as extra custom file chooser controls.
     *
     * <p>
     * Note: if there was a previous accessory, you should unregister
     * any listeners that the accessory might have registered with the
     * file chooser.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets the accessory component on the VFSJFileChooser.
     */
    public void setAccessory(JComponent newAccessory)
    {
        JComponent oldValue = accessory;
        accessory = newAccessory;
        firePropertyChange(ACCESSORY_CHANGED_PROPERTY, oldValue, accessory);
    }

    /**
     * Sets the <code>VFSJFileChooser</code> to allow the user to just
     * select files, just select
     * directories, or select both files and directories.  The default is
     * <code>JFilesChooser.FILES_ONLY</code>.
     *
     * @param mode the type of files to be displayed
     *
     * @exception IllegalArgumentException  if <code>mode</code> is an
     *                          illegal file selection mode
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets the types of files that the VFSJFileChooser can choose.
     *        enum: FILES_ONLY VFSJFileChooser.FILES_ONLY
     *              DIRECTORIES_ONLY VFSJFileChooser.DIRECTORIES_ONLY
     *              FILES_AND_DIRECTORIES VFSJFileChooser.FILES_AND_DIRECTORIES
     *
     *
     * @see #getFileSelectionMode
     */
    public void setFileSelectionMode(SELECTION_MODE mode)
    {
        if (fileSelectionMode == mode)
        {
            return;
        }

        if ((mode == SELECTION_MODE.FILES_ONLY) ||
                (mode == SELECTION_MODE.DIRECTORIES_ONLY) ||
                (mode == SELECTION_MODE.FILES_AND_DIRECTORIES))
        {
            SELECTION_MODE oldValue = fileSelectionMode;
            fileSelectionMode = mode;
            firePropertyChange(FILE_SELECTION_MODE_CHANGED_PROPERTY, oldValue,
                fileSelectionMode);
        }
        else
        {
            throw new IllegalArgumentException(
                "Incorrect Mode for file selection: " + mode);
        }
    }

    /**
     * Returns the current file-selection mode.  The default is
     * <code>SELECTION_MODE.FILES_ONLY</code>.
     *
     * @return the type of files to be displayed, one of the following:
     * <ul>
     * <li>SELECTION_MODE.FILES_ONLY
     * <li>SELECTION_MODE.DIRECTORIES_ONLY
     * <li>SELECTION_MODE.FILES_AND_DIRECTORIES
     * </ul>
     * @see #setFileSelectionMode
     */
    public SELECTION_MODE getFileSelectionMode()
    {
        return fileSelectionMode;
    }

    /**
     * Convenience call that determines if files are selectable based on the
     * current file selection mode.
     *
     * @see #setFileSelectionMode
     * @see #getFileSelectionMode
     */
    public boolean isFileSelectionEnabled()
    {
        return ((fileSelectionMode == SELECTION_MODE.FILES_ONLY) ||
        (fileSelectionMode == SELECTION_MODE.FILES_AND_DIRECTORIES));
    }

    /**
     * Convenience call that determines if directories are selectable based
     * on the current file selection mode.
     *
     * @see #setFileSelectionMode
     * @see #getFileSelectionMode
     */
    public boolean isDirectorySelectionEnabled()
    {
        return ((fileSelectionMode == SELECTION_MODE.DIRECTORIES_ONLY) ||
        (fileSelectionMode == SELECTION_MODE.FILES_AND_DIRECTORIES));
    }

    /**
     * Sets the file chooser to allow multiple file selections.
     *
     * @param b true if multiple files may be selected
     * @beaninfo
     *       bound: true
     * description: Sets multiple file selection mode.
     *
     * @see #isMultiSelectionEnabled
     */
    public void setMultiSelectionEnabled(boolean b)
    {
        if (multiSelectionEnabled == b)
        {
            return;
        }

        boolean oldValue = multiSelectionEnabled;
        multiSelectionEnabled = b;
        firePropertyChange(MULTI_SELECTION_ENABLED_CHANGED_PROPERTY, oldValue,
            multiSelectionEnabled);
    }

    /**
     * Returns true if multiple files can be selected.
     * @return true if multiple files can be selected
     * @see #setMultiSelectionEnabled
     */
    public boolean isMultiSelectionEnabled()
    {
        return multiSelectionEnabled;
    }

    /**
     * Returns true if hidden files are not shown in the file chooser;
     * otherwise, returns false.
     *
     * @return the status of the file hiding property
     * @see #setFileHidingEnabled
     */
    public boolean isFileHidingEnabled()
    {
        return useFileHiding;
    }

    /**
     * Sets file hiding on or off. If true, hidden files are not shown
     * in the file chooser. The job of determining which files are
     * shown is done by the <code>FileView</code>.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets file hiding on or off.
     *
     * @param b the boolean value that determines whether file hiding is
     *          turned on
     * @see #isFileHidingEnabled
     */
    public void setFileHidingEnabled(boolean b)
    {
        // Dump showFilesListener since we'll ignore it from now on
        if (showFilesListener != null)
        {
            Toolkit.getDefaultToolkit()
                   .removePropertyChangeListener(SHOW_HIDDEN_PROP,
                showFilesListener);
            showFilesListener = null;
        }

        boolean oldValue = useFileHiding;
        useFileHiding = b;
        firePropertyChange(FILE_HIDING_CHANGED_PROPERTY, oldValue, useFileHiding);
    }

    /**
     * Sets the current file filter. The file filter is used by the
     * file chooser to filter out files from the user's view.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets the File Filter used to filter out files of type.
     *
     * @param filter the new current file filter to use
     * @see #getFileFilter
     */
    public void setFileFilter(AbstractVFSFileFilter filter)
    {
        AbstractVFSFileFilter oldValue = fileFilter;
        fileFilter = filter;

        if (filter != null)
        {
            if (isMultiSelectionEnabled() && (selectedFiles != null) &&
                    (selectedFiles.length > 0))
            {
                List<FileObject> fList = new ArrayList<FileObject>(selectedFiles.length);
                boolean failed = false;

                for (FileObject aSelectedFile : selectedFiles)
                {
                    if (filter.accept(aSelectedFile))
                    {
                        fList.add(aSelectedFile);
                    }
                    else
                    {
                        failed = true;
                    }
                }

                if (failed)
                {
                    setSelectedFileObjects(EMPTY_FILEOBJECT_ARRAY);
                }
            }
            else if ((selectedFile != null) && !filter.accept(selectedFile))
            {
                setSelectedFileObject(null);
            }
        }

        firePropertyChange(FILE_FILTER_CHANGED_PROPERTY, oldValue, fileFilter);
    }

    /**
     * Returns the currently selected file filter.
     *
     * @return the current file filter
     * @see #setFileFilter
     * @see #addChoosableFileFilter
     */
    public AbstractVFSFileFilter getFileFilter()
    {
        return fileFilter;
    }

    /**
     * Sets the file view to used to retrieve UI information, such as
     * the icon that represents a file or the type description of a file.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets the File View used to get file type information.
     *
     * @see #getFileView
     */
    public void setFileView(AbstractVFSFileView fileView)
    {
        AbstractVFSFileView oldValue = this.fileView;
        this.fileView = fileView;
        firePropertyChange(FILE_VIEW_CHANGED_PROPERTY, oldValue, fileView);
    }

    /**
     * Returns the current file view.
     *
     * @see #setFileView
     */
    public AbstractVFSFileView getFileView()
    {
        return fileView;
    }

    /**
     * Sets the converter for {@link FileObject} objects.
     * 
     * @param value the converter
     */
    public void setFileObjectConverter(FileObjectConverter value) 
    {
      	fileObjectConverter = value;
    }
    
    /**
     * Returns the current converter for {@link FileObject} objects.
     * 
     * @return the converter
     */
    public FileObjectConverter getFileObjectConverter()
    {
      	return fileObjectConverter;
    }
    
    /**
      * Returns the filename.
      * @param fileObject the <code>File</code>
      * @return the <code>String</code> containing the filename for
      *          <code>fileObject</code>
      * @see FileView#getName
      */
    public String getName(FileObject fileObject)
    {
        String filename = null;

        if (fileObject != null)
        {
            if (getFileView() != null)
            {
                filename = getFileView().getName(fileObject);
            }

            if ((filename == null) && (uiFileView != null))
            {
                filename = uiFileView.getName(fileObject);
            }
        }

        return filename;
    }

    /**
     * Returns the file description.
     * @param fileObject the <code>File</code>
     * @return the <code>String</code> containing the file description for
     *          <code>fileObject</code>
     * @see FileView#getDescription
     */
    public String getDescription(FileObject fileObject)
    {
        String description = null;

        if (fileObject != null)
        {
            if (getFileView() != null)
            {
                description = getFileView().getDescription(fileObject);
            }

            if ((description == null) && (uiFileView != null))
            {
                description = uiFileView.getDescription(fileObject);
            }
        }

        return description;
    }

    /**
     * Returns the file type.
     * @param fileObject the <code>File</code>
     * @return the <code>String</code> containing the file type description for
     *          <code>fileObject</code>
     * @see FileView#getTypeDescription
     */
    public String getTypeDescription(FileObject fileObject)
    {
        String typeDescription = null;

        if (fileObject != null)
        {
            if (getFileView() != null)
            {
                typeDescription = getFileView().getTypeDescription(fileObject);
            }

            if ((typeDescription == null) && (uiFileView != null))
            {
                typeDescription = uiFileView.getTypeDescription(fileObject);
            }
        }

        return typeDescription;
    }

    /**
     * Returns the icon for this file or type of file, depending
     * on the system.
     * @param fileObject the <code>File</code>
     * @return the <code>Icon</code> for this file, or type of file
     * @see FileView#getIcon
     */
    public Icon getIcon(FileObject fileObject)
    {
        Icon icon = null;

        if (fileObject != null)
        {
            if (getFileView() != null)
            {
                icon = getFileView().getIcon(fileObject);
            }

            if ((icon == null) && (uiFileView != null))
            {
                icon = uiFileView.getIcon(fileObject);
            }
        }

        return icon;
    }

    /**
     * Returns true if the file (directory) can be visited.
     * Returns false if the directory cannot be traversed.
     * @param fileObject the <code>File</code>
     * @return true if the file/directory can be traversed, otherwise false
     * @see FileView#isTraversable
     */
    public boolean isTraversable(FileObject fileObject)
    {
        Boolean traversable = null;

        if (fileObject != null)
        {
            if (getFileView() != null)
            {
                traversable = getFileView().isTraversable(fileObject);
            }

            if ((traversable == null) && (uiFileView != null))
            {
                traversable = uiFileView.isTraversable(fileObject);
            }

            if (traversable == null)
            {
                traversable = getFileSystemView().isTraversable(fileObject);
            }
        }

        return ((traversable != null) && traversable.booleanValue());
    }

    /**
     * Returns true if the file should be displayed.
     * @param fileObject the <code>File</code>
     * @return true if the file should be displayed, otherwise false
     * @see FileFilter#accept
     */
    public boolean accept(FileObject fileObject)
    {
        boolean shown = true;

        if ((fileObject != null) && (fileFilter != null))
        {
            shown = fileFilter.accept(fileObject);
        }

        return shown;
    }

    /**
     * Sets the file system view that the <code>VFSJFileChooser</code> uses for
     * accessing and creating file system resources, such as finding
     * the floppy drive and getting a list of root drives.
     * @param fsv  the new <code>AbstractVFSFileSystemView</code>
     *
     * @beaninfo
     *      expert: true
     *       bound: true
     * description: Sets the FileSytemView used to get filesystem information.
     *
     * @see FileSystemView
     */
    public void setFileSystemView(AbstractVFSFileSystemView fsv)
    {
        AbstractVFSFileSystemView oldValue = fileSystemView;
        fileSystemView = fsv;
        firePropertyChange(FILE_SYSTEM_VIEW_CHANGED_PROPERTY, oldValue,
            fileSystemView);
    }

    /**
     * Returns the file system view.
     * @return the <code>AbstractVFSFileSystemView</code> object
     * @see #setFileSystemView
     */
    public AbstractVFSFileSystemView getFileSystemView()
    {
        return fileSystemView;
    }

    /**
     * Called by the UI when the user hits the Approve button
     * (labeled "Open" or "Save", by default). This can also be
     * called by the programmer.
     * This method causes an action event to fire
     * with the command string equal to
     * <code>APPROVE_SELECTION</code>.
     *
     * @see #APPROVE_SELECTION
     */
    public void approveSelection()
    {
        returnValue = RETURN_TYPE.APPROVE;

        if (dialog != null)
        {
            dialog.setVisible(false);
        }

        fireActionPerformed(APPROVE_SELECTION);
    }

    /**
     * Called by the UI when the user chooses the Cancel button.
     * This can also be called by the programmer.
     * This method causes an action event to fire
     * with the command string equal to
     * <code>CANCEL_SELECTION</code>.
     *
     * @see #CANCEL_SELECTION
     */
    public void cancelSelection()
    {
        returnValue = RETURN_TYPE.CANCEL;

        if (dialog != null)
        {
            dialog.setVisible(false);
        }

        fireActionPerformed(CANCEL_SELECTION);
    }

    /**
     * Adds an <code>ActionListener</code> to the file chooser.
     * @param l  the listener to be added
     * @see #approveSelection
     * @see #cancelSelection
     */
    public void addActionListener(ActionListener l)
    {
        listenerList.add(ActionListener.class, l);
    }

    /**
     * Removes an <code>ActionListener</code> from the file chooser.
     * @param l  the listener to be removed
     * @see #addActionListener
     */
    public void removeActionListener(ActionListener l)
    {
        listenerList.remove(ActionListener.class, l);
    }

    /**
     * Returns an array of all the action listeners
     * registered on this file chooser.
     *
     * @return all of this file chooser's <code>ActionListener</code>s
     *         or an empty
     *         array if no action listeners are currently registered
     *
     * @see #addActionListener
     * @see #removeActionListener
     *
     * @since 1.4
     */
    public ActionListener[] getActionListeners()
    {
        return (ActionListener[]) listenerList.getListeners(ActionListener.class);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type. The event instance
     * is lazily created using the <code>command</code> parameter.
     *
     * @see EventListenerList
     */
    protected void fireActionPerformed(String command)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        long mostRecentEventTime = EventQueue.getMostRecentEventTime();
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();

        if (currentEvent instanceof InputEvent)
        {
            modifiers = ((InputEvent) currentEvent).getModifiers();
        }
        else if (currentEvent instanceof ActionEvent)
        {
            modifiers = ((ActionEvent) currentEvent).getModifiers();
        }

        ActionEvent e = null;

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ActionListener.class)
            {
                // Lazily create the event:
                if (e == null)
                {
                    e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                            command, mostRecentEventTime, modifiers);
                }

                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI()
    {
        if (isAcceptAllFileFilterUsed())
        {
            removeChoosableFileFilter(getAcceptAllFileFilter());
        }

        if (defaultUI == null)
        {
            defaultUI = createDefaultUI();
            setUI(defaultUI);
        }

        if (fileSystemView == null)
        {
            // We were probably deserialized
            setFileSystemView(AbstractVFSFileSystemView.getFileSystemView());
        }

        uiFileView = getUI().getFileView(this);

        if (isAcceptAllFileFilterUsed())
        {
            addChoosableFileFilter(getAcceptAllFileFilter());
        }
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return the string "FileChooserUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&F class.
     */
    @Override
    public String getUIClassID()
    {
        return uiClassID;
    }

    /**
     * Gets the UI object which implements the L&F for this component.
     *
     * @return the FileChooserUI object that implements the FileChooserUI L&F
     */
    public AbstractVFSFileChooserUI getUI()
    {
        return (AbstractVFSFileChooserUI) defaultUI;
    }
    
    /**
     * Returns the default UI to use.
     * 
     * @return the default UI
     */
    protected MetalVFSFileChooserUI createDefaultUI() 
    {
        return new MetalVFSFileChooserUI(this);
    }

    /**
     * See <code>readObject</code> and <code>writeObject</code> in
     * <code>JComponent</code> for more
     * information about serialization in Swing.
     */
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
    }

    /**
     * See <code>readObject</code> and <code>writeObject</code> in
     * <code>JComponent</code> for more
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException
    {
    }

    /**
     * Returns a string representation of this <code>VFSJFileChooser</code>.
     * This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  a string representation of this <code>VFSJFileChooser</code>
     */
    @Override
    protected String paramString()
    {
        String approveButtonTextString = ((approveButtonText != null)
            ? approveButtonText : "");
        String dialogTitleString = ((dialogTitle != null) ? dialogTitle : "");
        String dialogTypeString;

        switch (dialogType)
        {
        case OPEN:
            dialogTypeString = "OPEN_DIALOG";

            break;

        case SAVE:
            dialogTypeString = "SAVE_DIALOG";

            break;

        case CUSTOM:
            dialogTypeString = "CUSTOM_DIALOG";

            break;

        default:
            dialogTypeString = "";

            break;
        }

        String returnValueString;

        switch (returnValue)
        {
        case CANCEL:
            returnValueString = "CANCEL_OPTION";

            break;

        case APPROVE:
            returnValueString = "APPROVE_OPTION";

            break;

        case ERROR:
            returnValueString = "ERROR_OPTION";

            break;

        default:
            returnValueString = "";

            break;
        }

        String useFileHidingString = (useFileHiding ? "true" : "false");
        String fileSelectionModeString;

        switch (fileSelectionMode)
        {
        case FILES_ONLY:
            fileSelectionModeString = "FILES_ONLY";

            break;

        case DIRECTORIES_ONLY:
            fileSelectionModeString = "DIRECTORIES_ONLY";

            break;

        case FILES_AND_DIRECTORIES:
            fileSelectionModeString = "FILES_AND_DIRECTORIES";

            break;

        default:
            fileSelectionModeString = "";

            break;
        }

        String currentDirectoryString = ((currentDirectory != null)
            ? currentDirectory.toString() : "");
        String selectedFileString = ((selectedFile != null)
            ? selectedFile.toString() : "");

        return new StringBuilder(super.paramString()).append(
            ",approveButtonText=").append(approveButtonTextString)
                                                     .append(",currentDirectory=")
                                                     .append(currentDirectoryString)
                                                     .append(",dialogTitle=")
                                                     .append(dialogTitleString)
                                                     .append(",dialogType=")
                                                     .append(dialogTypeString)
                                                     .append(",fileSelectionMode=")
                                                     .append(fileSelectionModeString)
                                                     .append(",returnValue=")
                                                     .append(returnValueString)
                                                     .append(",selectedFile=")
                                                     .append(selectedFileString)
                                                     .append(",useFileHiding=")
                                                     .append(useFileHidingString)
                                                     .toString();
    }

    /**
     * Gets the AccessibleContext associated with this VFSJFileChooser.
     * For file choosers, the AccessibleContext takes the form of an
     * AccessibleJFileChooser.
     * A new AccessibleJFileChooser instance is created if necessary.
     *
     * @return an AccessibleJFileChooser that serves as the
     *         AccessibleContext of this VFSJFileChooser
     */
    @Override
    public AccessibleContext getAccessibleContext()
    {
        if (m_accessibleContext == null)
        {
            m_accessibleContext = new AccessibleJFileChooser();
        }

        return m_accessibleContext;
    }

    private static class WeakPCL implements PropertyChangeListener
    {
        WeakReference<VFSJFileChooser> jfcRef;

        public WeakPCL(VFSJFileChooser jfc)
        {
            jfcRef = new WeakReference<VFSJFileChooser>(jfc);
        }

        public void propertyChange(PropertyChangeEvent ev)
        {
            assert ev.getPropertyName().equals(SHOW_HIDDEN_PROP);

            VFSJFileChooser jfc = jfcRef.get();

            if (jfc == null)
            {
                // Our VFSJFileChooser is no longer around, so we no longer need to
                // listen for PropertyChangeEvents.
                Toolkit.getDefaultToolkit()
                       .removePropertyChangeListener(SHOW_HIDDEN_PROP, this);
            }
            else
            {
                boolean oldValue = jfc.useFileHiding;
                jfc.useFileHiding = !((Boolean) ev.getNewValue()).booleanValue();
                jfc.firePropertyChange(FILE_HIDING_CHANGED_PROPERTY, oldValue,
                    jfc.useFileHiding);
            }
        }
    }

    /**
     * This class implements accessibility support for the
     * <code>VFSJFileChooser</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to file chooser user-interface
     * elements.
     */
    protected class AccessibleJFileChooser extends AccessibleJComponent
    {
        /**
         * Gets the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        @Override
        public AccessibleRole getAccessibleRole()
        {
            return AccessibleRole.FILE_CHOOSER;
        }
    } // inner class AccessibleJFileChooser

    /**
     * VFSJFileChooser dialog types
     * @author Yves Zoundi <yveszoundi at users dot sf dot net>
     * @version 0.0.1
     */
    public enum DIALOG_TYPE
    {
        /**
        * Type value indicating that the <code>VFSJFileChooser</code> supports an
        * "Open" file operation.
        */
        OPEN,
        /**
        * Type value indicating that the <code>VFSJFileChooser</code> supports a
        * "Save" file operation.
        */
        SAVE,
        /**
         * Type value indicating that the <code>VFSJFileChooser</code> supports a
         * developer-specified file operation.
         */
        CUSTOM;
    }

    /**
     * VFSJFileChooser return types
     * @author Yves Zoundi <yveszoundi at users dot sf dot net>
     * @author Stan Love
     * @version 0.0.2
     */
    public enum RETURN_TYPE
    {
        /**
        * Return value if cancel is chosen.
        */
        CANCEL,
        /**
        * Return value if approve (yes, ok) is chosen.
        */
        APPROVE,
        /**
        * Return value if an error occured.
        */
        ERROR,
        /**
        * Return value so users can pre-set a selection value
        */
        NO_SELECTION;

    }

    /**
     * VFSJFileChooser file selection types
     * @author Yves Zoundi <yveszoundi at users dot sf dot net>
     * @version 0.0.1
     */
    public enum SELECTION_MODE
    {
        /** files selection */
        FILES_ONLY,
        /** directories selection */
        DIRECTORIES_ONLY,
        /** files and directories selection */
        FILES_AND_DIRECTORIES;
    }
}
