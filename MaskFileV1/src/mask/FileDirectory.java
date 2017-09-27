package mask;

/**
 *
 * 
 * @author Michael Pierre
 *
 *	This class mimics the format of a table with an attachment file being a directory.
 *	The class is primarily used to make a list of object that the attachment directory can be read from. 
 */
public class FileDirectory
{
	private String driverName_; 
	private String dlNumber_; 
	private String driverAddress_; 
	private String mrAttachmentDir_;
	
	public FileDirectory()
	{
		
	}
	
	public void setDriverName(String driverName)
	{
		driverName_ = driverName;	
	}
	
	public void setDriverNumber(String dlNumber)
	{
		dlNumber_ = dlNumber; 
	}
	
	public void setDriverAddress(String driverAddress)
	{
		driverAddress_ = driverAddress;
	}
	
	public void setMrAttatchmentDir(String mrAttachmentDir)
	{
		mrAttachmentDir_ = mrAttachmentDir; 
	}
	
    public String getMrAttachmentDir()
    {
    	return mrAttachmentDir_;
    }
}
