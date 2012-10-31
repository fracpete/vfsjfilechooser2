/*
 * A helper class to deal with commons-vfs file abstractions
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
package com.googlecode.vfsjfilechooser2.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

/**
 * A helper class to deal with commons-vfs file abstractions
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @author Jojada Tirtowidjojo <jojada at users.sourceforge.net> 
 * @author Stephan Schuster <stephanschuster at users.sourceforge.net>
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version 0.0.5
 */
public final class VFSUtils
{
    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    // private static members
    private static FileSystemManager fileSystemManager;
    private static FileSystemOptions opts = new FileSystemOptions();
    private static final String OS_NAME = System.getProperty("os.name")
                                                .toLowerCase();
    private static final String PROTO_PREFIX = "://";
    private static final String FILE_PREFIX = OS_NAME.startsWith("windows")
        ? "file:///" : "file://";
    private static final int FILE_PREFIX_LEN = FILE_PREFIX.length();
    private static ReadWriteLock aLock = new ReentrantReadWriteLock(true);

    // File size localized strings
    private static final String kiloByteString = VFSResources.getMessage(
            "VFSJFileChooser.fileSizeKiloBytes");
    private static final String megaByteString = VFSResources.getMessage(
            "VFSJFileChooser.fileSizeMegaBytes");
    private static final String gigaByteString = VFSResources.getMessage(
            "VFSJFileChooser.fileSizeGigaBytes");

    // prevent unnecessary calls
    private VFSUtils()
    {
        throw new AssertionError("Trying to create a VFSUtils object");
    }

    public static void setFileSystemOptions(FileSystemOptions fileSystemOptions)
    {
        aLock.writeLock().lock();

        try
        {
            opts = fileSystemOptions;
        }
        finally
        {
            aLock.writeLock().unlock();
        }
    }

    /**
     * Returns the global filesystem manager
     * @return the global filesystem manager
     */
    public static FileSystemManager getFileSystemManager()
    {
        aLock.readLock().lock();

        try
        {
            if (fileSystemManager == null)
            {
                try
                {
                    StandardFileSystemManager fm = new StandardFileSystemManager();
                    fm.setCacheStrategy(CacheStrategy.MANUAL);
                    fm.init();
                    fileSystemManager = fm;
                }
                catch (Exception exc)
                {
                    throw new RuntimeException(exc);
                }
            }

            return fileSystemManager;
        }
        finally
        {
            aLock.readLock().unlock();
        }
    }

    /**
     * Sets the global filesystem manager
     * @param aFileSystemManager the global filesystem manager
     */
    public static void setFileSystemManager(
        FileSystemManager aFileSystemManager)
    {
        aLock.writeLock().lock();

        try
        {
            fileSystemManager = aFileSystemManager;
        }
        finally
        {
            aLock.writeLock().unlock();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a human-readable version of the file size, where the input
     * represents a specific number of bytes.
     *
     * @param size  the number of bytes
     * @return a human-readable display value (includes units)
     */
    public static String byteCountToDisplaySize(long size)
    {
        if ((size / ONE_GB) > 0)
        {
            //            displaySize = String.valueOf(size / ONE_GB) + " GB";
            return MessageFormat.format(gigaByteString,
                String.valueOf(size / ONE_GB));
        }
        else if ((size / ONE_MB) > 0)
        {
            //            displaySize = String.valueOf(size / ONE_MB) + " MB";
            return MessageFormat.format(megaByteString,
                String.valueOf(size / ONE_MB));
        }
        else if ((size / ONE_KB) > 0)
        {
            return MessageFormat.format(kiloByteString,
                String.valueOf(size / ONE_KB));

            //String.valueOf(size / ONE_KB) + " KB";
        }
        else
        {
            return String.valueOf(size);
        }
    }

    /**
     * Returns a buffered input stream from a file
     * @param fileObject A file object
     * @return an InputStream from the file object
     * @throws FileSystemException An exception while getting the file
     */
    public static InputStream getInputStream(FileObject fileObject)
        throws FileSystemException
    {
        return new BufferedInputStream(fileObject.getContent().getInputStream());
    }

    /**
     * Returns a buffered output stream from a file
     * @param fileObject A file object
     * @return an OutputStream from the file object
     * @throws FileSystemException An exception while getting the file
     */
    public static OutputStream getOutputStream(FileObject fileObject)
        throws FileSystemException
    {
        return new BufferedOutputStream(fileObject.getContent().getOutputStream());
    }

    /**
     * Tells whether a file is writable
     * @param fileObject
     * @return whether a file is writable
     */
    public static boolean canWrite(FileObject fileObject)
    {
        try
        {
            return fileObject.isWriteable();
        }
        catch (FileSystemException ex)
        {
            return false;
        }
    }

    /**
     * Returns a file representation
     * @param filePath The file path
     * @return a file representation
     */
    public static FileObject createFileObject(String filePath)
    {
        try
        {
            return getFileSystemManager().resolveFile(filePath, opts);
        }
        catch (FileSystemException ex)
        {
            return null;
        }
    }

    /**
     * Remove user credentials information
     * @param fileName The file name
     * @return The "safe" display name without username and password information
     */
    public static final String getFriendlyName(String fileName)
    {
    	return getFriendlyName(fileName, true);
    }
    
    public static final String getFriendlyName(String fileName, boolean excludeLocalFilePrefix)
    {
        StringBuilder filePath = new StringBuilder();

        int pos = fileName.lastIndexOf('@');

        if (pos == -1)
        {
            filePath.append(fileName);
        }
        else
        {
            int pos2 = fileName.indexOf(PROTO_PREFIX);

            if (pos2 == -1)
            {
                filePath.append(fileName);
            }
            else
            {
                String protocol = fileName.substring(0, pos2);

                filePath.append(protocol).append(PROTO_PREFIX)
                        .append(fileName.substring(pos + 1, fileName.length()));
            }
        }

        String returnedString = filePath.toString();

        if (excludeLocalFilePrefix && returnedString.startsWith(FILE_PREFIX))
        {
            return filePath.substring(FILE_PREFIX_LEN);
        }

        return returnedString;
    }

    /**
     * Returns the root filesystem of a given file
     * @param fileObject A file
     * @return the root filesystem of a given file
     */
    public static FileObject createFileSystemRoot(FileObject fileObject)
    {
        try
        {
            return fileObject.getFileSystem().getRoot();
        }
        catch (FileSystemException ex)
        {
            return null;
        }
    }

    /**
     * Returns all the files of a folder
     * @param folder A folder
     * @return the files of a folder
     */
    public static FileObject[] getFiles(FileObject folder)
    {
        try
        {
            return folder.getChildren();
        }
        catch (FileSystemException ex)
        {
            return new FileObject[0];
        }
    }

    /**
     * Return a folder's files
     * @param folder A folder abstraction
     * @param useFileHiding flag to include hidden files
     * @return a folder's files
     */
    public static FileObject[] getFiles(FileObject folder, boolean useFileHiding)
    {
        final FileObject[] fileList = getFiles(folder);

        if (useFileHiding)
        {
            return fileList;
        }
        else
        {
            List<FileObject> files = new ArrayList<FileObject>(fileList.length);

            // iterate over the file list
            for (FileObject file : fileList)
            {
                if (!isHiddenFile(file))
                {
                    files.add(file); // add the file
                }
            }

            return files.toArray(new FileObject[files.size()]);
        }
    }

    /**
     * Returns the root file system of a file representation
     * @param fileObject A file abstraction
     * @return the root file system of a file representation
     */
    public static FileObject getRootFileSystem(FileObject fileObject)
    {
        try
        {
            if ((fileObject == null) || !fileObject.exists())
            {
                return null;
            }

            return fileObject.getFileSystem().getRoot();
        }
        catch (FileSystemException ex)
        {
            return null;
        }
    }

    /**
     * Tells whether a file is hidden
     * @param fileObject a file representation
     * @return whether a file is hidden
     */
    public static boolean isHiddenFile(FileObject fileObject)
    {
        try
        {
            return fileObject.getName().getBaseName().charAt(0) == '.';
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * Tells whether a file is the root file system
     * @param fileObject A file representation
     * @return whether a file is the root file system
     */
    public static boolean isRoot(FileObject fileObject)
    {
        try
        {
            return fileObject.getParent() == null;
        }
        catch (FileSystemException ex)
        {
            return false;
        }
    }

    /**
     * Returns a file representation of a local file
     * @param file a local file
     * @return a file representation of a local file
     */
    public static FileObject toFileObject(File file)
    {
        try
        {
            return getFileSystemManager().toFileObject(file);
        }
        catch (FileSystemException ex)
        {
            return null;
        }
    }

    /**
     * Returns the parent directory of a file object
     * @param fileObject A file representation
     * @return the parent directory of a file object
     */
    public static FileObject getParentDirectory(FileObject fileObject)
    {
        if (fileObject == null)
            return fileObject;
        
        try
        {
            return fileObject.getParent();
        }
        catch (FileSystemException ex)
        {
            return fileObject;
        }
    }

    /**
     * Returns a file representation
     * @param filePath The file path
     * @return a file representation
     */
    public static FileObject resolveFileObject(String filePath)
    {
        try
        {
            if (filePath.startsWith("sftp://"))
            {
                SftpFileSystemConfigBuilder.getInstance()
                                           .setStrictHostKeyChecking(opts, "no");
            }

            return getFileSystemManager().resolveFile(filePath, opts);
        }
        catch (FileSystemException ex)
        {
            return null;
        }
    }

    /**
     * Returns a file representation
     * @param filePath The file path
     * @param options The filesystem options
     * @return a file representation
     */
    public static FileObject resolveFileObject(String filePath,
        FileSystemOptions options)
    {
        try
        {
            return getFileSystemManager().resolveFile(filePath, options);
        }
        catch (FileSystemException fse)
        {
            return null;
        }
    }

    /**
     * Returns a file representation
     * @param folder A folder
     * @param filename A filename
     * @return a file contained in a given folder
     */
    public static FileObject resolveFileObject(FileObject folder,
        String filename)
    {
        try
        {
            return folder.resolveFile(filename);
        }
        catch (FileSystemException ex)
        {
            return null;
        }
    }

    /**
     * Tells whether a file exists
     * @param fileObject A file representation
     * @return whether a file exists
     */
    public static boolean exists(FileObject fileObject)
    {
        if (fileObject == null)
        {
            return false;
        }

        try
        {
            return fileObject.exists();
        }
        catch (FileSystemException ex)
        {
            return false;
        }
    }

    /**
     * Returns whether a file object is a directory
     * @param fileObject A file object representation
     * @return whether a file object is a directory
     */
    public static boolean isDirectory(FileObject fileObject)
    {
        try
        {
            return fileObject.getType().equals(FileType.FOLDER);
        }
        catch (FileSystemException ex)
        {
            return false;
        }
    }

    /**
     * Tells whether a folder is the root filesystem
     * @param folder A folder
     * @return whether a folder is the root filesystem
     */
    public static boolean isFileSystemRoot(FileObject folder)
    {
        return isRoot(folder);
    }

    /**
     * Returns whether a folder contains a given file
     * @param folder A folder
     * @param file A file
     * @return whether a folder contains a given file
     */
    public static boolean isParent(FileObject folder, FileObject file)
    {
        try
        {
            FileObject parent = file.getParent();

            if (parent == null)
            {
                return false;
            }

            return parent.equals(folder);
        }
        catch (FileSystemException ex)
        {
            return false;
        }
    }
}
