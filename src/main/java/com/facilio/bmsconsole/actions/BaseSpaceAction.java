package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BaseSpaceAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unchecked")
	public String baseSpaceList() throws Exception 
	{
		boolean omitBasespaceAPI = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.OMIT_BASESPACE_API, Boolean.FALSE));
		if (omitBasespaceAPI) {
			List<BaseSpaceContext> basespaceList = new ArrayList<BaseSpaceContext>();
			setModuleName("Base Space");
			setBasespaces(basespaceList);
			return SUCCESS;
		}
		FacilioContext context = new FacilioContext();
		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
 		}
		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "basespace.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		if (getOrderBy() != null) {
 			JSONObject sorting = new JSONObject();
 			sorting.put("orderBy", getOrderBy());
 			sorting.put("orderType", getOrderType());
 			context.put(FacilioConstants.ContextNames.SORTING, sorting);
 		}
 		JSONObject pagination = new JSONObject();
 		pagination.put("page", getPage());
 		pagination.put("perPage", getPerPage());
 		if (getPerPage() < 0) {
 			pagination.put("perPage", 5000);
 		}
 		if (fetchHierarchy != null && fetchHierarchy) {
 			context.put(FacilioConstants.ContextNames.FETCH_HIERARCHY, true);
 		}
 		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.BASE_SPACE);
		context.put(FacilioConstants.ContextNames.WITH_READINGS, withReadings);
		FacilioChain getAllSpace = FacilioChainFactory.getAllAreaChain();
		getAllSpace.execute(context);
		
		setModuleName("Base Space");
		setBasespaces((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	
	
	private boolean isZone;
	public boolean getIsZone() {
		return isZone;
	}
	public void setIsZone(boolean isZone) {
		this.isZone = isZone;
	}
	public String childrenList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACE_ID, getSpaceId());
		context.put(FacilioConstants.ContextNames.SPACE_TYPE, getSpaceType());
		context.put(FacilioConstants.ContextNames.IS_ZONE, getIsZone());
		
		FacilioChain getAllSpace = FacilioChainFactory.getBaseSpaceChildrenChain();
		getAllSpace.execute(context);
		
		setBasespaces((List<BaseSpaceContext>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST));
		
		return SUCCESS;
	}
	
	public String basespaceWithChildren() throws Exception {
		List<BaseSpaceContext> spaces = SpaceAPI.getBaseSpaceWithChildren(spaceId);
		setResult(ContextNames.BASE_SPACE_LIST, spaces);
		return SUCCESS;
	}
	
	
	private Boolean fetchHierarchy;
	
	public Boolean getFetchHierarchy() {
		return fetchHierarchy;
	}
	public void setFetchHierarchy(Boolean fetchHierarchy) {
		this.fetchHierarchy = fetchHierarchy;
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
	
	private List<String> customFieldNames;
	public List<String> getCustomFieldNames() 
	{
		return customFieldNames;
	}
	public void setCustomFieldNames(List<String> customFieldNames) 
	{
		this.customFieldNames = customFieldNames;
	}
	
	private BaseSpaceContext basespace;

	public BaseSpaceContext getBasespace() {
		return basespace;
	}

	public void setBasespace(BaseSpaceContext basespace) {
		this.basespace = basespace;
	}
	private List<BaseSpaceContext> basespaces;
	public List<BaseSpaceContext> getBasespaces() 
	{
		return basespaces;
	}
	public void setBasespaces(List<BaseSpaceContext> basespaces) 
	{
		this.basespaces = basespaces;
	}
	
	public String getViewDisplayName()
	{
		return "All Spaces";
	}
	
	public List<BaseSpaceContext> getRecords() 
	{
		return basespaces;
	}
	
	private long spaceId;
	
	public long getSpaceId() {
		return this.spaceId;
	}
	
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}

	private long baseSpaceId;
	
	private String spaceType;

	public String getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(String spaceType) {
		this.spaceType = spaceType;
	}
	
	String filters;
	public void setFilters(String filters){
		this.filters = filters;
	}
	public String getFilters(){
		return this.filters;
	}
	
	String orderBy;
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderBy() {
		return this.orderBy;
	}
	
	String orderType;
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderType() {
		return this.orderType;
	}
	
	String search;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return this.search;
	}
	
	private Boolean withReadings;
	public Boolean getWithReadings() {
		if (withReadings == null) {
			return false;
		}
		return withReadings;
	}

	public void setWithReadings(Boolean withReadings) {
		this.withReadings = withReadings;
	}
	
	private Boolean fetchDeleted;
	public Boolean getFetchDeleted() {
		if (fetchDeleted == null) {
			return false;
		}
		return fetchDeleted;
	}
	public void setFetchDeleted(Boolean fetchDeleted) {
		this.fetchDeleted = fetchDeleted;
	}
	
	
/******************      V2 Api    ******************/
	
	public String v2baseSpaceList() throws Exception {
		baseSpaceList();
		setResult(FacilioConstants.ContextNames.BASE_SPACE_LIST, basespaces);
		return SUCCESS;
	}

	public String getBaseSpaceDirectChildren() throws Exception {
		/*
			Input  -  BaseSpace ID (Supported only for Site and Building)
			Output -  Site => List of Independent Spaces and Buildings
					  Building => List of Independent Spaces and Floors
		 */

		FacilioChain chain = ReadOnlyChainFactory.getSpaceDirectChildrenChain();
		chain.getContext().put(ContextNames.BASE_SPACE_ID, getBaseSpaceId());
		chain.getContext().put(ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);
		chain.execute();

		setResult(ContextNames.BASE_SPACE_LIST, chain.getContext().get(ContextNames.BASE_SPACE_LIST));

		return SUCCESS;
	}
	
	public String basespaceDetailsWithHierarchy() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
		context.put(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, getFetchDeleted());
		FacilioChain chain = ReadOnlyChainFactory.getBasespaceWithHierarchy();
		chain.execute(context);
		basespace = (BaseSpaceContext) context.get(FacilioConstants.ContextNames.BASE_SPACE);
		setResult(FacilioConstants.ContextNames.BASE_SPACE, basespace);
		return SUCCESS;
	}

	public String fetchParentNodes() throws Exception{
		long spaceId = getBaseSpaceId();
		FacilioChain getParentNodesChain = ReadOnlyChainFactory.getTreeParentNodesChain();
		FacilioContext context = getParentNodesChain.getContext();
		context.put("spaceId",spaceId);
		getParentNodesChain.execute();
		setResult("expandedKeys",context.get("expandedKeys"));
		setResult("selectedNode",getBaseSpaceId());
		setResult("nodes",context.get("node"));
//		setResult("parentIdVsChildrenMap",context.get("parentIdVsChildrenMap"));
		return SUCCESS;
	}

	public String getPortfolioCardData() throws Exception{
		FacilioChain portfolioCardsChain = ReadOnlyChainFactory.getPortfolioCardsChain();
		FacilioContext portfolioCardsContext = new FacilioContext();
		portfolioCardsChain.execute(portfolioCardsContext);
		setResult("cardResult",portfolioCardsContext.get("cardData"));
		return SUCCESS;
	}
	public String getSearchResult() throws Exception{
		FacilioChain portfolioSearchChain = ReadOnlyChainFactory.getPortfolioSearchChain();
		FacilioContext searchContext = portfolioSearchChain.getContext();
		searchContext.put(ContextNames.SEARCH,getSearch());
		searchContext.put(ContextNames.OFFSET,getPage());
		searchContext.put(ContextNames.MODULE_NAME,getModuleName());
		portfolioSearchChain.execute();
		setResult(ContextNames.BASE_SPACE_LIST,searchContext.get(ContextNames.SEARCH_RESULT));
		setResult(ContextNames.COUNT,searchContext.get(ContextNames.COUNT));
		return SUCCESS;
	}

	public long getBaseSpaceId() {
		return baseSpaceId;
	}

	public void setBaseSpaceId(long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}
}