/**
 * 
 */
package com.googlecode.vfsjfilechooser2.demo;

import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.URI;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.commons.vfs2.provider.ftp.FtpFileObject;
import org.apache.commons.vfs2.provider.sftp.SftpFileProvider;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystem;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

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

public class TomBrowserSFTP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Test 08.01.2021
		TomBrowserSFTP ts=new TomBrowserSFTP();
		ts.start();
	}
	// TODO Test 08.01.2021
	private void start() {

		String protocoll = "sftp";
		Integer port = 22;
		
		String remoteHost = "myftpserver.mydomain.at";
		String password = "changeIT";
		String username = "changeIT";		
		String path="/tomtest";
	
		try {
			String userInfo = URLEncoder.encode(username,"UTF-8") + ":" + URLEncoder.encode(password,"UTF-8");
//			String userInfo =URLEncoder.encode(passwd,"UTF-8") + ":" + URLEncoder.encode(user,"UTF-8") ;
			URI uri=new URI(protocoll, userInfo, remoteHost, port,path);
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
			System.out.println("jetzt wird filechooseraufgerufen:");
			
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
