package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;

public class GenericGetModuleDataDetailCommand extends FacilioCommand {

	protected Class beanClassName;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		boolean fetchDeleted = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, false);
		
		if(recordId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
				context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);
			}
			if (beanClassName == null) {
				beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
				if (beanClassName == null) {
					if (module.isCustom()) {
						beanClassName = CustomModuleData.class;
					}
					else {
						beanClassName = ModuleBaseWithCustomFields.class;
					}
				}
			}
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																	.module(module)
																	.beanClass(beanClassName)
																	.select(fields)
																	.andCondition(CriteriaAPI.getIdCondition(recordId, module))
																	;

			boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
			if (skipModuleCriteria) {
				builder.skipModuleCriteria();
			}

			// To fetch deleted records set FacilioConstants.ContextNames.FETCH_DELETED_RECORDS to true
			if(fetchDeleted) {
				builder.fetchDeleted();
			}
			if(module.getName().equals("peopleannouncement")){
				Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
				builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("people"), Collections.singleton(AccountUtil.getCurrentUser().getPeopleId()), StringOperators.IS));
			}
			// TODO remove this and use FETCH_SUPPLEMENTS
			List<LookupField>fetchLookup = (List<LookupField>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
			if (CollectionUtils.isNotEmpty(fetchLookup)) {
				builder.fetchSupplements(fetchLookup);
			}
			
			List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
	        if (CollectionUtils.isNotEmpty(supplementFields)) {
	        		builder.fetchSupplements(supplementFields);
	        }
			
			List<ModuleBaseWithCustomFields> records = builder.get();
			if(records.size() > 0) {
				ResourceAPI.loadModuleResources(records, fields);
				context.put(FacilioConstants.ContextNames.RECORD, records.get(0));
			}
		}
		return false;
	}
}
