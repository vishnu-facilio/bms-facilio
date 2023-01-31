package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import nl.basjes.shaded.org.springframework.util.CollectionUtils;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;


public class ValidateBeforeButtonDeletionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        List<Long> record= (List<Long>) context.get("recordIds");
        long id= record.get(0);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(modBean.getAllFields(FacilioConstants.ContextNames.CUSTOM_DEVICE_BUTTON_MAPPING))
                .table(modBean.getModule(FacilioConstants.ContextNames.CUSTOM_DEVICE_BUTTON_MAPPING).getTableName())
                .andCondition(CriteriaAPI.getCondition("RIGHT_ID","right", String.valueOf(id), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if(props.size() > 0){
            throw new IllegalArgumentException("Used button cannot be deleted");
        }

        return false;
    }
}
