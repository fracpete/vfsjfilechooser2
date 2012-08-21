/*
 * Some constants for the library
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
package com.googlecode.vfsjfilechooser2.constants;

import java.io.File;


/**
 * VFSJFileChooser constants
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public final class VFSJFileChooserConstants
{
    /**
     * The user directory
     */
    public static final File HOME_DIRECTORY = new File(System.getProperty(
                "user.home"));

    /**
     * The settings directory
     */
    public static final File CONFIG_DIRECTORY = new File(HOME_DIRECTORY,
            ".vfsjfilechooser");

    /**
     * The bookmarks file
     */
    public static final File BOOKMARKS_FILE = new File(CONFIG_DIRECTORY,
            "favorites.xml");

    /**
     *
     */
    public static final String uiClassID = "VFSFileChooserUI";

    /** Instruction to cancel the current selection. */
    public static final String CANCEL_SELECTION = "CancelSelection";

    /**
     * Instruction to approve the current selection
     * (same as pressing yes or ok).
     */
    public static final String APPROVE_SELECTION = "ApproveSelection";

    /** Identifies change in the text on the approve (yes, ok) button. */
    public static final String APPROVE_BUTTON_TEXT_CHANGED_PROPERTY = "ApproveButtonTextChangedProperty";

    /**
     * Identifies change in the tooltip text for the approve (yes, ok)
     * button.
     */
    public static final String APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY = "ApproveButtonToolTipTextChangedProperty";

    /** Identifies change in the mnemonic for the approve (yes, ok) button. */
    public static final String APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY = "ApproveButtonMnemonicChangedProperty";

    /** Instruction to display the control buttons. */
    public static final String CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY = "ControlButtonsAreShownChangedProperty";

    /** Identifies user's directory change. */
    public static final String DIRECTORY_CHANGED_PROPERTY = "directoryChanged";

    /** Identifies change in user's single-file selection. */
    public static final String SELECTED_FILE_CHANGED_PROPERTY = "SelectedFileChangedProperty";

    /** Identifies change in user's multiple-file selection. */
    public static final String SELECTED_FILES_CHANGED_PROPERTY = "SelectedFilesChangedProperty";

    /** Enables multiple-file selections. */
    public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";

    /**
     * Says that a different object is being used to find available drives
     * on the system.
     */
    public static final String FILE_SYSTEM_VIEW_CHANGED_PROPERTY = "FileSystemViewChanged";

    /**
     * Says that a different object is being used to retrieve file
     * information.
     */
    public static final String FILE_VIEW_CHANGED_PROPERTY = "fileViewChanged";

    /** Identifies a change in the display-hidden-files property. */
    public static final String FILE_HIDING_CHANGED_PROPERTY = "FileHidingChanged";

    /** User changed the kind of files to display. */
    public static final String FILE_FILTER_CHANGED_PROPERTY = "fileFilterChanged";

    /**
     * Identifies a change in the kind of selection (single,
     * multiple, etc.).
     */
    public static final String FILE_SELECTION_MODE_CHANGED_PROPERTY = "fileSelectionChanged";

    /**
     * Says that a different accessory component is in use
     * (for example, to preview files).
     */
    public static final String ACCESSORY_CHANGED_PROPERTY = "AccessoryChangedProperty";

    /**
     * Identifies whether a the AcceptAllFileFilter is used or not.
     */
    public static final String ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY = "acceptAllFileFilterUsedChanged";

    /** Identifies a change in the dialog title. */
    public static final String DIALOG_TITLE_CHANGED_PROPERTY = "DialogTitleChangedProperty";

    /**
     * Identifies a change in the type of files displayed (files only,
     * directories only, or both files and directories).
     */
    public static final String DIALOG_TYPE_CHANGED_PROPERTY = "DialogTypeChangedProperty";

    /**
     * Identifies a change in the list of predefined file filters
     * the user can choose from.
     */
    public static final String CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY = "ChoosableFileFilterChangedProperty";

    /**
     * Identifies hidden files
     */
    public static final String SHOW_HIDDEN_PROP = "awt.file.showHiddenFiles";
}
