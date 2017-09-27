package mask;


/**
 * This class represents a repository comparison. This class is used in the comparison process to store the file name of the non-sensitive document
 * as well as its comparison value. The comparison value is the result of subtracting the size of the records file size (metadata) from the repositorys 
 * document size. The smallest comparison value is the non-sensitive document used
 * @author Michael Pierre
 *
 */
public class RepositoryCompare
{

	private String fileName_; 
	private Integer comparisonValue_; 
	
	public RepositoryCompare(String fileName, Integer comparisonValue)
	{
		fileName_ = fileName; 
		comparisonValue_ = comparisonValue; 
		
	}
	
	public String getFileName()
	{
		return fileName_;
	}
	
	public int getComparisonValue()
	{
		return comparisonValue_; 
	}
}
