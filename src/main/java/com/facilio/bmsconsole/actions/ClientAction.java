package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class ClientAction extends FacilioAction {

	private ClientContext client;

	public ClientContext getClient() {
		return client;
	}

	public void setClient(ClientContext client) {
		this.client = client;
	}

	private List<ClientContext> clients;

	public List<ClientContext> getClients() {
		return clients;
	}

	public void setClients(List<ClientContext> clients) {
		this.clients = clients;
	}

	private long clientId;

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	private List<Long> clientIds;

	public List<Long> getClientIds() {
		return clientIds;
	}

	public void setClientIds(List<Long> clientIds) {
		this.clientIds = clientIds;
	}

	public String addClient() throws Exception {
		if (!CollectionUtils.isEmpty(clients)) {
			for (ClientContext client : clients) {
				LocationContext address = client.getAddress();
				if (address == null) {
					address = new LocationContext();
					address.setLat(1.1);
					address.setLng(1.1);
				}
				address.setName(client.getName() + "_location");
				FacilioChain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.getContext().put(FacilioConstants.ContextNames.RECORD, address);
				addLocation.execute();
				long locationId = (long) addLocation.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
				address.setId(locationId);
				client.setAddress(address);
			}
			FacilioChain c = TransactionChainFactory.addClientsChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, clients);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITORS,
					c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateClient() throws Exception {
		if(!CollectionUtils.isEmpty(clients)) {
			for(ClientContext v : clients) {
				//update location
				LocationContext address = v.getAddress();
				
				if(address != null && address.getLat() != -1 && address.getLng() != -1)
				{
					FacilioChain locationChain = null;
					address.setName(v.getName()+"_Location");
					
					if (address.getId() > 0) {
						locationChain = FacilioChainFactory.updateLocationChain();
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, address);
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, address.getId());
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(address.getId()));
					
						locationChain.execute();
						v.setAddress(address);
					}
					else {
						locationChain = FacilioChainFactory.addLocationChain();
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, address);
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, address.getId());
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(address.getId()));
					
						locationChain.execute();
						long locationId = (long) locationChain.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
						address.setId(locationId);
					}
				}
				else {
					v.setAddress(null);
				}
			}
			FacilioChain c = TransactionChainFactory.updateClientsChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, clients);
			c.execute();
			setResult(FacilioConstants.ContextNames.CLIENTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
}
