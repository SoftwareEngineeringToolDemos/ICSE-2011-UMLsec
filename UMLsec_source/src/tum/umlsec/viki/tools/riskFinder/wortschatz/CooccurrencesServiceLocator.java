/**
 * CooccurrencesServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Mar 28, 2005 (10:34:47 CEST) WSDL2Java emitter.
 */

package tum.umlsec.viki.tools.riskFinder.wortschatz;

public class CooccurrencesServiceLocator extends org.apache.axis.client.Service implements CooccurrencesService {

    public CooccurrencesServiceLocator() {
    }


    public CooccurrencesServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CooccurrencesServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Cooccurrences
    private java.lang.String Cooccurrences_address = "http://pcai055.informatik.uni-leipzig.de:8100/axis/services/Cooccurrences";

    public java.lang.String getCooccurrencesAddress() {
        return Cooccurrences_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CooccurrencesWSDDServiceName = "Cooccurrences";

    public java.lang.String getCooccurrencesWSDDServiceName() {
        return CooccurrencesWSDDServiceName;
    }

    public void setCooccurrencesWSDDServiceName(java.lang.String name) {
        CooccurrencesWSDDServiceName = name;
    }

    public Cooccurrences getCooccurrences() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Cooccurrences_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCooccurrences(endpoint);
    }

    public Cooccurrences getCooccurrences(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            CooccurrencesSoapBindingStub _stub = new CooccurrencesSoapBindingStub(portAddress, this);
            _stub.setPortName(getCooccurrencesWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCooccurrencesEndpointAddress(java.lang.String address) {
        Cooccurrences_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Cooccurrences.class.isAssignableFrom(serviceEndpointInterface)) {
                CooccurrencesSoapBindingStub _stub = new CooccurrencesSoapBindingStub(new java.net.URL(Cooccurrences_address), this);
                _stub.setPortName(getCooccurrencesWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Cooccurrences".equals(inputPortName)) {
            return getCooccurrences();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:Cooccurrences", "CooccurrencesService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:Cooccurrences", "Cooccurrences"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("Cooccurrences".equals(portName)) {
            setCooccurrencesEndpointAddress(address);
        }
        else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
