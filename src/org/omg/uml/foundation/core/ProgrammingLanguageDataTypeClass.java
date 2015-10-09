package org.omg.uml.foundation.core;

/**
 * ProgrammingLanguageDataType class proxy interface.
 */
public interface ProgrammingLanguageDataTypeClass extends javax.jmi.reflect.RefClass {
    /**
     * The default factory operation used to create an instance object.
     * @return The created instance object.
     */
    public ProgrammingLanguageDataType createProgrammingLanguageDataType();
    /**
     * Creates an instance object having attributes initialized by the passed 
     * values.
     * @param name 
     * @param visibility 
     * @param isSpecification 
     * @param isRoot 
     * @param isLeaf 
     * @param isAbstract 
     * @param expression 
     * @return The created instance object.
     */
    public ProgrammingLanguageDataType createProgrammingLanguageDataType(java.lang.String name, org.omg.uml.foundation.datatypes.VisibilityKind visibility, boolean isSpecification, boolean isRoot, boolean isLeaf, boolean isAbstract, org.omg.uml.foundation.datatypes.TypeExpression expression);
}
