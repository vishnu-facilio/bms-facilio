package com.facilio.bmsconsole.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class RecordAPI {

	public static void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}
	
	public static void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}
	
	public static ModuleBaseWithCustomFields getRecord (String modName, Long recId) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(modName);
		List<FacilioField> fields = modBean.getAllFields(modName);
		
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(modName);
		if (beanClassName == null) {
			beanClassName = ModuleBaseWithCustomFields.class;
		}
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
															.module(module)
															.beanClass(beanClassName)
															.select(fields)
															.andCondition(CriteriaAPI.getIdCondition(Long.valueOf(recId), module))
															;
		
		List<? extends ModuleBaseWithCustomFields> records = builder.get();
		if(CollectionUtils.isNotEmpty(records)) {
			return records.get(0);
		}
		else {
			return null;
		}
	}
	
	public static ClientContext getClientForSite(long siteId) throws Exception {
		ClientContext clientContext = null;
		if (siteId <= 0) {
			return clientContext;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(SiteContext.class)
				.andCondition(CriteriaAPI.getIdCondition(siteId, module))
				;
		List<SiteContext> services = selectBuilder.get();
		if(!CollectionUtils.isEmpty(services)) {
			return services.get(0).getClient();
		}
		
		return clientContext;
		
	}
}
