/**
 * 
 */
package com.googlecode.vfsjfilechooser2.demo;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;

/**
 * @author f3thomas
 *
 * created 08.01.2021
 */

public class ExampleDownloadSFTP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Test 08.01.2021
		ExampleDownloadSFTP ts=new ExampleDownloadSFTP();
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
		
		
		String remoteFile="/tomtest/tomtestfile.txt";
		
		try {
			
			FileSystemManager manager = VFS.getManager();
			
			
			//change this with your local testFile
		    FileObject local = manager.resolveFile(System.getProperty("user.dir") + "/" + "temp_"  + "vfsFile.pdf");
		    FileObject local2 = manager.resolveFile(System.getProperty("user.dir") + "/" + "temp_"  + "vfsFile2FTP4.pdf");
		    
		    FileObject remote = manager.resolveFile("sftp://" + username + ":" + password + "@" + remoteHost + "/" + remoteFile);
		    remote.copyFrom(local, Selectors.SELECT_SELF);
		    local2.copyFrom(remote, Selectors.SELECT_SELF);
		    local2.close();
		    local.close();
		    remote.close();
		    
		    
		    System.out.println("hat funktioniert ");
//		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
