package space_battle.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
/**
 * A wrapper class for the Apache common's FTPClient class.
 * When instantiated, it creates an FTPClient object and connects it to the FTP server described in the constructor.
 * 
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class FTPConnector {
	FTPClient ftp;
	/**
	 * 
	 * @param host Host name of the FTP server.
	 * @param user Username for login to the FTP server.
	 * @param pwd Password for the login.
	 * @throws Exception SocketTimeoutException if the connection with the FTP server could not have been established. Typically when the server is down.
	 */
	public FTPConnector(String host, String user, String pwd) throws Exception {
        ftp = new FTPClient();
        ftp.setConnectTimeout(120);
        try{
	        ftp.connect(host);
			boolean login = ftp.login(user, pwd); 
			if (login){
				//System.out.println("FTP Connection successful");
			}
			else{
				//System.out.println("FTP Connection failed...");  
			}
			ftp.setDefaultTimeout(120);
	        ftp.setDataTimeout(5000);
	        ftp.setFileType(FTP.ASCII_FILE_TYPE);
        }
        catch(SocketTimeoutException e){
        	System.out.println("FTPConnector catch block - ftp connection could not be established in the first place - new high score will not be handled");
        }
    }
	/**
	 * Sets the file transfer between the client and the server to binary. (needed before sending a file to the server via an output stream.)
	 */
	public void setFileTypeToBinary(){
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sets the file transfer between the client and the server to ASCII.
	 */
	public void setFileTypeToAscii(){
		try {
			ftp.setFileType(FTP.ASCII_FILE_TYPE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Download a file from the FTP server and converts it content to a String.
	 * @param remoteFilePath Path of the file to download on the server.
	 * @return A String with the content of the file downloaded.
	 */
    public String downloadFileAndCopyToString(String remoteFilePath) {
    	
    	InputStream istream;
    	String fileContent = null;
    	
		try {
			istream = this.ftp.retrieveFileStream(remoteFilePath);         
			fileContent = getStringFromInputStream(istream);
			ftp.completePendingCommand();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
            try {
				throw new Exception("Connect failed: " + ftp.getReplyString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
         else{
          	System.out.println("FTP reply: " + ftp.getReplyString());
         }
		return fileContent;
    }
    /**
     * Disconnects the FTPClient object from the server.
     */
    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
    }
    
    /**
     * Converts an InputStream to String. Used by {@link #downloadFileAndCopyToString(String)}.
     * @param is The input stream from which this method gets the content.
     * @return The content of the input stream.
     */
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
 	/**
 	 * Getter of the FTPClient object itself. (Creating/writing to the file is simpler using the class' native methods than writing wrapper methods.)
 	 * @return The FTPClient object of this class. ({@link #ftp})
 	 */
	public FTPClient getFtp() {
		return ftp;
	}

}
