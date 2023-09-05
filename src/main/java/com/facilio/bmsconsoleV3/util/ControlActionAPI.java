package com.facilio.bmsconsoleV3.util;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.*;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import java.util.*;
import java.util.stream.Collectors;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.fms.message.Message;

public class ControlActionAPI {
    public static void deleteActions(Long controlActionId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.ACTION_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.ACTION_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        DeleteRecordBuilder<V3ActionContext> builder = new DeleteRecordBuilder<V3ActionContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlAction"),String.valueOf(controlActionId), NumberOperators.EQUALS));
        builder.markAsDelete();
    }
    public static void createAction(List<V3ActionContext> actionContextList) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.ACTION_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.ACTION_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        InsertRecordBuilder<V3ActionContext> builder = new InsertRecordBuilder<V3ActionContext>()
                .module(module)
                .fields(fieldList)
                .addRecords(actionContextList);
        builder.save();
    }
    public static void generateCommand(Long controlActionId) throws Exception{
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put(FacilioConstants.Control_Action.CONTROL_ACTION_ID, controlActionId);
        Messenger.getMessenger().sendMessage(new Message()
                .setKey("controlAction/" + controlActionId)
                .setOrgId(orgId)
                .setContent(message)
        );
    }
    public static List<V3AssetContext> getFilteredAssets(Criteria siteCriteria, Criteria assetCriteria, Criteria controllerCriteria, V3AssetCategoryContext assetCategoryContext) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        SelectRecordsBuilder<V3AssetContext> builder = new SelectRecordsBuilder<V3AssetContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3AssetContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("category"),String.valueOf(assetCategoryContext.getId()),NumberOperators.EQUALS));
        if(siteCriteria != null){
            builder.andCriteria(siteCriteria);
        }
        if(assetCriteria != null){
            builder.andCriteria(assetCriteria);
        }
        if(controllerCriteria != null){
            builder.andCriteria(controllerCriteria);
        }
        return builder.get();
    }
    public static V3ControlActionContext getControlAction(Long controlActionId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        SelectRecordsBuilder<V3ControlActionContext> builder = new SelectRecordsBuilder<V3ControlActionContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3ControlActionContext.class)
                .andCondition(CriteriaAPI.getIdCondition(controlActionId,module));
        V3ControlActionContext controlActionContext = builder.fetchFirst();
        controlActionContext.setActionContextList(getActionOfControlAction(controlActionId));
        if(controlActionContext.getSiteCriteriaId() != null) {
            controlActionContext.setSiteCriteria(CriteriaAPI.getCriteria(controlActionContext.getSiteCriteriaId()));
        }
        if(controlActionContext.getAssetCriteriaId() != null) {
            controlActionContext.setSiteCriteria(CriteriaAPI.getCriteria(controlActionContext.getAssetCriteriaId()));
        }
        if(controlActionContext.getControllerCriteriaId() != null){
            controlActionContext.setControllerCriteria(CriteriaAPI.getCriteria(controlActionContext.getControllerCriteriaId()));
        }
        return controlActionContext;
    }
    public static List<V3ActionContext> getActionOfControlAction(Long controlActionId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.ACTION_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.ACTION_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        SelectRecordsBuilder<V3ActionContext> builder = new SelectRecordsBuilder<V3ActionContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3ActionContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlAction"),String.valueOf(controlActionId),NumberOperators.EQUALS));
        return builder.get();
    }
    public static void setReadingValueForCommand(V3CommandsContext commandsContext,V3ActionContext actionContext) throws Exception{
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        V3AssetContext assetContext = commandsContext.getAsset();
        FacilioField field = moduleBean.getField(commandsContext.getFieldId());
        Object actionValue = actionContext.getScheduleActionValue();
        V3ActionContext.ActionOperatorTypeEnum actionOperatorTypeEnum = actionContext.getScheduledActionOperatorTypeEnum();
        if(commandsContext.getCommandActionType() == V3CommandsContext.CommandActionType.REVERT_ACTION.getVal()){
            actionValue = actionContext.getRevertActionValue();
            actionOperatorTypeEnum = actionContext.getRevertActionOperatorTypeEnum();
        }
        ReadingDataMeta readingDataMeta = ReadingsAPI.getReadingDataMeta(assetContext.getId(), field);
        //Todo in case of null the getValue returns -1
        Object value = readingDataMeta.getValue();
            if(value instanceof Double) {
                if (actionOperatorTypeEnum == V3ActionContext.ActionOperatorTypeEnum.DECREASED_BY) {
                    actionValue = Double.parseDouble(String.valueOf(value)) - Double.parseDouble(String.valueOf(actionValue));
                } else if (actionOperatorTypeEnum == V3ActionContext.ActionOperatorTypeEnum.INCREASED_BY) {
                    actionValue = Double.parseDouble(String.valueOf(value)) + Double.parseDouble(String.valueOf(actionValue));
                }
            }
        commandsContext.setSetValue(actionValue);
        commandsContext.setPreviousValue(value);
        commandsContext.setPreviousValueCapturedTime(readingDataMeta.getTtime());
        return;
    }
    public static void createCommand(List<V3CommandsContext> commandsContextList) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        InsertRecordBuilder<V3CommandsContext> builder = new InsertRecordBuilder<V3CommandsContext>()
                .module(module)
                .fields(fieldList)
                .addRecords(commandsContextList);
        builder.save();
    }
    public static List<V3CommandsContext> pickCommandsToBeExecuted(Long controlActionId, Long actionTime) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        SelectRecordsBuilder<V3CommandsContext> builder = new SelectRecordsBuilder<V3CommandsContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3CommandsContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlAction"),String.valueOf(controlActionId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("actionTime"),String.valueOf(actionTime),NumberOperators.EQUALS));
        return builder.get();
    }
    public static int updateCommand(V3CommandsContext commandsContext) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        UpdateRecordBuilder<V3CommandsContext> builder = new UpdateRecordBuilder<V3CommandsContext>()
                .module(module)
                .fields(fieldList)
                .andCondition(CriteriaAPI.getIdCondition(commandsContext.getId(),module));
        return builder.update(commandsContext);

    }
    public static void generateControlActionFromTemplateWms(Long controlActionTemplateId,Long startTime, Long endTime){
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ID, controlActionTemplateId);
        message.put("startTime",startTime);
        message.put("endTime",endTime);
        Messenger.getMessenger().sendMessage(new Message()
                .setKey("controlActionTemplate/" + controlActionTemplateId)
                .setOrgId(orgId)
                .setContent(message)
        );
    }
    public static V3ControlActionTemplateContext getControlActionTemplate(Long controlActionTemplateId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        SelectRecordsBuilder<V3ControlActionTemplateContext> builder = new SelectRecordsBuilder<V3ControlActionTemplateContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3ControlActionTemplateContext.class)
                .andCondition(CriteriaAPI.getIdCondition(controlActionTemplateId,module));

        V3ControlActionTemplateContext controlActionTemplateContext = builder.fetchFirst();
        controlActionTemplateContext.setActionContextList(getActionOfControlAction(controlActionTemplateId));
        return controlActionTemplateContext;
    }
    public static void dropControlActionCommands(Long controlActionId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        Long currentMills = System.currentTimeMillis();
        DeleteRecordBuilder<V3CommandsContext> builder = new DeleteRecordBuilder<V3CommandsContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlAction"),String.valueOf(controlActionId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("actionTime"),String.valueOf(currentMills),NumberOperators.GREATER_THAN));
        builder.markAsDelete();
        return;
    }
    public static List<PeopleContext>getApprovalList(Long controlActionId,String moduleName) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fieldList = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fieldList)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("left"),String.valueOf(controlActionId),NumberOperators.EQUALS));
        List<Map<String,Object>> prop =  builder.get();
        List<PeopleContext> peopleIdList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(prop)){
            for(Map<String,Object> map : prop){
               peopleIdList.add(PeopleAPI.getPeopleForId((Long) map.get("right")));
            }
        }
        return peopleIdList;
    }
    public static List<V3ControlActionTemplateContext> getControlActionTemplateForNightlyJobExecution() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        SelectRecordsBuilder<V3ControlActionTemplateContext> builder = new SelectRecordsBuilder<V3ControlActionTemplateContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3ControlActionTemplateContext.class);
        return builder.get();
    }
    public static Long getGeneratedUpToTimeForControlActionTemplate(Long controlActionTemplateId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        SelectRecordsBuilder<V3ControlActionContext> builder = new SelectRecordsBuilder<V3ControlActionContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3ControlActionContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlActionTemplate"),String.valueOf(controlActionTemplateId),NumberOperators.EQUALS))
                .orderBy("ID DESC").limit(1);
        return builder.fetchFirst().getScheduledActionDateTime();
    }
    public static void dropControlActionsOfControlActionTemplate(Long controlActionTemplateId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);

        SelectRecordsBuilder<V3ControlActionContext> builder = new SelectRecordsBuilder<V3ControlActionContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3ControlActionContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlActionTemplate"),String.valueOf(controlActionTemplateId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("scheduledActionDateTime"),String.valueOf(System.currentTimeMillis()),NumberOperators.GREATER_THAN));
       List<V3ControlActionContext> controlActionContextList =  builder.get();

       if(CollectionUtils.isNotEmpty(controlActionContextList)){
           List<Long> controlActionIds = controlActionContextList.stream().map(V3ControlActionContext::getId).collect(Collectors.toList());
           DeleteRecordBuilder<V3ControlActionContext> deleteRecordBuilder = new DeleteRecordBuilder<V3ControlActionContext>()
                   .module(module)
                   .andCondition(CriteriaAPI.getIdCondition(controlActionIds,module));
           deleteRecordBuilder.markAsDelete();
           for(V3ControlActionContext v3ControlActionContext : controlActionContextList){
               dropControlActionCommands(v3ControlActionContext.getId());
               deleteActions(v3ControlActionContext.getId());
           }

       }
        return;
    }
    public static int updateControlAction(V3ControlActionContext controlActionContext) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);

        UpdateRecordBuilder<V3ControlActionContext> builder = new UpdateRecordBuilder<V3ControlActionContext>()
                .module(module)
                .fields(fieldList)
                .andCondition(CriteriaAPI.getIdCondition(controlActionContext.getId(),module));
        return builder.update(controlActionContext);

    }
    public static boolean checkForControlActionCompletedStatus(Long controlActionId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);
        Long currentMills = System.currentTimeMillis();
        SelectRecordsBuilder<V3CommandsContext> builder = new SelectRecordsBuilder<V3CommandsContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3CommandsContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlAction"),String.valueOf(controlActionId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("actionTime"),String.valueOf(currentMills),NumberOperators.GREATER_THAN));
        List<V3CommandsContext> commandsContextList = builder.get();
        if(CollectionUtils.isEmpty(commandsContextList)){
            return true;
        }
        return false;
    }
    public static V3ControlActionContext.ControlActionStatus getCompletionTypeOfControlAction(Long controlActionId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        List<FacilioField> fieldList = modBean.getAllFields(FacilioConstants.Control_Action.COMMAND_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fieldList);
        SelectRecordsBuilder<V3CommandsContext> builder = new SelectRecordsBuilder<V3CommandsContext>()
                .module(module)
                .select(fieldList)
                .beanClass(V3CommandsContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlAction"),String.valueOf(controlActionId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("controlActionCommandStatus"),String.valueOf(V3CommandsContext.ControlActionCommandStatus.SUCCESS.getVal()),NumberOperators.NOT_EQUALS));
        List<V3CommandsContext> commandsContextList = builder.get();
        if(CollectionUtils.isEmpty(commandsContextList)){
            return V3ControlActionContext.ControlActionStatus.COMPLETED;
        }
        return V3ControlActionContext.ControlActionStatus.COMPLETED_WITH_ERROR;
    }


}
