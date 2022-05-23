package com.facilio.bmsconsole.formFactory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;


public class FormFactoryMigration {

    private static final Logger logger = LogManager.getLogger(FormFactoryMigration.class.getName());

    public static void formMigration(Map<String, Map<String, FacilioForm>> moduleNameVsFormNameVsForm) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        long orgId = AccountUtil.getCurrentOrg().getId();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFormFields())
                .table(ModuleFactory.getFormModule().getTableName());
//                .andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));

        List<FacilioForm> dbFormsList = FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(), FacilioForm.class);

        Map<String, Map<String, FacilioForm>> moduleNameVsFormNameVsDbForms = new HashMap<>();
        Map<String, List<FacilioForm>> moduleNameVsDbForms = new HashMap<>();
        Set<String> moduleNameList = moduleNameVsFormNameVsForm.keySet();
        moduleNameList.forEach(moduleName -> moduleNameVsDbForms.put(moduleName, new ArrayList<>()));

        for (FacilioForm dbForm : dbFormsList) {
            long moduleId = dbForm.getModuleId();
            FacilioModule facilioModule = modBean.getModule(moduleId);
            dbForm.setModule(facilioModule);
            String moduleName = dbForm.getModule().getName();
            if (moduleNameVsDbForms.containsKey(moduleName)) {
                moduleNameVsDbForms.get(moduleName).add(dbForm);
            } else {
                List<FacilioForm> dbForms = new ArrayList<>();
                dbForms.add(dbForm);
                moduleNameVsDbForms.put(moduleName, dbForms);
            }
        }

        Set<String> moduleNames = moduleNameVsDbForms.keySet();

        for (String module : moduleNames) {
            Map<String, FacilioForm> formNameVsFormMap = new HashMap<>();
            List<FacilioForm> formList = moduleNameVsDbForms.get(module);
            for (FacilioForm form : formList) {
                String formName = form.getName();
                formNameVsFormMap.put(formName, form);
            }
            moduleNameVsFormNameVsDbForms.put(module, formNameVsFormMap);
        }


        for (String key : moduleNameList) {

            Map<String, FacilioForm> dbFormList = moduleNameVsFormNameVsDbForms.get(key);
            Map<String, FacilioForm> moduleFormList = moduleNameVsFormNameVsForm.get(key);

            Set<String> dbFormNameList = dbFormList.keySet();

            Set<String> moduleFormNameList = moduleFormList.keySet();

            for (String moduleFormName : moduleFormNameList) {
                if (!dbFormNameList.contains(moduleFormName)) {
                    FacilioModule facilioModule = modBean.getModule(key);

                    FacilioChain newForm = FacilioFormChainFactory.getAddFormCommand();
                    FacilioContext context=newForm.getContext();
                    context.put(FacilioConstants.ContextNames.MODULE_NAME,facilioModule.getName());
                    context.put(FacilioConstants.ContextNames.FORM,moduleFormList.get(moduleFormName));
                    newForm.execute();
                }
            }
        }
    }
}