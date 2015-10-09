package org.omg.uml.foundation.core;

/**
 * TemplateParameter object instance interface.
 */
public interface TemplateParameter extends javax.jmi.reflect.RefObject {
    /**
     * Returns the value of reference template.
     * @return Value of reference template.
     */
    public org.omg.uml.foundation.core.ModelElement getTemplate();
    /**
     * Sets the value of reference template. See {@link #getTemplate} for description 
     * on the reference.
     * @param newValue New value to be set.
     */
    public void setTemplate(org.omg.uml.foundation.core.ModelElement newValue);
    /**
     * Returns the value of reference parameter.
     * @return Value of reference parameter.
     */
    public org.omg.uml.foundation.core.ModelElement getParameter();
    /**
     * Sets the value of reference parameter. See {@link #getParameter} for description 
     * on the reference.
     * @param newValue New value to be set.
     */
    public void setParameter(org.omg.uml.foundation.core.ModelElement newValue);
    /**
     * Returns the value of reference defaultElement.
     * @return Value of reference defaultElement.
     */
    public org.omg.uml.foundation.core.ModelElement getDefaultElement();
    /**
     * Sets the value of reference defaultElement. See {@link #getDefaultElement} 
     * for description on the reference.
     * @param newValue New value to be set.
     */
    public void setDefaultElement(org.omg.uml.foundation.core.ModelElement newValue);
}
