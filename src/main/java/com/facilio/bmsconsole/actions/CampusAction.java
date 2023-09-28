package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.opensymphony.xwork2.ActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class CampusAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long stateTransitionId = -1;
	public long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

	private Long approvalTransitionId;
	public Long getApprovalTransitionId() {
		return approvalTransitionId;
	}
	public void setApprovalTransitionId(Long approvalTransitionId) {
		this.approvalTransitionId = approvalTransitionId;
	}

	@SuppressWarnings("unchecked")
	public String campusList() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);
		FacilioChain getAllCampus = FacilioChainFactory.getAllCampusChain();
		getAllCampus.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setCampuses((List<SiteContext>) context.get(FacilioConstants.ContextNames.SITE_LIST));
		
		return SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String newCampus() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		FacilioChain newCampus = FacilioChainFactory.getNewCampusChain();
		newCampus.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Map mp = ActionContext.getContext().getParameters();
		String isajax = ((org.apache.struts2.dispatcher.Parameter)mp.get("ajax")).getValue();
		if(isajax!=null && isajax.equals("true"))
		{
			return "ajaxsuccess";
		}
		return SUCCESS;
	}
	
	private List<FacilioField> fields;
	
	public String addCampus() throws Exception 
	{
		FacilioContext context = new FacilioContext();

		LocationContext location = site.getLocation();
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(site.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			FacilioChain addLocation = FacilioChainFactory.addLocationChain();
			addLocation.execute(context);
			long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			location.setId(locationId);
		}
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.SITE_ACTIVITY);
		context.put(FacilioConstants.ContextNames.SITE, site);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		FacilioChain addCampus = FacilioChainFactory.getAddCampusChain();
		addCampus.execute(context);
		
		setCampusId(site.getId());
		
		return SUCCESS;
	}
	
	public String updateCampus() throws Exception {
		FacilioContext context = new FacilioContext();
		LocationContext location = site.getLocation();
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(site.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			if (location.getId() > 0) {
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
				site.setLocation(null);
			}
			else {
				FacilioChain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
			}
		}
		else {
			site.setLocation(null);
		}
		
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.SITE_ACTIVITY);
		context.put(FacilioConstants.ContextNames.BASE_SPACE, site);
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, "site");
		CommonCommandUtil.addEventType(EventType.EDIT, context);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, approvalTransitionId);
		FacilioChain updateCampus = FacilioChainFactory.getUpdateCampusChain();
		updateCampus.execute(context);
		setSite(site);
		return SUCCESS;
	}
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String deleteCampus() throws Exception {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ID, id);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "site");
			FacilioChain deleteCampus = FacilioChainFactory.deleteSpaceChain();
			deleteCampus.execute(context);
			setId(id);
		}
		catch (Exception e) {
			setError("error",e.getMessage());
			throw e;
		}
		return SUCCESS;
	
	}

	public String v2DeleteCampus () throws Exception {
		deleteCampus();
		setResult(FacilioConstants.ContextNames.ID,id);
		return SUCCESS;
	}


	public String viewCampus() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getCampusId());
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);
		
		FacilioChain getCampusChain = FacilioChainFactory.getCampusDetailsChain();
		getCampusChain.execute(context);
		
		setSite((SiteContext) context.get(FacilioConstants.ContextNames.SITE));
		
		return SUCCESS;
	}
	
	public String reportCards() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getCampusId());

		context.put(FacilioConstants.ContextNames.REPORT_CARDS_META, fetchReportCardsMeta);
		FacilioChain getReportCardsChain = FacilioChainFactory.getSiteReportCardsChain();
		getReportCardsChain.execute(context);
		
		setReports((JSONObject) context.get(FacilioConstants.ContextNames.REPORTS));
		setReportcards((JSONArray) context.get(FacilioConstants.ContextNames.REPORT_CARDS));
		
		return SUCCESS;
	}

	public String getSiteInsights() throws Exception{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getCampusId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME,"site");
		FacilioChain getSiteInsightsChain = FacilioChainFactory.getSiteInsights();
		getSiteInsightsChain.execute(context);
		setReports((JSONObject) context.get(FacilioConstants.ContextNames.COUNT));

		return SUCCESS;
	}

	public String getChildrenSpace() throws Exception{
		FacilioChain getChildrenChain = TransactionChainFactoryV3.getBaseSpaceChildrenChain();
		FacilioContext context = getChildrenChain.getContext();
		context.put(FacilioConstants.ContextNames.SITE_ID,getCampusId());
		getChildrenChain.execute();
		setReports((JSONObject) context.get("list"));

		return SUCCESS;
	}

	public String getSiteDetails() throws Exception {
		FacilioChain getSiteChain = ReadOnlyChainFactory.getSiteforChildSpaceChain();
		FacilioContext siteChainContext = getSiteChain.getContext();
		siteChainContext.put("childId",getId());
		getSiteChain.execute();
		setResult("site",siteChainContext.get("site"));
		return SUCCESS;
	}

	public String v2SitesList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getSpaceModuleListChain();
		FacilioContext constructListContext = chain.getContext();
		constructListContext(constructListContext);
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "resource.name");
			searchObj.put("query", getSearch());
			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, "site");

		chain.execute();

		if (isFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,
					chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		} else {
			sites = (List<SiteContext>) chain.getContext()
					.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.SITE_LIST, sites);
		}
		return SUCCESS;
	}

	public String getSitesTotalArea() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getSiteTotalAreaChain();
		chain.execute();
		setResult(FacilioConstants.ContextNames.TOTAL_AREA, chain.getContext().get(FacilioConstants.ContextNames.TOTAL_AREA));
		setResult(FacilioConstants.ContextNames.UNIT, chain.getContext().get(FacilioConstants.ContextNames.UNIT));	
		return SUCCESS;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewCampusLayout(fields);
	}
	
	private String moduleName;
	public String getModuleName() 
	{
		return moduleName;
	}
	public void setModuleName(String moduleName) 
	{
		this.moduleName = moduleName;
	}
	
	private ActionForm actionForm;
	public ActionForm getActionForm() 
	{
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) 
	{
		this.actionForm = actionForm;
	}
	
	private SiteContext site;
	public SiteContext getSite() {
		return site;
	}
	public void setSite(SiteContext site) {
		this.site = site;
	}
	
	private JSONObject reports;
	public JSONObject getReports() {
		return reports;
	}
	public void setReports(JSONObject reports) {
		this.reports = reports;
	}
	
	private JSONArray reportcards;
	public JSONArray getReportcards() {
		return reportcards;
	}
	public void setReportcards(JSONArray reportcards) {
		this.reportcards = reportcards;
	}

	private long campusId;
	public long getCampusId() 
	{
		return campusId;
	}
	public void setCampusId(long campusId) 
	{
		this.campusId = campusId;
	}
	
	private List<SiteContext> campuses;
	public List<SiteContext> getCampuses() 
	{
		return campuses;
	}
	public void setCampuses(List<SiteContext> campuses) 
	{
		this.campuses = campuses;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.SITE;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewCampusLayout();
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummaryCampusLayout();
	}
	
	public String getViewDisplayName()
	{
		return "All Campus";
	}
	
	public List<SiteContext> getRecords() 
	{
		return campuses;
	}
	
	public SiteContext getRecord() 
	{
		return site;
	}
	private JSONObject error;
	public JSONObject getError() {
		return error;
	}

	public List<SiteContext> getSites() {
		return sites;
	}

	public void setSites(List<SiteContext> sites) {
		this.sites = sites;
	}

	private List<SiteContext> sites;

	public List<String> getFetchReportCardsMeta() {
		return fetchReportCardsMeta;
	}

	public void setFetchReportCardsMeta(List<String> fetchReportCardsMeta) {
		this.fetchReportCardsMeta = fetchReportCardsMeta;
	}

	private List<String> fetchReportCardsMeta;
	
	@SuppressWarnings("unchecked")
	public void setError(String key, Object error) {
		if (this.error == null) {
			this.error = new JSONObject();
		}
		this.error.put(key, error);			
	}
}