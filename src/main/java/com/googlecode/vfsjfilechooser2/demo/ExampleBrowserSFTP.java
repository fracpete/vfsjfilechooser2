/**
 * 
 */
package com.googlecode.vfsjfilechooser2.demo;

import java.net.URI;
import java.net.URLEncoder;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.RETURN_TYPE;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.SELECTION_MODE;
import com.googlecode.vfsjfilechooser2.utils.VFSUtils;

/**
 * @author f3thomas
 *
 * created 08.01.2021
 */

public class ExampleBrowserSFTP {

	/**
	 * @param args the command-line args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Test 08.01.2021
		ExampleBrowserSFTP ts=new ExampleBrowserSFTP();
		ts.start();
	}
	// TODO Test 08.01.2021
	private void start() {

		String protocoll = "sftp";
		Integer port = 22;
		
		
		//change the remoteHost,password and username with your server credetials
		String remoteHost = "sftpserver.example.com";
		String password = "changeIT";
		String username = "changeIT";	
		
		
		//change this with your requested baseDirectory
		String path="/tomtest";
	
		try {
			String userInfo = URLEncoder.encode(username,"UTF-8") + ":" + URLEncoder.encode(password,"UTF-8");
//			String userInfo =URLEncoder.encode(passwd,"UTF-8") + ":" + URLEncoder.encode(user,"UTF-8") ;
			URI uri=new URI(protocoll, userInfo, remoteHost, port,path,null,null);
			// create a file chooser
			FileSystemManager manager = VFS.getManager();
			FileObject remote = manager.resolveFile(uri.toString());
			System.out.println("remoteExists:" + VFSUtils.exists(remote));
			VFSJFileChooser fileChooser = new VFSJFileChooser();
		    fileChooser.setCurrentDirectoryObject(remote);
		    
			// configure the file dialog
//			fileChooser.setAccessory(new DefaultAccessoriesPanel(fileChooser));
			fileChooser.setFileHidingEnabled(false);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);
//			fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_AND_DIRECTORIES);
			fileChooser.setCurrentDirectoryObject(remote);			
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
