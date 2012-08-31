/*
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

/**
 * DefaultFileObjectConverter.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package com.googlecode.vfsjfilechooser2.utils;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

import org.apache.commons.vfs2.FileObject;

/**
 * Converts {@link FileObject} objects into {@link File} ones.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultFileObjectConverter
  implements Serializable, FileObjectConverter<File> {

  /** for serialization. */
  private static final long serialVersionUID = 5113461292255671412L;

  /**
   * Converts the {@link FileObject} object into a Java file object.
   * 
   * @param file	the object to convert
   * @return		the generated object, or null if failed to convert
   */
  @Override
  public File convertFileObject(FileObject file) {
    if (file == null)
	return null;
    try {
      return new File(new URI(file.getName().getURI().replace(" ", "%20")));
    }
    catch (Exception e) {
      System.err.println("Failed to convert '" + file + "' into file!");
      e.printStackTrace();
      return null;
    }
  }

}
