/**
 * 
 */
package com.googlecode.vfsjfilechooser2.demo;

import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
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

public class ExampleDownloadFTP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Test 08.01.2021
		ExampleDownloadFTP ts=new ExampleDownloadFTP();
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
		String remoteFile="/tom";
		

		try {
			FileSystemOptions options= new FileSystemOptions();
			FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
			FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, true);
			
			FileSystemManager manager = VFS.getManager();
			
			//change this with your local testFile
		    FileObject local = manager.resolveFile(System.getProperty("user.dir") + "/" + "temp_"  + "vfsFile.pdf");
		    FileObject local2 = manager.resolveFile(System.getProperty("user.dir") + "/" + "temp_"  + "vfsFile2FTP4.pdf");
		    
		    
		    
		    System.out.println(local.getURL());
		    FileObject remote = manager.resolveFile("ftp://" + username + ":" + password + "@" + remoteHost + "/" + remoteFile,options);
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
