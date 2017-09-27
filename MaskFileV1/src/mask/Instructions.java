package mask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * 
 * @author Michael Pierre
 * 
 * This class contains methods that allow the program function instructions to be displayed in the GUI message box
 * 
 */
public class Instructions 
{
	
	public Instructions()
	{
		
	}
	
	/**
	 * Retrieves single file search functions
	 * @return
	 * @throws IOException
	 */
	public String instrSingleFile() throws IOException
	{
			String fileName = "Instructions/SingleFileMask.txt";	
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		    try 
		    {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null)
		        {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        return sb.toString();
		    } 
		    finally
		    {
		        br.close();
		    }
	}
	
	public String instrOracleDb() throws IOException
	{
			String fileName = "Instructions/OracleDBMask.txt";	
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		    try 
		    {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null)
		        {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        return sb.toString();
		    } 
		    finally
		    {
		        br.close();
		    }
	}
	/** 
	 * Retrieves the instructions to work with a directory 
	 * @return
	 * @throws IOException
	 */
	public String instrDirectory() throws IOException
	{
		
			String fileName = "Instructions/MultiFileMask.txt";		
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		    try
		    {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null)
		        {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        return sb.toString();
		    } 
		    finally 
		    {
		        br.close();
		    }
	}
	
	/**
	 * Retrieves the instructions to work with a csv file
	 * @return
	 * @throws IOException
	 */
	public String instrCsv() throws IOException
	{		 
			String fileName = "Instructions/CsvFileMask.txt";		
			BufferedReader br = new BufferedReader(new FileReader(fileName));		
		    try 
		    {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) 
		        {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        return sb.toString();
		    } 
		    finally
		    {
		        br.close();
		    }
	}
	
	public String instrCsvFileDir() throws IOException
	{ 		
		
		String fileName = "Instructions/CsvFileDirMask.txt";		
	    BufferedReader br = new BufferedReader(new FileReader(fileName));	
		try 
		{
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null)
		        {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        return sb.toString();
		} 
		finally 
		{
		        br.close();
		}
	}
	
}
