package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;


public class VisitorAction extends FacilioAction 
{
	private static final long serialVersionUID = 1L;
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private Boolean fetchCount;
	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}
	
	private VisitorContext visitor;
	public VisitorContext getVisitor() {
		return visitor;
	}
	public void setVisitor(VisitorContext visitor) {
		this.visitor = visitor;
	}
	
	private List<VisitorContext> visitors;
	public List<VisitorContext> getVisitors() {
		return visitors;
	}
	public void setVisitors(List<VisitorContext> visitors) {
		this.visitors = visitors;
	}

	private List<Long> visitorIds;
	
	public List<Long> getVisitorIds() {
		return visitorIds;
	}
	public void setVisitorIds(List<Long> visitorIds) {
		this.visitorIds = visitorIds;
	}

	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private File signature;
	private String signatureFileName;
	private  String signatureContentType;
	
	private File avatar;
	private String avatarFileName;
	private  String avatarContentType;
	
	public File getAvatar() {
		return avatar;
	}
	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}
	public String getAvatarFileName() {
		return avatarFileName;
	}
	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}
	public String getAvatarContentType() {
		return avatarContentType;
	}
	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	public File getSignature() {
		return signature;
	}
	public void setSignature(File signature) {
		this.signature = signature;
	}
	public String getSignatureFileName() {
		return signatureFileName;
	}
	public void setSignatureFileName(String signatureFileName) {
		this.signatureFileName = signatureFileName;
	}
	public String getSignatureContentType() {
		return signatureContentType;
	}
	public void setSignatureContentType(String signatureContentType) {
		this.signatureContentType = signatureContentType;
	}
	public String addVisitors() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitors)) {
			for(VisitorContext v : visitors) {
				LocationContext location = v.getLocation();
				if(location == null) {
					location = new LocationContext();
					location.setLat(1.1);
					location.setLng(1.1);
				}
				location.setName(v.getName()+"_location");
				FacilioChain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.getContext().put(FacilioConstants.ContextNames.RECORD, location);
				addLocation.execute();
				long locationId = (long) addLocation.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
				v.setLocation(location);
				
			}
			FacilioChain c = TransactionChainFactory.addVisitorChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitors);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITORS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateVisitors() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitors)) {
			for(VisitorContext v : visitors) {
				//update location
				LocationContext location = v.getLocation();
				
				if(location != null && location.getLat() != -1 && location.getLng() != -1)
				{
					FacilioChain locationChain = null;
					location.setName(v.getName()+"_Location");
					
					if (location.getId() > 0) {
						locationChain = FacilioChainFactory.updateLocationChain();
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, location);
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));
					
						locationChain.execute();
						v.setLocation(location);
					}
					else {
						locationChain = FacilioChainFactory.addLocationChain();
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, location);
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
						locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));
					
						locationChain.execute();
						long locationId = (long) locationChain.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
						location.setId(locationId);
					}
				}
				else {
					v.setLocation(null);
				}
			}
			FacilioChain c = TransactionChainFactory.updateVisitorChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitors);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITORS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
		public String updateVisitor() throws Exception {
	
			if (avatar != null) {
				visitor.setAvatar(avatar);
				visitor.setAvatarFileName(avatarFileName);
				visitor.setAvatarContentType(avatarContentType);
			}
			if (signature != null) {
				visitor.setSignature(signature);
				visitor.setSignatureFileName(signatureFileName);
				visitor.setSignatureContentType(signatureContentType);
			}
			
			//update location
			LocationContext location = visitor.getLocation();
			
			if(location != null && location.getLat() != -1 && location.getLng() != -1)
			{
				FacilioChain locationChain = null;
				location.setName(visitor.getName()+"_Location");
				locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, location);
				locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
				locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));
			
				if (location.getId() > 0) {
					locationChain = FacilioChainFactory.updateLocationChain();
					locationChain.execute();
					visitor.setLocation(location);
				}
				else {
					locationChain = FacilioChainFactory.addLocationChain();
					locationChain.execute();
					long locationId = (long) locationChain.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
					location.setId(locationId);
				}
			}
			else {
				visitor.setLocation(null);
			}
		FacilioChain c = TransactionChainFactory.updateVisitorChain();
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(visitor));
		c.execute();
		setResult(FacilioConstants.ContextNames.VISITORS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	}
		
	public String addVisitor() throws Exception {
		
		FacilioChain c = TransactionChainFactory.addVisitorChain();
		if (avatar != null) {
			visitor.setAvatar(avatar);
			visitor.setAvatarFileName(avatarFileName);
			visitor.setAvatarContentType(avatarContentType);
		}
		if (signature != null) {
			visitor.setSignature(signature);
			visitor.setSignatureFileName(signatureFileName);
			visitor.setSignatureContentType(signatureContentType);
		}
		LocationContext location = visitor.getLocation();
		if(location == null) { 
			location = new LocationContext();
			location.setLat(1.1);
			location.setLng(1.1);
		}
		location.setName(visitor.getName()+"_location");
		FacilioChain addLocation = FacilioChainFactory.addLocationChain();
		addLocation.getContext().put(FacilioConstants.ContextNames.RECORD, location);
		addLocation.execute();
		long locationId = (long) addLocation.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
		location.setId(locationId);
		visitor.setLocation(location);
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(visitor));
		c.execute();
		setResult(FacilioConstants.ContextNames.VISITORS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	public String deleteVisitors() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorIds)) {
			FacilioChain c = FacilioChainFactory.deleteVisitorChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, visitorIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getVisitorsList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getVisitorListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "visitor");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Visitor.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "visitor.name");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
		List<VisitorContext> visitors = (List<VisitorContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.VISITORS, visitors);
		}
		
		return SUCCESS;
	}
	
	public String getVisitorDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getVisitorDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		VisitorContext visitor = (VisitorContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.VISITOR, visitor);
		
		return SUCCESS;
	}
	

}
