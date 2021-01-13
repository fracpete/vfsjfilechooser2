/**
 * 
 */
package com.googlecode.vfsjfilechooser2.demo;

import java.io.IOException;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.vfs2.provider.UriParser;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

/**
 * @author f3thomas
 *
 * created 08.01.2021
 */

public class TomDownloadSSHClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Test 08.01.2021
		TomDownloadSSHClient ts=new TomDownloadSSHClient();
		ts.start();
	}
	// TODO Test 08.01.2021
	private void start() {
		
		String protocoll = "sftp";
		Integer port = 22;
	
		
		String remoteHost = "myftpserver.mydomain.at";
		String password = "changeIT";
		String username = "changeIT";			
		
		String localpath=System.getProperty("user.dir") + "/" + "temp_" + "vfsFile.pdf";
//		String path="/ALLE/Adjustment/H350_30B/Adjustment_de-de/Programme/01_Squaring.f4g";
//		String path="/ALLE/Adjustment/H350_30B/Adjustment_de-de/Programme/F4Integrate Overview for Techs1_GER.pdf";
//		String path="/ALLE/Adjustment/H350_30B/Adjustment_de-de/Programme/F4Integrate Overview for Techs1_GER.pdf";
		
		String path = "/root/hallo tom.test";
		
		try {
//		    path=URIUtil.encodePath(path);
//            path=path.replaceAll("%20"," ");
//		    path = "\"" + path + "\"";
		    System.out.println(path);
			SSHClient sshClient = setupSshj(remoteHost,username,password);
		    SFTPClient sftpClient = sshClient.newSFTPClient();
		    String remoteFile = path;
		    System.out.println("TomDownload2.start() von: " + remoteFile + "    ->   " + localpath );
		    sftpClient.get(remoteFile, localpath);
		 
		    sftpClient.close();
		    sshClient.disconnect();
		    
		    
		    
		    

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private SSHClient setupSshj(String remoteHost,String username,String password) throws IOException {
	    SSHClient client = new SSHClient();
	    client.addHostKeyVerifier(new PromiscuousVerifier());
	    client.connect(remoteHost);
	    client.authPassword(username, password);
	    return client;
	}

}
