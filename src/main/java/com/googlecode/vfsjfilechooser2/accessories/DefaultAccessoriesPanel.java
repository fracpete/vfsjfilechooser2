/*
 *
 * Copyright (C) 2008-2009 Yves Zoundi
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
package com.googlecode.vfsjfilechooser2.accessories;


import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.commons.vfs2.FileObject;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.accessories.bookmarks.BookmarksDialog;
import com.googlecode.vfsjfilechooser2.accessories.connection.ConnectionDialog;
import com.googlecode.vfsjfilechooser2.utils.SwingCommonsUtilities;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;


/**
 * <p>The default accessory panel you could add
 * It contains a bookmarks manager and the a connection dialog</p>
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.2
 */
@SuppressWarnings("serial")
public final class DefaultAccessoriesPanel extends JComponent
{
    private static final String RES_PATH = "/com/googlecode/vfsjfilechooser2/plaf/icons/";
    private JButton bookmarksButton;
    private JButton localFSButton;
    private JButton connectionsButton;
    private BookmarksDialog bookmarksDialog;
    private ConnectionDialog connectionDialog;
    private JComponent buttonsPanel;
    private VFSJFileChooser fileChooser;

    /**
     * Create an accessory panel
     * @param fileChooser The file dialog
     */
    public DefaultAccessoriesPanel(final VFSJFileChooser fileChooser)
    {
        setLayout(new BorderLayout());

        this.fileChooser = fileChooser;

        initBorder();
        initComponents();
    }

    private void initBorder()
    {
        Border outsideBorder = new EtchedBorder();
        Border insideBorder = new EmptyBorder(2, 4, 0, 2);

        Border insideBorder1 = new CompoundBorder(outsideBorder, insideBorder);
        Border outsideBorder1 = new EmptyBorder(0, 2, 0, 2);

        setBorder(new CompoundBorder(outsideBorder1, insideBorder1));
    }

    private Icon getIcon(String iconName)
    {
        URL iconURL = getClass().getResource(RES_PATH + iconName);

        return new ImageIcon(iconURL);
    }

    private void initComponents()
    {
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(0, 1, 3, 3));

        Action action;

        action = new ManageBookmarksAction(VFSResources.getMessage(
                    "VFSJFileChooser.bookmarksLabelText"), getIcon("book.png"));
        bookmarksButton = new JButton(action);
        bookmarksButton.setHorizontalAlignment(SwingConstants.LEFT);

        action = new ConnectionWizardAction(VFSResources.getMessage(
                    "VFSJFileChooser.connectionButtonText"),
                getIcon("connect.png"));
        connectionsButton = new JButton(action);
        connectionsButton.setHorizontalAlignment(SwingConstants.LEFT);

        action = new LocalFilesAction(VFSResources.getMessage(
                    "VFSJFileChooser.localFilesButtonText"),
                getIcon("drive.png"));
        localFSButton = new JButton(action);
        localFSButton.setHorizontalAlignment(SwingConstants.LEFT);

        buttonsPanel.add(bookmarksButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(connectionsButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(localFSButton);

        add(buttonsPanel, BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.CENTER);

        final Frame c = (Frame) SwingUtilities.getWindowAncestor(fileChooser);

        bookmarksDialog = new BookmarksDialog(c, fileChooser);

        connectionDialog = new ConnectionDialog(c, bookmarksDialog, fileChooser);
    }

    /**
     * Action to display the connection wizard dialog
     */
    private class ConnectionWizardAction extends AbstractAction
    {
        public ConnectionWizardAction(String name, Icon icon)
        {
            super(name, icon);
        }

        public void actionPerformed(ActionEvent e)
        {
            connectionDialog.setLocationRelativeTo(connectionDialog.getOwner());
            connectionDialog.setVisible(true);
        }
    }

    /**
     * Action to show the local file system
     */
    private class LocalFilesAction extends AbstractAction
    {
        public LocalFilesAction(String name, Icon icon)
        {
            super(name, icon);
        }

        public void actionPerformed(ActionEvent e)
        {
            FileObject fo = SwingCommonsUtilities.getVFSFileChooserDefaultDirectory();
            fileChooser.setCurrentDirectoryObject(fo);
        }
    }

    /**
     * Action to display the bookmarks manager dialog
     */
    private class ManageBookmarksAction extends AbstractAction
    {
        public ManageBookmarksAction(String name, Icon icon)
        {
            super(name, icon);
        }

        public void actionPerformed(ActionEvent e)
        {
            bookmarksDialog.setLocationRelativeTo(bookmarksDialog.getOwner());
            bookmarksDialog.setVisible(true);
        }
    }
}
