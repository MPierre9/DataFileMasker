How folder "Repository" works 
_____________________________


repositoryConfig sets the path where the repository is located. The purpose of the repository 
is to have a storage area of non-sensitive documents of various types that can be used by the 
application to use the non-sensitive document in place of the real one. 


For example: Say there is a record in the filemetadata2 table with attribute as follows 

FILE_NAME: AAA999.AAA
FILE_PATH: C:\PDF\MED_REC
FILE_SIZE: 120
FILE_TYPE: PDF


The application would match the most relevant document from the repository to "mask" the actual document.

There are two matching options. 


Aggressive match: Which will find the document based on file type and size


Non-Aggressive match: If Aggr. Match is unchecked then the application will match on size alone 
                      and will disregard the file type.






The proper way to setup repositoryConfig is as follows: 

\\OND2C00886317\MichaelPierre\Michaels Deliverables\DataFileMaskerV2\Repository