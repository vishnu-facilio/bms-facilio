package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SharingAPI {
	public static void addSharing (SharingContext sharing, long parentId, FacilioModule module) throws Exception {
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
	}
	
	public static SharingContext getSharing (long parentId, FacilioModule module) throws Exception {
		List<FacilioField> fields = FieldFactory.getSharingFields(module);
		FacilioField parentIdField = FieldFactory.getAsMap(fields).get("parentId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(parentId), PickListOperators.IS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			SharingContext sharing = new SharingContext();
			for (Map<String, Object> prop : props) {
				sharing.add(FieldUtil.getAsBeanFromMap(prop, SingleSharingContext.class));
			}
			return sharing;
		}
		return null;
	}
}
