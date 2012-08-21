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
package com.googlecode.vfsjfilechooser2.accessories.connection;


import org.apache.commons.vfs2.FileObject;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.accessories.bookmarks.BookmarksDialog;
import com.googlecode.vfsjfilechooser2.accessories.bookmarks.TitledURLEntry;
import com.googlecode.vfsjfilechooser2.accessories.connection.Credentials.Builder;
import com.googlecode.vfsjfilechooser2.filechooser.PopupHandler;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;


/**
 * The connection dialog
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @author Jojada Tirtowidjojo <jojada at users.sourceforge.net>
 * @version 0.0.1
 */
@SuppressWarnings("serial")
public final class ConnectionDialog extends JDialog
{
    private static final String DIALOG_TITLE = VFSResources.getMessage(
            "VFSJFileChooser.connectionButtonText");
    private JLabel usernameLabel;
    private JLabel defaultRemotePathLabel;
    private JLabel passwordLabel;
    private JLabel protocolLabel;
    private JLabel portLabel;
    private JLabel hostnameLabel;
    private JTextField hostnameTextField;
    private JTextField defaultRemotePathTextField;
    private JTextField usernameTextField;
    private JPasswordField passwordTextField;
    private JFormattedTextField portTextField;
    private boolean isPortTextFieldDirty;
    private JComboBox protocolList;
    private JComponent buttonsPanel;
    private JButton connectButton;
    private DefaultComboBoxModel protocolModel;
    private JButton cancelButton;
    private JComponent centerPanel;
    private VFSJFileChooser fileChooser;
    private BookmarksDialog bookmarksDialog;
    private Thread currentWorker;

    /**
     * @param parent
     * @param m_dialog
     * @param chooser
     */
    public ConnectionDialog(Frame parent, BookmarksDialog m_dialog,
        VFSJFileChooser chooser)
    {
        super(parent, DIALOG_TITLE, true);

        this.fileChooser = chooser;
        this.bookmarksDialog = m_dialog;

        initComponents();
        initListeners();
    }

    private void initComponents()
    {
        initCenterPanelComponents();
        initBottomPanelComponents();

        getContentPane().add(this.buttonsPanel, BorderLayout.SOUTH);
        getContentPane().add(this.centerPanel, BorderLayout.CENTER);

        pack();
    }

    private void initCenterPanelComponents()
    {
        // create the panel
        this.centerPanel = new JPanel(new GridBagLayout());
        this.centerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        // create the components
        this.hostnameLabel = new JLabel(VFSResources.getMessage(
                    "VFSJFileChooser.hostnameLabelText"));
        this.hostnameLabel.setForeground(Color.RED);
        this.hostnameTextField = new JTextField(25);

        this.portLabel = new JLabel(VFSResources.getMessage(
                    "VFSJFileChooser.portLabelText"));
        this.portTextField = new JFormattedTextField(NumberFormat.getInstance());
        this.isPortTextFieldDirty = false;

        this.protocolLabel = new JLabel(VFSResources.getMessage(
                    "VFSJFileChooser.protocolLabelText"));
        this.protocolModel = new DefaultComboBoxModel(Protocol.values());
        this.protocolList = new JComboBox(protocolModel);
        this.protocolList.setRenderer(new ProtocolRenderer());

        this.usernameLabel = new JLabel(VFSResources.getMessage(
                    "VFSJFileChooser.usernameLabelText"));
        this.usernameTextField = new JTextField(20);

        this.passwordLabel = new JLabel(VFSResources.getMessage(
                    "VFSJFileChooser.passwordLabelText"));
        this.passwordTextField = new JPasswordField(12);

        this.defaultRemotePathLabel = new JLabel(VFSResources.getMessage(
                    "VFSJFileChooser.pathLabelText"));
        this.defaultRemotePathTextField = new JTextField(20);

        // Add the components to the panel
        makeGridPanel(new Component[]
            {
                hostnameLabel, hostnameTextField, portLabel, portTextField,
                protocolLabel, protocolList, usernameLabel, usernameTextField,
                passwordLabel, passwordTextField, defaultRemotePathLabel,
                defaultRemotePathTextField
            });
    }

    private void makeGridPanel(Component[] components)
    {
        final Insets insets = new Insets(5, 5, 5, 5);
        final GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = insets;
        gbc.ipadx = 0;
        gbc.ipady = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int i = 0;
        int j = 0;

        for (Component component : components)
        {
            gbc.gridx = i;
            gbc.gridy = j;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;

            centerPanel.add(component, gbc);

            i++;

            // 2 components per row
            if ((i % 2) == 0)
            {
                j++;
                i = 0;
            }
        }
    }

    private void initListeners()
    {
        this.portTextField.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyTyped(KeyEvent e)
                {
                    char c = e.getKeyChar();

                    if (!((Character.isDigit(c) ||
                            (c == KeyEvent.VK_BACK_SPACE) ||
                            (c == KeyEvent.VK_DELETE))))
                    {
                        getToolkit().beep();
                        e.consume();
                    }
                    else
                    {
                        setPortTextFieldDirty(true);
                    }
                }
            });

        this.portTextField.addFocusListener(new FocusAdapter()
            {
                @Override
                public void focusLost(FocusEvent e)
                {
                    JFormattedTextField f = (JFormattedTextField) e.getSource();
                    String text = f.getText();

                    if (text.length() == 0)
                    {
                        f.setValue(null);
                    }

                    try
                    {
                        f.commitEdit();
                    }
                    catch (ParseException exc)
                    {
                    }
                }
            });

        this.cancelButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (currentWorker != null)
                    {
                        if (currentWorker.isAlive())
                        {
                            currentWorker.interrupt();
                            setCursor(Cursor.getDefaultCursor());
                        }
                    }

                    setVisible(false);
                }
            });

        this.connectButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    currentWorker = new Thread()
                            {
                                @Override
                                public void run()
                                {
                                    StringBuilder error = new StringBuilder();
                                    FileObject fo = null;

                                    setCursor(Cursor.getPredefinedCursor(
                                            Cursor.WAIT_CURSOR));

                                    try
                                    {
                                        String m_username = usernameTextField.getText();
                                        String m_defaultRemotePath = defaultRemotePathTextField.getText();
                                        char[] m_password = passwordTextField.getPassword();
                                        String m_hostname = hostnameTextField.getText();
                                        String m_protocol = protocolList.getSelectedItem()
                                                                        .toString();

                                        int m_port = -1;

                                        if (portTextField.isEditValid() &&
                                                (portTextField.getValue() != null))
                                        {
                                            String s = portTextField.getValue()
                                                                    .toString();
                                            m_port = Integer.valueOf(s);
                                        }

                                        Builder credentialsBuilder = Credentials.newBuilder(m_hostname)
                                                                                .defaultRemotePath(m_defaultRemotePath)
                                                                                .username(m_username)
                                                                                .password(m_password)
                                                                                .protocol(m_protocol)
                                                                                .port(m_port);

                                        Credentials credentials = credentialsBuilder.build();

                                        String uri = credentials.toFileObjectURL();

                                        if (isInterrupted())
                                        {
                                            setPortTextFieldDirty(false);

                                            return;
                                        }

                                        fo = VFSUtils.resolveFileObject(uri);

                                        if ((fo != null) && !fo.exists())
                                        {
                                            fo = null;
                                        }
                                    }
                                    catch (Exception err)
                                    {
                                        error.append(err.getMessage());
                                        setCursor(Cursor.getDefaultCursor());
                                    }

                                    if ((error.length() > 0) || (fo == null))
                                    {
                                        error.delete(0, error.length());
                                        error.append("Failed to connect!");
                                        error.append("\n");
                                        error.append(
                                            "Please check parameters and try again.");

                                        JOptionPane.showMessageDialog(ConnectionDialog.this,
                                            error, "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                        setCursor(Cursor.getDefaultCursor());

                                        return;
                                    }

                                    if (isInterrupted())
                                    {
                                        return;
                                    }

                                    fileChooser.setCurrentDirectoryObject(fo);

                                    setCursor(Cursor.getDefaultCursor());

                                    resetFields();

                                    if (bookmarksDialog != null)
                                    {
                                        String bTitle = fo.getName()
                                                          .getBaseName();

                                        if (bTitle.trim().equals(""))
                                        {
                                            bTitle = fo.getName().toString();
                                        }

                                        String bURL = fo.getName().getURI();
                                        bookmarksDialog.getBookmarks()
                                                       .add(new TitledURLEntry(
                                                bTitle, bURL));
                                        bookmarksDialog.getBookmarks().save();
                                    }

                                    setVisible(false);
                                }
                            };

                    currentWorker.setPriority(Thread.MIN_PRIORITY);
                    currentWorker.start();
                }
            });

        // add the usual right click popup menu(copy, paste, etc.)
        PopupHandler.installDefaultMouseListener(hostnameTextField);
        PopupHandler.installDefaultMouseListener(portTextField);
        PopupHandler.installDefaultMouseListener(usernameTextField);
        PopupHandler.installDefaultMouseListener(passwordTextField);
        PopupHandler.installDefaultMouseListener(defaultRemotePathTextField);

        this.protocolList.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    if (e.getStateChange() == ItemEvent.SELECTED)
                    {
                        selectPortNumber();
                    }
                }
            });

        this.protocolList.setSelectedItem(Protocol.FTP);
    }

    private void selectPortNumber()
    {
        //Go and get default port number according to the selected protocol
        Protocol protocol = (Protocol) protocolList.getSelectedItem();

        if (protocol.toString().equals("FILE"))
        {
            enableFields(false);

            this.isPortTextFieldDirty = false;

            return;
        }
        else
        {
            enableFields(true);
        }

        //if user types in a port number
        //or empties port number field
        //then do not set protocol's default port number
        if (isPortTextFieldDirty() && portTextField.isEditValid())
        {
            return;
        }

        portTextField.setValue(protocol.getPort());
    }

    private void setPortTextFieldDirty(boolean b)
    {
        this.isPortTextFieldDirty = b;
    }

    private boolean isPortTextFieldDirty()
    {
        return this.isPortTextFieldDirty;
    }

    private void enableFields(boolean b)
    {
        Component[] components = 
            {
                hostnameLabel, hostnameTextField, usernameLabel,
                usernameTextField, passwordLabel, passwordTextField, portLabel,
                portTextField
            };

        for (Component component : components)
        {
            component.setVisible(b);
        }
    }

    private void resetFields()
    {
        this.isPortTextFieldDirty = false;
        hostnameTextField.setText("");
        protocolList.setSelectedItem(Protocol.FTP);
        usernameTextField.setText("");
        passwordTextField.setText("");
        defaultRemotePathTextField.setText("");
    }

    private void initBottomPanelComponents()
    {
        this.buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        this.cancelButton = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.cancelButtonText"));
        this.connectButton = new JButton(VFSResources.getMessage(
                    "VFSJFileChooser.connectionButtonText"));

        this.buttonsPanel.add(this.connectButton);
        this.buttonsPanel.add(this.cancelButton);
    }

    private static class ProtocolRenderer extends BasicComboBoxRenderer
    {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus)
        {
            if (isSelected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());

                if (-1 < index)
                {
                    Protocol aProtocol = (Protocol) value;
                    list.setToolTipText(aProtocol.getDescription());
                }
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());

            return this;
        }
    }
}
