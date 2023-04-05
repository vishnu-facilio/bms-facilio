package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.MultiImportField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetMultiImportFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);

        FacilioUtil.throwIllegalArgumentException(module == null ,"Module not found");

        List<FacilioField> facilioFields = modBean.getAllFields(moduleName);

        List<MultiImportField> multiImportField = new ArrayList<>();
        for(FacilioField facilioField : facilioFields){
            MultiImportField importField =  new MultiImportField();
            importField.setField(facilioField);
            if(facilioField.isRequired()){
                importField.setMandatory(true);
            }
            multiImportField.add(importField);
        }
        context.put(FacilioConstants.ContextNames.FIELDS,multiImportField);

        return false;
    }
}
