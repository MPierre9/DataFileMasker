package mask;

public class MaskFile 
{
    private String fileName_; 
    private String fileType_; 
    private String fileSize_; 
	
    public MaskFile() 
    {
    	
    }
    
    public MaskFile(String fileName, String fileType, String fileSize)
	{
		fileName = fileName_; 
		fileType = fileType_;
		fileSize = fileSize_; 
	}
    
    public String getFileName()
    {
    	return fileName_;
    }
    
    public void setFileName(String fileName)
    {
    	fileName_ = fileName; 
    }
    
    public String getFileType()
    {
    	return fileType_; 
    }
    
    public void setFileType(String fileType)
    {
    	fileType_ = fileType;
    }
    
    public String getFileSize()
    {
    	return fileSize_; 
    }
    
    public void setFileSize(String fileSize)
    {
    	fileSize_ = fileSize;
    }
    
    @Override
    public String toString()
    {
    	return "File: " + getFileName() + getFileType() + " File Size: " + getFileSize();
    }
    
    
	
	
	
}
