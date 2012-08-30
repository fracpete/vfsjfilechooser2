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
package com.googlecode.vfsjfilechooser2.accessories.bookmarks;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.vfs2.FileObject;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;


/**
 * Bookmarks manager panel
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @author Jojada Tirtowidjojo <jojada at users.sourceforge.net>
 * @version 0.0.1
 */
@SuppressWarnings("serial")
public class BookmarksManagerPanel extends JPanel
{
    public static final int NO_BOOKMARK_SELECTION_INDEX = -1;
    private JScrollPane scrollPane;
    private JTable table;
    private JButton bOpen;
    private JButton bCancel;
    private JButton bEdit;
    private JButton bAdd;
    private JButton bDelete;
    private JButton bMoveUp;
    private JButton bMoveDown;
    private Bookmarks model;
    private VFSJFileChooser chooser;
    private final Dimension tableSize = new Dimension(350, 200);
    private BookmarksDialog parentDialog;

    public BookmarksManagerPanel(BookmarksDialog parentDialog,
        VFSJFileChooser chooser)
    {
        this.parentDialog = parentDialog;
        this.chooser = chooser;

        model = new Bookmarks();

        table = new JTable(model);
        scrollPane = new JScrollPane(table);

        table.setPreferredScrollableViewportSize(tableSize);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        bCancel = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.closeButtonText"));

        bOpen = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.openButtonText"));
        bOpen.setIcon(new ImageIcon(getClass()
                                        .getResource("/com/googlecode/vfsjfilechooser2/plaf/icons/document-open.png")));
        bOpen.setHorizontalAlignment(SwingConstants.LEFT);

        bAdd = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.addButtonText"));
        bAdd.setIcon(new ImageIcon(getClass()
                                       .getResource("/com/googlecode/vfsjfilechooser2/plaf/icons/list-add.png")));
        bAdd.setHorizontalAlignment(SwingConstants.LEFT);

        bEdit = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.editButtonText"));
        bEdit.setIcon(new ImageIcon(getClass()
                                        .getResource("/com/googlecode/vfsjfilechooser2/plaf/icons/book_edit.png")));
        bEdit.setHorizontalAlignment(SwingConstants.LEFT);

        bDelete = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.deleteButtonText"));
        bDelete.setIcon(new ImageIcon(getClass()
                                          .getResource("/com/googlecode/vfsjfilechooser2/plaf/icons/list-remove.png")));
        bDelete.setHorizontalAlignment(SwingConstants.LEFT);

        bMoveUp = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.moveUpButtonText"));
        bMoveUp.setIcon(new ImageIcon(getClass()
                                          .getResource("/com/googlecode/vfsjfilechooser2/plaf/icons/go-up.png")));
        bMoveUp.setHorizontalAlignment(SwingConstants.LEFT);

        bMoveDown = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.moveDownButtonText"));
        bMoveDown.setIcon(new ImageIcon(getClass()
                                            .getResource("/com/googlecode/vfsjfilechooser2/plaf/icons/go-down.png")));
        bMoveDown.setHorizontalAlignment(SwingConstants.LEFT);

        final ActionHandler ah = new ActionHandler();

        bOpen.addActionListener(ah);
        bCancel.addActionListener(ah);
        bEdit.addActionListener(ah);
        bAdd.addActionListener(ah);
        bDelete.addActionListener(ah);
        bMoveUp.addActionListener(ah);
        bMoveDown.addActionListener(ah);

        final Box south = Box.createHorizontalBox();
        south.add(Box.createHorizontalGlue());
        south.add(bCancel);
        south.add(Box.createHorizontalGlue());

        final JPanel buttons = new JPanel(new GridLayout(0, 1, 5, 5));

        buttons.add(bAdd);
        buttons.add(bEdit);
        buttons.add(bDelete);
        buttons.add(bOpen);
        buttons.add(Box.createVerticalStrut(10));
        buttons.add(bMoveUp);
        buttons.add(bMoveDown);

        JPanel east = new JPanel();
        east.add(buttons, BorderLayout.NORTH);
        east.add(new JPanel(), BorderLayout.CENTER); // don't ask

        setLayout(new BorderLayout(10, 10));

        add(scrollPane, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        add(east, BorderLayout.EAST);

        setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10,
                UIManager.getColor("Panel.background")));
    }

    public Bookmarks getModel()
    {
        return model;
    }

    public void cancel()
    {
        parentDialog.setVisible(false);
    }

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent actionEvent)
        {
            JButton button = (JButton) actionEvent.getSource();

            final int row = table.getSelectedRow();

            if (button.equals(bAdd))
            {
                parentDialog.showEditorView(NO_BOOKMARK_SELECTION_INDEX);
            }
            else if (button.equals(bEdit))
            {
                if (row != NO_BOOKMARK_SELECTION_INDEX)
                {
                    parentDialog.showEditorView(row);
                }
                else
                {
                    JOptionPane.showMessageDialog(getParent(),
                        VFSResources.getMessage(
                            "VFSJFileChooser.noselectionLabel"),
                        VFSResources.getMessage(
                            "VFSJFileChooser.errorLabel"),
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (button.equals(bOpen))
            {
                if (row != NO_BOOKMARK_SELECTION_INDEX)
                {
                    Thread worker = new Thread()
                        {
                            @Override
                            public void run()
                            {
                                setCursor(Cursor.getPredefinedCursor(
                                        Cursor.WAIT_CURSOR));

                                TitledURLEntry aTitledURLEntry = model.getEntry(row);

                                FileObject fo = null;

                                try
                                {
                                    fo = VFSUtils.resolveFileObject(aTitledURLEntry.getURL());

                                    if ((fo != null) && !fo.exists())
                                    {
                                        fo = null;
                                    }
                                }
                                catch (Exception exc)
                                {
                                    fo = null;
                                }

                                setCursor(Cursor.getDefaultCursor());

                                if (fo == null)
                                {
                                    StringBuilder msg = new StringBuilder();
                                    msg.append("Failed to connect to ");
                                    msg.append(aTitledURLEntry.getURL());
                                    msg.append("\n");
                                    msg.append(
                                        "Please check URL entry and try again.");
                                    JOptionPane.showMessageDialog(null, msg,
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                else
                                {
                                    chooser.setCurrentDirectoryObject(fo);
                                    parentDialog.setVisible(false);
                                }
                            }
                        };

                    worker.setPriority(Thread.MIN_PRIORITY);
                    SwingUtilities.invokeLater(worker);
                }
                else
                {
                    JOptionPane.showMessageDialog(getParent(),
                        VFSResources.getMessage(
                            "VFSJFileChooser.noselectionLabel"),
                        VFSResources.getMessage(
                            "VFSJFileChooser.errorLabel"),
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (button.equals(bDelete))
            {
                int[] rows = table.getSelectedRows();

                if (rows.length == 0)
                {
                    JOptionPane.showMessageDialog(getParent(),
                        VFSResources.getMessage(
                            "VFSJFileChooser.noselectionLabel"),
                        VFSResources.getMessage(
                            "VFSJFileChooser.errorLabel"),
                        JOptionPane.ERROR_MESSAGE);

                    return;
                }
                else
                {
                    for (int i = rows.length - 1; i >= 0; i--)
                    {
                        model.delete(rows[i]);
                    }
                }
            }
            else if (button.equals(bMoveUp))
            {
                int[] rows = table.getSelectedRows();

                if (rows.length == 0)
                {
                    JOptionPane.showMessageDialog(getParent(),
                        VFSResources.getMessage(
                            "VFSJFileChooser.noselectionLabel"),
                        VFSResources.getMessage(
                            "VFSJFileChooser.errorLabel"),
                        JOptionPane.ERROR_MESSAGE);

                    return;
                }
                else if (rows.length > 1)
                {
                    return;
                }
                else if (rows[0] > 0)
                {
                    model.moveup(rows[0]);
                    table.setRowSelectionInterval(rows[0] - 1, rows[0] - 1);
                }
            }
            else if (button.equals(bMoveDown))
            {
                int[] rows = table.getSelectedRows();

                if (rows.length == 0)
                {
                    JOptionPane.showMessageDialog(getParent(),
                        VFSResources.getMessage(
                            "VFSJFileChooser.noselectionLabel"),
                        VFSResources.getMessage(
                            "VFSJFileChooser.errorLabel"),
                        JOptionPane.ERROR_MESSAGE);

                    return;
                }
                else if (rows.length > 1)
                {
                    return;
                }
                else if (rows[0] < (model.getRowCount() - 1))
                {
                    model.movedown(rows[0]);
                    table.setRowSelectionInterval(rows[0] + 1, rows[0] + 1);
                }
            }
            else if (button.equals(bCancel))
            {
                cancel();
            }
        }
    } // inner class ActionHandler
}
