package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Setter
public class BmsPointsTaggingAction extends FacilioAction{

    String name;
    String value;

    public String updateInternalApi() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        HashMap<String,Object> row= new HashMap<>();
        row.put("updated",0);

        GenericUpdateRecordBuilder builder1 = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition("IS_COMMISSIONED", "updated", "0", NumberOperators.GREATER_THAN));
        builder1.update(row);
        setResult(FacilioConstants.ContextNames.RESULT, "success");
        return SUCCESS;


    }
    public String deleteInternalApi() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");

        GenericDeleteRecordBuilder builder1 = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("IS_COMMISSIONED", "updated", "0", NumberOperators.GREATER_THAN_EQUAL));
        builder1.delete();
        setResult(FacilioConstants.ContextNames.RESULT, "success");
        return SUCCESS;

    }

    public String insertOrgInfo() throws Exception{
        CommonCommandUtil.insertOrgInfo(name,value);
        setResult(FacilioConstants.ContextNames.RESULT, "success");
        return SUCCESS;

    }

}
