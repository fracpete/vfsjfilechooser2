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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.StringWriter;

import java.util.Iterator;
import java.util.List;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Utility class to save bookmarks
 * 
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @author Stan Love
 * @version 0.0.2
 */
final class BookmarksWriter {
	private Writer writer;

	public BookmarksWriter() {
	}

	private void startAttribute(String name, String value) throws IOException {
		writer.write(" ");
		writer.write(name);
		writer.write(" =");
		writer.write("\"");
		writer.write(value);
		writer.write("\"");
	}

	private void startTag(String name) throws IOException {
		writer.write("<" + name + ">");
	}

	private void startNewLine() throws IOException {
		writer.write("\n");
	}

	private void endTag(String tagName) throws IOException {
		writer.write("</" + tagName + ">");
	}

	private void writeData(List<TitledURLEntry> entries)
			throws java.io.IOException {
		startTag("entries");

		Iterator<TitledURLEntry> it = entries.iterator();

		while (it.hasNext()) {
			TitledURLEntry entry = it.next();

			if ((entry == null)
					|| ((entry.getTitle() == null) || (entry.getTitle()
							.length() == 0))) {
				it.remove();
			}

			startNewLine();
			writer.write("<entry");
			startAttribute("title", entry.getTitle());
			startAttribute("url", entry.getURL());
			writer.write("/>");
		}

		startNewLine();
		endTag("entries");
	}

	public void writeToFile(List<TitledURLEntry> entries, File bookmarksFile)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		if ((entries == null) || (bookmarksFile == null)) {
			throw new NullPointerException();
		}

		// String write_type=""; //allow multiple encryption options
		String write_type = "b1"; // allow multiple encryption options
		if (write_type.equals("")) {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(bookmarksFile), "UTF-8"));

			writeData(entries);

			writer.flush();
			writer.close();
		}// if(write_type.equals(""))
		else if (write_type.equals("b1")) {
			// writer = new BufferedWriter(new OutputStreamWriter(
			// new FileOutputStream(bookmarksFile), "UTF-8"));
			writer = (new StringWriter());

			writeData(entries);

			// get the bytes so we can do the encryption

			byte[] out = writer.toString().getBytes();

			// do the encryption

			byte[] raw = new byte[16];
			raw[0] = (byte) 1;
			raw[2] = (byte) 23;
			raw[3] = (byte) 24;
			raw[4] = (byte) 2;
			raw[5] = (byte) 99;
			raw[6] = (byte) 200;
			raw[7] = (byte) 202;
			raw[8] = (byte) 209;
			raw[9] = (byte) 199;
			raw[10] = (byte) 181;
			raw[11] = (byte) 255;
			raw[12] = (byte) 33;
			raw[13] = (byte) 210;
			raw[14] = (byte) 214;
			raw[15] = (byte) 216;

			SecretKeySpec skeyspec = new SecretKeySpec(raw, "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			byte[] encrypted = cipher.doFinal(out);

			// write out results
			BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(bookmarksFile), "UTF-8"));
			writer2.write("b1");
			// writer2.write(out);
			writer2.write(Util.byteArraytoHexString(encrypted));
			writer2.flush();
			writer2.close();
		}// if(write_type.equals("b1"))
		else {
			System.out
					.println("FATAL ERROR -- BookmarksWriter.java  unknown write style");
			System.exit(10);
		}
	}
}
