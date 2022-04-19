package com.facilio.bmsconsole.context;

import com.amazonaws.HttpMethod;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.workflows.functions.FacilioConnectionFunctions;

import java.util.List;
import java.util.Map;

public class ConnectionApiContext {
    long id = -1l;
    long orgId = -1l;
    long connectionId = -1l;
    String name;
    String url;
    int type;
    ConnectionContext connectionContext;
    
    List<ConnectionParamContext> connectionParams;
    
    public List<ConnectionParamContext> getConnectionParams() {
		return connectionParams;
	}
	public void setConnectionParams(List<ConnectionParamContext> connectionParams) {
		this.connectionParams = connectionParams;
	}

    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    public void setConnectionContext(ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public Object execute(Map<String,String> params) throws Exception {
        if (this.type==1){
        	
        	return ConnectionUtil.getUrlResult(this.connectionContext, this.url, params, HttpMethod.GET,null,null,null,null);
        	
        }
        if (this.type ==2){
            //TODO handle post
        }
        return null;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    @Override
    public String toString() {
        return "ConnectionApiContext{" +
                "id:"+this.id+","+
                "connectionId:"+this.connectionId+","+
                "name:"+this.name+","+
                "url:"+this.url+","+
                "type:"+this.type+
                "}";
    }
}
