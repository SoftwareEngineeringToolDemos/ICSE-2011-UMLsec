package org.omg.uml.behavioralelements.collaborations;

/**
 * CollaborationInstanceSet object instance interface.
 */
public interface CollaborationInstanceSet extends org.omg.uml.foundation.core.ModelElement {
    /**
     * Returns the value of reference interactionInstanceSet.
     * @return Value of reference interactionInstanceSet.
     */
    public java.util.Collection getInteractionInstanceSet();
    /**
     * Returns the value of reference collaboration.
     * @return Value of reference collaboration.
     */
    public org.omg.uml.behavioralelements.collaborations.Collaboration getCollaboration();
    /**
     * Sets the value of reference collaboration. See {@link #getCollaboration} 
     * for description on the reference.
     * @param newValue New value to be set.
     */
    public void setCollaboration(org.omg.uml.behavioralelements.collaborations.Collaboration newValue);
    /**
     * Returns the value of reference participatingInstance.
     * @return Value of reference participatingInstance.
     */
    public java.util.Collection getParticipatingInstance();
    /**
     * Returns the value of reference participatingLink.
     * @return Value of reference participatingLink.
     */
    public java.util.Collection getParticipatingLink();
    /**
     * Returns the value of reference constrainingElement.
     * @return Value of reference constrainingElement.
     */
    public java.util.Collection getConstrainingElement();
}
