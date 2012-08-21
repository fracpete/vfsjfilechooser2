/*
 *
 * Copyright (C) 2008-2009 Yves Zoundi
 * Copyright (C) 2008-2009 Stan Love
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
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
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
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.googlecode.vfsjfilechooser2.accessories.connection.Credentials;
import com.googlecode.vfsjfilechooser2.accessories.connection.Protocol;
import com.googlecode.vfsjfilechooser2.accessories.connection.Credentials.Builder;
import com.googlecode.vfsjfilechooser2.filechooser.PopupHandler;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;
import com.googlecode.vfsjfilechooser2.utils.VFSURIParser;
import com.googlecode.vfsjfilechooser2.utils.VFSURIValidator;


/**
 * The connection dialog
 * 
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @author Jojada Tirtowidjojo <jojada at users.sourceforge.net>
 * @author Stan Love
 * @version 0.0.4
 */
@SuppressWarnings("serial")
public final class BookmarksEditorPanel extends JPanel {
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel portLabel;
	private JLabel hostnameLabel;
	private JTextField bookmarkNameTextField;
	private JTextField hostnameTextField;
	private JTextField defaultRemotePathTextField;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private JFormattedTextField portTextField;
	private boolean isPortTextFieldDirty;
	private JComboBox protocolList;
	private JComponent buttonsPanel;
	private JButton okButton;
	private JButton cancelButton;
	private JComponent centerPanel;
	private Bookmarks bookmarks;
	private int editIndex = -1;
	private BookmarksDialog parentDialog;

	/**
	 * Default constructor
	 * 
	 * @param parentDialog
	 *            The bookmarks dialog
	 * @param bookmarks
	 *            The bookmarks
	 */
	public BookmarksEditorPanel(BookmarksDialog parentDialog,
			Bookmarks bookmarks) {
		this.parentDialog = parentDialog;
		this.bookmarks = bookmarks;
		setLayout(new BorderLayout());
		initComponents();
		initListeners();
	}

	/**
	 * Returns the bookmarks
	 * 
	 * @return the bookmarks
	 */
	public Bookmarks getBookmarks() {
		return this.bookmarks;
	}

	/**
	 * Update the panel contents
	 * 
	 * @param editIndex
	 *            Bookmark index to edit
	 */
	public void updateFieds(int editIndex) {
		resetFields();
		this.editIndex = editIndex;
		// Set port text field to dirty
		// so that default port number won't be assigned
		setPortTextFieldDirty(true);
		updateFields();
	}

	private void updateFields() {
		if (editIndex != -1) {
			TitledURLEntry tue = bookmarks.getEntry(editIndex);
			bookmarkNameTextField.setText(tue.getTitle());
		
		 	//sl start		
			VFSURIValidator v = new VFSURIValidator();
			if(! v.isValid(tue.getURL())){
				//popup a warning 
				JOptionPane.showMessageDialog(null,VFSResources.getMessage("VFSFileChooser.errBADURI"));
			}
		 	//sl stop		

			VFSURIParser parser = new VFSURIParser(tue.getURL(),
					!isPortTextFieldDirty());

			protocolList.setSelectedItem(parser.getProtocol());

			if (parser.getProtocol() != Protocol.FILE) {
				hostnameTextField.setText(parser.getHostname());

				if (parser.getPortnumber() != null
						&& parser.getPortnumber().length() > 0) {
					int test_port = -1;
					try{					//stan
						test_port = Integer.valueOf(parser.getPortnumber());
						portTextField.setValue(test_port);
					}
					catch (Exception ex){
						portTextField.setValue(null);
					}
					if( (test_port<0) || (test_port>65535)){                       //stan
						portTextField.setValue(null);
					}
				} else {
					portTextField.setValue(null);
				}

				if (parser.getUsername() != null) {
					usernameTextField.setText(parser.getUsername());
				}

				if (parser.getPassword() != null) {
					passwordTextField.setText(parser.getPassword());
				}
			}

			defaultRemotePathTextField.setText(parser.getPath());
		}
	}

	private void initComponents() {
		initCenterPanelComponents();
		initBottomPanelComponents();

		add(this.buttonsPanel, BorderLayout.SOUTH);
		add(this.centerPanel, BorderLayout.CENTER);
	}

	private void initCenterPanelComponents() {
		// create the panel
		this.centerPanel = new JPanel(new GridBagLayout());
		this.centerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		JLabel bookmarkNameLabel = new JLabel(VFSResources
				.getMessage("VFSJFileChooser.fileNameHeaderText"));
		this.bookmarkNameTextField = new JTextField(25);

		// create the components
		this.hostnameLabel = new JLabel(VFSResources
				.getMessage("VFSJFileChooser.hostnameLabelText"));
		this.hostnameLabel.setForeground(Color.RED);
		this.hostnameTextField = new JTextField(25);

		this.portLabel = new JLabel(VFSResources
				.getMessage("VFSJFileChooser.portLabelText"));
		this.portTextField = new JFormattedTextField(NumberFormat.getInstance());
		this.isPortTextFieldDirty = false;

		JLabel protocolLabel = new JLabel(VFSResources
				.getMessage("VFSJFileChooser.protocolLabelText"));
		DefaultComboBoxModel protocolModel = new DefaultComboBoxModel(Protocol
				.values());
		this.protocolList = new JComboBox(protocolModel);
		this.protocolList.setRenderer(new ProtocolRenderer());

		this.usernameLabel = new JLabel(VFSResources
				.getMessage("VFSJFileChooser.usernameLabelText"));
		this.usernameTextField = new JTextField(20);

		this.passwordLabel = new JLabel(VFSResources
				.getMessage("VFSJFileChooser.passwordLabelText"));
		this.passwordTextField = new JPasswordField(12);

		JLabel defaultRemotePathLabel = new JLabel(VFSResources
				.getMessage("VFSJFileChooser.pathLabelText"));
		this.defaultRemotePathTextField = new JTextField(20);

		// Add the components to the panel
		makeGridPanel(new Component[] { bookmarkNameLabel,
				bookmarkNameTextField, hostnameLabel, hostnameTextField,
				portLabel, portTextField, protocolLabel, protocolList,
				usernameLabel, usernameTextField, passwordLabel,
				passwordTextField, defaultRemotePathLabel,
				defaultRemotePathTextField });

		// add the usual right click popup menu(copy, paste, etc.)
		PopupHandler.installDefaultMouseListener(bookmarkNameTextField);
		PopupHandler.installDefaultMouseListener(hostnameTextField);
		PopupHandler.installDefaultMouseListener(portTextField);
		PopupHandler.installDefaultMouseListener(usernameTextField);
		PopupHandler.installDefaultMouseListener(passwordTextField);
		PopupHandler.installDefaultMouseListener(defaultRemotePathTextField);
	}

	private void makeGridPanel(Component[] components) {
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

		for (Component component : components) {
			gbc.gridx = i;
			gbc.gridy = j;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;

			centerPanel.add(component, gbc);

			i++;

			// 2 components per row
			if ((i % 2) == 0) {
				j++;
				i = 0;
			}
		}
	}

	private void initListeners() {
		this.portTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();

				if (!((Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))) {
					getToolkit().beep();
					e.consume();
				} else {
					setPortTextFieldDirty(true);
				}
			}
		});

		this.portTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				JFormattedTextField f = (JFormattedTextField) e.getSource();
				String text = f.getText();

				if (text.length() == 0) {
					f.setValue(null);
				}

				try {
					f.commitEdit();
				} catch (ParseException exc) {
				}
			}
		});

		this.cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentDialog.restoreDefaultView();
			}
		});

		this.okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String bookmarkName = bookmarkNameTextField.getText();

				StringBuilder errors = new StringBuilder();

				if ("".equals(bookmarkName.trim())) {
					errors
							.append(
									VFSResources
											.getMessage("VFSJFileChooser.errorBookmarknameRequired"))
							.append("\n");
				}

				String m_username = usernameTextField.getText();
				String m_defaultRemotePath = defaultRemotePathTextField
						.getText();
				if(!m_defaultRemotePath.startsWith("/")){              //stan -- make sure all paths are absolute
					m_defaultRemotePath="/"+m_defaultRemotePath;
				}
				char[] m_password = passwordTextField.getPassword();
				String m_hostname = hostnameTextField.getText();
				String m_protocol = protocolList.getSelectedItem().toString()
						.toLowerCase();

				if (!"file".equals(m_protocol)) {
					if ("".equals(m_hostname.trim())) {
						errors
								.append(
										VFSResources
												.getMessage("VFSJFileChooser.errorHostnameRequired"))
								.append("\n");
					}
				}

				if( (m_username.equals("")) && (!m_password.equals("")) ){
						errors
								.append(
										VFSResources
												.getMessage("VFSJFileChooser.errorUsernameRequired"))
								.append("\n");
				}

				if (errors.length() > 0) {
					JOptionPane.showMessageDialog(parentDialog, errors
							.toString(), VFSResources
							.getMessage("VFSJFileChooser.errorLabel"),
							JOptionPane.ERROR_MESSAGE);

					return;
				}

				int m_port = -1;

				if (portTextField.isEditValid()
						&& (portTextField.getValue() != null)) {
					String s = portTextField.getValue().toString();
					try{					//stan
						m_port = Integer.valueOf(s);
					}
					catch (Exception ex){
						m_port = -1;
						portTextField.setValue(null);
						s="";
					}
					if( (m_port<0) || (m_port>65535)){                       //stan
						m_port = -1;
						portTextField.setValue(null);
						s="";
					}
				}

				Builder credentialsBuilder = Credentials.newBuilder(m_hostname)
						.defaultRemotePath(m_defaultRemotePath).username(
								m_username).password(m_password).protocol(
								m_protocol).port(m_port);

				Credentials credentials = credentialsBuilder.build();

				String uri = credentials.toFileObjectURL();

		 		//sl start		
				VFSURIValidator v = new VFSURIValidator();
				if(! v.isValid(uri)){
					//popup a warning 
					JOptionPane.showMessageDialog(null,VFSResources.getMessage("VFSFileChooser.errBADURI"));
					//System.out.println("BookmarksEditorPanel -- bad uri="+uri+"=");
				}
				else {
					//System.out.println("BookmarksEditorPanel -- good uri="+uri+"=");
				}
		 		//sl stop		
				if (editIndex == -1) {
					bookmarks.add(new TitledURLEntry(bookmarkName, uri));
				} else {
					bookmarks.setValueAt(bookmarkName, editIndex, 0);
					bookmarks.setValueAt(uri, editIndex, 1);
				}

				bookmarks.save(); // sl

				parentDialog.restoreDefaultView();
			}
		});

		this.protocolList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					selectPortNumber();
				}
			}
		});

		this.protocolList.setSelectedItem(Protocol.FTP);
	}

	private void selectPortNumber() {
		// Go and get default port number according to the selected protocol
		Protocol protocol = (Protocol) protocolList.getSelectedItem();

		if (protocol.toString().equals("FILE")) {
			enableFields(false);

			this.isPortTextFieldDirty = false;

			return;
		} else {
			enableFields(true);
		}

		// if user types in a port number
		// or empties port number field
		// then do not set protocol's default port number
		if (isPortTextFieldDirty() && portTextField.isEditValid()) {
			return;
		}

		portTextField.setValue(protocol.getPort());
	}

	private void setPortTextFieldDirty(boolean b) {
		this.isPortTextFieldDirty = b;
	}

	private boolean isPortTextFieldDirty() {
		return this.isPortTextFieldDirty;
	}

	private void enableFields(boolean b) {
		Component[] components = { hostnameLabel, hostnameTextField,
				usernameLabel, usernameTextField, passwordLabel,
				passwordTextField, portLabel, portTextField };

		for (Component component : components) {
			component.setVisible(b);
		}
	}

	private void resetFields() {
		this.isPortTextFieldDirty = false;
		bookmarkNameTextField.setText("");
		hostnameTextField.setText("");
		protocolList.setSelectedItem(Protocol.FTP);
		usernameTextField.setText("");
		passwordTextField.setText("");
		defaultRemotePathTextField.setText("");
	}

	private void initBottomPanelComponents() {
		this.buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		this.cancelButton = new JButton(VFSResources
				.getMessage("VFSJFileChooser.cancelButtonText"));
		this.okButton = new JButton(VFSResources
				.getMessage("VFSJFileChooser.okButtonText"));

		this.buttonsPanel.add(this.okButton);
		this.buttonsPanel.add(this.cancelButton);
	}

	private static class ProtocolRenderer extends BasicComboBoxRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());

				if (-1 < index) {
					Protocol aProtocol = (Protocol) value;
					list.setToolTipText(aProtocol.getDescription());
				}
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());

			return this;
		}
	}
}
