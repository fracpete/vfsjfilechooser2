/*
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
package com.googlecode.vfsjfilechooser2.plaf;


import org.apache.commons.vfs2.FileObject;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.plaf.basic.BasicVFSDirectoryModel;

import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;


/**
 * Interface for a delegate class of the FileChooserUI
 * @author Yves Zoundi (yveszoundi at users dot sf dot net)
 * @version 0.0.1
 */
public interface VFSFileChooserUIAccessorIF
{
    /**
     *
     * @return the filechooser
     */
    public VFSJFileChooser getFileChooser();

    /**
     *
     * @return the directory model
     */
    public BasicVFSDirectoryModel getModel();

    /**
     *
     * @return the list
     */
    public JPanel createList();

    /**
    *
    * @return the details view
    */
    public JPanel createDetailsView();

    /**
     *
     * @return true if dir selected
     */
    public boolean isDirectorySelected();

    /**
     *
     * @return the dir
     */
    public FileObject getDirectory();

    /**
     *
     * @return the approve selection action
     */
    public Action getApproveSelectionAction();

    /**
     *
     * @return the change to parent dir action
     */
    public Action getChangeToParentDirectoryAction();

    /**
     *
     * @return the new folder action
     */
    public Action getNewFolderAction();

    /**
     *
     * @param list the list
     * @return the new mouse listener
     */
    public MouseListener createDoubleClickListener(JList list);

    /**
     *
     * @return the list selection listener
     */
    public ListSelectionListener createListSelectionListener();

    /**
     *
     * @return true if to use shell folder
     */
    public boolean usesShellFolder();
}
