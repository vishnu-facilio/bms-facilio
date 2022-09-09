package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GenericFetchLookUpFieldsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = Constants.getModBean();

        FacilioModule module = modBean.getModule(moduleName);

        Objects.requireNonNull(module,"Module "+moduleName+" doesn't exists.");

        List<FacilioField> fields = modBean.getAllFields(moduleName);

        List<LookupField> fetchLookup = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(fields)) {
            for (FacilioField field : fields) {
                if (field.getDataTypeEnum().equals(FieldType.LOOKUP)) {
                    fetchLookup.add((LookupField) field);
                }
            }
        }

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,fetchLookup);
        return false;
    }
}
