package org.omg.uml.behavioralelements.commonbehavior;

/**
 * Instance object instance interface.
 */
public interface Instance extends org.omg.uml.foundation.core.ModelElement {
    /**
     * Returns the value of reference classifier.
     * @return Value of reference classifier.
     */
    public java.util.Collection getClassifier();
    /**
     * Returns the value of reference linkEnd.
     * @return Value of reference linkEnd.
     */
    public java.util.Collection getLinkEnd();
    /**
     * Returns the value of reference slot.
     * @return Value of reference slot.
     */
    public java.util.Collection getSlot();
    /**
     * Returns the value of reference componentInstance.
     * @return Value of reference componentInstance.
     */
    public org.omg.uml.behavioralelements.commonbehavior.ComponentInstance getComponentInstance();
    /**
     * Sets the value of reference componentInstance. See {@link #getComponentInstance} 
     * for description on the reference.
     * @param newValue New value to be set.
     */
    public void setComponentInstance(org.omg.uml.behavioralelements.commonbehavior.ComponentInstance newValue);
    /**
     * Returns the value of reference ownedInstance.
     * @return Value of reference ownedInstance.
     */
    public java.util.Collection getOwnedInstance();
    /**
     * Returns the value of reference ownedLink.
     * @return Value of reference ownedLink.
     */
    public java.util.Collection getOwnedLink();
}
