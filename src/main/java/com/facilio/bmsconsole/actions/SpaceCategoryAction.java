package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class SpaceCategoryAction  extends ActionSupport{

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
	
	public String spaceCategoriesList() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain getSpaceCategoriesChain = FacilioChainFactory.getSpaceCategoriesChain();
		getSpaceCategoriesChain.execute(context);
		setSpaceCategories((List<SpaceCategoryContext>)context.get(FacilioConstants.ContextNames.SPACECATEGORIESLIST));
		
		return SUCCESS;
	}
	
	public String addSpaceCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACECATEGORY, getSpaceCategory());
		Chain addSpaceCategoryChain = FacilioChainFactory.addSpaceCategoryChain();
		addSpaceCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String editSpaceCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACECATEGORY, getSpaceCategory());
		Chain editSpaceCategoryChain = FacilioChainFactory.editSpaceCategoryChain();
		editSpaceCategoryChain.execute(context);
		
		return SUCCESS;
	}
	
	public String deleteSpaceCategory() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACECATEGORY, getSpaceCategory());
		Chain deleteSpaceCategoryChain = FacilioChainFactory.deleteSpaceCategoryChain();
		deleteSpaceCategoryChain.execute(context);
		
		return SUCCESS;
	}
	

}
