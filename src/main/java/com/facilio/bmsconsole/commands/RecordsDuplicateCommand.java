package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;

public class RecordsDuplicateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.ID);
		int startingNumber = (int) context.get(FacilioConstants.ContextNames.STARTING_NUMBER);
		JSONObject duplicateObj = (JSONObject) context.get(FacilioConstants.ContextNames.DUPLICATE_OBJECT);
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		FacilioField primaryField = modBean.getPrimaryField(moduleName);
		if( primaryField == null) {
			return false;
		}
		
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
		if (beanClassName == null) {
			if (module.isCustom()) {
				beanClassName = CustomModuleData.class;
			}
			else {
				beanClassName = ModuleBaseWithCustomFields.class;
			}
		}
		
		ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context
				.get(FacilioConstants.ContextNames.RECORD);
		
		if (((ModuleBaseWithCustomFields) record).getLocalId() > 0) {
			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		}
				
		String quantity = (String) duplicateObj.get("quantity");
		String name = (String) duplicateObj.get("name");
		String prefix = (String) duplicateObj.get("prefix");
		String sufix = (String) duplicateObj.get("sufix");		
		
		
		List<ModuleBaseWithCustomFields> records = new ArrayList<ModuleBaseWithCustomFields>();
		if (quantity != null) {
			for (int i = 0; i < Integer.parseInt(quantity); i++) {
				ModuleBaseWithCustomFields item = (ModuleBaseWithCustomFields) FieldUtil.cloneBean(record, beanClassName);
				((ModuleBaseWithCustomFields) item).setId(-1L);
				((ModuleBaseWithCustomFields) item).setLocalId(-1L);
				String value = (prefix != null ? prefix + " " : "") + name + " " + (startingNumber) + (sufix != null ? " " +sufix : "");
				PropertyUtils.setProperty(item, primaryField.getName(), value);
				records.add(item);
				startingNumber = startingNumber + 1;
			}
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		
		return false;
	}

	

}
