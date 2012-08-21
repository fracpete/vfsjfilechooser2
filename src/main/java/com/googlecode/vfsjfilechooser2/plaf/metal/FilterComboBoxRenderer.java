/*
 * Renderer for the file filters drop down list
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
package com.googlecode.vfsjfilechooser2.plaf.metal;


import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileFilter;

/**
 * Renderer for the file filters drop down list
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
@SuppressWarnings("serial")
final class FilterComboBoxRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);

        if ((value != null) && value instanceof AbstractVFSFileFilter)
        {
            setText(((AbstractVFSFileFilter) value).getDescription());
        }

        return this;
    }
}
