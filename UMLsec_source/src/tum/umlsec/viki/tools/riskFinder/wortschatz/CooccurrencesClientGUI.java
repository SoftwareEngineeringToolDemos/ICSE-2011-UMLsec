/*
 * ClientGUI.java
 *
 * Created on 24. April 2004, 21:23
 */

package tum.umlsec.viki.tools.riskFinder.wortschatz;



import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;



/**
 *
 * @author  Administrator
 */
public class CooccurrencesClientGUI extends JFrame implements ActionListener {
    
    private JTextField objTxtFldLogin = null;
    private JPasswordField objTxtFldPasswd = null;
    private JComboBox objBox = null;
    private JTabbedPane objTabbedPane = null;
    
    private HashMap objTxtFieldParameters = new HashMap();
    private HashMap objAttachments = new HashMap();
    private HashMap objFileInputs = new HashMap();
    
    private CooccurrencesClient objClient = null;
    private DefaultTableModel tableModel = null;
    private String[][] strResult = null;
    private JPanel objResponsePanel = null;
    
    
    private JFileChooser objInputFile = null;
    
    /** Creates new form ClientGUI */
    public CooccurrencesClientGUI() {
        
        try{
            objClient = new CooccurrencesClient();
            
        }
        catch( Exception e ){
            JOptionPane.showMessageDialog(this, e.getMessage(), "Service Error",
            JOptionPane.ERROR_MESSAGE);
        }
        
        initComponents();
        setBounds(0, 0, 800, 600);
        
        getContentPane().setLayout( null );
        setTitle( objClient.getServiceName() );
        setJMenuBar( createMenu() );
        objTabbedPane = new JTabbedPane();
        
        JPanel objRequestPanel = new JPanel();
        objResponsePanel = new JPanel();
        
        objTabbedPane.addTab("Request", null, objRequestPanel, "Request");
        objTabbedPane.addTab("Result", null, objResponsePanel, "Result");
        
        this.getContentPane().add( objTabbedPane );
        objTabbedPane.setBounds( 10, 10, 780, 530 );
        
        JPanel objDescriptionPanel = new JPanel();
        JPanel objLoginPanel = new JPanel();
        JPanel objParameterPanel = new JPanel();
        
        objDescriptionPanel.setBorder( new TitledBorder( "Description") );
        objLoginPanel.setBorder( new TitledBorder( "Login") );
        objParameterPanel.setBorder( new TitledBorder( "Parameter") );
        
        objRequestPanel.setLayout( null );
        objDescriptionPanel.setBounds( 10, 10, 740, 130 );
        objLoginPanel.setBounds( 10, 150, 740, 80 );
        objParameterPanel.setBounds( 10, 240, 740, 200 );
        
        JButton objBtnSend = new JButton( "send >>" );
        JButton objBtnPing = new JButton( "ping >>" );
        
        objBtnSend.setBounds( 200, 460, 100, 20 );
        objBtnSend.addActionListener( this );
        
        objBtnPing.setBounds( 470, 460, 100, 20 );
        objBtnPing.addActionListener( this );
        
        objRequestPanel.add( objBtnSend );
        objRequestPanel.add( objBtnPing );
        
        objDescriptionPanel.setLayout( null );
        javax.swing.JTextPane objlblDescription = new javax.swing.JTextPane();
        objlblDescription.setText( objClient.getDescription() );
        objlblDescription.setLayout( null );
        objlblDescription.setBounds( 15, 15,  700, 80 );
        objlblDescription.setBackground( getBackground() );
        objDescriptionPanel.add( objlblDescription );
        
        JLabel objLblLogin = new JLabel( "Login" );
        JLabel objLblPasswd = new JLabel( "Password" );
        JLabel objLblAuthLevel = new JLabel( "required authorization level" );
        JLabel objLblAuth = new JLabel( objClient.getServiceAuthorizationLevel() );
        objTxtFldLogin = new JTextField();
        objTxtFldPasswd = new JPasswordField();
        
        
        objLoginPanel.setLayout( null );
        objLblLogin.setBounds(15, 20, 85, 20);
        objLblPasswd.setBounds(15, 45, 85, 20);
        objLblAuthLevel.setBounds(450, 20, 200, 20);
        objLblAuth.setBounds( 450, 45, 200, 20 );
        objTxtFldLogin.setBounds(110, 20, 250, 20);
        objTxtFldPasswd.setBounds( 110, 45, 250, 20 );
        
        objParameterPanel.setLayout( null );
        
        String strWebserviceType = objClient.getWebServiceType();
        if ( "de.uni_leipzig.wortschatz.webservice.webservicetypes.MySQLSelectType".equals( strWebserviceType ) ||
        "de.uni_leipzig.wortschatz.webservice.webservicetypes.MySQLModifyDBType".equals( strWebserviceType )){
            JLabel objLblCorpus = new JLabel( "Corpus" );
            
            objBox = new JComboBox();
            objBox.setEditable( true );
            
            for( int i = 0; i < objClient.getCorpus().length; i++ ){
                objBox.addItem( objClient.getCorpus()[i] );
            }
            
            objLblCorpus.setBounds( 15, 20, 150, 20 );
            objBox.setBounds( 200, 20, 150, 20 );
            
            objParameterPanel.add( objLblCorpus );
            objParameterPanel.add( objBox );
        }
        
        
        
        for ( int i = 0; i < objClient.getInputFields().length; i++ ){
            
            JLabel objLabel = new JLabel( objClient.getInputFields()[i] );
            objLabel.setBounds( 15 , 40 + (i+1)*25, 150, 20 );
            
            objParameterPanel.add( objLabel );
            
            
            if( objClient.getInputFields()[i].toLowerCase().indexOf( "passw" ) != -1 ){
                JPasswordField objInput = new JPasswordField();
                objInput.setBounds( 200, 45 + (i+1)*25, 150, 20 );
                objParameterPanel.add( objInput );
                
                objTxtFieldParameters.put( objClient.getInputFields()[i], objInput );
            }
            else if( objClient.getInputFields()[i].toLowerCase().indexOf( "file" ) != -1 ){
                JButton openButton = new JButton( "Load " + objClient.getInputFields()[i] );
                openButton.addActionListener(this);
                openButton.setBounds( 200, 45 + (i+1)*25, 150, 20 );
                JLabel lblFileName = new JLabel( "no file selected" );
                lblFileName.setBounds( 380, 45 + (i+1)*25, 300, 20 );
                objParameterPanel.add( openButton );
                objParameterPanel.add( lblFileName );
                objFileInputs.put( objClient.getInputFields()[i], lblFileName);
            }
            else{
                JTextField objInput = new JTextField();
                objInput.setBounds( 200, 45 + (i+1)*25, 150, 20 );
                objParameterPanel.add( objInput );
                
                objTxtFieldParameters.put( objClient.getInputFields()[i], objInput );
            }
            
        }
        
        objLoginPanel.add( objLblLogin );
        objLoginPanel.add( objLblPasswd );
        objLoginPanel.add( objLblAuthLevel );
        objLoginPanel.add( objLblAuth );
        objLoginPanel.add( objTxtFldLogin );
        objLoginPanel.add( objTxtFldPasswd );
        
        objRequestPanel.add( objDescriptionPanel );
        objRequestPanel.add( objLoginPanel );
        objRequestPanel.add( objParameterPanel );
        
        objResponsePanel.setLayout( new BorderLayout() );
        
        JTable objResponse = new JTable(1, 1);
        
        String[] strFields = objClient.getDBFields();
        tableModel = new DefaultTableModel( strFields , 0 );
        
        int intCountIndex = objClient.getDBFields().length;
        strResult = new String[0][intCountIndex];
        
        objResponse.setModel( tableModel );
        
        JScrollPane tableScrollPane = new JScrollPane( objResponse );
        
        objResponsePanel.add( tableScrollPane );
        
    }
    
    private void createTable( String[][] strResultMatrix ){
        
        JTable objResponse = new JTable(1, 1);
        
	String strFields[] = null;
	
	if ( objClient.getDBFields().length == 1 || objClient.getDBFields()[0].equals( "" ) ){
    	    strFields = objClient.getDBFields();
	}
	else{
	    strFields = new String[strResultMatrix[0].length];

	    for( int i = 0; i < strResultMatrix[0].length; i++ ){
		strFields[i] = "";
	    }
	}	
	DefaultTableModel tableModel = new DefaultTableModel( strFields , 0 );
        
        int intCountIndex = strResultMatrix[0].length;

        if ( intCountIndex == 1 ){
            while( true ){
                try{
                    intCountIndex++;
                    String strTMP = strResultMatrix[intCountIndex][0];
                }
                catch( Exception e ){}
            }
        }
        intCountIndex--;
        
        tableModel.addRow( new String[intCountIndex] );
        
        for( int i = 0; i < strResultMatrix.length; i++ ){
            
            String[] strRow = new String[intCountIndex];
            
            for( int j = 0; j < intCountIndex; j++ ){
                strRow[j] = strResultMatrix[i][j];
            }
            
            tableModel.addRow( strRow );
        }
        
        objResponse.setModel( tableModel );
        
        JScrollPane tableScrollPane = new JScrollPane( objResponse );
        
        objResponsePanel.add( tableScrollPane );
    }
    
    /** This method is ccooccurrencesed from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        pack();
    }//GEN-END:initComponents
    
    
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    
    
    public void actionPerformed(ActionEvent event) {
        
        if( event.getActionCommand().startsWith( "Load " ) ){
            objInputFile = new JFileChooser();
            int intStatus = objInputFile.showOpenDialog( CooccurrencesClientGUI.this );
            
            if( intStatus == JFileChooser.APPROVE_OPTION ) {
                File objFile = objInputFile.getSelectedFile();
                objAttachments.remove( event.getActionCommand().substring( 5 ) );
                objAttachments.put( event.getActionCommand().substring( 5 ), objFile.getAbsolutePath() );
                JLabel objTmpLabel = (JLabel)objFileInputs.get( event.getActionCommand().substring( 5 ) );
                objTmpLabel.setText( objFile.getName() );
            }
        }
        
        if( event.getActionCommand().equals("ping >>") ){
            
            String strMessage = "";
            
            try{
                strMessage = objClient.ping();
                JOptionPane.showMessageDialog(this, strMessage );
            }
            catch( Exception e ){
                JOptionPane.showMessageDialog(this, e.getMessage(), "Service Error",
                JOptionPane.ERROR_MESSAGE);
            }
        }
        
        if( event.getActionCommand().equals("send >>") ){
            
            if ( this.objTxtFldLogin.getText() != null ){
                objClient.setUsername( this.objTxtFldLogin.getText() );
            }
            else{
                objClient.setUsername( "" );
            }
            
            if( objTxtFldPasswd.getPassword() !=  null){
                String strPasswd = "";
                
                for ( int i = 0; i < objTxtFldPasswd.getPassword().length; i++ ){
                    strPasswd += objTxtFldPasswd.getPassword()[i];
                }
                
                objClient.setPassword( strPasswd );
            }
            else{
                objClient.setPassword( "" );
            }

            
            if( objBox != null ){
                objClient.setCorpus( (String)objBox.getSelectedItem() );
            }
            
            java.util.Set objSet = objTxtFieldParameters.keySet();
            java.util.Iterator objIterator = objSet.iterator();
            
            while( objIterator.hasNext() ){
                String strKey = (String)objIterator.next();
                
                if( strKey.toLowerCase().indexOf( "password" ) != -1 ){
                    
                    String strPasswd = "";
                    
                    if( ((JPasswordField)objTxtFieldParameters.get( strKey )).getPassword() !=  null){
                        
                        for ( int i = 0; i < ((JPasswordField)objTxtFieldParameters.get( strKey )).getPassword().length; i++ ){
                            strPasswd += ((JPasswordField)objTxtFieldParameters.get( strKey )).getPassword()[i];
                        }
                        objClient.addParameter( strKey, ((JTextField)objTxtFieldParameters.get( strKey )).getText() );
                    }
                    
                    objClient.addParameter( strKey, strPasswd );
                    
                }
                else if( strKey.toLowerCase().indexOf( "file" ) != -1 ){}
                else{
                    objClient.addParameter( strKey, ((JTextField)objTxtFieldParameters.get( strKey )).getText() );
                }
            }
                        
            try{
                objClient.addAttachment( objAttachments );
            }
            catch( Exception e ){
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error while processing the attachment file",
                JOptionPane.ERROR_MESSAGE);
            }
	    
	    String strFields[] = null;            
            try{
                objClient.execute();
                
                strResult = objClient.getResult();
                
                objTabbedPane.setSelectedIndex( 1 );
            
		
		if( objClient.getDBFields().length == 1 && objClient.getDBFields()[0].equals( ""  )  ){
		    strFields = new String[ strResult[0].length  ];
		    
		    for( int i = 0; i < strResult[0].length; i++ ){
			strFields[i] = "";
		    }
		    
		}
		else{
		    strFields = objClient.getDBFields();
		}
		
                tableModel.setDataVector( null, strFields );
                
                
		while( tableModel.getRowCount() > 0 ){
                    tableModel.removeRow( 0 );
                }
                
                int intCountIndex = strFields.length;
                for( int i = 0; i < strResult.length; i++ ){
                    
                    String[] strRow = new String[intCountIndex];
                    
                    for( int j = 0; j < intCountIndex; j++ ){
                        strRow[j] = strResult[i][j];
                    }
                    
                    tableModel.addRow( strRow );
                }
                
            }
            catch( Exception e ){
                JOptionPane.showMessageDialog(this, e.getMessage(), "Service Error",
                JOptionPane.ERROR_MESSAGE);
            }
            repaint();
            
            Object[] objAttachments = objClient.getAttachments();
            
            for( int i = 0; i < objAttachments.length; i++ ){
                try{
                    int intSaveStatus = JFileChooser.APPROVE_OPTION;
                    JFileChooser objSaveDialog = null;
                    java.io.File objTMPFile = null;
                    do{
                        org.apache.axis.attachments.AttachmentPart objFile = (org.apache.axis.attachments.AttachmentPart)objAttachments[i];
                        String strFilename = objFile.getContentLocation();
                        objSaveDialog = new JFileChooser();
                        objSaveDialog.setDialogTitle( "Save as ..." );
                        objSaveDialog.setSelectedFile( new java.io.File( strFilename ) );
                        intSaveStatus = objSaveDialog.showSaveDialog( CooccurrencesClientGUI.this );
                        
                        if( (intSaveStatus == JFileChooser.APPROVE_OPTION) ){
                            javax.activation.DataHandler objDH = objFile.getDataHandler();
                            objTMPFile = objSaveDialog.getSelectedFile();
                            objTMPFile.createNewFile();
                            
                            if ( objTMPFile.canWrite() ){
                                java.io.FileOutputStream objFOS = new java.io.FileOutputStream( objTMPFile );
                                objDH.writeTo( objFOS );
                            }
                            else{
                                JOptionPane.showMessageDialog(this, "No permissions to write.", "Service Error",
                                JOptionPane.ERROR_MESSAGE);
                                
                            }
                            
                        }
                        
                    }
                    while( !( (intSaveStatus == JFileChooser.CANCEL_OPTION) || ( objTMPFile.canWrite() ) ) );
                }
                catch( Exception e ){
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Service Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
            
            
            
        }
        
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new CooccurrencesClientGUI().show();
    }
    
    private JMenuBar createMenu(){
        JMenuBar objWindowMenuBar = new JMenuBar();
        
        JMenu objFileMenu = new JMenu( "File" );
        
        JMenuItem objFileExitItem = new JMenuItem( "Exit" );
        
        objFileMenu.add( objFileExitItem );
        objWindowMenuBar.add( objFileMenu );
        
        ActionListener lst = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        
        objFileExitItem.addActionListener( lst );
        
        return objWindowMenuBar;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
