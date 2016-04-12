package edu.usm.cos420;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHTunnel {
   private String strSshUser;            // SSH login username
   private String strSshPassword;         // SSH login password
   private String strSshHost;             // hostname or ip or SSH server
   private int nSshPort;                  // remote SSH host port number
   private String strRemoteHost;          // hostname or ip of your database server
   private int nLocalPort;                // local port number use to bind SSH tunnel
   private int nRemotePort;               // remote port number of your database 
   private Session session;

   public SSHTunnel()
   {
	   
   }
   public SSHTunnel( String strSshUser, String strSshPassword, String strSshHost, int nSshPort, String strRemoteHost, int nLocalPort, int nRemotePort )
   {
	   this.strSshUser = strSshUser;
       this.strSshPassword = strSshPassword;
       this.strSshHost = strSshHost;
       this.nSshPort = nSshPort;
       this.strRemoteHost = strRemoteHost;
       this.nLocalPort = nLocalPort;
       this.nRemotePort = nRemotePort;
   }
   
   public int doSshTunnel() throws JSchException
   {
	    final Properties sshConfig = new Properties();
	    sshConfig.put( "StrictHostKeyChecking", "no" );

	    final JSch jsch = new JSch();
	    session = jsch.getSession( strSshUser, strSshHost, 22 );
	    session.setPassword( strSshPassword );
	    
	    session.setConfig( sshConfig );
	    
	    session.connect();
	    System.out.println("Secure Connection");
       
	    session.setPortForwardingL(nLocalPort, "127.0.0.1", nRemotePort);
        System.out.println("localhost:"+nLocalPort+" -> "+strRemoteHost+":"+nRemotePort);
        System.out.println("Port Forwarded");
        return nLocalPort;
   }
   
   public void closeSession() 
   {
	   if(session !=null && session.isConnected()){
	       System.out.println("Closing SSH Connection");
	       session.disconnect();
	   }   
   }
   
   public String getStrSshUser() {
	return strSshUser;
}
   public void setStrSshUser(String strSshUser) {
	   this.strSshUser = strSshUser;
   }
   public String getStrSshPassword() {
	   return strSshPassword;
   }
   public void setStrSshPassword(String strSshPassword) {
	   this.strSshPassword = strSshPassword;
   }
   public String getStrSshHost() {
	   return strSshHost;
   }
   public void setStrSshHost(String strSshHost) {
	   this.strSshHost = strSshHost;
   }
   public int getnSshPort() {
	   return nSshPort;
   }
   public void setnSshPort(int nSshPort) {
	   this.nSshPort = nSshPort;
   }
   public String getStrRemoteHost() {
	   return strRemoteHost;
   }
   public void setStrRemoteHost(String strRemoteHost) {
	   this.strRemoteHost = strRemoteHost;
   }
   public int getnLocalPort() {
	   return nLocalPort;
   }
   public void setnLocalPort(int nLocalPort) {
	   this.nLocalPort = nLocalPort;
   }
   public int getnRemotePort() {
	   return nRemotePort;
   }
   public void setnRemotePort(int nRemotePort) {
	   this.nRemotePort = nRemotePort;
   }

}
