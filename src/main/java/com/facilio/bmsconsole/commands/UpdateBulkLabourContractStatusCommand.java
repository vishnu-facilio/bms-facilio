package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateBulkLabourContractStatusCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
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
	     context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.updateViaMap(updateMap));
		return false;
	}

}
