/*
 *
 * Copyright (C) 2008-2009 Yves Zoundi
 * Copyright (C) 2012 University of Waikato, Hamilton, NZ (made GlobFilter functional)
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ActionMapUIResource;


import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.local.LocalFile;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.DIALOG_TYPE;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.SELECTION_MODE;
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileFilter;
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileSystemView;
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileView;
import com.googlecode.vfsjfilechooser2.filepane.VFSFilePane;
import com.googlecode.vfsjfilechooser2.plaf.AbstractVFSFileChooserUI;
import com.googlecode.vfsjfilechooser2.utils.SwingCommonsUtilities;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

/**
 * The BasicFileChooserUI implementation using commons-vfs based on Swing BasicFileChooserUI
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public class BasicVFSFileChooserUI extends AbstractVFSFileChooserUI
{
    /* FileView icons */
    protected Icon directoryIcon = null;
    protected Icon fileIcon = null;
    protected Icon computerIcon = null;
    protected Icon hardDriveIcon = null;
    protected Icon floppyDriveIcon = null;
    protected Icon newFolderIcon = null;
    protected Icon upFolderIcon = null;
    protected Icon homeFolderIcon = null;
    protected Icon listViewIcon = null;
    protected Icon detailsViewIcon = null;
    protected Icon viewMenuIcon = null;
    protected int saveButtonMnemonic = 0;
    protected int openButtonMnemonic = 0;
    protected int cancelButtonMnemonic = 0;
    protected int updateButtonMnemonic = 0;
    protected int helpButtonMnemonic = 0;

    /**
     * The mnemonic keycode used for the approve button when a directory
     * is selected and the current selection mode is FILES_ONLY.
     *
     * @since 1.4
     */
    protected int directoryOpenButtonMnemonic = 0;
    protected String saveButtonText = null;
    protected String openButtonText = null;
    protected String cancelButtonText = null;
    protected String updateButtonText = null;
    protected String helpButtonText = null;

    /**
     * The label text displayed on the approve button when a directory
     * is selected and the current selection mode is FILES_ONLY.
     *
     * @since 1.4
     */
    protected String directoryOpenButtonText = null;
    private String openDialogTitleText = null;
    private String saveDialogTitleText = null;
    protected String saveButtonToolTipText = null;
    protected String openButtonToolTipText = null;
    protected String cancelButtonToolTipText = null;
    protected String updateButtonToolTipText = null;
    protected String helpButtonToolTipText = null;

    /**
     * The tooltip text displayed on the approve button when a directory
     * is selected and the current selection mode is FILES_ONLY.
     *
     * @since 1.4
     */
    protected String directoryOpenButtonToolTipText = null;

    // Some generic FileChooser functions
    private Action approveSelectionAction = new ApproveSelectionAction();
    private Action cancelSelectionAction = new CancelSelectionAction();
    private Action updateAction = new UpdateAction();
    private Action newFolderAction;
    private Action goHomeAction = new GoHomeAction();
    private Action changeToParentDirectoryAction = new ChangeToParentDirectoryAction();
    private String newFolderErrorSeparator = null;
    private String newFolderErrorText = null;
    private String newFolderParentDoesntExistTitleText = null;
    private String newFolderParentDoesntExistText = null;
    private String fileDescriptionText = null;
    private String directoryDescriptionText = null;
    private VFSJFileChooser filechooser = null;
    private boolean directorySelected = false;
    private FileObject directory = null;
    private PropertyChangeListener propertyChangeListener = null;
    private AcceptAllFileFilter acceptAllFileFilter = new AcceptAllFileFilter();
    private AbstractVFSFileFilter actualFileFilter = null;
    private GlobFilter globFilter = null;
    private BasicVFSDirectoryModel model = null;
    private BasicVFSFileView fileView = new BasicVFSFileView();
    private boolean usesSingleFilePane;
    private boolean readOnly;

    // The accessoryPanel is a container to place the VFSJFileChooser accessory component
    private JPanel accessoryPanel = null;
    private Handler handler;

    public BasicVFSFileChooserUI(VFSJFileChooser b)
    {
        this.filechooser = b;
    }

    /**
     *
     * @param c
     */
    @Override
    public void installUI(JComponent c)
    {
        accessoryPanel = new JPanel(new BorderLayout());
        filechooser = (VFSJFileChooser) c;

        createModel();

        clearIconCache();

        installDefaults(filechooser);
        installComponents(filechooser);
        installListeners(filechooser);

        filechooser.applyComponentOrientation(filechooser.getComponentOrientation());
    }

    /**
     *
     * @param c
     */
    @Override
    public void uninstallUI(JComponent c)
    {
        uninstallListeners((VFSJFileChooser) filechooser);
        uninstallComponents((VFSJFileChooser) filechooser);
        uninstallDefaults((VFSJFileChooser) filechooser);

        if (accessoryPanel != null)
        {
            accessoryPanel.removeAll();
        }

        accessoryPanel = null;
        getFileChooser().removeAll();

        handler = null;
    }

    /**
     *
     * @param fc
     */
    public void installComponents(VFSJFileChooser fc)
    {
    }

    /**
     *
     * @param fc
     */
    public void uninstallComponents(VFSJFileChooser fc)
    {
    }

    protected void installListeners(VFSJFileChooser fc)
    {
        propertyChangeListener = createPropertyChangeListener(fc);

        if (propertyChangeListener != null)
        {
            fc.addPropertyChangeListener(propertyChangeListener);
        }

        fc.addPropertyChangeListener(getModel());

        ActionMap actionMap = getActionMap();
        SwingUtilities.replaceUIActionMap(fc, actionMap);
    }

    protected ActionMap getActionMap()
    {
        return createActionMap();
    }

    protected ActionMap createActionMap()
    {
        ActionMap map = new ActionMapUIResource();

        Action refreshAction = new AbstractVFSUIAction(VFSFilePane.ACTION_REFRESH)
            {
                public void actionPerformed(ActionEvent evt)
                {
                    getFileChooser().rescanCurrentDirectory();
                }
            };

        map.put(VFSFilePane.ACTION_APPROVE_SELECTION,
            getApproveSelectionAction());
        map.put(VFSFilePane.ACTION_CANCEL, getCancelSelectionAction());
        map.put(VFSFilePane.ACTION_REFRESH, refreshAction);
        map.put(VFSFilePane.ACTION_CHANGE_TO_PARENT_DIRECTORY,
            getChangeToParentDirectoryAction());

        return map;
    }

    protected void uninstallListeners(VFSJFileChooser fc)
    {
        if (propertyChangeListener != null)
        {
            fc.removePropertyChangeListener(propertyChangeListener);
        }

        fc.removePropertyChangeListener(getModel());
        SwingUtilities.replaceUIInputMap(fc,
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
        SwingUtilities.replaceUIActionMap(fc, null);
    }

    protected void installDefaults(VFSJFileChooser fc)
    {
        installIcons(fc);
        installStrings(fc);
        usesSingleFilePane = UIManager.getBoolean(
                "FileChooser.usesSingleFilePane");
        readOnly = UIManager.getBoolean("FileChooser.readOnly");
        LookAndFeel.installProperty(fc, "opaque", Boolean.FALSE);
    }

    protected void installIcons(VFSJFileChooser fc)
    {
        UIDefaults defaults = UIManager.getLookAndFeel().getDefaults();
        directoryIcon = lookupIcon("folder.png");
        fileIcon = lookupIcon("file.png");
        computerIcon = defaults.getIcon("FileView.computerIcon");
        hardDriveIcon = defaults.getIcon("FileView.hardDriveIcon");
        floppyDriveIcon = defaults.getIcon("FileView.floppyDriveIcon");
        newFolderIcon = lookupIcon("folder_add.png");
        upFolderIcon = lookupIcon("go-up.png");
        homeFolderIcon = lookupIcon("folder_user.png");
        detailsViewIcon = lookupIcon("application_view_detail.png");
        listViewIcon = lookupIcon("application_view_list.png");
        viewMenuIcon = defaults.getIcon("FileChooser.viewMenuIcon");
    }

    // Hardcoded path until we get the possibility to have themes
    // or to use the default laf icons
    private Icon lookupIcon(String path)
    {
        return new ImageIcon(getClass()
                                 .getResource("/com/googlecode/vfsjfilechooser2/plaf/icons/" +
                path));
    }

    protected void installStrings(VFSJFileChooser fc)
    {
        Locale l = fc.getLocale();
        newFolderErrorText = VFSResources.getMessage("VFSJFileChooser.newFolderErrorText");
        newFolderErrorSeparator = VFSResources.getMessage("VFSJFileChooser.newFolderErrorSeparator");

        newFolderParentDoesntExistTitleText = VFSResources.getMessage("VFSJFileChooser.newFolderParentDoesntExistTitleText");
        newFolderParentDoesntExistText = VFSResources.getMessage("VFSJFileChooser.newFolderParentDoesntExistText");

        fileDescriptionText = VFSResources.getMessage("VFSJFileChooser.fileDescriptionText");
        directoryDescriptionText = VFSResources.getMessage("VFSJFileChooser.directoryDescriptionText");

        saveButtonText = VFSResources.getMessage("VFSJFileChooser.saveButtonText");
        openButtonText = VFSResources.getMessage("VFSJFileChooser.openButtonText");
        saveDialogTitleText = VFSResources.getMessage("VFSJFileChooser.saveDialogTitleText");
        openDialogTitleText = VFSResources.getMessage("VFSJFileChooser.openDialogTitleText");
        cancelButtonText = VFSResources.getMessage("VFSJFileChooser.cancelButtonText");
        updateButtonText = VFSResources.getMessage("VFSJFileChooser.updateButtonText");
        helpButtonText = VFSResources.getMessage("VFSJFileChooser.helpButtonText");
        directoryOpenButtonText = VFSResources.getMessage("VFSJFileChooser.directoryOpenButtonText");

        saveButtonMnemonic = getMnemonic("VFSJFileChooser.saveButtonMnemonic", l);
        openButtonMnemonic = getMnemonic("VFSJFileChooser.openButtonMnemonic", l);
        cancelButtonMnemonic = getMnemonic("VFSJFileChooser.cancelButtonMnemonic", l);
        updateButtonMnemonic = getMnemonic("VFSJFileChooser.updateButtonMnemonic", l);
        helpButtonMnemonic = getMnemonic("VFSJFileChooser.helpButtonMnemonic", l);
        directoryOpenButtonMnemonic = getMnemonic("VFSJFileChooser.directoryOpenButtonMnemonic",
                l);

        saveButtonToolTipText = VFSResources.getMessage("VFSJFileChooser.saveButtonToolTipText");
        openButtonToolTipText = VFSResources.getMessage("VFSJFileChooser.openButtonToolTipText");
        cancelButtonToolTipText = VFSResources.getMessage("VFSJFileChooser.cancelButtonToolTipText");
        updateButtonToolTipText = VFSResources.getMessage("VFSJFileChooser.updateButtonToolTipText");
        helpButtonToolTipText = VFSResources.getMessage("VFSJFileChooser.helpButtonToolTipText");
        directoryOpenButtonToolTipText = VFSResources.getMessage("VFSJFileChooser.directoryOpenButtonToolTipText");
    }

    protected void uninstallDefaults(VFSJFileChooser fc)
    {
        uninstallIcons(fc);
        uninstallStrings(fc);
    }

    protected void uninstallIcons(VFSJFileChooser fc)
    {
        directoryIcon = null;
        fileIcon = null;
        computerIcon = null;
        hardDriveIcon = null;
        floppyDriveIcon = null;

        newFolderIcon = null;
        upFolderIcon = null;
        homeFolderIcon = null;
        detailsViewIcon = null;
        listViewIcon = null;
        viewMenuIcon = null;
    }

    protected void uninstallStrings(VFSJFileChooser fc)
    {
        saveButtonText = null;
        openButtonText = null;
        cancelButtonText = null;
        updateButtonText = null;
        helpButtonText = null;
        directoryOpenButtonText = null;

        saveButtonToolTipText = null;
        openButtonToolTipText = null;
        cancelButtonToolTipText = null;
        updateButtonToolTipText = null;
        helpButtonToolTipText = null;
        directoryOpenButtonToolTipText = null;
    }

    protected void createModel()
    {
        if (model != null)
        {
            model.invalidateFileCache();
        }

        model = new BasicVFSDirectoryModel(getFileChooser());
    }

    public BasicVFSDirectoryModel getModel()
    {
        return model;
    }

    public PropertyChangeListener createPropertyChangeListener(
        VFSJFileChooser fc)
    {
        return null;
    }

    public String getFileName()
    {
        return null;
    }

    public String getDirectoryName()
    {
        return null;
    }

    public void setFileName(String filename)
    {
    }

    public void setDirectoryName(String dirname)
    {
    }

    public void rescanCurrentDirectory(VFSJFileChooser fc)
    {
    }

    public void ensureFileIsVisible(VFSJFileChooser fc, FileObject f)
    {
    }

    public VFSJFileChooser getFileChooser()
    {
        return filechooser;
    }

    public JPanel getAccessoryPanel()
    {
        return accessoryPanel;
    }

    protected JButton getApproveButton(VFSJFileChooser fc)
    {
        return null;
    }

    public String getApproveButtonToolTipText(VFSJFileChooser fc)
    {
        String tooltipText = fc.getApproveButtonToolTipText();

        if (tooltipText != null)
        {
            return tooltipText;
        }

        if (fc.getDialogType() == DIALOG_TYPE.OPEN)
        {
            return openButtonToolTipText;
        }
        else if (fc.getDialogType() == DIALOG_TYPE.SAVE)
        {
            return saveButtonToolTipText;
        }

        return null;
    }

    public void clearIconCache()
    {
        fileView.clearIconCache();
    }

    // ********************************************
    // ************ Create Listeners **************
    // ********************************************
    private Handler getHandler()
    {
        if (handler == null)
        {
            handler = new Handler();
        }

        return handler;
    }

    protected MouseListener createDoubleClickListener(VFSJFileChooser fc,
        JList list)
    {
        return new Handler(list);
    }

    public ListSelectionListener createListSelectionListener(VFSJFileChooser fc)
    {
        return getHandler();
    }

    /**
     * Property to remember whether a directory is currently selected in the UI.
     *
     * @return <code>true</code> iff a directory is currently selected.
     * @since 1.4
     */
    protected boolean isDirectorySelected()
    {
        return directorySelected;
    }

    /**
     * Property to remember whether a directory is currently selected in the UI.
     * This is normally called by the UI on a selection event.
     *
     * @param b iff a directory is currently selected.
     * @since 1.4
     */
    protected void setDirectorySelected(boolean b)
    {
        directorySelected = b;
    }

    /**
     * Property to remember the directory that is currently selected in the UI.
     *
     * @return the value of the <code>directory</code> property
     * @see #setDirectory
     * @since 1.4
     */
    protected FileObject getDirectory()
    {
        return directory;
    }

    /**
     * Property to remember the directory that is currently selected in the UI.
     * This is normally called by the UI on a selection event.
     *
     * @param f the <code>File</code> object representing the directory that is
     *          currently selected
     * @since 1.4
     */
    protected void setDirectory(FileObject f)
    {
        directory = f;
    }

    public static int getUIDefaultsInt(Object key, Locale l, int defaultValue)
    {
        Object value = UIManager.get(key, l);

        if (value instanceof Integer)
        {
            return ((Integer) value).intValue();
        }

        if (value instanceof String)
        {
            try
            {
                return Integer.parseInt((String) value);
            }
            catch (NumberFormatException nfe)
            {
            }
        }

        return defaultValue;
    }

    /**
     * Returns the mnemonic for the given key.
     */
    private int getMnemonic(String key, Locale l)
    {
        return getUIDefaultsInt(key, l, 0);
    }

    // *******************************************************
    // ************ FileChooser UI PLAF methods **************
    // *******************************************************
    /**
     * Returns the default accept all file filter
     */
    public AbstractVFSFileFilter getAcceptAllFileFilter(VFSJFileChooser fc)
    {
        return acceptAllFileFilter;
    }

    public AbstractVFSFileView getFileView(VFSJFileChooser fc)
    {
        return fileView;
    }

    /**
     * Returns the title of this dialog
     */
    public String getDialogTitle(VFSJFileChooser fc)
    {
        String dialogTitle = fc.getDialogTitle();

        if (dialogTitle != null)
        {
            return dialogTitle;
        }
        else if (fc.getDialogType() == DIALOG_TYPE.OPEN)
        {
            return openDialogTitleText;
        }
        else if (fc.getDialogType() == DIALOG_TYPE.SAVE)
        {
            return saveDialogTitleText;
        }
        else
        {
            return getApproveButtonText(fc);
        }
    }

    public int getApproveButtonMnemonic(VFSJFileChooser fc)
    {
        int mnemonic = fc.getApproveButtonMnemonic();

        if (mnemonic > 0)
        {
            return mnemonic;
        }
        else if (fc.getDialogType() == DIALOG_TYPE.OPEN)
        {
            return openButtonMnemonic;
        }
        else if (fc.getDialogType() == DIALOG_TYPE.SAVE)
        {
            return saveButtonMnemonic;
        }
        else
        {
            return mnemonic;
        }
    }

    public String getApproveButtonText(VFSJFileChooser fc)
    {
        String buttonText = fc.getApproveButtonText();

        if (buttonText != null)
        {
            return buttonText;
        }
        else if (fc.getDialogType() == DIALOG_TYPE.OPEN)
        {
            return openButtonText;
        }
        else if (fc.getDialogType() == DIALOG_TYPE.SAVE)
        {
            return saveButtonText;
        }
        else
        {
            return null;
        }
    }

    // *****************************
    // ***** Directory Actions *****
    // *****************************
    public Action getNewFolderAction()
    {
        if (newFolderAction == null)
        {
            newFolderAction = new NewFolderAction();

            // Note: Don't return null for readOnly, it might
            // break older apps.
            if (readOnly)
            {
                newFolderAction.setEnabled(false);
            }
        }

        return newFolderAction;
    }

    public Action getGoHomeAction()
    {
        return goHomeAction;
    }

    public Action getChangeToParentDirectoryAction()
    {
        return changeToParentDirectoryAction;
    }

    public Action getApproveSelectionAction()
    {
        return approveSelectionAction;
    }

    public Action getCancelSelectionAction()
    {
        return cancelSelectionAction;
    }

    public Action getUpdateAction()
    {
        return updateAction;
    }

    private void resetGlobFilter()
    {
        if (actualFileFilter != null)
        {
            VFSJFileChooser chooser = getFileChooser();
            AbstractVFSFileFilter currentFilter = chooser.getFileFilter();

            if ((currentFilter != null) && currentFilter.equals(globFilter))
            {
                chooser.setFileFilter(actualFileFilter);
                chooser.removeChoosableFileFilter(globFilter);
            }

            actualFileFilter = null;
        }
    }

    private static boolean isGlobPattern(String filename)
    {
        return (((File.separatorChar == '\\') &&
        ((filename.indexOf('*') >= 0) || (filename.indexOf('?') >= 0))) ||
        ((File.separatorChar == '/') &&
        ((filename.indexOf('*') >= 0) || (filename.indexOf('?') >= 0) ||
        (filename.indexOf('[') >= 0))));
    }

    public void changeDirectory(FileObject dir)
    {
        VFSJFileChooser fc = getFileChooser();

        fc.setCurrentDirectoryObject(dir);

        if ((fc.getFileSelectionMode() == SELECTION_MODE.FILES_AND_DIRECTORIES) &&
                fc.getFileSystemView().isFileSystem(dir))
        {
            setFileName(dir.getName().getBaseName());
        }
    }

    private class Handler implements MouseListener, ListSelectionListener
    {
        JList list;

        Handler()
        {
        }

        Handler(JList list)
        {
            this.list = list;
        }

        public void mouseClicked(MouseEvent evt)
        {
            // System.out.println("count2:" + evt.getClickCount());

            // Note: we can't depend on evt.getSource() because of backward
            // compatability
            if ((list != null) && SwingUtilities.isLeftMouseButton(evt) &&
                    (evt.getClickCount() == 2))
            {
                int index = SwingCommonsUtilities.loc2IndexFileList(list,
                        evt.getPoint());

                if (index >= 0)
                {
                    FileObject f = (FileObject) list.getModel()
                                                    .getElementAt(index);

                    if (getFileChooser().isTraversable(f))
                    {
                        list.clearSelection();
                        changeDirectory(f);
                    }
                    else
                    {
                        getFileChooser().approveSelection();
                    }
                }
            }
        }

        public void mouseEntered(MouseEvent evt)
        {
            if (list != null)
            {
                TransferHandler th1 = getFileChooser().getTransferHandler();
                TransferHandler th2 = list.getTransferHandler();

                if (th1 != th2)
                {
                    list.setTransferHandler(th1);
                }

                if (getFileChooser().getDragEnabled() != list.getDragEnabled())
                {
                    list.setDragEnabled(getFileChooser().getDragEnabled());
                }
            }
        }

        public void mouseExited(MouseEvent evt)
        {
        }

        public void mousePressed(MouseEvent evt)
        {
        }

        public void mouseReleased(MouseEvent evt)
        {
        }

        public void valueChanged(ListSelectionEvent evt)
        {
            if (!evt.getValueIsAdjusting())
            {
                VFSJFileChooser chooser = getFileChooser();
                AbstractVFSFileSystemView fsv = chooser.getFileSystemView();
                JList list = (JList) evt.getSource();

                SELECTION_MODE fsm = chooser.getFileSelectionMode();
                boolean useSetDirectory = usesSingleFilePane &&
                    (fsm == SELECTION_MODE.FILES_ONLY);

                if (chooser.isMultiSelectionEnabled())
                {
                    FileObject[] files = new FileObject[0];

                    Object[] objects = list.getSelectedValues();

                    if (objects != null)
                    {
                        final int count = objects.length;

                        if ((count == 1) &&
                                (VFSUtils.isDirectory((FileObject) objects[0]) &&
                                chooser.isTraversable(((FileObject) objects[0])) &&
                                (useSetDirectory ||
                                !fsv.isFileSystem(((FileObject) objects[0])))))
                        {
                            setDirectorySelected(true);
                            setDirectory(((FileObject) objects[0]));
                        }
                        else
                        {
                            List<FileObject> fList = new ArrayList<FileObject>(count);

                            for (int i = 0; i < count; i++)
                            {
                                FileObject f = (FileObject) objects[i];
                                boolean isDir = VFSUtils.isDirectory(f);

                                if ((chooser.isFileSelectionEnabled() &&
                                        !isDir) ||
                                        (chooser.isDirectorySelectionEnabled() &&
                                        fsv.isFileSystem(f) && isDir))
                                {
                                    fList.add(f);
                                }
                            }

                            if (!fList.isEmpty())
                            {
                                files = fList.toArray(new FileObject[fList.size()]);
                            }

                            setDirectorySelected(false);
                        }
                    }

                    chooser.setSelectedFileObjects(files);
                }
                else
                {
                    FileObject file = (FileObject) list.getSelectedValue();

                    if ((file != null) && chooser.isTraversable(file) &&
                            (useSetDirectory || !fsv.isFileSystem(file)))
                    {
                        setDirectorySelected(true);
                        setDirectory(file);

                        if (usesSingleFilePane)
                        {
                            chooser.setSelectedFileObject(null);
                        }
                    }
                    else
                    {
                        setDirectorySelected(false);

                        if (file != null)
                        {
                            chooser.setSelectedFileObject(file);
                        }
                    }
                }
            }
        }
    }

    protected class DoubleClickListener extends MouseAdapter
    {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        Handler handler;

        public DoubleClickListener(JList list)
        {
            handler = new Handler(list);
        }

        /**
         * The JList used for representing the files is created by subclasses, but the
         * selection is monitored in this class.  The TransferHandler installed in the
         * VFSJFileChooser is also installed in the file list as it is used as the actual
         * transfer source.  The list is updated on a mouse enter to reflect the current
         * data transfer state of the file chooser.
         */
        @Override
        public void mouseEntered(MouseEvent e)
        {
            handler.mouseEntered(e);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            handler.mouseClicked(e);
        }
    }

    protected class SelectionListener implements ListSelectionListener
    {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        public void valueChanged(ListSelectionEvent e)
        {
            getHandler().valueChanged(e);
        }
    }

    /**
     * Creates a new folder.
     */
    @SuppressWarnings("serial")
    protected class NewFolderAction extends AbstractAction
    {
        protected NewFolderAction()
        {
            super(VFSFilePane.ACTION_NEW_FOLDER);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (readOnly)
            {
                return;
            }

            VFSJFileChooser fc = getFileChooser();
            FileObject currentDirectory = fc.getCurrentDirectoryObject();

            if (!VFSUtils.exists(currentDirectory))
            {
                JOptionPane.showMessageDialog(fc,
                    newFolderParentDoesntExistText,
                    newFolderParentDoesntExistTitleText,
                    JOptionPane.WARNING_MESSAGE);

                return;
            }

            FileObject newFolder;

            try
            {
                newFolder = fc.getFileSystemView()
                              .createNewFolder(currentDirectory);

                if (fc.isMultiSelectionEnabled())
                {
                    fc.setSelectedFileObjects(new FileObject[] { newFolder });
                }
                else
                {
                    fc.setSelectedFileObject(newFolder);
                }
            }
            catch (IOException exc)
            {
                JOptionPane.showMessageDialog(fc,
                    newFolderErrorText + newFolderErrorSeparator + exc,
                    newFolderErrorText, JOptionPane.ERROR_MESSAGE);

                return;
            }

            fc.rescanCurrentDirectory();
        }
    }

    /**
     * Acts on the "home" key event or equivalent event.
     */
    @SuppressWarnings("serial")
    protected class GoHomeAction extends AbstractAction
    {
        protected GoHomeAction()
        {
            super("Go Home");
        }

        public void actionPerformed(ActionEvent e)
        {
            VFSJFileChooser fc = getFileChooser();
            FileObject currentDir = fc.getCurrentDirectoryObject();

            if (currentDir instanceof LocalFile)
            {
                changeDirectory(fc.getFileSystemView().getHomeDirectory());
            }
            else
            {
                try
                {
                    changeDirectory(fc.getCurrentDirectoryObject().getFileSystem()
                                      .getRoot());
                }
                catch (FileSystemException ex)
                {
                    Logger.getLogger(BasicVFSFileChooserUI.class.getName())
                          .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @SuppressWarnings("serial")
    protected class ChangeToParentDirectoryAction extends AbstractAction
    {
        protected ChangeToParentDirectoryAction()
        {
            super("Go Up");
            putValue(Action.ACTION_COMMAND_KEY,
                VFSFilePane.ACTION_CHANGE_TO_PARENT_DIRECTORY);
        }

        public void actionPerformed(ActionEvent e)
        {
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                                       .getFocusOwner();

            if ((focusOwner == null) ||
                    !(focusOwner instanceof javax.swing.text.JTextComponent))
            {
                getFileChooser().changeToParentDirectory();
            }
        }
    }

    /**
     * Responds to an Open or Save request
     */
    @SuppressWarnings("serial")
    protected class ApproveSelectionAction extends AbstractAction
    {
        protected ApproveSelectionAction()
        {
            super(VFSFilePane.ACTION_APPROVE_SELECTION);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (isDirectorySelected())
            {
                FileObject dir = getDirectory();

                if (dir != null)
                {
                    changeDirectory(dir);

                    return;
                }
            }

            VFSJFileChooser chooser = getFileChooser();

            String filename = getFileName();
            AbstractVFSFileSystemView fs = chooser.getFileSystemView();
            FileObject dir = chooser.getCurrentDirectoryObject();

            if (filename != null)
            {
                // Remove whitespace from beginning and end of filename
                filename = filename.trim();
            }

            if ((filename == null) || filename.equals(""))
            {
                // no file selected, multiple selection off, therefore cancel the approve action
                resetGlobFilter();

                return;
            }

            FileObject selectedFile = null;
            FileObject[] selectedFiles = null;

            if ((filename != null) && !filename.equals(""))
            {
                // Unix: Resolve '~' to user's home directory
                if (File.separatorChar == '/')
                {
                    if (filename.startsWith("~/"))
                    {
                        filename = System.getProperty("user.home") +
                            filename.substring(1);
                    }
                    else if (filename.equals("~"))
                    {
                        filename = System.getProperty("user.home");
                    }
                }

                if (chooser.isMultiSelectionEnabled() &&
                        (filename.charAt(0) == '\"'))
                {
                    List<FileObject> fList = new ArrayList<FileObject>();

                    filename = filename.substring(1);

                    if (filename.endsWith("\""))
                    {
                        filename = filename.substring(0, filename.length() - 1);
                    }

                    do
                    {
                        String str;
                        int i = filename.indexOf("\" \"");

                        if (i > 0)
                        {
                            str = filename.substring(0, i);
                            filename = filename.substring(i + 3);
                        }
                        else
                        {
                            str = filename;
                            filename = "";
                        }

                        FileObject file = fs.createFileObject(str);

                        if (file == null)
                        {
                            file = fs.createFileObject(dir, str);
                        }

                        fList.add(file);
                    }
                    while (filename.length() > 0);

                    if (!fList.isEmpty())
                    {
                        selectedFiles = fList.toArray(new FileObject[fList.size()]);
                    }

                    resetGlobFilter();
                }
                else
                {
                    /// ZOUNDI MARK
                    selectedFile = fs.createFileObject(filename);

                    if (!VFSUtils.exists(selectedFile))
                    {
                        selectedFile = VFSUtils.resolveFileObject(getFileName());

                        if ((selectedFile == null) ||
                                !VFSUtils.exists(selectedFile))
                        {
                            selectedFile = fs.getChild(dir, filename);
                        }
                    }

                    // check for wildcard pattern
                    AbstractVFSFileFilter currentFilter = chooser.getFileFilter();

                    if (!VFSUtils.exists(selectedFile) &&
                            isGlobPattern(filename))
                    {
                        changeDirectory(VFSUtils.getParentDirectory(
                                selectedFile));

                        if (globFilter == null)
                        {
                            globFilter = new GlobFilter();
                        }

                        try
                        {
                            globFilter.setPattern(selectedFile.getName()
                                                              .getBaseName());

                            if (!(currentFilter instanceof GlobFilter))
                            {
                                actualFileFilter = currentFilter;
                            }

                            chooser.setFileFilter(null);
                            chooser.setFileFilter(globFilter);

                            return;
                        }
                        catch (PatternSyntaxException pse)
                        {
                            // Not a valid glob pattern. Abandon filter.
                        }
                    }

                    resetGlobFilter();

                    // Check for directory change action
                    boolean isDir = ((selectedFile != null) &&
                        VFSUtils.isDirectory(selectedFile));
                    boolean isTrav = ((selectedFile != null) &&
                        chooser.isTraversable(selectedFile));
                    boolean isDirSelEnabled = chooser.isDirectorySelectionEnabled();
                    boolean isFileSelEnabled = chooser.isFileSelectionEnabled();
                    boolean isCtrl = ((e != null) &&
                        ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0));

                    if (isDir && isTrav && (isCtrl || !isDirSelEnabled))
                    {
                        changeDirectory(selectedFile);

                        return;
                    }
                    else if ((isDir || !isFileSelEnabled) &&
                            (!isDir || !isDirSelEnabled) &&
                            (!isDirSelEnabled || VFSUtils.exists(selectedFile)))
                    {
                        selectedFile = null;
                    }
                }
            }

            if ((selectedFiles != null) || (selectedFile != null))
            {
                if ((selectedFiles != null) ||
                        chooser.isMultiSelectionEnabled())
                {
                    if (selectedFiles == null)
                    {
                        selectedFiles = new FileObject[] { selectedFile };
                    }

                    chooser.setSelectedFileObjects(selectedFiles);
                    // Do it again. This is a fix for bug 4949273 to force the
                    // selected value in case the ListSelectionModel clears it
                    // for non-existing file names.
                    chooser.setSelectedFileObjects(selectedFiles);
                }
                else
                {
                    chooser.setSelectedFileObject(selectedFile);
                }

                chooser.approveSelection();
            }
            else
            {
                if (chooser.isMultiSelectionEnabled())
                {
                    chooser.setSelectedFileObjects(null);
                }
                else
                {
                    chooser.setSelectedFile(null);
                }

                chooser.cancelSelection();
            }
        }
    }

    /* A file filter which accepts file patterns containing
     * the special wildcards *? on Windows and *?[] on Unix.
     */
    static class GlobFilter extends AbstractVFSFileFilter
    {
        String globPattern;

        public void setPattern(String globPattern)
        {
            this.globPattern = globPattern;
        }

        public boolean accept(FileObject f)
        {
	    if (f == null) {
		return false;
	    }
	    if (VFSUtils.isDirectory(f)) {
		return true;
	    }
	    return FilenameUtils.wildcardMatch(f.getName().getBaseName(), globPattern);
        }

        public String getDescription()
        {
            return globPattern;
        }
    }

    /**
     * Responds to a cancel request.
     */
    @SuppressWarnings("serial")
    protected class CancelSelectionAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            getFileChooser().cancelSelection();
        }
    }

    /**
     * Rescans the files in the current directory
     */
    @SuppressWarnings("serial")
    protected class UpdateAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            VFSJFileChooser fc = getFileChooser();
            fc.setCurrentDirectoryObject(fc.getFileSystemView()
                                     .createFileObject(getDirectoryName()));
            fc.rescanCurrentDirectory();
        }
    }

    // *****************************************
    // ***** default AcceptAll file filter *****
    // *****************************************
    protected static class AcceptAllFileFilter extends AbstractVFSFileFilter
    {
        public AcceptAllFileFilter()
        {
        }

        public boolean accept(FileObject f)
        {
            return true;
        }

        public String getDescription()
        {
            return VFSResources.getMessage("VFSJFileChooser.acceptAllFileFilterText");
        }

        @Override
        public String toString()
        {
            return getDescription();
        }
    }

    // ***********************
    // * FileView operations *
    // ***********************
    public class BasicVFSFileView extends AbstractVFSFileView
    {
        /* FileView type descriptions */
        // PENDING(jeff) - pass in the icon cache size
        protected Map<FileObject, Icon> iconCache = new ConcurrentHashMap<FileObject, Icon>();

        public BasicVFSFileView()
        {
        }

        public void clearIconCache()
        {
            iconCache = null;
            iconCache = new ConcurrentHashMap<FileObject, Icon>();
        }

        @Override
        public String getName(FileObject f)
        {
            // Note: Returns display name rather than file name
            String fileName = null;

            if (f != null)
            {
                fileName = f.getName().getBaseName();
            }

            if (fileName != null)
            {
                if (fileName.trim().equals(""))
                {
                    fileName = f.getName().toString();
                }
            }

            return fileName;
        }

        @Override
        public String getDescription(FileObject f)
        {
            return f.getName().getBaseName();
        }

        @Override
        public String getTypeDescription(FileObject f)
        {
            String type = getFileChooser().getFileSystemView()
                              .getSystemTypeDescription(f);

            if (type == null)
            {
                if (VFSUtils.isDirectory(f))
                {
                    type = directoryDescriptionText;
                }
                else
                {
                    type = fileDescriptionText;
                }
            }

            return type;
        }

        public Icon getCachedIcon(FileObject f)
        {
            return (Icon) iconCache.get(f);
        }

        public void cacheIcon(FileObject f, Icon i)
        {
            if ((f == null) || (i == null))
            {
                return;
            }

            iconCache.put(f, i);
        }

        @Override
        public Icon getIcon(FileObject f)
        {
            Icon icon = getCachedIcon(f);

            if (icon != null)
            {
                return icon;
            }

            icon = fileIcon;

            if (f != null)
            {
                AbstractVFSFileSystemView fsv = getFileChooser()
                                                    .getFileSystemView();

                if (fsv.isFloppyDrive(f))
                {
                    icon = floppyDriveIcon;
                }
                else if (fsv.isDrive(f))
                {
                    icon = hardDriveIcon;
                }
                else if (fsv.isComputerNode(f))
                {
                    icon = computerIcon;
                }
                else if (VFSUtils.isDirectory(f))
                {
                    icon = directoryIcon;
                }
            }

            cacheIcon(f, icon);

            return icon;
        }

        public Boolean isHidden(FileObject f)
        {
            return VFSUtils.isHiddenFile(f);
        }
    }
}
