package com.facilio.alarms.sensor.util;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.alarms.sensor.context.SensorRuleAlarmMeta;
import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.alarms.sensor.sensorrules.SensorRuleTypeValidationInterface;
import com.facilio.alarms.sensor.context.sensoralarm.SensorAlarmContext;
import com.facilio.alarms.sensor.context.sensoralarm.SensorEventContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpEventContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;

public class SensorRuleUtil {

	private static final Logger LOGGER = Logger.getLogger(SensorRuleUtil.class.getName());

	//TODO To be removed after Energy fields are moved to counter fields behaviour
	private static final List<String> SPL_ENERGY_COUNTER_FIELDS = Arrays.asList(new String[]{"totalEnergyConsumption", "phaseEnergyR", "phaseEnergyY", "phaseEnergyB"});

	public static boolean isCounterField(NumberField field) {
		return field.isCounterField() ||
				(
						field.getModule() != null
								&& FacilioConstants.ContextNames.ENERGY_DATA_READING.equals(field.getModule().getName())
								&& SPL_ENERGY_COUNTER_FIELDS.contains(field.getName())
				);
	}

	public static List<SensorRuleContext> getSensorRuleByIds(List<Long> ruleIds) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getSensorRuleFields())
			.table(ModuleFactory.getSensorRuleModule().getTableName())
			.andCondition(CriteriaAPI.getIdCondition(ruleIds, ModuleFactory.getSensorRuleModule()));
							
		List<Map<String, Object>> props = selectBuilder.get();
		List<SensorRuleContext> sensorRuleList = getSensorRuleFromProps(props, true, false);
		if (sensorRuleList != null && !sensorRuleList.isEmpty()) {
			for (SensorRuleContext sensorRule : sensorRuleList) {
				setMatchedResourcesIds(sensorRule);
			}
		}
		return sensorRuleList;
	}

	private static void setMatchedResourcesIds(SensorRuleContext sensorRule) throws Exception {
		List<AssetContext> categoryAssets = AssetsAPI.getAssetListOfCategory(sensorRule.getAssetCategoryId());
		List<Long> assetIds = categoryAssets.stream().map(asset -> asset.getId()).collect(Collectors.toList());
//		sensorRule.setMatchedResourceIds(assetIds);
	}

	public static List<SensorRuleContext> fetchSensorRulesByModule(String moduleName, boolean isFetchSubProps, boolean isHistorical) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule readingModule = modBean.getModule(moduleName);
		FacilioModule categoryModule = modBean.getParentModule(readingModule.getModuleId());

		if (categoryModule != null) {
			List<SensorRuleContext> sensorRules = SensorRuleUtil.getSensorRuleByModuleId(categoryModule, isFetchSubProps, isHistorical);
			return sensorRules;
		}
		return null;
	}

	public static List<SensorRuleContext> getSensorRuleByModuleId(FacilioModule childModule, boolean isFetchSubProps, boolean isHistorical) throws Exception {

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRuleFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSensorRuleFields())
				.table(ModuleFactory.getSensorRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), "true", BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), childModule.getExtendedModuleIds(), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		return getSensorRuleFromProps(props, isFetchSubProps, isHistorical);
	}

	public static List<SensorRuleContext> getSensorRuleByCategoryId(long assetCategoryId, List<Long> readingFieldIds, boolean isFetchSubProps, boolean isHistorical) throws Exception {

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRuleFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSensorRuleFields())
				.table(ModuleFactory.getSensorRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), "true", BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetCategoryId"), "" + assetCategoryId, NumberOperators.EQUALS));

		if (readingFieldIds != null && !readingFieldIds.isEmpty()) {
			selectBuilder
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingFieldId"), readingFieldIds, NumberOperators.EQUALS));
		}

		List<Map<String, Object>> props = selectBuilder.get();
		return getSensorRuleFromProps(props, isFetchSubProps, isHistorical);
	}

	public static List<SensorRuleContext> getSensorRuleFromProps(List<Map<String, Object>> props, boolean isFetchSubProps, boolean isHistorical) throws Exception {

		if (props != null && !props.isEmpty()) {

			List<SensorRuleContext> sensorRules = FieldUtil.getAsBeanListFromMapList(props, SensorRuleContext.class);

			if (isFetchSubProps) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<Long> fieldIds = new ArrayList<Long>();
				List<Long> assetCategories = new ArrayList<Long>();

				for (SensorRuleContext sensorRule : sensorRules) {
					fieldIds.add(sensorRule.getSensorFieldId());
					assetCategories.add(sensorRule.getAssetCategoryId());
				}

				List<FacilioField> fields = modBean.getFields(fieldIds);
				Map<Long, FacilioField> fieldMap = FieldFactory.getAsIdMap(fields);

				for (SensorRuleContext sensorRule : sensorRules) {
					sensorRule.setSensorField(fieldMap.get(sensorRule.getSensorFieldId()));
					sensorRule.setSensorModule(modBean.getModule(sensorRule.getSensorModuleId())); //category will be the rule module
					if(!isHistorical) {
					//	fetchAlarmMeta(sensorRule);
					}
				}
			}
			return sensorRules;
		}
		return null;
	}

//	public static List<BaseEventContext> executeSensorRules(List<SensorRuleContext> sensorRules, List<ReadingContext> readings, boolean isHistorical, List<SensorRollUpEventContext> sensorMeterRollUpEvents) throws Exception {
//		if (sensorRules != null && !sensorRules.isEmpty()) {
//			List<Long> sensorRuleIds = sensorRules.stream().map(sensorRule -> sensorRule.getId()).collect(Collectors.toList());
//			HashMap<Long, JSONObject> sensorRuleValidatorPropsMap = SensorRuleUtil.getSensorRuleValidatorPropsByParentRuleIds(sensorRuleIds);
//
//			LinkedHashMap<Long, List<ReadingContext>> assetReadingsMap = groupReadingsByResourceId(readings);
//			LinkedHashMap<Long, List<SensorRuleContext>> fieldSensorRulesMap = groupSensorRulesByFieldId(sensorRules);
//			LinkedHashMap<String, SensorRollUpEventContext> fieldSensorRollUpEventMeta = new LinkedHashMap<String, SensorRollUpEventContext>();
//			LinkedHashMap<Long, SensorRollUpEventContext> assetSensorRollUpEventMeta = new LinkedHashMap<Long, SensorRollUpEventContext>();
//
//			LinkedHashMap<String, List<ReadingContext>> historicalReadingsMap = new LinkedHashMap<String, List<ReadingContext>>();
//			LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap = null;
//			if (isHistorical) {
//				constructHistoryReadingsMap(readings, sensorRules, historicalReadingsMap);
//				completeHistoricalReadingsMap = new LinkedHashMap<String, List<ReadingContext>>();
//			} else {
//				SensorRollUpUtil.fetchSensorRollUpAlarmMeta(assetReadingsMap.keySet(), fieldSensorRollUpEventMeta, assetSensorRollUpEventMeta);
//			}
//
//			List<SensorEventContext> sensorEvents = new ArrayList<SensorEventContext>();
//			List<SensorRollUpEventContext> sensorFieldRollUpEvents = new ArrayList<SensorRollUpEventContext>();
//
//			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////				LOGGER.info("fieldSensorRulesMap: "+ fieldSensorRulesMap+" assetReadingsMap "+assetReadingsMap);
//			}
//
//			for (ReadingContext reading : readings) {
//				if (reading.getSourceTypeEnum() == SourceType.SYSTEM) {
//					continue;
//				}
//				boolean isFirstAssetSensorRule = true;
//				for (Long readingFieldId : fieldSensorRulesMap.keySet()) {
//					boolean isFirstFieldSensorRule = true;
//					List<SensorRuleContext> fieldSensorRules = fieldSensorRulesMap.get(readingFieldId);
//
//					for (SensorRuleContext sensorRule : fieldSensorRules) {
//						List<ReadingContext> historicalReadings = new ArrayList<ReadingContext>();
//						historicalReadings = (isHistorical && !sensorRule.getSensorRuleTypeEnum().isCurrentValueDependent()) ? historicalReadingsMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), sensorRule.getReadingField())) : null;
//
//						if (reading.getReading(sensorRule.getSensorField().getName()) != null) {
//							SensorRuleTypeValidationInterface validatorType = sensorRule.getSensorRuleTypeEnum().getSensorRuleValidationType();
//							boolean result = validatorType.evaluateSensorRule(sensorRule, reading, sensorRuleValidatorPropsMap.get(sensorRule.getId()), isHistorical, historicalReadings, completeHistoricalReadingsMap);
//							JSONObject defaultSeverityProps = validatorType.getDefaultSeverityAndSubject();
//							checkDefaultSeverityProps(defaultSeverityProps, sensorRuleValidatorPropsMap.get(sensorRule.getId()));
//
//							if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////								LOGGER.info("reading: "+ reading+" sensorRule "+sensorRule+ " result: "+result);
//							}
//
//							if (result) {
////								SensorEventContext sensorEvent = sensorRule.constructEvent(reading, defaultSeverityProps, isHistorical);
////								sensorEvents.add(sensorEvent);
//
//								if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////									LOGGER.info("reading: "+ reading+" sensorRule "+sensorRule+ " sensorEvent: "+sensorEvent);
//								}
//
//								if (isFirstFieldSensorRule && !validatorType.getSensorRuleTypeFromValidator().isMeterRollUp()) {
////									SensorRollUpEventContext sensorFieldRollUpEvent = sensorEvent.constructRollUpEvent(reading, defaultSeverityProps, isHistorical, false, sensorRule);
////									sensorFieldRollUpEvents.add(sensorFieldRollUpEvent);
////									fieldSensorRollUpEventMeta.put(ReadingsAPI.getRDMKey(reading.getParentId(), sensorFieldRollUpEvent.getReadingField()), sensorFieldRollUpEvent);
//
//									if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////										LOGGER.info("reading: "+ reading+" sensorRule "+sensorRule+ " sensorFieldRollUpEvent: "+sensorFieldRollUpEvent);
//									}
//									isFirstFieldSensorRule = false;
//								}
//
//								if (isFirstAssetSensorRule && validatorType.getSensorRuleTypeFromValidator().isMeterRollUp()) {
////									SensorRollUpEventContext sensorAssetRollUpEvent = sensorEvent.constructRollUpEvent(reading, defaultSeverityProps, isHistorical, true, sensorRule);
////									sensorMeterRollUpEvents.add(sensorAssetRollUpEvent);
////									assetSensorRollUpEventMeta.put(reading.getParentId(), sensorAssetRollUpEvent);
//
//									isFirstAssetSensorRule = false;
//									if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////										LOGGER.info("reading: "+ reading+" sensorRule "+sensorRule+ " sensorAssetRollUpEvent: "+sensorAssetRollUpEvent);
//									}
//								}
//							} else {
////								SensorEventContext sensorEvent = sensorRule.constructClearEvent(reading, defaultSeverityProps, isHistorical);
////								if (sensorEvent != null) {
////									sensorEvents.add(sensorEvent);
////								}
//							}
//						}
//					}
//
//					int canClearFieldEventList = 0, fieldSensorRuleList = 0;
//					for (SensorRuleContext fieldSensorRule : fieldSensorRules) {
//						if (!fieldSensorRule.getSensorRuleTypeEnum().isMeterRollUp()) { //field sensor rule
//							Map<String, SensorRuleAlarmMeta> metaMap = fieldSensorRule.getAlarmMetaMap();
//
//							if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////								LOGGER.info("reading: "+ reading+" fieldSensorRule: "+fieldSensorRule+ " metaMap: "+metaMap+ " canClearFieldEventList: "+canClearFieldEventList+ " fieldSensorRuleList: "+fieldSensorRuleList);
//							}
//
//							if (metaMap != null) {                                    //to avoid validations not started for evaluation
//								SensorRuleAlarmMeta alarmMeta = metaMap != null ? metaMap.get(reading.getParentId()) : null;
//								if (alarmMeta != null) { //Check if all are clear
//									fieldSensorRuleList++;
//									if (alarmMeta.isClear()) {
//										canClearFieldEventList++;
//									}
//								}
//							}
//						}
//					}
//
//					if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////						LOGGER.info("reading: "+ reading+" fieldSensorRules "+fieldSensorRules+ " fieldSensorRollUpEventMetaMap: " +fieldSensorRollUpEventMeta+ " canClearFieldEventList: "+canClearFieldEventList+ " fieldSensorRuleList: "+fieldSensorRuleList);
//					}
//
//					if (canClearFieldEventList > 0 && fieldSensorRuleList > 0 && canClearFieldEventList == fieldSensorRuleList) {
//						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//						String key = ReadingsAPI.getRDMKey(reading.getParentId(), modBean.getField(readingFieldId));
//						SensorRollUpEventContext fieldSensorRollUpMetaEvent = fieldSensorRollUpEventMeta.get(key);
//						if (fieldSensorRollUpMetaEvent != null && !fieldSensorRollUpMetaEvent.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
//							SensorRollUpEventContext sensorFieldRollUpEvent = SensorRollUpUtil.constructRollUpClearEvent(fieldSensorRollUpMetaEvent, reading, isHistorical, false);
//							sensorFieldRollUpEvents.add(sensorFieldRollUpEvent);
//
//							if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////								LOGGER.info("reading: "+ reading+ " fieldSensorRollUpMetaEvent: "+fieldSensorRollUpMetaEvent);
//							}
//							fieldSensorRollUpEventMeta.put(ReadingsAPI.getRDMKey(reading.getParentId(), sensorFieldRollUpEvent.getReadingField()), sensorFieldRollUpEvent);
//						}
//					}
//				}
//
//				int canClearAssetRollUpEventList = 0, meterSensorRuleList = 0;
//				for (SensorRuleContext sensorRule : sensorRules) {
//					if (sensorRule.getSensorRuleTypeEnum().isMeterRollUp()) { //Meter sensor rule
//						Map<String , SensorRuleAlarmMeta> metaMap = sensorRule.getAlarmMetaMap();
//
//						if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////							LOGGER.info("reading: "+ reading+" sensorRule: "+sensorRule+ " metaMap: "+metaMap+ " canClearAssetRollUpEventList: "+canClearAssetRollUpEventList+ " meterSensorRuleList: "+meterSensorRuleList);
//						}
//
//						if (metaMap != null) {
//							SensorRuleAlarmMeta alarmMeta = metaMap != null ? metaMap.get(reading.getParentId()) : null;
//							if (alarmMeta != null) { ///Check if all are clear
//								meterSensorRuleList++;
//								if (alarmMeta.isClear()) {
//									canClearAssetRollUpEventList++;
//								}
//							}
//						}
//					}
//				}
//
//				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////					LOGGER.info("reading: "+ reading+" AssetsensorRules: "+sensorRules+ " assetSensorRollUpEventMetaMap: "+assetSensorRollUpEventMeta+ " canClearAssetRollUpEventList: "+canClearAssetRollUpEventList+ " meterSensorRuleList: "+meterSensorRuleList);
//				}
//
//				if (canClearAssetRollUpEventList > 0 && meterSensorRuleList > 0 && canClearAssetRollUpEventList == meterSensorRuleList) {
//					SensorRollUpEventContext assetSensorRollUpMetaEvent = assetSensorRollUpEventMeta.get(reading.getParentId());
//					if (assetSensorRollUpMetaEvent != null && !assetSensorRollUpMetaEvent.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
//						SensorRollUpEventContext sensorAssetRollUpEvent = SensorRollUpUtil.constructRollUpClearEvent(assetSensorRollUpMetaEvent, reading, isHistorical, true);
//						sensorMeterRollUpEvents.add(sensorAssetRollUpEvent);
//
//						if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////							LOGGER.info("reading: "+ reading+ " sensorAssetRollUpEvent: "+sensorAssetRollUpEvent);
//						}
//
//						assetSensorRollUpEventMeta.put(reading.getParentId(), sensorAssetRollUpEvent);
//					}
//				}
//			}
//
//			List<BaseEventContext> baseEvents = new ArrayList<BaseEventContext>();
//			baseEvents.addAll(sensorEvents);
//			baseEvents.addAll(sensorFieldRollUpEvents);
//
//			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 339l) {
////				LOGGER.info("sensorEvents: "+ sensorEvents+ " sensorFieldRollUpEvents: "+sensorFieldRollUpEvents+ " sensorMeterRollUpEvents: "+sensorMeterRollUpEvents+" baseEvents "+baseEvents);
//			}
//
//			if (baseEvents != null && !baseEvents.isEmpty()) {
//				if (isHistorical) {
//					baseEvents.addAll(sensorMeterRollUpEvents);
////					FacilioChain addEventChain = TransactionChainFactory.getV2AddEventChain(isHistorical); //to be removed when handled in framework
////					addEventChain.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
////					addEventChain.getContext().put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, isHistorical);
////					addEventChain.execute();
//					return baseEvents;
//				} else {
//					FacilioChain addEventChain = TransactionChainFactory.getV2AddEventChain(isHistorical);
//					addEventChain.getContext().put(EventConstants.EventContextNames.EVENT_LIST, baseEvents);
//					addEventChain.getContext().put(EventConstants.EventContextNames.IS_HISTORICAL_EVENT, isHistorical);
//					addEventChain.execute();
//				}
//			}
//		}
//		return null;
//	}

	private static void checkDefaultSeverityProps(JSONObject defaultSeverityProps, JSONObject ruleProps) {
		if (ruleProps != null) {
			if (ruleProps.get("severity") != null) {
				defaultSeverityProps.put("severity", ruleProps.get("severity"));
			}
			if (ruleProps.get("subject") != null) {
				defaultSeverityProps.put("subject", ruleProps.get("subject"));
			}
		}
	}

	public static LinkedHashMap<Long, List<ReadingContext>> groupReadingsByResourceId(List<ReadingContext> readings) {
		LinkedHashMap<Long, List<ReadingContext>> assetReadingsMap = new LinkedHashMap<Long, List<ReadingContext>>();
		for (ReadingContext reading : readings) {
			List<ReadingContext> assetReadings = assetReadingsMap.get(reading.getParentId());
			if (assetReadings == null) {
				assetReadings = new ArrayList<ReadingContext>();
				assetReadingsMap.put(reading.getParentId(), assetReadings);
			}
			assetReadings.add(reading);
		}
		return assetReadingsMap;
	}

//	public static LinkedHashMap<Long, List<SensorRuleContext>> groupSensorRulesByFieldId(List<SensorRuleContext> sensorRules) {
//		LinkedHashMap<Long, List<SensorRuleContext>> fieldSensorRulesMap = new LinkedHashMap<Long, List<SensorRuleContext>>();
//		for (SensorRuleContext sensorRule : sensorRules) {
//			List<SensorRuleContext> fieldSensorRules = fieldSensorRulesMap.get(sensorRule.getReadingFieldId());
//			if (fieldSensorRules == null) {
//				fieldSensorRules = new ArrayList<SensorRuleContext>();
//				fieldSensorRulesMap.put(sensorRule.getReadingFieldId(), fieldSensorRules);
//			}
//			fieldSensorRules.add(sensorRule);
//		}
//		return fieldSensorRulesMap;
//	}

	public static LinkedHashMap<FacilioModule, List<FacilioField>> groupSensorRuleFieldsByModule(Set<FacilioField> sensorRuleFields) {
		LinkedHashMap<FacilioModule, List<FacilioField>> sensorRuleModuleVsFieldMap = new LinkedHashMap<FacilioModule, List<FacilioField>>();
		for (FacilioField sensorRuleField : sensorRuleFields) {
			List<FacilioField> sensorRuleModuleFields = sensorRuleModuleVsFieldMap.get(sensorRuleField.getModuleId());
			if (sensorRuleModuleFields == null) {
				sensorRuleModuleFields = new ArrayList<FacilioField>();
				sensorRuleModuleVsFieldMap.put(sensorRuleField.getModule(), sensorRuleModuleFields);
			}
			sensorRuleModuleFields.add(sensorRuleField);
		}
		return sensorRuleModuleVsFieldMap;
	}

	public static void  fetchAlarmMeta(SensorRuleContext sensorRule,Long resourceId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SENSOR_ALARM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		HashMap<String, SensorRuleAlarmMeta> metaMap = new HashMap<>();

		List<SensorAlarmContext> sensorAlarms = new SelectRecordsBuilder<SensorAlarmContext>()
												.select(fields)
												.beanClass(SensorAlarmContext.class)
												.moduleName(FacilioConstants.ContextNames.SENSOR_ALARM)
												.andCondition(CriteriaAPI.getCondition(fieldMap.get("sensorRule"), String.valueOf(sensorRule.getId()), PickListOperators.IS))
												.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"),String.valueOf(resourceId), PickListOperators.IS))
												.fetchSupplement((LookupField) fieldMap.get("severity"))
												.get();
		if (CollectionUtils.isNotEmpty(sensorAlarms)) {
			for (SensorAlarmContext sensorAlarm : sensorAlarms) {
				SensorRuleAlarmMeta alarmMeta = constructNewAlarmMeta(sensorAlarm.getId(), sensorRule, sensorAlarm.getSensorRuleTypeEnum(),sensorAlarm.getResource(), sensorAlarm.getSeverity().getSeverity().equals(FacilioConstants.Alarm.CLEAR_SEVERITY), sensorAlarm.getSubject());
				String key=getSensorAlarmMetaKey(alarmMeta.getResourceId(), sensorAlarm.getSensorRuleType());
				metaMap.put(key, alarmMeta);
			}
			sensorRule.setAlarmMetaMap(metaMap);
		}
	}
	
	public static SensorRuleAlarmMeta constructNewAlarmMeta (long alarmId, SensorRuleContext sensorRule, SensorRuleType sensorRuleType, ResourceContext resource, boolean isClear, String subject) {
		SensorRuleAlarmMeta meta = new SensorRuleAlarmMeta();
		meta.setOrgId(AccountUtil.getCurrentOrg().getId());
		meta.setAlarmId(alarmId);
		meta.setRuleGroupId(sensorRule.getId());
		meta.setResourceId(resource.getId());
		meta.setResource(resource);
		meta.setReadingFieldId(sensorRule.getSensorFieldId());
		meta.setClear(isClear);
		if(sensorRuleType!=null){
			meta.setSensorRuleType(sensorRuleType.getIndex());
		}
		if(!StringUtils.isEmpty(subject)) {
			meta.setSubject(subject);
		}
		return meta;
	}
	
	public static void addDefaultEventProps(ReadingContext reading, JSONObject defaultSeverityProps, BaseEventContext sensorEvent) throws Exception {
		
		if((ResourceContext) reading.getParent() == null) {
			ResourceContext resource = (ResourceContext) AssetsAPI.getAssetInfo(reading.getParentId());
			sensorEvent.setResource(resource);
			sensorEvent.setSiteId(resource.getSiteId());
		}
		else {
			sensorEvent.setResource((ResourceContext) reading.getParent());
			sensorEvent.setSiteId(((ResourceContext) reading.getParent()).getSiteId());
		}
		sensorEvent.setCreatedTime(reading.getTtime());
		
		if(defaultSeverityProps != null) {
			if (defaultSeverityProps.get("subject") != null && StringUtils.isNotEmpty((String)defaultSeverityProps.get("subject")) && 
					(sensorEvent.getEventMessage() == null || sensorEvent.getEventMessage().isEmpty())) {
				sensorEvent.setEventMessage((String)defaultSeverityProps.get("subject"));
			}
			if (defaultSeverityProps.get("comment") != null && StringUtils.isNotEmpty((String)defaultSeverityProps.get("comment")) && 
					(sensorEvent.getComment() == null || sensorEvent.getComment().isEmpty())) {
				sensorEvent.setComment((String)defaultSeverityProps.get("comment"));
			}		
		}
	}

	public static HashMap<Long, JSONObject> getSensorRuleValidatorPropsByParentRuleIds(List<Long> parentRuleIdList) throws Exception {

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRulePropsFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSensorRulePropsFields())
				.table(ModuleFactory.getSensorRulePropsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentSensorRuleId"), parentRuleIdList, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		HashMap<Long,JSONObject> sensorRulePropsMap = new HashMap<>();

		JSONParser parser = new JSONParser();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				if (prop.get("parentSensorRuleId") != null && prop.get("ruleValidatorProps") != null) {
					long parentSensorRuleId = (long) prop.get("parentSensorRuleId");
					String ruleValidatorPropsStr = (String) prop.get("ruleValidatorProps");

					JSONObject ruleValidatorProps = (JSONObject) parser.parse(ruleValidatorPropsStr);
					JSONObject ruleValidatorPropsJson = sensorRulePropsMap.get(parentSensorRuleId);
					if (ruleValidatorPropsJson == null) {
						sensorRulePropsMap.put(parentSensorRuleId, ruleValidatorProps);
					} else {
						ruleValidatorPropsJson.putAll(ruleValidatorProps);
					}
				}
			}

		}
		return sensorRulePropsMap;
	}

	public static boolean isAllowedSensorMetric(NumberField numberField) {

//		List<Integer> allowedMetricIds = new ArrayList<Integer>();
//		allowedMetricIds.add(Metric.TEMPERATURE.getMetricId());
//		allowedMetricIds.add(Metric.ABSOLUTE_HUMIDITY.getMetricId());
//		allowedMetricIds.add(Metric.SPECIFIC_HUMIDITY.getMetricId());
//		allowedMetricIds.add(Metric.PRESSURE.getMetricId());
//		allowedMetricIds.add(Metric.VAPOUR_PRESSURE.getMetricId());
//		allowedMetricIds.add(Metric.ENERGY.getMetricId());
//		
//		Integer fieldMetricId = numberField.getMetric();
//		if(fieldMetricId != null && fieldMetricId != - 1) {
//			if(allowedMetricIds.contains(fieldMetricId)){
//				return true;
//			}	
//		}
//		return false;
		return true;
	}

	public static ReadingContext fetchSingleReadingContext(NumberField numberField, long resourceId, long ttime) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(numberField.getModule().getName()));

		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(numberField.getModule().getName()))
				.module(numberField.getModule())
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(numberField.getName()), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(ttime), NumberOperators.LESS_THAN))
				.orderBy("TTIME DESC").limit(1);
		ReadingContext readingContext = selectBuilder.fetchFirst();
		return readingContext;
	}

	public static List<ReadingContext> getReadingsBtwDayTimeInterval(NumberField numberField, long resourceId, long endTime, int noOfHoursToBeFetched) throws Exception {
//		long lastNdaysEndTime = DateTimeUtil.getDayStartTimeOf(endTime);
//		long lastNdaysStartTime = DateTimeUtil.getDayStartTimeOf(lastNdaysEndTime - (Integer.valueOf(noOfHoursToBeFetched) * 3600 * 1000));	
//		long lastNdaysStartTime = endTime - (Integer.valueOf(noOfHoursToBeFetched) * 3600 * 1000);	//Hours to ms

		long lastNdaysStartTime = DateTimeUtil.addHours(endTime, -1 * noOfHoursToBeFetched);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(numberField.getModule().getName()));

		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(modBean.getAllFields(numberField.getModule().getName()))
				.module(numberField.getModule())
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(numberField.getName()), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), lastNdaysStartTime + "," + (endTime - 1000), DateOperators.BETWEEN))
				.orderBy("TTIME");

		List<ReadingContext> readingContexts = selectBuilder.get();
		return readingContexts;
	}

	public static List<Double> getLiveOrHistoryReadingsToBeEvaluated(NumberField numberField, long resourceId, long readingEndTime, int noOfHoursToBeFetched, boolean isHistorical, List<ReadingContext> historicalReadings, LinkedHashMap<String, List<ReadingContext>> completeHistoricalReadingsMap, SensorRuleType sensorRuleValidatorType) throws Exception {

		List<ReadingContext> readingsToBeEvaluated = new ArrayList<ReadingContext>();
		if (isHistorical) {
			String key = ReadingsAPI.getRDMKey(resourceId, numberField) + "_" + sensorRuleValidatorType.getIndex();
			List<ReadingContext> completeHistoricalReadings = completeHistoricalReadingsMap.get(key);

			if (historicalReadings != null && !historicalReadings.isEmpty() && completeHistoricalReadings == null) {
				completeHistoricalReadings = new ArrayList<ReadingContext>();
				completeHistoricalReadingsMap.put(key, completeHistoricalReadings);

				List<ReadingContext> bufferIntervalReadings = SensorRuleUtil.getReadingsBtwDayTimeInterval(numberField, resourceId, readingEndTime, noOfHoursToBeFetched);
				if (bufferIntervalReadings != null && !bufferIntervalReadings.isEmpty()) {
					completeHistoricalReadings.addAll(bufferIntervalReadings);
				}
				completeHistoricalReadings.addAll(historicalReadings);
			}

			if (completeHistoricalReadings != null && !completeHistoricalReadings.isEmpty()) {
				long pastIntervalStartTime = DateTimeUtil.addHours(readingEndTime, -1 * noOfHoursToBeFetched);
				for (ReadingContext historyReading : completeHistoricalReadings) {
					if (historyReading.getTtime() >= pastIntervalStartTime && historyReading.getTtime() <= readingEndTime) {
						readingsToBeEvaluated.add(historyReading);
					}
				}
			}
		} else {
			readingsToBeEvaluated = SensorRuleUtil.getReadingsBtwDayTimeInterval(numberField, resourceId, readingEndTime, noOfHoursToBeFetched);
		}
		List<Double> readings = SensorRuleUtil.getReadings(readingsToBeEvaluated, numberField);
		return readings;
	}

	public static List<ReadingContext> fetchReadingsForSensorRuleField(FacilioModule module, List<FacilioField> sensorRuleFields, List<Long> resourceIds, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(fields)
				.module(module)
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), resourceIds, PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), startTime + "," + endTime, DateOperators.BETWEEN))
				.orderBy("TTIME");

		for (FacilioField sensorRuleField : sensorRuleFields) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(sensorRuleField, CommonOperators.IS_NOT_EMPTY));
		}

		return selectBuilder.get();
	}

	public static List<Double> getReadings(List<ReadingContext> readingContexts, NumberField numberField) {
		List<Double> readings = new ArrayList<Double>();
		if (readingContexts != null && !readingContexts.isEmpty()) {
			for (ReadingContext readingContext : readingContexts) {
				Object value = readingContext.getReading(numberField.getName());
				if (value != null) {
					readings.add(Double.parseDouble(value.toString()));
				}
			}
		}
		return readings;
	}
	
	private static void constructHistoryReadingsMap(List<ReadingContext> readings, List<SensorRuleContext> sensorRules, LinkedHashMap<String, List<ReadingContext>> historicalReadingsMap) {
		Set<FacilioField> sensorRuleReadingFields = sensorRules.stream().map(sensorRule -> sensorRule.getSensorField()).collect(Collectors.toSet());
		for(FacilioField sensorRuleReadingField: sensorRuleReadingFields) 
		{
			for(ReadingContext reading: readings) 
			{
				String key = ReadingsAPI.getRDMKey(reading.getParentId(), sensorRuleReadingField);
				List<ReadingContext> parentFieldReadingsList = historicalReadingsMap.get(key);
				if(parentFieldReadingsList == null) {
					parentFieldReadingsList = new ArrayList<ReadingContext>();
					historicalReadingsMap.put(key, parentFieldReadingsList);
				}
				parentFieldReadingsList.add(reading);
			}
		}
	}

	public static String getSensorAlarmMetaKey (long resourceId,int ruleType){
		if(resourceId>-1 && ruleType>-1){
			return resourceId+"_"+ruleType;
		}
		return null;
	}
}
