package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateBulkLabourContractStatusCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule labourContractModule = modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS);
		List<Long> labourContractIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		int status = (Integer)context.get(FacilioConstants.ContextNames.STATUS);
		Map<String, Object> updateMap = new HashMap<>();
		FacilioField statusField = modBean.getField("status", labourContractModule.getName());
		updateMap.put("status", status);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		updatedfields.add(statusField);
		UpdateRecordBuilder<LabourContractContext> updateBuilder = new UpdateRecordBuilder<LabourContractContext>()
						.module(labourContractModule)
						.fields(updatedfields)
						.andCondition(CriteriaAPI.getIdCondition(labourContractIds,labourContractModule));
		updateBuilder.withChangeSet(LabourContractContext.class);
	     context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.updateViaMap(updateMap));
	 	context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, updateBuilder.getChangeSet());
		return false;
	}

}
