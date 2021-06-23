package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateAlarmAssetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		String source = (String) context.get(EventConstants.EventContextNames.SOURCE);
		if(alarm != null && source != null && !source.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			UpdateRecordBuilder<AlarmContext> updateBuilder = new UpdateRecordBuilder<AlarmContext>()
																	.module(module)
																	.fields(fields)
																	.andCustomWhere("SOURCE = ?", source);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(alarm));
		}
		return false;
	}

}
