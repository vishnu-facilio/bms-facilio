package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.util.V3Util;

public class AddFaultImpactRelationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		NewReadingRuleContext readingRule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
		
		if(readingRule.getImpact() != null) {
			
			FaultImpactContext impact = (FaultImpactContext)V3Util.getRecord(FacilioConstants.FaultImpact.MODULE_NAME, readingRule.getImpact().getId(), null);
			
			Map<Long, ResourceContext> assetsMap = (Map<Long, ResourceContext>) context.get(FacilioConstants.ContextNames.ASSETS);
			
			NameSpaceContext impactNameSpaceContext = new NameSpaceContext(NSType.IMPACT_RULE,readingRule.getId(),readingRule.getNs().getExecInterval());

			Long impactNameSpaceId = NewReadingRuleAPI.addNamespace(impactNameSpaceContext);
			
			List<NameSpaceField> fields = new ArrayList<NameSpaceField>();
			
			for(FaultImpactNameSpaceFieldContext faultImpactField : impact.getFields()) {
				NameSpaceField field = faultImpactField.getNameSpaceField();
				field.setNsId(impactNameSpaceId);
				fields.add(field);
			}
			
			NewReadingRuleAPI.addNamespaceFields(impactNameSpaceId, assetsMap, fields);
			
		}
		return false;
	}

}
