package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map.Entry;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class UpdateDeltaInRDMInReadingResetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		for(ResetCounterMetaContext resetCounter:resetCounterMetaList){
			
			if(resetCounter.getEndvalue() > 0){
				
				FacilioField field = resetCounter.getField();
				String moduleName = field.getModule().getName();
				FacilioModule module = modBean.getModule(moduleName);
				List<FacilioField> fields = modBean.getAllFields(moduleName);
				ReadingContext reading = ReadingsAPI.getReading(module, fields, resetCounter.getReadingDataId());

				FacilioField deltaField = modBean.getField(field.getName()+"Delta", moduleName);
				ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(reading.getParentId(),deltaField);
				if(rdm != null && rdm.getReadingDataId() == reading.getId()){
					for(Entry<String, Object> entry : reading.getReadings().entrySet()){
						if(entry.getKey().equalsIgnoreCase(field.getName()+"Delta")){
							rdm.setValue(entry.getValue());
							ReadingsAPI.updateReadingDataMeta(rdm);
						}
					}
				}
				
				
			}
		}
		return false;
	}

}
