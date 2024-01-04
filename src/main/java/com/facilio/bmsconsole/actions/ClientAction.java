package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
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

	private long recordId = -1;

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	private List<Long> siteIds;

	public List<Long> getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(List<Long> siteIds) {
		this.siteIds = siteIds;
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
		if (!CollectionUtils.isEmpty(clients)) {
			for (ClientContext v : clients) {
				// update location
				LocationContext address = v.getAddress();

				if (address != null && address.getLat() != -1 && address.getLng() != -1) {
					FacilioChain locationChain = null;
					address.setName(v.getName() + "_Location");

					if (address.getId() > 0) {
						locationChain = FacilioChainFactory.updateLocationChain();
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, address);
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, address.getId());
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST,
								Collections.singletonList(address.getId()));

						locationChain.execute();
						v.setAddress(address);
					} else {
						locationChain = FacilioChainFactory.addLocationChain();
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, address);
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, address.getId());
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST,
								Collections.singletonList(address.getId()));

						locationChain.execute();
						long locationId = (long) locationChain.getContext()
								.get(FacilioConstants.ContextNames.RECORD_ID);
						address.setId(locationId);
					}
				} else {
					v.setAddress(null);
				}
			}
			FacilioChain c = TransactionChainFactory.updateClientsChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, clients);
			c.execute();
			setResult(FacilioConstants.ContextNames.CLIENTS,
					c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteClients() throws Exception {

		if (!CollectionUtils.isEmpty(clientIds)) {
			FacilioChain c = FacilioChainFactory.deleteClientListChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, clientIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST,
					c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}

	public String disassociateClientFromSite() throws Exception {
		if (!CollectionUtils.isEmpty(siteIds)) {
			FacilioChain chain = TransactionChainFactory.disassociateClientFromSiteChain();
			chain.getContext().put(FacilioConstants.ContextNames.SITE_LIST, siteIds);
			chain.execute();
		}

		return SUCCESS;
	}
	
	public String associateClientFromSite() throws Exception {
		if (!CollectionUtils.isEmpty(siteIds) && clientId > 0) {
			FacilioChain chain = TransactionChainFactory.associateClientFromSiteChain();
			chain.getContext().put(FacilioConstants.ContextNames.SITE_LIST, siteIds);
			chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, clientId);
			chain.execute();
		}

		return SUCCESS;
	}

	public String getClientDetails() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getClientDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, clientId);

		chain.execute();

		ClientContext client = (ClientContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.CLIENT, client);

		return SUCCESS;
	}
	public List<V3SiteContext> getAssociatedClientSite(FacilioModule module, List<FacilioField> fields, V3ClientContext client) throws Exception{
		SelectRecordsBuilder<V3SiteContext> selectBuilder = new SelectRecordsBuilder<V3SiteContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(V3SiteContext.class)
				.andCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(client.getId()), NumberOperators.EQUALS)).skipScopeCriteria();
		List<V3SiteContext> clienSiteList = selectBuilder.get();
		return clienSiteList;
	}

	public String clientsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Clients.ID desc");

		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "client.name");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		if (getCount()) { // only count
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
		} else {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}

		FacilioChain clientListChain = ReadOnlyChainFactory.getClientsListChain();
		clientListChain.execute(context);
		if (getCount()) {
			setItemCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", itemCount);
		} else {
			clients = (List<ClientContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (clients == null) {
				clients = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.CLIENTS, clients);
		}
		return SUCCESS;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private Boolean count;

	public Boolean getCount() {
		if (count == null) {
			return false;
		}
		return count;
	}

	public void setCount(Boolean count) {
		this.count = count;
	}

	private Long itemCount;

	public Long getItemCount() {
		return itemCount;
	}

	public void setItemCount(Long inventryCount) {
		this.itemCount = inventryCount;
	}

}
