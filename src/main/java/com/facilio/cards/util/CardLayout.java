package com.facilio.cards.util;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.readingkpi.ReadingKpiAPI;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.floorplan.FloorPlanViewContext;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateRange;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public enum CardLayout {

	READINGCARD_LAYOUT_1("readingcard_layout_1"), READINGCARD_LAYOUT_2("readingcard_layout_2"),
	READINGCARD_LAYOUT_3("readingcard_layout_3"), READINGCARD_LAYOUT_4("readingcard_layout_4"),
	READINGCARD_LAYOUT_5("readingcard_layout_5"), READINGCARD_LAYOUT_6("readingcard_layout_6"),
	GAUGE_LAYOUT_1("gauge_layout_1"), GAUGE_LAYOUT_2("gauge_layout_2"), GAUGE_LAYOUT_3("gauge_layout_3"),
	GAUGE_LAYOUT_4("gauge_layout_4"), GAUGE_LAYOUT_5("gauge_layout_5"), GAUGE_LAYOUT_6("gauge_layout_6"),
	ENERGYCARD_LAYOUT_1("energycard_layout_1"), ENERGYCOST_LAYOUT_1("energycost_layout_1"),
	CARBONCARD_LAYOUT_1("carboncard_layout_1"), WEATHERCARD_LAYOUT_1("weathercard_layout_1"),
	GRAPHICALCARD_LAYOUT_1("graphicalcard_layout_1"), MAPCARD_LAYOUT_1("mapcard_layout_1"),
	CONTROL_LAYOUT_1("controlcard_layout_1"), WEB_LAYOUT_1("web_layout_1"),
	DESK_STATUS_LAYOUT("deskstatus_layout_1"),

	PMREADINGS_LAYOUT_1("pmreadings_layout_1") {
		@Override
		protected Object execute(WidgetCardContext cardContext) throws Exception {
			JSONObject cardParams = cardContext.getCardParams();

			String title = (String) cardParams.get("title");
			Long pmId = (Long) cardParams.get("pmId");
			Long resourceId = (Long) cardParams.get("resourceId");
			String dateRange = (String) cardParams.get("dateRange");

			Operator dateOperator = DateOperators.getAllOperators().get(dateRange);

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.RECORD_ID, pmId);
			context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
			context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);

			FacilioChain pmReadingsChain = FacilioChainFactory.getPreventiveMaintenanceReadingsChain();
			try {
				pmReadingsChain.execute(context);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.WARNING, "Exception in get pm readings data::: ", e);
			}

			Collection<WorkOrderContext> workOrderContexts = (Collection<WorkOrderContext>) context
					.get(ContextNames.RESULT);

			JSONObject returnValue = new JSONObject();
			returnValue.put("title", title);
			returnValue.put("value", workOrderContexts);

			return returnValue;
		}
	},

	KPICARD_LAYOUT_1("kpicard_layout_1") {
		@Override
		protected Object execute(WidgetCardContext cardContext) throws Exception {
			JSONObject cardParams = cardContext.getCardParams();

			String title = (String) cardParams.get("title");
			Object imageId = (Object) cardParams.get("imageId");
			String kpiType = (String) cardParams.get("kpiType");
			String dateRange = (String) cardParams.get("dateRange");
			String dateField = (String) cardParams.get("dateField");
			String subText = (String) cardParams.get("subText");

			Long kpiId;
			Long parentId;
			String yAggr;
			boolean isNewKpi = false;
		
				
				if (cardParams.containsKey("kpi") && cardParams.get("kpi") instanceof JSONObject) {
					JSONObject kpiConfig = (JSONObject) cardParams.get("kpi");
					kpiId = (Long) kpiConfig.get("kpiId");
					parentId = (Long) kpiConfig.get("parentId");
					yAggr = (String) kpiConfig.get("yAggr");
					isNewKpi = kpiConfig.containsKey("isNewKpi") ? (boolean) kpiConfig.get("isNewKpi") : false;
				} else if (cardParams.containsKey("kpi") && cardParams.get("kpi") instanceof Map) {
					Map<String, Object> kpiConfig = (Map<String, Object>) cardParams.get("kpi");
					kpiId = (Long) kpiConfig.get("kpiId");
					parentId = (Long) kpiConfig.get("parentId");
					yAggr = (String) kpiConfig.get("yAggr");
					isNewKpi = kpiConfig.containsKey("isNewKpi") ? (boolean) kpiConfig.get("isNewKpi") : false;
				} else {
					FacilioUtil.throwIllegalArgumentException(true,"KPI should not be empty");
					throw new IllegalStateException();
				}
			
		
			

			Object cardValue = null;
			Object listData = null;
			Object fields = null;
			Object kpi = null;
			String period = null;
			JSONArray variables = null;

			if ("module".equalsIgnoreCase(kpiType)) {
				try {
					KPIContext kpiContext = KPIUtil.getKPI(kpiId, false);
					if (cardContext.getCardUserFilters() != null)// db lookup filters
					{
						FacilioChain generateCriteriaChain = ReadOnlyChainFactory.getGenerateCriteriaFromFilterChain();
						FacilioContext generateCriteriaContext = generateCriteriaChain.getContext();
						generateCriteriaContext.put(FacilioConstants.ContextNames.MODULE_NAME,
								kpiContext.getModuleName());
						generateCriteriaContext.put(FacilioConstants.ContextNames.FILTERS,
								cardContext.getCardUserFilters());
						generateCriteriaChain.execute();
						Criteria cardFilterCriteria = (Criteria) generateCriteriaContext
								.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
						kpiContext.getCriteria().andCriteria(cardFilterCriteria);
					}

					if (cardContext.getCardFilters() != null) {// db timeline filters

						JSONObject timeLineFilters = (JSONObject) cardContext.getCardFilters();

						kpiContext.setDateOperator(DateOperators.BETWEEN);
						kpiContext.setDateValue((String) timeLineFilters.get("dateValueString"));
						period = (String) timeLineFilters.get("dateLabel");

					} else if (dateRange != null) {
						kpiContext.setDateOperator((DateOperators) DateOperators.getAllOperators().get(dateRange));
						period = dateRange;
					}

					cardValue = KPIUtil.getKPIValue(kpiContext);

					// kpi = KPIUtil.getKPI(kpiId,false);
					kpi = kpiContext;

					if (subText != null && subText.indexOf("${") >= 0) {
						fields = KPIUtil.getKPIModuleFIelds(kpiContext);

						listData = KPIUtil.getKPIList(kpiContext, null);

						if (listData != null) {
							try {
								variables = new JSONArray();

								Map<String, Object> record = (Map<String, Object>) listData;
								List<FacilioField> moduleFields = (List<FacilioField>) fields;
								for (FacilioField field : moduleFields) {
									Object value = record.get(field.getName());

									variables.add(getVariable(field.getName(), field.getDisplayName(),
											field.getDataTypeEnum().name(), value, null));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.log(Level.WARNING, "Exception in getKPIValue::: ", e);
				}
			} else if ("reading".equalsIgnoreCase(kpiType)) {
				try {
					if (isNewKpi) {
						cardValue = ReadingKpiAPI.getCurrentValueOfKpi(kpiId, parentId);

					} else {
						cardValue = FormulaFieldAPI.getFormulaCurrentValue(kpiId, parentId);

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.log(Level.WARNING, "Exception in get KPI Reading Value::: ", e);
				}
			}

			JSONObject jobj = new JSONObject();
			jobj.put("value", cardValue);
			jobj.put("unit", null);
			jobj.put("kpi", kpi);
			jobj.put("moduleData", listData);
			jobj.put("fields", fields);

			JSONObject returnValue = new JSONObject();
			returnValue.put("title", title);
			returnValue.put("value", jobj);

			if (period != null) {
				returnValue.put("period", period);
			}
			if (imageId != null) {
				returnValue.put("image", imageId);
			}
			if (variables != null && variables.size() > 0) {
				returnValue.put("variables", variables);
			}

			return returnValue;
		}
	},
	KPICARD_LAYOUT_2("kpicard_layout_2") {
		@Override
		protected Object execute(WidgetCardContext cardContext) throws Exception {
			JSONObject cardParams = cardContext.getCardParams();

			String title = (String) cardParams.get("title");
			Object imageId = (Object) cardParams.get("imageId");
			String kpiType = (String) cardParams.get("kpiType");
			String dateRange = (String) cardParams.get("dateRange");
			String dateField = (String) cardParams.get("dateField");
			String baselineRange = (String) cardParams.get("baseline");

			Long kpiId;
			Long parentId;
			String yAggr;
			if (cardParams.get("kpi") instanceof JSONObject) {
				JSONObject kpiConfig = (JSONObject) cardParams.get("kpi");
				kpiId = (Long) kpiConfig.get("kpiId");
				parentId = (Long) kpiConfig.get("parentId");
				yAggr = (String) kpiConfig.get("yAggr");
			} else if (cardParams.get("kpi") instanceof Map) {
				Map<String, Object> kpiConfig = (Map<String, Object>) cardParams.get("kpi");
				kpiId = (Long) kpiConfig.get("kpiId");
				parentId = (Long) kpiConfig.get("parentId");
				yAggr = (String) kpiConfig.get("yAggr");
			} else {
				throw new IllegalStateException();
			}

			Object cardValue = null;
			Object cardBaseValue = null;
			Object listData = null;
			Object fields = null;
			Object kpi = null;
			String period = null;
			DateRange baselineDateObj = null;
			JSONArray variables = null;

			if ("module".equalsIgnoreCase(kpiType)) {
				// try {
				// KPIContext kpiContext = KPIUtil.getKPI(kpiId, false);
				// if (dateRange != null) {
				// kpiContext.setDateOperator((DateOperators)
				// DateOperators.getAllOperators().get(dateRange));
				// }
				// cardValue = KPIUtil.getKPIValue(kpiContext);
				// if (baselineRange != null) {
				// cardBaseValue = KPIUtil.getKPIBaseValueValue(kpiContext, baselineRange);
				// }
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// LOGGER.log(Level.WARNING, "Exception in getKPIValue::: ", e);
				// }

				try {
					KPIContext kpiContext = KPIUtil.getKPI(kpiId, false);
					if (cardContext.getCardUserFilters() != null)// db lookup filters
					{
						FacilioChain generateCriteriaChain = ReadOnlyChainFactory.getGenerateCriteriaFromFilterChain();
						FacilioContext generateCriteriaContext = generateCriteriaChain.getContext();
						generateCriteriaContext.put(FacilioConstants.ContextNames.MODULE_NAME,
								kpiContext.getModuleName());
						generateCriteriaContext.put(FacilioConstants.ContextNames.FILTERS,
								cardContext.getCardUserFilters());
						generateCriteriaChain.execute();
						Criteria cardFilterCriteria = (Criteria) generateCriteriaContext
								.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
						kpiContext.getCriteria().andCriteria(cardFilterCriteria);
					}

					if (cardContext.getCardFilters() != null) {// db timeline filters

						JSONObject timeLineFilters = (JSONObject) cardContext.getCardFilters();

						kpiContext.setDateOperator(DateOperators.BETWEEN);
						kpiContext.setDateValue((String) timeLineFilters.get("dateValueString"));
						period = (String) timeLineFilters.get("dateLabel");

						if (baselineRange != null) {
							BaseLineContext baseline = BaseLineAPI.getBaseLine(baselineRange);
							DateRange actualRange = kpiContext.getDateOperatorEnum().getRange(kpiContext.getDateValue());
							baselineDateObj = baseline.calculateBaseLineRange(actualRange, baseline.getAdjustTypeEnum());
							cardBaseValue = KPIUtil.getKPIBaseValueValue(kpiContext, baselineRange);
						}

					} else if (dateRange != null) {
						kpiContext.setDateOperator((DateOperators) DateOperators.getAllOperators().get(dateRange));
						period = dateRange;
						if (baselineRange != null) {
							cardBaseValue = KPIUtil.getKPIBaseValueValue(kpiContext, baselineRange);
						}
					}

					cardValue = KPIUtil.getKPIValue(kpiContext);

					kpi = KPIUtil.getKPI(kpiId);
					fields = KPIUtil.getKPIModuleFIelds(kpiContext);

					listData = KPIUtil.getKPIList(kpiContext, null);

					if (listData != null) {
						try {
							variables = new JSONArray();

							Map<String, Object> record = (Map<String, Object>) listData;
							List<FacilioField> moduleFields = (List<FacilioField>) fields;
							for (FacilioField field : moduleFields) {
								Object value = record.get(field.getName());

								variables.add(getVariable(field.getName(), field.getDisplayName(),
										field.getDataTypeEnum().name(), value, null));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.log(Level.WARNING, "Exception in getKPIValue::: ", e);
				}
			} else if ("reading".equalsIgnoreCase(kpiType)) {
				try {
					cardValue = FormulaFieldAPI.getFormulaCurrentValue(kpiId, parentId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.log(Level.WARNING, "Exception in get KPI Reading Value::: ", e);
				}
			}

			JSONObject jobj = new JSONObject();
			jobj.put("value", cardValue);
			jobj.put("unit", null);

			JSONObject jobj1 = new JSONObject();
			jobj1.put("value", cardBaseValue);
			jobj1.put("unit", null);
			jobj1.put("dateRange", baselineDateObj);
			jobj.put("kpi", kpi);
			jobj.put("moduleData", listData);
			jobj.put("fields", fields);

			JSONObject returnValue = new JSONObject();
			returnValue.put("title", title);
			if (imageId != null) {
				returnValue.put("image", imageId);
			}
			returnValue.put("value", jobj);
			returnValue.put("baselineValue", jobj1);
			returnValue.put("period", dateRange);
			returnValue.put("baselinePeriod", baselineRange);
			returnValue.put("title", title);
			returnValue.put("value", jobj);

			if (period != null) {
				returnValue.put("period", period);
			}
			if (imageId != null) {
				returnValue.put("image", imageId);
			}
			if (variables != null && variables.size() > 0) {
				returnValue.put("variables", variables);
			}
			return returnValue;
		}
	},
	GAUGE_LAYOUT_7("gauge_layout_7") {
		@Override
		protected Object execute(WidgetCardContext cardContext) throws Exception {
			JSONObject cardParams = cardContext.getCardParams();

			String title = (String) cardParams.get("title");
			String kpiType = (String) cardParams.get("kpiType");
			String dateRange = (String) cardParams.get("dateRange");
			String dateField = (String) cardParams.get("dateField");
			String subText = (String) cardParams.get("subText");

			String maxSafeLimitType = (String) cardParams.get("maxSafeLimitType");
			String centerTextType = (String) cardParams.get("centerTextType");

			Long kpiId;
			Long parentId;
			String yAggr;

			JSONObject maxValue = new JSONObject();
			Map<String, Object> maxKpi = (Map<String, Object>) cardParams.get("maxSafeLimitKpi");

			JSONObject centerValue = new JSONObject();
			Map<String, Object> centerKpi = (Map<String, Object>) cardParams.get("centerTextKpi");

			List<Map<String, Object>> kpis = (List<Map<String, Object>>) cardParams.get("kpis");
			List<Map<String, Object>> kpiList = new ArrayList<>();
			int i = 0;
			for (Map<String, Object> kpi : kpis) {
				kpiList.add(i, getKPIData(kpi, kpiType, cardContext));
				i++;
			}

			JSONObject returnValue = new JSONObject();
			if (maxSafeLimitType.equals("kpi")) {
				returnValue.put("maxValue", getKPIData(maxKpi, kpiType, cardContext));
			} else if (maxSafeLimitType.contentEquals("constant")) {
				maxValue.put("value", cardParams.get("maxSafeLimitConstant"));
				returnValue.put("maxValue", maxValue);
			}
			if (centerTextType.equals("kpi")) {
				returnValue.put("centerValue", getKPIData(centerKpi, kpiType, cardContext));
			} else if (centerTextType.contentEquals("text")) {
				centerValue.put("value", cardParams.get("centerText"));
				returnValue.put("centerValue", centerValue);
			}
			returnValue.put("title", title);
			returnValue.put("values", kpiList);

			return returnValue;
		}
	},
	PHOTOS_LAYOUT_1("photos_layout_1") {
		@Override
		protected Object execute(WidgetCardContext cardContext) throws Exception {
			String cardParamsString = cardContext.getCardParams().toJSONString();
			JSONParser parser = new JSONParser();
			JSONObject cardParams = (JSONObject) parser.parse(cardParamsString);
			JSONObject returnValue = new JSONObject();

			String title = (String) cardParams.get("title");
			String moduleName = (String) cardParams.get("moduleName");
			String subModuleName = (String) cardParams.get("attachmentModule");
			JSONObject criteriaObj = (JSONObject) cardParams.get("criteria");
			Long limit = (Long) cardParams.get("limit");
			Criteria criteria = null;
			if (criteriaObj != null) {
				criteria = FieldUtil.getAsBeanFromMap(criteriaObj, Criteria.class);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> allFields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> allFieldsMap = FieldFactory.getAsMap(allFields);
			List<FacilioField> photoFields = allFields.stream().filter(
					field -> field.getDataTypeEnum() != null && field.getDataType() == FieldType.FILE.getTypeAsInt())
					.collect(Collectors.toList());
			Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
			if (beanClassName == null) {
				if (module.isCustom()) {
					beanClassName = CustomModuleData.class;
				} else {
					beanClassName = ModuleBaseWithCustomFields.class;
				}
			}
			List<FacilioField> fields = new ArrayList<>();
			fields.add(FieldFactory.getIdField(module));
			FacilioField primaryField = modBean.getPrimaryField(moduleName);
			fields.add(primaryField);
			if (CollectionUtils.isNotEmpty(photoFields)) {
				fields.addAll(photoFields);
			}

			SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> selectRecordBuilder = new SelectRecordsBuilder<>()
					.module(module).beanClass(beanClassName).select(fields).orderBy("ID DESC");
			if (criteria != null) {
				selectRecordBuilder.andCriteria(criteria);
			}
			if (moduleName.equals(ContextNames.WORK_ORDER) && allFieldsMap.containsKey("noOfAttachments")) {
				selectRecordBuilder.andCondition(CriteriaAPI.getCondition(allFieldsMap.get("noOfAttachments"),
						String.valueOf(0), NumberOperators.GREATER_THAN)).limit(100);
			} else {
				selectRecordBuilder.limit(1000);
			}

			List<Map<String, Object>> records = selectRecordBuilder.getAsProps();
			List<Long> recordIds = new ArrayList<>();
			Map<Long, String> recordIdsVsName = new HashMap<>();
			List<JSONObject> submoduleVsRecords = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(records)) {
				records.stream().forEach(record -> {
					recordIds.add((Long) record.get("id"));
					recordIdsVsName.put((Long) record.get("id"), record.get(primaryField.getName()).toString());
				});
				if (StringUtils.isNotEmpty(subModuleName)) {
					FacilioModule subModule = modBean.getModule(subModuleName);
					// Class className =
					// FacilioConstants.ContextNames.getClassFromModule(subModule);
					// if (className == null) {
					// className = AttachmentContext.class;
					// }
					// TODO remove this temp hardcoding class name. Remove once v3 related changes
					// is fixed
					Class className = AttachmentContext.class;
					List<FacilioField> subModuleFields = modBean.getAllFields(subModuleName);
					FileStore.NamespaceConfig namespaceConfig = FileStore.getNamespace(FileStore.DEFAULT_NAMESPACE);
					List<FacilioField> fileFields = FieldFactory.getFileFields(namespaceConfig.getTableName());
					subModuleFields.addAll(fileFields);
					Map<String, FacilioField> subModuleFieldsMap = FieldFactory.getAsMap(subModuleFields);
					Map<String, FacilioField> fileFieldsMap = FieldFactory.getAsMap(subModuleFields);
					SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> attachmentBuilder = new SelectRecordsBuilder<>();
					attachmentBuilder.select(subModuleFields).module(subModule).beanClass(className)
							.innerJoin(namespaceConfig.getTableName())
							.on(MessageFormat.format("{0}.FILE_ID = {1}.FILE_ID", namespaceConfig.getTableName(),
									subModule.getTableName()))
							.andCondition(CriteriaAPI.getCondition(fileFieldsMap.get("contentType"), "image",
									StringOperators.CONTAINS))
							.orderBy(MessageFormat.format("{0}.UPLOADED_TIME DESC", namespaceConfig.getTableName()));
					if (limit != null) {
						attachmentBuilder.limit(limit.intValue());
					} else {
						attachmentBuilder.limit(100);
					}
					if (subModuleName.equals(ContextNames.TASK_ATTACHMENTS)) {
						List<TaskContext> tasks = TicketAPI.getRelatedTasks(recordIds, false, false);
						List<Long> taskIds = new ArrayList<>();
						Map<Long, JSONObject> taskDetailsMap = new HashMap<>();
						tasks.stream().forEach(task -> {
							taskIds.add(task.getId());
							JSONObject meta = new JSONObject();
							meta.put("taskSubject", task.getSubject());
							meta.put("woId", task.getParentTicketId());
							taskDetailsMap.put(task.getId(), meta);
						});
						attachmentBuilder.andCondition(CriteriaAPI.getCondition(subModuleFieldsMap.get("parentId"),
								taskIds, PickListOperators.IS));
						returnValue.put("taskMeta", taskDetailsMap);
					} else {
						attachmentBuilder.andCondition(CriteriaAPI.getCondition(subModuleFieldsMap.get("parentId"),
								recordIds, PickListOperators.IS));
					}
					List<? extends ModuleBaseWithCustomFields> subModuleRecords = attachmentBuilder.get();
					if (CollectionUtils.isNotEmpty(subModuleRecords)) {
						JSONObject subModuleHolder = new JSONObject();
						subModuleHolder.put("records", subModuleRecords);
						subModuleHolder.put("displayName", subModule.getDisplayName());
						subModuleHolder.put("moduleName", subModuleName);
						submoduleVsRecords.add(subModuleHolder);
					}
				}
			}

			returnValue.put("title", title);
			returnValue.put("recordsPrimaryValue", recordIdsVsName);
			returnValue.put("subModuleVsRecords", submoduleVsRecords);
			return returnValue;
		}
	},
	FLOORPLAN_LAYOUT_1("floorplan_layout_1") {
		@Override
		protected Object execute(WidgetCardContext cardContext) throws Exception {
			JSONObject cardParams = cardContext.getCardParams();

			String title = (String) cardParams.get("title");
			Long floorPlanId = (Long) cardParams.get("floorPlanId");
			String viewMode = (String) cardParams.get("viewMode");

			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getFloorPlanModule().getTableName()).select(FieldFactory.getFloorPlanFields())
					.andCondition(CriteriaAPI.getCondition("Floor_Plan.ID", "id", String.valueOf(floorPlanId),
							NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectRecordBuilder.get();

			JSONObject returnValue = new JSONObject();
			returnValue.put("title", title);

			if (props != null && !props.isEmpty()) {
				returnValue.put("value", props.get(0));
			} else {
				returnValue.put("value", null);

			}

			if (viewMode != null) {
				 int scriptModeInt = ((Long) cardParams.getOrDefault("scriptModeInt", 3)).intValue();

				Map<Long, String> object = (Map<Long, String>) cardParams.get("viewParams");
				JSONObject viewParams = (JSONObject) new JSONParser().parse(JSONObject.toJSONString(object));

				FloorPlanViewContext floorPlanViewMode = new FloorPlanViewContext();
				floorPlanViewMode.setViewMode(viewMode);
				floorPlanViewMode.setFloorPlanId(floorPlanId);
				floorPlanViewMode.setScriptModeInt(scriptModeInt);

				if (viewParams != null) {
					floorPlanViewMode.setViewParams(viewParams);
				}

				FacilioChain chain = TransactionChainFactory.getExecuteFloorPlanWorkflowChain();
				chain.getContext().put(FacilioConstants.ContextNames.FLOORPLAN_VIEW_CONTEXT, floorPlanViewMode);

				chain.execute();
				returnValue.put("viewMode", viewMode);
				returnValue.put("floorPlanViewMode",
						chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT));
				returnValue.put("data", chain.getContext().get(FacilioConstants.ContextNames.RESULT));
				returnValue.put("state", ((FloorPlanViewContext) chain.getContext()
						.get(FacilioConstants.ContextNames.FLOORPLAN_VIEW_CONTEXT)).getViewState());
			}

			return returnValue;
		}
	},

	TABLE_LAYOUT_1("table_layout_1");

	private static final Logger LOGGER = Logger.getLogger(CardLayout.class.getName());

	private String name;

	private CardLayout(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	protected Object execute(WidgetCardContext cardContext) throws Exception {

		if (cardLayoutScriptMap.containsKey(this.name)) {

			WorkflowContext workflow = new WorkflowContext();
			workflow.setIsV2Script(true);

			JSONObject cardParams = cardContext.getCardParams();

			List<Object> paramsList = new ArrayList<Object>();
			paramsList.add(cardParams);

			workflow.setWorkflowV2String(cardLayoutScriptMap.get(this.name));

			FacilioChain workflowChain = TransactionChainFactory.getExecuteWorkflowChain();
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);

			workflowChain.execute();

			return workflow.getReturnValue();
		}
		return null;
	}

	public Object getResult(WidgetCardContext cardContext) throws Exception {

		Object returnValue = null;
		if (cardContext.getScriptMode() == WidgetCardContext.ScriptMode.CUSTOM_SCRIPT) {

			WorkflowContext workflow = new WorkflowContext();
			workflow.setIsV2Script(true);

			List<Object> paramsList = new ArrayList<Object>();
			paramsList.add(cardContext.getCardParams());

			if (cardContext.getCustomScript() != null) {
				workflow.setWorkflowV2String(cardContext.getCustomScript());
			} else if (cardContext.getCustomScriptId() != null) {
				workflow = WorkflowUtil.getWorkflowContext(cardContext.getCustomScriptId());
				cardContext.setCustomScript(workflow.getWorkflowV2String());
			}

			FacilioChain workflowChain = TransactionChainFactory.getExecuteWorkflowChain();
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);

			workflowChain.execute();

			returnValue = workflow.getReturnValue();
		} else {
			returnValue = this.execute(cardContext);
		}
		return getCardResultWithVariables(cardContext, returnValue);
	}

	private Object getCardResultWithVariables(WidgetCardContext cardContext, Object cardResult) {
		try {
			if (cardResult != null && (cardResult instanceof Map) || (cardResult instanceof JSONObject)) {
				JSONArray variables = new JSONArray();

				Map cardResultMap = (Map) cardResult;
				if (cardResultMap.containsKey("title")) {
					variables.add(getVariable("title", "Title", "STRING", cardResultMap.get("title"), null));
				}
				if (cardResultMap.containsKey("value") || cardResultMap.containsKey("actualValue")) {
					Map value;
					if (cardResultMap.containsKey("actualValue")) {
						value = (Map) cardResultMap.get("actualValue");
					} else {
						value = (Map) cardResultMap.get("value");
					}
					variables.add(getVariable("value", "Value", value));
				}
				if (cardResultMap.containsKey("maxValue")) {
					variables.add(getVariable("maxValue", "maxValue", (Map) cardResultMap.get("maxValue")));
				}
				if (cardResultMap.containsKey("centerValue")) {
					variables.add(getVariable("centerValue", "centerValue", (Map) cardResultMap.get("centerValue")));
				}
				if (cardResultMap.containsKey("baselineValue")) {
					variables.add(
							getVariable("baselineValue", "Baseline Value", (Map) cardResultMap.get("baselineValue")));
				}
				if (cardResultMap.containsKey("values")) {
					Object values = cardResultMap.get("values");
					if (values instanceof JSONArray) {
						JSONArray valuesList = (JSONArray) values;
						for (int i = 0; i < valuesList.size(); i++) {
							JSONObject val = (JSONObject) valuesList.get(i);
							String name = val.containsKey("name") ? (String) val.get("name")
									: (String) val.get("aggregation");
							variables.add(getVariable(name, name, val));
						}
					} else if (values instanceof List) {
						List valuesList = (List) values;
						for (int i = 0; i < valuesList.size(); i++) {
							Map val = (Map) valuesList.get(i);
							String name = val.containsKey("name") ? (String) val.get("name")
									: (String) val.get("aggregation");
							variables.add(getVariable(name, name, val));
						}
					}
				}
				if (cardResultMap.containsKey("variables")) {
					List cardVariables = (List) cardResultMap.get("variables");
					variables.addAll(cardVariables);
				}

				JSONObject cardParams = cardContext.getCardParams();
				String dateRange = (String) cardParams.get("dateRange");

				if (cardContext.getCardFilters() != null) {// db timeline filters

					JSONObject timeLineFilters = (JSONObject) cardContext.getCardFilters();
					String dateValueString = (String) timeLineFilters.get("dateValueString");

					if (dateValueString != null && !dateValueString.trim().isEmpty()) {
						long startTime = Long.parseLong(dateValueString.split(",")[0]);
						long endTime = Long.parseLong(dateValueString.split(",")[1]);

						variables.add(getVariable("startTime", "Start Time", "DATE_TIME", startTime, null));
						variables.add(getVariable("endTime", "End Time", "DATE_TIME", endTime, null));
					}
				} else if (dateRange != null) {
					DateOperators dateOperator = (DateOperators) DateOperators.getAllOperators().get(dateRange);
					DateRange dateRangeObj = dateOperator.getRange(null);

					variables.add(
							getVariable("startTime", "Start Time", "DATE_TIME", dateRangeObj.getStartTime(), null));
					variables.add(getVariable("endTime", "End Time", "DATE_TIME", dateRangeObj.getEndTime(), null));
				}

				variables
						.add(getVariable("currentTime", "Current Time", "DATE_TIME", System.currentTimeMillis(), null));

				cardResultMap.put("variables", variables);
				return cardResultMap;
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception occurred in get variables", e);
		}
		return cardResult;
	}

	private static JSONObject getVariable(String name, String displayName, String dataType, Object value, String unit) {
		JSONObject variable = new JSONObject();
		variable.put("name", name);
		variable.put("displayName", displayName);
		variable.put("dataType", dataType);
		variable.put("value", value);
		variable.put("unit", unit);
		return variable;
	}

	private static JSONObject getVariable(String name, String displayName, Map value) {
		JSONObject variable = new JSONObject();
		variable.put("name", name);
		variable.put("displayName", displayName);
		if (value != null) {
			variable.putAll(value);
		}
		return variable;
	}

	private static final Map<String, CardLayout> cardLayoutMap = Collections.unmodifiableMap(initCardLayoutMap());

	private static Map<String, CardLayout> initCardLayoutMap() {
		Map<String, CardLayout> cardLayoutMap = new HashMap<>();
		for (CardLayout cardLayout : values()) {
			cardLayoutMap.put(cardLayout.getName(), cardLayout);
		}
		return cardLayoutMap;
	}

	private static final Map<String, String> cardLayoutScriptMap = Collections
			.unmodifiableMap(loadDefaultCardLayouts());

	private static Map<String, String> loadDefaultCardLayouts() {
		Map<String, String> cardLayoutScriptMap = new HashMap<>();
		for (CardLayout cardLayout : values()) {
			try {

				URL scriptFileURL = CardLayout.class.getClassLoader()
						.getResource("conf/cardlayouts/" + cardLayout.getName() + ".fs");
				File scriptFile = (scriptFileURL != null) ? new File(scriptFileURL.getFile()) : null;
				if (scriptFile != null && scriptFile.exists()) {
					String scriptContent = FileUtils.readFileToString(scriptFile, StandardCharsets.UTF_8);
					cardLayoutScriptMap.put(cardLayout.getName(), scriptContent);
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Exception in loading card layout scripts::: ", e);
			}
		}
		return cardLayoutScriptMap;
	}

	public static CardLayout getCardLayout(String layoutName) {
		return cardLayoutMap.get(layoutName);
	}

	public static Map<String, CardLayout> getAllCardLayouts() {
		return cardLayoutMap;
	}

	public static Map<String, Object> getKPIData(Map<String, Object> kpi2, String kpiType,
			WidgetCardContext cardContext) {

		Long kpiId;
		Long parentId;
		String yAggr;

		if (kpi2 instanceof JSONObject) {
			JSONObject kpiConfig = (JSONObject) kpi2;
			kpiId = (Long) kpiConfig.get("kpiId");
			parentId = (Long) kpiConfig.get("parentId");
			yAggr = (String) kpiConfig.get("yAggr");
		} else if (kpi2 instanceof Map) {
			Map<String, Object> kpiConfig = (Map<String, Object>) kpi2;
			kpiId = (Long) kpiConfig.get("kpiId");
			parentId = (Long) kpiConfig.get("parentId");
			yAggr = (String) kpiConfig.get("yAggr");
		} else {
			throw new IllegalStateException();
		}

		Object cardValue = null;
		Object listData = null;
		Object fields = null;
		Object kpi = null;
		String period = null;
		String dateRange = (String) kpi2.get("dateRange");
		JSONArray variables = null;

		if ("module".equalsIgnoreCase(kpiType)) {
			try {
				KPIContext kpiContext = KPIUtil.getKPI(kpiId, false);
				if ((cardContext).getCardUserFilters() != null)// db lookup filters
				{
					FacilioChain generateCriteriaChain = ReadOnlyChainFactory.getGenerateCriteriaFromFilterChain();
					FacilioContext generateCriteriaContext = generateCriteriaChain.getContext();
					generateCriteriaContext.put(FacilioConstants.ContextNames.MODULE_NAME, kpiContext.getModuleName());
					generateCriteriaContext.put(FacilioConstants.ContextNames.FILTERS,
							cardContext.getCardUserFilters());
					generateCriteriaChain.execute();
					Criteria cardFilterCriteria = (Criteria) generateCriteriaContext
							.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
					kpiContext.getCriteria().andCriteria(cardFilterCriteria);
				}

				if (cardContext.getCardFilters() != null) {// db timeline filters

					JSONObject timeLineFilters = (JSONObject) cardContext.getCardFilters();

					kpiContext.setDateOperator(DateOperators.BETWEEN);
					kpiContext.setDateValue((String) timeLineFilters.get("dateValueString"));
					period = (String) timeLineFilters.get("dateLabel");

				} else if (dateRange != null) {// db timeline filters if present override default date period configured
												// in card
					kpiContext.setDateOperator((DateOperators) DateOperators.getAllOperators().get(dateRange));
					period = dateRange;
				}

				if (cardContext.getCardCustomScriptFilters() != null) {
					try {

						Criteria filterCriteria = DashboardFilterUtil.getUserFilterCriteriaForModule(
								cardContext.getCardCustomScriptFilters(), kpiContext.getModule());
						if (filterCriteria != null) {
							kpiContext.getCriteria().andCriteria(filterCriteria);
						}
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE,
								"Error applying custom script filters for widget" + cardContext.getId(), e);
					}

				}

				cardValue = KPIUtil.getKPIValue(kpiContext);

				kpi = kpiContext;
				/*
				 * kpi = KPIUtil.getKPI(kpiId); fields = KPIUtil.getKPIModuleFIelds(kpiContext);
				 * 
				 * listData = KPIUtil.getKPIList(kpiContext, null);
				 * 
				 * if (listData != null) { try { variables = new JSONArray();
				 * 
				 * Map<String, Object> record = (Map<String, Object>) listData;
				 * List<FacilioField> moduleFields = (List<FacilioField>) fields; for
				 * (FacilioField field : moduleFields) { Object value =
				 * record.get(field.getName());
				 * 
				 * variables.add(getVariable(field.getName(), field.getDisplayName(),
				 * field.getDataTypeEnum().name(), value, null)); } } catch (Exception e) {
				 * e.printStackTrace(); } }
				 */
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.WARNING, "Exception in getKPIValue::: ", e);
			}
		} else if ("reading".equalsIgnoreCase(kpiType)) {
			try {
				if(kpi2.containsKey("isNewKpi") && (boolean) kpi2.get("isNewKpi")){
					cardValue = ReadingKpiAPI.getCurrentValueOfKpi(kpiId, parentId);
				}
				else{
					cardValue = FormulaFieldAPI.getFormulaCurrentValue(kpiId, parentId);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.WARNING, "Exception in get KPI Reading Value::: ", e);
			}
		}

		JSONObject jobj = new JSONObject();
		jobj.put("value", cardValue);
		jobj.put("unit", null);
		jobj.put("kpi", kpi);
		jobj.put("moduleData", listData);
		jobj.put("fields", fields);

		return jobj;
	}
}
