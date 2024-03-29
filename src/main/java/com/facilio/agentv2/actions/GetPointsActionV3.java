package com.facilio.agentv2.actions;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.V3Action;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Getter @Setter
public class GetPointsActionV3 extends V3Action {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(GetPointsAction.class.getName());

    private String status;
    private Long deviceId;
    private Integer controllerType;
    private List<Long> controllerIds = new ArrayList<>();
    private Long agentId;
    private String filters;
    private int type=-1;
    private String moduleName;
    private Integer viewLimit = -1;
    private int readingScope = -1;
    /**
     * Get the Point count.Based on the Point filter. e.g.UNCONFIGURED..etc.
     *
     * @return the count.
     */
    public String getCount() throws Exception{

        FacilioChain chain = ReadOnlyChainFactory.getPointsdataCommand();
        FacilioContext context = chain.getContext();
        setWithCount(true);
        constructContext(context);
        context.put("status",status);
        context.put("controllerIds",controllerIds);
        context.put("controllerType",controllerType);
        if (moduleName != null) {
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        }
        else {
            context.put(FacilioConstants.ContextNames.MODULE_NAME,AgentConstants.POINTS);
        }
        context.put("agentId",agentId);
        chain.execute();
        setData("count",context.get("pointsCount"));
        return SUCCESS;
    }

    /**
     * Get the Points Data.Based on the Point filter. e.g.UNCONFIGURED..etc.
     *
     * @return the points data.
     */
    public String getPoints() throws Exception{

        FacilioChain chain = ReadOnlyChainFactory.getPointsdataCommand();
        FacilioContext context = chain.getContext();
        constructContext(context);
        context.put("status",status);
        context.put("controllerIds",controllerIds);
        context.put("controllerType",controllerType);
        if (moduleName != null) {
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        }
        else {
            context.put(FacilioConstants.ContextNames.MODULE_NAME,AgentConstants.POINTS);
        }
        context.put("agentId",agentId);
        chain.execute();
        setData("data",context.get("data"));
        setData("resourceMap",context.get("resourceMap"));
        setData("fieldMap",context.get("fieldMap"));
        setData("unitMap",context.get("unitMap"));

        return SUCCESS;
    }
    public String exportPoints() throws Exception{
        FacilioChain exportModule = TransactionChainFactory.getExportPointsChain();
        FacilioContext context = exportModule.getContext();
        constructContext(context);
        context.put(FacilioConstants.ContextNames.FILE_FORMAT, FileInfo.FileFormat.getFileFormat(type));
        context.put(AgentConstants.AGENT_ID, agentId);
        context.put(FacilioConstants.ContextNames.STATUS,status);
        context.put(AgentConstants.CONTROLLER_TYPE,controllerType);
        context.put(AgentConstants.CONTROLLERIDS,controllerIds);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        exportModule.execute();
        String fileUrl = (String) context.get(FacilioConstants.ContextNames.FILE_URL);
        setData("fileUrl", fileUrl);
        return SUCCESS;
    }
    public String exportPointsV2() throws Exception{
        FacilioChain chain = TransactionChainFactory.getExportPointsV2Chain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FILE_FORMAT, FileInfo.FileFormat.getFileFormat(type));
        context.put(AgentConstants.AGENT_ID,agentId);
        context.put(AgentConstants.CONTROLLER_TYPE,controllerType);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.FILTERS,filters);
        context.put(FacilioConstants.ContextNames.VIEW_NAME,getViewName());
        if (viewLimit != -1 && viewLimit > 5000){
            context.put(FacilioConstants.ContextNames.VIEW_LIMIT,viewLimit);
        }
        if(!controllerIds.isEmpty()) {
            context.put(AgentConstants.CONTROLLERIDS, controllerIds);
        }
        context.put(AgentConstants.READING_SCOPE,readingScope);
        chain.execute();
        String fileUrl = (String) context.get(FacilioConstants.ContextNames.FILE_URL);
        setData("fileUrl", fileUrl);
        return SUCCESS;
    }
    //Getting all controllerIds for specific agentId
    private List<Long> getControllerIds(long agentId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = ModuleFactory.getNewControllerModule();
        fields.add(FieldFactory.getIdField(module));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(fields)
                .table(module.getTableName()).andCondition(CriteriaAPI.getCondition(
                        FieldFactory.getNewAgentIdField(module), String.valueOf(agentId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        return props.stream().map(p -> (Long) p.get("id")).collect(Collectors.toList());
    }
    public JSONObject getPagination () {
        if (getPage() != 0) {
            JSONObject pagination = new JSONObject();
            pagination.put("page", getPage());
            pagination.put("perPage", getPerPage());
            return pagination;
        }
        return null;
    }
    public void constructContext(FacilioContext context) throws Exception {
        if (getPage() == 0) {
            setPage(1);
        }
        if (getPerPage() == -1) {
            setPerPage(50);
        }
        context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());

        if(getFilters() != null)
        {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);;
        }
        if (getWithCount()) {
            context.put(FacilioConstants.ContextNames.FETCH_COUNT, getWithCount());
        }
    }

}

