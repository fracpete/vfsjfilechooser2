/*
 * Icon in the navigation panel (home, up, new, etc.)
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
package com.googlecode.vfsjfilechooser2.plaf.metal;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;


/**
 * Icon in the navigation panel (home, up, new, etc.)
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
final class IndentIcon implements Icon
{
    private final static int space = 10;
    private Icon icon = null;
    protected int depth = 0;

    /**
     * @param icone
     */
    public IndentIcon(final Icon icone)
    {
        icon = icone;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        if (icon == null)
        {
        }
        else
        {
            if (c.getComponentOrientation().isLeftToRight())
            {
                icon.paintIcon(c, g, x + (depth * space), y);
            }
            else
            {
                icon.paintIcon(c, g, x, y);
            }
        }
    }

    public int getIconWidth()
    {
        if (icon == null)
        {
            return depth * space;
        }

        return icon.getIconWidth() + (depth * space);
    }

    public int getIconHeight()
    {
        if (icon == null)
        {
            return depth * space;
        }

        return icon.getIconHeight();
    }
}
