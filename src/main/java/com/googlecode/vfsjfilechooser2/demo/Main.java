/*
 * VFSJFileChooser demo
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
package com.googlecode.vfsjfilechooser2.demo;


import org.apache.commons.vfs2.FileObject;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.RETURN_TYPE;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.SELECTION_MODE;
import com.googlecode.vfsjfilechooser2.accessories.DefaultAccessoriesPanel;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;


/**
 * VFSJFileChooser demo
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
@SuppressWarnings("serial")
public final class Main extends JFrame implements Runnable
{
    // private members
    private VFSJFileChooser fileChooser;
    private JTextField filenameTextField;
    private JButton openButton;
    private String buttonText;

    /** Create a new instance of this class */
    public Main()
    {
        // create the frame with a default title
        super("VFSJFileChooser Demo");

        // add a window listener
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createGUI(); // build the GUI
    } // end constructor

    /**
     * Setup the GUI components
     */
    private void createGUI()
    {
        fileChooser = new VFSJFileChooser(); // create a file dialog

        // configure the file dialog
        fileChooser.setAccessory(new DefaultAccessoriesPanel(fileChooser));
        fileChooser.setFileHidingEnabled(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);

        // create the filename textbox
        filenameTextField = new JTextField(40);

        // lookup the localized string for the open button
        buttonText = VFSResources.getMessage(
                "VFSJFileChooser.directoryOpenButtonText");

        // the open button
        openButton = new JButton(new OpenAction(buttonText));

        // add the components to the frame
        getContentPane().add(openButton, BorderLayout.WEST);
        getContentPane().add(filenameTextField, BorderLayout.CENTER);

        // setup the components
        pack();
    } // end createGUI method

    /** display the frame and center it on the screen */
    public void run()
    {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        toFront();
    } // end run method

    /**
     * Entry point
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        // show the window in the event dispatching thread
        EventQueue.invokeLater(new Main());
    } // end main method

    /**
     * Class to open the file dialog
     * @author Yves Zoundi <yveszoundi at users dot sf dot net>
     * @version 0.0.1
     */
    private class OpenAction extends AbstractAction
    {
        /**
         * Create the action with a name
         * @param name The action's name
         */
        public OpenAction(String name)
        {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent actionEvent)
        {
            // show the file dialog
            RETURN_TYPE answer = fileChooser.showOpenDialog(Main.this);

            // check if a file was selected
            if (answer == RETURN_TYPE.APPROVE)
            {
                // retrieve the selected file
                final FileObject aFileObject = fileChooser.getSelectedFileObject();

                // remove authentication credentials from the file path
                final String safeName = VFSUtils.getFriendlyName(aFileObject.toString());

                // display the file path
                filenameTextField.setText(safeName);
            }
        }
    } // end class OpenAction
}
