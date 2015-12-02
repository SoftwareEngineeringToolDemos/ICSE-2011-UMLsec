package open.umlsec.tools.checksystem.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import open.umlsec.tools.checksystem.ToolSystemVerification;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import tum.umlsec.viki.framework.FrameworkBase;


/**
 * 
 * @author (GUI) Serge Kater
 * @author (refactoring) Bianca Batsch, bianca.batsch@isst.fraunhofer.de
 * 
 */
@SuppressWarnings("serial")
public class NewSystemAnalysisGui extends JDialog {
	

	   // Variables declaration - do not modify                     
	   private JButton btnCancel;
	   private JButton btnLoadXML;
	   private JButton btnOK;
	   private JLabel jLabel1;
	   private JLabel jLabel2;
	   private JLabel jLabel3;
	   private JScrollPane jScrollPane1;
	   private JTextField txtReqFilePath;
	   private JTextArea txtSystemAnalysisDesc;
	   private JTextField txtSystemAnalysisName;
	   // End of variables declaration
	   
	   private ToolSystemVerification systemVerificationTool = null;
	   @SuppressWarnings("unused")
	   private SystemVerificationGuiView gui = null;

   /** Creates new form NewSystemAnalysisGui */
   public NewSystemAnalysisGui(java.awt.Frame parent, boolean modal) {
       super(parent, modal);
       init();
   }

   private void init() {

       jLabel1 = new JLabel();
       txtSystemAnalysisName = new JTextField();
       jLabel2 = new JLabel();
       jScrollPane1 = new JScrollPane();
       txtSystemAnalysisDesc = new JTextArea();
       jLabel3 = new JLabel();
       txtReqFilePath = new JTextField();
       btnLoadXML = new JButton();
       btnOK = new JButton();
       btnCancel = new JButton();

       setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
       setName("Form"); // NOI18N

       ResourceMap resourceMap = Application.getInstance(
    		   SystemVerificationLoader.class).getContext().getResourceMap(NewSystemAnalysisGui.class);
       jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
       jLabel1.setName("jLabel1"); // NOI18N

       txtSystemAnalysisName.setText(resourceMap.getString("txtSystemAnalysisName.text")); // NOI18N
       txtSystemAnalysisName.setName("txtSystemAnalysisName"); // NOI18N

       jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
       jLabel2.setName("jLabel2"); // NOI18N

       jScrollPane1.setName("jScrollPane1"); // NOI18N

       txtSystemAnalysisDesc.setColumns(20);
       txtSystemAnalysisDesc.setRows(5);
       txtSystemAnalysisDesc.setName("txtSystemAnalysisDesc"); // NOI18N
       jScrollPane1.setViewportView(txtSystemAnalysisDesc);

       jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
       jLabel3.setName("jLabel3"); // NOI18N

       txtReqFilePath.setEditable(false);
       txtReqFilePath.setText(resourceMap.getString("jTextField1.text")); // NOI18N
       txtReqFilePath.setName("jTextField1"); // NOI18N

       btnLoadXML.setText(resourceMap.getString("btnLoadXML.text")); // NOI18N
       btnLoadXML.setName("btnLoadXML"); // NOI18N
       btnLoadXML.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent evt) {
               btnLoadXMLActionPerformed(evt);
           }
       });

       btnOK.setText(resourceMap.getString("btnOK.text")); // NOI18N
       btnOK.setName("btnOK"); // NOI18N
       btnOK.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent evt) {
               btnOKActionPerformed(evt);
           }
       });

       btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
       btnCancel.setName("btnCancel"); // NOI18N
       btnCancel.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent evt) {
               btnCancelActionPerformed(evt);
           }
       });

       GroupLayout layout = new GroupLayout(getContentPane());
       getContentPane().setLayout(layout);
       layout.setHorizontalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGroup(layout.createSequentialGroup()
               .addContainerGap()
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                   .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                   .addComponent(jLabel1)
                   .addComponent(txtSystemAnalysisName, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                   .addComponent(jLabel2)
                   .addComponent(jLabel3)
                   .addGroup(layout.createSequentialGroup()
                       .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                           .addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                           .addComponent(txtReqFilePath, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE))
                       .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                       .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                           .addComponent(btnOK, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                           .addComponent(btnLoadXML, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))))
               .addContainerGap())
       );
       layout.setVerticalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGroup(layout.createSequentialGroup()
               .addGap(25, 25, 25)
               .addComponent(jLabel1)
               .addGap(8, 8, 8)
               .addComponent(txtSystemAnalysisName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
               .addGap(18, 18, 18)
               .addComponent(jLabel2)
               .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
               .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
               .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
               .addComponent(jLabel3)
               .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                   .addComponent(txtReqFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                   .addComponent(btnLoadXML))
               .addGap(18, 18, 18)
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                   .addComponent(btnOK)
                   .addComponent(btnCancel))
               .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
       );

       pack();
   }// </editor-fold>                        

   private void btnOKActionPerformed(ActionEvent evt) {                                      
       File xmlReqFile = null;
       
       if (!(txtReqFilePath.getText().equals(""))){
    	   xmlReqFile = new File(txtReqFilePath.getText());    	   
       }
       
	   systemVerificationTool = (ToolSystemVerification)SystemVerificationLoader.getTool(
			   									SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);
	   
	   systemVerificationTool.createAnalysis(txtSystemAnalysisName.getText(),
								   				txtSystemAnalysisDesc.getText(), 
								   									xmlReqFile);
	   	   
		SystemVerificationLoader.getGui().setAnalysisName(txtSystemAnalysisName.getText());
		SystemVerificationLoader.getGui().setAnalysisDescription(txtSystemAnalysisDesc.getText());
	   
		if (xmlReqFile != null){
			SystemVerificationLoader.getGui().setAnalysisXMLFileName(
															xmlReqFile.getName(), 
															xmlReqFile.getPath());
			
			SystemVerificationLoader.getGui().updateRequirementList();
		} else {
			SystemVerificationLoader.getGui().writeLn("No Requirement XML file loaded");
		}

	   dispose();
   }                                     

   private void btnCancelActionPerformed(ActionEvent evt) {                                          
       dispose();
   }                                         

   private void btnLoadXMLActionPerformed(ActionEvent evt) {
		JFileChooser 	fileChooser = new JFileChooser();
		int 			userSelection;
		FrameworkBase 	framework = SystemVerificationLoader.getFramework();
		
		fileChooser.setFileFilter(new FileFilterXML());

		fileChooser.setCurrentDirectory(new File(
							framework.getAppSettings().getModelDirectory()));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		
		userSelection = fileChooser.showOpenDialog(this); 
		if(userSelection == JFileChooser.APPROVE_OPTION) {
			
			txtReqFilePath.setText(fileChooser.getSelectedFile().getPath());			
		}
		
   }
   
   /**
   * @param args the command line arguments
   */
   public static void main(String args[]) {
       java.awt.EventQueue.invokeLater(new Runnable() {
           public void run() {
               NewSystemAnalysisGui dialog = new NewSystemAnalysisGui(new JFrame(), true);
               dialog.addWindowListener(new WindowAdapter() {
                   public void windowClosing(WindowEvent e) {
                       System.exit(0);
                   }
               });
               dialog.setVisible(true);
           }
       });
   }


}
