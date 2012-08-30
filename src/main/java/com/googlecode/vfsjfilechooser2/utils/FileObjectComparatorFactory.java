/*
 * File comparators factory
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
package com.googlecode.vfsjfilechooser2.utils;

import java.util.Comparator;

import org.apache.commons.vfs2.FileObject;


/**
 *
 * File comparators factory
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 *
 */
public final class FileObjectComparatorFactory
{
    private FileObjectComparatorFactory()
    {
        throw new AssertionError(
            "Trying to instanciate FileObjectComparatorFactory");
    }

    /**
     * Return a new filename comparator
     * @param isSortAsc ascendant sorting
     * @return a new comparator
     */
    public static Comparator<FileObject> newFileNameComparator(
        boolean isSortAsc)
    {
        return new DirectoriesFirstComparatorWrapper(new FileNameComparator(
                isSortAsc));
    }

    /**
     * Return a new size comparator
     * @param isSortAsc ascendant sorting
     * @return a new comparator
     */
    public static Comparator<FileObject> newSizeComparator(boolean isSortAsc)
    {
        return new DirectoriesFirstComparatorWrapper(new SizeComparator(
                isSortAsc));
    }

    /**
     * Return a new date comparator
     * @param isSortAsc ascendant sorting
     * @return a new comparator
     */
    public static Comparator<FileObject> newDateComparator(boolean isSortAsc)
    {
        return new DirectoriesFirstComparatorWrapper(new DateComparator(
                isSortAsc));
    }

    private static class FileNameComparator implements Comparator<FileObject>
    {
        private boolean isSortAsc = true;

        FileNameComparator(boolean isSortAsc)
        {
            this.isSortAsc = isSortAsc;
        }

        public int compare(FileObject a, FileObject b)
        {
            try
            {
                int result = a.getName().toString().toLowerCase()
                              .compareTo(b.getName().toString().toLowerCase());

                if (!isSortAsc)
                {
                    result = -result;
                }

                return result;
            }
            catch (Exception err)
            {
                return -1;
            }
        }
    }

    /**
     * This class sorts directories before files, comparing directory to
     * directory and file to file using the wrapped comparator.
     */
    private static class DirectoriesFirstComparatorWrapper implements Comparator<FileObject>
    {
        private Comparator<FileObject> delegate;

        public DirectoriesFirstComparatorWrapper(
            Comparator<FileObject> comparator)
        {
            this.delegate = comparator;
        }

        public int compare(FileObject f1, FileObject f2)
        {
            if ((f1 != null) && (f2 != null))
            {
                boolean traversable1 = VFSUtils.isDirectory(f1);
                boolean traversable2 = VFSUtils.isDirectory(f2);

                // directories go first
                if (traversable1 && !traversable2)
                {
                    return -1;
                }

                if (!traversable1 && traversable2)
                {
                    return 1;
                }
            }

            return delegate.compare(f1, f2);
        }
    }

    private static class SizeComparator implements Comparator<FileObject>
    {
        private boolean isSortAsc = true;

        SizeComparator(boolean isSortAsc)
        {
            this.isSortAsc = isSortAsc;
        }

        public int compare(FileObject a, FileObject b)
        {
            try
            {
                int result = new Long(a.getContent().getSize()).compareTo(new Long(b.getContent().getSize()));

                if (!isSortAsc)
                {
                    result = -result;
                }

                return result;
            }
            catch (Exception err)
            {
                return -1;
            }
        }
    }

    private static class DateComparator implements Comparator<FileObject>
    {
        private boolean isSortAsc = true;

        DateComparator(boolean isSortAsc)
        {
            this.isSortAsc = isSortAsc;
        }

        public int compare(FileObject a, FileObject b)
        {
            try
            {
                int result = new Long(a.getContent().getLastModifiedTime()).compareTo(new Long(b.getContent().getLastModifiedTime()));

                if (!isSortAsc)
                {
                    result = -result;
                }

                return result;
            }
            catch (Exception err)
            {
                return -1;
            }
        }
    }
}
