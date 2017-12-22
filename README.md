# DataFileMasker
A Java application designed to mask Oracle Database Files and create sample production data. . Developed and maintained by Michael Pierre.

![DataFileMaskerPreview](https://i.imgur.com/LfWzv93.jpg)

**Summary**: The Data File Masker (DFM) is a program that masks data files of any type (jpg, pdf, txt, etc.) to the exact same size, name, and type. The primary purpose of this program is to mask data files so consumer privacy is protected when working with the data. There are currently six options to mask files: 
1.	Single File Mask:
2.	Directory of Files Mask:
3.	CSV File Metadata Mask 
4.	CSV File Metadata (DIR) Mask
5.	Oracle File Metadata Mask 
6.	Oracle File Metadata Mask using repository

Each option has its specific use case and can be only used in certain situations. 

**Note**: Option five listed above will mask the file using binary 1s. The good thing about this option is it creates a file of the exact same size, type, and name. However, when you open the file it will open to just 1s. Option six listed above does almost the same thing however instead of using 1s to create the masked file it uses a repository of non-sensitive documents to use in place of the sensitive documents. The good thing about this option is you can open the file and it shows some relevant data however the exact size of the real file compared to the new “masked” file will be off. 
