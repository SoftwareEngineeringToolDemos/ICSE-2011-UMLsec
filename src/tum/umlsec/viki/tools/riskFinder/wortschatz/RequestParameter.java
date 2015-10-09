/**
 * RequestParameter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Mar 28, 2005 (10:34:47 CEST) WSDL2Java emitter.
 */

package tum.umlsec.viki.tools.riskFinder.wortschatz;

public class RequestParameter  implements java.io.Serializable {
    private java.lang.String corpus;
    private DataMatrix parameters;

    public RequestParameter() {
    }

    public RequestParameter(
           java.lang.String corpus, DataMatrix parameters) {
           this.corpus = corpus;
           this.parameters = parameters;
    }


    /**
     * Gets the corpus value for this RequestParameter.
     * 
     * @return corpus
     */
    public java.lang.String getCorpus() {
        return corpus;
    }


    /**
     * Sets the corpus value for this RequestParameter.
     * 
     * @param corpus
     */
    public void setCorpus(java.lang.String corpus) {
        this.corpus = corpus;
    }


    /**
     * Gets the parameters value for this RequestParameter.
     * 
     * @return parameters
     */
    public DataMatrix getParameters() {
        return parameters;
    }


    /**
     * Sets the parameters value for this RequestParameter.
     * 
     * @param parameters
     */
    public void setParameters(DataMatrix parameters) {
        this.parameters = parameters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestParameter)) return false;
        RequestParameter other = (RequestParameter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.corpus==null && other.getCorpus()==null) || 
             (this.corpus!=null &&
              this.corpus.equals(other.getCorpus()))) &&
            ((this.parameters==null && other.getParameters()==null) || 
             (this.parameters!=null &&
              this.parameters.equals(other.getParameters())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCorpus() != null) {
            _hashCode += getCorpus().hashCode();
        }
        if (getParameters() != null) {
            _hashCode += getParameters().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestParameter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Synonyms", "RequestParameter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("corpus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:Synonyms", "corpus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parameters");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:Synonyms", "parameters"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:Synonyms", "DataMatrix"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
