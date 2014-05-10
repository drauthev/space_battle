package server;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FTPConnector {
	FTPClient ftp;
	
	public FTPConnector(String host, String user, String pwd) throws Exception {
        ftp = new FTPClient();
        ftp.setConnectTimeout(5); // set connection timeout to 5 seconds
        try{
	        ftp.connect(host);
			boolean login = ftp.login(user, pwd); 
			if (login){
				System.out.println("FTP Connection successful");
			}
			else{
				System.out.println("FTP Connection failed...");  
			}
	        ftp.setFileType(FTP.ASCII_FILE_TYPE);
	        ftp.enterLocalPassiveMode();
        }
        catch(SocketTimeoutException e){
        	System.out.println("FTPConnector catch block - ftp connection could not be established in the first place - new high score will not be handled");
        }
    }
	
	public void setFileTypeToBinary(){
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setFileTypeToAscii(){
		try {
			ftp.setFileType(FTP.ASCII_FILE_TYPE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
    public String downloadFileAndCopyToString(String remoteFilePath) {
    	
    	InputStream istream;
    	String fileContent = null;
		try {
			istream = this.ftp.retrieveFileStream(remoteFilePath);
			fileContent = getStringFromInputStream(istream);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
    }
     
    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                // do nothing as file is already downloaded from FTP server
            }
        }
    }
    
    // convert InputStream to String
 	private static String getStringFromInputStream(InputStream is) {
  
 		BufferedReader br = null;
 		StringBuilder sb = new StringBuilder();
  
 		String line;
 		try {
  
 			br = new BufferedReader(new InputStreamReader(is));
 			while ((line = br.readLine()) != null) {
 				sb.append(line);
 			}
  
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			if (br != null) {
 				try {
 					br.close();
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 			}
 		}
  
 		return sb.toString();
  
 	}

	public FTPClient getFtp() {
		return ftp;
	}

}
