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
package com.googlecode.vfsjfilechooser2.plaf.metal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;


/**
 * <code>ButtonAreaLayout</code> behaves in a similar manner to
 * <code>FlowLayout</code>. It lays out all components from left to
 * right, flushed right. The widths of all components will be set
   * to the largest preferred size width.
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
final class ButtonAreaLayout implements LayoutManager
{
    private int hGap = 5;
    private int topMargin = 17;
    private final Dimension dummyDimension = new Dimension(0, 0);

    public void addLayoutComponent(String string, Component comp)
    {
    }

    public void layoutContainer(Container container)
    {
        Component[] children = container.getComponents();

        if ((children != null) && (children.length > 0))
        {
            final int numChildren = children.length;
            Dimension[] sizes = new Dimension[numChildren];
            Insets insets = container.getInsets();
            int yLocation = insets.top + topMargin;
            int maxWidth = 0;

            for (int counter = 0; counter < numChildren; counter++)
            {
                sizes[counter] = children[counter].getPreferredSize();
                maxWidth = Math.max(maxWidth, sizes[counter].width);
            }

            int xLocation;
            int xOffset;

            if (container.getComponentOrientation().isLeftToRight())
            {
                xLocation = container.getSize().width - insets.left - maxWidth;
                xOffset = hGap + maxWidth;
            }
            else
            {
                xLocation = insets.left;
                xOffset = -(hGap + maxWidth);
            }

            for (int counter = numChildren - 1; counter >= 0; counter--)
            {
                children[counter].setBounds(xLocation, yLocation, maxWidth,
                    sizes[counter].height);
                xLocation -= xOffset;
            }
        }
    }

    public Dimension minimumLayoutSize(Container c)
    {
        if (c != null)
        {
            Component[] children = c.getComponents();

            if ((children != null) && (children.length > 0))
            {
                final int numChildren = children.length;
                int height = 0;
                Insets cInsets = c.getInsets();
                int extraHeight = topMargin + cInsets.top + cInsets.bottom;
                int extraWidth = cInsets.left + cInsets.right;
                int maxWidth = 0;

                for (Component comp : children)
                {
                    Dimension aSize = comp.getPreferredSize();
                    height = Math.max(height, aSize.height);
                    maxWidth = Math.max(maxWidth, aSize.width);
                }

                return new Dimension(extraWidth + (numChildren * maxWidth) +
                    ((numChildren - 1) * hGap), extraHeight + height);
            }
        }

        return dummyDimension;
    }

    public Dimension preferredLayoutSize(Container c)
    {
        return minimumLayoutSize(c);
    }

    public void removeLayoutComponent(Component c)
    {
    }
}
