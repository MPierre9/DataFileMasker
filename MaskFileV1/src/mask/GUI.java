package mask;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.SystemColor;

/**
 * 
 * @author Michael Pierre
 * 
 * This class creates the GUI that is used to invoke the file masking methods. Designed for ease of use and program efficiency 
 * this GUI holds all the options the program offers which allows for quick file masking and other use.
 *
 */
public class GUI implements Runnable
{

	private JFrame frmDataFileMasker;
	private JTextField filename; 
	private JTextArea textArea = new JTextArea(5, 20);
	private JRadioButton rdbtnSingleFile;
	private JRadioButton rdbtnDirectory;
	private JRadioButton rdbtnFileMetadataCsv ;
	private JRadioButton rdbtnFileMetadataCsvdir;
	private JRadioButton rdbtnOracleMetadataMask;
	private Instructions i = new Instructions();
	private Thread runner;
	private JButton maskButton;
    private volatile boolean isRunning = true;
	private String file;
	private Mask t;
	private JLabel loading;
	private JButton searchButton;
	private JTextField oracleUsername;
	private JTextField oraclePassword;
	private JTextField oracleHost;
	private JTextField oraclePort;
	private JTextField oracleServiceName;
	private JTextField oracleTableName;
	private String oracleNumberOfRows;
	public JCheckBox chckbxRepositoryMask;
	public JCheckBox chckbxAggressiveMatch;
	OracleCon oc;
	JCheckBox chckbxUseConfigFile;
	/**
	 * Launch the application.
	 */
	public void create()
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try
				{
					GUI window = new GUI();
					window.frmDataFileMasker.setVisible(true);
				

				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});	
	
	}

	/**
	 * Creates the application GUI (only used for development purposes. Primarily Eclipse window builder).
	 */
	
	public GUI() 
	{
	//	initialize();
	}
	/**
	 * Initialize the contents of the frame. 
	 * @throws IOException 
	 */
	void initialize()
	{	
		ButtonGroup group = new ButtonGroup();
		frmDataFileMasker = new JFrame();
		frmDataFileMasker.setTitle("Data File Masker V2 - Ministry of Transportation");
		frmDataFileMasker.setBounds(100, 100, 790, 600);
		frmDataFileMasker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDataFileMasker.getContentPane().setLayout(null);
		JScrollPane scrollPane = new JScrollPane(); 
		scrollPane.setBounds(323, 106, 441,445);
		frmDataFileMasker.getContentPane().add(scrollPane);
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);

		
	    maskButton = new JButton("Mask");
		maskButton.setBounds(77, 528, 109, 23);
		frmDataFileMasker.getContentPane().add(maskButton);
		
		filename = new JTextField();
		filename.setBounds(18, 59, 253, 20);
		frmDataFileMasker.getContentPane().add(filename);
		
		filename.setColumns(10);
		
		
		searchButton = new JButton("Search");
		searchButton.setBounds(281, 58, 78, 23);
		frmDataFileMasker.getContentPane().add(searchButton);

		chckbxAggressiveMatch = new JCheckBox("Aggr. Match");
		chckbxAggressiveMatch.setBounds(201, 332, 116, 23);
		chckbxAggressiveMatch.setBackground(SystemColor.controlHighlight);
		frmDataFileMasker.getContentPane().add(chckbxAggressiveMatch);
		chckbxAggressiveMatch.setVisible(false);

		
	    rdbtnSingleFile = new JRadioButton("Single file");
	    rdbtnSingleFile.setBackground(SystemColor.controlHighlight);
	    rdbtnSingleFile.addChangeListener(new ChangeListener() 
	    {	
	       public void stateChanged(ChangeEvent e)
	       {   
	    	   try 
	    	   { 
	    		   if(isSingleFile())
	    		   textArea.setText(i.instrSingleFile());  	    		    			  			   
	    	   } 
	    	
	    	   catch (IOException e1) 
	    	   {
	    		   // TODO Auto-generated catch block
	    		   e1.printStackTrace();
	    	   }
	    	}
	    });
		
	    rdbtnSingleFile.setBounds(77, 306, 109, 23);
		rdbtnSingleFile.setSelected(true);
	

		group.add(rdbtnSingleFile);

		frmDataFileMasker.getContentPane().add(rdbtnSingleFile);
		
		rdbtnDirectory = new JRadioButton("Directory of files");
		rdbtnDirectory.setBackground(SystemColor.controlHighlight);
	 
		rdbtnDirectory.addChangeListener(new ChangeListener() 
	    {	
	       public void stateChanged(ChangeEvent e)
	       {   
	    	   try 
	    	   { 
	    		   if(isFullDirectory())
	    		   textArea.setText(i.instrDirectory());  	    		    			  			   
	    	   } 
	    	
	    	   catch (IOException e1) 
	    	   {
	    		   // TODO Auto-generated catch block
	    		   e1.printStackTrace();
	    	   }
	    	}
	    });
	    
		rdbtnDirectory.setBounds(77, 332, 131, 23);

		group.add(rdbtnDirectory);
		
		frmDataFileMasker.getContentPane().add(rdbtnDirectory);
		
	    rdbtnFileMetadataCsv = new JRadioButton("File metadata csv");
	    rdbtnFileMetadataCsv.setBackground(SystemColor.controlHighlight);
	    rdbtnFileMetadataCsv.addChangeListener(new ChangeListener() 
	    {	
	       public void stateChanged(ChangeEvent e)
	       {   
	    	   try 
	    	   { 
	    		   if(isCsvFile())
	    		   textArea.setText(i.instrCsv());  	    		    			  			   
	    	   } 
	    	
	    	   catch (IOException e1) 
	    	   {
	    		   // TODO Auto-generated catch block
	    		   e1.printStackTrace();
	    	   }
	    	}
	    });
		rdbtnFileMetadataCsv.setBounds(77, 358, 131, 23);
		group.add(rdbtnFileMetadataCsv);

		frmDataFileMasker.getContentPane().add(rdbtnFileMetadataCsv);
		
		JLabel lblMaskingOptions = new JLabel("Masking Options:");
		lblMaskingOptions.setBounds(65, 268, 121, 31);
		frmDataFileMasker.getContentPane().add(lblMaskingOptions);
	    
	    JLabel lblNewLabel_1 = new JLabel("");
	    lblNewLabel_1.setIcon(new ImageIcon(GUI.class.getResource("/mask/rsz_mto (1).png")));
	    lblNewLabel_1.setBounds(518, 0, 256, 100);
	    frmDataFileMasker.getContentPane().add(lblNewLabel_1);
	    
	    JLabel lblNewLabel = new JLabel("Data File Masker V2");
	    lblNewLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
	    lblNewLabel.setBounds(49, 11, 207, 36);
	    frmDataFileMasker.getContentPane().add(lblNewLabel);
	    
	    JLabel lblNewLabel_2 = new JLabel("");
	    lblNewLabel_2.setIcon(new ImageIcon(GUI.class.getResource("/mask/rsz_1rsz_fadinglineblank.png")));
	    lblNewLabel_2.setBounds(30, 440, 228, 31);
	    frmDataFileMasker.getContentPane().add(lblNewLabel_2);
	    
	    loading = new JLabel("");
	    loading.setIcon(new ImageIcon(GUI.class.getResource("/mask/load2.gif")));
	    loading.setBounds(96, 450, 99, 83);
	    loading.setVisible(false);
	    frmDataFileMasker.getContentPane().add(loading);
	    
	    rdbtnFileMetadataCsvdir = new JRadioButton("File metadata csvDir");
	    rdbtnFileMetadataCsvdir.setBackground(SystemColor.controlHighlight);
	    rdbtnFileMetadataCsvdir.addChangeListener(new ChangeListener() {
	    	public void stateChanged(ChangeEvent e) {
	    		
	    		 try 
		    	   { 
		    		   if(isCsvFileDir())
		    		   textArea.setText(i.instrCsvFileDir());  	    		    			  			   
		    	   } 
		    	
		    	   catch (IOException e1) 
		    	   {
		    		   // TODO Auto-generated catch block
		    		   e1.printStackTrace();
		    	   }
	    	}
	    });
		
	    group.add(rdbtnFileMetadataCsvdir);
	    rdbtnFileMetadataCsvdir.setBounds(77, 384, 147, 23);
	    frmDataFileMasker.getContentPane().add(rdbtnFileMetadataCsvdir);
	    
	    rdbtnOracleMetadataMask = new JRadioButton("Oracle Metadata mask");
	    rdbtnOracleMetadataMask.setBackground(SystemColor.controlHighlight);
	    rdbtnOracleMetadataMask.addChangeListener(new ChangeListener() 
	    {
	    	public void stateChanged(ChangeEvent arg0) 
	    	{
	    		
	    		 try 
		    	 { 
		    		   if(isOracleDb())
		    		   {
		    			   chckbxUseConfigFile.setVisible(true);
		    			   chckbxRepositoryMask.setVisible(true);
		    			   textArea.setText(i.instrOracleDb()); 
		    		   }
		    		   else
		    		   {
		    			   chckbxUseConfigFile.setVisible(false);
		    			   chckbxRepositoryMask.setVisible(false);
		    			   chckbxAggressiveMatch.setVisible(false);

		    		   }
		    	 } 
		    	
		    	 catch (IOException e1) 
		    	 {
		    		   e1.printStackTrace();
		    	 }
	    	}
	    });
	    group.add(rdbtnOracleMetadataMask);
	    rdbtnOracleMetadataMask.setBounds(77, 410, 165, 23);
	    frmDataFileMasker.getContentPane().add(rdbtnOracleMetadataMask);
	    
	    oracleUsername = new JTextField();
	    oracleUsername.setColumns(10);
	    oracleUsername.setBounds(130, 90, 141, 20);
	    frmDataFileMasker.getContentPane().add(oracleUsername);
	    
	    
	    chckbxRepositoryMask = new JCheckBox("Use Repository");
	    chckbxRepositoryMask.addChangeListener(new ChangeListener() 
	    {
	    	public void stateChanged(ChangeEvent arg0)
	    	{
	    		if(chckbxRepositoryMask.isSelected())
	    			chckbxAggressiveMatch.setVisible(true);
	    		
	    		else
	    			chckbxAggressiveMatch.setVisible(false);
	    			
	    	}
	    });
	    chckbxRepositoryMask.setBounds(201, 306, 116, 23);
	    frmDataFileMasker.getContentPane().add(chckbxRepositoryMask);
	    chckbxRepositoryMask.setBackground(SystemColor.controlHighlight);
	    chckbxRepositoryMask.setVisible(false);
	
	    
	    oraclePassword = new JPasswordField();
	    oraclePassword.setColumns(10);
	    oraclePassword.setBounds(130, 121, 141, 20);
	    frmDataFileMasker.getContentPane().add(oraclePassword);
	    
	    oracleHost = new JTextField();
	    oracleHost.setColumns(10);
	    oracleHost.setBounds(130, 152, 141, 20);
	    frmDataFileMasker.getContentPane().add(oracleHost);
	    
	    JLabel lblUsername = new JLabel("UserName");
	    lblUsername.setBounds(28, 93, 70, 14);
	    frmDataFileMasker.getContentPane().add(lblUsername);
	    
	    JLabel lblPassword = new JLabel("Password");
	    lblPassword.setBounds(28, 124, 70, 14);
	    frmDataFileMasker.getContentPane().add(lblPassword);
	    
	    JLabel lblHost = new JLabel("Host");
	    lblHost.setBounds(30, 155, 46, 14);
	    frmDataFileMasker.getContentPane().add(lblHost);
	    
	    oraclePort = new JTextField();
	    oraclePort.setColumns(10);
	    oraclePort.setBounds(130, 183, 141, 20);
	    frmDataFileMasker.getContentPane().add(oraclePort);
	    
	    JLabel lblPort = new JLabel("Port");
	    lblPort.setBounds(30, 186, 46, 14);
	    frmDataFileMasker.getContentPane().add(lblPort);
	    
	    oracleServiceName = new JTextField();
	    oracleServiceName.setColumns(10);
	    oracleServiceName.setBounds(130, 214, 141, 20);
	    frmDataFileMasker.getContentPane().add(oracleServiceName);
	    
	    JLabel lblServiceName = new JLabel("Service Name");
	    lblServiceName.setBounds(30, 217, 90, 14);
	    frmDataFileMasker.getContentPane().add(lblServiceName);
	    
	    oracleTableName = new JTextField();
	    oracleTableName.setColumns(10);
	    oracleTableName.setBounds(130, 245, 141, 20);
	    frmDataFileMasker.getContentPane().add(oracleTableName);
	    
	    JLabel lblTableName = new JLabel("Table Name");
	    lblTableName.setBounds(30, 248, 70, 14);
	    frmDataFileMasker.getContentPane().add(lblTableName);
	    
	    chckbxUseConfigFile = new JCheckBox("Use config file");
	    chckbxUseConfigFile.setBackground(SystemColor.controlHighlight);
	    chckbxUseConfigFile.setVisible(false);
	    chckbxUseConfigFile.addChangeListener(new ChangeListener() 
	    {
	    	public void stateChanged(ChangeEvent arg0) 
	    	{
	    		   BufferedReader in = null;
					try 
					{
						in = new BufferedReader(new FileReader("DbConnect/dbConnect.ini"));
					}
					catch (FileNotFoundException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			   String str;

	    			   List<String> list = new ArrayList<String>();
	    			   try {
						while((str = in.readLine()) != null){
						       list.add(str);
						   }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			   	
	    			   String[] config = list.toArray(new String[0]);
	    			   if(isConfig() && isOracleDb())
	    			   {
	    				   oracleUsername.setText(config[0]);
	    				   oraclePassword.setText(config[1]);
	    				   oracleHost.setText(config[2]);
	    				   oraclePort.setText(config[3]);
	    				   oracleServiceName.setText(config[4]);
	    				   oracleTableName.setText(config[5]);
	    			   }
	    			   else if(!isConfig() || !isOracleDb())
	    			   {
	    				   oracleUsername.setText("");
	    				   oraclePassword.setText("");
	    				   oracleHost.setText("");
	    				   oraclePort.setText("");
	    				   oracleServiceName.setText("");
	    				   oracleTableName.setText("");
	    			   }
	    			   
	    	}
	    });
	    chckbxUseConfigFile.setBounds(201, 272, 109, 23);
	    frmDataFileMasker.getContentPane().add(chckbxUseConfigFile);
	    
	    JLabel lblBackground = new JLabel("background");
	    lblBackground.setIcon(new ImageIcon(GUI.class.getResource("/mask/white background.jpg")));
	    lblBackground.setBounds(0, 0, 774, 562);
	    frmDataFileMasker.getContentPane().add(lblBackground);
	    
	   
	   
		frmDataFileMasker.setVisible(true);
		
		/**
		 * Button listener for searchButton. If the search button is clicked the following method will determine which radio option is 
		 * selected and allow files for that corresponding option. For example: if the full directory masking option is selected then 
		 * the user can only select directories instead of files
		 */
		searchButton.addActionListener(new ActionListener() 
		{
				public void actionPerformed(ActionEvent ae) 
				{
					if(isSingleFile() || isCsvFile() || isCsvFileDir() || isOracleDb())
					{
						JFileChooser chooser = new JFileChooser();
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						int option = chooser.showOpenDialog(frmDataFileMasker);
						if (option == JFileChooser.APPROVE_OPTION)
						{
							filename.setText(((chooser.getSelectedFile()!=null)?
										       chooser.getSelectedFile().getAbsolutePath():"nothing"));
						}	 
					}
					else if(isFullDirectory())
					{
						JFileChooser chooser = new JFileChooser();
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						int option = chooser.showOpenDialog(frmDataFileMasker);
						if (option == JFileChooser.APPROVE_OPTION)
						{
							filename.setText(((chooser.getSelectedFile()!=null)?
										       chooser.getSelectedFile().getAbsolutePath():"nothing"));
						}
					}


				}
		});	
		
		/** 
		 * button listener for variable maskButton. If the button is pressed validation is performed and then the masking thread is
		 * started.
		 * 
		 */
		maskButton.addActionListener(new ActionListener() 
	    {
	      public void actionPerformed(ActionEvent ae) 
	      {  
	    	  String line = filename.getText();
	    	  String [] splitLine = line.split("\\.");
	    	  File t = new File(filename.getText());
	    	  if(isSingleFile() || isFullDirectory() || isCsvFile() || isCsvFileDir())
	    	  {
	    		if(filename.getText().equals(""))  
	      	  	{
	    		  textArea.setText("ERROR! Search bar is NULL");
	      	  	}
	    		else if(isFullDirectory())
	    		{
		    		  file = filename.getText();
		    		  File f = new File(file);	
		    		  if(!f.isDirectory())
		    		  {
		    			  textArea.setText("ERROR! Must be a directory not a file");
		    		  }
		    		  
	    		}
	    	  	else if(t.exists() &&( isCsvFile() ||isCsvFileDir()))
	    	  	{
	    	  		if(isCsvFile() && !splitLine[1].equals("csv") )
	    		  textArea.setText("ERROR! Must be a csv file");
	    	  	}
	    	  	else 
	    	  	{
	    	  	
	    		  System.out.println("clicked");
	    		  
	    		  file = filename.getText();
		    		File f = new File(file);
		    		if(f.exists())
		    		{
		    			maskButton.setEnabled(false);
		    		    loading.setVisible(true);
		    		   
		    		    
		    			RunnableThread("Run Process");
		    		}
		    		else
		    			textArea.setText("ERROR! FILE/DIRECTORY DOES NOT EXIST. PLEASE SELECT FILE/DIRECTORY THAT EXISTS");
	    	  	}
	    	  }
	    	  
	    	  else if(isOracleDb())
	    	  { 
	    		   if(isConfig())
	    		   {
	    			   BufferedReader in = null;
					try {
						in = new BufferedReader(new FileReader("DbConnect/dbConnect.ini"));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			   String str;

	    			   List<String> list = new ArrayList<String>();
	    			   try {
						while((str = in.readLine()) != null){
						       list.add(str);
						   }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	    			   String[] config = list.toArray(new String[0]); 
	    			   oc = new OracleCon(); 
		    		   oc.setNumberOfRows(config[6]);
						
		    		   try{
		    			   oc.initiate(config[0], config[1], config[2], config[3], config[4], config[5]);
		    		   }
		    		   catch(Exception e)
		    		   {
		    			   show("EXCEPTION ERRROR: " + e.getMessage());
		    		   }

			
				//SELECT SHOULD BE HEEEERE   
	    			   maskButton.setEnabled(false);
	    			   loading.setVisible(true);
	    			   file = filename.getText(); 
	    			   textArea.setText(null);
	    			   show("Connecting to Oracle Database " + config[4] + "...");
	    			   show("Checking Credentials....");
	    			   
	    			   RunnableThread("Run Process");
	    		   }
	    		   else if(!oracleUsername.getText().equals("") || !oraclePassword.getText().equals("") || !oracleHost.getText().equals("") || !oraclePort.getText().equals("") ||  !oracleServiceName.getText().equals("") || !oracleTableName.getText().equals(""))
	    		   {
	    			   System.out.println("Oracle connect manual");
	    			   oc = new OracleCon();
	    			   oc.setNumberOfRows("0");
	    		       boolean flag = true;
	    			
	    		
	    		       try {
						oc.initiate(oracleUsername.getText(),
								       oraclePassword.getText(),
								       oracleHost.getText(),
								       oraclePort.getText(),
								       oracleServiceName.getText(),
								       oracleTableName.getText());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						flag = false;
					}
	    			
	    			
		
	    			   if(flag)
	    			   {
	    				   maskButton.setEnabled(false);
	    				   loading.setVisible(true);
	    				   file = filename.getText(); 
	    				   textArea.setText(null);
	    				   show("Connecting to Oracle Database ");
	    				   RunnableThread("Run Process"); 
	    			   }
	    			   else
	    			   {
	    				   textArea.setText("");
	    				   show("ERROR Oracle credentials are incorrect!");
	    			   }
	    		   }
	    		   else
	    			  textArea.setText("ERROR! INVALID ORACLE CREDENTIALS");
	    		   
	    	  }
	    	  
	      }
	    });
	}
	public OracleCon getOracleCon()
	{
		return oc;
	}
	/**
	 * This thread allows the gui to display message box messages without the gui freezing 
	 * @param threadName
	 */
	public void RunnableThread(String threadName)
	{
		runner = new Thread(this, threadName); 
		textArea.setText("");
		runner.start();
		
	}
	
	/**
	 * Starts the thread and creates a Test object that will decide the masking method to be run.
	 */
	public void run()
	{ 
		//while(!Thread.currentThread().isInterrupted())
	//	{
		t = new Mask(file);
	    t.run();
 	    maskButton.setEnabled(true);
	    loading.setVisible(false);
	    textArea.append("DONE!");
	//	}
	}
	
	
	/** 
	 * Shows whatever text is passed to it in the GUI message box area 
	 * @param text
	 */
	public void show(String text)
	{
		textArea.append(text + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());

	}
	
	/**
	 * Determines if single box is selected 
	 * @return a boolean value true of false if single file masking is selected
	 */
	public boolean isSingleFile()
	{			
		return rdbtnSingleFile.isSelected();
	}
	
	/** 
	 * Determines if the full directory masking method is selected
	 * @return true or false if the full directory masking method is selected
	 */
	public boolean isFullDirectory()
	{
		return rdbtnDirectory.isSelected();
	}
	
	/** 
	 * Determines if the CSV file masking method is selected
	 * @return true or false if the CSV file masking method is selected
	 */
	public boolean isCsvFile()
	{
		return rdbtnFileMetadataCsv.isSelected() ;
	}
	
	/**
	 * Determines if the CSV file masking directory method is selected
	 * @return true or false if the CSV file masking method is selected
	 */
	public boolean isCsvFileDir()
	{
		return rdbtnFileMetadataCsvdir.isSelected();
	}
	
	public boolean isOracleDb()
	{
		return  rdbtnOracleMetadataMask.isSelected();
	}
	
	public boolean isConfig()
	{
		return chckbxUseConfigFile.isSelected();
	}
}
