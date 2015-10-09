package org.omg.uml.foundation.core;

/**
 * TaggedValue object instance interface.
 */
public interface TaggedValue extends org.omg.uml.foundation.core.ModelElement {
    /**
     * Returns the value of attribute dataValue.
     * @return Value of dataValue attribute.
     */
    public java.util.Collection getDataValue();
    /**
     * Returns the value of reference modelElement.
     * @return Value of reference modelElement.
     */
    public org.omg.uml.foundation.core.ModelElement getModelElement();
    /**
     * Sets the value of reference modelElement. See {@link #getModelElement} 
     * for description on the reference.
     * @param newValue New value to be set.
     */
    public void setModelElement(org.omg.uml.foundation.core.ModelElement newValue);
    /**
     * Returns the value of reference type.
     * @return Value of reference type.
     */
    public org.omg.uml.foundation.core.TagDefinition getType();
    /**
     * Sets the value of reference type. See {@link #getType} for description 
     * on the reference.
     * @param newValue New value to be set.
     */
    public void setType(org.omg.uml.foundation.core.TagDefinition newValue);
    /**
     * Returns the value of reference referenceValue.
     * @return Value of reference referenceValue.
     */
    public java.util.Collection getReferenceValue();
}
