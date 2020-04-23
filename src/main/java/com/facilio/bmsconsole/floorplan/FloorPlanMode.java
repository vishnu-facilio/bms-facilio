package com.facilio.bmsconsole.floorplan;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.cards.util.CardLayout;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControllableAssetCategoryContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum FloorPlanMode {

	DEFAULT("default"),
	SPACE_CATEGORY("space_category"),
	RESERVATION("reservation"),
	ASSET("asset"),
	CONTROL_POINTS("control_points") {
		@Override
		public Object execute(FloorPlanViewContext viewModeContext) throws Exception {
			
			FloorPlanContext floorPlanContext = new FloorPlanContext();
			floorPlanContext.setId(viewModeContext.getFloorPlanId());
			
			FacilioChain c = TransactionChainFactory.getFloorPlanChain();
			c.getContext().put(FacilioConstants.ContextNames.FLOOR_PLAN, floorPlanContext);
			c.execute();
			floorPlanContext = (FloorPlanContext) c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLAN);
			
			List<SpaceContext> spaces = SpaceAPI.getAllSpaces(floorPlanContext.getFloorId());
			
			JSONArray areas = new JSONArray();
			if (spaces != null && !spaces.isEmpty()) {
				for (SpaceContext space : spaces) {
					
					FacilioChain getControllableCategoryChain = ReadOnlyChainFactory.getControllableCategoryFromSpaceIdChain();
					
					FacilioContext context1 = getControllableCategoryChain.getContext();
					
					context1.put(FacilioConstants.ContextNames.SPACE_ID, space.getId());
					
					getControllableCategoryChain.execute();
					
					Map<Long,ControllableAssetCategoryContext> controllableAssetCategoryMap = (Map<Long,ControllableAssetCategoryContext>) context1.get(ControlActionUtil.CONTROLLABLE_CATEGORIES);
					
					JSONArray markers = new JSONArray();
					if (controllableAssetCategoryMap != null) {
						Iterator<Long> itr = controllableAssetCategoryMap.keySet().iterator();
						while (itr.hasNext()) {
							Long key = itr.next();
							ControllableAssetCategoryContext controlCategory = controllableAssetCategoryMap.get(key);
							
							JSONObject marker = new JSONObject();
							marker.put("title", controlCategory.getControlTypeEnum().getName());
							marker.put("data", controlCategory);
							
							if (controlCategory.getControlTypeEnum() == ControllableAssetCategoryContext.ControllableCategory.AC) {
								marker.put("type", FloorPlanMarkerType.CONTROL_TEMPERATURE.getName());
							}
							if (controlCategory.getControlTypeEnum() == ControllableAssetCategoryContext.ControllableCategory.LIGHT) {
								marker.put("type", FloorPlanMarkerType.CONTROL_LIGHT.getName());
							}
							
							markers.add(marker);
						}
					}
					
					JSONObject area = new JSONObject();
					area.put("label", space.getName());
					area.put("spaceId", space.getId());
					area.put("markers", markers);
					areas.add(area);
				}
			}
			
			JSONObject floorPlan = new JSONObject();
			floorPlan.put("areas", areas);
			return floorPlan;
		}
	},
	HEATMAP("heatmap"),
	ALARMS("alarms"),
	MAINTENANCE("maintenance"),
	PEOPLE("people");
	
	private static final Logger LOGGER = Logger.getLogger(FloorPlanMode.class.getName());
	
	private String name;
	
	private FloorPlanMode (String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	private JSONObject getFunctionParams(FloorPlanViewContext viewContext) throws Exception {
		
		FloorPlanContext floorPlanContext = new FloorPlanContext();
		floorPlanContext.setId(viewContext.getFloorPlanId());
		
		FacilioChain c = TransactionChainFactory.getFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.FLOOR_PLAN, floorPlanContext);
		c.execute();
		floorPlanContext = (FloorPlanContext) c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLAN);
		
		JSONObject params = new JSONObject();
		params.put("floorId", floorPlanContext.getFloorId());
		params.put("viewParams", viewContext.getViewParams());
		
		List<SpaceContext> spaces = SpaceAPI.getAllSpaces(floorPlanContext.getFloorId());
		if (spaces != null) {
			List<Long> spaceIds = spaces.stream().map(SpaceContext::getId).collect(Collectors.toList());
			params.put("spaceId", spaceIds);
		}
		return params;
	}
	
	protected Object execute(FloorPlanViewContext viewContext) throws Exception {
		
		if (floorPlanModeScriptMap.containsKey(this.name)) {
			
			WorkflowContext workflow = new WorkflowContext();
			workflow.setIsV2Script(true);
			
			List<Object> paramsList = new ArrayList<Object>();
			paramsList.add(getFunctionParams(viewContext));
			
			workflow.setWorkflowV2String(floorPlanModeScriptMap.get(this.name));
			
			FacilioChain workflowChain = TransactionChainFactory.getExecuteWorkflowChain();
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);
			
			workflowChain.execute();
			
			return workflow.getReturnValue();
		}
		return null;
	}
	
	public Object getResult(FloorPlanViewContext viewContext) throws Exception {
		
		if (viewContext.getScriptMode() == FloorPlanViewContext.ScriptMode.CUSTOM_SCRIPT) {
			
			WorkflowContext workflow = new WorkflowContext();
			workflow.setIsV2Script(true);
			
			List<Object> paramsList = new ArrayList<Object>();
			paramsList.add(getFunctionParams(viewContext));
			
			if (viewContext.getCustomScript() != null) {
				workflow.setWorkflowV2String(viewContext.getCustomScript());
			}
			else if (viewContext.getCustomScriptId() != null) {
				workflow = WorkflowUtil.getWorkflowContext(viewContext.getCustomScriptId());
				viewContext.setCustomScript(workflow.getWorkflowV2String());
			}
			
			FacilioChain workflowChain = TransactionChainFactory.getExecuteWorkflowChain();
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			workflowChain.getContext().put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);
			
			workflowChain.execute();
			
			return workflow.getReturnValue();
		}
		else {
			return this.execute(viewContext);
		}
	}
	
	private static final Map<String, FloorPlanMode> floorPlanModeMap = Collections.unmodifiableMap(initFloorPlanModeMap());
	private static Map<String, FloorPlanMode> initFloorPlanModeMap() {
		Map<String, FloorPlanMode> floorPlanModeMap = new HashMap<>();
		for (FloorPlanMode floorPlanMode : values()) {
			floorPlanModeMap.put(floorPlanMode.getName(), floorPlanMode);
		}
		return floorPlanModeMap;
	}
	private static final Map<String, String> floorPlanModeScriptMap = Collections.unmodifiableMap(loadDefaultFloorPlanModes());
	private static Map<String, String> loadDefaultFloorPlanModes() {
		Map<String, String> floorPlanModeScriptMap = new HashMap<>();
		for (FloorPlanMode floorPlanMode : values()) {
			try {
		
				URL scriptFileURL = CardLayout.class.getClassLoader().getResource("conf/floorplanmode/" + floorPlanMode.getName() + ".fs");
				File scriptFile = (scriptFileURL != null) ? new File(scriptFileURL.getFile()) : null;
				if (scriptFile != null && scriptFile.exists()) {
					String scriptContent = FileUtils.readFileToString(scriptFile, StandardCharsets.UTF_8);
					floorPlanModeScriptMap.put(floorPlanMode.getName(), scriptContent);
				}
			}
			catch (Exception e) {
				LOGGER.log(Level.WARNING, "Exception in loading floor plan mode scripts::: ", e);
			}
		}
		return floorPlanModeScriptMap;
	}
	
	public static FloorPlanMode getFloorPlanMode(String modeName) {
		return floorPlanModeMap.get(modeName);
	}
	
	public static Map<String, FloorPlanMode> getAllFloorPlanModes() {
		return floorPlanModeMap;
	}
}
