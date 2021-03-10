package com.facilio.bmsconsole.context;

public class ConnectionParamContext {

	long id = -1l;
	long orgId = -1l;
	long connectionId = -1l;
	long connectionApiId = -1l;
	String key;
	String value;
	boolean isProperty;
	
	
	public long getConnectionApiId() {
		return connectionApiId;
	}
	public void setConnectionApiId(long connectionApiId) {
		this.connectionApiId = connectionApiId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public boolean isProperty() {
		return isProperty;
	}

	public void setProperty(boolean property) {
		isProperty = property;
	}
	public void setIsProperty(boolean property) {
		isProperty = property;
	}
	public boolean getIsProperty() {
		return isProperty;
	}
	public String toString(){
		return "{" +
				"id:"+id + ","+
				"orgId:"+orgId + ","+
				"connectionId:"+connectionId + ","+
				"key:"+key + ","+
				"value:"+value + ","+
				"isProperty:"+isProperty+
				"}";
	}
}
