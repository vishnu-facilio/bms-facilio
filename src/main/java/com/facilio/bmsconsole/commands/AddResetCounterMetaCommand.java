package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddResetCounterMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		if(resetCounterMetaList != null && !resetCounterMetaList.isEmpty()){
			FacilioModule module = ModuleFactory.getResetCounterMetaModule();
			List<Map<String, Object>> props = new ArrayList<>();
			
			for (ResetCounterMetaContext reset : resetCounterMetaList) {
				Map<String, Object> prop = FieldUtil.getAsProperties(reset);
				props.add(prop);
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
					.fields(FieldFactory.getResetCounterMetaFields()).addRecords(props);
           insertBuilder.save();
		}
		return false;
	}

}
