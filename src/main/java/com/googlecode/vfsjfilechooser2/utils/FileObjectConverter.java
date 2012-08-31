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
 * FileObjectConverter.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package com.googlecode.vfsjfilechooser2.utils;

import java.io.File;

import org.apache.commons.vfs2.FileObject;

/**
 * Interface for classes converting {@link FileObject} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of file object that gets generated
 */
public interface FileObjectConverter<T extends File> {

  /**
   * Converts the {@link FileObject} object into a Java file object.
   * 
   * @param file	the object to convert
   * @return		the generated object or null if failed to convert
   */
  public T convertFileObject(FileObject file);
}
