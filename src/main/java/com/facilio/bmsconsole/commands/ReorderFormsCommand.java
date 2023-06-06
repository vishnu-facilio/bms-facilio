package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReorderFormsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        long nextFormId = (long) context.get(FacilioConstants.FormContextNames.NEXT_FORM_ID);
        long previousFormId = (long) context.get(FacilioConstants.FormContextNames.PREVIOUS_FORM_ID);
        long appId = (long) context.get(FacilioConstants.ContextNames.APP_ID);

        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Invalid module name");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        long moduleId = module.getModuleId();

        double formSequenceNumber = FormsAPI.getFormSequenceNumber(previousFormId, nextFormId, moduleId, appId);

        FacilioForm form = FormsAPI.getFormFromDB(formId,false);
        context.put(FacilioConstants.ContextNames.FORM, FieldUtil.getAsProperties(form));

        FacilioModule formModule = ModuleFactory.getFormModule();
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(formModule.getTableName())
                .fields(Collections.singletonList(FieldFactory.getField("sequenceNumber", "SEQUENCE_NUMBER", formModule, FieldType.DECIMAL)))
                .andCondition(CriteriaAPI.getIdCondition(formId, formModule));
        Map<String, Object> map = new HashMap<>();
        map.put("sequenceNumber", formSequenceNumber);
        builder.update(map);


        return false;
    }
}
