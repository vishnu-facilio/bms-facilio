package com.facilio.bmsconsoleV3.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.timelineview.context.TimelineScheduledViewContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.actions.picklist.PickListUtil;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.timeline.context.TimelineRequest;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.TimelineViewUtil;
import com.facilio.v3.util.V3Util;
import com.facilio.ims.handler.AuditLogHandler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Getter @Setter
public class TimelineAction extends RESTAPIHandler {
	private List<String> localSearchDisabled = Arrays.asList(FacilioConstants.ContextNames.USERS, FacilioConstants.ContextNames.REQUESTER,
			FacilioConstants.ContextNames.GROUPS, FacilioConstants.ContextNames.ROLE, FacilioConstants.ContextNames.READING_RULE_MODULE);
	private List<Integer> SPACE_RESOURCE_TYPE = new ArrayList<Integer>(){{
		add(V3ResourceContext.ResourceType.SPACE.getValue());
		add(V3ResourceContext.ResourceType.ASSET.getValue());
	}};

	public String fetchTimelineGroupdata() throws Exception {
		FacilioChain getViewChain = FacilioChainFactory.getViewDetailsChain();
		FacilioContext context = getViewChain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, timelineModuleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, timelineViewName);
		context.put(FacilioConstants.ContextNames.APP_ID, appId);
		getViewChain.execute();

		FacilioView viewObj = (FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(viewObj == null || viewObj.getType() != FacilioView.ViewType.TIMELINE.getIntVal()) {
			throw new IllegalArgumentException("Invalid View data passed");
		}
		TimelineViewContext timelineViewObj = (TimelineViewContext) viewObj;

		FacilioField groupByField = ((TimelineViewContext)viewObj).getGroupByField();
		if(groupByField == null) {
			throw new IllegalArgumentException("Group field not configured for the view");
		}
		if(groupByField instanceof LookupField) {
			FacilioModule module = null;
			LookupField lookupField = (LookupField) timelineViewObj.getGroupByField();
			FacilioModule lookupModule = lookupField.getLookupModule();
			long groupByChildModuleId = timelineViewObj.getGroupByChildModuleId();

			if(groupByChildModuleId > 0) {
				module = timelineViewObj.getChildModule();
				List<Long> extendedModuleIds = module.getExtendedModuleIds();
				if(CollectionUtils.isEmpty(extendedModuleIds) || !extendedModuleIds.contains(lookupModule.getModuleId())) {
					throw new IllegalArgumentException("Invalid Grouping by ChildModuleId");
				}
			} else {
				module = lookupModule;
			}
			setModuleName(module.getName());
			if (LookupSpecialTypeUtil.isSpecialType(getModuleName())) {
				setData(FacilioConstants.ContextNames.PICKLIST, PickListUtil.getSpecialModulesPickList(getModuleName(), getPage(), getPerPage(), getSearch(), getFilters(), null, timelineViewObj.getGroupCriteria(), getOrderBy(), getOrderType()));
				setMeta("moduleType", FacilioModule.ModuleType.PICK_LIST.name());
				setMeta("localSearch", !localSearchDisabled.contains(getModuleName()));
			} else {
				FacilioContext pickListContext = new FacilioContext();
				pickListContext.put(FacilioConstants.ContextNames.IS_TO_FETCH_DECOMMISSIONED_RESOURCE, BooleanUtils.isFalse(isToFetchDecommissionedResource)? isToFetchDecommissionedResource : true);
				PickListUtil.populatePicklistContext(pickListContext, getModuleName(), getFilters(), getSearch(), getCriteria(), getClientCriteria(), getDefault(), getViewName(), getPage(), getPerPage());

				Criteria serverCriteria = new Criteria();

				if(!(groupByChildModuleId > 0) && lookupField.getLookupModule() != null && lookupField.getLookupModule().getName().equals(FacilioConstants.ContextNames.RESOURCE)) {
					Criteria resourceTypeCriteria = new Criteria();
					resourceTypeCriteria.addAndCondition(CriteriaAPI.getCondition("RESOURCE_TYPE", "resourceType", StringUtils.join(SPACE_RESOURCE_TYPE, ","), NumberOperators.EQUALS));

					serverCriteria.andCriteria(resourceTypeCriteria);
				}

				if (lookupField.getLookupModule() != null && lookupField.getLookupModule().getName().equals("ticketstatus")) {
					Criteria ticketStatusCriteria = new Criteria();
					ModuleBean moduleBean = Constants.getModBean();
					FacilioModule parentModule = moduleBean.getModule(getTimelineModuleName());
					FacilioField parentModuleIdField = moduleBean.getField("parentModuleId", "ticketstatus");
					ticketStatusCriteria.addAndCondition(CriteriaAPI.getCondition(parentModuleIdField, String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS));

					serverCriteria.andCriteria(ticketStatusCriteria);
				}

				if (timelineViewObj.getGroupCriteriaId() > 0) {
					serverCriteria.andCriteria(timelineViewObj.getGroupCriteria());
				}

				if (!serverCriteria.isEmpty()) {
					pickListContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, serverCriteria);
				}
				if (getOrderType() != null) {
					ModuleBean moduleBean = Constants.getModBean();
					FacilioField primaryField = moduleBean.getPrimaryField(getModuleName());
					if (primaryField == null) {
						primaryField = FieldFactory.getIdField(moduleBean.getModule(getModuleName()));
					}
					JSONObject sorting = new JSONObject();
					sorting.put("orderBy", primaryField.getCompleteColumnName());
					sorting.put("orderType", getOrderType());
					pickListContext.put(FacilioConstants.ContextNames.SORTING, sorting);
				}
				pickListContext = PickListUtil.fetchPickListData(pickListContext);

				setData(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
				setMeta("moduleType", ((FacilioModule) pickListContext.get(FacilioConstants.ContextNames.MODULE)).getTypeEnum().name());
				setMeta("localSearch", pickListContext.getOrDefault(FacilioConstants.PickList.LOCAL_SEARCH, false));
			}
		}
		else if(groupByField instanceof EnumField) {
			FacilioChain pickListChain = ReadOnlyChainFactory.getEnumFieldPickListChain();
			FacilioContext pickListContext = pickListChain.getContext();
			pickListContext.put(FacilioConstants.ContextNames.ID, groupByField.getFieldId());
			pickListContext.put(FacilioConstants.ContextNames.PAGE, getPage());
			pickListContext.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
			pickListContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, timelineViewObj.getGroupCriteria());
			if (getSearch() != null) {
				pickListContext.put(FacilioConstants.ContextNames.SEARCH, getSearch());
			}
			if (getOrderType() != null) {
				JSONObject sorting = new JSONObject();
				FacilioField valueField = FieldFactory.getStringField("value", "VAL", ModuleFactory.getEnumFieldValuesModule());
				sorting.put("orderBy", valueField.getCompleteColumnName());
				sorting.put("orderType", getOrderType());
				pickListContext.put(FacilioConstants.ContextNames.SORTING, sorting);
			}
			pickListChain.execute();
			setData(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
			setMeta("localSearch", pickListContext.getOrDefault(FacilioConstants.PickList.LOCAL_SEARCH, false));
		}

		return SUCCESS;
	}
	public String fetchTimelineScheduledViewGroupData() throws Exception {
		FacilioChain getViewChain = FacilioChainFactory.getViewDetailsChain();
		FacilioContext context = getViewChain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, timelineModuleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, timelineViewName);
		context.put(FacilioConstants.ContextNames.APP_ID, appId);
		getViewChain.execute();

		FacilioView viewObj = (FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(viewObj == null || !viewObj.isTimelineView()) {
			throw new IllegalArgumentException("Invalid View data passed");
		}
		TimelineScheduledViewContext timelineViewObj = viewObj.getTimelineScheduledViewContext();

		FacilioField groupByField = timelineViewObj.getGroupByField();
		if(groupByField == null) {
			throw new IllegalArgumentException("Group field not configured for the view");
		}
		if(groupByField instanceof LookupField) {
			FacilioModule module = null;
			LookupField lookupField = (LookupField) timelineViewObj.getGroupByField();
			FacilioModule lookupModule = lookupField.getLookupModule();
			long groupByChildModuleId = timelineViewObj.getGroupByChildModuleId();

			if(groupByChildModuleId > 0) {
				module = timelineViewObj.getChildModule();
				List<Long> extendedModuleIds = module.getExtendedModuleIds();
				if(CollectionUtils.isEmpty(extendedModuleIds) || !extendedModuleIds.contains(lookupModule.getModuleId())) {
					throw new IllegalArgumentException("Invalid Grouping by ChildModuleId");
				}
			} else {
				module = lookupModule;
			}
			setModuleName(module.getName());
			if (LookupSpecialTypeUtil.isSpecialType(getModuleName())) {
				setData(FacilioConstants.ContextNames.PICKLIST, PickListUtil.getSpecialModulesPickList(getModuleName(), getPage(), getPerPage(), getSearch(), getFilters(), null, timelineViewObj.getGroupCriteria(), getOrderBy(), getOrderType()));
				setMeta("moduleType", FacilioModule.ModuleType.PICK_LIST.name());
				setMeta("localSearch", !localSearchDisabled.contains(getModuleName()));
			} else {
				FacilioContext pickListContext = new FacilioContext();
				pickListContext.put(FacilioConstants.ContextNames.IS_TO_FETCH_DECOMMISSIONED_RESOURCE, BooleanUtils.isFalse(isToFetchDecommissionedResource)? isToFetchDecommissionedResource : true);
				PickListUtil.populatePicklistContext(pickListContext, getModuleName(), getFilters(), getSearch(), getCriteria(), getClientCriteria(), getDefault(), getViewName(), getPage(), getPerPage());

				Criteria serverCriteria = new Criteria();

				if(!(groupByChildModuleId > 0) && lookupField.getLookupModule() != null && lookupField.getLookupModule().getName().equals(FacilioConstants.ContextNames.RESOURCE)) {
					Criteria resourceTypeCriteria = new Criteria();
					resourceTypeCriteria.addAndCondition(CriteriaAPI.getCondition("RESOURCE_TYPE", "resourceType", StringUtils.join(SPACE_RESOURCE_TYPE, ","), NumberOperators.EQUALS));

					serverCriteria.andCriteria(resourceTypeCriteria);
				}
				if (lookupField.getLookupModule() != null && lookupField.getLookupModule().getName().equals("ticketstatus")) {
					Criteria ticketStatusCriteria = new Criteria();
					ModuleBean moduleBean = Constants.getModBean();
					FacilioModule parentModule = moduleBean.getModule(getTimelineModuleName());
					FacilioField parentModuleIdField = moduleBean.getField("parentModuleId", "ticketstatus");
					ticketStatusCriteria.addAndCondition(CriteriaAPI.getCondition(parentModuleIdField, String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS));

					serverCriteria.andCriteria(ticketStatusCriteria);
				}

				if (timelineViewObj.getGroupCriteriaId() > 0) {
					serverCriteria.andCriteria(timelineViewObj.getGroupCriteria());
				}

				if (!serverCriteria.isEmpty()) {
					pickListContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, serverCriteria);
				}
				if (getOrderType() != null) {
					ModuleBean moduleBean = Constants.getModBean();
					FacilioField primaryField = moduleBean.getPrimaryField(getModuleName());
					if (primaryField == null) {
						primaryField = FieldFactory.getIdField(moduleBean.getModule(getModuleName()));
					}
					JSONObject sorting = new JSONObject();
					sorting.put("orderBy", primaryField.getCompleteColumnName());
					sorting.put("orderType", getOrderType());
					pickListContext.put(FacilioConstants.ContextNames.SORTING, sorting);
				}
				pickListContext = PickListUtil.fetchPickListData(pickListContext);

				setData(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
				setMeta("moduleType", ((FacilioModule) pickListContext.get(FacilioConstants.ContextNames.MODULE)).getTypeEnum().name());
				setMeta("localSearch", pickListContext.getOrDefault(FacilioConstants.PickList.LOCAL_SEARCH, false));
			}
		}
		else if(groupByField instanceof EnumField) {
			FacilioChain pickListChain = ReadOnlyChainFactory.getEnumFieldPickListChain();
			FacilioContext pickListContext = pickListChain.getContext();
			pickListContext.put(FacilioConstants.ContextNames.ID, groupByField.getFieldId());
			pickListContext.put(FacilioConstants.ContextNames.PAGE, getPage());
			pickListContext.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
			pickListContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, timelineViewObj.getGroupCriteria());
			if (getSearch() != null) {
				pickListContext.put(FacilioConstants.ContextNames.SEARCH, getSearch());
			}
			if (getOrderType() != null) {
				JSONObject sorting = new JSONObject();
				FacilioField valueField = FieldFactory.getStringField("value", "VAL", ModuleFactory.getEnumFieldValuesModule());
				sorting.put("orderBy", valueField.getCompleteColumnName());
				sorting.put("orderType", getOrderType());
				pickListContext.put(FacilioConstants.ContextNames.SORTING, sorting);
			}
			pickListChain.execute();
			setData(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
			setMeta("localSearch", pickListContext.getOrDefault(FacilioConstants.PickList.LOCAL_SEARCH, false));
		}

		return SUCCESS;
	}

	public String timelineData() throws Exception {
		FacilioChain chain = ChainUtil.getTimelineChain();
		FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest());
		chain.execute();
		setData(FacilioConstants.ContextNames.TIMELINE_DATA, context.get(FacilioConstants.ContextNames.TIMELINE_DATA));
		return SUCCESS;
	}
	public String timelineScheduledViewData() throws Exception {
		FacilioChain chain = ChainUtil.getTimelineScheduledViewChain();
		FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest());
		chain.execute();
		setData(FacilioConstants.ContextNames.TIMELINE_DATA, context.get(FacilioConstants.ContextNames.TIMELINE_DATA));
		return SUCCESS;
	}

	public String timelinePatch() throws Exception {

		FacilioChain validationChain = ChainUtil.getTimelinePatchValidationChain();
		FacilioContext validationContext = validationChain.getContext();
		validationContext.put(FacilioConstants.ContextNames.CV_NAME, this.getViewName());
		validationContext.put(FacilioConstants.ContextNames.MODULE_NAME, this.getModuleName());
		validationContext.put(FacilioConstants.ContextNames.DATA, this.getData());
		Object oldRecord = V3Util.getRecord(this.getModuleName(), this.getId(), this.getQueryParameters());
		validationContext.put(FacilioConstants.ContextNames.OLD_RECORD_MAP, oldRecord);
		validationChain.execute();

		//removing permission restricted fields
		Map<String, Object> data = this.getData();
		if (data == null) {
			data = new HashMap<>();
		}
		removeRestrictedFields(data, this.getModuleName(), true);

		JSONObject result = V3Util.processAndUpdateSingleRecord(this.getModuleName(), this.getId(), data, this.getParams(), this.getQueryParameters(), this.getStateTransitionId(), this.getCustomButtonId(), this.getApprovalTransitionId(), this.getQrValue(), this.getLocationValue(), this.getCurrentLocation(),null);

		String message = "Record {%s} of module %s has been "
				+ (validationContext.containsKey(FacilioConstants.ContextNames.TIMELINE_PATCHTYPE) ? validationContext.get(FacilioConstants.ContextNames.TIMELINE_PATCHTYPE): "updated")
				+" through TimelineView";
		addAuditLog(Collections.singletonList((JSONObject)result.get(getModuleName())), getModuleName(), message,
				AuditLogHandler.ActionType.UPDATE, true);

		if(this.getTimelineRequest() != null) {
			FacilioChain chain = ChainUtil.getTimelineChain();
			FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest());
			chain.execute();
			setData(new JSONObject());
			setData(FacilioConstants.ContextNames.TIMELINE_DATA, context.get(FacilioConstants.ContextNames.TIMELINE_DATA));
		}

		return SUCCESS;
	}

	public String timelineScheduledViewPatch() throws Exception {

		FacilioChain validationChain = ChainUtil.getTimelineScheduledViewPatchValidationChain();
		FacilioContext validationContext = validationChain.getContext();
		validationContext.put(FacilioConstants.ContextNames.CV_NAME, this.getViewName());
		validationContext.put(FacilioConstants.ContextNames.MODULE_NAME, this.getModuleName());
		validationContext.put(FacilioConstants.ContextNames.DATA, this.getData());
		Object oldRecord = V3Util.getRecord(this.getModuleName(), this.getId(), this.getQueryParameters());
		validationContext.put(FacilioConstants.ContextNames.OLD_RECORD_MAP, oldRecord);
		validationChain.execute();

		//removing permission restricted fields
		Map<String, Object> data = this.getData();
		if (data == null) {
			data = new HashMap<>();
		}
		removeRestrictedFields(data, this.getModuleName(), true);

		JSONObject result = V3Util.processAndUpdateSingleRecord(this.getModuleName(), this.getId(), data, this.getParams(),
				this.getQueryParameters(), this.getStateTransitionId(), this.getCustomButtonId(), this.getApprovalTransitionId(),
				this.getQrValue(), this.getLocationValue(), this.getCurrentLocation(),null);

		String message = "Record {%s} of module %s has been "
				+ (validationContext.containsKey(FacilioConstants.ContextNames.TIMELINE_PATCHTYPE) ? validationContext.get(FacilioConstants.ContextNames.TIMELINE_PATCHTYPE): "updated")
				+" through TimelineView";
		addAuditLog(Collections.singletonList((JSONObject)result.get(getModuleName())), getModuleName(), message,
				AuditLogHandler.ActionType.UPDATE, true);

		if(this.getTimelineRequest() != null) {
			FacilioChain chain = ChainUtil.getTimelineScheduledViewChain();
			FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest());
			chain.execute();
			setData(new JSONObject());
			setData(FacilioConstants.ContextNames.TIMELINE_DATA, context.get(FacilioConstants.ContextNames.TIMELINE_DATA));
		}

		return SUCCESS;
	}

	public String timelineSelectiveList() throws Exception {
		FacilioChain chain = ChainUtil.getTimelineListChain();
		FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest(), true,
				this.getPage(), this.getPerPage(), false);
		chain.execute();
		setData(timelineRequest.getModuleName(), context.get(FacilioConstants.ContextNames.TIMELINE_CUSTOMIZATONDATA_MAP));
		return SUCCESS;
	}
	public String timelineScheduledViewSelectiveList() throws Exception {
		FacilioChain chain = ChainUtil.getTimelineScheduledViewListChain();
		FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest(), true,
				this.getPage(), this.getPerPage(), false);
		chain.execute();
		setData(timelineRequest.getModuleName(), context.get(FacilioConstants.ContextNames.TIMELINE_CUSTOMIZATONDATA_MAP));
		return SUCCESS;
	}

	public  String list(boolean getUnscheduled) throws Exception{
		FacilioChain chain = ChainUtil.getTimelineListChain();
		FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest(), true,
				this.getPage(), this.getPerPage(), getUnscheduled);
		chain.execute();
		setData(timelineRequest.getModuleName(), context.get(FacilioConstants.ContextNames.TIMELINE_CUSTOMIZATONDATA_MAP));
		return SUCCESS;
	}

	public String timelineUnScheduledList() throws Exception {
		FacilioChain chain = ChainUtil.getTimelineListChain();
		FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest(), true,
				this.getPage(), this.getPerPage(), true);
		chain.execute();
		setData(timelineRequest.getModuleName(), context.get(FacilioConstants.ContextNames.TIMELINE_CUSTOMIZATONDATA_MAP));
		return SUCCESS;
	}

	public String timelineScheduledViewUnScheduledList() throws Exception {
		FacilioChain chain = ChainUtil.getTimelineScheduledViewListChain();
		FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest(), true,
				this.getPage(), this.getPerPage(), true);
		chain.execute();
		setData(timelineRequest.getModuleName(), context.get(FacilioConstants.ContextNames.TIMELINE_CUSTOMIZATONDATA_MAP));
		return SUCCESS;
	}

    private Boolean isToFetchDecommissionedResource;
	private TimelineRequest timelineRequest;
	public TimelineRequest getTimelineRequest() {
		return timelineRequest;
	}
	public void setTimelineRequest(TimelineRequest timelineRequest) {
		this.timelineRequest = timelineRequest;
	}

	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	private String clientCriteria;
	public String getClientCriteria() {
		return clientCriteria;
	}
	public void setClientCriteria(String clientCriteria) {
		this.clientCriteria = clientCriteria;
	}

	private String _default;
	public String getDefault() {
		return _default;
	}
	public void setDefault(String _default) {
		this._default = _default;
	}

	private String timelineViewName;
	public void setTimelineViewName(String timelineViewName) {
		this.timelineViewName = timelineViewName;
	}
	public String getTimelineViewName() {
		return timelineViewName;
	}

	private String timelineModuleName;
	public void setTimelineModuleName(String timelineModuleName) {
		this.timelineModuleName = timelineModuleName;
	}
	public String getTimelineModuleName() {
		return timelineModuleName;
	}

	private long appId;
	public void setAppId(long appId) {
		this.appId = appId;
	}
	public long getAppId() {
		return appId;
	}
}
