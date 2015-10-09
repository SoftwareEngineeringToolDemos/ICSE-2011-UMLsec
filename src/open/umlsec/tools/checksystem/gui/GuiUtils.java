package open.umlsec.tools.checksystem.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

/**
 * 
 * @author 
 *
 */
@SuppressWarnings("serial")
class CheckListItem extends JCheckBox
{
   private String  label;
   private boolean isSelected = false;
 
   public CheckListItem(String label)
   {
      this.label = label;
   }
 
   public boolean isSelected()
   {
      return isSelected;
   }
 
   public void setSelected(boolean isSelected)
   {
      this.isSelected = isSelected;
   }

   public String toString()
   {
      return label;
   }
   
}
 
// Handles rendering cells in the list using a check box
@SuppressWarnings("serial")
class CheckListRenderer extends JCheckBox implements ListCellRenderer
{
   public Component getListCellRendererComponent(
         JList list, Object value, int index,
         boolean isSelected, boolean hasFocus)
   {
      setEnabled(list.isEnabled());
      setSelected(((CheckListItem)value).isSelected());
      setFont(list.getFont());
      setBackground(list.getBackground());
      setForeground(list.getForeground());
      setText(value.toString());
      return this;
   }
}

class ActionJList extends MouseAdapter{
	  protected JList list;
	    
	  public ActionJList(JList l){
	   list = l;
	   }
	    
	  public void mouseClicked(MouseEvent e){
	   if(e.getClickCount() == 1){
	     int index = list.locationToIndex(e.getPoint());
	     ListModel dlm = list.getModel();
	     Object item = dlm.getElementAt(index);;
	     list.ensureIndexIsVisible(index);
	     System.out.println("Double clicked on " + item);
	     }
	   }
}

class PopUpMenu{
	JPopupMenu Pmenu;
	JMenuItem menuItem;

	public PopUpMenu(){
		JFrame frame = new JFrame("Please select");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Pmenu = new JPopupMenu();
		menuItem = new JMenuItem("Enable");
		Pmenu.add(menuItem);
		menuItem = new JMenuItem("Disable");
		Pmenu.add(menuItem);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){}
		});
		frame.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent Me){
				if(Me.isPopupTrigger()){
					Pmenu.show(Me.getComponent(), Me.getX(), Me.getY());
				}
			}
		});
		frame.setSize(400,400);
		frame.setVisible(true);
	}
}

