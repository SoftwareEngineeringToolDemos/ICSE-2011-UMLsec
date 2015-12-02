//Containerclass for UMLseCh-Delta
package tum.umlsec.viki.tools.umlseChNotationAnalyser.checks;

import java.util.Iterator;
import org.omg.uml.foundation.core.*;
import tum.umlsec.viki.UmlTypeScanner;


public class StructUMLseChDelta {
    private ModelElement element;
    private String value;
    private UmlTypeScanner umlident = new UmlTypeScanner();

    // Constructor
    public StructUMLseChDelta(ModelElement _element, String _value) {
        this.element = _element;
        this.value = _value;
    }

   
    // Value methods
    public String getValue() {
        return this.value;
    }

    public String[] getValues() {
        String[] temp;
        temp = this.value.split(",");

        //deleting pre- or suffix spaces in the array
        for (String item: temp) item.trim();

        return temp;
    }

    public boolean setValue(String value) {
        this.value = value;
        return true;
    }

    // ModelElement methods
    public ModelElement getModelElement() {
        return this.element;
    }

    public boolean setModelElement(ModelElement element) {
        this.element = element;
        return true;
    }

    // ID method
    public String getID() {
        return umlident.modelElementID(this.element);
    }

    // Parent & path methods
    public ModelElement getParent() {
      TaggedValue tv = (TaggedValue) this.element;
      return tv.getModelElement();
    }
    
    public String getPath() {
      TaggedValue tv = (TaggedValue) this.element;
      return("[" + umlident.modelElementType(tv.getModelElement().getClass().getName()) + "] " + tv.getModelElement().getName() + " -> [TaggedValue] " + tv.getType().getName());
    }

    // Pattern method (only for Subs)
    public ModelElement getPattern(){
      TaggedValue tv = (TaggedValue) this.element;
      ModelElement me = tv.getModelElement();
      for(Iterator iter = me.getTaggedValue().iterator(); iter.hasNext();){
        TaggedValue pat = (TaggedValue) iter.next();
        if(pat.getType().getName().equals("pattern") && pat.getModelElement().equals(me)){
          return pat;
        }
      }
      return null;
    }

    // Typ method
    public String getType(){
        return umlident.modelElementType(this.element.getClass().getName());
    }

    public boolean equals(StructUMLseChDelta item){
      if(this.value.equals(item.getValue())
              && this.getParent().equals(item.getParent())
              && this.getType().equals(item.getType())){
              return true;
      }else{
        return false;
      }

        
    }
}