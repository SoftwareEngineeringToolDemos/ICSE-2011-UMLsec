package org.omg.uml.foundation.core;

/**
 * Comment object instance interface.
 */
public interface Comment extends org.omg.uml.foundation.core.ModelElement {
    /**
     * Returns the value of attribute body.
     * @return Value of attribute body.
     */
    public java.lang.String getBody();
    /**
     * Sets the value of body attribute. See {@link #getBody} for description 
     * on the attribute.
     * @param newValue New value to be set.
     */
    public void setBody(java.lang.String newValue);
    /**
     * Returns the value of reference annotatedElement.
     * @return Value of reference annotatedElement.
     */
    public java.util.Collection getAnnotatedElement();
}
