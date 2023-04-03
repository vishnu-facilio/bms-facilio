package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.UserBean;
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
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.wms.message.WmsNotification;
import com.facilio.wms.util.WmsApi;

;

public class NotificationAPI {
	
	private static final Logger LOGGER = LogManager.getLogger(NotificationAPI.class.getName());
	
	
/*	private static List<Pair<String, Boolean>> getMobileInstanceIDs(List<Long> userIds) throws Exception {
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
	}*/
	
	public static List<Long> checkUserDelegation(List<Long> userIds) throws Exception {
		
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		
		if(userIds != null && !userIds.isEmpty()) {
			List<Long> updatedUserIds = new ArrayList<Long>();
			
			for(Long userId : userIds) {
				
				User user = userBean.getUser(userId, false);
				if(user != null) {
					
					List<User> delegatedUsers = DelegationUtil.getDelegatedUsers(user, DateTimeUtil.getCurrenTime(), DelegationType.EMAIL_NOTIFICATION);
					if(CollectionUtils.isNotEmpty(delegatedUsers)){
						for(User delegatedUser: delegatedUsers){
							if(!updatedUserIds.contains(delegatedUser.getOuid())) {
								updatedUserIds.add(delegatedUser.getOuid());
							}
						}
					}
					else{
						if(!updatedUserIds.contains(user.getOuid())){
							updatedUserIds.add(user.getOuid());
						}
					}

				}
			}
			
			return updatedUserIds;
		}
		
		return userIds;
	}
	
	public static void sendPushNotification(List<Long> userIds,JSONObject message) throws Exception {
		
		userIds = checkUserDelegation(userIds);

		UserNotificationContext userNotificationContext = FieldUtil.getAsBeanFromJson(message, UserNotificationContext.class);

		ApplicationContext applicationContext = ApplicationApi.getApplicationForId((Long) message.get("application"));
		String appLinkName = applicationContext.getLinkName();
		List<UserMobileSetting> mobileInstanceSettings = getMobileInstanceIDs(userIds,appLinkName);
		LOGGER.info("Sending push notifications for ids : "+userIds);
		if (mobileInstanceSettings != null && !mobileInstanceSettings.isEmpty()) {
			LOGGER.info("Sending push notifications for mobileIds : "+mobileInstanceSettings.stream().map(pair -> pair.getMobileInstanceId()).collect(Collectors.toList()));
			for (UserMobileSetting mobileInstanceSetting : mobileInstanceSettings) {
				if (mobileInstanceSetting != null) {

					JSONObject obj = new JSONObject();
					if (appLinkName.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
						obj = UserNotificationContext.getFcmObject(userNotificationContext);
					}else {
						obj = UserNotificationContext.getFcmObjectMaintainence(userNotificationContext);
					}

//					message.put("to","fHD-LqsgPBA:APA91bEuVMMnWqC_LaxYg1w1K9fF4bL9Exunbh7W4syfBBCkEIhQC0lYP2CT-EKAbdvS7Hl3iayAdKojXUgQ_OwAlMANO7Rtl8DbQ1-Zettsae6hXRG9bzh6ob9IjGXQBwNTBOu-qbmF");
					obj.put("to", mobileInstanceSetting.getMobileInstanceId());

					Map<String, String> headers = new HashMap<>();
					headers.put("Content-Type", "application/json");
					headers.put("Authorization", "key="+ (mobileInstanceSetting.isFromPortal() ? FacilioProperties.getPortalPushNotificationKey() : FacilioProperties.getPushNotificationKey()));

					String url = "https://fcm.googleapis.com/fcm/send";

					AwsUtil.doHttpPost(url, headers, null, obj.toJSONString());
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
		
		recipients = checkUserDelegation(recipients);
		
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

	public static List<UserMobileSetting> getMobileInstanceIDs(List<Long> idList, String appLinkName) throws Exception {
		List<User> userList = AccountUtil.getUserBean().getUsers(null, true, false, idList);
		List<Long> userIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(userList)) {
			userIdList = userList.stream().map(User::getUid).collect(Collectors.toList());
			List<UserMobileSetting> instanceIdList = IAMUserUtil.getUserMobileSettingInstanceIds(userIdList,appLinkName);
			for (UserMobileSetting instance : instanceIdList) {
				Boolean fromPortal = instance.isFromPortal();
				if (fromPortal == null) {
					instance.setFromPortal(false);
				}
			}
			return  instanceIdList;
		}

		return null;
	}

	public static void pushNotification(JSONObject obj, List<Long> idLongList, Boolean isPushNotification, Object currentRecord, FacilioModule module , WorkflowRuleContext currentRule) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long parentModuleId = -1;
		if (module != null) {
			parentModuleId = module.getModuleId();
		} else if (currentRecord instanceof ModuleBaseWithCustomFields) {
			parentModuleId = ((ModuleBaseWithCustomFields) currentRecord).getModuleId();
		} else {
			// temp fix
			LOGGER.info("Cannot find moduleId for the current record, skipping for now");
		}
		List<Long> delegatedUserList = NotificationAPI.checkUserDelegation(idLongList);
		if (CollectionUtils.isNotEmpty(delegatedUserList)) {
			idLongList = delegatedUserList;
		}
		if (CollectionUtils.isNotEmpty(idLongList) && parentModuleId > 0) {
			LOGGER.info("Notification Modules entry : " + idLongList);
			List<UserNotificationContext> recordList = new ArrayList<>();
			if (idLongList.size() > 0) {
				for (Long uid : idLongList) {
					UserNotificationContext userNotification = new UserNotificationContext();
					userNotification = UserNotificationContext.instance(obj);
					User user = new User();
					user.setId(uid);
					userNotification.setUser(user);
					if (currentRule!=null) {
						userNotification.setSiteId(currentRule.getSiteId());
					}
					userNotification.setParentModule(parentModuleId);
					if (currentRecord instanceof ModuleBaseWithCustomFields) {
						long parentId = ((ModuleBaseWithCustomFields) currentRecord).getId();
						userNotification.setParentId(parentId);
					}
					userNotification.setActionType(UserNotificationContext.ActionType.SUMMARY);
					recordList.add(userNotification);
				}
			}

			FacilioModule userNotificationModule = modBean.getModule(FacilioConstants.ContextNames.USER_NOTIFICATION);
			JSONObject pushNotificationObj = new JSONObject();
			pushNotificationObj.put("isPushNotification", isPushNotification);
			V3Util.createRecordList(userNotificationModule, FieldUtil.getAsMapList(recordList, UserNotificationContext.class), pushNotificationObj, null);
		}
	}
}
