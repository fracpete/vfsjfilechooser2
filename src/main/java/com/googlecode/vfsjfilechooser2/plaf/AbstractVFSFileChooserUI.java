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
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileFilter;
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileView;

import javax.swing.plaf.ComponentUI;


/**
 * The FileChooserUI implementation using commons-vfs based on Swing FileChooserUI
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public abstract class AbstractVFSFileChooserUI extends ComponentUI
{
    /**
     *
     * @param fc
     * @return
     */
    public abstract AbstractVFSFileFilter getAcceptAllFileFilter(
        VFSJFileChooser fc);

    /**
     *
     * @param fc
     * @return
     */
    public abstract AbstractVFSFileView getFileView(VFSJFileChooser fc);

    /**
     *
     * @param fc
     * @return
     */
    public abstract String getApproveButtonText(VFSJFileChooser fc);

    /**
     *
     * @param fc
     * @return
     */
    public abstract String getDialogTitle(VFSJFileChooser fc);

    /**
     *
     * @param fc
     */
    public abstract void rescanCurrentDirectory(VFSJFileChooser fc);

    /**
     *
     * @param fc The fileChooser
     * @param f The fileobject
     */
    public abstract void ensureFileIsVisible(VFSJFileChooser fc, FileObject f);
}
