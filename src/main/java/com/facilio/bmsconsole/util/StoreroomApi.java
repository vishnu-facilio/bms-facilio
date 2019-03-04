package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class StoreroomApi {
	public static Map<Long, StoreRoomContext> getStoreRoomMap(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
		SelectRecordsBuilder<StoreRoomContext> selectBuilder = new SelectRecordsBuilder<StoreRoomContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(StoreRoomContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		return selectBuilder.getAsMap();
	}
	
	public static StoreRoomContext getStoreRoom(long id) throws Exception
	{
		if(id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
		SelectRecordsBuilder<StoreRoomContext> selectBuilder = new SelectRecordsBuilder<StoreRoomContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(StoreRoomContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<StoreRoomContext> storerooms =  selectBuilder.get();
		if(storerooms!=null &&!storerooms.isEmpty()) {
			return storerooms.get(0);
		}
		return null;
	}
}
