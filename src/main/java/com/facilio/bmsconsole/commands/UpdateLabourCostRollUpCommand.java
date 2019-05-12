package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateLabourCostRollUpCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule labourModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR);
		
		LabourContractContext labourcontract = (LabourContractContext)context.get(FacilioConstants.ContextNames.RECORD);
		for(LabourContractLineItemContext lineItem : labourcontract.getLineItems()) {
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField costField = modBean.getField("cost", labourModule.getName());
			updateMap.put("cost", lineItem.getCost() );
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(costField);
			if(lineItem.getLabour() != null && lineItem.getLabour().getId() != -1) {
				UpdateRecordBuilder<LabourContext> updateBuilder = new UpdateRecordBuilder<LabourContext>()
								.module(labourModule)
								.fields(updatedfields)
								.andCondition(CriteriaAPI.getIdCondition(lineItem.getLabour().getId(),labourModule));
			     updateBuilder.updateViaMap(updateMap);
			}
			
		}
		return false;
	}

}
