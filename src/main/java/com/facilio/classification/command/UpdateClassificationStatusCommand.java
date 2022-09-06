package com.facilio.classification.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateClassificationStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long classificationId=(long)context.get(FacilioConstants.ContextNames.CLASSIFICATION_ID);
        Boolean status=(Boolean)context.get(FacilioConstants.ContextNames.STATUS);
        FacilioUtil.throwIllegalArgumentException(classificationId==-1,"ClassificationId cannot be empty");
        String moduleName=FacilioConstants.ContextNames.CLASSIFICATION;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = modBean.getField("status", moduleName);
        FacilioModule module =modBean.getModule(moduleName);
        validateClassificationId(classificationId,modBean.getField("id",moduleName),module);

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Arrays.asList(field))
                .andCondition(CriteriaAPI.getIdCondition(classificationId, module));
        Map<String,Object> prop=new HashMap<>();
        prop.put(field.getName(), status);
        updateBuilder.update(prop);

        return false;
    }
    private void validateClassificationId(Long classificationId,FacilioField field,FacilioModule module) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder=new GenericSelectRecordBuilder()
                .select(Arrays.asList(field))
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(classificationId,module));
       List<Map<String,Object>> res=selectRecordBuilder.get();
       FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(res),"Invalid Classification ID");


    }
}
