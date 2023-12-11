package com.facilio.qa.command;

import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.util.InspectionAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QAndAType;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CloneQAndAExtendedTemplateCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long templateId = (Long) context.get(FacilioConstants.QAndA.OLD_TEMPLATE_ID);
        String newTemplateName = (String) context.get(FacilioConstants.QAndA.CLONED_TEMPLATE_NAME);
        V3Util.throwRestException(templateId == null || templateId <= 0, ErrorCode.VALIDATION_ERROR, "Template Id to be cloned is empty");
        V3Util.throwRestException(newTemplateName == null, ErrorCode.VALIDATION_ERROR, "Template Name is empty");
        QAndATemplateContext qandaTemplate = V3RecordAPI.getRecord(FacilioConstants.QAndA.Q_AND_A_TEMPLATE, templateId);
        QAndAType qAndAType = qandaTemplate.getQAndAType();
        V3Util.throwRestException(qAndAType == null, ErrorCode.VALIDATION_ERROR, "Error occured while fetching the template data");
//        QAndATemplateContext templateObject = qAndAType.getTemplateClass().getDeclaredConstructor().newInstance();
        QAndATemplateContext templateObject = V3RecordAPI.getRecord(qAndAType.getTemplateModule(), templateId);
        templateObject.setId(-1);
        templateObject.setName(newTemplateName);
        Map<String, Object> templateMap=FieldUtil.getAsProperties(templateObject);
        if(qAndAType.getTemplateModule().equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)){
            Map<String, FacilioField> triggerFieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
            List<InspectionTriggerContext> triggers = InspectionAPI.getInspectionTrigger(CriteriaAPI.getCondition(triggerFieldMap.get("parent"), Collections.singleton(templateId), NumberOperators.EQUALS), true);
            if(triggers!=null){
                triggers = triggers.stream().map(trigger -> {
                    trigger.setId(-1);
                    return trigger;
                }).collect(Collectors.toList());

            }
            templateMap.put("triggers",FieldUtil.getAsMapList(triggers,InspectionTriggerContext.class));
        }
        FacilioContext clonedQandATemplate = V3Util.createRecord(Constants.getModBean().getModule(qAndAType.getTemplateModule()),templateMap);
        List<? extends QAndATemplateContext> recordList = Constants.getRecordList(clonedQandATemplate);
        if (CollectionUtils.isNotEmpty(recordList)) {
            context.put(FacilioConstants.QAndA.CLONED_Q_AND_A_TEMPLATE, recordList.get(0)!=null ? recordList.get(0):null);
            context.put(FacilioConstants.QAndA.CLONED_Q_AND_A_TEMPLATE_ID, recordList.get(0)!=null ? recordList.get(0).getId():null);
        }
        return false;
    }
}
