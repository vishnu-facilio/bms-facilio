package com.facilio.bmsconsole.workflow.rule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.UserUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.ControlActionTemplate;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.SMSUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ReadingRuleType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand.PointedList;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.fw.BeanFactory;
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
						List<String> emails = new ArrayList<>();
						AwsUtil.sendEmail(obj);

						emails.add(to);
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
	SMS_NOTIFICATION(2) {
		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) {
			// TODO Auto-generated method stub
			if (obj != null && AwsUtil.isProduction()) {
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
						for (Object toEmail : toEmails) {
							String to = (String) toEmail;
							if (to != null && !to.isEmpty() && checkIfActiveUserFromEmail(to)) {
								obj.put("to", to);

								if (AccountUtil.getCurrentOrg().getId() == 104) {
									LOGGER.info("Gonna Email : "+obj.toJSONString());
								}

								AwsUtil.sendEmail(obj);
								emails.add(to);
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
			if (obj != null && AwsUtil.isProduction()) {
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
					
					BaseEventContext event = ((ReadingRuleContext) currentRule).constructEvent(obj, (ReadingContext) currentRecord,context);

					context.put(EventConstants.EventContextNames.EVENT_LIST, Collections.singletonList(event));
					Boolean isHistorical = (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT);
					if (isHistorical == null) {
						isHistorical = false;
					}

					if (!isHistorical) {
						Chain addEvent = TransactionChainFactory.getV2AddEventChain();
						addEvent.execute(context);
						if(currentRule.getRuleTypeEnum() == RuleType.ALARM_TRIGGER_RULE) {
							Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap = (Map<String, PointedList<AlarmOccurrenceContext>>)context.get("alarmOccurrenceMap");
							AlarmOccurrenceContext alarmOccuranceContext = alarmOccurrenceMap.entrySet().iterator().next().getValue().get(0);
							context.put(FacilioConstants.ContextNames.READING_RULE_ALARM_OCCURANCE, alarmOccuranceContext);
						}
						
					} else {
						processNewAlarmMeta((ReadingRuleContext) currentRule, (ResourceContext) ((ReadingContext) currentRecord).getParent(), ((ReadingContext) currentRecord).getTtime(), event, context);
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
						Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
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


		//Assuming readings will come in ascending order of time and this is needed only for historical
		private void processNewAlarmMeta (ReadingRuleContext rule, ResourceContext resource, long time, BaseEventContext event, Context context) throws Exception {
			Map<Long, ReadingRuleAlarmMeta> metaMap = (Map<Long, ReadingRuleAlarmMeta>) context.get(FacilioConstants.ContextNames.READING_RULE_ALARM_META);
			ReadingRuleAlarmMeta alarmMeta = metaMap.get(resource.getId());
			if (alarmMeta == null) {
				metaMap.put(resource.getId(), addAlarmMeta(event.getAlarmOccurrence(), resource, rule));
			} else if (alarmMeta.isClear()) {
				alarmMeta.setClear(false);
			}
		}

		private ReadingRuleAlarmMeta addAlarmMeta (AlarmOccurrenceContext alarmOccurence, ResourceContext resource, ReadingRuleContext rule) throws Exception {
			return ReadingRuleAPI.constructNewAlarmMeta(-1, resource, rule, false);
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
				if (obj != null && AwsUtil.isProduction()) {
					String ids = (String) obj.get("id");

					if (!StringUtils.isEmpty(ids)) {
						List<Pair<String, Boolean>> mobileInstanceSettings = getMobileInstanceIDs(ids);
						LOGGER.info("Sending push notifications for ids : "+ids);
						if (mobileInstanceSettings != null && !mobileInstanceSettings.isEmpty()) {
							LOGGER.info("Sending push notifications for mobileIds : "+mobileInstanceSettings.stream().map(pair -> pair.getLeft()).collect(Collectors.toList()));
							for (Pair<String, Boolean> mobileInstanceSetting : mobileInstanceSettings) {
								if (mobileInstanceSetting != null) {
									// content.put("to",
									// "exA12zxrItk:APA91bFzIR6XWcacYh24RgnTwtsyBDGa5oCs5DVM9h3AyBRk7GoWPmlZ51RLv4DxPt2Dq2J4HDTRxW6_j-RfxwAVl9RT9uf9-d9SzQchMO5DHCbJs7fLauLIuwA5XueDuk7p5P7k9PfV");
									obj.put("to", mobileInstanceSetting.getLeft());

									Map<String, String> headers = new HashMap<>();
									headers.put("Content-Type", "application/json");
									headers.put("Authorization", "key="+ (mobileInstanceSetting.getRight() ? AwsUtil.getPortalPushNotificationKey() : AwsUtil.getPushNotificationKey()));

									String url = "https://fcm.googleapis.com/fcm/send";

									AwsUtil.doHttpPost(url, headers, null, obj.toJSONString());
//									System.out.println("Push notification sent");
//									System.out.println(obj.toJSONString());
								}
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Exception occurred ", e);
			}
		}

		private List<Pair<String, Boolean>> getMobileInstanceIDs(String idList) throws Exception {
			List<Pair<String, Boolean>> mobileInstanceIds = new ArrayList<>();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField email = new FacilioField();
			email.setName("email");
			email.setColumnName("EMAIL");
			email.setDataType(FieldType.STRING);
			email.setModule(ModuleFactory.getUserModule());
			fields.add(email);

			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(AccountConstants.getUserMobileSettingFields());

			fields.add(fieldMap.get("mobileInstanceId"));
			fields.add(fieldMap.get("fromPortal"));

			// Condition condition = CriteriaAPI.getCondition("EMAIL", "email",
			// StringUtils.join(emails, ","), StringOperators.IS);

			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("ORG_Users")
					.innerJoin("User_Mobile_Setting")
					.on("ORG_Users.USERID = User_Mobile_Setting.USERID")
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(AccountConstants.getOrgUserModule()))
					.andCondition(CriteriaAPI.getCondition("ORG_Users.ORG_USERID", "ouid", idList, NumberOperators.EQUALS))
					.orderBy("USER_MOBILE_SETTING_ID");

			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				UserUtil.setIAMUserProps(props, AccountUtil.getCurrentOrg().getOrgId(), false);
				for (Map<String, Object> prop : props) {
					Boolean fromPortal = (Boolean)prop.get("fromPortal");
					if (fromPortal == null) {
						fromPortal = false;
					}
					Boolean userStatus = (Boolean)prop.get("userStatus");
					if(userStatus != null && userStatus) {
						mobileInstanceIds.add(Pair.of((String) prop.get("mobileInstanceId"), fromPortal));
					}
					// emailToMobileId.put((String) prop.get("email"), (String)
					// prop.get("mobileInstanceId"));
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
					Chain executePm = TransactionChainFactory.getNewExecutePreventiveMaintenanceChain();
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
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
					UpdateRecordBuilder<WorkOrderContext> updateBuilder = new UpdateRecordBuilder<WorkOrderContext>()
							.module(woModule).fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
							.andCondition(CriteriaAPI.getIdCondition(workOrder.getId(), woModule));
					updateBuilder.update(updateWO);
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
					AlarmOccurrenceContext lastOccurrence = getAlarmOccurrenceFromAlarm((BaseAlarmContext) currentRecord);
					if (lastOccurrence != null) {
						WorkOrderContext workOrder = WorkOrderAPI.getWorkOrder(lastOccurrence.getWoId());
						if (workOrder == null) {
							FacilioContext woContext = new FacilioContext();
							woContext.put(FacilioConstants.ContextNames.RECORD_ID, lastOccurrence.getId());
							Chain c = TransactionChainFactory.getV2AlarmOccurrenceCreateWO();
							c.execute(woContext);
						}
						else {
							NoteContext note = new NoteContext();
							note.setBody(getNewV2AlarmCommentForUnClosedWO((BaseAlarmContext) currentRecord));
							note.setParentId(workOrder.getId());
							note.setCreatedTime(lastOccurrence.getLastOccurredTime());

							FacilioContext noteContext = new FacilioContext();
							noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
							noteContext.put(FacilioConstants.ContextNames.TICKET_MODULE, FacilioConstants.ContextNames.WORK_ORDER);
							noteContext.put(FacilioConstants.ContextNames.NOTE, note);

							Chain addNote = TransactionChainFactory.getAddNotesChain();
							addNote.execute(noteContext);
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

						Chain addWorkOrder = TransactionChainFactory.getAddWoFromAlarmChain();
						addWorkOrder.execute(woContext);

					} else {
						fetchSeverities(alarm);
						NoteContext note = new NoteContext();
						note.setBody(getNewAlarmCommentForUnClosedWO(alarm));
						note.setParentId(wo.getId());
						note.setCreatedTime(alarm.getModifiedTime());

						FacilioContext noteContext = new FacilioContext();
						noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
						noteContext.put(FacilioConstants.ContextNames.TICKET_MODULE, FacilioConstants.ContextNames.WORK_ORDER);
						noteContext.put(FacilioConstants.ContextNames.NOTE, note);

						Chain addNote = TransactionChainFactory.getAddNotesChain();
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
					AlarmOccurrenceContext lastOccurrence = getAlarmOccurrenceFromAlarm((BaseAlarmContext) currentRecord);
					if (lastOccurrence != null) {
						wo = WorkOrderAPI.getWorkOrder(lastOccurrence.getWoId());
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

					Chain updateWorkOrder = TransactionChainFactory.getUpdateWorkOrderChain();
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
			if (currentRule.getEvent() == null) {
				currentRule.setEvent(WorkflowRuleAPI.getWorkflowEvent(currentRule.getEventId()));
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			WorkflowEventContext event = currentRule.getEvent();
			List<FacilioField> fields = new ArrayList<>();
			long currentTime = System.currentTimeMillis();
			for (Object key : obj.keySet()) {
				FacilioField field = modBean.getField((String) key, event.getModule().getName());
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
					.module(event.getModule())
					.andCondition(CriteriaAPI.getIdCondition(((ModuleBaseWithCustomFields) currentRecord).getId(), event.getModule()))
					;
			updateBuilder.updateViaMap(obj);

		}

	},
	CREATE_WORK_ORDER(14) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule,
								  Object currentRecord) throws Exception {
			// TODO Auto-generated method stub

			LOGGER.info("Action::Add Workorder::"+obj);

			WorkOrderContext wo = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
			wo.setSourceType(SourceType.WORKFLOW_RULE);
			FacilioContext woContext = new FacilioContext();
			woContext.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
			woContext.put(FacilioConstants.ContextNames.TASK_MAP, wo.getTaskList());

			Chain addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
			addWorkOrder.execute(woContext);

		}

	},
	CLEAR_ALARM(15,false) {

		@Override
		public void performAction(JSONObject obj, Context context, WorkflowRuleContext currentRule, Object currentRecord) throws Exception {
			if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
				((ReadingRuleContext) currentRule).constructAndAddClearEvent(context, (ResourceContext) ((ReadingContext) currentRecord).getParent(), ((ReadingContext) currentRecord).getTtime());
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
			if (currentRule.getEvent() == null) {
				currentRule.setEvent(WorkflowRuleAPI.getWorkflowEvent(currentRule.getEventId()));
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			WorkflowEventContext event = currentRule.getEvent();
			FacilioModule module = event.getModule();

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
				id = (long) FieldUtil.castOrParseValueAsPerType(FieldType.NUMBER, parentId);
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
			if (currentRule.getEvent() == null) {
				currentRule.setEvent(WorkflowRuleAPI.getWorkflowEvent(currentRule.getEventId()));
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			WorkflowEventContext event = currentRule.getEvent();
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
				FacilioField field = modBean.getField((String) key, event.getModule().getName());
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
					.module(event.getModule())
					.andCondition(CriteriaAPI.getIdCondition(((ModuleBaseWithCustomFields) currentRecord).getId(), event.getModule()))
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
					
					Chain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandForControlGroupChain();
					
					
					context.put(ControlActionUtil.CONTROL_ACTION_GROUP_ID, controlActionGroupId);
					context.put(ControlActionUtil.VALUE, val);
					
					executeControlActionCommandChain.execute(context);
				}
				else {
					long fieldId = FacilioUtil.parseLong(obj.get("metric"));
					long resourceId = FacilioUtil.parseLong(obj.get("resource"));
					
					ResourceContext resourceContext = new ResourceContext();
					resourceContext.setId(resourceId);
					
					ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
					controlActionCommand.setResource(resourceContext);
					controlActionCommand.setFieldId(fieldId);
					controlActionCommand.setValue(val);
					
					context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, Collections.singletonList(controlActionCommand));
					
					Chain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandChain();
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
			FacilioTimer.scheduleOneTimeJob(FacilioUtil.parseLong(context.get("jobid")), "DefaultMLJob", System.currentTimeMillis(), "ml");
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

			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext((Long)obj.get("resultWorkflowId"));
			workflowContext.setLogNeeded(true);

			Map<String, Object> props = FieldUtil.getAsProperties(currentRecord);

			List<Object> currentRecordList = new ArrayList<>();
			currentRecordList.add(props);

			context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
			context.put(WorkflowV2Util.WORKFLOW_PARAMS, currentRecordList);

			Chain chain = TransactionChainFactory.getExecuteWorkflowChain();

			chain.execute(context);

//			Map<String,Object> currentRecordMap = new HashMap<>();
//			
//			currentRecordMap.put("record", currentRecord);
//			
//			WorkflowUtil.getWorkflowExpressionResult(workflowContext, currentRecordMap);
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
					Chain newAssetBreakdown = TransactionChainFactory.getAddAssetDowntimeChain();
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
					Chain newAssetBreakdown = TransactionChainFactory.getAddAssetDowntimeChain();
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
		User user = userBean.getUser(email);
		return user != null && user.getUserStatus();
	}

	private static boolean checkIfActiveUserFromPhone(String phone) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		User user = userBean.getUserFromPhone(phone);
		return user != null && user.getUserStatus();
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

	private static String getNewV2AlarmCommentForUnClosedWO (BaseAlarmContext alarm) {
		AlarmOccurrenceContext lastOccurrence = alarm.getLastOccurrence();
		if (lastOccurrence.getPreviousSeverity() == null) {
			return "Alarm associated with this work order has been raised to "+lastOccurrence.getSeverity().getSeverity()+" at "+ DateTimeUtil.getFormattedTime(lastOccurrence.getLastOccurredTime());
		}
		else {
			return "Alarm associated with this work order updated from "+lastOccurrence.getPreviousSeverity().getSeverity()+" to "+lastOccurrence.getSeverity().getSeverity()+" at "+ DateTimeUtil.getFormattedTime(lastOccurrence.getLastOccurredTime());
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
}