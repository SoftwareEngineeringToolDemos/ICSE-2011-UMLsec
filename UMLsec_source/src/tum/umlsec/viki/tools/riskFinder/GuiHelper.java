package tum.umlsec.viki.tools.riskFinder;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

import org.apache.log4j.Logger;

/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de<br>
 * enthält Methoden für GUI-Funktionen des Riskfinder
 *
 */
public class GuiHelper {
	
	private static Logger logger = Logger.getLogger("GuiHelper");
	private BsiContent bsicontent;
	private JComboBox comboBox;
	private JTextField textField;
	private JList listG1;
	private DefaultListModel defaultListModelG1;
	private JList listG2;
	private DefaultListModel defaultListModelG2;
	private JList listG3;
	private DefaultListModel defaultListModelG3;
	private JList listG4;
	private DefaultListModel defaultListModelG4;
	private JList listG5;
	private DefaultListModel defaultListModelG5;
	private JList listG6;
	private DefaultListModel defaultListModelG6;
	private JList listM1;
	private DefaultListModel defaultListModelM1;
	private JList listM2;
	private DefaultListModel defaultListModelM2;
	private JList listM3;
	private DefaultListModel defaultListModelM3;
	private JList listM4;
	private DefaultListModel defaultListModelM4;
	private JList listM5;
	private DefaultListModel defaultListModelM5;
	private JList listM6;
	private DefaultListModel defaultListModelM6;
	private JList listM7;
	private DefaultListModel defaultListModelM7;
	private JList delList;
	private DefaultListModel delListModel;
	private Vector<String> patternList;
	
	public GuiHelper(){
		getBsicontent();
	}

	/**
	 * erstellt ein neues Frame fürs Pattern erstellen
	 */
	public void newPatternFrame(){
		logger.info("make new Pattern");
	
		JFrame frame = new JFrame();
        JPanel contentPane = (JPanel) frame.getContentPane();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{167, 6, 20, 6, 129};
        gridBagLayout.rowHeights = new int[]{1, 21, 1, 20};
        gridBagLayout.columnWeights = new double[]{0, 0, 1, 0, 0};
        gridBagLayout.rowWeights = new double[]{0, 0, 0, 1};
        contentPane.setLayout(gridBagLayout);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(100, 100));
        
        // make save button for the tabs
        JButton tabButtonG1 = new JButton("G1");
	    ActionListener al;
	    al = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listG1.isEnabled()){
	    		  int idx[] = listG1.getSelectedIndices();
	    	  		for (int i = 0; i < idx.length; i++){
	    	  			RepositoryHelper.g1.add(defaultListModelG1.getElementAt(i).toString());
	    	  		}
	    	  		listG1.setEnabled(false);
	      		} else {
	      			listG1.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonG1.addActionListener(al);
	    JButton tabButtonG2 = new JButton("G2");
	    ActionListener a2;
	    a2 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listG2.isEnabled()){
	    		  int idx2[] = listG2.getSelectedIndices();
	    	  		for (int i = 0; i < idx2.length; i++){
	    	  			RepositoryHelper.g2.add(defaultListModelG2.getElementAt(i).toString());
	    	  		}
	    	  		listG2.setEnabled(false);
	      		} else {
	      			listG2.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonG2.addActionListener(a2);
	    JButton tabButtonG3 = new JButton("G3");
	    ActionListener a3;
	    a3 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listG3.isEnabled()){
	    		  int idx3[] = listG3.getSelectedIndices();
	    	  		for (int i = 0; i < idx3.length; i++){
	    	  			RepositoryHelper.g3.add(defaultListModelG3.getElementAt(i).toString());
	    	  		}
	    	  		listG3.setEnabled(false);
	      		} else {
	      			listG3.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonG3.addActionListener(a3);
	    JButton tabButtonG4 = new JButton("G4");
	    ActionListener a4;
	    a4 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listG4.isEnabled()){
	    		  int idx4[] = listG4.getSelectedIndices();
	    	  		for (int i = 0; i < idx4.length; i++){
	    	  			RepositoryHelper.g4.add(defaultListModelG4.getElementAt(i).toString());
	    	  		}
	    	  		listG4.setEnabled(false);
	      		} else {
	      			listG4.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonG4.addActionListener(a4);
	    JButton tabButtonG5 = new JButton("G5");
	    ActionListener a5;
	    a5 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listG5.isEnabled()){
	    		  int idx5[] = listG5.getSelectedIndices();
	    	  		for (int i = 0; i < idx5.length; i++){
	    	  			RepositoryHelper.g5.add(defaultListModelG5.getElementAt(i).toString());
	    	  		}
	    	  		listG5.setEnabled(false);
	      		} else {
	      			listG5.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonG5.addActionListener(a5);
	    JButton tabButtonG6 = new JButton("G6");
	    ActionListener a6;
	    a6 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listG6.isEnabled()){
	    		  int idx6[] = listG6.getSelectedIndices();
	    	  		for (int i = 0; i < idx6.length; i++){
	    	  			RepositoryHelper.g6.add(defaultListModelG6.getElementAt(i).toString());
	    	  		}
	    	  		listG6.setEnabled(false);
	      		} else {
	      			listG6.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonG6.addActionListener(a6);
	    JButton tabButtonM1 = new JButton("M1");
	    ActionListener am1;
	    am1 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listM1.isEnabled()){
	    		  int idxm1[] = listM1.getSelectedIndices();
	    	  		for (int i = 0; i < idxm1.length; i++){
	    	  			RepositoryHelper.m1.add(defaultListModelM1.getElementAt(i).toString());
	    	  		}
	    	  		listM1.setEnabled(false);
	      		} else {
	      			listM1.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonM1.addActionListener(am1);
	    JButton tabButtonM2 = new JButton("M2");
	    ActionListener am2;
	    am2 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listM2.isEnabled()){
	    		  int idxm2[] = listM2.getSelectedIndices();
	    	  		for (int i = 0; i < idxm2.length; i++){
	    	  			RepositoryHelper.m2.add(defaultListModelM2.getElementAt(i).toString());
	    	  		}
	    	  		listM2.setEnabled(false);
	      		} else {
	      			listM2.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonM2.addActionListener(am2);
	    JButton tabButtonM3 = new JButton("M3");
	    ActionListener am3;
	    am3 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listM3.isEnabled()){
	    		  int idxm3[] = listM3.getSelectedIndices();
	    	  		for (int i = 0; i < idxm3.length; i++){
	    	  			RepositoryHelper.m3.add(defaultListModelM3.getElementAt(i).toString());
	    	  		}
	    	  		listM3.setEnabled(false);
	      		} else {
	      			listM3.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonM3.addActionListener(am3);
	    JButton tabButtonM4 = new JButton("M4");
	    ActionListener am4;
	    am4 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listM4.isEnabled()){
	    		  int idxm4[] = listM4.getSelectedIndices();
	    	  		for (int i = 0; i < idxm4.length; i++){
	    	  			RepositoryHelper.m4.add(defaultListModelM4.getElementAt(i).toString());
	    	  		}
	    	  		listM4.setEnabled(false);
	      		} else {
	      			listM4.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonM4.addActionListener(am4);
	    JButton tabButtonM5 = new JButton("M5");
	    ActionListener am5;
	    am5 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listM5.isEnabled()){
	    		  int idxm5[] = listM5.getSelectedIndices();
	    	  		for (int i = 0; i < idxm5.length; i++){
	    	  			RepositoryHelper.m5.add(defaultListModelM5.getElementAt(i).toString());
	    	  		}
	    	  		listM5.setEnabled(false);
	      		} else {
	      			listM5.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonM5.addActionListener(am5);
	    JButton tabButtonM6 = new JButton("M6");
	    ActionListener am6;
	    am6 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listM6.isEnabled()){
	    		  int idxm6[] = listM6.getSelectedIndices();
	    	  		for (int i = 0; i < idxm6.length; i++){
	    	  			RepositoryHelper.m6.add(defaultListModelM6.getElementAt(i).toString());
	    	  		}
	    	  		listM6.setEnabled(false);
	      		} else {
	      			listM6.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonM6.addActionListener(am6);
	    JButton tabButtonM7 = new JButton("M7");
	    ActionListener am7;
	    am7 = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  if (listM7.isEnabled()){
	    		  int idxm7[] = listM7.getSelectedIndices();
	    	  		for (int i = 0; i < idxm7.length; i++){
	    	  			RepositoryHelper.m7.add(defaultListModelM7.getElementAt(i).toString());
	    	  		}
	    	  		listM7.setEnabled(false);
	      		} else {
	      			listM7.setEnabled(true);
	      		}
	    	}
	    };
	    tabButtonM7.addActionListener(am7);
	    
	    // fill jLists with Keys from HashMaps
        JPanel panelG1 = new JPanel();
        GridBagLayout gridBagLayout2 = new GridBagLayout();
        gridBagLayout2.columnWidths = new int[]{20};
        gridBagLayout2.rowHeights = new int[]{20};
        gridBagLayout2.columnWeights = new double[]{1};
        gridBagLayout2.rowWeights = new double[]{1};
        panelG1.setLayout(gridBagLayout2);
        listG1 = new JList();
        defaultListModelG1 = new DefaultListModel();
        for (int i=1; i<bsicontent.getGefaehrdungsKatalog().get("G 1 Höhere Gewalt").size(); i++){
        	defaultListModelG1.addElement(getKey(bsicontent.getGefaehrdungsKatalog().get("G 1 Höhere Gewalt").keySet(), i));
        }
        listG1.setModel(defaultListModelG1);
        JScrollPane scrollPaneG1 = new JScrollPane(listG1);
        scrollPaneG1.setPreferredSize(new Dimension(23, 23));
        scrollPaneG1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelG1.add(scrollPaneG1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("G1", panelG1);
        JPanel paneG1 = new JPanel();
	    paneG1.setOpaque(false);
	    paneG1.add(tabButtonG1);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneG1);
        //-----
        JPanel panelG2 = new JPanel();
        GridBagLayout gridBagLayoutg2 = new GridBagLayout();
        gridBagLayoutg2.columnWidths = new int[]{20};
        gridBagLayoutg2.rowHeights = new int[]{20};
        gridBagLayoutg2.columnWeights = new double[]{1};
        gridBagLayoutg2.rowWeights = new double[]{1};
        panelG2.setLayout(gridBagLayoutg2);
        listG2 = new JList();
        defaultListModelG2 = new DefaultListModel();
         for (int i=1; i<bsicontent.getGefaehrdungsKatalog().get("G 2 Organisatorische Mängel").size(); i++){
        	defaultListModelG2.addElement(getKey(bsicontent.getGefaehrdungsKatalog().get("G 2 Organisatorische Mängel").keySet(), i));
        }
        listG2.setModel(defaultListModelG2);
        JScrollPane scrollPaneG2 = new JScrollPane(listG2);
        scrollPaneG2.setPreferredSize(new Dimension(23, 23));
        scrollPaneG2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelG2.add(scrollPaneG2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("G2", null, panelG2);
        JPanel paneG2 = new JPanel();
	    paneG2.setOpaque(false);
	    paneG2.add(tabButtonG2);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneG2);
        
       //----
        JPanel panelG3 = new JPanel();
        GridBagLayout gridBagLayoutg3 = new GridBagLayout();
        gridBagLayoutg3.columnWidths = new int[]{20};
        gridBagLayoutg3.rowHeights = new int[]{20};
        gridBagLayoutg3.columnWeights = new double[]{1};
        gridBagLayoutg3.rowWeights = new double[]{1};
        panelG3.setLayout(gridBagLayoutg3);
        listG3 = new JList();
        defaultListModelG3 = new DefaultListModel();
        for (int i=1; i<bsicontent.getGefaehrdungsKatalog().get("G 3 Menschliche Fehlhandlung").size(); i++){
        	defaultListModelG3.addElement(getKey(bsicontent.getGefaehrdungsKatalog().get("G 3 Menschliche Fehlhandlung").keySet(), i));
        }
        listG3.setModel(defaultListModelG3);
        JScrollPane scrollPaneG3 = new JScrollPane(listG3);
        scrollPaneG3.setPreferredSize(new Dimension(23, 23));
        scrollPaneG3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelG3.add(scrollPaneG3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("G3", null, panelG3);
        JPanel paneG3 = new JPanel();
	    paneG3.setOpaque(false);
	    paneG3.add(tabButtonG3);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneG3);
        
        //-----
        JPanel panelG4 = new JPanel();
        GridBagLayout gridBagLayoutg4 = new GridBagLayout();
        gridBagLayoutg4.columnWidths = new int[]{20};
        gridBagLayoutg4.rowHeights = new int[]{20};
        gridBagLayoutg4.columnWeights = new double[]{1};
        gridBagLayoutg4.rowWeights = new double[]{1};
        panelG4.setLayout(gridBagLayoutg4);
        listG4 = new JList();
        defaultListModelG4 = new DefaultListModel();
        for (int i=1; i<bsicontent.getGefaehrdungsKatalog().get("G 4 Technisches Versagen").size(); i++){
        	defaultListModelG4.addElement(getKey(bsicontent.getGefaehrdungsKatalog().get("G 4 Technisches Versagen").keySet(), i));
        }
        listG4.setModel(defaultListModelG4);
        JScrollPane scrollPaneG4 = new JScrollPane(listG4);
        scrollPaneG4.setPreferredSize(new Dimension(23, 23));
        scrollPaneG4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelG4.add(scrollPaneG4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("G4", null, panelG4);
        JPanel paneG4 = new JPanel();
	    paneG4.setOpaque(false);
	    paneG4.add(tabButtonG4);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneG4);
        
        //-----
        JPanel panelG5 = new JPanel();
        GridBagLayout gridBagLayoutg5 = new GridBagLayout();
        gridBagLayoutg5.columnWidths = new int[]{20};
        gridBagLayoutg5.rowHeights = new int[]{20};
        gridBagLayoutg5.columnWeights = new double[]{1};
        gridBagLayoutg5.rowWeights = new double[]{1};
        panelG5.setLayout(gridBagLayoutg5);
        listG5 = new JList();
        defaultListModelG5 = new DefaultListModel();
        for (int i=1; i<bsicontent.getGefaehrdungsKatalog().get("G 5 Vorsätzliche Handlungen").size(); i++){
        	defaultListModelG5.addElement(getKey(bsicontent.getGefaehrdungsKatalog().get("G 5 Vorsätzliche Handlungen").keySet(), i));
        }
        listG5.setModel(defaultListModelG5);
        JScrollPane scrollPaneG5 = new JScrollPane(listG5);
        scrollPaneG5.setPreferredSize(new Dimension(23, 23));
        scrollPaneG5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelG5.add(scrollPaneG5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("G5", null, panelG5);
        JPanel paneG5 = new JPanel();
	    paneG5.setOpaque(false);
	    paneG5.add(tabButtonG5);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneG5);
      //-----
        JPanel panelG6 = new JPanel();
        GridBagLayout gridBagLayoutg6 = new GridBagLayout();
        gridBagLayoutg6.columnWidths = new int[]{20};
        gridBagLayoutg6.rowHeights = new int[]{20};
        gridBagLayoutg6.columnWeights = new double[]{1};
        gridBagLayoutg6.rowWeights = new double[]{1};
        panelG6.setLayout(gridBagLayoutg6);
        listG6 = new JList();
        defaultListModelG6 = new DefaultListModel();
        for (int i=1; i<bsicontent.getGefaehrdungsKatalog().get("G 6 Datenschutz").size(); i++){
        	defaultListModelG6.addElement(getKey(bsicontent.getGefaehrdungsKatalog().get("G 6 Datenschutz").keySet(), i));
        }
        listG6.setModel(defaultListModelG6);
        JScrollPane scrollPaneG6 = new JScrollPane(listG6);
        scrollPaneG6.setPreferredSize(new Dimension(23, 23));
        scrollPaneG6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelG6.add(scrollPaneG6, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("G6", null, panelG6);
        JPanel paneG6 = new JPanel();
	    paneG6.setOpaque(false);
	    paneG6.add(tabButtonG6);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneG6);
        //----
        JPanel panelM1 = new JPanel();
        GridBagLayout gridBagLayoutm1 = new GridBagLayout();
        gridBagLayoutm1.columnWidths = new int[]{20};
        gridBagLayoutm1.rowHeights = new int[]{20};
        gridBagLayoutm1.columnWeights = new double[]{1};
        gridBagLayoutm1.rowWeights = new double[]{1};
        panelM1.setLayout(gridBagLayoutm1);
        listM1 = new JList();
        defaultListModelM1 = new DefaultListModel();
        for (int i=1; i<bsicontent.getMassnahmenKatalog().get("M 1 Infrastruktur").size(); i++){
        	defaultListModelM1.addElement(getKey(bsicontent.getMassnahmenKatalog().get("M 1 Infrastruktur").keySet(), i));
        }
        listM1.setModel(defaultListModelM1);
        JScrollPane scrollPaneM1 = new JScrollPane(listM1);
        scrollPaneM1.setPreferredSize(new Dimension(23, 23));
        scrollPaneM1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelM1.add(scrollPaneM1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("M1", null, panelM1);
        JPanel paneM1 = new JPanel();
	    paneM1.setOpaque(false);
	    paneM1.add(tabButtonM1);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneM1);
        
        //----
        JPanel panelM2 = new JPanel();
        GridBagLayout gridBagLayoutm2 = new GridBagLayout();
        gridBagLayoutm2.columnWidths = new int[]{20};
        gridBagLayoutm2.rowHeights = new int[]{20};
        gridBagLayoutm2.columnWeights = new double[]{1};
        gridBagLayoutm2.rowWeights = new double[]{1};
        panelM2.setLayout(gridBagLayoutm2);
        listM2 = new JList();
        defaultListModelM2 = new DefaultListModel();
        for (int i=1; i<bsicontent.getMassnahmenKatalog().get("M 2 Organisation").size(); i++){
        	defaultListModelM2.addElement(getKey(bsicontent.getMassnahmenKatalog().get("M 2 Organisation").keySet(), i));
        }
        listM2.setModel(defaultListModelM2);
        JScrollPane scrollPaneM2 = new JScrollPane(listM2);
        scrollPaneM2.setPreferredSize(new Dimension(23, 23));
        scrollPaneM2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelM2.add(scrollPaneM2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("M2", null, panelM2);
        JPanel paneM2 = new JPanel();
	    paneM2.setOpaque(false);
	    paneM2.add(tabButtonM2);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneM2);

        //----
        JPanel panelM3 = new JPanel();
        GridBagLayout gridBagLayoutm3 = new GridBagLayout();
        gridBagLayoutm3.columnWidths = new int[]{20};
        gridBagLayoutm3.rowHeights = new int[]{20};
        gridBagLayoutm3.columnWeights = new double[]{1};
        gridBagLayoutm3.rowWeights = new double[]{1};
        panelM3.setLayout(gridBagLayoutm3);
        listM3 = new JList();
        defaultListModelM3 = new DefaultListModel();
        for (int i=1; i<bsicontent.getMassnahmenKatalog().get("M 3 Personal").size(); i++){
        	defaultListModelM3.addElement(getKey(bsicontent.getMassnahmenKatalog().get("M 3 Personal").keySet(), i));
        }
        listM3.setModel(defaultListModelM3);
        JScrollPane scrollPaneM3 = new JScrollPane(listM3);
        scrollPaneM3.setPreferredSize(new Dimension(23, 23));
        scrollPaneM3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelM3.add(scrollPaneM3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("M3", null, panelM3);
        JPanel paneM3 = new JPanel();
	    paneM3.setOpaque(false);
	    paneM3.add(tabButtonM3);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneM3);

        //----
        JPanel panelM4 = new JPanel();
        GridBagLayout gridBagLayoutm4 = new GridBagLayout();
        gridBagLayoutm4.columnWidths = new int[]{20};
        gridBagLayoutm4.rowHeights = new int[]{20};
        gridBagLayoutm4.columnWeights = new double[]{1};
        gridBagLayoutm4.rowWeights = new double[]{1};
        panelM4.setLayout(gridBagLayoutm4);
        listM4 = new JList();
        defaultListModelM4 = new DefaultListModel();
        for (int i=1; i<bsicontent.getMassnahmenKatalog().get("M 4 Hardware und Software").size(); i++){
        	defaultListModelM4.addElement(getKey(bsicontent.getMassnahmenKatalog().get("M 4 Hardware und Software").keySet(), i));
        }
        listM4.setModel(defaultListModelM4);
        JScrollPane scrollPaneM4 = new JScrollPane(listM4);
        scrollPaneM4.setPreferredSize(new Dimension(23, 23));
        scrollPaneM4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelM4.add(scrollPaneM4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("M4", null, panelM4);
        JPanel paneM4 = new JPanel();
	    paneM4.setOpaque(false);
	    paneM4.add(tabButtonM4);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneM4);

        //----
        JPanel panelM5 = new JPanel();
        GridBagLayout gridBagLayoutm5 = new GridBagLayout();
        gridBagLayoutm5.columnWidths = new int[]{20};
        gridBagLayoutm5.rowHeights = new int[]{20};
        gridBagLayoutm5.columnWeights = new double[]{1};
        gridBagLayoutm5.rowWeights = new double[]{1};
        panelM5.setLayout(gridBagLayoutm5);
        listM5 = new JList();
        defaultListModelM5 = new DefaultListModel();
        for (int i=1; i<bsicontent.getMassnahmenKatalog().get("M 5 Kommunikation").size(); i++){
        	defaultListModelM5.addElement(getKey(bsicontent.getMassnahmenKatalog().get("M 5 Kommunikation").keySet(), i));
        }
        listM5.setModel(defaultListModelM5);
        JScrollPane scrollPaneM5 = new JScrollPane(listM5);
        scrollPaneM5.setPreferredSize(new Dimension(23, 23));
        scrollPaneM5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelM5.add(scrollPaneM5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("M5", null, panelM5);
        JPanel paneM5 = new JPanel();
	    paneM5.setOpaque(false);
	    paneM5.add(tabButtonM5);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneM5);

        //----
        JPanel panelM6 = new JPanel();
        GridBagLayout gridBagLayoutm6 = new GridBagLayout();
        gridBagLayoutm6.columnWidths = new int[]{20};
        gridBagLayoutm6.rowHeights = new int[]{20};
        gridBagLayoutm6.columnWeights = new double[]{1};
        gridBagLayoutm6.rowWeights = new double[]{1};
        panelM6.setLayout(gridBagLayoutm6);
        listM6 = new JList();
        defaultListModelM6 = new DefaultListModel();
        for (int i=1; i<bsicontent.getMassnahmenKatalog().get("M 6 Notfallvorsorge").size(); i++){
        	defaultListModelM6.addElement(getKey(bsicontent.getMassnahmenKatalog().get("M 6 Notfallvorsorge").keySet(), i));
        }
        listM6.setModel(defaultListModelM6);
        JScrollPane scrollPaneM6 = new JScrollPane(listM6);
        scrollPaneM6.setPreferredSize(new Dimension(23, 23));
        scrollPaneM6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelM6.add(scrollPaneM6, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("M6", null, panelM6);
        JPanel paneM6 = new JPanel();
	    paneM6.setOpaque(false);
	    paneM6.add(tabButtonM6);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneM6);
      //----
        JPanel panelM7 = new JPanel();
        GridBagLayout gridBagLayoutm7 = new GridBagLayout();
        gridBagLayoutm7.columnWidths = new int[]{20};
        gridBagLayoutm7.rowHeights = new int[]{20};
        gridBagLayoutm7.columnWeights = new double[]{1};
        gridBagLayoutm7.rowWeights = new double[]{1};
        panelM7.setLayout(gridBagLayoutm7);
        listM7 = new JList();
        defaultListModelM7 = new DefaultListModel();
        for (int i=1; i<bsicontent.getMassnahmenKatalog().get("M 7 Datenschutz").size(); i++){
        	defaultListModelM7.addElement(getKey(bsicontent.getMassnahmenKatalog().get("M 7 Datenschutz").keySet(), i));
        }
        listM7.setModel(defaultListModelM7);
        JScrollPane scrollPaneM7 = new JScrollPane(listM7);
        scrollPaneM7.setPreferredSize(new Dimension(23, 23));
        scrollPaneM7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelM7.add(scrollPaneM7, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        tabbedPane.addTab("M7", null, panelM7);
        JPanel paneM7 = new JPanel();
	    paneM7.setOpaque(false);
	    paneM7.add(tabButtonM7);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, paneM7);
        //----
        
        contentPane.add(tabbedPane, new GridBagConstraints(0, 2, 5, 2, 0.0, 0.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        
        comboBox = new JComboBox();
        DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
        defaultComboBoxModel.addElement("B_1_Übergreifende_Aspekte");
        defaultComboBoxModel.addElement("B_2_Infrastruktur");
        defaultComboBoxModel.addElement("B_3_IT-Systeme");
        defaultComboBoxModel.addElement("B_4_Netze");
        defaultComboBoxModel.addElement("B_5_Anwendungen");
        contentPane.add(comboBox, new GridBagConstraints(0, 1, 1, 2, 0.0, 0.0, 16, 1, new Insets(0, 0, 0, 0), 0, 0));

        JButton button = new JButton();
        button.setText("add to Repository");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	RepositoryHelper.allG.removeAllElements();
            	RepositoryHelper.allM.removeAllElements();
            	RepositoryHelper.allG.addAll(RepositoryHelper.g1);
            	RepositoryHelper.allG.addAll(RepositoryHelper.g2);
            	RepositoryHelper.allG.addAll(RepositoryHelper.g3);
            	RepositoryHelper.allG.addAll(RepositoryHelper.g4);
            	RepositoryHelper.allG.addAll(RepositoryHelper.g5);
            	RepositoryHelper.allG.addAll(RepositoryHelper.g6);
            	RepositoryHelper.allM.addAll(RepositoryHelper.m1);
            	RepositoryHelper.allM.addAll(RepositoryHelper.m2);
            	RepositoryHelper.allM.addAll(RepositoryHelper.m3);
            	RepositoryHelper.allM.addAll(RepositoryHelper.m4);
            	RepositoryHelper.allM.addAll(RepositoryHelper.m5);
            	RepositoryHelper.allM.addAll(RepositoryHelper.m6);
            	RepositoryHelper.allM.addAll(RepositoryHelper.m7);
            	SystemVerificationLoader.getGui().getRepositoryHelper().resetAllVectors();
            	SystemVerificationLoader.getGui().getRepositoryHelper().addUMLsecPatternToXml(SystemVerificationLoader.getGui().getRepositoryFile(), RepositoryHelper.allG, RepositoryHelper.allM, textField.getText(), comboBox.getSelectedItem().toString());
            	
            }
        });
 
        contentPane.add(button, new GridBagConstraints(4, 0, 1, 3, 0.0, 0.0, 15, 2, new Insets(0, 0, 0, 0), 0, 0));
        textField = new JTextField();
        textField.setColumns(8);
        textField.setText("enter Patternname");
        contentPane.add(textField, new GridBagConstraints(2, 1, 1, 2, 0.0, 0.0, 15, 1, new Insets(0, 0, 0, 0), 0, 0));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setTitle("UMLsecPattern");
        frame.setBounds(new Rectangle(500, 0, 500, 677));
		
        frame.setVisible(true);        

	}

	/**
	 * erstellt Frame zum Pattern löschen
	 */
	public void delPatternFrame(){
		JFrame frame = new JFrame();
        JPanel contentPane = (JPanel) frame.getContentPane();
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{1, 20, 36};
        gridBagLayout.rowHeights = new int[]{0, 28, 20};
        gridBagLayout.columnWeights = new double[]{0, 1, 0};
        gridBagLayout.rowWeights = new double[]{0, 0, 1};
        contentPane.setLayout(gridBagLayout);

        delList = new JList();
        delListModel = new DefaultListModel();
        //add existing pattern from xml to list
        patternList = SystemVerificationLoader.getGui().getRepositoryHelper().getPatternList( SystemVerificationLoader.getGui().getRepositoryFile());
        for (int i = 0 ;i < patternList.size(); i++){
        	delListModel.addElement(patternList.elementAt(i).toString());
        }
        delList.setModel(delListModel);

        JScrollPane scrollPane = new JScrollPane(delList);
        scrollPane.setPreferredSize(new Dimension(23, 23));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(scrollPane, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));

        JButton delButton = new JButton();
        delButton.setText("delete");
        ActionListener delButtonListener = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  // get selected
	    	  int idx[] = delList.getSelectedIndices();
	    	  for (int i =0; i<idx.length; i++){
	    		  SystemVerificationLoader.getGui().getRepositoryHelper().delPattern(SystemVerificationLoader.getGui().getRepositoryFile(),delListModel.elementAt(i).toString());
	    	  }
	    	patternList = SystemVerificationLoader.getGui().getRepositoryHelper().getPatternList( SystemVerificationLoader.getGui().getRepositoryFile());
	    	for (int j = 0 ;j < patternList.size(); j++){
  	        	delListModel.addElement(patternList.elementAt(j).toString());
  	        }
  	        delList.setModel(delListModel);
  	        delList.repaint();
	    	}
	    };
	    delButton.addActionListener(delButtonListener);
        contentPane.add(delButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setTitle("delete Pattern");
        frame.setBounds(new Rectangle(500, 0, 250, 344));
        frame.setVisible(true);
	}
	
	private String getKey(Set<String> keySet, int i){
		int count = 0;
		String key = "";
		for (Iterator<String> it = keySet.iterator();it.hasNext();){
			if(i == count){
				return key;
			}
			count++;
			key = it.next();
		}
		return null;
	}

	/**
	 * holt BSI Katalog Inhalt
	 * @return BsiContent
	 */
	public BsiContent getBsicontent() {

		if(bsicontent == null)
			bsicontent = new BsiContent();
		
		return bsicontent;
	}
}
