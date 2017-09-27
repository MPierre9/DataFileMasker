package mask;


/**
 * This classes purpose is to represent record from an oracle table. It only gets the essential information needed to create a masked file 
 * which is file name, path, type, and size. The application can work with a table with 10 or however much columns but only needs these 4 things
 * to function. The FileMetaData class allows us to easily access information about the record. 
 * 
 * Is used in a ArrayList 
 * @author Michael Pierre
 *
 */
public class FileMetaData 
{

	private String fileName_; 
	private String filePath_; 
	private String fileSize_;  
	private String fileType_; 
	
	public FileMetaData()
	{
		
	}
	
	public String getFileName()
	{
		return fileName_;
	}
	
	public void setFileName(String fileName)
	{
		fileName_ = fileName; 
	}
	
	public String getFilePath()
	{
		return filePath_;
	}
	
	public void setFilePath(String filePath)
	{
		filePath_ = filePath;
	}
	
	public String getFileSize()
	{
		return fileSize_; 
	}
	
	public void setFileSize(String fileSize)
	{
		fileSize_ = fileSize; 
	}
	
	public String getFileType() 
	{
		return fileType_;
	}
	
	public void setFileType(String fileType)
	{
		fileType_ = fileType;
	}
	
}
