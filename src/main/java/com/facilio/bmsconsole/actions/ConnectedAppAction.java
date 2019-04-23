package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

import java.util.List;

public class ConnectedAppAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	
	
	private ConnectedAppContext connectedApp;

	public ConnectedAppContext getConnectedApp() {
		return connectedApp;
	}

	public void setConnectedApp(ConnectedAppContext connectedApp) {
		this.connectedApp = connectedApp;
	}

	private long connectedAppId;

	public long getConnectedAppId() {
		return connectedAppId;
	}

	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}

	public String addConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, connectedApp);
		
		Chain addItem = TransactionChainFactory.getAddOrUpdateConnectedAppChain();
		addItem.execute(context);
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}

	public String updateConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, connectedApp);
		context.put(FacilioConstants.ContextNames.ID, connectedApp.getId());

		Chain updateItemChain = TransactionChainFactory.getAddOrUpdateConnectedAppChain();
		updateItemChain.execute(context);
		setConnectedAppId(connectedApp.getId());
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}
	
	public String deleteConnectedApp() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, connectedAppId);

		Chain updateItemChain = TransactionChainFactory.getDeleteConnectedAppChain();
		updateItemChain.execute(context);
		setResult(FacilioConstants.ContextNames.ID, connectedAppId);
		return SUCCESS;
	}
	
	private String linkName;
	
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	public String getLinkName() {
		return this.linkName;
	}

	public String connectedAppDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getConnectedAppId());
		context.put(FacilioConstants.ContextNames.LINK_NAME, getLinkName());

		Chain fetchDetailsChain = ReadOnlyChainFactory.fetchConnectedAppDetails();
		fetchDetailsChain.execute(context);

		setConnectedApp((ConnectedAppContext) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedApp);
		return SUCCESS;
	}
	
	private List<ConnectedAppContext> connectedAppList;

	public List<ConnectedAppContext> getConnectedAppList() {
		return connectedAppList;
	}

	public void setConnectedAppList(List<ConnectedAppContext> connectedAppList) {
		this.connectedAppList = connectedAppList;
	}

	public String connectedAppList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain fetchListChain = ReadOnlyChainFactory.getConnectedAppsList();
		fetchListChain.execute(context);
		
		connectedAppList = (List<ConnectedAppContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.CONNECTED_APPS, connectedAppList);
		return SUCCESS;
	}
}
