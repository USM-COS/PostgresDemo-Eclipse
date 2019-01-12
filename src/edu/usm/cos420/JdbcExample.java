package edu.usm.cos420;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.util.Properties;

import com.jcraft.jsch.JSchException;


/* 
 * Code to demonstrate creation, deletion of tables. Also, records are inserted. 
 * 
 * This code requires that a postgres (or mysql) jdbc jar file be included and a JSCH jar file.  
 * Maven can be used to pull these dependencies  
 * 
 * To manually include in the project, choose properties, then build path, then add an external jar
 * 
 */

public class JdbcExample {

    private static Properties properties = new Properties();

    private static void loadProps() {
		InputStream in = JdbcExample.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            System.out.println( "No config.properties was found.");
            e.printStackTrace();
        }
	}
	
	private static SSHTunnel createTunnelFromProperties() throws JSchException
	{
        String strSshUser = properties.getProperty("ssh.user");  // SSH login username
        String strSshPassword = properties.getProperty("ssh.password");                   // SSH login password
        String strSshHost = properties.getProperty("ssh.host");          // hostname or ip or SSH server
        String sSshPort = properties.getProperty("ssh.port");  // remote SSH host port number
        int nSshPort = Integer.parseInt(sSshPort);  // remote SSH host port number
        String strRemoteHost = properties.getProperty("remote.host");          // hostname or ip of your database server
        int nLocalPort = Integer.parseInt(properties.getProperty("local.port"));     // local port number use to bind SSH tunnel
        int nRemotePort = Integer.parseInt(properties.getProperty("remote.port"));  // remote port number of your database 
	
        SSHTunnel tunnel = new SSHTunnel(strSshUser, strSshPassword, strSshHost, nSshPort, strRemoteHost, nLocalPort, nRemotePort);
	    return tunnel;
	}
	
	public static Connection createDBConnection() throws ClassNotFoundException, SQLException
	{
		String strDbUser = properties.getProperty("jdbc.username");        // database login username
        String strDbPassword = properties.getProperty("jdbc.password");    // database login password
        String remoteHost = properties.getProperty("remote.host");     // local port nu	
        Connection con;
        
        System.out.println("Trying connection ");
    	
		Class.forName("org.postgresql.Driver");
//    	Class.forName("com.mysql.jdbc.Driver");
//        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:"+tunnelPort+"/cos420?user="+strDbUser+"&password="+strDbPassword);
		  con = DriverManager.getConnection("jdbc:postgresql://" + remoteHost +":5432" + "/clinic", strDbUser,strDbPassword);

    	if(!con.isClosed())
          System.out.println("Successfully connected to Postgres server using TCP/IP...");

        return con;
	}
	
	public static void emptyNursesTable(Connection con) throws SQLException
	{
	    Statement st = null;
	
        String createStr = "CREATE TABLE nurses (userId INTEGER, firstName VARCHAR(30), lastName VARCHAR(30), countryCode VARCHAR(10), primary key(userID))";
    	String dropStr = "DROP TABLE nurses";
    	
        DatabaseMetaData dbm = con.getMetaData();
        ResultSet tables = dbm.getTables("cos420", null, "nurses", null);
 
        st = con.createStatement();      

        if (tables != null && tables.next()) {
        // Table exists
        	st.executeUpdate(dropStr);  
        	System.out.println("Got rid of old nurse table");
        }

        st.executeUpdate(createStr);
	
	}
	
	public static void addToNursesTable(Connection con) throws SQLException
	{
	    Statement st = null;
	
		st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        ResultSet uprs = st.executeQuery("SELECT * FROM nurses");

		uprs.moveToInsertRow();
  	
		uprs.updateInt("userId", 1);
		uprs.updateString("firstName", "Jorn");
		uprs.updateString("lastName", "Klungsoyr");
		uprs.updateString("countryCode", "Ghana");

		uprs.insertRow();

		uprs.updateInt("userId", 2);
		uprs.updateString("firstName", "Sally");
		uprs.updateString("lastName", "Saviour");
		uprs.updateString("countryCode", "Ghana");
		uprs.insertRow();

	}

	public static void displayNursesTable(Connection con) throws SQLException 
	{
		Statement st = null;
		ResultSet rs = null;

		st = con.createStatement();      

		rs = st.executeQuery("SELECT userID, firstName, lastName, countryCode FROM nurses");

		while(rs.next()) {
			int userId = rs.getInt(1);
			String firstName = rs.getString(2);
			String lastName = rs.getString(3);
			String countryCode = rs.getString(4);

			System.out.println(userId + ". " + lastName + ", " +
					firstName + " (" + countryCode + ")");
		}

	}
	public static void main(String[] args) 
	{

		SSHTunnel tunnel = null;
		Connection con = null;

		loadProps();

		System.out.println("properties loaded ");
		
		try {

//			tunnel = createTunnelFromProperties();
//			int tunnelPort = tunnel.doSshTunnel();

			con = createDBConnection();
			emptyNursesTable(con);
			addToNursesTable(con);
            displayNursesTable(con);
            
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
			if (tunnel != null)
				tunnel.closeSession();
		}
	}
}