package org.omg.uml.foundation.core;

/**
 * TemplateArgument object instance interface.
 */
public interface TemplateArgument extends javax.jmi.reflect.RefObject {
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
     * Returns the value of reference binding.
     * @return Value of reference binding.
     */
    public org.omg.uml.foundation.core.Binding getBinding();
    /**
     * Sets the value of reference binding. See {@link #getBinding} for description 
     * on the reference.
     * @param newValue New value to be set.
     */
    public void setBinding(org.omg.uml.foundation.core.Binding newValue);
}
