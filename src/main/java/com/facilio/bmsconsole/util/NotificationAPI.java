package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.wms.message.WmsNotification;
import com.facilio.wms.util.WmsApi;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationAPI {
	
	public static void sendNotification(Long recipient, NotificationContext notification) throws Exception {
		
		List<Long> recipients = new ArrayList<>();
		recipients.add(recipient);
		sendNotification(recipients, notification);
	}
	
	public static void sendNotification(List<Long> recipients, NotificationContext notification) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getNotificationModule().getTableName())
				.fields(FieldFactory.getNotificationFields());
		
		for (Long recipient : recipients) {
			notification.setUserId(recipient);
			notification.setActorId(AccountUtil.getCurrentUser().getId());
			Map<String, Object> props = FieldUtil.getAsProperties(notification);
			insertBuilder.addRecord(props);
		}
		
		insertBuilder.save();
		
		WmsApi.sendNotification(recipients, new WmsNotification().setNotification(FieldUtil.getAsJSON(notification)));
	}
	
	public static boolean markAllAsSeen(long userId) throws Exception {
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getNotificationModule().getTableName())
				.fields(FieldFactory.getNotificationFields())
				.andCustomWhere("ORGID = ? AND USERID = ? AND (IS_SEEN IS NULL OR IS_SEEN = false)", orgId, userId);
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("isSeen", true);
		prop.put("seenAt", System.currentTimeMillis());
		
		int updatedRows = updateBuilder.update(prop);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	public static boolean markAllAsRead(long userId) throws Exception {
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getNotificationModule().getTableName())
				.fields(FieldFactory.getNotificationFields())
				.andCustomWhere("ORGID = ? AND USERID = ? AND (IS_READ IS NULL OR IS_READ = false)", orgId, userId);
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("isRead", true);
		prop.put("readAt", System.currentTimeMillis());
		
		int updatedRows = updateBuilder.update(prop);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	public static boolean markAsRead(long userId, List<Long> idList) throws Exception {
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		String ids = StringUtils.join(idList, ",");
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(ModuleFactory.getNotificationModule()));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(ids);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getNotificationModule().getTableName())
				.fields(FieldFactory.getNotificationFields())
				.andCustomWhere("ORGID = ? AND USERID = ?", orgId, userId)
				.andCondition(idCondition);
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("isRead", true);
		prop.put("readAt", System.currentTimeMillis());
		
		int updatedRows = updateBuilder.update(prop);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	public static int getUnseenNotificationsCount(long userId) throws Exception {
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		FacilioField countFld = new FacilioField();
		countFld.setName("unseen");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getNotificationModule().getTableName())
				.andCustomWhere("ORGID = ? AND USERID = ? AND (IS_SEEN IS NULL OR IS_SEEN = false)", orgId, userId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Long count = ((Number) props.get(0).get("unseen")).longValue();
			return count.intValue();
		}
		return 0;
	}
	
	public static int getUnreadNotificationsCount(long userId) throws Exception {
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		FacilioField countFld = new FacilioField();
		countFld.setName("unread");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getNotificationModule().getTableName())
				.andCustomWhere("ORGID = ? AND USERID = ? AND (IS_READ IS NULL OR IS_READ = false)", orgId, userId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Long count = ((Number) props.get(0).get("unread")).longValue();
			return count.intValue();
		}
		return 0;
	}
	
	public static List<NotificationContext> getNotifications(long userId, int offset, int limit) throws Exception {
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getNotificationFields())
				.table(ModuleFactory.getNotificationModule().getTableName())
				.andCustomWhere("ORGID = ? AND USERID = ?", orgId, userId)
				.orderBy("CREATED_TIME desc")
				.offset(offset)
				.limit(limit);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<NotificationContext> notifications = new ArrayList();
			for(Map<String, Object> prop : props) {
				notifications.add(FieldUtil.getAsBeanFromMap(prop, NotificationContext.class));
			}
			return notifications;
		}
		return null;
	}
}
