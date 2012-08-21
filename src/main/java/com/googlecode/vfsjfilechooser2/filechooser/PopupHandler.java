/*
 * Right click popup menu handler
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.googlecode.vfsjfilechooser2.utils.VFSResources;


/**
 * Right click popup menu handler
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public final class PopupHandler extends MouseAdapter
{
    private JTextComponent txt;
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JMenuItem cutItem;
    private JMenuItem selectAllItem;
    private JMenuItem clearItem;

    /**
     * Create a new instance of PopupListener
     */
    private PopupHandler()
    {
        // initialize the menu items of the popup menu
        clearItem = new JMenuItem(VFSResources.getMessage(
                    "VFSJFileChooser.clearItemText"));
        copyItem = new JMenuItem(VFSResources.getMessage(
                    "VFSJFileChooser.copyItemText"));
        pasteItem = new JMenuItem(VFSResources.getMessage(
                    "VFSJFileChooser.pasteItemText"));
        cutItem = new JMenuItem(VFSResources.getMessage(
                    "VFSJFileChooser.cutItemText"));
        selectAllItem = new JMenuItem(VFSResources.getMessage(
                    "VFSJFileChooser.selectAllItemText"));

        // listener for the cut item
        cutItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    txt.cut();
                }
            });

        // listener for the copy item
        copyItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    txt.copy();
                }
            });

        // listener for the paste menu item
        pasteItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    txt.paste();
                }
            });

        // listener for the "clear" menu item
        clearItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    txt.setText("");
                }
            });

        // listener for the "Select all" menu item
        selectAllItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    txt.selectAll();
                }
            });

        // add the menu items to the popup menu
        popup.add(cutItem);
        popup.add(copyItem);
        popup.add(pasteItem);
        popup.add(clearItem);
        popup.add(selectAllItem);
    }

    private static final PopupHandler INSTANCE = new PopupHandler();

    public static void installDefaultMouseListener(JTextComponent jtc){
        jtc.addMouseListener(INSTANCE);
    }
    /**
     * Create a new instance of PopupListener
     * @param popup A popup menu
     */
    public PopupHandler(JPopupMenu popup)
    {
        setPopup(popup);
    }

    /*
     * (non-Javadoc)
     * @see  java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e))
        {
            txt = (JTextComponent) e.getSource();

            final boolean editable = txt.isEditable();
            final boolean textSelected = (txt.getSelectionStart() != txt.getSelectionEnd());
            copyItem.setEnabled(textSelected);
            cutItem.setEnabled(editable && textSelected);
            pasteItem.setEnabled(editable);

            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Set the popup menu associated to this listener
     * @param popup The popup menu associated to this listener
     */
    public void setPopup(JPopupMenu popup)
    {
        this.popup = popup;
    }

    /**
     * Returns the popup menu associated to this listener
     * @return The popup menu associated to this listener
     */
    public JPopupMenu getPopup()
    {
        return popup;
    }
}
