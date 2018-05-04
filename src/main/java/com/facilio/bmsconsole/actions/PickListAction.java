package com.facilio.bmsconsole.actions;

import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetDepartmentContext;
import com.facilio.bmsconsole.context.AssetTypeContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class PickListAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			setPickList(LookupSpecialTypeUtil.getPickList(moduleName));
		}
		else {
			Context context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
			//System.out.println("Context is "+context);
			//FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
			Chain pickListChain = FacilioChainFactory.getPickListChain();
			pickListChain.execute(context);
			//FacilioTransactionManager.INSTANCE.getTransactionManager().commit();			
			setPickList((Map<Long, String>) context.get(FacilioConstants.ContextNames.PICKLIST));
		}
		
		return SUCCESS;
	}
	
	private Map<Long, String> pickList;
	public Map<Long, String> getPickList() {
		return pickList;
	}
	public void setPickList(Map<Long, String> pickList) {
		this.pickList = pickList;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	TicketCategoryContext ticketCategory;
	public TicketCategoryContext getTicketCategory() {
		return ticketCategory;
	}
	public void setTicketCategory(TicketCategoryContext ticketCategory) {
		this.ticketCategory = ticketCategory;
	}

	public String addTicketCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketCategory());
		Chain addTicketCategoryChain = FacilioChainFactory.getAddTicketCategoryChain();
		addTicketCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	TicketPriorityContext ticketPriority;
	public TicketPriorityContext getTicketPriority() {
		return ticketPriority;
	}
	public void setTicketPriority(TicketPriorityContext ticketPriority) {
		this.ticketPriority = ticketPriority;
	}

	public String addTicketPriority() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketPriority());
		Chain addTicketPriorityChain = FacilioChainFactory.getAddTicketPriorityChain();
		addTicketPriorityChain.execute(context);
		
		return SUCCESS;
	}
	
	TicketTypeContext ticketType;
	public TicketTypeContext getTicketType() {
		return ticketType;
	}
	public void setTicketType(TicketTypeContext ticketType) {
		this.ticketType = ticketType;
	}

	public String addTicketType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getTicketType());
		Chain addTicketTypeChain = FacilioChainFactory.getAddTicketTypeChain();
		addTicketTypeChain.execute(context);
		
		return SUCCESS;
	}
	
	AssetCategoryContext assetCategory;
	public AssetCategoryContext getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(AssetCategoryContext assetCategory) {
		this.assetCategory = assetCategory;
	}

	public String addAssetCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetCategory());
		Chain addAssetCategoryChain = FacilioChainFactory.getAddAssetCategoryChain();
		addAssetCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	AssetDepartmentContext assetDepartment;
	public AssetDepartmentContext getAssetDepartment() {
		return assetDepartment;
	}
	public void setAssetDepartment(AssetDepartmentContext assetDepartment) {
		this.assetDepartment = assetDepartment;
	}

	public String addAssetDepartment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetDepartment());
		Chain addAssetDepartmentChain = FacilioChainFactory.getAddAssetDepartmentChain();
		addAssetDepartmentChain.execute(context);
		
		return SUCCESS;
	}
	
	AssetTypeContext assetType;
	public AssetTypeContext getAssetType() {
		return assetType;
	}
	public void setAssetType(AssetTypeContext assetType) {
		this.assetType = assetType;
	}

	public String addAssetType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetType());
		Chain addAssetTypeChain = FacilioChainFactory.getAddAssetTypeChain();
		addAssetTypeChain.execute(context);
		
		return SUCCESS;
	}
	
	AlarmSeverityContext assetSeverity;
	public AlarmSeverityContext getAssetSeverity() {
		return assetSeverity;
	}
	public void setAssetSeverity(AlarmSeverityContext assetSeverity) {
		this.assetSeverity = assetSeverity;
	}

	public String addAlarmSeverity() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getAssetSeverity());
		Chain addAlarmSeverityChain = FacilioChainFactory.getAddAlarmSeverityChain();
		addAlarmSeverityChain.execute(context);
		
		return SUCCESS;
	}
}
