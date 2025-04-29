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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Utility class to read bookmarks
 * 
 * @author Yves Zoundi (yveszoundi at users dot sf dot net)
 * @author Stan Love
 * @version 0.0.2
 */
final class BookmarksReader {
	private List<TitledURLEntry> entries;
	private Logger logger = Logger.getLogger(Bookmarks.class.getName());

	public BookmarksReader(File bookmarksFile) {
		entries = new ArrayList<TitledURLEntry>();
		Reader reader = null;
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(new BookmarksHandler());

			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(bookmarksFile), "UTF-8"));

			// read 1st 2 bytes to support multiple encryptions
			char[] code = new char[2];
			reader.read(code, 0, 2);
			logger.log(Level.FINEST, "code=" + String.valueOf(code) + "=");
			if ((code[0] == 'b') && (code[1] == '1')) {
				logger.log(Level.FINEST, "in encrypted code section");
				// read the encrypted file
				InputStream is = new FileInputStream(bookmarksFile);

				int the_length = (int) bookmarksFile.length() - 2;
				logger.log(Level.FINEST, "raw_length=" + (the_length + 2));
				if (the_length <= 0)
					the_length = 1;
				logger.log(Level.FINEST, "fixed_length=" + the_length);
				byte[] code2 = new byte[2];
				byte[] outhex = new byte[the_length];
				try {
					is.read(code2);
					is.read(outhex);
					// is.read(outhex,2,the_length);
					is.close();
				} catch (Exception e) {
					logger.log(Level.INFO, "exception reading encrypted file"
							+ e);
				}

				byte[] out = Util.hexByteArrayToByteArray(outhex);

				// do the decryption

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
				cipher.init(Cipher.DECRYPT_MODE, skeyspec);
				byte[] decrypted = cipher.doFinal(out);

				// convert decrypted into a bytestream and parse it
				ByteArrayInputStream bstream = new ByteArrayInputStream(
						decrypted);
				InputSource inputSource = new InputSource(bstream);
				xmlReader.parse(inputSource);
				logger.log(Level.FINEST, "leaving encrypted code section");
			} else {
				logger.log(Level.FINEST, "in decrypted code section");
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(bookmarksFile), "UTF-8"));
				InputSource inputSource = new InputSource(reader);
				xmlReader.parse(inputSource);
				logger.log(Level.FINEST, "leaving decrypted code section");
			}
		} catch (SAXParseException e) {
			StringBuilder sb = new StringBuilder();
			sb.append("Error parsing xml bookmarks file").append("\n").append(
					e.getLineNumber()).append(":").append(e.getColumnNumber())
					.append("\n").append(e.getMessage());
			throw new RuntimeException(sb.toString(), e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Bookmarks file doesn't exist!", e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally{
			if(reader != null){
				try{
					reader.close();
				}
				catch(IOException ioe){
					logger.log(Level.WARNING, "Unable to close bookmarks stream", ioe);
				}
			}
		}
	}

	public List<TitledURLEntry> getParsedEntries() {
		return entries;
	}

	private class BookmarksHandler implements ContentHandler {
		public void setDocumentLocator(Locator locator) {
		}

		public void startDocument() throws SAXException {
		}

		public void endDocument() throws SAXException {
		}

		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
		}

		public void endPrefixMapping(String prefix) throws SAXException {
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			if ("entry".equals(localName)) {
				TitledURLEntry tue = null;
				String title = atts.getValue("title");
				String url = atts.getValue("url");

				if ((title != null) && (url != null)) {
					tue = new TitledURLEntry(title, url);
					entries.add(tue);
				}
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
		}

		public void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
		}

		public void processingInstruction(String target, String data)
				throws SAXException {
		}

		public void skippedEntity(String name) throws SAXException {
		}
	}
}
