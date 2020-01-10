package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddResetCounterMetaCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(AddResetCounterMetaCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		Boolean isCopyReading = (Boolean) context.get("IS_COPY_READING");
		
		if(isCopyReading == true) {
			LOGGER.info("####Asset Copy Reading execution in AddResetCounterMetaCommand start time : "+ System.currentTimeMillis());
		}
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
