package org.omg.uml.behavioralelements.commonbehavior;

/**
 * SubsystemInstance class proxy interface.
 */
public interface SubsystemInstanceClass extends javax.jmi.reflect.RefClass {
    /**
     * The default factory operation used to create an instance object.
     * @return The created instance object.
     */
    public SubsystemInstance createSubsystemInstance();
    /**
     * Creates an instance object having attributes initialized by the passed 
     * values.
     * @param name 
     * @param visibility 
     * @param isSpecification 
     * @return The created instance object.
     */
    public SubsystemInstance createSubsystemInstance(java.lang.String name, org.omg.uml.foundation.datatypes.VisibilityKind visibility, boolean isSpecification);
}
