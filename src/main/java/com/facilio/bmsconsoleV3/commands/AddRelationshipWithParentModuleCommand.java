package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.v3.context.Constants;

public class AddRelationshipWithParentModuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<VirtualMeterTemplateContext> virtualMeterTemplateContexts = Constants.getRecordList((FacilioContext) context);
		
		if (CollectionUtils.isNotEmpty(virtualMeterTemplateContexts)) {
			
			for (VirtualMeterTemplateContext virtualMeterTemplateContext : virtualMeterTemplateContexts) {
				
				String parentModuleName = virtualMeterTemplateContext.getScopeEnum().getModuleName();
				
				String childModuleName = FacilioConstants.Meter.METER;
				
				RelationRequestContext relationship = new RelationRequestContext();
				
				relationship.setName("VM Rel");
				relationship.setDescription("VM Rel desc");

				relationship.setFromModule(Constants.getModBean().getModule(parentModuleName));
				relationship.setFromModuleId(Constants.getModBean().getModule(parentModuleName).getModuleId());
				relationship.setToModule(Constants.getModBean().getModule(childModuleName));
				relationship.setToModuleId(Constants.getModBean().getModule(childModuleName).getModuleId());
				
				relationship.setRelationName("VM Forwared");
				relationship.setReverseRelationName("vm reverse");
				
				relationship.setRelationType(RelationRequestContext.RelationType.ONE_TO_ONE);
				
				
				FacilioChain chain = TransactionChainFactory.getAddOrUpdateRelationChain();
		        FacilioContext newContext = chain.getContext();
		        newContext.put(FacilioConstants.ContextNames.RELATION, relationship);

		        chain.execute();
		        
		        virtualMeterTemplateContext.setRelationShipId(((RelationRequestContext)newContext.get(FacilioConstants.ContextNames.RELATION)).getId());
			}
			
		}
		return false;
	}

}
