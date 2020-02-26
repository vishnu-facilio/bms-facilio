package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
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


        context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, allFields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(allFields)
                .table(controllerModule.getTableName())
                .innerJoin(childTableModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + childTableModule.getTableName() + ".ID")
                .leftJoin(pointModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + pointModule.getTableName() + "."+FieldFactory.getControllerIdField(pointModule).getColumnName());

  /*      if(context.containsKey(AgentConstants.AGENT_ID)){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(controllerModule), String.valueOf(context.get(AgentConstants.AGENT_ID)), NumberOperators.EQUALS));
        }*/
        selectRecordBuilder.limit(100);
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


        if(containsCheck(AgentConstants.AGENT_ID,context)){
            selectRecordBuilder.andCustomWhere(controllerModule.getTableName()+"."+FieldFactory.getAgentIdField(controllerModule).getName()+"=?",context.get(AgentConstants.AGENT_ID));
        }
            selectRecordBuilder.groupBy(controllerModule.getTableName()+".ID");
            List<Map<String, Object>> result = selectRecordBuilder.get();
            context.put(FacilioConstants.ContextNames.RECORD_LIST, result);
            LOGGER.info(" query "+selectRecordBuilder.toString());
            if (result != null) {
                LOGGER.debug("No of records fetched for module : " + childTableModuleName + " is " + result.size());
            }
        return false;
    }

}
