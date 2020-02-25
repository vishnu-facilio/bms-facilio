package com.facilio.agentv2.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.GenericGetModuleDataListCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetControllerCommand extends AgentV2Command {
    private static final Logger LOGGER = LogManager.getLogger(GenericGetModuleDataListCommand.class.getName());

    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        LOGGER.info(" in get controllerdata chain ");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String childTableModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule controllerModule = ModuleFactory.getNewControllerModule();
        FacilioModule pointModule = ModuleFactory.getPointModule();
        LOGGER.info(" modules obtained ");
        if (childTableModuleName == null) {
            throw new Exception(" module name can't be null ");
        }
        FacilioModule childTableModule = modBean.getModule(childTableModuleName);
        List<FacilioField> allFields = new ArrayList<>();
        allFields.addAll(modBean.getModuleFields(controllerModule.getName()));

        allFields.addAll(modBean.getModuleFields(childTableModule.getName()));
     /*  if (childFields == null) {
            throw new Exception(" child fields cant be empty " + childTableModuleName);
        }
        fields.addAll(childFields);*/
        for (FacilioField allField : allFields) {
            System.out.println(allField.getName());
        }



        System.out.println(1);
        allFields.add(FieldFactory.getConfigurationInProgressPointCountConditionField());
        System.out.println(2);
        allFields.add(FieldFactory.getSubscriptionInProgressPointCountConditionField());
        System.out.println(3);
        allFields.add(FieldFactory.getSubscribedPointCountConditionField());
        System.out.println(4);
        allFields.add(FieldFactory.getConfiguredPointCountConditionField());
        System.out.println(5);

        context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, allFields);
        LOGGER.info(" fields size "+allFields.size());
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(allFields)
                .table(controllerModule.getTableName())
                .innerJoin(childTableModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + childTableModule.getTableName() + ".ID")
                .innerJoin(pointModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + pointModule.getTableName() + "."+FieldFactory.getControllerIdField(pointModule).getColumnName());

  /*      if(context.containsKey(AgentConstants.AGENT_ID)){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(controllerModule), String.valueOf(context.get(AgentConstants.AGENT_ID)), NumberOperators.EQUALS));
        }*/

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            selectRecordBuilder.offset(offset);
            selectRecordBuilder.limit(perPage);
        }
            selectRecordBuilder.groupBy(controllerModule.getTableName()+".ID");
            List<Map<String, Object>> result = selectRecordBuilder.get();
        System.out.println(result);
            LOGGER.info("- - - - select controllers - - - - " + selectRecordBuilder.toString());
            context.put(FacilioConstants.ContextNames.RECORD_LIST, result);
            if (result != null) {
                LOGGER.debug("No of records fetched for module : " + childTableModuleName + " is " + result.size());
            }
        return false;
    }

}
