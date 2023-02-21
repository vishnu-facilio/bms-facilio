package com.facilio.bmsconsole.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.db.criteria.Criteria;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, visible = true, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = FacilioView.class, name = "1"),
    @JsonSubTypes.Type(value = TimelineViewContext.class, name = "2"),
})
public class FacilioView {

	public FacilioView() {}

	// Add all properties here as it will be used for cloning
	public FacilioView(FacilioView view) {
		this.orgId = view.orgId;
		this.id = view.id;
		this.includeParentCriteria = view.includeParentCriteria;
		this.name = view.name;
		this.displayName = view.displayName;
		this.type = view.type;
		if (view.viewSharing != null) {
			this.viewSharing = new SharingContext<>(view.viewSharing);
		}
		this.moduleId = view.moduleId;
		this.groupId = view.groupId;
		this.moduleName = view.moduleName;
		this.criteriaId = view.criteriaId;
		if (view.criteria != null) {
			this.criteria = view.criteria.clone();
		}
		this.isDefault = view.isDefault;
		this.fields = view.fields;
		this.sortFields = view.sortFields;
		this.defaultModulefields = view.defaultModulefields;
		this.lookupFields = view.lookupFields;
		this.filters = view.filters;
		this.sequenceNumber = view.sequenceNumber;
		this.isHidden = view.isHidden;
		this.primary = view.primary;
		this.fieldDisplayNames = view.fieldDisplayNames;
		this.appId = view.appId;
		this.ownerId = view.ownerId;
		this.isLocked = view.isLocked;
		this.isEditable = view.isEditable;
		this.isListView = view.isListView;
		this.isCalendarView = view.isCalendarView;
		this.calendarViewContext = view.calendarViewContext;
		this.excludeModuleCriteria=view.excludeModuleCriteria;
		this.appLinkNames = view.appLinkNames;
	}

	private Boolean isListView;
	public void setIsListView(Boolean listView) {
		this.isListView = listView;
	}
	public boolean isListView() {
		if (isListView != null) {
			return isListView;
		}
		return false;
	}
	public Boolean getIsListView() {
		return isListView;
	}

	private Boolean isCalendarView;
	public void setIsCalendarView(Boolean isCalendarView) {
		this.isCalendarView = isCalendarView;
	}
	public boolean isCalendarView() {
		if (isCalendarView != null) {
			return isCalendarView;
		}
		return false;
	}
	public Boolean getIsCalendarView() {
		return isCalendarView;
	}

	private CalendarViewContext calendarViewContext;
	public CalendarViewContext getCalendarViewContext() {
		return calendarViewContext;
	}

	public void setCalendarViewContext(CalendarViewContext calendarViewContext) {
		this.calendarViewContext = calendarViewContext;
	}
	private List<String> appLinkNames;

	public List<String> getAppLinkNames() {
		return appLinkNames;
	}

	public void setAppLinkNames(List<String> appLinkNames) {
		this.appLinkNames = appLinkNames;
	}
	private Long ownerId = -1L;
	public Long getOwnerId() { return ownerId;}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	private Boolean isLocked;
	public void setLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}
	public boolean isLocked(){
		if (isLocked != null){
			return isLocked.booleanValue();
		}
		return false;
	}
	public Boolean getIsLocked() {
		return isLocked;
	}

	private Boolean isEditable;

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	public boolean isEditable(){
		if (isEditable != null){
			return isEditable.booleanValue();
		}
		return false;
	}

	private long appId = -1;
	
	
	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private SharingContext<SingleSharingContext> viewSharing;
	public SharingContext<SingleSharingContext> getViewSharing() {
		return viewSharing;
	}
	public void setViewSharing(SharingContext<SingleSharingContext> sharingType) {
		this.viewSharing = sharingType;
	}

	private Boolean includeParentCriteria;
	
	public Boolean getIncludeParentCriteria() {
		if (includeParentCriteria == null) {
			return false;
		}
		return includeParentCriteria;
	}
	public void setIncludeParentCriteria(Boolean includeParentCriteria) {
		this.includeParentCriteria = includeParentCriteria;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	private ViewType type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public ViewType getTypeEnum() {
		return type;
	}
	public void setType(int type) {
		this.type = ViewType.TYPE_MAP.get(type);
	}
	public void setType(ViewType type) {
		this.type = type;
	}
	
	private long groupId = -1;
	
	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private String moduleName = "";
	public String getModuleName() throws Exception{
		if(StringUtils.isEmpty(moduleName) && moduleId > 0)
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			moduleName = modBean.getModule(this.getModuleId()).getName();
		}
		return moduleName;
	}
	
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria = null;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private Boolean isDefault;
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public boolean isDefault() {
		if(isDefault != null) {
			return isDefault.booleanValue();
		}
		return false;
	}
	
	private List<ViewField> fields;
	public List<ViewField> getFields() {
		return fields;
	}
	public void setFields(List<ViewField> fields) {
		this.fields = fields;
	}
	
	private List<SortField> sortFields;
	public List<SortField> getSortFields() {
		return sortFields;
	}
	public void setSortFields(List<SortField> sortFields) {
		this.sortFields = sortFields;
	}
	
	private Map<String, ViewField> defaultModulefields;
	public void setDefaultModuleFields(String moduleName, String parentViewName) throws Exception {
		List<ViewField> fields = null;
		if(parentViewName != null && !parentViewName.isEmpty()) {
			fields = ColumnFactory.getColumns(moduleName, parentViewName);
		}
		else {
			fields = ColumnFactory.getColumns(moduleName, "default");
		}
		
 		if (fields != null && !fields.isEmpty()) {
			defaultModulefields = new HashMap<>();
			for(ViewField vf : fields) {
				if(vf.getName().equals("tenant")){
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
						continue;
					}
				}
				if(vf.getName().equals("safetyPlan")){
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
						continue;
					}
				}
				defaultModulefields.put(vf.getName(),vf);
			}
		}
	}
	public Map<String, ViewField> getDefaultModuleFields(){
		return defaultModulefields;
	}
	
	private Map<String, Object> lookupFields;
	public void setLookupFields( Map<String, Object> lookupFields) {
		this.lookupFields = lookupFields;
	}
	public Map<String, Object> getLookupFields(){
		return lookupFields;
	}
	

	private JSONObject filters;
	public JSONObject getFilters() {
		return filters;
	}
	public void setFilters(JSONObject filters) {
		this.filters = filters;
	}
	public void setFiltersJson(String json) throws ParseException {
		this.filters = (JSONObject) new JSONParser().parse(json);
	}
	
	private int sequenceNumber = -1;
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public FacilioView setOrder(int order) {
		this.sequenceNumber = order;
		return this;
	}
	
	private Boolean isHidden;
	public Boolean getIsHidden() {
		return isHidden;
	}
	public void setHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}
	public boolean isHidden() {
		if(isHidden != null) {
			return isHidden.booleanValue();
		}
		return false;
	}
	
	private Boolean primary;
	public Boolean getPrimary() {
		return primary;
	}
	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}
	public boolean isPrimary() {
		if(primary != null) {
			return primary.booleanValue();
		}
		return false;
	}
	
	private Map<String, String> fieldDisplayNames;
	public Map<String, String> getFieldDisplayNames() {
		return fieldDisplayNames;
	}
	public void setFieldDisplayNames(Map<String, String> fieldDisplayNames) {
		this.fieldDisplayNames = fieldDisplayNames;
	}

	private int viewType = ViewType.TABLE_LIST.getIntVal();
	public void setViewType(int viewType) {
		this.viewType = viewType;
	}
	public int getViewType() {
		return viewType;
	}
	
	public static enum ViewType {
		TABLE_LIST(1),
		TIMELINE(2);
		
		private int intVal;
		
		private ViewType(int val) {
			// TODO Auto-generated constructor stub
			this.intVal = val;
		}
		public static ViewType getViewType(int val){
			return TYPE_MAP.get(val);
		}
		public int getIntVal() {
			return intVal;
		}
		
		private static final Map<Integer, ViewType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ViewType> initTypeMap() {
			Map<Integer, ViewType> typeMap = new HashMap<>();
			for(ViewType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

	}

	private Boolean excludeModuleCriteria;
	public void setExcludeModuleCriteria(Boolean excludeModuleCriteria) {
		this.excludeModuleCriteria = excludeModuleCriteria;
	}
	public boolean isExcludeModuleCriteria(){
		if (excludeModuleCriteria != null){
			return excludeModuleCriteria;
		}
		return false;
	}
}
