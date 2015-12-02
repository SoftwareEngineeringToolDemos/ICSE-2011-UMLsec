/**
 * SynonymsServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Mar 28, 2005 (10:34:47 CEST) WSDL2Java emitter.
 */

package tum.umlsec.viki.tools.riskFinder.wortschatz;

public class SynonymsServiceLocator extends org.apache.axis.client.Service implements SynonymsService {

    public SynonymsServiceLocator() {
    }


    public SynonymsServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SynonymsServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Synonyms
    private java.lang.String Synonyms_address = "http://pcai055.informatik.uni-leipzig.de:8100/axis/services/Synonyms";

    public java.lang.String getSynonymsAddress() {
        return Synonyms_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SynonymsWSDDServiceName = "Synonyms";

    public java.lang.String getSynonymsWSDDServiceName() {
        return SynonymsWSDDServiceName;
    }

    public void setSynonymsWSDDServiceName(java.lang.String name) {
        SynonymsWSDDServiceName = name;
    }

    public Synonyms getSynonyms() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Synonyms_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSynonyms(endpoint);
    }

    public Synonyms getSynonyms(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SynonymsSoapBindingStub _stub = new SynonymsSoapBindingStub(portAddress, this);
            _stub.setPortName(getSynonymsWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSynonymsEndpointAddress(java.lang.String address) {
        Synonyms_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Synonyms.class.isAssignableFrom(serviceEndpointInterface)) {
                SynonymsSoapBindingStub _stub = new SynonymsSoapBindingStub(new java.net.URL(Synonyms_address), this);
                _stub.setPortName(getSynonymsWSDDServiceName());
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
        if ("Synonyms".equals(inputPortName)) {
            return getSynonyms();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:Synonyms", "SynonymsService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:Synonyms", "Synonyms"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("Synonyms".equals(portName)) {
            setSynonymsEndpointAddress(address);
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
