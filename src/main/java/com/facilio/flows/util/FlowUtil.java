package com.facilio.flows.util;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.context.ParametersContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

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

        FlowContext flow = FieldUtil.getAsBeanFromMap(builder.fetchFirst(),FlowContext.class);

        return flow;
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

    public static Boolean validateStartBlock(long flowId) throws Exception{

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .select(FieldFactory.getFlowTransitionFields())
                .andCondition(CriteriaAPI.getCondition("FLOW_ID","flowId", String.valueOf(flowId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("START_BLOCK","isStartBlock", String.valueOf(true), BooleanOperators.IS));

        List<FlowTransitionContext> flowTransitions = FieldUtil.getAsBeanListFromMapList(builder.get(),FlowTransitionContext.class);

        if (flowTransitions.size()>0) {
            return true;
        }

        return false;

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

    public static FlowTransitionContext getFlowTransition(long id) throws Exception{

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFlowTransitionFields())
                .table(ModuleFactory.getFlowTransitionModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getFlowTransitionModule()));

        FlowTransitionContext flowTransition = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), FlowTransitionContext.class);
        return flowTransition;
    }

    public static void addOrUpdateParameter(ParametersContext parameters) throws Exception{

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

}
