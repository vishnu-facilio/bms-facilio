package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

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
	
	public static void updateStoreRoomLastPurchasedDate(long storeRoomId, long lastPurchasedDate) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
		StoreRoomContext storeRoom = new StoreRoomContext();
		storeRoom.setId(storeRoomId);
		storeRoom.setLastPurchasedDate(lastPurchasedDate);
		UpdateRecordBuilder<StoreRoomContext> updateBuilder = new UpdateRecordBuilder<StoreRoomContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(storeRoomId, module));
		updateBuilder.update(storeRoom);
	}
	
	public static SelectRecordsBuilder<StoreRoomContext> getStoreRoomListBuilder(Long siteId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
		SelectRecordsBuilder<StoreRoomContext> builder = new SelectRecordsBuilder<StoreRoomContext>().module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName())).select(fields);
		builder.innerJoin("Storeroom_Sites").on("Storeroom_Sites.STORE_ROOM_ID = "+module.getTableName()+".ID");

		List<Long> accessibleSpaces = AccountUtil.getCurrentUser().getAccessibleSpace();
		
		if (siteId != null && siteId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("Storeroom_Sites.SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
		}
		else {
			if (accessibleSpaces != null && !accessibleSpaces.isEmpty()) {
				Set<Long> siteIds = new HashSet<Long>();
				for(Long accessibleSpace : accessibleSpaces) {
					BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(accessibleSpace);
					siteIds.add(baseSpace.getSite().getId());
				}
				builder.andCondition(CriteriaAPI.getConditionFromList("Storeroom_Sites.SITE_ID", "siteId", siteIds, NumberOperators.EQUALS));
			}
		}
		return builder;

	}
	
	public static Set<Long> getStoreRoomList(Long siteId) throws Exception {
		SelectRecordsBuilder<StoreRoomContext> builder = getStoreRoomListBuilder(siteId);
		List<StoreRoomContext> storeRooms = builder.get();
		Set<Long> storeIds = new HashSet<Long>();
		if(CollectionUtils.isNotEmpty(storeRooms)) {
			for(StoreRoomContext storeRoom : storeRooms) {
				storeIds.add(storeRoom.getId());
			}
		}
		return storeIds;
	}
}
