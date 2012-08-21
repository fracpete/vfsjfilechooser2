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
package com.googlecode.vfsjfilechooser2.filechooser;


import org.apache.commons.vfs2.FileObject;

import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

import javax.swing.Icon;

/**
 * The fileview implementation using commons-vfs based on Swing FileView
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public abstract class AbstractVFSFileView
{
    /**
     * The name of the file. Normally this would be simply
     * <code>f.getName()</code>.
     * @param f
     * @return
     */
    public String getName(FileObject f)
    {
        return VFSUtils.getFriendlyName(f.getName().toString());
    }

    /**
     * A human readable description of the file. For example,
     * a file named <i>jag.jpg</i> might have a description that read:
     * "A JPEG image file of James Gosling's face".
     * @param f
     * @return
     */
    public String getDescription(FileObject f)
    {
        return getName(f);
    }

    /**
     * A human readable description of the type of the file. For
     * example, a <code>jpg</code> file might have a type description of:
     * "A JPEG Compressed Image File"
     * @param f
     * @return
     */
    public String getTypeDescription(FileObject f)
    {
        return getName(f);
    }

    /**
     * The icon that represents this file in the <code>JFileChooser</code>.
     * @param f
     * @return
     */
    public Icon getIcon(FileObject f)
    {
        return null;
    }

    /**
     * Whether the directory is traversable or not. This might be
     * useful, for example, if you want a directory to represent
     * a compound document and don't want the user to descend into it.
     * @param f
     * @return
     */
    public Boolean isTraversable(FileObject f)
    {
        return null;
    }
}
