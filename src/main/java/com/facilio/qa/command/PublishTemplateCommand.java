package com.facilio.qa.command;

import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.util.InspectionAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PublishTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long templateId = (Long) context.get(FacilioConstants.QAndA.TEMPLATE_ID);
        FacilioField publishField = Constants.getModBean().getField("isPublished", FacilioConstants.QAndA.Q_AND_A_TEMPLATE);
        if(publishField!=null && templateId!=null) {
            QAndATemplateContext qAndATemplate = V3RecordAPI.getRecord(FacilioConstants.QAndA.Q_AND_A_TEMPLATE, templateId, QAndATemplateContext.class);
            qAndATemplate.setIsPublished(true);
            V3RecordAPI.updateRecord(qAndATemplate, Constants.getModBean().getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE), Collections.singletonList(publishField));
            if(qAndATemplate.getQAndAType().getTemplateModule().equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)) {
                Map<String, FacilioField> triggerFieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
                List<InspectionTriggerContext> triggers = InspectionAPI.getInspectionTrigger(CriteriaAPI.getCondition(triggerFieldMap.get("parent"), Collections.singleton(templateId), NumberOperators.EQUALS), true);
                triggers.forEach(trigger -> trigger.getParent().setIsPublished(true));
                FacilioChain inspectionGenerationChain = TransactionChainFactory.getInspectionGenerationChain();
                FacilioContext inspectionGenerationContext = inspectionGenerationChain.getContext();
                inspectionGenerationContext.put(FacilioConstants.Inspection.INSPECTION_TRIGGERS,triggers);
                inspectionGenerationChain.execute();
            }
        }
        return false;
    }
}
