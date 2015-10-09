package org.omg.uml.behavioralelements.collaborations;

/**
 * InteractionInstanceSet object instance interface.
 */
public interface InteractionInstanceSet extends org.omg.uml.foundation.core.ModelElement {
    /**
     * Returns the value of reference context.
     * @return Value of reference context.
     */
    public org.omg.uml.behavioralelements.collaborations.CollaborationInstanceSet getContext();
    /**
     * Sets the value of reference context. See {@link #getContext} for description 
     * on the reference.
     * @param newValue New value to be set.
     */
    public void setContext(org.omg.uml.behavioralelements.collaborations.CollaborationInstanceSet newValue);
    /**
     * Returns the value of reference interaction.
     * @return Value of reference interaction.
     */
    public org.omg.uml.behavioralelements.collaborations.Interaction getInteraction();
    /**
     * Sets the value of reference interaction. See {@link #getInteraction} for 
     * description on the reference.
     * @param newValue New value to be set.
     */
    public void setInteraction(org.omg.uml.behavioralelements.collaborations.Interaction newValue);
    /**
     * Returns the value of reference participatingStimulus.
     * @return Value of reference participatingStimulus.
     */
    public java.util.Collection getParticipatingStimulus();
}
