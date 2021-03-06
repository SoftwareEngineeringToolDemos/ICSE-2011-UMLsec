package org.omg.uml.behavioralelements.commonbehavior;

/**
 * A_createAction_instantiation association proxy interface.
 */
public interface ACreateActionInstantiation extends javax.jmi.reflect.RefAssociation {
    /**
     * Queries whether a link currently exists between a given pair of instance 
     * objects in the associations link set.
     * @param createAction Value of the first association end.
     * @param instantiation Value of the second association end.
     * @return Returns true if the queried link exists.
     */
    public boolean exists(org.omg.uml.behavioralelements.commonbehavior.CreateAction createAction, org.omg.uml.foundation.core.Classifier instantiation);
    /**
     * Queries the instance objects that are related to a particular instance 
     * object by a link in the current associations link set.
     * @param createAction Required value of the first association end.
     * @return Collection of related objects.
     */
    public java.util.Collection getCreateAction(org.omg.uml.foundation.core.Classifier instantiation);
    /**
     * Queries the instance object that is related to a particular instance object 
     * by a link in the current associations link set.
     * @param instantiation Required value of the second association end.
     * @return Related object or <code>null</code> if none exists.
     */
    public org.omg.uml.foundation.core.Classifier getInstantiation(org.omg.uml.behavioralelements.commonbehavior.CreateAction createAction);
    /**
     * Creates a link between the pair of instance objects in the associations 
     * link set.
     * @param createAction Value of the first association end.
     * @param instantiation Value of the second association end.
     */
    public boolean add(org.omg.uml.behavioralelements.commonbehavior.CreateAction createAction, org.omg.uml.foundation.core.Classifier instantiation);
    /**
     * Removes a link between a pair of instance objects in the current associations 
     * link set.
     * @param createAction Value of the first association end.
     * @param instantiation Value of the second association end.
     */
    public boolean remove(org.omg.uml.behavioralelements.commonbehavior.CreateAction createAction, org.omg.uml.foundation.core.Classifier instantiation);
}
