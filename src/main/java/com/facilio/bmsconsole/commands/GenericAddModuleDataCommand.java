package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateChangeSet;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenericAddModuleDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		if(record != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);

			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			
			InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
																						.module(module)
																						.fields(fields)
																						;
			
			Boolean setLocalId = (Boolean) context.get(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID);
			if (setLocalId != null && setLocalId) {
				insertRecordBuilder.withLocalId();
			}
			
			Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
			if (withChangeSet != null && withChangeSet) {
				insertRecordBuilder.withChangeSet();
			}
			long id = insertRecordBuilder.insert(record);
			record.setId(id);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);

		}
		else {
			throw new IllegalArgumentException("Record cannot be null during addition");
		}
		return false;
	}

}
