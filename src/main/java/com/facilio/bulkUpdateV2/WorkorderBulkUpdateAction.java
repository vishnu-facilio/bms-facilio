package com.facilio.bulkUpdateV2;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.V3Action;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.bulkUpdateV2.WorkorderBulkUpdateUtil.workorderBulkUpdateHandler;


@Getter@Setter
public  class WorkorderBulkUpdateAction extends V3Action {

    private String moduleName;
    private String viewName;
    private String actionName;


    public String workorderBulkUpdate() throws Exception
    {
        String moduleName = getModuleName();
        Boolean excludeParentFilter=false;
        Map<String, List<Object>> queryParameters=new HashMap<>();
        Criteria serverCriteria=null;
        int limit = Integer.parseInt(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.WORK_ORDER_BULKUPDATE, 500));
        Long recordCount=fetchRecordCount();
        if(recordCount<=limit) {
            FacilioContext listContext = V3Util.fetchList(getModuleName(), true, getViewName(), getFilters(), excludeParentFilter, getClientCriteria(), getOrderBy(), getOrderType(), getSearch(), getPage(), limit, false, queryParameters, serverCriteria, false, true, null);
            Map<String, List> recordMap = (Map<String, List>)
                    listContext.get(Constants.RECORD_MAP);
            List<ModuleBaseWithCustomFields> wos = recordMap.get(moduleName);
            Map<String, Object> dataMap = new HashMap<>();
            List<Long> ids = new ArrayList<>();
            wos.stream()
                    .map(ModuleBaseWithCustomFields::getId)
                    .forEach(id -> ids.add(id));
            workorderBulkUpdateHandler(this.getData(), moduleName, this.getParams(), ids, null, getStateTransitionId(), getCustomButtonId(), getApprovalTransitionId(), getQrValue(), getLocationValue(), getCurrentLocation());
        }
        else{
            throw new IllegalArgumentException("The number of records ("+ recordCount+") exceeds the allowed limit (" + limit +") for the bulk update action");
        }


        return SUCCESS;
    }

    public String fetchBulkActionLimit() throws Exception{
        int limit=-1;
        if(getActionName()!=null){
            if(getActionName().equals(FacilioConstants.OrgInfoKeys.WORK_ORDER_BULKUPDATE)){
                limit = Integer.parseInt(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.WORK_ORDER_BULKUPDATE, 500));
            }
            else if(getActionName().equals(FacilioConstants.OrgInfoKeys.PPM_BULKUPDATE)){
                limit = Integer.parseInt(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.PPM_BULKUPDATE, 200));
            }
            else if(getActionName().equals(FacilioConstants.OrgInfoKeys.PPM_BULKPUBLISH)){
                limit = Integer.parseInt(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.PPM_BULKPUBLISH, 200));
            }
        }

        setData("limit",limit);
        return SUCCESS;
    }

    public String pmBulkUpdate() throws Exception{
        String moduleName = getModuleName();
        Boolean excludeParentFilter=false;
        Boolean isV3 = true;
        Boolean withCount=false;

        Map<String, List<Object>> queryParameters=new HashMap<>();
        Criteria serverCriteria=null;

        int limit = Integer.parseInt(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.PPM_BULKUPDATE, 200));

        Long recordCount=fetchRecordCount();
        if(recordCount<=limit) {
            FacilioContext listContext = V3Util.fetchList(getModuleName(), isV3, getViewName(), getFilters(), excludeParentFilter, getClientCriteria(), getOrderBy(), getOrderType(), getSearch(), getPage(), limit, withCount, queryParameters, serverCriteria, false, true, null);
            Map<String, List> recordMap = (Map<String, List>)
                    listContext.get(Constants.RECORD_MAP);
            List<ModuleBaseWithCustomFields> wos = recordMap.get(moduleName);
            Map<String, Object> dataMap = new HashMap<>();
            List<Long> ids = new ArrayList<>();
            wos.stream()
                    .map(ModuleBaseWithCustomFields::getId)
                    .forEach(id -> ids.add(id));
            workorderBulkUpdateHandler(this.getData(), moduleName, this.getParams(), ids, null, getStateTransitionId(), getCustomButtonId(), getApprovalTransitionId(), getQrValue(), getLocationValue(), getCurrentLocation());
        }
        else{
            throw new IllegalArgumentException("The number of records ("+ recordCount+") exceeds the allowed limit (" + limit +") for the bulk update action");
        }
        return SUCCESS;
    }
    public String fetchBulkUpdateWorkorderFields() throws Exception{
        FacilioChain fetchFieldsChain = FacilioChainFactory.getAllFieldsChain();
        FacilioContext context = fetchFieldsChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,getModuleName());
        context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,-1l);
        fetchFieldsChain.execute(context);
        JSONObject meta = (JSONObject) context.get("meta");

        List<String> systemfields= new ArrayList<>();
        systemfields.add("requester");
        systemfields.add("assignedTo");
        systemfields.add("category");
        systemfields.add("dueDate");
        systemfields.add("priority");
        systemfields.add("type");
        systemfields.add("resource");

        meta.put("systemfields",systemfields);
        this.setData(meta);

        return SUCCESS;
    }

    public String fetchBulkUpdatePmFields() throws Exception{
        FacilioChain fetchFieldsChain = FacilioChainFactory.getAllFieldsChain();
        FacilioContext context = fetchFieldsChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,getModuleName());
        context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,-1l);
        fetchFieldsChain.execute(context);
        JSONObject meta = (JSONObject) context.get("meta");

        List<String> systemfields= new ArrayList<>();
        systemfields.add("leadTime");
        systemfields.add("vendor");
        context.put("systemfields",systemfields);

        meta.put("systemfields",systemfields);
        this.setData(meta);

        return SUCCESS;
    }

    public String pmBulkPublish() throws Exception{
        String moduleName = getModuleName();
        Boolean excludeParentFilter=false;
        Boolean isV3 = true;
        Boolean withCount=false;
        Map<String, List<Object>> queryParameters=new HashMap<>();
        Criteria serverCriteria=null;
        int limit = Integer.parseInt(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.PPM_BULKPUBLISH, 200));

        Long recordCount=fetchRecordCount();
        if(recordCount<=limit) {
            FacilioContext listContext = V3Util.fetchList(getModuleName(), isV3, getViewName(), getFilters(), excludeParentFilter, getClientCriteria(), getOrderBy(), getOrderType(), getSearch(), getPage(), limit, withCount, queryParameters, serverCriteria, false, true, null);
            Map<String, List> recordMap = (Map<String, List>)
                    listContext.get(Constants.RECORD_MAP);
            List<ModuleBaseWithCustomFields> wos = recordMap.get(moduleName);
            Map<String, Object> dataMap = new HashMap<>();
            List<Long> ids = new ArrayList<>();
            wos.stream()
                    .map(ModuleBaseWithCustomFields::getId)
                    .forEach(id -> ids.add(id));

            FacilioChain publishPPM = TransactionChainFactoryV3.getPublishPM();
            FacilioContext context = publishPPM.getContext();
            context.put("pmIds", ids);
            publishPPM.execute();
        }
        else{
            throw new IllegalArgumentException("The number of records ("+ recordCount+") exceeds the allowed limit (" + limit +") for the bulk update action");
        }

        return SUCCESS;
    }

    public Long fetchRecordCount() throws Exception{
        FacilioChain countChain = ChainUtil.getCountChain(getModuleName());
        FacilioContext context = countChain.getContext();

        context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);

        String filters = getFilters();
        if (!filters.equals("null") && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(Constants.FILTERS, json);

            context.put(Constants.EXCLUDE_PARENT_CRITERIA, false);
        }

        FacilioModule module = ChainUtil.getModule(getModuleName());
        V3Config v3Config = ChainUtil.getV3Config(getModuleName());

        Constants.setV3config(context, v3Config);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);

        countChain.execute();
        Long count = Constants.getCount(context);
        return count;

    }


}
