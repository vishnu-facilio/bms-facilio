package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateResetCounterMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		FacilioModule module = ModuleFactory.getResetCounterMetaModule();
		
		for(ResetCounterMetaContext resetCounter:resetCounterMetaList){
			Map<String, Object> prop = FieldUtil.getAsProperties(resetCounter);
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName()).fields(FieldFactory.getResetCounterMetaFields())
					.andCondition(CriteriaAPI.getIdCondition(resetCounter.getId(), module));
			updateBuilder.update(prop);
		}
		
		return false;
	}

}
