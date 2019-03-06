package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GenericGetModuleDataDetailCommand implements Command {

	protected Class beanClassName;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(recordId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			if (beanClassName == null) {
				beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(moduleName);
				if (beanClassName == null) {
					beanClassName = ModuleBaseWithCustomFields.class;
				}
			}
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																	.module(module)
																	.beanClass(beanClassName)
																	.select(fields)
																	.andCondition(CriteriaAPI.getIdCondition(recordId, module))
																	;

			List<LookupFieldMeta> fetchLookup = (List<LookupFieldMeta>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
			if (CollectionUtils.isNotEmpty(fetchLookup)) {
				builder.fetchLookups(fetchLookup);
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
