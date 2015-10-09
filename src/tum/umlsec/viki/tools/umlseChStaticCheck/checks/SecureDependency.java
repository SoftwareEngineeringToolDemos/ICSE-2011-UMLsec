// define the packagefolder
package tum.umlsec.viki.tools.umlseChStaticCheck.checks;

// important imports
import java.util.*;

import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;
import org.omg.uml.foundation.core.UmlClass;
import tum.umlsec.viki.UmlTypeScanner;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;
import tum.umlsec.viki.tools.umlseChNotationAnalyser.checks.StructUMLseChDelta;




// Checking Class extends the StaticCheckerBase - forces to implement the "check"-Method
public class SecureDependency extends StaticCheckerBase {
	
	private static Logger logger=Logger.getLogger("umlseChStaticCheck");

    //Output Textbox Stream
    ITextOutput textOutput;
    IMdrContainer mdrContainer;

    //datastructure for UMLseCh-Delta
    Vector<StructUMLseChDelta> add = new Vector();
    Vector<StructUMLseChDelta> del = new Vector();
    Vector<StructUMLseChDelta> subs = new Vector();


    // Secure Dependency check-method
    public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
        boolean fillbool, checkAbool, checkBbool, checkCbool;
        this.textOutput = _textOutput;
        this.mdrContainer = _mdrContainer;

        textOutput.writeLn("####[ UMLseCh Secure Dependency check ]####");
        textOutput.writeLn();
        textOutput.writeLn("##########################################################");
        textOutput.writeLn("# STEP 1:                                                #");
        textOutput.writeLn("# Filling the delta-vectors: add, delete and substitute  #");
        textOutput.writeLn("##########################################################");
        textOutput.writeLn();

        fillbool = this.fillVectors();

        textOutput.writeLn();
        textOutput.writeLn("##########################################################");
        textOutput.writeLn("# STEP 2a:                                               #");
        textOutput.writeLn("# Checking that the same model element is not            #");
        textOutput.writeLn("# added and deleted (by UMLseCh) at the same time.       #");
        textOutput.writeLn("##########################################################");
        textOutput.writeLn();

        checkAbool = this.checkA();

        textOutput.writeLn();
        textOutput.writeLn("##########################################################");
        textOutput.writeLn("# STEP 2b:                                               #");
        textOutput.writeLn("# Checking that no element of a substition is an element #");
        textOutput.writeLn("# of the add or delete vector.                           #");
        textOutput.writeLn("##########################################################");
        textOutput.writeLn();

        checkBbool = this.checkB();

        textOutput.writeLn();
        textOutput.writeLn("##########################################################");
        textOutput.writeLn("# STEP 3:                                                #");
        textOutput.writeLn("# Checking the secure dependency UMLseCh conditions.     #");
        textOutput.writeLn("##########################################################");
        textOutput.writeLn();

        checkCbool = this.checkC();

        textOutput.writeLn();
        textOutput.writeLn("##########################################################");
        textOutput.writeLn("# STEP 4:                                                #");
        textOutput.writeLn("# Conclusion:                                            #");
        textOutput.writeLn("##########################################################");
        textOutput.writeLn();

        if (fillbool && checkAbool && checkBbool && checkCbool){
          textOutput.writeLn("[SUCC] 'Secure Dependency' conditions are");
          textOutput.writeLn("       fulfilled after UMLseCh evolution. See above");
          textOutput.writeLn("       for more informations.");
          return true;
        }else{
          textOutput.writeLn("[FAIL] 'Secure Dependency' conditions are");
          textOutput.writeLn("       violated after UMLseCh evolution. See above");
          textOutput.writeLn("       for more informations.");
          return false;
        }
    }

    //--------------------------------------------------------------------------
    private boolean fillVectors(){
        CorePackage corePackage;
        corePackage = mdrContainer.getUmlPackage().getCore();

        // Filling the vectors with all UMLseCh relevant TaggedValues
        TaggedValueClass taggedValueClass = (TaggedValueClass)corePackage.getTaggedValue();
        for (Iterator iter1 = taggedValueClass.refAllOfClass().iterator(); iter1.hasNext();){
            TaggedValue tagValue = (TaggedValue) iter1.next();

            if(tagValue!=null && tagValue.getType()!=null && tagValue.getType().getName()!=null && (tagValue.getType().getName().equals("ref")
                || tagValue.getType().getName().equals("pattern")
                || tagValue.getType().getName().startsWith("substitute")
                || tagValue.getType().getName().startsWith("add")
                || tagValue.getType().getName().startsWith("delete")
                || tagValue.getType().getName().startsWith("["))){

                //adds the elements to the delta-datastructure
                if (tagValue.getType().getName().startsWith("add"))         add.addElement(new StructUMLseChDelta(tagValue, tagValue.getDataValue().iterator().next().toString()));
                if (tagValue.getType().getName().startsWith("substitute")) subs.addElement(new StructUMLseChDelta(tagValue, tagValue.getDataValue().iterator().next().toString()));
                if (tagValue.getType().getName().startsWith("delete"))      del.addElement(new StructUMLseChDelta(tagValue, tagValue.getDataValue().iterator().next().toString()));
            }
        }

        // Show an overview of found UMLseCh TaggedValues
        for(StructUMLseChDelta item: add){
            for(String ausgabe: item.getValues()){
                textOutput.writeLn("[add] " + ausgabe);
            }
        }
        for(StructUMLseChDelta item: del){
            for(String ausgabe: item.getValues()){
                textOutput.writeLn("[del] " + ausgabe);
            }
        }
        for(StructUMLseChDelta item: subs){
            for(String ausgabe: item.getValues()){
                textOutput.write("[sub] " + ausgabe);
            }
            TaggedValue pat = (TaggedValue) item.getPattern();
            if(pat != null) textOutput.writeLn("  [pattern] " + pat.getDataValue().iterator().next());
        }


        return true;
    }

    //--------------------------------------------------------------------------
    private boolean checkA(){
        for(StructUMLseChDelta item: add){
            for(StructUMLseChDelta item2: del){
                if (item.equals(item2)){
                  textOutput.writeLn("[FAIL] add-vector-element with the value '" + item.getValue() + "' and parent");
                  textOutput.writeLn("       '" + item.getParent().getName() + "' equals an delete-vector-element with the same");
                  textOutput.writeLn("       value and parent!");
                  return false;
                }
            }
        }
        textOutput.writeLn("[SUCC] The add- and delete-vector are disjoint.");
        textOutput.writeLn("       The check was succesful.");
        return true;
    }

    //--------------------------------------------------------------------------
    private boolean checkB(){
      
      for(StructUMLseChDelta item2: subs){
        TaggedValue pat = (TaggedValue) item2.getPattern();
        
        // Check Add - Subs
        for(StructUMLseChDelta item: add){
          if(pat != null){
            if(item.getValue().equals(pat.getDataValue().iterator().next())){
              textOutput.writeLn("[FAIL] The pattern of a substitute-vector-element with the value");
              textOutput.writeLn("       '" + item.getValue() +  "' and the parent '" + item.getParent().getName() + "'");
              textOutput.writeLn("       equals an element of the add-vector with same value and parent");
              return false;
            }
            if(item.getValue().equals(item2.getValue())){
              textOutput.writeLn("[FAIL] The substitute-vector-element with the value '" + item.getValue() +  "'");
              textOutput.writeLn("       and the parent '" + item.getParent().getName() + "' equals an add-vector-element");
              textOutput.writeLn("       with same value and parent");
              return false;
            }
          }
        }

        // Check Del - Subs
        for(StructUMLseChDelta item: del){
          if(pat != null){
            if(item.getValue().equals(pat.getDataValue().iterator().next())){
              textOutput.writeLn("[FAIL] The pattern of a substitute-vector-element with the value");
              textOutput.writeLn("       '" + item.getValue() +  "' and the parent '" + item.getParent().getName() + "'");
              textOutput.writeLn("       equals an element of the delete-vector with same value and parent");
              return false;
            }
            if(item.getValue().equals(item2.getValue())){
              textOutput.writeLn("[FAIL] The substitute-vector-element with the value '" + item.getValue() +  "'");
              textOutput.writeLn("       and the parent '" + item.getParent().getName() + "' equals an elementof the");
              textOutput.writeLn("       delete-vector with same value and parent");
              return false;
            }
          }
        }
      }
      textOutput.writeLn("[SUCC] The substitute-vector, its patterns and the delete-");
      textOutput.writeLn("       or add-vector are disjoint. The check was succesful.");
      return true;
    }

    //--------------------------------------------------------------------------
    private boolean checkC(){

      //#################################
      //### STEP 1: Find and delete UMLseCh Tags which will be deleted anyway (because the parent will be deleted)
      UmlTypeScanner umlident = new UmlTypeScanner();
	  
      // Vectors for elements to be removed
      Vector<StructUMLseChDelta> addToDelete = new Vector();
      Vector<StructUMLseChDelta> delToDelete = new Vector();
      Vector<StructUMLseChDelta> subsToDelete = new Vector();

      textOutput.writeLn("[Step 3a] Searching for deletion of complex model elements:");
      boolean delcheck = false;

      for(StructUMLseChDelta delitem: del){

        // search for UMLseCh which wants to delete a UMLClass
        if(umlident.isElementType(this.mdrContainer.getUmlPackage().getCore(), delitem.getValue(), "UmlClass")){
          textOutput.writeLn("  [!!!] Found deletion of a class: every vector element belonging to this");
          textOutput.writeLn("        class has to be deleted from the delta-vectors.");
          delcheck = true;
          // Find and delete delete-UMLseCh Tags with similar Parent
          for(StructUMLseChDelta delitem2: del){
            if(!delitem2.equals(delitem) && delitem2.getParent().getName().equals(delitem.getValue())){
              textOutput.writeLn("  [!->] deleting 'delete = " + delitem2.getValue() + "'");
              delToDelete.add(delitem2); // add to removelist
            }
          }
          // Find and delete add-UMLseCh Tags with similar Parent
          for(StructUMLseChDelta additem2: add){
            if(additem2.getParent().getName().equals(delitem.getValue())){
              textOutput.writeLn("  [!->] deleting 'add = " + additem2.getValue() + "'");
              addToDelete.add(additem2); // add to removelist
            }
          }
          // Find and delete subs-UMLseCh Tags with similar Parent
          for(StructUMLseChDelta subitem2: subs){
            if(subitem2.getParent().getName().equals(delitem.getValue())){
              textOutput.writeLn("  [!->] deleting 'substitute = " + subitem2.getValue() + "'");
              subsToDelete.add(subitem2); // add to removelist
            }
          }
        }

        // search for UMLseCh which wants to delete a Dependency
        if(umlident.isElementType(this.mdrContainer.getUmlPackage().getCore(), delitem.getValue(), "Dependency")){
          textOutput.writeLn("  [!!!] Found deletion of a dependency: every vector element belonging to");
          textOutput.writeLn("        this dependency has to be deleted from the delta-vectors.");
          delcheck = true;
          // Find and delete delete-UMLseCh Tags with similar Parent
          for(StructUMLseChDelta delitem2: del){
            if(!delitem2.equals(delitem) && delitem2.getParent().getName().equals(delitem.getValue())){
              textOutput.writeLn("  [!->] deleting 'delete = " + delitem2.getValue() + "'");
              delToDelete.add(delitem2); // add to removelist
            }
          }
          // Find and delete add-UMLseCh Tags with similar Parent
          for(StructUMLseChDelta additem2: add){
            if(additem2.getParent().getName().equals(delitem.getValue())){
              textOutput.writeLn("  [!->] deleting 'add = " + additem2.getValue() + "'");
              addToDelete.add(additem2); // add to removelist
            }
          }
          // Find and delete subs-UMLseCh Tags with similar Parent
          for(StructUMLseChDelta subitem2: subs){
            if(subitem2.getParent().getName().equals(delitem.getValue())){
              textOutput.writeLn("  [!->] deleting 'substitute = " + subitem2.getValue() + "'");
              subsToDelete.add(subitem2); // add to removelist
            }
          }

        }
      }

      // Now delete the subelements
      for(StructUMLseChDelta additem: addToDelete){
        add.remove(additem);
      }
      for(StructUMLseChDelta delitem: delToDelete){
        del.remove(delitem);
      }
      for(StructUMLseChDelta subitem: subsToDelete){
        subs.remove(subitem);
      }

      if(delcheck){
        textOutput.writeLn("  ... cleaning delta-vectors complete!");
      }else{
        textOutput.writeLn("  Nothing to delete ...");
      }
      textOutput.writeLn("");

      //#################################
      //### STEP 2: ITERATE OVER ALL DELS + CHECK

      textOutput.writeLn("[Step 3b] Checking the delete-vector");
      boolean bool_del = true;

      // find UMLseCh Tags associated to Operations
      for(StructUMLseChDelta delitem: del){
        if(umlident.isElementType(this.mdrContainer.getUmlPackage().getCore(), umlident.umlseChOperation(delitem.getValue()), "Operation")){
          textOutput.writeLn("  [!!!] Found UMLseCh tagvalue associated to the operation: " + delitem.getValue());
          textOutput.writeLn("  [Info] parent model element: " + delitem.getParent().getName());

          //Check that the Tag is a TagValue of an UmlClass
          if(umlident.modelElementType(delitem.getParent().getClass().getName()).equals("UmlClass")){
            for(Iterator iter_dep = this.mdrContainer.getUmlPackage().getCore().getDependency().refAllOfClass().iterator(); iter_dep.hasNext();){
              Dependency depend     = (Dependency) iter_dep.next();
              UmlClass client       = (UmlClass) depend.getClient().iterator().next();
              UmlClass supplier     = (UmlClass) depend.getSupplier().iterator().next();
              Stereotype depend_stereo  = (Stereotype) depend.getStereotype().iterator().next();
              String depend_stereo_value = "";
              if(depend_stereo != null) depend_stereo_value = depend_stereo.getName();
              //Only call and send dependencies
              if(  (depend_stereo_value.equals("call") || depend_stereo_value.equals("send"))
              &&(client.equals(delitem.getParent()) || supplier.equals(delitem.getParent())) ){
                boolean bool_client   = false;
                boolean bool_supplier = false;
                textOutput.writeLn("  [!->] Checking both classes belonging to dependency " + depend.getName() + ": [" + client.getName() + "-->" + supplier.getName() + "].");
                textOutput.writeLn("        Both classes should have the same UMLseCh delete-tagvalue.");

                //Check that in the other Class the security Tag is also removed
                for(StructUMLseChDelta delitem2: del){
                  //The other class has to delete the same securityTag for the same method ...
                  if(delitem.getValue().equals(delitem2.getValue()) && delitem2.getParent().equals(client)){
                    textOutput.writeLn("        [ok] " + client.getName() + " has 'delete = " + delitem.getValue() + "'");
                    bool_client = true;
                  }
                  if(delitem.getValue().equals(delitem2.getValue()) && delitem2.getParent().equals(supplier)){
                    textOutput.writeLn("        [ok] " + supplier.getName() + " has 'delete = " + delitem.getValue() + "'");
                    bool_supplier = true;
                  }
                  //... or must be deleted!
                  if(supplier.equals(delitem.getParent()) && delitem2.getValue().equals(client.getName())){
                    textOutput.writeLn("        [ok] Because " + client.getName() + " and all its dependencies will be deleted.");
                    bool_supplier = true;
                  }
                  if(client.equals(delitem.getParent()) && delitem2.getValue().equals(supplier.getName())){
                    textOutput.writeLn("        [ok] Because " + supplier.getName() + " and all its dependencies will be deleted.");
                    bool_supplier = true;
                  }
                }//-End of Check that in the other ...
                if(!bool_client){
                  textOutput.writeLn("        [xx] " + client.getName() + " has not the delete-UMLseCh-tagvalue which");
                  textOutput.writeLn("             " + supplier.getName() + " has.");
                }
                if(!bool_supplier){
                  textOutput.writeLn("        [xx] " + supplier.getName() + " has not the delete-UMLseCh-tagvalue which");
                  textOutput.writeLn("             " + client.getName() + " has.");
                }
                bool_del = bool_del & bool_client & bool_supplier;

              }
            }
          }else{
            textOutput.writeLn("[ERROR] UMLseCh delete-tagvalue referring to an operation but its parent is no class!");
          }//-End of Check that the Tag is a TagValue of an UMLClass
        }
      }
      // Conclusion of Iterating over als del-Deltas
      if(bool_del) textOutput.writeLn("  [SUCC] the UMLseCh delete-delta satisfies the secure dependency conditions.");
      else textOutput.writeLn("  [FAIL] the UMLseCh delete-delta does not satisfy the secure dependency conditions.");
      textOutput.writeLn("");

      //#################################
      //### STEP 3: ITERATE OVER ALL ADDS + CHECK

      textOutput.writeLn("[Step 3b] Checking the add-vector");
      boolean bool_add = true;

      // find UMLseCh Tags associated to Operations
      for(StructUMLseChDelta additem: add){
        if(umlident.isElementType(this.mdrContainer.getUmlPackage().getCore(), umlident.umlseChOperation(additem.getValue()), "Operation")){
          textOutput.writeLn("  [!!!] Found UMLseCh tagvalue associated to the operation: " + additem.getValue());
          textOutput.writeLn("  [Info] parent model element: " + additem.getParent().getName());

          //Check that the Tag is a TagValue of an UmlClass
          if(umlident.modelElementType(additem.getParent().getClass().getName()).equals("UmlClass")){
            for(Iterator iter_dep = this.mdrContainer.getUmlPackage().getCore().getDependency().refAllOfClass().iterator(); iter_dep.hasNext();){
              Dependency depend     = (Dependency) iter_dep.next();
              UmlClass client       = (UmlClass) depend.getClient().iterator().next();
              UmlClass supplier     = (UmlClass) depend.getSupplier().iterator().next();
              Stereotype depend_stereo  = (Stereotype) depend.getStereotype().iterator().next();
              String depend_stereo_value = "";
              if(depend_stereo != null) depend_stereo_value = depend_stereo.getName();
              //Only call and send dependencies
              if(  (depend_stereo_value.equals("call") || depend_stereo_value.equals("send"))
                && (client.equals(additem.getParent()) || supplier.equals(additem.getParent())) ){
                boolean bool_client   = false;
                boolean bool_supplier = false;
                textOutput.writeLn("  [!->] Checking both classes belonging to dependency " + depend.getName() + ": [" + client.getName() + "-->" + supplier.getName() + "].");
                textOutput.writeLn("        Both classes should have the same UMLseCh add-tagvalue.");

                //Check that in the other Class the security Tag is also removed
                for(StructUMLseChDelta additem2: add){
                  //The other class has to delete the same securityTag for the same method ...
                  if(additem.getValue().equals(additem2.getValue()) && additem2.getParent().equals(client)){
                    textOutput.writeLn("        [ok] " + client.getName() + " has 'add = " + additem.getValue() + "'");
                    bool_client = true;
                  }
                  if(additem.getValue().equals(additem2.getValue()) && additem2.getParent().equals(supplier)){
                    textOutput.writeLn("        [ok] " + client.getName() + " has 'add = " + additem.getValue() + "'");
                    bool_supplier = true;
                  }
                }
                for(StructUMLseChDelta delitem2: del){
                  if(supplier.equals(additem.getParent()) && delitem2.getValue().equals(client.getName())){
                    textOutput.writeLn("        [ok] Because " + client.getName() + " and all its dependencies will be deleted");
                    bool_supplier = true;
                  }
                  if(client.equals(additem.getParent()) && delitem2.getValue().equals(supplier.getName())){
                    textOutput.writeLn("        [ok] Because " + supplier.getName() + " and all its dependencies will be deleted");
                    bool_supplier = true;
                  }
                }//-End of Check that in the other ...
                if(!bool_client){
                  textOutput.writeLn("        [xx] " + client.getName() + " has not the add-UMLseCh-tagvalue which");
                  textOutput.writeLn("             " + supplier.getName() + " has.");
                }
                if(!bool_supplier){
                  textOutput.writeLn("        [xx] " + supplier.getName() + " has not the add-UMLseCh-tagvalue which");
                  textOutput.writeLn("             " + client.getName() + " has.");
                }
                bool_add = bool_add & bool_client & bool_supplier;

              }
            }
          }else{
            textOutput.writeLn("[ERROR] UMLseCh delete-tagvalue referring to an operation but its parent is no class!");
          }//-End of Check that the Tag is a TagValue of an UMLClass
        }
      }
      // Conclusion of Iterating over als del-Deltas
      if(bool_add) textOutput.writeLn("  [SUCC] the UMLseCh add-delta satisfies the secure dependency conditions");
      else textOutput.writeLn("  [FAIL] the UMLseCh add-delta does not satisfy the secure dependency conditions");
      textOutput.writeLn("");

      //#################################
      //### STEP 4: ITERATE OVER ALL SUBS + CHECK

      textOutput.writeLn("[Step 3c] Checking the substitute-vector");
      boolean bool_sub = true;

      for(StructUMLseChDelta subitem: subs){
        TaggedValue pattern = (TaggedValue) subitem.getPattern();
        String pattern_value = (String) pattern.getDataValue().iterator().next();
        // find UMLseCh Tags associated to Operations
        if(umlident.isElementType(this.mdrContainer.getUmlPackage().getCore(), umlident.umlseChOperation(pattern_value), "Operation")){
          textOutput.writeLn("  [!!!] Found UMLseCh tagvalue associated to the operation: " + subitem.getValue());
          textOutput.writeLn("  [Info] parent model element: " + subitem.getParent().getName());

          //Check that the Tag is a TagValue of an UmlClass
          if(umlident.modelElementType(subitem.getParent().getClass().getName()).equals("UmlClass")){
            for(Iterator iter_dep = this.mdrContainer.getUmlPackage().getCore().getDependency().refAllOfClass().iterator(); iter_dep.hasNext();){
              Dependency depend     = (Dependency) iter_dep.next();
              UmlClass client       = (UmlClass) depend.getClient().iterator().next();
              UmlClass supplier     = (UmlClass) depend.getSupplier().iterator().next();
              Stereotype depend_stereo  = (Stereotype) depend.getStereotype().iterator().next();
              String depend_stereo_value = "";
              if(depend_stereo != null) depend_stereo_value = depend_stereo.getName();
              //Only call and send dependencies
              if(  (depend_stereo_value.equals("call") || depend_stereo_value.equals("send"))
              &&(client.equals(subitem.getParent()) || supplier.equals(subitem.getParent())) ){
                boolean bool_client   = false;
                boolean bool_supplier = false;
                textOutput.writeLn("  [!->] Checking both classes belonging to dependency " + depend.getName() + ": [" + client.getName() + "-->" + supplier.getName() + "].");
                textOutput.writeLn("        Both classes should have the same UMLseCh add-tagvalue.");

                //Check that in the other Class the security Tag is also removed
                for(StructUMLseChDelta subitem2: subs){
                  TaggedValue pattern2 = (TaggedValue) subitem2.getPattern();
                  String pattern2_value = (String) pattern2.getDataValue().iterator().next();
                  //The other class has to delete the same securityTag for the same method ...
                  if(subitem.getValue().equals(subitem2.getValue()) && pattern_value.equals(pattern2_value) && subitem2.getParent().equals(client)){
                    textOutput.writeLn("        [ok] " + client.getName() + " has 'substitute = " + subitem.getValue() + "' and 'pattern = " + pattern_value + "'");
                    bool_client = true;
                  }
                  if(subitem.getValue().equals(subitem2.getValue()) && pattern_value.equals(pattern2_value) && subitem2.getParent().equals(supplier)){
                    textOutput.writeLn("        [ok] " + supplier.getName() + " has 'substitute = " + subitem.getValue() + "' and 'pattern = " + pattern_value + "'");
                    bool_supplier = true;
                  }
                }
                for(StructUMLseChDelta delitem2: del){
                  if(supplier.equals(subitem.getParent()) && delitem2.getValue().equals(client.getName())){
                    textOutput.writeLn("        [ok] Because " + client.getName() + " and all its dependencies will be deleted");
                    bool_supplier = true;
                  }
                  if(client.equals(subitem.getParent()) && delitem2.getValue().equals(supplier.getName())){
                    textOutput.writeLn("        [ok] Because " + supplier.getName() + " and all its dependencies will be deleted");
                    bool_supplier = true;
                  }
                }//-End of Check that in the other ...
                if(!bool_client){
                  textOutput.writeLn("        [xx] " + client.getName() + " has not the UMLseCh substitute-tagvalue or");
                  textOutput.writeLn("             pattern which " + supplier.getName() + " has.");
                }
                if(!bool_supplier){
                  textOutput.writeLn("        [xx] " + supplier.getName() + " has not the UMLseCh substitute-tagvalue or");
                  textOutput.writeLn("             pattern which " + client.getName() + " has.");
                }
                bool_sub = bool_sub & bool_client & bool_supplier;

              }
            }
          }else{
            textOutput.writeLn("[ERROR] UMLseCh delete-tagvalue referring to an operation but its parent is no class!");
          }//-End of Check that the Tag is a TagValue of an UMLClass
        }
      }

      // Conclusion of Iterating over als del-Deltas
      if(bool_sub) textOutput.writeLn("  [SUCC] the UMLseCh substitution-delta satisfies the secure dependency conditions");
      else textOutput.writeLn("  [FAIL] the UMLseCh substitution-delta does not satisfy the secure dependency conditions");
      textOutput.writeLn("");

      //#################################
      //### STEP X: Conclusion

      boolean bool_conclusion = bool_del & bool_add & bool_sub;
      textOutput.writeLn("[Step 3d] Analyse all vector-check results:");
      if(bool_conclusion){
        textOutput.writeLn("  [SUCC] The secure dependency UMLseCh conditions are fulfilled.");
      }else{
        textOutput.writeLn("  [FAIL] The secure dependency UMLseCh conditions are violated.");
      }
      return bool_conclusion;

    }//End of Check C

}//End of Class
