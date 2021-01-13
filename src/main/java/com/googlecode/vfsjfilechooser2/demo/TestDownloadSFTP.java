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

public class TestDownloadSFTP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Test 08.01.2021
		TestDownloadSFTP ts=new TestDownloadSFTP();
		ts.start();
	}
	// TODO Test 08.01.2021
	private void start() {
		
		String protocoll = "sftp";
		Integer port = 22;
		String remoteHost = "mysftpserver.mydomain.at";
		String password = "changeIT";
		String username = "changeIT";		
		String remoteFile="/tomtest/tomtestfile.txt";
		
		try {
			
			FileSystemManager manager = VFS.getManager();
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
