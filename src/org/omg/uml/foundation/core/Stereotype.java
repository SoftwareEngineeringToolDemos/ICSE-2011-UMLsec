package org.omg.uml.foundation.core;

/**
 * NodeStereotype object instance interface.
 */
public interface Stereotype extends org.omg.uml.foundation.core.GeneralizableElement {
    /**
     * Returns the value of attribute icon.
     * @return Value of attribute icon.
     */
    public java.lang.String getIcon();
    /**
     * Sets the value of icon attribute. See {@link #getIcon} for description 
     * on the attribute.
     * @param newValue New value to be set.
     */
    public void setIcon(java.lang.String newValue);
    /**
     * Returns the value of attribute baseClass.
     * @return Value of baseClass attribute.
     */
    public java.util.Collection getBaseClass();
    /**
     * Returns the value of reference definedTag.
     * @return Value of reference definedTag.
     */
    public java.util.Collection getDefinedTag();
    /**
     * Returns the value of reference stereotypeConstraint.
     * @return Value of reference stereotypeConstraint.
     */
    public java.util.Collection getStereotypeConstraint();
}
