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

import javax.swing.filechooser.FileView;

import org.apache.commons.vfs2.FileObject;

/**
 * A file filter for file objects based on java.io.FileFilter
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public abstract class AbstractVFSFileFilter
{
    /**
     * Whether the given file is accepted by this filter.
     * @param f
     * @return
     */
    public abstract boolean accept(FileObject f);

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     * @return
     * @see FileView#getName
     */
    public abstract String getDescription();
}
