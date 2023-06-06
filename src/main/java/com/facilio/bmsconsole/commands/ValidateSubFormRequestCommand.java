package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateSubFormRequestCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long parentFormId = (long) context.getOrDefault(FacilioConstants.ContextNames.PARENT_FORM_ID, -1l);

        if(parentFormId<1){
			throw new IllegalArgumentException("Parent form id is empty");
		}

        FacilioForm subForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (subForm == null) {
            throw new IllegalArgumentException ("Form cannot be empty");
        }

        subForm.setHideInList(true);
        subForm.setAppLinkName(SignupUtil.getSignupApplicationLinkName());
        subForm.setType(FacilioForm.Type.SUB_FORM);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        FacilioModule sectionModule = ModuleFactory.getFormSectionModule();
        Criteria formCriteria = new Criteria();
        formCriteria.addAndCondition(CriteriaAPI.getCondition("Form_Section.FORMID", "formId", String.valueOf(parentFormId), NumberOperators.EQUALS));
        formCriteria.addAndCondition(CriteriaAPI.getCondition("Forms.MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        formCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getField("subFormId", "SUB_FORM_ID", sectionModule, FieldType.NUMBER), CommonOperators.IS_NOT_EMPTY));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFormSectionModule().getTableName())
                .select(FieldFactory.getFormSectionFields())
                .innerJoin(ModuleFactory.getFormModule().getTableName())
                .on("Form_Section.SUB_FORM_ID = Forms.ID")
                .andCriteria(formCriteria);
        List<Map<String, Object>> props = builder.get();

        if (!CollectionUtils.isEmpty(props)) {
            throw new IllegalArgumentException("Subform already exists for the given module");
        }

        return false;
    }

}
