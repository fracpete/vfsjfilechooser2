/**
 * 
 */
package com.googlecode.vfsjfilechooser2.demo;

import java.net.URI;
import java.net.URLEncoder;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.RETURN_TYPE;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.SELECTION_MODE;
import com.googlecode.vfsjfilechooser2.accessories.DefaultAccessoriesPanel;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

/**
 * @author f3thomas
 *
 * created 08.01.2021
 */

public class ExampleBrowserFTP {

	/**
	 * @param args the command-line args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Test 08.01.2021
		ExampleBrowserFTP ts=new ExampleBrowserFTP();
		ts.start();
	}
	// TODO Test 08.01.2021
	private void start() {
		
		String protocoll = "ftp";
		Integer port = 21;
		
		//change the remoteHost,password and username with your server credetials
		String remoteHost = "ftpserver.example.com";
		String password = "changeIT";
		String username = "changeIT";		
		
		//change this with your requested baseDirectory
		String path="/tomFolder";
	
		try {
			String userInfo = URLEncoder.encode(username,"UTF-8") + ":" + URLEncoder.encode(password,"UTF-8");
			URI uri=new URI(protocoll, userInfo, remoteHost, port,path,null,null);
			// create a file chooser
//			VFS.setUriStyle(false);
			FileSystemManager manager =VFS.getManager();
			FileSystemOptions options= new FileSystemOptions();
			FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
			FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, true);
			FileObject remote = manager.resolveFile(uri.toString(),options);
			FileObject[] children = remote.getChildren();
			VFSJFileChooser fileChooser = new VFSJFileChooser(remote);
			fileChooser.setAccessory(new DefaultAccessoriesPanel(fileChooser));
			fileChooser.setFileHidingEnabled(false);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);
//			fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_AND_DIRECTORIES);
			// show the file dialog
			RETURN_TYPE answer = fileChooser.showOpenDialog(null);
			
			// check if a file was selected
			if (answer == RETURN_TYPE.APPROVE)
			{
				final FileObject aFileObject = fileChooser.getSelectedFileObject();
				
				// remove authentication credentials from the file path
				final String safeName = VFSUtils.getFriendlyName(aFileObject.toString());
				
				System.out.printf("%s %s", "You selected:", safeName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
