package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DissociateTermsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> recordIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        if(CollectionUtils.isNotEmpty(recordIds)) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            FacilioModule termsAssociatedModule = modBean.getModule(moduleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(moduleName));
            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(termsAssociatedModule.getTableName())
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("terms"), recordIds, NumberOperators.EQUALS));
            deleteRecordBuilder.delete();
        }
        return false;
    }
}
