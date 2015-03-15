# Instantiation #

The following example creates a file chooser component for selecting multiple files:

```
import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.SELECTION_MODE;
...
VFSJFileChooser fileChooser = new VFSJFileChooser();
fileChooser.setFileHidingEnabled(false);
fileChooser.setMultiSelectionEnabled(true);
fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);
```


# Selected files #

The following snippet retrieves the files that the user selected for opening:

```
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.RETURN_TYPE;
import java.io.File;
...
RETURN_TYPE answer = fileChooser.showOpenDialog(this);
if (RETURN_TYPE == RETURN_TYPE.APPROVE) {
  File[] files = fileChooser.getSelectedFiles();
  for (File file: files) {
    System.out.println(file);
  }
}
```


# File filters #

File filters have to be derived from the following abstract class:

```
com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileFilter
```

Here is an example implementation:

```
import com.googlecode.vfsjfilechooser2.filechooser.AbstractVFSFileFilter;
import org.apache.commons.vfs2.FileObject;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

public class TextFileFilter extends AbstractVFSFileFilter {
  public boolean accept(FileObject f) {
    if (VFSUtils.isDirectory(f))
      return false;
    return f.getName().getBaseName().toLowerCase().endsWith(".txt");
  }
  public String getDescription() {
    return "Text files (*.txt)";
  }
}
```