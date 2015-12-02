/**
 * DataVector.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Mar 28, 2005 (10:34:47 CEST) WSDL2Java emitter.
 */

package tum.umlsec.viki.tools.riskFinder.wortschatz;

public class DataVector  implements java.io.Serializable {
    private java.lang.String[] dataRow;

    public DataVector() {
    }

    public DataVector(
           java.lang.String[] dataRow) {
           this.dataRow = dataRow;
    }


    /**
     * Gets the dataRow value for this DataVector.
     * 
     * @return dataRow
     */
    public java.lang.String[] getDataRow() {
        return dataRow;
    }


    /**
     * Sets the dataRow value for this DataVector.
     * 
     * @param dataRow
     */
    public void setDataRow(java.lang.String[] dataRow) {
        this.dataRow = dataRow;
    }

    public java.lang.String getDataRow(int i) {
        return this.dataRow[i];
    }

    public void setDataRow(int i, java.lang.String _value) {
        this.dataRow[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DataVector)) return false;
        DataVector other = (DataVector) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataRow==null && other.getDataRow()==null) || 
             (this.dataRow!=null &&
              java.util.Arrays.equals(this.dataRow, other.getDataRow())));
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
        if (getDataRow() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDataRow());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDataRow(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DataVector.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://datatypes.webservice.wortschatz.uni_leipzig.de", "DataVector"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataRow");
        elemField.setXmlName(new javax.xml.namespace.QName("http://datatypes.webservice.wortschatz.uni_leipzig.de", "dataRow"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
