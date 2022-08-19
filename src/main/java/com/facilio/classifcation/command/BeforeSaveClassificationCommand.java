package com.facilio.classifcation.command;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.classifcation.context.ClassificationAttributeContext;
import com.facilio.classifcation.context.ClassificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeforeSaveClassificationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ClassificationContext> classificationList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);

        if (CollectionUtils.isEmpty(classificationList)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR);
        }

        for (ClassificationContext classificationContext : classificationList) {
            validateClassification(classificationContext);

            classificationContext.setLinkName(generateLinkName(classificationContext.getName()));

            // create classification modules
            createClassificationModuleAndFields(classificationContext);
        }
        return false;
    }

    private void validateClassification(ClassificationContext classificationContext) {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(classificationContext.getName()), "Classification name cannot be empty");
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(classificationContext.getAppliedModuleIds()), "Classification should be applied to at least one module");
    }

    private String generateLinkName(String name) throws Exception {
        String linkName = name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        // check this name exists before
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule classificationModule = modBean.getModule(FacilioConstants.ContextNames.CLASSIFICATION);
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(classificationModule)
                .beanClass(ClassificationContext.class)
                .select(modBean.getAllFields(classificationModule.getName()))
                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", linkName, StringOperators.STARTS_WITH));
        List<Map<String, Object>> maps = builder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            // TODO
        }
        return linkName;
    }

    private FacilioModule createClassificationModuleAndFields(ClassificationContext classification) throws Exception {
        FacilioModule module = new FacilioModule();
        module.setName("__classification_module_" + classification.getLinkName());
        module.setDisplayName("Classification Module for " + classification.getName());
        module.setType(FacilioModule.ModuleType.CUSTOM);
        module.setTableName("Classification_Data");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long classificationModuleId = modBean.addModule(module);
        module.setModuleId(classificationModuleId);
        classification.setClassificationModuleId(classificationModuleId);

        // add fields
        FacilioField mainField = FieldFactory.getNumberField("localId", "LOCAL_ID", module);
        mainField.setMainField(true);
        mainField.setDefault(true);
        mainField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        modBean.addField(mainField);

        if (CollectionUtils.isNotEmpty(classification.getAttributes())) {
            List<FacilioField> fields = new ArrayList<>();
            for (ClassificationAttributeContext attribute : classification.getAttributes()) {
                FacilioField field = attribute.generateField();
                fields.add(field);
            }

            FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
            FacilioContext context = addFieldsChain.getContext();
            context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
            context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
            addFieldsChain.execute();
        }

        return module;
    }
}
