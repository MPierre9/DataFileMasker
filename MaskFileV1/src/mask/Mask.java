package mask;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static java.nio.file.StandardCopyOption.*;


import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

import com.opencsv.CSVReader;
/**
 * 
 * @author Michael Pierre 
 * @version V.04 edit 08/11/2017
 * Contact: m.pierre@live.ca
 * 
 * This class handles all mathematical calculations and methods that perform the data file masking 
 * There are currently five methods of data file masking that the program supports 
 * 
 * 1. Single file masking: Masks a single file that is chosen in the included GUI 
 * 
 * 2. Multi File masking: Masks all files in a selected directory. The best way to use this option is to create a pre made directory
 *    desired files to be masked 
 *    
 * 3. CSV file masking: Meant to simulate masking files from a database. This method involves importing a csv file with current field names
 *    "File Name", "File Type", and "File Size" into the program to be read and created as physical data files. Basically from 
 *     file metadata to an actual file.
 *     
 * 4. CSV file masking (file path): This option is almost the exact same as the standard CSV masking function except instead of creating a file 
 * 									based on a record of metadata a file is masked from the given location in the CSV file. 
 * 
 * 5. Oracle Filemetadata mask: This option allows the user to create physical files of the exact same name, type, size, and path. However when opened the file
 *                              simply contains 1s. This option is designed to work with any table as long as file name, path, size, and type are provided. 
 *                              
 *                              There is also a new option added as of 08/11/2017 that allows the use of a non-sensitive document repository to be used to mask the files
 *                              How this works is the program searches the repository for the non-sensitive that best matches the real document. There are two ways of doing this
 *                              Aggressive match which will match the non-sensitive document based on file type and size. Meaning It HAS to be a pdf if the real one if a pdf. The second 
 *                              option is to simply leave this option off which will match based on the closest file size only and NOT file type.
 *     
 *     Note: The main focus of this application is its functionality with Oracle which would be option 5 listed above. 
 *     
 *     Program Summary: This data file masker works by using source files containing exact sizes of binary data to fill a 
 *     					corresponding file. By using the source files to help mask the masking process is tremendously improved. 
 *     					For Example: if I want to mask a 9.6 megabyte file the algorithm will analyze that size and make the 
 *     					appropriate decisions. In this case the algorithm will subtract 8 megabytes from that total file size 	
 *     					and that portion will be filled by the 4mbFill file twice. Then with the remained 1.6 megabytes the
 *     					algorithm will split that into 1 megabyte to be filled by the 1mbFill file and so on by the 1kbFill 
 *     					and 1bFill file. Basically the file size is broken down until it reaches byte level and the appropriate
 *     					source files are used to fill the masked file with data. 
 *     
 *     
 *  
 * 				 
 * 						
 */
public class Mask
{	
    private static File file2 ; //The main file to be masked (used in the singular file masking method)
    private static final int ONE_MB =  1048576; //The size of one megabyte in bytes
    private static File outfile; //The masked file to be produced when the masking process is done
    private static int mbCount = 0; //How many megabytes the masked file needs
    private static int kbCount = 0; //How many kilobytes the masked file needs 
    private static int bCount = 0 ; //How many bytes the masked file needs
    private static int qCount = 0 ; //How many qBytes the masked file needs (qByte is 4mb)
    private static List<MaskFile> fileList ; //List of files to be masked of type MaskFile. Used in the CSV file masking method.
    private static List<FileDirectory> fileList2;
    private static GUI gui_ = new GUI(); //Creates GUI object which is used to show the message box messages in this class
    private boolean flag = false;
    private double tmpFileL = 0 ; // Temporary file length that is used in masking calculations
    private boolean doesExist = false; 
    private boolean oracleFlag = false; 
    private int oracleSize ; 
    String path; //The path where the masked files are to be created
    String oraclePath;
    File dir ;
    File oDir;// The file where the masked files are
    String [] configPath;
    String [] resPath;
    
    /**
     * Creates new file when class is initialized 
     * @param fileName
     */
    public Mask(String fileName)
    {
	    file2 = new File(fileName); 
		BufferedReader in = null;
		
    	try 
    	{
    		in = new BufferedReader(new FileReader("OutputFileConfig/OutputFileConfig.ini"));
    	} 
    	catch (FileNotFoundException e) 
    	{
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
	    	e.printStackTrace();
	    }
	   
	    configPath = list.toArray(new String[0]);
	    
	 		BufferedReader in2 = null;
	 		
	     	try 
	     	{
	     		in2 = new BufferedReader(new FileReader("Repository/repositoryConfig.ini"));
	     	} 
	     	catch (FileNotFoundException e) 
	     	{
	     		e.printStackTrace();
	     	}
	 	    String str2;

	 	    List<String> list2 = new ArrayList<String>();
	 	    try 
	 	    {
	 	    	while((str2 = in2.readLine()) != null)
	 	    	{
	 		       list2.add(str2);
	 	    	}
	 	    } 
	 	    catch (IOException e) 
	 	    {
	 	    	e.printStackTrace();
	 	    }
	 	   
	 	    resPath = list2.toArray(new String[0]);
	 	    System.out.println("Repository Path " + resPath[0]);
    }
 
   
    /**
     * Creates a directory to store the masked files
     */
    public void createDirectory() 
    { 	   
    	dir = new File(configPath[0]);       
		// attempt to create the directory here
		boolean successful = dir.mkdir();
		
		if (successful)
		{
			// creating the directory succeeded
			System.out.println("directory was created successfully");
		}
		
		else
		{
  			// creating the directory failed
  			System.out.println("failed trying to create the directory");
		}	
    }
    
    public void createDirectoryOracle(String str) 
    { 
    	   String attach = str.substring(3, str.length());
    	  // System.out.println("Attach "+ attach);
    	  
    	   String[] parts = attach.split("/");

    	 //  oDir = new File("C:/Users/PierreMi/Documents/MaskedV2/"+attach);  
    	  		try
    	  		{
    	  		  oDir = new File(configPath[0]);
    	  		  oDir.mkdir();
    	  	      oDir = new File(configPath[0]+parts[0]);
    	  		  oDir.mkdir();
            	  oDir = new File(configPath[0]+parts[0]+"/"+parts[1]);
            	  oDir.mkdir();
            	  oDir = new File(configPath[0]+parts[0]+"/"+parts[1] + "/" +parts[2]);
            	  oDir.mkdir();
            	  oDir = new File(configPath[0]+parts[0]+"/"+parts[1] + "/" +parts[2]+"/"+parts[3]);
            	  oDir.mkdir();
            	  oDir = new File(configPath[0]+parts[0]+"/"+parts[1]+ "/" +parts[2]+"/"+parts[3]+"/"+parts[4]);
            	  oDir.mkdir();
            	  oDir = new File(configPath[0]+parts[0]+"/"+parts[1]+"/"+parts[2]+"/"+parts[3]+"/"+parts[4]+"/"+parts[5]);
            	  oDir.mkdir();
    	  		}
    	  		catch(ArrayIndexOutOfBoundsException e)
    	  		{
    	  			
    	  		}
    	  	 // All directories are made 
    	  	 // Next step is to create files 
    	  	 // Now its important to remember that the filemetadata path will be used to write to that file
    	  	

    }
    
    /**
     * Creates a sing file to be masked, the file is retrieved from the class when it is initialized
     */
    public void createMaskedFile()
    {   
    	path = dir.getAbsolutePath();
    	System.out.println(dir);
		try
		{			
			String sourceFileName = file2.getName();
			System.out.println("Source file: " + sourceFileName);
			outfile = new File(path + "/" + sourceFileName);	
			if (outfile.createNewFile())
			{
				System.out.println("Masked file is created!");
				gui_.show("Masked file is created!");
				doesExist = false;
			}
			else
			{
				System.out.println("File already exists.");
				doesExist = true;
				gui_.show("File already exists.");
			}
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
  /**
   * Creates all files from a directory to be masked 
   * @param sourceFileName the file name of the current file being masked 
   * @param j the count to determine which file is to be created
   */
    public void createMultiFile(String sourceFileName,int j)
    {   
    	path = dir.getAbsolutePath();
    	System.out.println("COUNT MULTI: " + j);
		try
		{				
			System.out.println("Source file: " + sourceFileName);
			outfile = new File(path + "/" + sourceFileName);	
			if (outfile.createNewFile())
			{
				System.out.println("File is created!");
				gui_.show("Masked file created");
				doesExist = false; 
			}
			else
			{
				System.out.println("File already exists.");			
				gui_.show("Masked file already exists.");
				doesExist = true;
			}
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
    
    /**
     * Takes data from the csv file and creates the files to be masked using the count i to determine the files attributes
     * @param sourceFileName the file name from the csv file
     * @param i the record count
     */
    public void createMaskedFileCsv(String sourceFileName, int i)
    {  
    	path = dir.getAbsolutePath();
		try
		{			
			System.out.println("Source file: " + sourceFileName);
			outfile = new File(path + "/" + sourceFileName + fileList.get(i).getFileType());
			if (outfile.createNewFile())
			{
				System.out.println("File is created!");
				gui_.show("Masked file created!");
				doesExist = false;
			}
			else
			{
				System.out.println("File already exists.");
				gui_.show("Masked file already exists.");
				doesExist = true;
			}

		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
    
    /**
     * This class creates the file based on table metadata (This method is only used for NON repository option)
     * @param sourceFileName the file name of the record in the oracle table
     * @param dbPath is the path that is in the oracle table example: C:/CM_PDF/Medical_Record
     * @param fileType the file type for that record in the oracle table (after conversion from IBM TYPE ex. RICHTEXT -> rtf)
     */
    public void createMaskedFileOracle(String sourceFileName, String dbPath, String fileType)
    {      	  
    	String attach = dbPath.substring(3, dbPath.length());

		try
		{			
			System.out.println("Source file: " + sourceFileName);
			outfile = new File(configPath[0]+"/"+attach+"/"+sourceFileName+"."+fileType);
			if (outfile.createNewFile())
			{
				System.out.println("File is created!");
				gui_.show("Masked file created!");
		
				doesExist = false;
			}
			else
			{
				System.out.println("File already exists.");
				gui_.show("Masked file already exists.");
				doesExist = true;
			}

		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
   
    /**
     * Masks a single file
     * @param file is the file to be analysed and masked 
     */
    public void singleFileRun(File file)
    {   
    		 createMaskedFile();
    		 if(!doesExist)
    		 {
    			 qCount = 0; 
    			 mbCount = 0; 
    			 kbCount = 0; 
    			 bCount = 0;  	 
    			 long startTime = System.currentTimeMillis();
    			 double leftOverMb = 0; 
    			 double kbToFill = 0;  
    			 double byteToFill = 0; 
    			 double numOfMbFill = 0; 
    			 double numOf4MbFill = 0;
    			 double FOUR_MB = 4194304;
    			 double file2L = (double) file.length();
    			 double kbL = (double) ONE_MB;
    			 double tmpFileL = file.length();
    			 double takeQBytes = 0;
    			 double x = 0; 
    			 int qBytesFill = 0;
    			 FileInputStream instream = null;
    			 FileInputStream instream2 = null;
    			 FileInputStream instream3 = null;
    			 FileOutputStream outstream = null;

    			 System.out.println("File length " + file2.length() + " bytes.");    	 
    			 numOf4MbFill = file2L / FOUR_MB; 
    			 System.out.println("Number of 4mb to fill: " + numOf4MbFill); 
    			 qBytesFill = (int) numOf4MbFill;
    			 x = FOUR_MB * qBytesFill;
    			 takeQBytes = (tmpFileL - x );
    			 System.out.println("Final mb to fill: " + takeQBytes);
    			 numOfMbFill =  takeQBytes /kbL ;   
    			 System.out.println("Number of megabytes to fill: " + numOfMbFill + ".");
    			 int mbFill = (int)numOfMbFill; 
    			 int quadFill = mbFill / 4; 
    			 System.out.println("Quad Fill... " + quadFill);
    			 leftOverMb = numOfMbFill - mbFill;	   
    			 System.out.println("Left over mb "+leftOverMb); 
    			 kbToFill = leftOverMb * 1024;  
    			 System.out.println("kb to fill " + kbToFill);
    			 int kbOutFill = (int) kbToFill;
    			 System.out.println("Kilobytes to fill " + kbOutFill);
    			 byteToFill = kbToFill - kbOutFill;
    			 int bytesFill = (int) (byteToFill * 1024); 
    	
    			 /////////////////////////////////////
    			 //If file is at megabyte level then a filler file will be used to fill the file until it can filled with bytes only 
    			 //This might speed up the process already having a 1mb fill file 
    			 try
    			 {
    				 while(qCount != qBytesFill)
    				 { 
    					 File infile =new File("MaskFillFiles/4mbFill.txt");
     
    					 instream = new FileInputStream(infile);

    					 outstream = new FileOutputStream(outfile,true);
     
    					 byte[] buffer = new byte[2048];
     
    					 int length;
    					 /*copying the contents from input stream to
    					  * output stream using read and write methods
    					  */
    					 while ((length = instream.read(buffer)) > 0)
    					 {
    						 outstream.write(buffer, 0, length); 		
    					 }
    					 qCount++;
    				 }
        		
    				 while(mbCount != mbFill)
    				 { 
    					 
    					 File infile =new File("MaskFillFiles/1mbFill.txt");
    					 File infile2 = new File("MaskFillFiles/1kbFill.txt");
     
    					 instream = new FileInputStream(infile);
    					 instream2 = new FileInputStream(infile2);

    					 outstream = new FileOutputStream(outfile,true);
     
    					 byte[] buffer = new byte[2048];
     
    					 int length;
    					 /*copying the contents from input stream to
    					  * output stream using read and write methods
    					  */
        	    
    					 while ((length = instream.read(buffer)) > 0)
    					 {
    						 outstream.write(buffer, 0, length);    	
    					 }
    					 mbCount++;
    				 }

    				 while(kbCount != kbOutFill)
    				 {
    					 File infile3 = new File("MaskFillFiles/1kbFill.txt");
    					 outstream = new FileOutputStream(outfile,true);
     
    					 byte[] buffer = new byte[2048];
     
    					 int length;
    					 instream2 = new FileInputStream(infile3);
    					 /*copying the contents from input stream to
    					  * output stream using read and write methods
    					  */
    					 while ((length = instream2.read(buffer)) > 0)
    					 {
    						 outstream.write(buffer, 0, length);	
    					 }	       	       
    					 kbCount++;
    				 }
	
    				 while(bCount != bytesFill)
    				 {	
    					 //System.out.println("Filling bytes..." + count2);
    					 File infile3= new File("MaskFillFiles/1bFill.txt");
     
    					 instream3 = new FileInputStream(infile3);

    					 outstream = new FileOutputStream(outfile,true);
     
    					 byte[] buffer = new byte[2048];
     
    					 int length;
    					 /*copying the contents from input stream to
    					  * output stream using read and write methods
    					  */
        	    
    					 while ((length = instream3.read(buffer)) > 0)
    					 {
    						 outstream.write(buffer, 0, length);	
    					 }	
        	       
    					 bCount++;
    				 }

    				 //Closing the input/output file streams
    				 if(qCount > 0 || mbCount > 0)
    					 instream.close();
        	  
    				 outstream.close();

    				 System.out.println("File copied successfully!!");
    			 }

    			 catch(IOException ioe)
    			 {
    				 ioe.printStackTrace();
    			 }
        	
    			 if(!flag)
    			 {
    				 try 
    				 {
    					 Desktop.getDesktop().open(new File(configPath[0]));
    				 }   	    
    				 catch (IOException e) 
    				 {
    					 // TODO Auto-generated catch block
    					 e.printStackTrace();
    				 }
    			 }

        	
    			 long endTime   = System.currentTimeMillis();
    			 long totalTime = endTime - startTime;
    			 System.out.println("Program excuted in: " + (totalTime) + " seconds.");
    			 gui_.show("Program excuted in: " + (totalTime) + " second(s).");
    		 }
    }
    
    /**
     * The following method masks all files in a particular directory. It is advised to have a pre-deterimined directory 
     * with the files you want to mask to avoid potential errors. The method uses an array of Files to create the masked 
     * files. 
     */
    public void multiFileRun()
    {
    	int j = 0; 
    	long totalTime3 = 0;
    	File [] listOfFiles = file2.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) 
	    {
	       if (listOfFiles[i].isFile()) 
	       {
	         System.out.println("File " + listOfFiles[i].getName());
	         gui_.show("File " + listOfFiles[i].getName() + " File Size: " + listOfFiles[i].length());
	       } 
	     
	       else if (listOfFiles[i].isDirectory()) 
	       {
	         System.out.println("Directory " + listOfFiles[i].getName());
	         gui_.show("Directory " + listOfFiles[i].getName());
	       }
	    } 
	    if(!doesExist)
	    {
	    	for(j = 0;j < listOfFiles.length;j++)
	    	{	    	 
	    	   	   createMultiFile(listOfFiles[j].getName(),j);  
	    	 
	    		   qCount = 0; 
	    		   mbCount = 0; 
	    		   kbCount = 0; 
	    		   bCount = 0;      	 
	    		   long startTime = System.currentTimeMillis();
	      	 	   double leftOverMb = 0;
	      	 	   double kbToFill = 0;  
	      	 	   double byteToFill = 0; 
	      	 	   double numOfMbFill = 0; 
	      	 	   double numOf4MbFill = 0;
	      	 	   double FOUR_MB = 4194304;
	      	 	   double file2L = (double) listOfFiles[j].length();
	      	 	   double kbL = (double) ONE_MB;
	      	 	   double tmpFileL = listOfFiles[j].length();
	      	 	   double takeQBytes = 0;
	      	 	   double x = 0; 
	      	 	   int qBytesFill = 0;
	      	 	   FileInputStream instream = null;
	      	 	   FileInputStream instream2 = null;
	      	 	   FileInputStream instream3 = null;
	      	 	   FileOutputStream outstream = null;
	      	 	   System.out.println("File length " + listOfFiles[j].length() + " bytes.");    	 
	      	 	   numOf4MbFill = file2L / FOUR_MB; 
	      	 	   System.out.println("Number of 4mb to fill: " + numOf4MbFill); 
	      	 	   qBytesFill = (int) numOf4MbFill;
	      	 	   x = FOUR_MB * qBytesFill;
	      	 	   takeQBytes = (tmpFileL - x );
	      	 	   System.out.println("Final mb to fill: " + takeQBytes);
	      	 	   numOfMbFill =  takeQBytes /kbL ;   
	      	 	   System.out.println("Number of megabytes to fill: " + numOfMbFill + ".");
	      	 	   int mbFill = (int)numOfMbFill; 
	      	 	   int quadFill = mbFill / 4; 
	      	 	   System.out.println("Quad Fill... " + quadFill);
	      	 	   leftOverMb = numOfMbFill - mbFill;	   
	      	 	   System.out.println("Left over mb "+leftOverMb); 
	      	 	   kbToFill = leftOverMb * 1024;  
	      	 	   System.out.println("kb to fill " + kbToFill);
	      	 	   int kbOutFill = (int) kbToFill;
	      	 	   System.out.println("Kilobytes to fill " + kbOutFill);
	      	 	   byteToFill = kbToFill - kbOutFill;
	      	 	   int bytesFill = (int) (byteToFill * 1024); 
      	
	      	 	   /////////////////////////////////////
	      	 	   //If file is at megabyte level then a filler file will be used to fill the file until it can filled with bytes only 
	      	 	   //This might speed up the process already having a 1mb fill file 
	      	 	   try
	      	 	   {
	      	 		   while(qCount != qBytesFill)
	      	 		   { //  System.out.println("Filling Megabytes..." + count);
	      	 			   File infile =new File("MaskFillFiles/4mbFill.txt");
	       
	      	 			   instream = new FileInputStream(infile);
	      	 			   outstream = new FileOutputStream(outfile,true);
	       
	      	 			   byte[] buffer = new byte[1024];	       
      	 			       int length;
	      	 			   /*copying the contents from input stream to
	      	 			    * output stream using read and write methods
	      	 			    */
	          	    
	      	 			   while ((length = instream.read(buffer)) > 0)
	      	 			   {
	      	 				   outstream.write(buffer, 0, length); 		
      	 			   }
      	 			   qCount++;
	      	 		   }
	          		
       	 		   while(mbCount != mbFill)
	      	       { 		
       	 			   		//  System.out.println("Filling Megabytes..." + count);
	      	 			   File infile =new File("MaskFillFiles/1mbFill.txt");
	      	 			   File infile2 = new File("MaskFillFiles/1kbFill.txt");
	       
	      	 			   instream = new FileInputStream(infile);
	      	 			   instream2 = new FileInputStream(infile2);

	      	 			   outstream = new FileOutputStream(outfile,true);
	       
	      	 			   byte[] buffer = new byte[1024];       
	      	 			   int length;
	      	 			   /*copying the contents from input stream to
	      	 			    * output stream using read and write methods
	      	 			    */
	          	    
	      	 			   while ((length = instream.read(buffer)) > 0)
	      	 			   {
	      	 				   outstream.write(buffer, 0, length);    	
	      	 			   }
	      	 			   mbCount++;
	      	       }

      	 		   while(kbCount != kbOutFill)
      	 		   {
	      	 			   //System.out.println("Filling Kilobytes..." + count1);
	          			
	      	 			   File infile2 = new File("MaskFillFiles/1kbFill.txt");
	       
	      	 			   instream2 = new FileInputStream(infile2);

	      	 			   outstream = new FileOutputStream(outfile,true);
	       
	      	 			   byte[] buffer = new byte[1024];
	       
	      	 			   int length;
	      	 			   /*copying the contents from input stream to
	      	 			    * output stream using read and write methods
	      	 			    */
	      	 			   while ((length = instream2.read(buffer)) > 0)
	      	 			   {
	      	 				   outstream.write(buffer, 0, length);	
	      	 			   }	       	       
	      	 			   kbCount++;
      	 		   }
	  		      	   
      	 		   while(bCount != bytesFill)
      	 		   {	
	      	 			   //System.out.println("Filling bytes..." + count2);
	      	 			   File infile3= new File("MaskFillFiles/1bFill.txt");
	       
	      	 			   instream3 = new FileInputStream(infile3);

	      	 			   outstream = new FileOutputStream(outfile,true);
	       
	      	 			   byte[] buffer = new byte[1024];
	       
	      	 			   int length;
	      	 			   /*copying the contents from input stream to
	      	 			    * output stream using read and write methods
	      	 			    */
	          	    
	      	 			   while ((length = instream3.read(buffer)) > 0)
	      	 			   {
	      	 				   outstream.write(buffer, 0, length);	
	      	 			   }	
          	       
	      	 			   bCount++;
      	 		   }	

      	 		   //Closing the input/output file streams
      	 		   if(mbCount > 0 || qCount > 0 )
      	 			   instream.close();
        	  
      	 		   outstream.close();

      	 		   System.out.println("File copied successfully!!");
	          	}

	          	catch(IOException ioe)
	          	{
	          		ioe.printStackTrace();
	          	}
	      	 	  
	            long endTime   = System.currentTimeMillis();	           
	            long totalTime = endTime - startTime;
	            totalTime3 = totalTime3 + totalTime;
	            System.out.println("Program excuted in: " + (totalTime) + " seconds.");
	            gui_.show("File Masked in: " + (totalTime) + " milisecond(s).");     
	    	}
	       
	   }
	   try 
  	   {
			Desktop.getDesktop().open(new File(configPath[0]));
  	   } 
  	   catch (IOException e) 
  	   {
			// TODO Auto-generated catch block
			e.printStackTrace();
  	   }
	   gui_.show("TOTAL TIME: " + totalTime3 + " miliseconds");

    }
   
    
    /**
     * The following method takes in a CSV file and masks the records containing in it. Note: the records must be in the exact format as the 
     * file class.
     * @throws IOException
     */
    public void csvFileRun() throws IOException 
    {   
    	int totalTime3 = 0;
    	CSVReader reader = new CSVReader(new FileReader(file2), ',');

		fileList = new ArrayList<MaskFile>();

		// read line by line
		String[] record = null;
		//Gets records delimited by a comma and sets them to the File class
		//From this the goal is to generate a list of file objects that can be read and masks
		int j = 0;
		while ((record = reader.readNext()) != null)
		{
			MaskFile f = new MaskFile();
			f.setFileName(record[0]);//f.setFileName("XXXXXXXX_" + j); Mask file name to not be identified
			f.setFileType(record[1]);
			f.setFileSize(record[2]); //f.setFileSize(1048576); Option is file size is not given
			fileList.add(f);
			System.out.println(fileList);
			j++;
		}

		reader.close();
	    fileList.remove(0);
		for(int i = 0; i < fileList.size();i++)
		{
		
			 createMaskedFileCsv(fileList.get(i).getFileName(),i);	   
			 if(!doesExist)
			 {
			 qCount = 0; 
	    	 mbCount = 0; 	    	
	    	 kbCount = 0; 
	    	 bCount = 0;
	    	 
	    	 long startTime = System.currentTimeMillis();
	    	 double numOfMbFill = 0; 
	    	 double numOf4MbFill = 0;
	    	 double FOUR_MB = 4194304;
	    	 double kbL = (double) ONE_MB;
	    	 int qBytesFill = 0 ; 
	    	 double takeQBytes = 0;
	    	 double size = Double.parseDouble(fileList.get(i).getFileSize());
	    	 System.out.println("Size " + size );
	    	 numOf4MbFill = size / FOUR_MB; 
	    	 System.out.println("Number of 4mb to fill: " + numOf4MbFill);
	    	 
	    	 qBytesFill = (int) numOf4MbFill;
	    	 
	    	 double tmpFileL = Double.parseDouble(fileList.get(i).getFileSize());	    	  
	    	 double x = 0; 
	    	 x = FOUR_MB * qBytesFill;
	    	 System.out.println("X " + x);
	    	 takeQBytes = (tmpFileL - x );
	    	 System.out.println("Final mb to fill: " + takeQBytes);
	    	 numOfMbFill =  takeQBytes /kbL ;  
	    	 
	    	 System.out.println("Number of megabytes to fill: " + numOfMbFill + ".");
	    	 int mbFill = (int)numOfMbFill;	    	 
	    	 int quadFill = mbFill / 4; 
	    	 //x = total length / qbyte 
	    	 //y = total length - qbyte (for x times)
	    	 
	    	 //after qybte added 3.89461mb needed in which regular calculation would proceed
	    	 //Add a Qbyte 4 times 
	    	 //So 16 qbyte
	    
	    	 System.out.println("Quad Fill... " + quadFill);
	    	 
	    	 double leftOverMb = 0;
    	     double kbToFill = 0;  
	    	 double byteToFill = 0; 
	    	 leftOverMb = numOfMbFill - mbFill;
	    	   
	    	  System.out.println("Left over mb "+leftOverMb);	    	 
	    	  kbToFill = leftOverMb * 1024;
	    	  
	    	  System.out.println("kb to fill " + kbToFill);
	    	  
	    	  int kbOutFill = (int) kbToFill;
	    	  
	    	  System.out.println("Kilobytes to fill " + kbOutFill);
	    	  
	    	  byteToFill = kbToFill - kbOutFill;	    	  
	    	  
	    	  int bytesFill = (int) (byteToFill * 1024); 
	    	  /////////////////////////////////////
	    	  //If file is at megabyte level then a filler file will be used to fill the file until it can filled with bytes only 
	    	  //This might speed up the process already having a 1mb fill file 
	        
	    	  FileInputStream instream = null;
	    	  FileInputStream instream2 = null;
	    	  FileInputStream instream3 = null;
	    	  FileOutputStream outstream = null; 	
	        	
	    	  try
	    	  {
	    		    //Fills the masked file with the appropriate amount of 4mb blocks
	        		while(qCount != qBytesFill)
	         	    { 	//  System.out.println("Filling Megabytes..." + count);
	        			File infile =new File("MaskFillFiles/4mbFill.txt");
		        		instream = new FileInputStream(infile);
	        			outstream = new FileOutputStream(outfile,true); //The second parameter of the FileOutputStream allows for file to be appended with data instead of overwritten
	        			byte[] buffer = new byte[1024];
	        			int length;
	        			/*copying the contents from input stream to	        			 
	        			 * output stream using read and write methods*/
	        	    
	        			while ((length = instream.read(buffer)) > 0)
	        			{
	        				outstream.write(buffer, 0, length); 		
	        			}
	        			qCount++;
	         	    }
	        		
	        		//Fills the masked file with the appropriate amount of 1mb blocks
	        		while(mbCount != mbFill)
	        		{	 //  System.out.println("Filling Megabytes..." + count);
	        			File infile =new File("MaskFillFiles/1mbFill.txt");
	        			File infile2 = new File("MaskFillFiles/1kbFill.txt");
	     	        	instream = new FileInputStream(infile);	        	
	     	        	instream2 = new FileInputStream(infile2);
	        			outstream = new FileOutputStream(outfile,true);
	        			byte[] buffer = new byte[1024];
	        			int length;	        
	        			/*copying the contents from input stream to	        	
	        			 *output stream using read and write methods	    
	        			 */   		
	        	    	while ((length = instream.read(buffer)) > 0)
	        	    	{
	        	    		outstream.write(buffer, 0, length);
	          	    	}
	        	    	mbCount++;
	        		}

	        		//Fills the masked file with the appropriate amount of 1kb blocks
	        		while(kbCount != kbOutFill)
	        		{
	        			//System.out.println("Filling Kilobytes..." + count1);
    			
	        			File infile2 = new File("MaskFillFiles/1kbFill.txt");	     
	        			instream2 = new FileInputStream(infile2);
	        			outstream = new FileOutputStream(outfile,true);
	        			byte[] buffer = new byte[1024];	     	        			
	        			int length;
	        			/*copying the contents from input stream to
	        			 * output stream using read and write methods
	        			 */
	        	    	while ((length = instream2.read(buffer)) > 0)
	        	    	{
	        	    		outstream.write(buffer, 0, length);		          	    	
	        	    	}	
	        	    		kbCount++;
	         	   		}
	        		
	        			// Fills the file with the appropriate amount of bytes
	    	        	while(bCount != bytesFill)	       
	    	        	{	
	    	        		//System.out.println("Filling bytes..." + count2);
	    	        		File infile3= new File("MaskFillFiles/1bFill.txt");
	    	        		instream3 = new FileInputStream(infile3);
	    	        		outstream = new FileOutputStream(outfile,true);
	    	        		byte[] buffer = new byte[1024];	     
	    	        		int length;
	    	        		/*copying the contents from input stream to
	    	        		 * output stream using read and write methods/	        			 */
	        	    
	    	        		while ((length = instream3.read(buffer)) > 0)
	    	        		{	
	    	        			outstream.write(buffer, 0, length);	
	    	        		}	
	    	        		bCount++;
	    	        	}
	    	        	//Closing the input/output file streams
	    	        	if(qCount > 0 || mbCount > 0)
	    	        	instream.close();
	    	        	
	    	        	outstream.close();
	    	        	System.out.println("File: " + fileList.get(i).getFileName() + " masked successfully!!");
	    	        	gui_.show("File: " + fileList.get(i).getFileName() + " masked successfully!!");
	        		}
	        		catch(IOException ioe)
	    	  		{	
	        			ioe.printStackTrace();
	    	  		}

	    	  		long endTime   = System.currentTimeMillis();
	    	  		long totalTime = endTime - startTime;
	    	  		totalTime3 = (int) (totalTime3 + totalTime);
	    	  		System.out.println("Program excuted in: " + (totalTime) + " miliseconds.");
	    	  		gui_.show("Program excuted in: " + (totalTime) + " milisecond(s).");
		}
		
		try 
        {
				Desktop.getDesktop().open(new File(configPath[0]));
        } 
        catch (IOException e)
        {
        	// TODO Auto-generated catch block
				e.printStackTrace();
        }
		}
		System.out.println("TOTAL TIME: " + totalTime3);
		gui_.show("TOTAL TIME: " + totalTime3 + " miliseconds");
    }
    
    public GUI getGUI()
    {
    	return gui_;
    }
    
    /**
     * This method is used for the Oracle repository mask. It handles the matching of the non-sensitive documents and uses them in place of the
     * real file.
     */
    public void oracleRepositoryMask()
    {
    	gui_.show("Running Oracle Repository Mask...");
    	String[] contents = new File("Repository").list();
    	List<File> files = new ArrayList<File>();
		long startTime = System.currentTimeMillis();

    	for(int i=0;i < contents.length;i++)
    	{
    		files.add(new File("Repository/"+contents[i]));
    		System.out.println("Contents " + i + " " + contents[i]);
    	}
    	
    	BufferedReader in = null; 
    	BufferedReader in2 = null; 

    	try 
    	{
    		in = new BufferedReader(new FileReader("OutputFileConfig/IBM_TYPES.ini"));
    		in2 = new BufferedReader(new FileReader("OutputFileConfig/Extensions.ini"));

    	} 
    	catch (FileNotFoundException e) 
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
	    String str,str2;

	    List<String> list = new ArrayList<String>();
	    List<String> list2 = new ArrayList<String>();

	    try 
	    {
	    	while((str = in.readLine()) != null)
	    	{
		       list.add(str);
		       
	    	}
	    	while((str2 = in2.readLine()) != null)
	    	{
		       list2.add(str2); 
	    	}
	    } 
	    catch (IOException e) 
	    {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    }
	   
	    String ibmTypes [] = list.toArray(new String[0]);
	    String Extensions [] = list2.toArray(new String[0]);

    	/////////////////Gets the oracle connection which allows us to get the metadata
    	OracleCon oc2 = new OracleCon();  
    	oc2  = gui_.getOracleCon();
    	List <FileMetaData> metaList = new ArrayList<FileMetaData>(); 
    	
    	metaList = oc2.getFileList(); 
    	long totalTime3=0;
    	System.out.println("METALIST SIZE " + metaList.size());
    	if(metaList.size() != 0)
    	{
    		for(int i = 0; i < metaList.size();i++)
    		{
    			System.out.println("MetaList " + i + " - " + metaList.get(i).getFileName());
    			String fileName = metaList.get(i).getFilePath().replace("\\", "/");
    			file2 = new File(fileName);
    			createDirectoryOracle(fileName);
    			String fileType = null,b,c,d; 
    			//2d array for ibm name and then actual extension 
    			//for the length of the array 
    			//if filetype of the record matches one of the ibm array type then filetype
		        
			 
    			gui_.show("Scanning " +  metaList.get(i).getFileName());
    			
    			for(int k =0;k<ibmTypes.length;k++)
    			{
    				if(metaList.get(i).getFileType().equals(ibmTypes[k]))
    					fileType = Extensions[k];	 
    			}
    			
    			double sizeInBytes = 0;
    			int result = 0;
    			List<RepositoryCompare> resCompare = new ArrayList<RepositoryCompare>();
    			
    			for(int j = 0; j < files.size();j++)
    			{
        			oracleSize = Integer.parseInt(metaList.get(i).getFileSize());
    				sizeInBytes = oracleSize * 1024; 	
    				
    				if(sizeInBytes < files.get(j).length())
    				{
    				    result = (int) (files.get(j).length() - sizeInBytes);
    					resCompare.add(new RepositoryCompare(files.get(j).getName(),result));	
    				}
    				else
    				{
    					 result =  (int) (sizeInBytes - (int) files.get(j).length());
     					 resCompare.add(new RepositoryCompare(files.get(j).getName(),result));
    				}
    				
    			}
    			
    			//If aggressive match is selected we are going to remove all the files from the repository array list that don't have the same file type
    			if(gui_.chckbxAggressiveMatch.isSelected())
    			{
    				gui_.show("Aggressive Match Selected.");
    				List<RepositoryCompare> filterType = new ArrayList<RepositoryCompare>();
    				String ext = "";
    				for(int a = 0;a < resCompare.size();a++)
    				{
    	    	    	ext = FilenameUtils.getExtension(resPath[0]+"/"+resCompare.get(a).getFileName());
    	    	    	System.out.println("Comparing " + ext + " to " + fileType);
    					if(!ext.equals(fileType))
    					{
    						filterType.add(resCompare.get(a));
    					}
    				}
    				
    				for(RepositoryCompare res : filterType)
    				{
    					System.out.println("Filter -- " + res.getFileName());
    				}
    				resCompare.removeAll(filterType);
    				System.out.println(resCompare.size() + "<--- size");
    			}
    			// If the size is zero then there is no file in the repository that matches the records file type
    		    if(resCompare.size() == 0)
    			{
    				gui_.show("ERROR! - No non-sensitive file with file type ." + fileType + " found in repository");
    				gui_.show("");
    				gui_.show("As a result please add a non-sensitive file of type ." + fileType + " to be used in the masking process");
    				gui_.show("_____________________________________________________");

    			}
    			else if(resCompare.size() != 0)
    			{

    				resCompare.sort((o1, o2) -> Integer.compare(o1.getComparisonValue(),o2.getComparisonValue()));

    				for(RepositoryCompare res : resCompare)
    				{
    					System.out.println("--- "  + res.getFileName());
    				}

    				String attach = metaList.get(i).getFilePath().substring(3, metaList.get(i).getFilePath().length());
    				System.out.println("Path? -- " + path );
    				File p1,p2; 
    				p1= new File(resPath[0]+"/"+resCompare.get(0).getFileName());
    				String ext = FilenameUtils.getExtension(resPath[0]+"/"+resCompare.get(0).getFileName());
    				p2= new File(configPath[0]+"/"+attach+"/" + metaList.get(i).getFileName()+"."+ext);

    				try 
    				{
    					gui_.show("Using " + resCompare.get(0).getFileName() + " as masked file");
    					gui_.show("Attempting to copy...");
    					Files.copy(p1.toPath(),p2.toPath(), REPLACE_EXISTING);
    				} 
    				catch (IOException e) 
    				{
    					gui_.show(e.getMessage());
    				}
    				gui_.show("Copy Complete!");
    				gui_.show("___________________________________");
    				//createMaskedFileOracle(metaList.get(i).getFileName(),metaList.get(i).getFilePath(),fileType);
    				oracleFlag = true; 
    				oracleSize = Integer.parseInt(metaList.get(i).getFileSize());
    			}
    		}
    		
    	}
    	
    	long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		totalTime3 = (int) (totalTime3 + totalTime);
		System.out.println("Program excuted in: " + (totalTime) + " miliseconds.");
		gui_.show("File Masked in " + totalTime + " miliseconds");
    	try 
		{
			Desktop.getDesktop().open(new File(configPath[0]));
		}   	    
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		
    }
    
    /**
     * This method is used for the basic oracle file metadata mask in which the masked file is filled with 1s
     */
    public void oracleFileMask()
    {
    	//////////////////

    	if(gui_.chckbxRepositoryMask.isSelected())
    	{
    		oracleRepositoryMask();
    	}
    	else
    	{
    		BufferedReader in = null; 
    		BufferedReader in2 = null; 

    		try 
    		{
    			in = new BufferedReader(new FileReader("OutputFileConfig/IBM_TYPES.ini"));
    			in2 = new BufferedReader(new FileReader("OutputFileConfig/Extensions.ini"));

    		} 
    		catch (FileNotFoundException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		String str,str2;

    		List<String> list = new ArrayList<String>();
    		List<String> list2 = new ArrayList<String>();

    		try 
    		{
    			while((str = in.readLine()) != null)
    			{
    				list.add(str);

    			}
    			while((str2 = in2.readLine()) != null)
    			{
    				list2.add(str2); 
    			}
    		} 
    		catch (IOException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

    		String ibmTypes [] = list.toArray(new String[0]);
    		String Extensions [] = list2.toArray(new String[0]);

    		/////////////////
    		OracleCon oc2 = new OracleCon();  
    		oc2  = gui_.getOracleCon();
    		List <FileMetaData> metaList = new ArrayList<FileMetaData>(); 

    		metaList = oc2.getFileList(); 
    		long totalTime3=0;
    		System.out.println("METALIST SIZE " + metaList.size());
    		if(metaList.size() != 0)
    		{
    			for(int i = 0; i < metaList.size();i++)
    			{
    				String fileName = metaList.get(i).getFilePath().replace("\\", "/");
    				file2 = new File(fileName);
    				flag = true;
    				createDirectoryOracle(fileName);
    				String fileType = null,b,c,d; 
    				//2d array for ibm name and then actual extension 
    				//for the length of the array 
    				//if filetype of the record matches one of the ibm array type then filetype


    				for(int k =0;k<ibmTypes.length;k++)
    				{
    					if(metaList.get(i).getFileType().equals(ibmTypes[k]))
    						fileType = Extensions[k];	 
    				}


    				createMaskedFileOracle(metaList.get(i).getFileName(),metaList.get(i).getFilePath(),fileType);
    				oracleFlag = true; 
    				oracleSize = Integer.parseInt(metaList.get(i).getFileSize());
    				double sizeInBytes = oracleSize * 1024; 			
    				qCount = 0; 
    				mbCount = 0; 
    				kbCount = 0; 
    				bCount = 0;  	 

    				long startTime = System.currentTimeMillis();
    				double leftOverMb = 0; 
    				double kbToFill = 0;  
    				double byteToFill = 0; 
    				double numOfMbFill = 0; 
    				double numOf4MbFill = 0;
    				double FOUR_MB = 4194304;
    				double file2L = sizeInBytes;
    				double kbL = (double) ONE_MB;
    				double tmpFileL = sizeInBytes;
    				double takeQBytes = 0;
    				double x = 0; 
    				int qBytesFill = 0;
    				FileInputStream instream = null;
    				FileInputStream instream2 = null;
    				FileInputStream instream3 = null;
    				FileOutputStream outstream = null;

    				System.out.println("File length " + file2.length() + " bytes.");    	 
    				numOf4MbFill = file2L / FOUR_MB; 
    				System.out.println("Number of 4mb to fill: " + numOf4MbFill); 
    				qBytesFill = (int) numOf4MbFill;
    				x = FOUR_MB * qBytesFill;
    				takeQBytes = (tmpFileL - x );
    				System.out.println("Final mb to fill: " + takeQBytes);
    				numOfMbFill =  takeQBytes /kbL ;   
    				System.out.println("Number of megabytes to fill: " + numOfMbFill + ".");
    				int mbFill = (int)numOfMbFill; 
    				int quadFill = mbFill / 4; 
    				System.out.println("Quad Fill... " + quadFill);
    				leftOverMb = numOfMbFill - mbFill;	   
    				System.out.println("Left over mb "+leftOverMb); 
    				kbToFill = leftOverMb * 1024;  
    				System.out.println("kb to fill " + kbToFill);
    				int kbOutFill = (int) kbToFill;
    				System.out.println("Kilobytes to fill " + kbOutFill);
    				byteToFill = kbToFill - kbOutFill;
    				int bytesFill = (int) (byteToFill * 1024); 

    				/////////////////////////////////////
    				//If file is at megabyte level then a filler file will be used to fill the file until it can filled with bytes only 
    				//This might speed up the process already having a 1mb fill file 
    				gui_.show("MASKING FILE: " + metaList.get(i).getFileName());

    				try
    				{
    					while(qCount != qBytesFill)
    					{ 
    						File infile =new File("MaskFillFiles/4mbFill.txt");

    						instream = new FileInputStream(infile);

    						outstream = new FileOutputStream(outfile,true);

    						byte[] buffer = new byte[2048];

    						int length;
    						/*copying the contents from input stream to
    						 * output stream using read and write methods
    						 */
    						while ((length = instream.read(buffer)) > 0)
    						{
    							outstream.write(buffer, 0, length); 		
    						}
    						qCount++;
    					}

    					while(mbCount != mbFill)
    					{ 

    						File infile =new File("MaskFillFiles/1mbFill.txt");
    						File infile2 = new File("MaskFillFiles/1kbFill.txt");

    						instream = new FileInputStream(infile);
    						instream2 = new FileInputStream(infile2);

    						outstream = new FileOutputStream(outfile,true);

    						byte[] buffer = new byte[2048];

    						int length;
    						/*copying the contents from input stream to
    						 * output stream using read and write methods
    						 */

    						while ((length = instream.read(buffer)) > 0)
    						{
    							outstream.write(buffer, 0, length);    	
    						}
    						mbCount++;
    					}

    					while(kbCount != kbOutFill)
    					{
    						File infile3 = new File("MaskFillFiles/1kbFill.txt");
    						outstream = new FileOutputStream(outfile,true);

    						byte[] buffer = new byte[2048];

    						int length;
    						instream2 = new FileInputStream(infile3);
    						/*copying the contents from input stream to
    						 * output stream using read and write methods
    						 */
    						while ((length = instream2.read(buffer)) > 0)
    						{
    							outstream.write(buffer, 0, length);	
    						}	       	       
    						kbCount++;
    					}

    					while(bCount != bytesFill)
    					{	
    						//System.out.println("Filling bytes..." + count2);
    						File infile3= new File("MaskFillFiles/1bFill.txt");

    						instream3 = new FileInputStream(infile3);

    						outstream = new FileOutputStream(outfile,true);

    						byte[] buffer = new byte[2048];

    						int length;
    						/*copying the contents from input stream to
    						 * output stream using read and write methods
    						 */

    						while ((length = instream3.read(buffer)) > 0)
    						{
    							outstream.write(buffer, 0, length);	
    						}	

    						bCount++;
    					}

    					//Closing the input/output file streams
    					if(qCount > 0 || mbCount > 0)
    						instream.close();

    					outstream.close();

    					System.out.println("File copied successfully!!");
    				}

    				catch(IOException ioe)
    				{
    					ioe.printStackTrace();
    				}






    				long endTime   = System.currentTimeMillis();
    				long totalTime = endTime - startTime;
    				totalTime3 = (int) (totalTime3 + totalTime);
    				System.out.println("Program excuted in: " + (totalTime) + " miliseconds.");
    				gui_.show("File Masked in " + totalTime + " miliseconds");
    				gui_.show("_________________________________________");



    			}
    		}
    		else 
    		{
    			gui_.show("Error could not mask metaList size is zero");
    			if(gui_.isOracleDb())
    			{
    				gui_.show("Check Oracle Credentials");
    			}
    		}

    		try 
    		{
    			Desktop.getDesktop().open(new File(configPath[0]));
    		}   	    
    		catch (IOException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

    		JOptionPane.showMessageDialog(null, "Total Masking Time: " +totalTime3 + " Miliseconds", "InfoBox: " + "Message", JOptionPane.INFORMATION_MESSAGE);

    		gui_.show("Total Masking Time: " +totalTime3 + " Miliseconds");
    	}
    }
    
    /**
     * The following method handles reading a csv file and masking a field file that is a directory
     * @throws IOException
     */
    public void csvFileRunDir() throws IOException 
    {       	 
    	long startTime = System.currentTimeMillis();
    	int totalTime3 = 0;
    	CSVReader reader = new CSVReader(new FileReader(file2), ',','"','|');
		fileList2 = new ArrayList<FileDirectory>();
		
		// read line by line
		String[] record = null;
		//Gets records delimited by a comma and sets them to the File class
		//From this the goal is to generate a list of file objects that can be read and masks
		int j = 0;
		while ((record = reader.readNext()) != null)
		{
			FileDirectory f = new FileDirectory();
			f.setDriverName(record[0]);//f.setFileName("XXXXXXXX_" + j); Mask file name to not be identified
			f.setDriverNumber(record[1]);
			f.setDriverAddress(record[2]); //f.setFileSize(1048576); Option is file size is not given
			f.setMrAttatchmentDir(record[3]);

			fileList2.add(f);
			System.out.println(fileList);
			j++;
		}

		reader.close();
		
	    fileList2.remove(0);
		for(int i = 0; i < fileList2.size();i++)
		{
			String fileName = fileList2.get(i).getMrAttachmentDir().replace("\\", "/");
			file2 = new File(fileName);
			flag = true;
			singleFileRun(file2);
			 
			
		}
	    	  		long endTime   = System.currentTimeMillis();
	    	  		long totalTime = endTime - startTime;
	    	    		totalTime3 = (int) (totalTime3 + totalTime);
	    	 
		
		
		try 
        {
				Desktop.getDesktop().open(new File(configPath[0]));
        } 
		
        catch (IOException e)
        {
        	// TODO Auto-generated catch block
				e.printStackTrace();
        }
    
		System.out.println("TOTAL TIME: " + totalTime3);
		gui_.show("TOTAL TIME: " + totalTime3 + " miliseconds");
    }
    
    
    
    
    
    public void run()
    {
         if(gui_.isFullDirectory())
         {      
        	gui_.show("MULTI FILE MASKING...");
        	createDirectory();
        	multiFileRun();
         }
         
         if(gui_.isOracleDb())
         {
        	System.out.println("ORACLE DATABASE FILE MASKING...");
        	
        	oracleFileMask();
        	
        	
         }
         if(gui_.isSingleFile())
         {
        	 gui_.show("SINGLE FILE MASKING...");
        	 createDirectory();
        	 System.out.println("SINGLE SELECTED.");
        	 File file = file2;
        	 singleFileRun(file);
         }
         
         if(gui_.isCsvFile())
         {
         	 gui_.show("CSV FILE MASKING...");
        	 createDirectory();
        	 try 
        	 {
				csvFileRun();
			 } 
        	 catch (IOException e) 
        	 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
         } 
         if(gui_.isCsvFileDir())
         {
        	 gui_.show("CSV FILE MASKING (DIRECTORY)...");
        	 createDirectory();
        	 try 
        	 {
				csvFileRunDir();
			 } 
        	 catch (IOException e) 
        	 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
         }
    }
    
    /** 
     * The following example was used to create the source files needed to fill the file(s) to be masked (1bFill,1kbFill,etc.)
     * This was the original way the program was going to mask files but the time to mask was exponential.
     */
     public static void adminCreateSrc()
	 {
		 File file = new File("C:/Users/PierreMi/Documents/Masked/asd87.txt");
		 try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) 
		 {
			 while (file.length() < 1) 
			 {
				 writer.write("1");
				 writer.flush();
			 }
       
			 System.out.println("1 KB Data is written to the file.! " + file.length());
		 } 
		 catch (IOException e) 
		 {
			 e.printStackTrace();
		 }
	 }
    /**
     * Initializes the GUI which essentially holds the program together along with its functions
     * @param args
     */
    public static void main(String args[])
	{		
        gui_.initialize(); 
	}
}
