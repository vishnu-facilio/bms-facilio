package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.actions.picklist.PickListUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.LookupField;
import com.facilio.timeline.context.TimelineRequest;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.TimelineViewUtil;
import com.facilio.v3.util.V3Util;
import com.facilio.wmsv2.handler.AuditLogHandler;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class TimelineAction extends RESTAPIHandler {

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

		if(((TimelineViewContext)viewObj).getGroupByField() == null) {
			throw new IllegalArgumentException("Group field not configured for the view");
		}
		setModuleName(((LookupField)(((TimelineViewContext)viewObj).getGroupByField())).getLookupModule().getName());
		if(LookupSpecialTypeUtil.isSpecialType(getModuleName())) {
			setData(FacilioConstants.ContextNames.PICKLIST, PickListUtil.getSpecialModulesPickList(getModuleName(), getPage(), getPerPage(), getSearch()));
			setMeta("moduleType", FacilioModule.ModuleType.PICK_LIST.name());
			setMeta("localSearch", !FacilioConstants.ContextNames.USERS.equals(getModuleName()));
		}
		else {
			FacilioContext pickListContext = new FacilioContext();
			PickListUtil.populatePicklistContext(pickListContext, getModuleName(), getFilters(), getSearch(), getCriteria(), getClientCriteria(), getDefault(), getViewName(), getPage(), getPerPage());
			if(((TimelineViewContext)viewObj).getGroupCriteriaId() > 0) {
				pickListContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, ((TimelineViewContext) viewObj).getGroupCriteria());
			}
			pickListContext = PickListUtil.fetchPickListData(pickListContext);

			setData(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
			setMeta("moduleType", ((FacilioModule) pickListContext.get(FacilioConstants.ContextNames.MODULE)).getTypeEnum().name());
			setMeta("localSearch", pickListContext.getOrDefault(FacilioConstants.PickList.LOCAL_SEARCH, true));
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

		JSONObject result = V3Util.processAndUpdateSingleRecord(this.getModuleName(), this.getId(), data, this.getParams(), this.getQueryParameters(), this.getStateTransitionId(), this.getCustomButtonId(), this.getApprovalTransitionId(), this.getQrValue());

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

	public String timelineSelectiveList() throws Exception {
		FacilioChain chain = ChainUtil.getTimelineListChain();
		FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest(), true,
				this.getPage(), this.getPerPage(), false);
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