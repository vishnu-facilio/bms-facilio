package com.facilio.bmsconsole.workflow.rule;

import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.bmsconsoleV3.context.V3CustomModuleData;
import com.facilio.bmsconsoleV3.context.V3MailMessageContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3AttachmentAPI;
import com.facilio.fs.FileInfo;
import com.facilio.modules.fields.FileField;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.activity.ActivityType;
import com.facilio.activity.AlarmActivityType;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ExecuteSpecificWorkflowsCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.UpdateWoIdInNewAlarmCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.templates.ControlActionTemplate;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ReadingRuleType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand.PointedList;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.pdf.PdfUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.PublicFileUtil;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public enum ActionType {

	EMAIL_NOTIFICATION(1) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) {
				try {
					String to = (String) obj.get("to");
					if (to != null && !to.isEmpty() && checkIfActiveUserFromEmail(to)) {
						String attachmentsUrl = (String) obj.getOrDefault("attachmentsUrl", null);
						String attachmentsNames = (String) obj.getOrDefault("attachmentsNames", null);
						List<String> emails = new ArrayList<>();
						emails.add(to);
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
						}
						
						if(StringUtils.isNotEmpty(attachmentsUrl) && StringUtils.isNotEmpty(attachmentsNames)){
							String[] urls = attachmentsUrl.split(",");
							String[] names = attachmentsNames.split(",");
							if(names.length == urls.length) {
								Map<String, String> filesMap = new HashMap<String, String>();
								for(int i = 0; i < names.length; i++) {
									filesMap.put(names[i], urls[i]);
								}
								FacilioFactory.getEmailClient().sendEmail(obj, filesMap);
								return;
							}
						}
						FacilioFactory.getEmailClient().sendEmail(obj);
						
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	SMS_NOTIFICATION(2) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null && FacilioProperties.isProduction()) {
				try {
					String to = (String) obj.get("to");
					if (to != null && !to.isEmpty() && checkIfActiveUserFromPhone(to)) {
						List<String> sms = new ArrayList<>();
						SMSUtil.sendSMS(obj);

						sms.add(to);
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	BULK_EMAIL_NOTIFICATION(3) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) {
				try {
					JSONArray toEmails = null;
					Object toAddr = obj.remove("to");

					if (toAddr instanceof JSONArray) {
						toEmails = (JSONArray) toAddr;
					} else if (toAddr instanceof String) {
						toEmails = getTo(toAddr.toString());
					}

					if (toEmails != null && !toEmails.isEmpty()) {
						List<String> emails = new ArrayList<>();
						Boolean sendAsSeparateMail = (Boolean) obj.get("sendAsSeparateMail");
						if (sendAsSeparateMail != null && !sendAsSeparateMail) {
							String activeToEmails = "";
							for (Object toEmail : toEmails) {
								String to = (String) toEmail;
								if (to != null && !to.isEmpty() && checkIfActiveUserFromEmail(to)) {
									if (StringUtils.isNotEmpty(activeToEmails)) {
										activeToEmails += ",";
									}
									activeToEmails += to;
									emails.add(to);
								}
							}
							obj.put("to", activeToEmails);
							FacilioFactory.getEmailClient().sendEmail(obj);
						} else {
							for (Object toEmail : toEmails) {
								String to = (String) toEmail;
								if (to != null && !to.isEmpty() && checkIfActiveUserFromEmail(to)) {
									obj.put("to", to);

									if (AccountUtil.getCurrentOrg().getId() == 104) {
										LOGGER.info("Gonna Email : " + obj.toJSONString());
									}

									FacilioFactory.getEmailClient().sendEmail(obj);
									emails.add(to);
								}
							}
						}
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_EMAILS, emails);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	BULK_SMS_NOTIFICATION(4) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null && FacilioProperties.isProduction()) {
				try {
					JSONArray tos = null;
					Object toNums = obj.remove("to");

					if (toNums instanceof JSONArray) {
						tos = (JSONArray) toNums;
					} else if (toNums instanceof String) {
						tos = getTo(toNums.toString());
					}
					if (tos != null && !tos.isEmpty()) {
						List<String> sms = new ArrayList<>();
						for (Object toObj : tos) {
							String to = (String) toObj;
							if (to != null && !to.isEmpty() && checkIfActiveUserFromPhone(to)) {
								obj.put("to", to);
								SMSUtil.sendSMS(obj);
								sms.add(to);
							}
						}
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}

	},
	WEB_NOTIFICATION(5) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) {
				try {
					List<Long> reciepents = null;
					String toIds = (String) obj.get("to");
						if (toIds != null) {
							String[] toList = toIds.trim().split(",");
							for (String toId : toList) {
								if (toId.isEmpty()) {
									continue;
								}
								long id = Long.valueOf(toId);
								if (checkIfActiveUserFromId(id)) {
									reciepents.add(id);
								}
							}

							NotificationContext notification = new NotificationContext();
							notification.setInfo((String) obj.get("message"));
							int type = (int) obj.get("activityType");
							notification.setNotificationType(EventType.valueOf(type));
							NotificationAPI.sendNotification(reciepents, notification);
						}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	ADD_ALARM(6) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) {
			try {
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
					LOGGER.info("performAction for Rule:  "+currentRule.getId());
					ReadingRuleContext readingrule = (ReadingRuleContext) currentRule;
					Boolean addRule = false;
					if (readingrule.getOverPeriod() > 0 || readingrule.getOccurences() > 0 || readingrule.isConsecutive() || readingrule.getThresholdType() == ReadingRuleContext.ThresholdType.FLAPPING.getValue()) {
						BaseEventContext event =  ((ReadingRuleContext) currentRule).constructPreEvent(obj, (ReadingContext) currentRecord,context);
        				addAlarm(event, obj, context, readingrule, currentRecord);
					}
					else {
						BaseEventContext event = ((ReadingRuleContext) currentRule).constructEvent(obj, (ReadingContext) currentRecord,context);
						///handle impacts
						JSONObject impacts = (JSONObject) obj.get("impact");
						if (impacts != null && !impacts.isEmpty()) {
							Set<String> set = impacts.keySet();
							for (String fieldName : set) {
								JSONArray workFlowId = (JSONArray) impacts.get(fieldName);
								for (int i = 0; i < workFlowId.size(); i++) {
									long workFlowIds = (long) workFlowId.get(i);
									FacilioChain c = FacilioChain.getTransactionChain();
									FacilioContext impactContext = c.getContext();
									impactContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
									impactContext.put(FacilioConstants.ContextNames.RECORD, currentRecord);
									String moduleName = currentRule.getModuleName();
									impactContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
									c.addCommand(new ExecuteSpecificWorkflowsCommand(Collections.singletonList(workFlowIds), RuleType.IMPACT_RULE));
									c.execute(impactContext);
									if (impactContext.containsKey("impact_value")) {
										if(impactContext.get("impact_value") != null) {
											double impact_key = (double) impactContext.get("impact_value");
											PropertyUtils.setProperty(event, fieldName , impact_key);
										}
										else {
											LOGGER.info("No impact value for workFlowIds: "+workFlowIds+" moduleName "+moduleName+" currentRecord : "+currentRecord);
										}									
									}
								}
							}
						}
						addAlarm(event, obj, context, currentRule, currentRecord);
					}

				} else {
					if (obj != null) {
						if (obj.containsKey("subject")) {
							String subject = (String) obj.get("subject");
							obj.put("message", subject);
						}

						if (currentRule instanceof ReadingRuleContext) {
							switch (((ReadingRuleContext) currentRule).getReadingRuleTypeEnum()) {
								case THRESHOLD_RULE:
									AlarmAPI.addReadingAlarmProps(obj, (ReadingRuleContext) currentRule, (ReadingContext) currentRecord);
									break;
								case ML_RULE:
									AlarmAPI.addMLAlarmProps(obj, (ReadingRuleContext) currentRule, context);
									break;
							}
						}

						FacilioContext addEventContext = new FacilioContext();
						addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
						FacilioChain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
						getAddEventChain.execute(addEventContext);
						EventContext event = ((List<EventContext>) addEventContext.get(EventConstants.EventContextNames.EVENT_LIST)).get(0);
						if (currentRule instanceof ReadingRuleContext) {
							processAlarmMeta((ReadingRuleContext) currentRule, (long) obj.get("resourceId"), (long) obj.get("timestamp"), event, context);
						}
						if (addEventContext.get(FacilioConstants.ContextNames.IS_ALARM_CREATED) != null) {
							context.put(FacilioConstants.ContextNames.IS_ALARM_CREATED, addEventContext.get(FacilioConstants.ContextNames.IS_ALARM_CREATED));
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Exception occurred ", e);
			}
		}

		//Assuming readings will come in ascending order of time
		private void processAlarmMeta (ReadingRuleContext rule, long resourceId, long time, EventContext event, Context context) throws Exception {
			if (event.getAlarmId() != -1) {
				boolean isHistorical = true;
				Map<Long, ReadingRuleAlarmMeta> metaMap = (Map<Long, ReadingRuleAlarmMeta>) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_META);
				if (metaMap == null) {
					metaMap = rule.getAlarmMetaMap();
					isHistorical = false;
				}
				if (isHistorical || rule.getRuleGroupId() == 4216) {/*if (AccountUtil.getCurrentOrg().getId() == 135) {*/
					LOGGER.info("Meta map of rule : "+rule.getId()+" when creating alarm for resource "+resourceId+" at time : "+time+" : "+metaMap);
				}

				if (metaMap != null) {
					ReadingRuleAlarmMeta alarmMeta = metaMap.get(resourceId);
					if (alarmMeta == null) {
						metaMap.put(resourceId, addAlarmMeta(event.getAlarmId(), resourceId, rule, isHistorical));
					}
					else if (alarmMeta.isClear()) {
						if (isHistorical || rule.getRuleGroupId() == 4216) {/*if (AccountUtil.getCurrentOrg().getId() == 135) {*/
							LOGGER.info("Updating meta with alarm id : "+event.getAlarmId()+" for rule : "+rule.getId()+" for resource : "+resourceId);
						}
						alarmMeta.setAlarmId(event.getAlarmId());
						alarmMeta.setClear(false);
						if (!isHistorical) {
							ReadingRuleAPI.markAlarmMetaAsNotClear(alarmMeta.getId(), event.getAlarmId());
						}
					}
				}
				else {
					metaMap = new HashMap<>();
					rule.setAlarmMetaMap(metaMap);
					metaMap.put(resourceId, addAlarmMeta(event.getAlarmId(), resourceId, rule, isHistorical));
				}
			}
		}

		private ReadingRuleAlarmMeta addAlarmMeta (long alarmId, long resourceId, ReadingRuleContext rule, boolean isHistorical) throws Exception {
			if (isHistorical) {
				return ReadingRuleAPI.constructAlarmMeta(alarmId, resourceId, rule);
			}
			else {
				return ReadingRuleAPI.addAlarmMeta(alarmId, resourceId, rule);
			}
		}


	},
	PUSH_NOTIFICATION(7) {

		@SuppressWarnings("unchecked")
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			try {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//				if (obj != null && FacilioProperties.isProduction()) {
					String ids = (String) obj.get("id");
					if (!StringUtils.isEmpty(ids)) {
						String[] toList = ids.trim().split(",");
						for (String toId : toList) {
							if (toId.isEmpty()) {
								continue;
							}
							long id = Long.valueOf(toId);
							LOGGER.info("Notification Modules entry : "+ id);
							UserNotificationContext userNotification = UserNotificationContext.instance(obj);
							User user = new User();
							user.setId(id);
							userNotification.setUser(user);
							userNotification.setSiteId(currentRule.getSiteId());
							FacilioModule module = modBean.getModule((String) context.getOrDefault("moduleName", null));
							userNotification.setParentModule(module.getModuleId());
							userNotification.setParentId((long) context.getOrDefault("recordId", -1l));
							userNotification.setActionType(UserNotificationContext.ActionType.SUMMARY);
							FacilioChain chain = TransactionChainFactoryV3.addRecords();
							FacilioContext notificationContext = chain.getContext();
							notificationContext.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(userNotification));
							notificationContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.USER_NOTIFICATION);
							Boolean isPushNotification = (Boolean) obj.get("isSendNotification");

							if (isPushNotification != null && isPushNotification ) {
								notificationContext.put(FacilioConstants.ContextNames.DATA, obj);
							}
							chain.execute();
						}
						// Moving push notification Post transcation after notification Context is inserted
//						List<Pair<String, Boolean>> mobileInstanceSettings = getMobileInstanceIDs(ids);
//						LOGGER.info("Sending push notifications for ids : "+ids);
//						if (mobileInstanceSettings != null && !mobileInstanceSettings.isEmpty()) {
//							LOGGER.info("Sending push notifications for mobileIds : "+mobileInstanceSettings.stream().map(pair -> pair.getLeft()).collect(Collectors.toList()));
//							for (Pair<String, Boolean> mobileInstanceSetting : mobileInstanceSettings) {
//								if (mobileInstanceSetting != null) {
//									// content.put("to",
//									// "exA12zxrItk:APA91bFzIR6XWcacYh24RgnTwtsyBDGa5oCs5DVM9h3AyBRk7GoWPmlZ51RLv4DxPt2Dq2J4HDTRxW6_j-RfxwAVl9RT9uf9-d9SzQchMO5DHCbJs7fLauLIuwA5XueDuk7p5P7k9PfV");
//									obj.put("to", mobileInstanceSetting.getLeft());
//
//									Map<String, String> headers = new HashMap<>();
//									headers.put("Content-Type", "application/json");
//									headers.put("Authorization", "key="+ (mobileInstanceSetting.getRight() ? FacilioProperties.getPortalPushNotificationKey() : FacilioProperties.getPushNotificationKey()));
//
//									String url = "https://fcm.googleapis.com/fcm/send";
//
//									AwsUtil.doHttpPost(url, headers, null, obj.toJSONString());
////									System.out.println("Push notification sent");
////									System.out.println(obj.toJSONString());
//								}
//							}
//						}
					}
				// }
			} catch (Exception e) {
				LOGGER.error("Exception occurred ", e);
			}
		}

		private List<Pair<String, Boolean>> getMobileInstanceIDs(String idList) throws Exception {
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
	},
	EXECUTE_PM(8) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			try {
				executePM(currentRule, currentRecord, context);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				StringBuilder builder = new StringBuilder("Error occurred duting PM execution for Rule ")
						.append(currentRule.getId());

				if (currentRecord instanceof ModuleBaseWithCustomFields) {
					builder.append(" for record : ")
							.append(((ModuleBaseWithCustomFields) currentRecord).getId());

				}
				CommonCommandUtil.emailException("ExecutePMAction", builder.toString(), e);
				LOGGER.error(builder.toString(), e);
			}

		}

		private void executePM(WorkflowRuleContext currentRule, Object currentRecord, Context context) throws Exception {
			FacilioModule module = ModuleFactory.getPMTriggersModule();
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getPMTriggerFields()).table(module.getTableName())
					.andCustomWhere("RULE_ID = ?", currentRule.getId());
			List<Map<String, Object>> pmProps = selectRecordBuilder.get();
			if (pmProps != null && !pmProps.isEmpty()) {

				PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(pmProps.get(0), PMTriggerContext.class);
				PreventiveMaintenance pm = PreventiveMaintenanceAPI.getActivePM(trigger.getPmId(), true);
				if(pm != null) {
					FacilioContext pmContext = new FacilioContext();
					pmContext.put(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER, trigger);
					pmContext.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
					pmContext.put(FacilioConstants.ContextNames.PM_RESET_TRIGGERS, true);

					if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
						if (currentRecord instanceof BaseAlarmContext) {
							AlarmOccurrenceContext occurrence = ActionType.getAlarmOccurrenceFromAlarm((BaseAlarmContext) currentRecord);
							((BaseAlarmContext) currentRecord).setLastOccurrence(occurrence);
							String comment = ActionType.getNewV2AlarmCommentForUnClosedWO((BaseAlarmContext) currentRecord);
							pmContext.put(FacilioConstants.ContextNames.PM_UNCLOSED_WO_COMMENT, comment);
						}
					}
					else {
						if (currentRecord instanceof AlarmContext) {
							fetchSeverities((AlarmContext) currentRecord);
							pmContext.put(FacilioConstants.ContextNames.PM_UNCLOSED_WO_COMMENT, getNewAlarmCommentForUnClosedWO((AlarmContext) currentRecord));
						}
					}

					pmContext.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
					FacilioChain executePm = TransactionChainFactory.getNewExecutePreventiveMaintenanceChain();
					executePm.execute(pmContext);


					WorkOrderContext wo = (WorkOrderContext) pmContext.get(FacilioConstants.ContextNames.WORK_ORDER);
					if (context != null) {
						context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
					}
					if (currentRecord instanceof AlarmContext && wo != null) {
						AlarmAPI.updateWoIdInAlarm(wo.getId(), ((AlarmContext) currentRecord).getId());
					}
				}
			}
		}

		@Override
		public boolean isTemplateNeeded() {
			return false;
		}
	},
	ASSIGNMENT_ACTION(9) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			long assignedToUserId = -1, assignGroupId = -1;

			assignedToUserId = (long) obj.get("assignedUserId");
			assignGroupId = (long) obj.get("assignedGroupId");

			WorkOrderContext workOrder = (WorkOrderContext) currentRecord;
			WorkOrderContext updateWO = new WorkOrderContext();

			boolean userAssigned = false;
			if (assignedToUserId != -1  && (workOrder.getAssignedTo() == null || workOrder.getAssignedTo().getOuid() == -1)) {
				User user = new User();
				user.setOuid(assignedToUserId);
				workOrder.setAssignedTo(user);
				updateWO.setAssignedTo(user);
				userAssigned = true;
			}

			if (assignGroupId != -1 && (workOrder.getAssignmentGroup() == null || workOrder.getAssignmentGroup().getGroupId() == -1)) {
				Group group = new Group();
				group.setId(assignGroupId);
				workOrder.setAssignmentGroup(group);
				updateWO.setAssignmentGroup(group);
				userAssigned = true;
			}
			try {
				if (userAssigned) {
					if (assignedToUserId != -1 || assignGroupId != -1) {
						FacilioStatus status = TicketAPI.getStatus("Assigned");
						workOrder.setStatus(status);
						updateWO.setStatus(status);
					}
					workOrder.setAssignedBy(AccountUtil.getCurrentUser());
					updateWO.setAssignedBy(AccountUtil.getCurrentUser());
					
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
					UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
							.module(woModule).fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
							.andCondition(CriteriaAPI.getIdCondition(workOrder.getId(), woModule));
					updateBuilder.update(updateWO);
					
					CommonCommandUtil.addEventType(EventType.ASSIGN_TICKET, (FacilioContext) context);
				}
			} catch (Exception e) {
				LOGGER.error("Exception occurred ", e);
			}

		}

	},
	SLA_ACTION(10) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {

//			long duedate = -1;
			WorkOrderContext workOrder = (WorkOrderContext) currentRecord;
			if (workOrder.getPriority() == null) {
				return;
			}
			if (workOrder.getDueDate() != -1) {
				return;
			}
			long workorderpriority = workOrder.getPriority().getId();
			Long duration = null;

			JSONArray slaPolicyJson = (JSONArray) obj.get("slaPolicyJson");
			Iterator iter = slaPolicyJson.iterator();
			while (iter.hasNext()) {
				JSONObject slaPolicy = (JSONObject) iter.next();
				long priorityId = Long.parseLong(slaPolicy.get("priority").toString());
				if (priorityId == workorderpriority && slaPolicy.get("duration") != null) {
					duration = Long.parseLong(slaPolicy.get("duration").toString()) * 1000;
				}

			}

			// duration =
			// (Long)slaPolicyJson.get(String.valueOf(workorderpriority));
			if (duration != null) {
				long dueDate = ((WorkOrderContext)currentRecord).getCreatedTime() + duration;

				WorkOrderContext updateWO = new WorkOrderContext();
				((WorkOrderContext)currentRecord).setDueDate(dueDate);
				updateWO.setDueDate(dueDate);

				try {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
					UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
							.module(woModule).fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
							.andCondition(CriteriaAPI.getIdCondition(((WorkOrderContext)currentRecord).getId(), woModule));
					updateBuilder.update(updateWO);

				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}

			}

		}
	},
	CREATE_WO_FROM_ALARM(11) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			try {
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
					BaseAlarmContext baseAlarm = (BaseAlarmContext) currentRecord;
					long lastWoId = baseAlarm.getLastWoId();
					AlarmOccurrenceContext lastOccurrence = getAlarmOccurrenceFromAlarm(baseAlarm);
					if (lastOccurrence != null) {
						boolean createNewWO = false;
						WorkOrderContext workOrder = null;
						if (lastWoId == -1) {
							createNewWO = true;
						}
						else {
							workOrder = WorkOrderAPI.getWorkOrder(lastWoId);
							if (workOrder == null) {
								createNewWO = true;
							} else {
								FacilioStatus moduleState = workOrder.getModuleState();
								FacilioStatus status = TicketAPI.getStatus(moduleState.getId());
								if (status.getType() == FacilioStatus.StatusType.CLOSED) {
									createNewWO = true;
								}
							}
						}

						if (createNewWO) {
							FacilioChain c = TransactionChainFactory.getV2AlarmOccurrenceCreateWO();
							Context woContext = c.getContext();
							if (obj != null) {
								workOrder = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
								CommonCommandUtil.addAlarmActivityToContext(baseAlarm.getId(), -1, AlarmActivityType.CREATE_WORKORDER, obj, (FacilioContext) context, lastOccurrence.getId() );
								woContext.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
								woContext.put(FacilioConstants.ContextNames.TASK_MAP, workOrder.getTaskList());
							}
							woContext.put(FacilioConstants.ContextNames.RECORD_ID, lastOccurrence.getId());
							c.execute();
						}
						else {
							NoteContext note = new NoteContext();
							note.setBody(getNewV2AlarmCommentForUnClosedWO(baseAlarm));
							note.setParentId(workOrder.getId());
							note.setCreatedTime(lastOccurrence.getLastOccurredTime());

							FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
							FacilioContext noteContext = addNote.getContext();
							noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
							noteContext.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
							noteContext.put(FacilioConstants.ContextNames.NOTE, note);

							noteContext.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
							noteContext.put(FacilioConstants.ContextNames.ALARM_OCCURRENCE, lastOccurrence);

							addNote.addCommand(new UpdateWoIdInNewAlarmCommand());
							addNote.execute();
						}
					}
				}
				else {
					AlarmContext alarm = (AlarmContext) currentRecord;
					WorkOrderContext wo = getWorkOrder((AlarmContext) currentRecord);
					if (wo == null) {
						FacilioContext woContext = new FacilioContext();
						woContext.put(FacilioConstants.ContextNames.ALARM, alarm);
						woContext.put(FacilioConstants.ContextNames.RECORD, obj);

						FacilioChain addWorkOrder = TransactionChainFactory.getAddWoFromAlarmChain();
						addWorkOrder.execute(woContext);

					} else {
						fetchSeverities(alarm);
						NoteContext note = new NoteContext();
						note.setBody(getNewAlarmCommentForUnClosedWO(alarm));
						note.setParentId(wo.getId());
						note.setCreatedTime(alarm.getModifiedTime());

						FacilioContext noteContext = new FacilioContext();
						noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
						noteContext.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
						noteContext.put(FacilioConstants.ContextNames.NOTE, note);

						FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
						addNote.execute(noteContext);

						AlarmAPI.updateWoIdInAlarm(wo.getId(), alarm.getId());
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				LOGGER.error("Exception occurred during creating Workorder from Alarm", e);
			}
		}
	},
	CLOSE_WO_FROM_ALARM(12) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) throws Exception {
			// TODO Auto-generated method stub
			try {
				WorkOrderContext wo = null;
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
					long lastWOID = ((BaseAlarmContext) currentRecord).getLastWoId();
//					AlarmOccurrenceContext lastOccurrence = getAlarmOccurrenceFromAlarm((BaseAlarmContext) currentRecord);
					if (lastWOID > 0) {
						wo = WorkOrderAPI.getWorkOrder(lastWOID);
					}
				}
				else {
					wo = WorkOrderAPI.getWorkOrder(((AlarmContext) currentRecord).getId());
				}
				if (wo != null) {
					FacilioContext updateContext = new FacilioContext();
					updateContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CLOSE_WORK_ORDER);

					WorkOrderContext workorder = new WorkOrderContext();
					workorder.setStatus(TicketAPI.getStatus("Closed"));

					context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
					context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(wo.getId()));

					FacilioChain updateWorkOrder = TransactionChainFactory.getUpdateWorkOrderChain();
					updateWorkOrder.execute(context);
				}
			}
			catch (Exception e) {
				LOGGER.error("Exception occurred during closing Workorder from Alarm", e);
			}
		}

		@Override
		public boolean isTemplateNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

	},
	FIELD_CHANGE(13) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) throws Exception {
			// TODO Auto-generated method stub
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			WorkflowEventContext event = currentRule.getEvent();
			List<FacilioField> fields = new ArrayList<>();
			long currentTime = System.currentTimeMillis();
			for (Object key : obj.keySet()) {
				FacilioField field = modBean.getField((String) key, currentRule.getModule().getName());
				if (field != null) {
					Object val = obj.get(key);
					if (val != null) {
						switch (field.getDataTypeEnum()) {
							case LOOKUP:
								Object id = ((Map<String, Object>)val).get("id");
								if ( FacilioConstants.ContextNames.USERS.equals(((LookupField) field).getSpecialType()) && FacilioConstants.Criteria.LOGGED_IN_USER.equals(id)) {
									val = AccountUtil.getCurrentUser();
								}
								else {
									val = FieldUtil.getEmptyLookupVal((LookupField) field, FacilioUtil.parseLong(id));
								}
								obj.put(key, FieldUtil.getAsProperties(val));
								break;
							case DATE:
							case DATE_TIME:
								val = currentTime + FacilioUtil.parseLong(val);
								obj.put(key, val);	// setting newly updated value if any
								break;
							case ENUM:
								val = FacilioUtil.parseInt(val);
								obj.put(key, val);
								break;
							default:
								break;
						}
						fields.add(field);
						if (field.isDefault()) {
							BeanUtils.setProperty(currentRecord, field.getName(), val);
						}
						else {
							((ModuleBaseWithCustomFields) currentRecord).setDatum(field.getName(), val);
						}
					}
				}
			}

			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.fields(fields)
					.module(currentRule.getModule())
					.andCondition(CriteriaAPI.getIdCondition(((ModuleBaseWithCustomFields) currentRecord).getId(), currentRule.getModule()))
					;
			updateBuilder.updateViaMap(obj);

		}

	},
	CREATE_WORK_ORDER(14) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) throws Exception {
			addWorkOrder(obj, SourceType.WORKFLOW_RULE, null);
		}

	},
	CLEAR_ALARM(15,false) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {

			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
				ReadingRuleContext alarmTriggerRule = (ReadingRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_ALARM_TRIGGER_RULES);
				if (alarmTriggerRule.getOverPeriod() > 0 || alarmTriggerRule.getOccurences() > 0 || alarmTriggerRule.isConsecutive() || alarmTriggerRule.getThresholdTypeEnum() == ReadingRuleContext.ThresholdType.FLAPPING) {
					PreEventContext preEvent = ((ReadingRuleContext) alarmTriggerRule).constructPreClearEvent((ReadingContext) currentRecord, (ResourceContext) ((ReadingContext) currentRecord).getParent());
					preEvent.constructAndAddPreClearEvent(context);
				}
				else  {
					((ReadingRuleContext) currentRule).constructAndAddClearEvent(context, (ResourceContext) ((ReadingContext) currentRecord).getParent(), ((ReadingContext) currentRecord).getTtime());
				}
			}
			else {
				Map<Long, ReadingRuleAlarmMeta> alarmMetaMap = (Map<Long, ReadingRuleAlarmMeta>) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_META);
				if (alarmMetaMap == null) {
					alarmMetaMap = ((ReadingRuleContext) currentRule).getAlarmMetaMap();
				}
				long resourceId = ((ReadingRuleContext) currentRule).getReadingRuleTypeEnum() == ReadingRuleType.THRESHOLD_RULE ? ((ReadingContext) currentRecord).getParentId() : ((ReadingRuleContext) currentRule).getResourceId();
				ReadingRuleAlarmMeta alarmMeta = alarmMetaMap != null ? alarmMetaMap.get(resourceId) : null;

				if (alarmMeta != null) {
					AlarmContext alarm = AlarmAPI.getAlarm(alarmMeta.getAlarmId());
					long clearTime = ((ReadingRuleContext) currentRule).getReadingRuleTypeEnum() == ReadingRuleType.THRESHOLD_RULE ? ((ReadingContext) currentRecord).getTtime() : (long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
					if (alarm.getModifiedTime() == clearTime) {
						return;
					}
					ReadingContext reading = (ReadingContext) currentRecord;
					if (reading != null) {
						ReadingRuleAPI.addClearEvent(context, obj, (ReadingRuleContext) currentRule, reading.getId(), ((ReadingRuleContext) currentRule).getMetric(reading), clearTime, resourceId);
					} else {
						ReadingRuleAPI.addClearEvent(context, obj, (ReadingRuleContext) currentRule, -1, null, clearTime, resourceId);
					}
				}
			}
		}
	},
	FORMULA_FIELD_CHANGE(16) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) throws Exception {
			// TODO Auto-generated method stub
//			if (currentRule.getEvent() == null) {
//				currentRule.setEvent(WorkflowRuleAPI.getWorkflowEvent(currentRule.getEventId()));
//			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			WorkflowEventContext event = currentRule.getEvent();
			FacilioModule module = currentRule.getModule();

			if (AccountUtil.getCurrentOrg().getId() == 186) {
				LOGGER.info("Template JSON in FOrmula Field Change : "+obj.toJSONString());
			}

			boolean sameRecord = true;
			String moduleName = (String) obj.get("moduleName");
			if(StringUtils.isNotEmpty(moduleName)) {
				module = modBean.getModule(moduleName);
				sameRecord = false;
			}

			long id = -1l;
			Object parentId = obj.get("parentId");
			if(parentId != null) {
				id = (long) FacilioUtil.castOrParseValueAsPerType(FieldType.NUMBER, parentId);
				sameRecord = false;
			}
			else {
				id = ((ModuleBaseWithCustomFields) currentRecord).getId();
			}

			List<FacilioField> fields = new ArrayList<>();
			Map<String, Object> props = new HashMap<String, Object>();

			Map<String,Object> params = new HashMap<>();
			Map<String,Object> currentRecordJson = null;
			currentRecordJson = FieldUtil.getAsProperties(currentRecord);
			params.put("record", currentRecordJson);

			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext((Long)obj.get("resultWorkflowId"));

			Map<String,Object> workflowResult = WorkflowUtil.getExpressionResultMap(workflowContext, params);
			if (AccountUtil.getCurrentOrg().getId() == 186) {
				LOGGER.info("Workflow result in field change action : "+workflowResult);
			}
			JSONArray fieldsJsonArray = (JSONArray) obj.get("fields");
			for (Object key : fieldsJsonArray) {
				FacilioField field = modBean.getField((String) key, module.getName());
				if (field != null) {
					fields.add(field);
					Object val = workflowResult.get(field.getName());
					props.put(field.getName(), val);

					if (sameRecord) {
						if (field.isDefault()) {
							BeanUtils.setProperty(currentRecord, field.getName(), val);
						}
						else {
							((ModuleBaseWithCustomFields) currentRecord).setDatum(field.getName(), val);
						}
					}
				}
			}

			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.fields(fields)
					.module(module)
					.andCondition(CriteriaAPI.getIdCondition(id, module))
					;
			updateBuilder.updateViaMap(props);
		}

	},
	ALARM_IMPACT_ACTION(17) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) throws Exception {
			// TODO Auto-generated method stub
//			if (currentRule.getEvent() == null) {
//				currentRule.setEvent(WorkflowRuleAPI.getWorkflowEvent(currentRule.getEventId()));
//			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			WorkflowEventContext event = currentRule.getEvent();
			List<FacilioField> fields = new ArrayList<>();
			Map<String, Object> props = new HashMap<String, Object>();

			Map<String,Object> currentRecordJson = null;
			long alarmId = -1l;
			if(currentRecord instanceof ReadingAlarmContext) {
				currentRecordJson = FieldUtil.getAsProperties(currentRecord);
				if(currentRecordJson.get("resource") != null) {
					currentRecordJson.put("resourceId", ((Map)currentRecordJson.get("resource")).get("id"));
				}
				alarmId = (Long) currentRecordJson.get("id");
			}

			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext((Long)obj.get("resultWorkflowId"));
			workflowContext.setLogNeeded(true);
			Object val = WorkflowUtil.getWorkflowExpressionResult(workflowContext, currentRecordJson);

			JSONArray fieldsJsonArray = (JSONArray) obj.get("fields");
			for (Object key : fieldsJsonArray) {
				FacilioField field = modBean.getField((String) key, currentRule.getModule().getName());
				if (field != null) {

					fields.add(field);

					if(field.getName().equals(AlarmAPI.ALARM_COST_FIELD_NAME)) {

						Double currrentValue = null;
						if(val != null) {
							currrentValue = Double.parseDouble(val.toString());
						}

						AlarmContext alarm = AlarmAPI.getAlarm(alarmId);
						if(alarm != null) {
							Object previousValue = alarm.getDatum(field.getName());
							if(previousValue != null && currrentValue != null) {
								Double previousValueDouble = Double.parseDouble(previousValue.toString());

								currrentValue = previousValueDouble+currrentValue;
							}
						}
						props.put(field.getName(), currrentValue);
					}
					else {
						props.put(field.getName(), val);
					}
					if (field.isDefault()) {
						BeanUtils.setProperty(currentRecord, field.getName(), val);
					}
					else {
						((ModuleBaseWithCustomFields) currentRecord).setDatum(field.getName(), val);
					}
				}
			}

			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.fields(fields)
					.module(currentRule.getModule())
					.andCondition(CriteriaAPI.getIdCondition(((ModuleBaseWithCustomFields) currentRecord).getId(), currentRule.getModule()))
					;
			updateBuilder.updateViaMap(props);
		}

	},
	CONTROL_ACTION (18) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {
			LOGGER.info("Performing Control action : "+obj.toJSONString());
			String val = (String) obj.get("val");
			if(AccountUtil.getCurrentOrg().getId() == 104l) {
				long fieldId = FacilioUtil.parseLong(obj.get("metric"));
				long resourceId = FacilioUtil.parseLong(obj.get("resource"));
				TimeSeriesAPI.setControlValue(resourceId, fieldId, val);
			}
			else {
				Integer actionType = (Integer) obj.get("actionType");
				
				if(currentRule.getRuleTypeEnum() == RuleType.CONTROL_ACTION_SCHEDULED_RULE) {
					context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.SCHEDULE);
				}
				else if(currentRule.getRuleTypeEnum() == RuleType.CONTROL_ACTION_READING_ALARM_RULE) {
					context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.ALARM_CONDITION);
				}
				else if(currentRule.getRuleTypeEnum() == RuleType.RECORD_SPECIFIC_RULE) {
					context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.RESERVATION_CONDITION);
				}
				
				if(actionType != null && actionType.equals(ControlActionTemplate.ActionType.GROUP.getIntVal())) {
					
					Long controlActionGroupId = (Long) obj.get("controlActionGroupId");
					
					FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandForControlGroupChain();
					
					
					context.put(ControlActionUtil.CONTROL_ACTION_GROUP_ID, controlActionGroupId);
					context.put(ControlActionUtil.VALUE, val);
					
					executeControlActionCommandChain.execute(context);
				}
				else {
					long fieldId = FacilioUtil.parseLong(obj.get("metric"));
					long resourceId = FacilioUtil.parseLong(obj.get("resource"));
					
					ResourceContext resourceContext = new ResourceContext();
					if(resourceId > 0) {
						resourceContext.setId(resourceId);
					}
					else {
						if (currentRecord instanceof ReadingAlarmContext) {
							ReadingAlarmContext alarm = (ReadingAlarmContext) currentRecord;
							resourceContext = alarm.getResource();
						}
						else if (currentRecord instanceof ReadingAlarm) {
							// new alarm
							ReadingAlarm readingAlarm = (ReadingAlarm) currentRecord;
							resourceContext = readingAlarm.getResource();
						}
					}
					
					ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
					controlActionCommand.setResource(resourceContext);
					controlActionCommand.setFieldId(fieldId);
					controlActionCommand.setValue(val);
					
					context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, Collections.singletonList(controlActionCommand));
					
					FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandChain();
					executeControlActionCommandChain.execute(context);
				}
			}
		}
	},
	CHANGE_STATE (19) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) throws Exception {
			Object newState = obj.get("new_state");
			long newStateId = newState != null ? Long.parseLong(newState.toString()) : -1;

			String moduleName = (String) obj.get("moduleName");
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			

			FacilioStatus status = TicketAPI.getStatus(newStateId);
			changeState(status, module, context, currentRecord);
		}
	},
	ML_JOB_ACTION (20) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) throws Exception
		{
			FacilioTimer.scheduleOneTimeJobWithTimestampInSec(FacilioUtil.parseLong(context.get("jobid")), "DefaultMLJob", System.currentTimeMillis(), "ml");
		}

		@Override
		public boolean isTemplateNeeded()
		{
			return false;
		}

	},
	WORKFLOW_ACTION (21) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) throws Exception
		{

			try {
			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext((Long)obj.get("resultWorkflowId"));
			workflowContext.setLogNeeded(true);

			Map<String, Object> props = FieldUtil.getAsProperties(currentRecord);

			List<Object> currentRecordList = new ArrayList<>();
			currentRecordList.add(props);

			context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
			context.put(WorkflowV2Util.WORKFLOW_PARAMS, currentRecordList);

			FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();

			chain.execute(context);
			}
			catch (Exception e) {
				LOGGER.error("Exception occurred on workflow Action", e);
			}

		}

		@Override
		public boolean isTemplateNeeded()
		{
			return false;
		}

	},
	REPORT_DOWNTIME_ACTION (22) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) throws Exception
		{
			if (currentRecord != null) {
				if (currentRecord instanceof ReadingAlarmContext) {
					ReadingAlarmContext alarm = (ReadingAlarmContext) currentRecord;
					AssetBDSourceDetailsContext assetBreakdown = new AssetBDSourceDetailsContext();
					assetBreakdown.setCondition(alarm.getSubject());
					assetBreakdown.setFromtime(alarm.getCreatedTime());
					assetBreakdown.setTotime(alarm.getClearedTime());
					assetBreakdown.setAssetid(alarm.getResource().getId());
					assetBreakdown.setSourceId(alarm.getId());
					assetBreakdown.setSourceType(AssetBDSourceDetailsContext.SourceType.ALARM.getValue());
					context.put(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS, assetBreakdown);
					FacilioChain newAssetBreakdown = TransactionChainFactory.getAddAssetDowntimeChain();
					newAssetBreakdown.execute(context);
				}
				else if (currentRecord instanceof ReadingAlarm) {
					// new alarm
					ReadingAlarm readingAlarm = (ReadingAlarm) currentRecord;
					AlarmOccurrenceContext occurrence = ActionType.getAlarmOccurrenceFromAlarm(readingAlarm);
					AssetBDSourceDetailsContext assetBreakdown = new AssetBDSourceDetailsContext();
					assetBreakdown.setCondition(readingAlarm.getSubject());
					assetBreakdown.setFromtime(occurrence.getCreatedTime());
					assetBreakdown.setTotime(occurrence.getClearedTime());
					assetBreakdown.setAssetid(readingAlarm.getResource().getId());
					assetBreakdown.setSourceId(readingAlarm.getId());
					assetBreakdown.setSourceType(AssetBDSourceDetailsContext.SourceType.ALARM.getValue());
					context.put(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS, assetBreakdown);
					FacilioChain newAssetBreakdown = TransactionChainFactory.getAddAssetDowntimeChain();
					newAssetBreakdown.execute(context);
				}
			}
		}
	},
	ASSET_EXPIRE_ACTION (23) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule("asset");
			
			FacilioStatus expiredStatus = TicketAPI.getStatus(module, "Expired");
			changeState(expiredStatus, module, context, currentRecord);
		}
		
		@Override
		public boolean isTemplateNeeded() {
			return false;
		}
	},
	CREATE_DEVIATION_WORK_ORDER(24) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) throws Exception {
			try {
				TaskContext task = (TaskContext) currentRecord;
				long pmId = task.getParentWo().getPm().getId();
				String taskUniqueId = pmId + "_" + task.getParentWo().getResource().getId() +"_" + task.getUniqueId(); 
				WorkOrderContext deviationWo = WorkOrderAPI.getOpenWorkOrderForDeviationTemplate(taskUniqueId);
				if (deviationWo != null) {
					NoteContext note = new NoteContext();
					note.setBody("Task " + task.getSubject() + " has been closed with the value " + task.getInputValue());
					note.setParentId(deviationWo.getId());
					note.setCreatedTime(task.getParentWo().getActualWorkEnd());

					FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
					FacilioContext noteContext = addNote.getContext();
					noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
					noteContext.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
					noteContext.put(FacilioConstants.ContextNames.NOTE, note);
					addNote.execute();
				}
				else {
					WorkOrderContext wo = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
					wo.setDeviationTaskUniqueId(taskUniqueId);
					List<AttachmentContext> attachments = AttachmentsAPI.getAttachments("taskattachments", task.getId());
					addWorkOrder(wo, SourceType.TASK_DEVIATION, attachments);
				}
			}
			catch(Exception e) {
				LOGGER.error("Exception occurred on creating deviation workorders", e);
			}
		}

	},
	
	ADD_VIOLATION_ALARM (25) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
				Object currentRecord) throws Exception {
			try {
				
				obj.remove("alarmType");
				long forumalaId = (long)obj.remove("formulaId");
				
				BaseEventContext event = FieldUtil.getAsBeanFromJson(obj, ViolationEventContext.class);
				ReadingRuleContext rule = ((ReadingRuleContext)currentRule);
				
				
				event.setSeverityString("Minor");
				ViolationEventContext violationEvent = (ViolationEventContext)event;
				violationEvent.setFormulaFieldId(forumalaId);
				
				rule.addDefaultEventProps(event, obj, (ReadingContext) currentRecord);
				
				addAlarm(event, obj, context, currentRule, currentRecord);
				
				event = null;
			} catch (Exception e) {
				LOGGER.error("Exception occurred ", e);
			}
		}
		
	},
	WHATSAPP_MESSAGE(26) {		
		
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) {
			if (obj != null) {  //&& FacilioProperties.isProduction() add this on commit
				try {
					String to = (String) obj.get("to");
					
					if (to != null && !to.isEmpty()) {
						List<String> sms = new ArrayList<>();
						
						Boolean isHtmlContent = null;
						String html = null;
						
						if(obj.get("isHtmlContent") != null)
						{
							isHtmlContent =  Boolean.parseBoolean((String)obj.get("isHtmlContent"));
							html = (String) obj.get("message");
						}
											
						if(isHtmlContent != null && html != null && isHtmlContent)
						{						
							final String htmlContentString = "\'" + html+ "\'";							
							File file = PdfUtil.exportUrlAsFile("http://www.facilio.com", null, htmlContentString, null, FileFormat.IMAGE);
							
							if(file !=null )
							{
								String htmlContentPublicUrl = PublicFileUtil.createPublicFile(file, "twilioImage", "jpeg", "image/jpeg");
//								htmlContentPublicUrl = htmlContentPublicUrl.replace("http://localhost:8080/", "https://ee765fb6.ngrok.io/");
								obj.put("htmlContentPublicUrl", htmlContentPublicUrl);	
								obj.put("message", "");																						
							}							
						}
						
						WhatsappUtil.sendMessage(obj); 
						
						sms.add(to);
						if (context != null) {
							context.put(FacilioConstants.Workflow.NOTIFIED_SMS, sms);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("Exception occurred ", e);
					
				}
			}
		}
	},
	MAKE_CALL(27) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null) { //&& FacilioProperties.isProduction() add this on commit
				try {
					String to = (String) obj.get("to");
					if (to != null && !to.isEmpty()) {
						
						CallUtil.makeCall(obj);
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
		}
	},
	IMPACTS(28) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {
		// 	context.put("impact_value", val);
			try {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> fields = new ArrayList<>();
				Map<String, Object> props = new HashMap<String, Object>();

				Map<String,Object> currentRecordJson = null;
				long alarmId = -1l;
				if(currentRecord instanceof ReadingAlarmContext) {
					currentRecordJson = FieldUtil.getAsProperties(currentRecord);
					if(currentRecordJson.get("resource") != null) {
						currentRecordJson.put("resourceId", ((Map)currentRecordJson.get("resource")).get("id"));
					}
					alarmId = (Long) currentRecordJson.get("id");
				}
				else if(currentRecord instanceof ReadingContext) {
					currentRecordJson = FieldUtil.getAsProperties(currentRecord);
					 if(currentRecordJson.get("parent") != null) {
						currentRecordJson.put("resourceId", ((Map)currentRecordJson.get("parent")).get("id"));
					}
				}
	
				WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext((Long)obj.get("resultWorkflowId"));
				workflowContext.setLogNeeded(true);
				Object val = WorkflowUtil.getWorkflowExpressionResult(workflowContext, currentRecordJson);
				context.put("impact_value", val);
				LOGGER.info("Calculated impact value for currentRule: "+currentRule+" currentRecord : "+currentRecord+ " workflowContext:" +workflowContext+ " impactresultval "+val);
				// impactContext.get("impact_key");
			}
			catch (Exception e) {
				LOGGER.info("Error in impact value for currentRule: "+currentRule+" currentRecord : "+currentRecord);
				System.out.println(e);
			}
		}
		@Override
		public boolean isTemplateNeeded() {
			return false;
		}
	},
	CHAT_BOT_INTENT_RESPONSE (29) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) throws Exception
		{
				
			ChatBotIntent chatBotIntent = (ChatBotIntent)context.get(ChatBotConstants.CHAT_BOT_INTENT);
			
			String response = ChatBotConstants.getDefaultIntentResponse(chatBotIntent.getName());
			
			context.put(WorkflowV2Util.WORKFLOW_RESPONSE, Collections.singletonMap(ChatBotConstants.CHAT_BOT_WORKFLOW_RETURN_TEXT, response));
				
		}

		@Override
		public boolean isTemplateNeeded()
		{
			return false;
		}

	},
	
	WORKFLOW_ACTION_WITH_LIST_PARAMS(30) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,Object currentRecord) throws Exception
		{

			try {
			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext((Long)obj.get("resultWorkflowId"));
			workflowContext.setLogNeeded(true);

			List<Object> currentRecordList = (List<Object>)currentRecord;

			FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
			
			FacilioContext newContext = chain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
			newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, currentRecordList);

			chain.execute();
			
			context.put(WorkflowV2Util.WORKFLOW_RESPONSE, workflowContext.getReturnValue());
			}
			catch (Exception e) {
				LOGGER.error("Exception occurred on workflow Action", e);
			}

		}

		@Override
		public boolean isTemplateNeeded()
		{
			return false;
		}

	},
	ACTIVITY_FOR_MODULE_RECORD(31) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {
			try {
				if (currentRecord instanceof ModuleBaseWithCustomFields) {
					ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) currentRecord;

					FacilioChain chain = TransactionChainFactory.getAddActivitiesCommand();
					FacilioContext activityContext = chain.getContext();

					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(record.getModuleId());
					if (module == null) {
						throw new IllegalArgumentException("Invalid module");
					}

					long activityTypeInt = (long) obj.get("activityType");
					ActivityType activityType = ActivityType.getActivityType(((Long) activityTypeInt).intValue());
					if (activityType == null) {
						throw new IllegalArgumentException("Activity Type cannot be empty");
					}

					activityContext.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
					ActivityContext ac = new ActivityContext();
					ac.setTtime(DateTimeUtil.getCurrenTime());
					ac.setParentId(record.getId());
					ac.setType(activityType);
					JSONObject info = (JSONObject) obj.get("info");
					ac.setInfo(info);
					long orgId = AccountUtil.getCurrentOrg().getId();
					ac.setDoneBy(AccountUtil.getOrgBean().getSuperAdmin(orgId));

					activityContext.put(FacilioConstants.ContextNames.ACTIVITY_LIST, Collections.singletonList(ac));
					chain.execute();
				}
			}
			catch (Exception ex) {
				LOGGER.error("Exception occurred ", ex);
			}
		}
	},
	MAIL_TO_CREATEWO(32) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {
			LOGGER.info("MAIL_TO_CREATEWO === >"+ currentRecord);

			SupportEmailContext supportEmailContext = (SupportEmailContext) context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL);
			V3MailMessageContext mailContext = (V3MailMessageContext) currentRecord;
//
//			V3WorkOrderContext workorderContext = new V3WorkOrderContext();
//			workorderContext = FieldUtil.getAsBeanFromJson(obj, V3WorkOrderContext.class);
//			workorderContext.setSendForApproval(true);
//			User requester = new User();
//			requester.setEmail(mailContext.getFrom());
//			workorderContext.setSubject(mailContext.getSubject());
//			workorderContext.setSourceType(TicketContext.SourceType.EMAIL_REQUEST.getIntVal());
//			if (mailContext.getContent() != null) {
//				workorderContext.setDescription(StringUtils.trim(mailContext.getContent()));
//			}
//			workorderContext.setSiteId(supportEmailContext.getSiteId());
//			workorderContext.setRequester(requester);
//			Map<String, List<Map<String, Object>>> attachments = new HashMap<>();
//			if (mailContext.getAttachmentsList().size() > 0) {
//				attachments.put("ticketAttachments", mailContext.getAttachmentsList());
//				workorderContext.setSubForm(attachments);
//			}
//			addWorkOrder(workorderContext);
			WorkOrderContext workorderContext = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
			workorderContext.setSourceType(SourceType.EMAIL_REQUEST);
			workorderContext.setSiteId(supportEmailContext.getSiteId());
			if(workorderContext.getDescription().length() > 2000){
				workorderContext.setDescription(workorderContext.getDescription().substring(0, 2000));
			}
			List<File> attachedFiles = new ArrayList<>();
			List<String> attachedFilesFileName = new ArrayList<>();
			List<String> attachedFilesContentType = new ArrayList<>();
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			if (mailContext.getAttachmentsList().size() > 0) {
				List<AttachmentV3Context> attachments = V3AttachmentAPI.getAttachments(mailContext.getId(), FacilioConstants.ContextNames.MAIL_ATTACHMENT);
				for (AttachmentV3Context attachment :attachments) {
					FileStore fs = FacilioFactory.getFileStore();
					Long fileId = attachment.getAttachmentId();
					FileInfo fileInfo = fs.getFileInfo(fileId, true);
					InputStream downloadStream = fs.readFile(fileInfo);
					File file = File.createTempFile(attachment.getAttachmentFileName(), "");
					FileUtils.copyInputStreamToFile(downloadStream, file);
					attachedFiles.add(file);
					attachedFilesFileName.add(attachment.getAttachmentFileName());
					attachedFilesContentType.add(attachment.getAttachmentContentType());
				}
			}
			bean.addWorkOrderFromEmail(workorderContext, attachedFiles, attachedFilesFileName, attachedFilesContentType);
			LOGGER.info("Added Workorder from Action Type MAIL_TO_CREATEWO : "  );
		}
		@Override
		public boolean isTemplateNeeded()
		{
			return false;
		}
	} ,
	MAIL_TO_CUSTOM_MODULE_RECORD(33) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {
			long formId = (long) obj.get("formId");
			if (formId > -1) {
				FacilioForm form = FormsAPI.getFormFromDB(formId);
				String moduleName = form.getModule().getName();
				Class beanClassName = V3CustomModuleData.class;
				obj.put("sourceType", 2);
				ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromJson(obj, beanClassName);
				Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
				FacilioChain createRecordChain = ChainUtil.getCreateRecordChain(moduleName);
				FacilioContext createContext = createRecordChain.getContext();
				Constants.setRecordMap(createContext, recordMap);
				Constants.setModuleName(createContext, moduleName);
				FacilioModule module = ChainUtil.getModule(moduleName);
				handleFileAttachmentField(currentRecord, record, module, obj);
				recordMap.put(moduleName, Collections.singletonList(record));
				Class beanClass = FacilioConstants.ContextNames.getClassFromModule(module);
				createContext.put(Constants.BEAN_CLASS, beanClass);
				createRecordChain.execute();
			}
		}
	}
	
	;
	
	
	private static AlarmOccurrenceContext getAlarmOccurrenceFromAlarm(BaseAlarmContext baseAlarm) throws Exception {
		AlarmOccurrenceContext lastOccurrence = NewAlarmAPI.getAlarmOccurrence(baseAlarm.getLastOccurrenceId());
		baseAlarm.setLastOccurrence(lastOccurrence);
		return lastOccurrence;
	}

	private int val;

	private ActionType(int val) {
		this.val = val;
	}

	private ActionType(int val,boolean isTemplateNeeded) {
		this.val = val;
		this.isTemplateNeeded = isTemplateNeeded;
	}

	public int getVal() {
		return val;
	}
	boolean isTemplateNeeded = true;
	public boolean isTemplateNeeded() {
		return isTemplateNeeded;
	}

	abstract public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
									   Object currentRecord) throws Exception;

	public static ActionType getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}

	private static final Map<Integer, ActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

	private static final Logger LOGGER = LogManager.getLogger(ActionType.class.getName());

	private static Map<Integer, ActionType> initTypeMap() {
		Map<Integer, ActionType> typeMap = new HashMap<>();
		for (ActionType type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}

	public static void main(String arg[]) throws Exception {
		System.out.println("hello world");
		ActionType t = ActionType.SMS_NOTIFICATION;
		JSONObject json = new JSONObject();
		json.put("to", "+919840425388");
		json.put("message", "hello world");
		t.performAction(json, null, null, null);
	}
	
	private static boolean checkIfActiveUserFromEmail(String email) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		User user = userBean.getUserFromEmail(email, null, AccountUtil.getCurrentOrg().getOrgId());
		return (user == null || (user != null && user.getUserStatus()));
	}

	private static boolean checkIfActiveUserFromPhone(String phone) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		User user = userBean.getUserFromPhone(phone, null, AccountUtil.getCurrentOrg().getOrgId());
		return (user == null || (user != null && user.getUserStatus()));
	}

	private static boolean checkIfActiveUserFromId(long ouid) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		User user = userBean.getUser(ouid, false);
		return user != null && user.getUserStatus();
	}

	private static JSONArray getTo(String to) {
		if(to != null && !to.isEmpty()) {
			JSONArray toList = new JSONArray();
			if(to.contains(",")) {
				String[] tos = to.trim().split("\\s*,\\s*");
				for(String toAddr : tos) {
					toList.add(toAddr);
				}
			}
			else {
				toList.add(to);
			}
			return toList;
		}
		return null;
	}

	private static WorkOrderContext getWorkOrder(AlarmContext alarm) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioField entityIdField = modBean.getField("entityId", FacilioConstants.ContextNames.ALARM);
		FacilioField woIdField = modBean.getField("woId", FacilioConstants.ContextNames.ALARM);
		FacilioField statusField = modBean.getField("status", FacilioConstants.ContextNames.WORK_ORDER);
		FacilioStatus closeStatus = TicketAPI.getStatus("Closed");


		SelectRecordsBuilder<WorkOrderContext> woBuilder = new SelectRecordsBuilder<WorkOrderContext>()
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.beanClass(WorkOrderContext.class)
				.andCustomWhere(module.getTableName()+".ID IN (SELECT " + woIdField.getColumnName()  +" FROM Alarms WHERE ORGID = ? AND "+entityIdField.getCompleteColumnName()+" = ?)", AccountUtil.getCurrentOrg().getId(), alarm.getEntityId())
				.andCondition(CriteriaAPI.getCondition(statusField, String.valueOf(closeStatus.getId()), PickListOperators.ISN_T));
		;

		List<WorkOrderContext> wos = woBuilder.get();
		if (wos != null && !wos.isEmpty()) {
			return wos.get(0);
		}
		return null;
	}

	private static void fetchSeverities(AlarmContext alarm) throws Exception {
		List<Long> ids = new ArrayList<>();

		if (alarm.getPreviousSeverity() != null) {
			ids.add(alarm.getPreviousSeverity().getId());
		}

		ids.add(alarm.getSeverity().getId());
		LOGGER.info("Severities : "+ids);
		Map<Long, AlarmSeverityContext> severityMap = AlarmAPI.getAlarmSeverityMap(ids);

		if (alarm.getPreviousSeverity() != null) {
			alarm.setPreviousSeverity(severityMap.get(alarm.getPreviousSeverity().getId()));
		}
		alarm.setSeverity(severityMap.get(alarm.getSeverity().getId()));
	}

	private static String getNewV2AlarmCommentForUnClosedWO (BaseAlarmContext alarm) throws Exception {
		AlarmOccurrenceContext lastOccurrence = alarm.getLastOccurrence();
		AlarmSeverityContext alarmSeverity = AlarmAPI.getAlarmSeverity(lastOccurrence.getSeverity().getId());
		if (lastOccurrence.getPreviousSeverity() == null) {
			return "Alarm associated with this work order has been raised to "+alarmSeverity.getSeverity()+" at "+ DateTimeUtil.getFormattedTime(lastOccurrence.getLastOccurredTime());
		}
		else {
			AlarmSeverityContext previousSeverity = AlarmAPI.getAlarmSeverity(lastOccurrence.getPreviousSeverity().getId());
			return "Alarm associated with this work order updated from "+ previousSeverity.getSeverity() +" to "+alarmSeverity.getSeverity()+" at "+ DateTimeUtil.getFormattedTime(lastOccurrence.getLastOccurredTime());
		}
	}

	private static String getNewAlarmCommentForUnClosedWO (AlarmContext alarm) {
		if (alarm.getPreviousSeverity() == null) {
			return "Alarm associated with this work order has been raised to "+alarm.getSeverity().getSeverity()+" at "+alarm.getModifiedTimeString();
		}
		else {
			return "Alarm associated with this work order updated from "+alarm.getPreviousSeverity().getSeverity()+" to "+alarm.getSeverity().getSeverity()+" at "+alarm.getModifiedTimeString();
		}
	}
	
	private static void changeState(FacilioStatus status, FacilioModule module, Context context, Object currentRecord) throws Exception {
		ModuleBaseWithCustomFields moduleData = ((ModuleBaseWithCustomFields) currentRecord);

		if (status == null) {
			// Invalid status
			return;
		}
		if (status.getParentModuleId() != module.getModuleId()) {
			// Invalid status for current module
			return;
		}
		StateFlowRulesAPI.updateState(moduleData, module, status, false, context);
	}
	
	private static void addWorkOrder (JSONObject obj, SourceType sourceType, List<AttachmentContext> attachments) throws Exception {
		WorkOrderContext wo = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
		addWorkOrder(wo, sourceType, attachments);
	}
	
	private static void addWorkOrder (WorkOrderContext wo, SourceType sourceType, List<AttachmentContext> attachments) throws Exception {
		// TODO Auto-generated method stub

		LOGGER.debug("Action::Add Workorder::"+wo);
		
		wo.setSourceType(sourceType);
		FacilioContext woContext = new FacilioContext();
		woContext.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		woContext.put(FacilioConstants.ContextNames.TASK_MAP, wo.getTaskList());
		woContext.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, attachments);
		woContext.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
		woContext.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

		FacilioChain addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(woContext);
	}

	private static void addWorkOrder (V3WorkOrderContext v3WorkOrderContext) throws Exception {
		ModuleBaseWithCustomFields woContext = v3WorkOrderContext;
		Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
		FacilioChain createRecordChain = ChainUtil.getCreateRecordChain(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioContext createContext = createRecordChain.getContext();
		createContext.put(FacilioConstants.ContextNames.REQUESTER, v3WorkOrderContext.getRequester());
		createContext.put(FacilioConstants.ApprovalRule.APPROVAL_REQUESTER, v3WorkOrderContext.getRequester());
		createContext.put(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST, true);
		recordMap.put(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(woContext));
		Constants.setRecordMap(createContext, recordMap);
		Constants.setModuleName(createContext, FacilioConstants.ContextNames.WORK_ORDER);
		FacilioModule module = ChainUtil.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		Class beanClass = FacilioConstants.ContextNames.getClassFromModule(module);
		createContext.put(Constants.BEAN_CLASS, beanClass);
		createRecordChain.execute();
	}

	public static void addAlarm(BaseEventContext event, JSONObject obj, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {
		context.put(EventConstants.EventContextNames.EVENT_LIST, Collections.singletonList(event));
		Boolean isHistorical = (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT);
		if (isHistorical == null) {
			isHistorical = false;
		}

		if (!isHistorical) {
			FacilioChain addEvent = TransactionChainFactory.getV2AddEventChain(false);
			FacilioContext eventContext = addEvent.getContext();
			eventContext.put(EventConstants.EventContextNames.EVENT_LIST, context.get(EventConstants.EventContextNames.EVENT_LIST));
			eventContext.put(EventConstants.EventContextNames.EVENT_RULE_LIST, context.get(EventConstants.EventContextNames.EVENT_RULE_LIST));
			addEvent.execute();
			context.put("alarmOccurrenceMap", eventContext.get("alarmOccurrenceMap"));
			if(currentRule.getRuleTypeEnum() == RuleType.ALARM_TRIGGER_RULE) {
				Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap = (Map<String, PointedList<AlarmOccurrenceContext>>)context.get("alarmOccurrenceMap");
				AlarmOccurrenceContext alarmOccuranceContext = alarmOccurrenceMap.entrySet().iterator().next().getValue().get(0);
				context.put(FacilioConstants.ContextNames.READING_RULE_ALARM_OCCURANCE, alarmOccuranceContext);
			}
			
		} else {
			processNewAlarmMeta((ReadingRuleContext) currentRule, (ResourceContext) ((ReadingContext) currentRecord).getParent(), ((ReadingContext) currentRecord).getTtime(), event, context);
		}
	}
	
	//Assuming readings will come in ascending order of time and this is needed only for historical
	private static void processNewAlarmMeta (ReadingRuleContext rule, ResourceContext resource, long time, BaseEventContext event, Context context) throws Exception {
		Map<Long, ReadingRuleAlarmMeta> metaMap = (Map<Long, ReadingRuleAlarmMeta>) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_META);
		ReadingRuleAlarmMeta alarmMeta = metaMap.get(resource.getId());
		if (alarmMeta == null) {
			metaMap.put(resource.getId(), addAlarmMeta(event.getAlarmOccurrence(), resource, rule));
		} else if (alarmMeta.isClear()) {
			alarmMeta.setClear(false);
		}
	}

	private static ReadingRuleAlarmMeta addAlarmMeta (AlarmOccurrenceContext alarmOccurence, ResourceContext resource, ReadingRuleContext rule) throws Exception {
		return ReadingRuleAPI.constructNewAlarmMeta(-1, resource, rule, false, StringUtils.EMPTY);
	}

	private static Map<String, Object> parseFileObject (AttachmentV3Context attachment, String fileFieldName) throws Exception {

		FileStore fs = FacilioFactory.getFileStore();
		Long fileId = attachment.getAttachmentId();
		FileInfo fileInfo = null;
		InputStream downloadStream = null;
		fileInfo = fs.getFileInfo(fileId, true);
		downloadStream = fs.readFile(fileInfo);
		File file = File.createTempFile(attachment.getAttachmentFileName(), "");
		FileUtils.copyInputStreamToFile(downloadStream, file);
		Map<String, Object> fileObject = new HashMap<>();
		fileObject.put("createdTime", System.currentTimeMillis());
		fileObject.put(fileFieldName+"FileName", attachment.getAttachmentFileName());
		fileObject.put(fileFieldName+"ContentType", attachment.getAttachmentContentType());
		fileObject.put(fileFieldName, file);

		return fileObject;

	}

	private static void handleFileAttachmentField(Object currentRecord, ModuleBaseWithCustomFields record, FacilioModule module , JSONObject obj) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (currentRecord instanceof V3MailMessageContext) {
			V3MailMessageContext mailContext = (V3MailMessageContext) currentRecord;
			List<AttachmentV3Context> attachments = V3AttachmentAPI.getAttachments(mailContext.getId(), FacilioConstants.ContextNames.MAIL_ATTACHMENT);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			if (mailContext.getAttachmentsList() != null && mailContext.getAttachmentsList().size() > 0) {
				addFileFields(attachments, obj, fields, record);
			}
			if (attachments != null && attachments.size() > 0) {
				List<FacilioModule> attachmentModules = modBean.getSubModules(module.getModuleId(), FacilioModule.ModuleType.ATTACHMENTS);
				if (attachmentModules != null && attachmentModules.size() > 0) {
					FacilioModule attachmentModule = attachmentModules.get(0);
					Map<String, List<Map<String, Object>>> attachmentMap = new HashMap<>();
					List<Map<String, Object>> attachmentList = new ArrayList<>();
					for (AttachmentV3Context attachmentV3Context :attachments) {
						attachmentList.add(parseFileObject(attachmentV3Context, "attachment"));
					}
					attachmentMap.put(attachmentModule.getName(), attachmentList);
					record.setSubForm(attachmentMap);
				}
			}
		}
	}
	private static void addFileFields(List<AttachmentV3Context> attachments,JSONObject obj,List<FacilioField> fields,ModuleBaseWithCustomFields record) throws Exception {
		List<FileField> fileFields = new ArrayList<>();
		Iterator<String> keysItr = obj.keySet().iterator();
		while(keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = obj.get(key);
			if (value != null) {
				if (value.equals(FacilioConstants.ContextNames.MAIL_ATTACHMENT)) {
					for (FacilioField f : fields) {
						if (f.getName().equals(key)) {
							if (f instanceof FileField) {
								FileField fileField = (FileField) f;
								fileFields.add(fileField);
								if (fileField.getFormatEnum() != null) {
									Boolean isAdded = false;
									// check if attachment type matches to file file type
									for (int i = attachments.size() - 1; i >= 0; i--) {
										AttachmentV3Context attachment = attachments.get(i);
										if (attachment.getAttachmentContentType().contains(fileField.getFormatEnum().getStringVal())) {
											record.addData(parseFileObject(attachment, f.getName()));
											attachments.remove(i);
											isAdded = true;
											break;
										}
									}
									if (!isAdded) {
										record.getData().remove(key);
									}
								} else {
									AttachmentV3Context attachment = attachments.get(0);
									record.addData(parseFileObject(attachment, f.getName()));
									attachments.remove(0);
									break;
								}

							}
						}
					}
				}
			}
		}
	}
}