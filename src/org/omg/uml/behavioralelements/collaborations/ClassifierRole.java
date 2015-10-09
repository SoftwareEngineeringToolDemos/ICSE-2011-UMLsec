package org.omg.uml.behavioralelements.collaborations;

/**
 * ClassifierRole object instance interface.
 */
public interface ClassifierRole extends org.omg.uml.foundation.core.Classifier {
    /**
     * Returns the value of attribute multiplicity.
     * @return Value of attribute multiplicity.
     */
    public org.omg.uml.foundation.datatypes.Multiplicity getMultiplicity();
    /**
     * Sets the value of multiplicity attribute. See {@link #getMultiplicity} 
     * for description on the attribute.
     * @param newValue New value to be set.
     */
    public void setMultiplicity(org.omg.uml.foundation.datatypes.Multiplicity newValue);
    /**
     * Returns the value of reference base.
     * @return Value of reference base.
     */
    public java.util.Collection getBase();
    /**
     * Returns the value of reference availableFeature.
     * @return Value of reference availableFeature.
     */
    public java.util.Collection getAvailableFeature();
    /**
     * Returns the value of reference availableContents.
     * @return Value of reference availableContents.
     */
    public java.util.Collection getAvailableContents();
    /**
     * Returns the value of reference conformingInstance.
     * @return Value of reference conformingInstance.
     */
    public java.util.Collection getConformingInstance();
}
