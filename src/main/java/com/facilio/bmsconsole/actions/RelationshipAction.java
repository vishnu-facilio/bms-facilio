package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.RelationshipContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

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

	public String addOrUpdateRelationShip() throws Exception {
		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.RELATIONSHIP, relationship);
		Chain chain;
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

		Chain chain = ReadOnlyChainFactory.getRelationshipChain();
		chain.execute(context);

		setResult("RelationshipList", context.get(FacilioConstants.ContextNames.RELATIONSHIP_LIST));
		return SUCCESS;
	}

	public String deleteRelationship() throws Exception {
		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.ID, id);
		Chain chain = TransactionChainFactory.getDeleteRelationshipChain();
		chain.execute(context);
		return SUCCESS;
	}

}
