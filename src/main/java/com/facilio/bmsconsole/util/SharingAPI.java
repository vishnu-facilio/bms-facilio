package com.facilio.bmsconsole.util;

import java.util.*;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class SharingAPI {
	public static void addSharing (SharingContext<? extends SingleSharingContext> sharing, long parentId, FacilioModule module) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(module.getTableName())
														.fields(FieldFactory.getSharingFields(module))
														;
		
		for (SingleSharingContext share : sharing) {
			share.setOrgId(AccountUtil.getCurrentOrg().getId());
			share.setParentId(parentId);
			insertBuilder.addRecord(FieldUtil.getAsProperties(share));
		}
		insertBuilder.save();
		
		for (int i = 0; i < insertBuilder.getRecords().size(); i++) {
			SingleSharingContext share = sharing.get(i);
			share.setId((long) insertBuilder.getRecords().get(i).get("id"));
		}
	}
	
	
	public static <E extends SingleSharingContext> SharingContext<E> getSharing (long parentId, FacilioModule module, Class<E> classObj) throws Exception {
		List<FacilioField> fields = FieldFactory.getSharingFields(module);
		FacilioField parentIdField = FieldFactory.getAsMap(fields).get("parentId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), PickListOperators.IS))
														.orderBy("ID")
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			SharingContext<E> sharing = new SharingContext<E>();
			for (Map<String, Object> prop : props) {
				sharing.add(FieldUtil.getAsBeanFromMap(prop, classObj));
			}
			return sharing;
		}
		return null;
	}
	public static int deleteSharingForParent (List<Long> parentIds, FacilioModule module) throws Exception {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", StringUtils.join(parentIds, ","), NumberOperators.EQUALS))
				;

		return deleteBuilder.delete();
	}
	public static int deleteSharing (Collection<Long> ids, FacilioModule module) throws Exception {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(ids, module))
														;
		
		return deleteBuilder.delete();
	}
	public static <E extends SingleSharingContext> SharingContext<E> getSharingList(FacilioModule module, Class<E> classObj) throws Exception {
		List<FacilioField> fields = FieldFactory.getSharingFields(module);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;
		List<Map<String, Object>> props = select.get();
		SharingContext<E> sharingList = new SharingContext<E>();
		if(props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				sharingList.add(FieldUtil.getAsBeanFromMap(prop, classObj));
			}
		}
		return sharingList;	
		
	}
	
	public static <E extends SingleSharingContext> Map<Long, SharingContext<E>> getSharingMap(FacilioModule module, Class<E> classObj) throws Exception {
		List<FacilioField> fields = FieldFactory.getSharingFields(module);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				;
		List<Map<String, Object>> props = select.get();
		Map<Long, SharingContext<E>> map = new HashMap<>();
		if(props != null && !props.isEmpty()) {
			List<E> sharingList = FieldUtil.getAsBeanListFromMapList(props, classObj);
			for (E sharing: sharingList) {
				long parentId = sharing.getParentId();
				SharingContext<E> recordSharing = map.get(parentId);
				if (recordSharing == null) {
					recordSharing = new SharingContext<E>();
					map.put(parentId, recordSharing);
				}
				recordSharing.add(sharing);
			}
		}
		return map;	
		
	}

	public static SharingContext<SingleSharingContext> getDefaultAppTypeSharing(FacilioView defaultView){
		SharingContext<SingleSharingContext> appSharing = new SharingContext<>();
		if(CollectionUtils.isNotEmpty(defaultView.getViewSharing())) {
			for(SingleSharingContext defaultSharing : defaultView.getViewSharing()){
				if(defaultSharing.getTypeEnum() == SingleSharingContext.SharingType.APP){
					appSharing.add(defaultSharing);
				}
			}
		}
		return appSharing;

	}
	public static SingleSharingContext getCurrentAppTypeSharingForCustomViews() {
		if(AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getAppDomain() != null) {
			SingleSharingContext appSharing = new SingleSharingContext();
			appSharing.setType(SingleSharingContext.SharingType.APP);
			appSharing.setAppType(AccountUtil.getCurrentUser().getAppDomain().getAppDomainType());
			return appSharing;
		}
		return null;
	}
}
