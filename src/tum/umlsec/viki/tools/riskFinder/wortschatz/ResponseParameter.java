/**
 * ResponseParameter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Mar 28, 2005 (10:34:47 CEST) WSDL2Java emitter.
 */

package tum.umlsec.viki.tools.riskFinder.wortschatz;

public class ResponseParameter  implements java.io.Serializable {
    private java.lang.String executionTime;
    private DataMatrix result;
    private int serviceMagnitude;
    private int userAmount;
    private int userMaxLimit;

    public ResponseParameter() {
    }

    public ResponseParameter(
           java.lang.String executionTime,
           DataMatrix result,
           int serviceMagnitude,
           int userAmount,
           int userMaxLimit) {
           this.executionTime = executionTime;
           this.result = result;
           this.serviceMagnitude = serviceMagnitude;
           this.userAmount = userAmount;
           this.userMaxLimit = userMaxLimit;
    }


    /**
     * Gets the executionTime value for this ResponseParameter.
     * 
     * @return executionTime
     */
    public java.lang.String getExecutionTime() {
        return executionTime;
    }


    /**
     * Sets the executionTime value for this ResponseParameter.
     * 
     * @param executionTime
     */
    public void setExecutionTime(java.lang.String executionTime) {
        this.executionTime = executionTime;
    }


    /**
     * Gets the result value for this ResponseParameter.
     * 
     * @return result
     */
    public DataMatrix getResult() {
        return result;
    }


    /**
     * Sets the result value for this ResponseParameter.
     * 
     * @param result
     */
    public void setResult(DataMatrix result) {
        this.result = result;
    }


    /**
     * Gets the serviceMagnitude value for this ResponseParameter.
     * 
     * @return serviceMagnitude
     */
    public int getServiceMagnitude() {
        return serviceMagnitude;
    }


    /**
     * Sets the serviceMagnitude value for this ResponseParameter.
     * 
     * @param serviceMagnitude
     */
    public void setServiceMagnitude(int serviceMagnitude) {
        this.serviceMagnitude = serviceMagnitude;
    }


    /**
     * Gets the userAmount value for this ResponseParameter.
     * 
     * @return userAmount
     */
    public int getUserAmount() {
        return userAmount;
    }


    /**
     * Sets the userAmount value for this ResponseParameter.
     * 
     * @param userAmount
     */
    public void setUserAmount(int userAmount) {
        this.userAmount = userAmount;
    }


    /**
     * Gets the userMaxLimit value for this ResponseParameter.
     * 
     * @return userMaxLimit
     */
    public int getUserMaxLimit() {
        return userMaxLimit;
    }


    /**
     * Sets the userMaxLimit value for this ResponseParameter.
     * 
     * @param userMaxLimit
     */
    public void setUserMaxLimit(int userMaxLimit) {
        this.userMaxLimit = userMaxLimit;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResponseParameter)) return false;
        ResponseParameter other = (ResponseParameter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.executionTime==null && other.getExecutionTime()==null) || 
             (this.executionTime!=null &&
              this.executionTime.equals(other.getExecutionTime()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            this.serviceMagnitude == other.getServiceMagnitude() &&
            this.userAmount == other.getUserAmount() &&
            this.userMaxLimit == other.getUserMaxLimit();
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
        if (getExecutionTime() != null) {
            _hashCode += getExecutionTime().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        _hashCode += getServiceMagnitude();
        _hashCode += getUserAmount();
        _hashCode += getUserMaxLimit();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseParameter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Synonyms", "ResponseParameter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("executionTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:Synonyms", "executionTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:Synonyms", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:Synonyms", "DataMatrix"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceMagnitude");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:Synonyms", "serviceMagnitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:Synonyms", "userAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userMaxLimit");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:Synonyms", "userMaxLimit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
