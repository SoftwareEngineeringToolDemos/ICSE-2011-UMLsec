package org.omg.uml.foundation.core;

/**
 * A_type_typedValue association proxy interface.
 */
public interface ATypeTypedValue extends javax.jmi.reflect.RefAssociation {
    /**
     * Queries whether a link currently exists between a given pair of instance 
     * objects in the associations link set.
     * @param type Value of the first association end.
     * @param typedValue Value of the second association end.
     * @return Returns true if the queried link exists.
     */
    public boolean exists(org.omg.uml.foundation.core.TagDefinition type, org.omg.uml.foundation.core.TaggedValue typedValue);
    /**
     * Queries the instance object that is related to a particular instance object 
     * by a link in the current associations link set.
     * @param type Required value of the first association end.
     * @return Related object or <code>null</code> if none exists.
     */
    public org.omg.uml.foundation.core.TagDefinition getType(org.omg.uml.foundation.core.TaggedValue typedValue);
    /**
     * Queries the instance objects that are related to a particular instance 
     * object by a link in the current associations link set.
     * @param typedValue Required value of the second association end.
     * @return Collection of related objects.
     */
    public java.util.Collection getTypedValue(org.omg.uml.foundation.core.TagDefinition type);
    /**
     * Creates a link between the pair of instance objects in the associations 
     * link set.
     * @param type Value of the first association end.
     * @param typedValue Value of the second association end.
     */
    public boolean add(org.omg.uml.foundation.core.TagDefinition type, org.omg.uml.foundation.core.TaggedValue typedValue);
    /**
     * Removes a link between a pair of instance objects in the current associations 
     * link set.
     * @param type Value of the first association end.
     * @param typedValue Value of the second association end.
     */
    public boolean remove(org.omg.uml.foundation.core.TagDefinition type, org.omg.uml.foundation.core.TaggedValue typedValue);
}
