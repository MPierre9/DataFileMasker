package mask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;  
/**
 *
 * @author PierreMi
 *
 * The following class allows for program to connect to Oracle as well as read rows from a table to be used by the Mask class. 
 * 
 */
public class OracleCon
{  
	private static List<FileMetaData> fileList;
	private String userName_,password_,host_,port_,serviceName_;
	private static String tableName_;
	private static String numOfRows_;
	public  void initiate(String username,String password,String host, String port, String serviceName, String tableName) throws SQLException
	{  
			setTableName(tableName);
			System.out.println("-------- Oracle JDBC Connection Testing ------");

	        try 
	        {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	        }
	        catch (ClassNotFoundException e) 
	        {
	        	System.out.println("Where is your Oracle JDBC Driver?");
	        	e.printStackTrace();
	        	return;
	        }

	        System.out.println("Oracle JDBC Driver Registered!");

	        Connection connection = null;
	        connection = DriverManager.getConnection("jdbc:oracle:thin:"+username+"/"+password+"@"+host+":"+port+"/"+serviceName);


	  

	        if (connection != null) 
	        {
	            System.out.println("You made it, take control your database now!");		
	            try {
					select(connection);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } 
	        
	        else
	        {
	            System.out.println("Failed to make connection!");
	        }
	} 
	
	public static void select(Connection conn)	throws SQLException
	{
			  int rows = Integer.parseInt(numOfRows_);
			  String query = "";
			  if(rows > 0)
			  {
				query = "SELECT * FROM " + tableName_ + " WHERE ROWNUM <=" + rows;
			  }
			  else
				  query = "SELECT * FROM " + tableName_ ;
				 
			  
				BufferedReader in = null;
				
		    	try 
		    	{
		    		in = new BufferedReader(new FileReader("DbConnect/ColumnConfig.ini"));
		    	} 
		    	catch (FileNotFoundException e) 
		    	{
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    	}
			    String str;

			    List<String> list = new ArrayList<String>();
			    try 
			    {
			    	while((str = in.readLine()) != null)
			    	{
				       list.add(str);
			    	}
			    } 
			    catch (IOException e) 
			    {
			    	// TODO Auto-generated catch block
			    	e.printStackTrace();
			    }
			   
			    String [] columnConfig = list.toArray(new String[0]);
			  
			  try
			  {
			    PreparedStatement ps = conn.prepareStatement(query);
			    // process the results
			    ResultSet rs = ps.executeQuery();
			    fileList = new ArrayList<FileMetaData>();
			    
			    while (rs.next())
			    {
			    	//System.out.println("File Name: " + rs.getString("FILE_NAME") + " File Path: " + rs.getString("FILE_PATH") + "File Size: " + rs.getString("FILE_SIZE")  + " File Type: " + rs.getString("FILE_TYPE"));
				    FileMetaData fm = new FileMetaData();
					try{
				    fm.setFileName(rs.getString(columnConfig[0]));//f.setFileName("XXXXXXXX_" + j); Mask file name to not be identified
					fm.setFilePath(rs.getString(columnConfig[1]));
					fm.setFileSize(rs.getString(columnConfig[2])); //f.setFileSize(1048576); Option is file size is not given
					fm.setFileType(rs.getString(columnConfig[3]));
					fileList.add(fm);
					}
					catch(Exception e) 
					{
						
					}
			    }
			    System.out.println("out" + rs.toString());
			    rs.close();
			    ps.close();
			  }
			  catch (SQLException se)
			  {
			    throw se;
			  }
	}  
	
	public List<FileMetaData> getFileList()
	{
		return fileList;
	}
	
	public void setUserName(String username)
	{
		userName_ = username; 
	}
	
	public void setPassword(String password)
	{
		password_ = password; 
	}
	
	public void setHost(String host)
	{
		host_ = host; 
	}
	
	public void setPort(String port)
	{
		port_ = port; 
	}
	
	public void setServiceName(String serviceName)
	{
		serviceName_ = serviceName; 
	}
	
	public void setTableName(String tableName)
	{
		tableName_ = tableName; 
	}
	
	public void setNumberOfRows(String numOfRows)
	{
		numOfRows_ = numOfRows;
	}
} 