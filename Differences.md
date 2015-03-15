# method signatures #

In order to make the new file chooser fit easily into existing frameworks, replacing Java's JFileChooser, I introduced methods for getting/setting `java.io.File` objects. Methods that previously were used for getting/setting Apache's `org.apache.commons.vfs2.FileObject` objects, were renamed:

Current Directory:
  * get/setCurrentDirectory: `File`
  * get/setCurrentDirectoryObject: `FileObject`

Selected file(s):
  * get/setSelectedFile(s): `File/File[]`
  * get/setSelectedFileObject(s): `FileObject/FileObject[]`

# conversion to File objects #

Using `FileObject` objects was a bit cumbersome for everyday handling of local files, hence the introduction of the methods that return/accept File objects.
In order to be able to convert `FileObject` instance to `File`, I introduced the `FileObjectConverter<T extends File>` interface (package `com.googlecode.vfsjfilechooser2.utils`) and implemented `DefaultFileObjectConverter` to convert to simple File objects. The converter instance can be set using the `setFileObjectConverter` method (and retrieved using `getFileObjectConverter`).

**Note:** The conversion to `File` objects does **not** work for remote files, you will need to use the methods that return `FileObject` objects or implement your converter (and subclass `java.io.File`).