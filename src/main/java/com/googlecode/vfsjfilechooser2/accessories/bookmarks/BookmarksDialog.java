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
package com.googlecode.vfsjfilechooser2.accessories.bookmarks;


import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;


/**
 * The bookmarks dialog
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
@SuppressWarnings("serial")
public class BookmarksDialog extends JDialog
{
    private static final String MANAGER_PANEL_ID = "MANAGER";
    private static final String EDITOR_PANEL_ID = "EDITOR";
    private static final String MANAGER_DIALOG_TITLE = VFSResources.getMessage(
            "VFSJFileChooser.bookmarksManagerDialogTitle");
    private static final String EDITOR_DIALOG_TITLE = VFSResources.getMessage(
            "VFSJFileChooser.bookmarksEditorDialogTitle");
    private BookmarksManagerPanel managerPanel;
    private BookmarksEditorPanel editorPanel;
    private CardLayout layout = new CardLayout();
    private JPanel cards = new JPanel(layout);
    private boolean defaultViewVisible = true;

    /**
     * Create a new instance of <code>BookmarksDialog</code>
     * @param parent The parent window
     * @param chooser the file chooser
     */
    public BookmarksDialog(Frame parent, VFSJFileChooser chooser)
    {
        super(parent, MANAGER_DIALOG_TITLE, true);

        saveBookmarksOnWindowClosing();

        managerPanel = new BookmarksManagerPanel(this, chooser);
        editorPanel = new BookmarksEditorPanel(this, managerPanel.getModel());

        cards.add(managerPanel, MANAGER_PANEL_ID);
        cards.add(editorPanel, EDITOR_PANEL_ID);
        getContentPane().add(cards);

        pack();
    }

    /**
     * Returns the bookmarks
     * @return the bookmarks
     */
    public Bookmarks getBookmarks()
    {
        return managerPanel.getModel();
    }

    /**
     * Display the bookmarks editor
     * @param index The bookmark index to edit (-1 for new entries)
     */
    public void showEditorView(int index)
    {
        editorPanel.updateFieds(index);
        setTitle(EDITOR_DIALOG_TITLE);
        layout.show(cards, EDITOR_PANEL_ID);
        defaultViewVisible = false;
        repaint();
    }

    /**
     * Show the default view
     */
    public void restoreDefaultView()
    {
        setTitle(MANAGER_DIALOG_TITLE);
        layout.show(cards, MANAGER_PANEL_ID);
        defaultViewVisible = true;
        repaint();
    }

    /**
     * Save the bookmarks when the dialog is hidden
     */
    protected void saveBookmarksOnWindowClosing()
    {
        this.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    // Use the default view if the dialog was
                    // closed without the close button
                    if (!defaultViewVisible)
                    {
                        defaultViewVisible = true;
                        setTitle(MANAGER_DIALOG_TITLE);
                        layout.show(cards, MANAGER_PANEL_ID);
                    }

                    // save the bookmarks
                    managerPanel.getModel().save();
                }
            });
    }

    /**
     * Hide the dialog
     */
    public void cancel()
    {
        setVisible(false);
    }
}
