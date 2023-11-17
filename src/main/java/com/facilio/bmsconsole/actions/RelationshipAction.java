package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.RelationshipContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import org.json.simple.JSONObject;

public class RelationshipAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RelationshipContext relationship;

	public RelationshipContext getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationshipContext relationship) {
		this.relationship = relationship;
	}

	private long id = -1;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	@Getter
	public String fromModuleName;
	@Getter
	public long recordId;
	@Getter
	public Integer relationCategory;
	@Getter
	public String moduleName;

	public String addOrUpdateRelationShip() throws Exception {
		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.RELATIONSHIP, relationship);
		FacilioChain chain;
		if (relationship.getId() > 0) {
			chain = TransactionChainFactory.getUpdateRelationShipChain();
		} else {
			chain = TransactionChainFactory.getAddRelationShipChain();
		}
		chain.execute(context);

		return SUCCESS;
	}

	public String getRelationshipList() throws Exception {
		FacilioContext context = new FacilioContext();

		FacilioChain chain = ReadOnlyChainFactory.getRelationshipChain();
		chain.execute(context);

		setResult("RelationshipList", context.get(FacilioConstants.ContextNames.RELATIONSHIP_LIST));
		return SUCCESS;
	}

	public String deleteRelationship() throws Exception {
		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.ID, id);
		FacilioChain chain = TransactionChainFactory.getDeleteRelationshipChain();
		chain.execute(context);
		return SUCCESS;
	}
	public String getRelationshipsWithDataAssociated() throws Exception{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.getFromModuleName()==null?this.getModuleName():this.getFromModuleName());
		context.put(FacilioConstants.ContextNames.RECORD_ID, this.getRecordId());
		context.put(FacilioConstants.ContextNames.RELATION_CATEGORY_NAME, this.getRelationCategory());
		JSONObject pagination = new JSONObject();
		pagination.put("page", this.getPage());
		pagination.put("perPage", this.getPerPage());
		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);

		FacilioChain chain = TransactionChainFactory.getRelationshipsWithDataAssociatedChain();
		chain.execute(context);
		setResult(FacilioConstants.ContextNames.RELATIONSHIP_LIST, context.get(FacilioConstants.ContextNames.RELATIONSHIP_LIST));
		return SUCCESS;
	}
	public String getUnUsedRelationshipsForRecord() throws Exception{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.getModuleName());
		context.put(FacilioConstants.ContextNames.RECORD_ID, this.getRecordId());
		context.put(FacilioConstants.ContextNames.RELATION_CATEGORY_NAME, this.getRelationCategory());
		JSONObject pagination = new JSONObject();
		pagination.put("page", this.getPage());
		pagination.put("perPage", this.getPerPage());
		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);

		FacilioChain chain = TransactionChainFactory.getUnUsedRelationshipsForRecord();
		chain.execute(context);
		setResult(FacilioConstants.ContextNames.RELATIONSHIP_LIST, context.get(FacilioConstants.ContextNames.RELATIONSHIP_LIST));
		return SUCCESS;
	}
}
