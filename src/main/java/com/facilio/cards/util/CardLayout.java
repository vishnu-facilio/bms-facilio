package com.facilio.cards.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public enum CardLayout {
	
	READINGCARD_LAYOUT_1 ("readingcard_layout_1"),
	READINGCARD_LAYOUT_2 ("readingcard_layout_2"),
	READINGCARD_LAYOUT_3 ("readingcard_layout_3"),
	READINGCARD_LAYOUT_4 ("readingcard_layout_4"),
	READINGCARD_LAYOUT_5 ("readingcard_layout_5"),
	READINGCARD_LAYOUT_6 ("readingcard_layout_6"),
	GAUGE_LAYOUT_1 ("gauge_layout_1"),
	GAUGE_LAYOUT_2 ("gauge_layout_2"),
	GAUGE_LAYOUT_3 ("gauge_layout_3"),
	GAUGE_LAYOUT_4 ("gauge_layout_4"),
	GAUGE_LAYOUT_5 ("gauge_layout_5"),
	ENERGYCARD_LAYOUT_1 ("energycard_layout_1"),
	ENERGYCOST_LAYOUT_1 ("energycost_layout_1"),
	CARBONCARD_LAYOUT_1 ("carboncard_layout_1"),
	WEATHERCARD_LAYOUT_1 ("weathercard_layout_1"),
	GRAPHICALCARD_LAYOUT_1 ("graphicalcard_layout_1"),
	MAPCARD_LAYOUT_1 ("mapcard_layout_1"),
	CONTROL_LAYOUT_1 ("controlcard_layout_1"),
	WEB_LAYOUT_1 ("web_layout_1"),

	
	PMREADINGS_LAYOUT_1 ("pmreadings_layout_1") {
		@Override
		public Object execute(WidgetCardContext cardContext) throws Exception {
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

			Collection<WorkOrderContext> workOrderContexts = (Collection<WorkOrderContext>) context.get(ContextNames.RESULT);

			JSONObject returnValue = new JSONObject();
			returnValue.put("title", title);
			returnValue.put("value", workOrderContexts);
			
			return returnValue;
		}
	},
	
	KPICARD_LAYOUT_1 ("kpicard_layout_1") {
		@Override
		public Object execute(WidgetCardContext cardContext) throws Exception {
			JSONObject cardParams = cardContext.getCardParams();
			
			String title = (String) cardParams.get("title");
			String kpiType = (String) cardParams.get("kpiType");
			String dateRange = (String) cardParams.get("dateRange");
			String dateField = (String) cardParams.get("dateField");
			 
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
			
			if ("module".equalsIgnoreCase(kpiType)) {
				try {
					KPIContext kpiContext = KPIUtil.getKPI(kpiId, false);
					if (dateRange != null) {
						kpiContext.setDateOperator((DateOperators) DateOperators.getAllOperators().get(dateRange));
					}
					cardValue = KPIUtil.getKPIValue(kpiContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.log(Level.WARNING, "Exception in getKPIValue::: ", e);
				}
			}
			else if ("reading".equalsIgnoreCase(kpiType)) {
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
			
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", title);
			returnValue.put("value", jobj);
			
			return returnValue;
		}
	},
	KPICARD_LAYOUT_2 ("kpicard_layout_2") {
		@Override
		public Object execute(WidgetCardContext cardContext) throws Exception {
			JSONObject cardParams = cardContext.getCardParams();

			String title = (String) cardParams.get("title");
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
			
			if ("module".equalsIgnoreCase(kpiType)) {
				try {
					KPIContext kpiContext = KPIUtil.getKPI(kpiId, false);
					if (dateRange != null) {
						kpiContext.setDateOperator((DateOperators) DateOperators.getAllOperators().get(dateRange));
					}
					cardValue = KPIUtil.getKPIValue(kpiContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.log(Level.WARNING, "Exception in getKPIValue::: ", e);
				}
			}
			else if ("reading".equalsIgnoreCase(kpiType)) {
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
			
			JSONObject returnValue = new JSONObject();
			returnValue.put("title", title);
			returnValue.put("value", jobj);
			returnValue.put("baselineValue", jobj);
			returnValue.put("period", dateRange);
			returnValue.put("baselinePeriod", baselineRange);

			
			return returnValue;
		}
	},
	PHOTOS_LAYOUT_1("photos_layout_1") {
		@Override
		public Object execute(WidgetCardContext cardContext) throws Exception {
			JSONObject cardParams = cardContext.getCardParams();

			String title = (String) cardParams.get("title");
			String moduleName = (String) cardParams.get("moduleName");
			List<String> modulePhotosList = (List<String>) cardParams.get("photosModuleList");
			JSONObject criteriaObj = (JSONObject) cardParams.get("criteria");
			Criteria criteria = FieldUtil.getAsBeanFromMap(criteriaObj, Criteria.class);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> allFields = modBean.getAllFields(moduleName);
			List<FacilioField> photoFields = allFields.stream().filter(field -> field.getDataTypeEnum() != null && field.getDataType() == FieldType.FILE.getTypeAsInt()).collect(Collectors.toList());
			Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
			if (beanClassName == null) {
				if (module.isCustom()) {
					beanClassName = CustomModuleData.class;
				}
				else {
					beanClassName = ModuleBaseWithCustomFields.class;
				}
			}
			List<FacilioField> fields = new ArrayList<>();
			fields.add(FieldFactory.getIdField(module));
			fields.add(modBean.getPrimaryField(moduleName));
			if (CollectionUtils.isNotEmpty(photoFields)) {
				fields.addAll(photoFields);
			}

			SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> selectRecordBuilder = new SelectRecordsBuilder<>()
					.module(module)
					.beanClass(beanClassName)
					.select(fields)
					;
			if (criteria != null) {
				selectRecordBuilder.andCriteria(criteria);
			}

			List<? extends ModuleBaseWithCustomFields> records = selectRecordBuilder.get();
			List<Long> recordIds;
			List<JSONObject> submoduleVsRecords = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(records)) {
				recordIds = records.stream().map(record -> record.getId()).collect(Collectors.toList());

				if (CollectionUtils.isNotEmpty(modulePhotosList)) {
					for (String subModuleName : modulePhotosList) {
						FacilioModule subModule = modBean.getModule(subModuleName);
						Class className = FacilioConstants.ContextNames.getClassFromModule(subModule);
						if (className == null) {
							className = AttachmentContext.class;
						}
						List<FacilioField> subModuleFields = modBean.getAllFields(subModuleName);
						Map<String, FacilioField> subModuleFieldsMap = FieldFactory.getAsMap(subModuleFields);
						SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> attachmentBuilder = new SelectRecordsBuilder<>();
						if (subModuleName.equals(ContextNames.TASK_ATTACHMENTS)) {
							List<TaskContext> tasks = TicketAPI.getRelatedTasks(recordIds,false,false);
							List<Long> taskIds = tasks.stream().map(task -> task.getId()).collect(Collectors.toList());
							attachmentBuilder
									.select(subModuleFields)
									.module(subModule)
									.beanClass(className)
									.andCondition(CriteriaAPI.getCondition(subModuleFieldsMap.get("parentId"), taskIds, PickListOperators.IS));
						} else {
							attachmentBuilder
									.select(subModuleFields)
									.module(subModule)
									.beanClass(className)
									.andCondition(CriteriaAPI.getCondition(subModuleFieldsMap.get("parentId"), recordIds, PickListOperators.IS));
						}
						List <? extends ModuleBaseWithCustomFields> subModuleRecords = attachmentBuilder.get();
						if (CollectionUtils.isNotEmpty(subModuleRecords)) {
							JSONObject subModuleHolder = new JSONObject();
							subModuleHolder.put("records", subModuleRecords);
							subModuleHolder.put("displayName", subModule.getDisplayName());
							subModuleHolder.put("moduleName", subModuleName);
							submoduleVsRecords.add(subModuleHolder);
						}
					}
				}
			}


			JSONObject returnValue = new JSONObject();
			returnValue.put("title", title);
			returnValue.put("parentModuleRecords", records);
			returnValue.put("subModuleVsRecords", submoduleVsRecords);
			return returnValue;
		}
	};
	
	private static final Logger LOGGER = Logger.getLogger(CardLayout.class.getName());
			
	private String name;
	
	private CardLayout (String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Object execute(WidgetCardContext cardContext) throws Exception {
		
		WorkflowContext workflow = new WorkflowContext();
		workflow.setIsV2Script(true);
		
		List<Object> paramsList = new ArrayList<Object>();
		paramsList.add(cardContext.getCardParams());
		
		if (cardContext.getScriptMode() == WidgetCardContext.ScriptMode.CUSTOM_SCRIPT) {
			if (cardContext.getCustomScript() != null) {
				workflow.setWorkflowV2String(cardContext.getCustomScript());
			}
			else if (cardContext.getCustomScriptId() != null) {
				workflow = WorkflowUtil.getWorkflowContext(cardContext.getCustomScriptId());
				cardContext.setCustomScript(workflow.getWorkflowV2String());
			}
		}
		else if (cardLayoutScriptMap.containsKey(this.name)) {
			workflow.setWorkflowV2String(cardLayoutScriptMap.get(this.name));			
		}
		
		FacilioChain workflowChain = TransactionChainFactory.getExecuteWorkflowChain();
		workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);
		
		workflowChain.execute();
		
		return workflow.getReturnValue();
	}
	
	private static final Map<String, CardLayout> cardLayoutMap = Collections.unmodifiableMap(initCardLayoutMap());
	private static Map<String, CardLayout> initCardLayoutMap() {
		Map<String, CardLayout> cardLayoutMap = new HashMap<>();
		for (CardLayout cardLayout : values()) {
			cardLayoutMap.put(cardLayout.getName(), cardLayout);
		}
		return cardLayoutMap;
	}
	private static final Map<String, String> cardLayoutScriptMap = Collections.unmodifiableMap(loadDefaultCardLayouts());
	private static Map<String, String> loadDefaultCardLayouts() {
		Map<String, String> cardLayoutScriptMap = new HashMap<>();
		for (CardLayout cardLayout : values()) {
			try {
		
				URL scriptFileURL = CardLayout.class.getClassLoader().getResource("conf/cardlayouts/" + cardLayout.getName() + ".fs");
				File scriptFile = (scriptFileURL != null) ? new File(scriptFileURL.getFile()) : null;
				if (scriptFile != null && scriptFile.exists()) {
					String scriptContent = FileUtils.readFileToString(scriptFile, StandardCharsets.UTF_8);
					cardLayoutScriptMap.put(cardLayout.getName(), scriptContent);
				}
			}
			catch (Exception e) {
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
}
