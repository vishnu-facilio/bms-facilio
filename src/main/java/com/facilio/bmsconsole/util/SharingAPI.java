package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.ViewSharingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
	public static int deleteSharing (Collection<Long> ids, FacilioModule module) throws SQLException {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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

}
