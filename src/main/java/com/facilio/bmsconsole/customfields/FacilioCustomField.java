package com.facilio.bmsconsole.customfields;

import org.apache.commons.chain.impl.ContextBase;

public class FacilioCustomField extends ContextBase {
	
	private long fieldId;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long objId;
	public long getObjId() {
		return objId;
	}
	public void setObjId(long objId) {
		this.objId = objId;
	}
	
	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	private int columnNum;
	public int getColumnNum() {
		return columnNum;
	}
	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}
	
	private int dataTypeCode;
	public int getDataTypeCode() {
		return dataTypeCode;
	}
	public void setDataTypeCode(int dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}
	
	private CFType dataType;
	public CFType getDataType() {
		return dataType;
	}
	public void setDataType(CFType dataType) {
		this.dataType = dataType;
	}
	
	private String tableName;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
