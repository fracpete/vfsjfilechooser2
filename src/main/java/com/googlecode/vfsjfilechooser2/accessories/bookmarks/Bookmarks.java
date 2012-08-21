/*
 *
 * Copyright (C) 2005-2008 Yves Zoundi
 * Copyright (C) 2005-2008 Stan Love
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


import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;
import javax.swing.JOptionPane;

import com.googlecode.vfsjfilechooser2.constants.VFSJFileChooserConstants;
import com.googlecode.vfsjfilechooser2.utils.VFSResources;
import com.googlecode.vfsjfilechooser2.utils.VFSURIValidator;

/**
 * The bookmarks table model
 * 
 * @author Dirk Moebius (JEdit)
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @author Stan Love
 * @version 0.0.5
 */
public class Bookmarks extends AbstractTableModel {
	private static final long serialVersionUID = 6142063286592461932L;
	private static final String COLUMN_TITLE_NAME = VFSResources
			.getMessage("VFSJFileChooser.fileNameHeaderText");
	private static final String COLUMN_URL_NAME = VFSResources
			.getMessage("VFSJFileChooser.pathLabelText");
	private static final int COLUMN_TITLE_INDEX = 0;
	private static final int COLUMN_URL_INDEX = 1;
	private static final int NB_COLUMNS = 2;
	private final List<TitledURLEntry> entries = new ArrayList<TitledURLEntry>();
	private File favorites = VFSJFileChooserConstants.BOOKMARKS_FILE;
	private Logger logger = Logger.getLogger(Bookmarks.class.getName());
	private transient final BookmarksWriter writer = new BookmarksWriter();

	/**
	 * 
	 */
	public Bookmarks() {
		if (!VFSJFileChooserConstants.CONFIG_DIRECTORY.exists()) {
			if (!VFSJFileChooserConstants.CONFIG_DIRECTORY.mkdirs()) {
				logger.log(Level.SEVERE, "Unable to create config directory");
			}
		}

		List<TitledURLEntry> values = load();

		for (TitledURLEntry entry : values) {
			add(entry);
		}
	}

	/**
	 * @param e
	 */
	public void add(TitledURLEntry e) {
		synchronized (entries) {
			entries.add(e);
			fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
	 		//sl start		
			VFSURIValidator v = new VFSURIValidator();
			if(! v.isValid(e.getURL())){
				//popup a warning 
				JOptionPane.showMessageDialog(null,VFSResources.getMessage("VFSFileChooser.errBADURI"));
				//System.out.println("bad uri -- add");
			}
			else{
				//System.out.println("Good uri -- add");
			}
	 		//sl stop
			save(); // sl
		}
	}

	/**
	 * @return
	 */
	public int getSize() {
		return entries.size();
	}

	/**
	 * @param index
	 * @return
	 */
	public String getTitle(int index) {
		TitledURLEntry e = getEntry(index);

		return (e == null) ? null : e.getTitle();
	}

	/**
	 * @param index
	 * @return
	 */
	public String getURL(int index) {
		TitledURLEntry e = getEntry(index);

		return (e == null) ? null : e.getURL();
	}

	/**
	 * @param index
	 * @return
	 */
	public TitledURLEntry getEntry(int index) {
		if ((index < 0) || (index > entries.size())) {
			return null;
		}

		synchronized (entries) {
			TitledURLEntry e = entries.get(index);

			return e;
		}
	}

	/**
	 * @param row
	 */
	public void delete(int row) {
		synchronized (entries) {
			entries.remove(row);
			fireTableRowsDeleted(row, row);
			save(); // sl
		}
	}

	/**
	 * @param row
	 */
	public void moveup(int row) {
		if (row == 0) {
			return;
		}

		TitledURLEntry b = getEntry(row);

		synchronized (entries) {
			entries.remove(row);
			entries.add(row - 1, b);
	 		//sl start		
			VFSURIValidator v = new VFSURIValidator();
			if(! v.isValid(b.getURL())){
				//popup a warning 
				JOptionPane.showMessageDialog(null,VFSResources.getMessage("VFSFileChooser.errBADURI"));
				//System.out.println("moveup -- baduri");
			}
			else{
				//System.out.println("moveup -- good uri");
			}
	 		//sl stop
			save(); // sl
		}

		fireTableRowsUpdated(row - 1, row);
	}

	public void movedown(int row) {
		if (row == (entries.size() - 1)) {
			return;
		}

		TitledURLEntry b = getEntry(row);

		synchronized (entries) {
			entries.remove(row);
			entries.add(row + 1, b);
	 		//sl start		
			VFSURIValidator v = new VFSURIValidator();
			if(! v.isValid(b.getURL())){
				//popup a warning 
				JOptionPane.showMessageDialog(null,VFSResources.getMessage("VFSFileChooser.errBADURI"));
				//System.out.println("movedown -- bad uri");
			}
			else{
				//System.out.println("movedown -- good uri");
			}
	 		//sl stop
			save(); // sl
		}

		fireTableRowsUpdated(row, row + 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return NB_COLUMNS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return entries.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		Object obj = null;

		if (row < entries.size()) {
			if (col == 0) {
				obj = getEntry(row).getTitle();
			} else if (col == 1) {
				obj = getEntry(row).getURL();
			}
		}

		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 *      int, int)
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		TitledURLEntry e = getEntry(row);

		if (col == COLUMN_TITLE_INDEX) {
			e.setTitle(value.toString());
		} else if (col == COLUMN_URL_INDEX) {
			e.setURL(value.toString());
		}

		fireTableRowsUpdated(row, row);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int index) {
		return (index == COLUMN_TITLE_INDEX) ? COLUMN_TITLE_NAME
				: COLUMN_URL_NAME;
	}

	public List<TitledURLEntry> load() {
		try {
			if (favorites.exists()) {
				return new BookmarksReader(favorites).getParsedEntries();
			} else {
				writeDefaultFavorites();
			}
		} catch (Exception e) {
			logger.warning("Rebuilding bookmarks");
			logger.log(Level.WARNING, e.getMessage(), e);
			writeDefaultFavorites();
		}

		return new ArrayList<TitledURLEntry>();
	}

	private void writeDefaultFavorites() {
		try {
			writer.writeToFile(new ArrayList<TitledURLEntry>(0), favorites);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Unable to write bookmarks", ex);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to write bookmarks", e);
		}
	}

	// end AbstractTableModel implementation
	public void save() {
		try {
			writer.writeToFile(entries, favorites);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Unable to write bookmarks", ex);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to write bookmarks", e);
		}
	}
}
