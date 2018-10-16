package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class SpaceCategoryAction  extends ActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private static org.apache.log4j.Logger log = LogManager.getLogger(SpaceCategoryAction.class.getName());


	SpaceCategoryContext spaceCategory;
	
	List<SpaceCategoryContext> spaceCategories;

	public SpaceCategoryContext getSpaceCategory() {
		return spaceCategory;
	}

	public void setSpaceCategory(SpaceCategoryContext spaceCategory) {
		this.spaceCategory = spaceCategory;
	}

	public List<SpaceCategoryContext> getSpaceCategories() {
		return spaceCategories;
	}

	public void setSpaceCategories(List<SpaceCategoryContext> spaceCategories) {
		this.spaceCategories = spaceCategories;
	}
	
	List<Long> spaceCategoryIds;
	
	
	public List<Long> getSpaceCategoryIds() {
		return spaceCategoryIds;
	}

	public void setSpaceCategoryIds(List<Long> spaceCategoryIds) {
		this.spaceCategoryIds = spaceCategoryIds;
	}
	
	String resultMessage;
	

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	boolean resultAction;
	

	public boolean isResultAction() {
		return resultAction;
	}

	public void setResultAction(boolean resultAction) {
		this.resultAction = resultAction;
	}

	public String spaceCategoriesList() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain getSpaceCategoriesChain = FacilioChainFactory.getAllSpaceCategoriesChain();
		getSpaceCategoriesChain.execute(context);
		setSpaceCategories((List<SpaceCategoryContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String addSpaceCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getSpaceCategory());
		Chain addSpaceCategoryChain = FacilioChainFactory.addSpaceCategoryChain();
		addSpaceCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateSpaceCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, getSpaceCategory());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, getSpaceCategoryIds());
		Chain updateSpaceCategoryChain = FacilioChainFactory.updateSpaceCategoryChain();
		updateSpaceCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String deleteSpaceCategory() {
		try {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, getSpaceCategoryIds());
		Chain deleteSpaceCategoryChain = FacilioChainFactory.deleteSpaceCategoryChain();
		deleteSpaceCategoryChain.execute(context);
		setResultAction(true);
		setResultMessage("Category deleted Successfully");
		return SUCCESS;
		}
		catch(Exception e)
		{
			log.info("Exception occurred ", e);
			setResultAction(false);
			setResultMessage("Error in deleting Category");
			return ERROR;
		}
	}
	

}
