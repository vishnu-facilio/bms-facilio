package com.facilio.bmsconsole.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tiles.request.collection.CollectionUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wms.message.WmsNotification;
import com.facilio.wms.util.WmsApi;

;

public class NotificationAPI {
	
	private static final Logger LOGGER = LogManager.getLogger(NotificationAPI.class.getName());
	
	
	private static List<Pair<String, Boolean>> getMobileInstanceIDs(List<Long> userIds) throws Exception {
		List<Pair<String, Boolean>> mobileInstanceIds = new ArrayList<>();
		List<User> userList = AccountUtil.getUserBean().getUsers(null, true, false, userIds);
		List<Long> userIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(userList)) {
			userIdList = userList.stream().map(User::getUid).collect(Collectors.toList());
			List<Map<String, Object>> instanceIdList = IAMUserUtil.getUserMobileSettingInstanceIds(userIdList);
			for (Map<String, Object> instance : instanceIdList) {
				Boolean fromPortal = (Boolean)instance.get("fromPortal");
				if (fromPortal == null) {
					fromPortal = false;
				}
				mobileInstanceIds.add(Pair.of((String) instance.get("mobileInstanceId"), fromPortal));
			}
		}
		
		return mobileInstanceIds;
	}
	
	public static void sendPushNotification(List<Long> userIds,JSONObject message) throws Exception {

		List<Pair<String, Boolean>> mobileInstanceSettings = getMobileInstanceIDs(userIds);
		LOGGER.info("Sending push notifications for ids : "+userIds);
		if (mobileInstanceSettings != null && !mobileInstanceSettings.isEmpty()) {
			LOGGER.info("Sending push notifications for mobileIds : "+mobileInstanceSettings.stream().map(pair -> pair.getLeft()).collect(Collectors.toList()));
			for (Pair<String, Boolean> mobileInstanceSetting : mobileInstanceSettings) {
				if (mobileInstanceSetting != null) {
					
//					message.put("to","fHD-LqsgPBA:APA91bEuVMMnWqC_LaxYg1w1K9fF4bL9Exunbh7W4syfBBCkEIhQC0lYP2CT-EKAbdvS7Hl3iayAdKojXUgQ_OwAlMANO7Rtl8DbQ1-Zettsae6hXRG9bzh6ob9IjGXQBwNTBOu-qbmF");
					message.put("to", mobileInstanceSetting.getLeft());

					Map<String, String> headers = new HashMap<>();
					headers.put("Content-Type", "application/json");
					headers.put("Authorization", "key="+ (mobileInstanceSetting.getRight() ? FacilioProperties.getPortalPushNotificationKey() : FacilioProperties.getPushNotificationKey()));

					String url = "https://fcm.googleapis.com/fcm/send";

					AwsUtil.doHttpPost(url, headers, null, message.toJSONString());
				}
			}
		}
	}
	
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

	public static List<Pair<String, Boolean>> getMobileInstanceIDs(String idList) throws Exception {
		List<Pair<String, Boolean>> mobileInstanceIds = new ArrayList<>();
		List<Long> idLongList = Stream.of(idList.split(",")).map(Long::valueOf).collect(Collectors.toList());
		List<User> userList = AccountUtil.getUserBean().getUsers(null, true, false, idLongList);
		List<Long> userIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(userList)) {
			userIdList = userList.stream().map(User::getUid).collect(Collectors.toList());
			List<Map<String, Object>> instanceIdList = IAMUserUtil.getUserMobileSettingInstanceIds(userIdList);
			for (Map<String, Object> instance : instanceIdList) {
				Boolean fromPortal = (Boolean)instance.get("fromPortal");
				if (fromPortal == null) {
					fromPortal = false;
				}
				mobileInstanceIds.add(Pair.of((String) instance.get("mobileInstanceId"), fromPortal));
			}
		}

		return mobileInstanceIds;
	}
}
