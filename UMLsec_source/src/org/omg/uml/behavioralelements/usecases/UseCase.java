package org.omg.uml.behavioralelements.usecases;

/**
 * UseCase object instance interface.
 */
public interface UseCase extends org.omg.uml.foundation.core.Classifier {
    /**
     * Returns the value of reference extend.
     * @return Value of reference extend.
     */
    public java.util.Collection getExtend();
    /**
     * Returns the value of reference include.
     * @return Value of reference include.
     */
    public java.util.Collection getInclude();
    /**
     * Returns the value of reference extensionPoint.
     * @return Value of reference extensionPoint.
     */
    public java.util.Collection getExtensionPoint();
}
