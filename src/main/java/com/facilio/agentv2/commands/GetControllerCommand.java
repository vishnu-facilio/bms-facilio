package com.facilio.agentv2.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.module.AgentModuleFactory;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.annotation.Module;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetControllerCommand extends AgentV2Command {
    private static final Logger LOGGER = LogManager.getLogger(GetControllerCommand.class.getName());

    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String childTableModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String controllerName = (String) context.get(AgentConstants.SEARCH_KEY);
        Integer controllerType = (Integer)context.get(AgentConstants.CONTROLLER_TYPE);
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        FacilioModule controllerModule = ModuleFactory.getNewControllerModule();
        FacilioModule pointModule = AgentConstants.getPointModule();
        if (pointModule == null){
            pointModule = ModuleFactory.getPointModule();
        }
        if (childTableModuleName == null) {
            throw new Exception(" module name can't be null ");
        }
        FacilioModule childTableModule = modBean.getModule(childTableModuleName);
        FacilioModule resourceModule = ModuleFactory.getResourceModule();
        List<FacilioField> allFields = new ArrayList<>();
        Objects.requireNonNull(childTableModule,"childTable module cant be empty, "+childTableModuleName);
        Objects.requireNonNull(resourceModule,"respurce module module cant be empty");
        allFields.addAll(modBean.getModuleFields(controllerModule.getName()));

        allFields.addAll(modBean.getModuleFields(childTableModule.getName()));
     /*  if (childFields == null) {
            throw new Exception(" child fields cant be empty " + childTableModuleName);
        }
        fields.addAll(childFields);*/
        if (pointModule == null) {
            allFields.add(FieldFactory.getSubscribedPointCountConditionField());
            allFields.add(FieldFactory.getSubscriptionInProgressPointCountConditionField());
            allFields.add(FieldFactory.getConfiguredPointCountConditionField());
            allFields.add(FieldFactory.getConfigurationInProgressPointCountConditionField());
            allFields.add(FieldFactory.getPointsCount());
        }
        else {
            allFields.add(FieldFactory.getSubscribedPointCountConditionField(pointModule));
            allFields.add(FieldFactory.getSubscriptionInProgressPointCountConditionField(pointModule));
            allFields.add(FieldFactory.getConfiguredPointCountConditionField(pointModule));
            allFields.add(FieldFactory.getConfigurationInProgressPointCountConditionField(pointModule));
            allFields.add(FieldFactory.getPointsCount(pointModule));
        }
        allFields.add(FieldFactory.getIdField(controllerModule));
        allFields.add(FieldFactory.getSiteIdField(controllerModule));
        allFields.add(FieldFactory.getNameField(ModuleFactory.getResourceModule()));
        context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, allFields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()

                .table(controllerModule.getTableName())
                .innerJoin(childTableModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + childTableModule.getTableName() + ".ID")
                .leftJoin(pointModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + pointModule.getTableName() + "." + FieldFactory.getControllerIdField(pointModule).getColumnName())
                .innerJoin(resourceModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + resourceModule.getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(resourceModule), "NULL", CommonOperators.IS_EMPTY));

        if (controllerType != FacilioControllerType.MODBUS_RTU.asInt()) {
            selectRecordBuilder = selectRecordBuilder.select(allFields);
        } else {
            allFields.addAll(FieldFactory.getRtuNetworkFields());
            selectRecordBuilder = selectRecordBuilder.select(allFields).innerJoin(ModuleFactory.getRtuNetworkModule().getTableName())
                    .on(childTableModule.getTableName() + ".NETWORK_ID = " + ModuleFactory.getRtuNetworkModule().getTableName() + ".ID");
        }


  /*      if(context.containsKey(AgentConstants.AGENT_ID)){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(controllerModule), String.valueOf(context.get(AgentConstants.AGENT_ID)), NumberOperators.EQUALS));
        }*/
        selectRecordBuilder.limit(100);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
        if (pagination != null && !fetchCount) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            selectRecordBuilder.offset(offset);
            selectRecordBuilder.limit(perPage);
        } else if (fetchCount) {
            selectRecordBuilder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(controllerModule));
            selectRecordBuilder.select(new ArrayList<>());
        }
        if(controllerType != null) {
        	selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerTypeField(controllerModule), String.valueOf(controllerType), NumberOperators.EQUALS));
        }
        if(StringUtils.isNotEmpty(controllerName)) {
        	selectRecordBuilder.andCustomWhere(resourceModule.getTableName()+".NAME = ?  OR  "+resourceModule.getTableName()+".NAME LIKE ?",controllerName,"%"+controllerName +"%");
        }
        if(containsCheck(AgentConstants.CONTROLLER_ID,context)){
            selectRecordBuilder.andCondition(CriteriaAPI.getIdCondition(String.valueOf(context.get(AgentConstants.CONTROLLER_ID)), controllerModule));
            selectRecordBuilder.select(new ArrayList<>());
        }
        if (filterCriteria != null && !filterCriteria.isEmpty()){
            selectRecordBuilder.andCriteria(filterCriteria);
        }
        if(containsCheck(AgentConstants.AGENT_ID,context)){
            //selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(controllerModule), String.valueOf(context.get(AgentConstants.AGENT_ID)), NumberOperators.EQUALS));
            selectRecordBuilder.andCustomWhere(controllerModule.getTableName()+"."+FieldFactory.getAgentIdField(controllerModule).getName()+"=?",context.get(AgentConstants.AGENT_ID));
        }
            selectRecordBuilder.groupBy(controllerModule.getTableName()+".ID");
            LOGGER.info("Select query for controllers : "+selectRecordBuilder.constructSelectStatement());
            List<Map<String, Object>> result = selectRecordBuilder.get();
            context.put(FacilioConstants.ContextNames.RECORD_LIST, result);
            if (result != null) {
                LOGGER.debug("No. of records fetched for module : " + childTableModuleName + " is " + result.size());
            }
        return false;
    }

}
