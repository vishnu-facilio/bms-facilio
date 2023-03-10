package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReplacePlaceHoldersCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(ReplacePlaceHoldersCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        String formattedString = (String) context.get(FacilioConstants.ContextNames.FORMATTED_STRING);
        if (StringUtils.isNotEmpty(moduleName) && id > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            List<FacilioField> fields = modBean.getAllFields(moduleName);
            List<FacilioField> supplementFields = fields.stream().filter(f -> f instanceof SupplementRecord).collect(Collectors.toList());

            SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                    .select(fields)
                    .module(module)
                    .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                    .fetchSupplements(supplementFields)
                    .andCondition(CriteriaAPI.getIdCondition(id, module));
            ModuleBaseWithCustomFields moduleData = builder.fetchFirst();

            Map<String, Object> params = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, moduleData, WorkflowRuleAPI.getOrgPlaceHolders());
            String replacedString = StringSubstitutor.replace(formattedString, params);

            JSONObject replacedJsonObj = new JSONObject();
            try {
                JSONParser parser = new JSONParser();
                replacedJsonObj = (JSONObject) parser.parse(replacedString);
                String formatedDataJson = replacedJsonObj.get("formDataJson").toString();
                replacedJsonObj.replace("formDataJson", parser.parse(formatedDataJson));
            }catch(Exception e){
                LOGGER.info("Replace placeholder Exception =>" , e);
            }

            context.put(FacilioConstants.ContextNames.REPLACED_STRING, replacedString);
            context.put(FacilioConstants.ContextNames.REPLACED_JSON,replacedJsonObj);
            context.put(FacilioConstants.ContextNames.WORK_FLOW_PARAMS, params);
            context.put(FacilioConstants.ContextNames.MODULE_DATA, moduleData);
        }
        return false;
    }
}
