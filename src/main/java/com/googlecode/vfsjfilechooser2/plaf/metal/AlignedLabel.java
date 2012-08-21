/*
 * Code based on the <code>AlignedLabel</code> class in swing' MetalFileChooserUI
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

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * Code based on the <code>AlignedLabel</code> class in swing' MetalFileChooserUI
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
@SuppressWarnings("serial")
final class AlignedLabel extends JLabel
{
    protected AlignedLabel[] group;
    private int maxWidth = 0;

    public AlignedLabel()
    {
        super();
        setAlignmentX(JComponent.LEFT_ALIGNMENT);
    }

    public AlignedLabel(String text)
    {
        super(text);
        setAlignmentX(JComponent.LEFT_ALIGNMENT);
    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();

        // Align the width with all other labels in group.
        return new Dimension(getMaxWidth() + 11, d.height);
    }

    private int getMaxWidth()
    {
        if ((maxWidth == 0) && (group != null))
        {
            int max = 0;

            for (AlignedLabel grp : group)
            {
                max = Math.max(grp.getSuperPreferredWidth(), max);
            }

            for (AlignedLabel grp : group)
            {
                grp.maxWidth = max;
            }
        }

        return maxWidth;
    }

    private int getSuperPreferredWidth()
    {
        return super.getPreferredSize().width;
    }
}
