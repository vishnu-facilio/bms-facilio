package com.facilio.qa.command;

import com.facilio.bmsconsole.activity.QAndAActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.WorkorderCostContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.AnswerContext;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrUpdateAnswerAttachment extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<AnswerContext> addedAnswers = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_ADDED);
        List<AnswerContext> updatedAnswers = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_UPDATED);
        List<AttachmentV3Context> addAttachments = new ArrayList<>();
        List<Long> fileIdsToBeDeleted = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(addedAnswers)) {
            for(AnswerContext addedAnswer : addedAnswers) {
                if(addedAnswer.getAttachmentFileIds()!=null &&addedAnswer.getAttachmentFileIds().size()>0){
                    for(Long attachmentFileId : addedAnswer.getAttachmentFileIds() ) {
                        AttachmentV3Context attachment  = new AttachmentV3Context();
                        attachment.setFileId(attachmentFileId);
                        attachment.setParent(addedAnswer);
                        attachment.setDatum("createdTime",addedAnswer.getSysCreatedTime());
                        addAttachments.add(attachment);
                    }
                }
            }
        }
        if(CollectionUtils.isNotEmpty(updatedAnswers)){
            for(AnswerContext updatedAnswer : updatedAnswers) {
                if(updatedAnswer.getAttachmentFileIds()!=null && updatedAnswer.getAttachmentFileIds().size()>0){
                    for(Long attachmentFileId : updatedAnswer.getAttachmentFileIds() ) {
                        AttachmentV3Context attachment = new AttachmentV3Context();
                        attachment.setFileId(attachmentFileId);
                        attachment.setParent(updatedAnswer);
                        attachment.setDatum("createdTime",updatedAnswer.getSysCreatedTime());
                        addAttachments.add(attachment);
                    }
                }
                if(updatedAnswer.getDeletedAttachmentFileIds()!=null && updatedAnswer.getDeletedAttachmentFileIds().size()>0){
                    for(Long deletedFileId : updatedAnswer.getDeletedAttachmentFileIds()){
                        fileIdsToBeDeleted.add(deletedFileId);
                    }
                }
            }
        }

        if(addAttachments!=null && addAttachments.size()>0) {
            InsertRecordBuilder<AttachmentV3Context> insertBuilder = new InsertRecordBuilder<AttachmentV3Context>()
                    .moduleName(FacilioConstants.QAndA.Answers.ATTACHMENT)
                    .table("Q_And_A_Answer_Attachments")
                    .fields(Constants.getModBean().getAllFields(FacilioConstants.QAndA.Answers.ATTACHMENT))
                    .addRecords(addAttachments);

            insertBuilder.save();
        }

        if(fileIdsToBeDeleted!=null && fileIdsToBeDeleted.size()>0 ){
            List<FacilioField> qandaAnswerAttachmentFields = Constants.getModBean().getAllFields("FacilioConstants.QAndA.Answers.ATTACHMENT");
            Map<String, FacilioField> qandAfieldMap = FieldFactory.getAsMap(qandaAnswerAttachmentFields);
            if(qandAfieldMap!=null && qandAfieldMap.containsKey("file") && qandAfieldMap.get("file")!=null) {
                DeleteRecordBuilder<AttachmentV3Context> deleteBuilder = new DeleteRecordBuilder<AttachmentV3Context>()
                        .moduleName(FacilioConstants.QAndA.Answers.ATTACHMENT)
                        .table("Q_And_A_Answer_Attachments")
                        .andCondition(CriteriaAPI.getCondition(qandAfieldMap.get("file"), StringUtils.join(fileIdsToBeDeleted,','), NumberOperators.EQUALS));
                deleteBuilder.delete();
            }
        }
        return false;
    }
}
