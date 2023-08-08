package com.facilio.odataservice.util;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.odataservice.action.ODataViewAction;
import com.facilio.odataservice.data.ODataFilterContext;
import com.facilio.v3.context.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.olingo.commons.api.data.EntityCollection;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.facilio.modules.FieldFactory.getField;
import static com.facilio.odataservice.util.ODATAUtil.*;

public class ODataModuleViewsUtil {

    private static final Logger LOGGER = LogManager.getLogger(ODataModuleViewsUtil.class.getName());

    public static List<String> getViewNames(String moduleName) throws Exception {
        List<String> viewName = new ArrayList<>();
        long appId = Objects.requireNonNull(AccountUtil.getCurrentApp()).getId();
        LOGGER.info("appid *"+appId);
        List<FacilioView> allViews = ViewAPI.getAllViews(appId,moduleName, FacilioView.ViewType.TABLE_LIST,true);
        List<FacilioView> userAccessibleViews = getUserAccessibleViews(allViews);
        for(FacilioView userAccessibleView : userAccessibleViews){
            String displayName = userAccessibleView.getDisplayName();
            if(!displayName.contains("-") ) {
                Pattern pattern = Pattern.compile("[a-zA-Z]");
                Matcher matcher = pattern.matcher(displayName.substring(0, 1));
                boolean matchFind = matcher.find();
                if (matchFind) {
                    viewName.add(displayName.replace(" ", "_"));
                }
            }
        }
        if(viewName.isEmpty()){
            LOGGER.info("No views present for "+moduleName);
        }
        return viewName;
    }

    private static List<FacilioView> getUserAccessibleViews(List<FacilioView> allViews) throws Exception{
        Criteria roleNameCriteria = new Criteria();
        String[] roleNames = { FacilioConstants.DefaultRoleNames.ADMIN, FacilioConstants.DefaultRoleNames.MAINTENANCE_ADMIN, FacilioConstants.DefaultRoleNames.CAFM_ADMIN };
        roleNameCriteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", StringUtils.join(roleNames, ","), StringOperators.IS));
        List<Role> adminRoles = AccountUtil.getRoleBean().getRoles(roleNameCriteria);
        List<Long> adminRoleIds = CollectionUtils.isNotEmpty(adminRoles) ? adminRoles.stream().map(Role::getId).collect(Collectors.toList()) : new ArrayList<>();

        User currentUser = AccountUtil.getCurrentUser();
        if(currentUser!=null) {
            long currentUserRoleId = currentUser.getRoleId();
            Boolean isSuperAdmin = currentUser.isSuperAdmin();
            boolean isPrivilegedRole = currentUser.getRole().isPrevileged() && (Objects.requireNonNull(AccountUtil.getCurrentApp()).getId() == currentUser.getApplicationId());
            boolean isAdmin = adminRoleIds.contains(currentUserRoleId);
            boolean isPrivilegedAccess = isSuperAdmin || isAdmin || isPrivilegedRole;
            return ViewAPI.filterAccessibleViews(allViews, AccountUtil.getCurrentUser(), isPrivilegedAccess);
        }else{
            throw new Exception("Current user is null");
        }
    }

    public static FacilioView getView(String view,String moduleName,long appId) throws Exception {
        long moduleId = -1;
        if(Constants.getModBean().getModule(moduleName)!= null && Constants.getModBean().getModule(moduleName).getModuleId() != -1){
            moduleId = Constants.getModBean().getModule(moduleName).getModuleId();
        }
        return ViewAPI.getView(view,moduleId , moduleName, Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId(),appId);
    }
    public static List<ViewField> getViewFields(String view, String moduleName) throws Exception{
        return getView(view,moduleName, Objects.requireNonNull(AccountUtil.getCurrentApp()).getId()).getFields();
    }
    public static List<FacilioField> getFields(List<ViewField> viewFields, FacilioModule module) throws Exception {
        Map<String,FacilioField> moduleFields = getModuleFields(module.getName());
        moduleFields.put("siteId",FieldFactory.getSiteIdField(module));
        moduleFields.put("moduleId",FieldFactory.getModuleIdField(module));
        if(FieldFactory.getAgentIdField(module) != null) {
            moduleFields.put("agentId", FieldFactory.getAgentIdField(module));
        }
        if(FieldFactory.getControllerIdField(module) != null) {
            moduleFields.put("controllerId", FieldFactory.getControllerIdField(module));
        }

        List<FacilioField>  ViewFields= new ArrayList<>();
        for(int i=0;i<viewFields.size();i++) {
            if(viewFields.get(i).getFieldName() != null && viewFields.get(i).getFieldName().equalsIgnoreCase("siteid")){
                ViewFields.add(moduleFields.get(viewFields.get(i).getFieldName()));
            }
            else if(viewFields.get(i).getField().getName() != null) {
                ViewFields.add(moduleFields.get(viewFields.get(i).getField().getName()));
            }
        }
        return ViewFields;
    }
    public static FacilioView getViewDetail(String moduleName,String viewName) throws Exception {
        return ODataViewAction.getViewDetail(moduleName,viewName);
    }
    public static Map<String,FacilioField> getFields(String viewDisplayName,String moduleName) throws Exception {
        String viewName;
        viewName = getViewName(viewDisplayName,moduleName);
        if(viewName!=null) {
            List<ViewField> viewFields = getViewFields(viewName, moduleName);
            Map<String, FacilioField> moduleFieldsMap = getModuleFields(moduleName);
            Map<String, FacilioField> viewFieldsMap = new HashMap<>();
            if (viewFields != null) {
                for (int i = 0; i < viewFields.size(); i++) {
                    if (viewFields.get(i).getFieldName() != null && viewFields.get(i).getFieldName().equalsIgnoreCase("siteid")) {
                        viewFieldsMap.put(viewFields.get(i).getFieldName(), getField(viewFields.get(i).getFieldName(), viewFields.get(i).getColumnDisplayName(), FieldType.STRING));
                    } else {
                        viewFieldsMap.put(viewFields.get(i).getField().getName(), moduleFieldsMap.get(viewFields.get(i).getField().getName()));
                    }
                }
            }
            getDefaultFields(moduleFieldsMap,viewFieldsMap);
            return viewFieldsMap;
        }else {
            return null;
        }

    }

    private static Map<String, FacilioField> getDefaultFields(Map<String, FacilioField> moduleFieldsMap, Map<String, FacilioField> viewFieldsMap) {

        if(viewFieldsMap.get("sysCreatedTime") == null && viewFieldsMap.get("createdTime") == null){
            if(moduleFieldsMap.containsKey("sysCreatedTime")){
                viewFieldsMap.put("sysCreatedTime",moduleFieldsMap.get("sysCreatedTime"));
            }else if(moduleFieldsMap.containsKey("createdTime")){
                viewFieldsMap.put("createdTime",moduleFieldsMap.get("createdTime"));
            }
        }
        if(viewFieldsMap.get("sysModifiedTime") == null && viewFieldsMap.get("modifiedTime") == null) {
            if(moduleFieldsMap.containsKey("sysModifiedTime")){
                viewFieldsMap.put("sysModifiedTime",moduleFieldsMap.get("sysModifiedTime"));
            }else if(moduleFieldsMap.containsKey("modifiedTime")){
                viewFieldsMap.put("modifiedTime",moduleFieldsMap.get("modifiedTime"));
            }
        }
        if(viewFieldsMap.get("id") == null && viewFieldsMap.get("serialNumber") == null) {
            if(moduleFieldsMap.containsKey("id")){
                viewFieldsMap.put("id",moduleFieldsMap.get("id"));
            }
        }
        return viewFieldsMap;
    }

    public static String getViewName(String displayName, String moduleName) throws Exception{
        List<FacilioField> fields = FieldFactory.getViewFields();
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        ApplicationContext application = AccountUtil.getCurrentApp();
        long appId = -1;
        if(application != null){
            appId = AccountUtil.getCurrentApp().getId();
        }else{
            appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        if(appId != -1) {
            long moduleId = Constants.getModBean().getModule(moduleName).getModuleId();
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table("Views")
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("appId"), Collections.singleton(appId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("type"), String.valueOf(1), StringOperators.IS))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("isHidden"), String.valueOf(1), StringOperators.ISN_T))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("displayName"), displayName.replace("_", " "), StringOperators.IS));
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleName"), moduleName, StringOperators.IS));
            criteria.addOrCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), Collections.singleton(moduleId), StringOperators.IS));
            builder.andCriteria(criteria);
            List<Map<String, Object>> props = builder.get();
            if(props!=null){
                Map<String, Object> propsObj = props.get(0);
                if(propsObj!=null){
                    return propsObj.get("name").toString();
                }
            }
        }
        else {
            LOGGER.info("appId for current app is null");
        }
        return null;
    }

    public static List<SupplementRecord> getSupplementRecords(List<FacilioField> viewFields) throws Exception {
        List<SupplementRecord> lookup = new ArrayList<>();
        for (FacilioField field : viewFields) {
            if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                lookup.add((SupplementRecord) field);
            }
        }
        return lookup;
    }

    public static EntityCollection getModuleViewRecords(String moduleName, Map<String, Object> builderParams, FacilioView selectedView) throws Exception{
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModule(module);
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(moduleName));
        fieldsAsMap.put("id",FieldFactory.getIdField(module));
        List<FacilioField> fields = getBuilderFields(module,builderParams,selectedView);
        List<SupplementRecord> lookupFields = getSupplementRecords(fields);
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = getSelectBuilder(module,fields,fieldsAsMap,moduleClass,lookupFields,builderParams,selectedView);
        List<Map<String, Object>> propsMap = FieldUtil.getAsMapList(selectBuilder.get(), ModuleBaseWithCustomFields.class);
        /** if $select query contains any field, then we need to filter that fields alone */
        if(propsMap!= null && !propsMap.isEmpty()) {
            propsMap = getModifiedPropsMap(propsMap,builderParams,moduleName);
        }
        return getMapAsEntityCollection(propsMap,module,false);
    }

    private static List<Map<String, Object>> getModifiedPropsMap(List<Map<String, Object>> propsMap, Map<String, Object> builderParams, String moduleName) throws Exception{
        LOGGER.info("modified props");
        List<Map<String,Object>> newPropsMap = new ArrayList<>();
        if(!builderParams.get("selectFields").toString().equalsIgnoreCase("")){
            String fieldName =  ODATAUtil.getDisplayNameVsFieldNameMap(moduleName).get(builderParams.get("selectFields").toString());
            if(fieldName == null){
                fieldName = builderParams.get("selectFields").toString();
            }
            for(Map<String,Object> prop:propsMap){
                Map<String,Object> tempMap = new HashMap<>();
                tempMap.put(builderParams.get("selectFields").toString(),prop.get(fieldName));
                newPropsMap.add(tempMap);
            }
            propsMap =newPropsMap;
        }
        LOGGER.info("modified props after");
        return propsMap;
    }

    private static List<FacilioField> getBuilderFields(FacilioModule module, Map<String, Object> builderParams, FacilioView selectedView) throws Exception{
        List<FacilioField> fields;
        if(!builderParams.get("selectFields").toString().equalsIgnoreCase("")){
            fields = new ArrayList<>();
            fields.add(Constants.getModBean().getField(ODATAUtil.getDisplayNameVsFieldNameMap(selectedView.getModuleName()).get(addNameSpaces(builderParams.get("selectFields").toString())),module.getName()));
            fields.add(FieldFactory.getIdField(module));
        }else {
            fields = Constants.getModBean().getAllFields(module.getName());
            List<FacilioField> viewFields = getFields(selectedView.getFields(),module);
            Map<String,FacilioField> viewFieldsMap = FieldFactory.getAsMap(viewFields);
            viewFields = getDefaultFields(viewFieldsMap,viewFields,FieldFactory.getAsMap(fields));
            fields = viewFields;
        }
        return fields;
    }

    private static List<FacilioField> getDefaultFields(Map<String, FacilioField> viewFieldsMap, List<FacilioField> viewFields,Map<String,FacilioField> fieldsAsMap){
        if(viewFieldsMap.get("sysCreatedTime") == null && viewFieldsMap.get("createdTime") == null){
            if(fieldsAsMap.containsKey("sysCreatedTime")){
                viewFields.add(fieldsAsMap.get("sysCreatedTime"));
            }else if(fieldsAsMap.containsKey("createdTime")){
                viewFields.add(fieldsAsMap.get("createdTime"));
            }
        }
        if(viewFieldsMap.get("sysModifiedTime") == null && viewFieldsMap.get("modifiedTime") == null) {
            if(fieldsAsMap.containsKey("sysModifiedTime")){
                viewFields.add(fieldsAsMap.get("sysModifiedTime"));
            }else if(fieldsAsMap.containsKey("modifiedTime")){
                viewFields.add(fieldsAsMap.get("modifiedTime"));
            }
        }
        if(viewFieldsMap.get("id") == null) {
            if(fieldsAsMap.containsKey("id")){
                viewFields.add(fieldsAsMap.get("id"));
            }
        }
        return viewFields;
    }
    private static SelectRecordsBuilder<ModuleBaseWithCustomFields> getLimitOffset(SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Map<String, Object> builderParams) throws Exception{
        int odataModuleRecordsLimit = (int) builderParams.get("limit");
        if(odataModuleRecordsLimit == -1) {
            odataModuleRecordsLimit = Integer.parseInt(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.ODATA_MODULE_RECORDS_LIMIT, 10000));
        }else{
            int skip = (int) builderParams.get("offset");
            if(skip != -1 && skip>0){
                selectBuilder.offset(skip);
            }
        }
        selectBuilder.limit(odataModuleRecordsLimit);
        return selectBuilder;
    }

    private static SelectRecordsBuilder<ModuleBaseWithCustomFields> getQueryFilters(SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Map<String, Object> builderParams) throws Exception {
        List<ODataFilterContext> filterContexts = ((List<ODataFilterContext>)builderParams.get("filterContext"));
        LOGGER.info("odata filter : "+filterContexts);
        if(filterContexts!= null && !filterContexts.isEmpty()){
            for(ODataFilterContext filterContext:filterContexts){
                Condition condition = new Condition();
                condition.setOperator(filterContext.getOperator());
                condition.setValue(filterContext.getValueList());
                condition.setField(filterContext.getfacilioField());
                selectBuilder.andCondition(condition);
            }
        }
        return selectBuilder;
    }

    private static SelectRecordsBuilder<ModuleBaseWithCustomFields> getViewCriteria(FacilioView viewDetail, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder) {
        if(viewDetail.getCriteria()!=null){
            Map<String, Condition> conditionMap =  viewDetail.getCriteria().getConditions();
            for(Map.Entry<String,Condition> entry: conditionMap.entrySet()){
                Condition condition = entry.getValue();
                selectBuilder.andCondition(condition);
            }
        }
        return selectBuilder;
    }

    private static SelectRecordsBuilder<ModuleBaseWithCustomFields> getOrderBy(FacilioView viewDetail, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Map<String,FacilioField> fieldsAsMap, Map<String, Object> builderParams) throws Exception{

        if(!builderParams.get("orderBy").toString().equalsIgnoreCase("")){
            String orderByField = builderParams.get("orderBy").toString();
            boolean isDescending = (boolean) builderParams.get("isDescending");
            String orderByFieldName = ODATAUtil.getDisplayNameVsFieldNameMap(viewDetail.getModuleName()).get(addNameSpaces(orderByField));
            String isAscDesc = !isDescending ? " DESC" : " ASC";
            String orderBy = orderByFieldName + isAscDesc;
            selectBuilder.orderBy(orderBy);
        }
        else if(viewDetail.getSortFields()!=null && !viewDetail.getSortFields().isEmpty()) {
            String OrderBy = viewDetail.getSortFields().get(0).getSortField().getName();
            Boolean isAsc = viewDetail.getSortFields().get(0).getIsAscending();
            String isAscOrDesc = !isAsc ? " DESC" : " ASC";
            OrderBy = fieldsAsMap.get(OrderBy).getColumnName();
            OrderBy = OrderBy + isAscOrDesc;
            selectBuilder.orderBy(OrderBy);
        }
        return selectBuilder;
    }

    public static Boolean isModuleEnabled(long moduleId) throws Exception {
        List<FacilioField> fields = FieldFactory.getODataModuleFields();
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getOdataModule().getTableName())
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("isEnabled"), String.valueOf(true), BooleanOperators.IS));
        if(selectRecordBuilder.get().isEmpty()){
            return false;
        }
        return true;
    }

    private static SelectRecordsBuilder<ModuleBaseWithCustomFields> getSelectBuilder(FacilioModule module, List<FacilioField> fields, Map<String,FacilioField> fieldsAsMap, Class<ModuleBaseWithCustomFields> moduleClass, List<SupplementRecord> lookupFields, Map<String, Object> builderParams, FacilioView selectedView) throws Exception{
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
                .module(module)
                .beanClass(moduleClass);

        selectBuilder.select(fields);
        selectBuilder.fetchSupplements(lookupFields);
        selectBuilder = getOrderBy(selectedView,selectBuilder,fieldsAsMap,builderParams);
        selectBuilder = getViewCriteria(selectedView,selectBuilder);
        selectBuilder = getQueryFilters(selectBuilder,builderParams);
        selectBuilder = getLimitOffset(selectBuilder,builderParams);
        return selectBuilder;
    }

    public static List<String> getViews(String moduleName) throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.GROUP_STATUS, true);
        context.put(FacilioConstants.ContextNames.APP_ID, AccountUtil.getCurrentApp().getId());
        context.put(FacilioConstants.ContextNames.RESTRICT_PERMISSIONS,false);
        context.put(FacilioConstants.ContextNames.VIEW_TYPE, 1);
        context.put(FacilioConstants.ContextNames.VIEW_GROUP_TYPE, 1);
        context.put(FacilioConstants.ContextNames.GET_ONLY_BASIC_VIEW_DETAILS, true);
        context.put(FacilioConstants.ContextNames.IS_FROM_BUILDER, false);
        FacilioChain getViewListsChain = FacilioChainFactory.getViewListChain();
        getViewListsChain.execute(context);
        List<ViewGroups> views = (List<ViewGroups>) context.get(FacilioConstants.ContextNames.VIEW_LIST);
        List<ViewGroups> viewGroups = (List<ViewGroups>) context.get(FacilioConstants.ContextNames.GROUP_VIEWS);
        List<ViewGroups> resultViewGroups;
        if(views == null && viewGroups != null && !viewGroups.isEmpty()){
            resultViewGroups = viewGroups;
        }else if(views != null && viewGroups == null && !views.isEmpty()){
            resultViewGroups = views;
        }else{
            throw new Exception("No Views present for "+moduleName+" in "+ApplicationApi.getApplicationName(AccountUtil.getCurrentApp().getId()));
        }
        List<String> viewNames = new ArrayList<>();
        for(ViewGroups viewGroup : resultViewGroups){
          List<FacilioView> groupViews = viewGroup.getViews();
          for(FacilioView view:groupViews){
              String displayName = view.getDisplayName();
              if(!displayName.contains("-") ) {
                  Pattern pattern = Pattern.compile("[a-zA-Z]");
                  Matcher matcher = pattern.matcher(displayName.substring(0, 1));
                  boolean matchFind = matcher.find();
                  if (matchFind) {
                      viewNames.add(displayName.replace(" ", "_"));
                  }
              }
          }
        }

        return viewNames;
    }
}
