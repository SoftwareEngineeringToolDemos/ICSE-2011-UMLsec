package org.omg.uml.foundation.core;

/**
 * ProgrammingLanguageDataType object instance interface.
 */
public interface ProgrammingLanguageDataType extends org.omg.uml.foundation.core.DataType {
    /**
     * Returns the value of attribute expression.
     * @return Value of attribute expression.
     */
    public org.omg.uml.foundation.datatypes.TypeExpression getExpression();
    /**
     * Sets the value of expression attribute. See {@link #getExpression} for 
     * description on the attribute.
     * @param newValue New value to be set.
     */
    public void setExpression(org.omg.uml.foundation.datatypes.TypeExpression newValue);
}
