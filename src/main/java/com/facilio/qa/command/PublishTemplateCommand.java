package com.facilio.qa.command;

import com.facilio.backgroundactivity.context.BackgroundActivity;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class PublishTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long templateId = (Long) context.get(FacilioConstants.QAndA.TEMPLATE_ID);
        FacilioField publishField = Constants.getModBean().getField("'isPublished'", FacilioConstants.Induction.INDUCTION_TEMPLATE);
        if(publishField!=null && templateId!=null) {
            QAndATemplateContext qAndATemplate = V3RecordAPI.getRecord(FacilioConstants.QAndA.Q_AND_A_TEMPLATE, templateId, QAndATemplateContext.class);
            qAndATemplate.setIsPublished(true);
            V3RecordAPI.updateRecord(qAndATemplate, Constants.getModBean().getModule(qAndATemplate.getQAndAType().getTemplateModule()), Collections.singletonList(publishField));
        }
//        UpdateRecordBuilder<QAndATemplateContext> update = new UpdateRecordBuilder<QAndATemplateContext>()
//                .moduleName(FacilioConstants.QAndA.Q_AND_A_TEMPLATE)
//                .fields(Constants.getModBean().getAllFields(FacilioConstants.QAndA.Q_AND_A_TEMPLATE))
//                .andCondition(CriteriaAPI.getIdCondition(templateId, Constants.getModBean().getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE)));
//
//        update.update(template);
        return false;
    }
}
