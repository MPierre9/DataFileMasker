dbConnect.ini Format Guide 
__________________________

The config file is read as follows: 

BEGINNING OF FILE
username
password
host
port
serviceName
tableName
numberOfRows (to mask, if 0 all rows are masked (mostly for testing))



ColumnConfig.ini Format Guide 
_____________________________ 

There are four fundamental attributes the Data File Masker depends on. File name, path, size, and type. If these four attributes 
are given the Data File Masker can work with any table 

The format of the config file is read as follows: 

BEGINNING OF FILE 
FILE_NAME
FILE_PATH (C:\Example)
FILE_SIZE (number only)
FILE_TYPE (ex. pdf,csv,doc,tiff,txt,etc.)
END

________________

Enter the corresponding column names of the table you wish to mask files from and it will be done. For example a 8 column table 
may look like. 

DRV_NAME
ATTCHFLE_PATH
SIZE
FLE_TYPE 

Don't include the other 4 column names because we only need those 4 attributes.