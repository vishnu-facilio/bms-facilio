package com.facilio.flows.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.blockfactory.enums.BlockType;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.context.ParameterContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowUtil {

    public static List<FlowContext> getFlowList(Criteria criteria, JSONObject pagination, String orderBy) throws Exception{

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFlowFields())
                .table(ModuleFactory.getFlowModule().getTableName());

        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);
        }

        if (criteria != null && !(criteria.isEmpty())){
            builder.andCriteria(criteria);
        }

        builder.orderBy(orderBy);

        List<FlowContext> flowList = FieldUtil.getAsBeanListFromMapList(builder.get(),FlowContext.class);

        return flowList;

    }

    public static FlowContext getFlow(long id) throws Exception{

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFlowModule().getTableName())
                .select(FieldFactory.getFlowFields())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getFlowModule()));

        Map<String,Object> prop = builder.fetchFirst();

        if(MapUtils.isEmpty(prop)){
            return null;
        }

        FlowContext flow = FieldUtil.getAsBeanFromMap(prop,FlowContext.class);
        List<ParameterContext> parameters = getParameters(id);
        flow.setParameters(parameters);
        return flow;
    }
    public static List<ParameterContext> getParameters(Long flowId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFlowParameterFields())
                .table(ModuleFactory.getFlowParameters().getTableName())
                .andCondition(CriteriaAPI.getCondition("FLOW_ID","flowId",flowId.toString(),NumberOperators.EQUALS));

        List<ParameterContext> parameters = FieldUtil.getAsBeanListFromMapList(builder.get(), ParameterContext.class);
        return parameters;
    }

    public static Boolean checkUniqueName(String uniqueName) throws Exception{

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .select(FieldFactory.getFlowTransitionFields())
                .andCondition(CriteriaAPI.getCondition("UNIQUE_NAME","uniqueName",uniqueName, StringOperators.IS));

        FlowTransitionContext flowTransition = FieldUtil.getAsBeanFromMap(builder.fetchFirst(),FlowTransitionContext.class);

        if (flowTransition != null)
            return true;

        return false;

    }

    public static FlowTransitionContext getStartBlock(long flowId) throws Exception{

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .select(FieldFactory.getFlowTransitionFields())
                .andCondition(CriteriaAPI.getCondition("FLOW_ID","flowId", String.valueOf(flowId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("START_BLOCK","isStartBlock", String.valueOf(true), BooleanOperators.IS));

        List<Map<String,Object>> list = builder.get();

        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        FlowTransitionContext startBlock = FieldUtil.getAsBeanFromMap(list.get(0),FlowTransitionContext.class);
        return startBlock;

    }
    public static Boolean validateConnectedFrom(long connectedFrom, long flowId) throws Exception{

       GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
               .table(ModuleFactory.getFlowTransitionModule().getTableName())
               .select(FieldFactory.getFlowTransitionFields())
               .andCondition(CriteriaAPI.getIdCondition(connectedFrom,ModuleFactory.getFlowTransitionModule()))
               .andCondition(CriteriaAPI.getCondition("FLOW_ID","flowId", String.valueOf(flowId),NumberOperators.EQUALS));

       List<FlowTransitionContext> flowTransitions = FieldUtil.getAsBeanListFromMapList(builder.get(),FlowTransitionContext.class);

       if (flowTransitions.size()>0) {
           return true;
       }

       return false;

    }

    public static List<FlowTransitionContext> getFlowTransitionList(long flowId) throws Exception{

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFlowTransitionFields())
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("FLOW_ID","flowId", String.valueOf(flowId), NumberOperators.EQUALS));

        List<FlowTransitionContext> flowTransitions = FieldUtil.getAsBeanListFromMapList(builder.get(), FlowTransitionContext.class);
        return flowTransitions;
    }
    public static List<FlowTransitionContext> getFlowTransitionListWithExtendedConfig(long flowId) throws Exception{

        List<FlowTransitionContext> result = new ArrayList<>();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFlowTransitionFields())
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("FLOW_ID","flowId", String.valueOf(flowId), NumberOperators.EQUALS));

        List<Map<String,Object>> list = builder.get();
        Map<String, List<Map<String,Object>>> blockTypeVsFlowTransitionListMap = list.stream().collect(Collectors.groupingBy((p)->(String)p.get(Constants.BLOCK_TYPE)));

        for (Map.Entry<String,List<Map<String,Object>>> entry:blockTypeVsFlowTransitionListMap.entrySet()){
            String blockTypeStr = entry.getKey();
            List<Map<String,Object>> props = entry.getValue();
            BlockType blockType = BlockType.valueOf(blockTypeStr);
            Class<? extends FlowTransitionContext> beanClass = FlowChainUtil.getBeanClassByBlockType(blockType);
            List<? extends FlowTransitionContext> flowTransitionList = FieldUtil.getAsBeanListFromMapList(props,beanClass);
            FacilioChain configChain = FlowChainUtil.getFlowTransitionListChain(blockType);
            if(configChain!=null){
                FacilioContext configContext = configChain.getContext();
                configContext.put(FacilioConstants.ContextNames.FLOW_TRANSITIONS,flowTransitionList);
                configChain.execute();
                flowTransitionList = (List<? extends FlowTransitionContext>) configContext.get(FacilioConstants.ContextNames.FLOW_TRANSITIONS);
            }
            result.addAll(flowTransitionList);
        }

        return result;
    }

    public static FlowTransitionContext getFlowTransition(long id) throws Exception{

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFlowTransitionFields())
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getFlowTransitionModule()));
        FlowTransitionContext flowTransition = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), FlowTransitionContext.class);
        return flowTransition;
    }
    public static FacilioContext getFlowTransitionWithConfig(long id) throws Exception{
        FacilioContext context = new FacilioContext();

        Map<String,Object> props = FlowUtil.getFlowTransitionAsProps(id);

        if(MapUtils.isEmpty(props)){
            return context;
        }
        BlockType blockType = BlockType.valueOf((String) props.get(Constants.BLOCK_TYPE));
        Class<? extends FlowTransitionContext> beanClass = FlowChainUtil.getBeanClassByBlockType(blockType);
        FlowTransitionContext flowTransition = FieldUtil.getAsBeanFromMap(props,beanClass);

        FacilioChain configChain = FlowChainUtil.getFlowTransitionSummaryChain(blockType);
        if(configChain!=null){
            FacilioContext configContext = configChain.getContext();
            configContext.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransition);
            configChain.execute();
            flowTransition = (FlowTransitionContext) configContext.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
            context.put(FacilioConstants.ContextNames.SUPPLEMENTS,configContext.get(FacilioConstants.ContextNames.SUPPLEMENTS));
        }
        context.put(FacilioConstants.ContextNames.FLOW_TRANSITION,flowTransition);
        return context;
    }

    public static Map<String,Object> getFlowTransitionAsProps(long id) throws Exception{
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFlowTransitionFields())
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getFlowTransitionModule()));
        return builder.fetchFirst();
    }
    public static void addOrUpdateParameter(ParameterContext parameters) throws Exception{

        if (parameters.getId() > 0){

            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .fields(FieldFactory.getFlowParameterFields())
                    .table(ModuleFactory.getFlowParameters().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(parameters.getId(),ModuleFactory.getFlowParameters()));

            builder.update(FieldUtil.getAsProperties(parameters));

        }else {

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .fields(FieldFactory.getFlowParameterFields())
                    .table(ModuleFactory.getFlowParameters().getTableName());

            Map<String, Object> props = FieldUtil.getAsProperties(parameters);
            builder.addRecord(props);
            builder.save();

        }

    }
    public static List<FlowTransitionContext> getFlowTransition(List<Long> connectedFromIds, long flowId) throws Exception {
        return getFlowTransition(connectedFromIds,flowId,FieldFactory.getFlowTransitionFields());
    }
    public static List<FlowTransitionContext>  getFlowTransition(List<Long> connectedFromIds, long flowId,List<FacilioField> selectFields) throws Exception{
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .select(selectFields)
                .andCondition(CriteriaAPI.getCondition("FLOW_ID","flowId", String.valueOf(flowId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getIdCondition(connectedFromIds,ModuleFactory.getFlowTransitionModule()));
        List<FlowTransitionContext> flowTransitions = FieldUtil.getAsBeanListFromMapList(builder.get(),FlowTransitionContext.class);
        return flowTransitions;
    }
    public static FlowTransitionContext getConnectedToBlock(long connectedFrom, long flowId, String handlePosition) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .select(FieldFactory.getFlowTransitionFields())
                .andCondition(CriteriaAPI.getCondition("FLOW_ID", "flowId", String.valueOf(flowId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CONNECTED_FROM", "connectedFrom", String.valueOf(connectedFrom), NumberOperators.EQUALS))
                .limit(1);
        if (StringUtils.isNotEmpty(handlePosition)) {
            builder.andCondition(CriteriaAPI.getCondition("HANDLE_POSITION", "handlePosition", handlePosition, StringOperators.IS));
        }
        FlowTransitionContext flowTransition = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), FlowTransitionContext.class);
        return flowTransition;
    }
    public static void addOrUpdateFlow(FlowContext flowContext) throws Exception{
        if(flowContext == null){
            return;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if ((flowContext.getModuleId() > 0) && (modBean.getModule(flowContext.getModuleId()) == null)) {
            throw new IllegalArgumentException("Invalid module");
        }

        if ((flowContext.getFlowType() == 2) && (flowContext.getModuleId() <= 0)){
            throw new IllegalArgumentException("Module Id cannot be null");
        }

        flowContext.setModifiedBy(AccountUtil.getCurrentUser().getOuid());
        flowContext.setSysModifiedTime(DateTimeUtil.getCurrenTime());

        Map<String, Object> props = FieldUtil.getAsProperties(flowContext);

        if (flowContext.getId() > 0) {

            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getFlowModule().getTableName())
                    .fields(FieldFactory.getFlowFields())
                    .andCondition(CriteriaAPI.getIdCondition(flowContext.getId(),ModuleFactory.getFlowModule()));

            builder.update(props);

        } else {

            flowContext.setCreatedBy(AccountUtil.getCurrentUser().getOuid());
            flowContext.setSysCreatedTime(DateTimeUtil.getCurrenTime());

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getFlowModule().getTableName())
                    .fields(FieldFactory.getFlowFields());

            builder.addRecord(props);
            builder.save();
        }

        flowContext.setId((long) props.get("id"));
        List<ParameterContext> parameters = flowContext.getParameters();

        deleteUnSentParameters(parameters,flowContext.getId());
        addOrUpdateParameters(parameters, flowContext.getId());

    }
    private static void addOrUpdateParameters(List<ParameterContext> parameters,long flowId) throws Exception {
        if(CollectionUtils.isEmpty(parameters)){
            return;
        }
        for (ParameterContext parameter : parameters){
            parameter.setFlowId(flowId);
            FlowUtil.addOrUpdateParameter(parameter);
        }
    }
    private static void deleteUnSentParameters(List<ParameterContext> parameters,long flowId) throws Exception {
        List<ParameterContext> oldParameters = FlowUtil.getParameters(flowId);
        List<ParameterContext> toBeDeletedParams = getTobeDeleteParameters(oldParameters,parameters);
        if(CollectionUtils.isEmpty(toBeDeletedParams)){
            return;
        }

        Set<Long> ids = toBeDeletedParams.stream().map(ParameterContext::getId).collect(Collectors.toSet());
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getFlowParameters().getTableName());

        int deleteCount = deleteRecordBuilder.batchDeleteById(ids);
    }
    private static List<ParameterContext> getTobeDeleteParameters(List<ParameterContext> oldParameters,List<ParameterContext> payLoadParameters){
        if(CollectionUtils.isEmpty(oldParameters)){
            return Collections.EMPTY_LIST;
        }
        if(payLoadParameters==null){
            payLoadParameters = new ArrayList<>();
        }
        Map<Long,ParameterContext> updatePayLoadParametersMap = payLoadParameters.stream().filter(p->p.getId()!=-1l)
                .collect(Collectors.toMap(ParameterContext::getId, Function.identity()));


        List<ParameterContext>  toBeDeletedParams = oldParameters.stream().filter(oldParameter->!updatePayLoadParametersMap.containsKey(oldParameter.getId())).collect(Collectors.toList());

        return toBeDeletedParams;
    }
    public static void updateFlowTransitionConnectedFrom(List<FlowTransitionContext> flowTransitions) throws Exception{

        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getFlowTransitionFields());

        List<GenericUpdateRecordBuilder.BatchUpdateContext> updateBatch = new ArrayList<>();

        for(FlowTransitionContext flowTransitionContext : flowTransitions){
            GenericUpdateRecordBuilder.BatchUpdateContext updateContext = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateContext.addUpdateValue("connectedFrom",flowTransitionContext.getConnectedFrom());
            updateContext.addWhereValue("id",flowTransitionContext.getId());
            updateContext.addWhereValue("flowId",flowTransitionContext.getFlowId());
            updateBatch.add(updateContext);

        }

        List<FacilioField> whereFields = new ArrayList<>();
        whereFields.add(fieldsMap.get("id"));
        whereFields.add(fieldsMap.get("flowId"));

        List<FacilioField> updateField = new ArrayList<>();
        updateField.add(fieldsMap.get("connectedFrom"));

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .fields(updateField)
                .table(ModuleFactory.getFlowTransitionModule().getTableName());
        builder.batchUpdate(whereFields, updateBatch);

    }

    public static void addOrUpdateParametersUsingParamName(List<ParameterContext> payLoadParameters,long flowId) throws Exception {
        if(CollectionUtils.isEmpty(payLoadParameters)){
            return;
        }
        List<ParameterContext> dbParameters = FlowUtil.getParameters(flowId);

        Map<String,ParameterContext> dbParamNameVsParamMap = dbParameters.stream().collect(Collectors.toMap(ParameterContext::getParameterName, Function.identity()));

        for(ParameterContext parameter : payLoadParameters){
            String parameterName = parameter.getParameterName();
            if(dbParamNameVsParamMap.containsKey(parameterName)){
                ParameterContext dbParam = dbParamNameVsParamMap.get(parameterName);
                parameter.setId(dbParam.getId());
                parameter.setFlowId(dbParam.getFlowId());
            }
        }
        List<ParameterContext> toBeDeletedParams = getTobeDeletedParameters(dbParameters,payLoadParameters);
        deleteParameters(toBeDeletedParams);
        for (ParameterContext parameter : payLoadParameters){
            parameter.setFlowId(flowId);
            FlowUtil.addOrUpdateParameter(parameter);
        }
    }
    private static List<ParameterContext> getTobeDeletedParameters(List<ParameterContext> oldParameters,List<ParameterContext> payLoadParameters){
        if(CollectionUtils.isEmpty(oldParameters)){
            return Collections.EMPTY_LIST;
        }
        if(payLoadParameters==null){
            payLoadParameters = new ArrayList<>();
        }
        Map<Long,ParameterContext> updatePayLoadParametersMap = payLoadParameters.stream().filter(p->p.getId()!=-1l)
                .collect(Collectors.toMap((ParameterContext::getId), Function.identity()));


        List<ParameterContext>  toBeDeletedParams = oldParameters.stream().filter(oldParameter->!updatePayLoadParametersMap.containsKey(oldParameter.getId())).collect(Collectors.toList());

        return toBeDeletedParams;
    }
    public static void deleteParameters(List<ParameterContext> toBeDeletedParams) throws Exception {
        if(CollectionUtils.isEmpty(toBeDeletedParams)){
            return;
        }

        Set<Long> ids = toBeDeletedParams.stream().map(ParameterContext::getId).collect(Collectors.toSet());
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getFlowParameters().getTableName());

        deleteRecordBuilder.batchDeleteById(ids);
    }

    @SneakyThrows
    public static Map<String,Object> getNewFlowMemory(){
        Map<String,Object>  memory = new HashMap<>();
        fillFlowSystemPlaceHolders(memory);
        return memory;
    }
    public static void fillFlowSystemPlaceHolders(Map<String,Object> memory) throws Exception{
        Map<String,Object> systemPlaceHolders = new HashMap<>();
        Map<String, Map<String, Object>> liveVariables = GlobalVariableUtil.getLiveVariables();
        if(MapUtils.isNotEmpty(liveVariables)){
            systemPlaceHolders.put("globalVariables",liveVariables);
        }
        systemPlaceHolders.put("org", FieldUtil.getAsJSON(AccountUtil.getCurrentOrg()));
        systemPlaceHolders.put("user",FieldUtil.getAsJSON(AccountUtil.getCurrentUser()));

        memory.put("_sys_",systemPlaceHolders);
    }
}
