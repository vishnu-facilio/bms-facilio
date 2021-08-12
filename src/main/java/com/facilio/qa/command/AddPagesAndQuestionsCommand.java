package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AddPagesAndQuestionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<QAndATemplateContext> list = Constants.getRecordListFromMap(recordMap, moduleName);

        ModuleBean modBean = Constants.getModBean();
        FacilioField questionPageField = modBean.getField("page", FacilioConstants.QAndA.QUESTION);
        FacilioField pageTemplateField = modBean.getField("parent", FacilioConstants.QAndA.PAGE);

        for (QAndATemplateContext template : list) {
            List<PageContext> pages = template.getPages();
            if (CollectionUtils.isNotEmpty(pages)) {
                int pageSequence = 1;
                for (PageContext page : pages) {
                    page.setPosition(pageSequence++);
                    handleQuestions(template, page, questionPageField);
                }
                setRelations(template, FacilioConstants.QAndA.PAGE, pageTemplateField, pages);
            }
            template.setPages(null);
        }

        return false;
    }

    private void setRelations (V3Context record, String subModuleName, FacilioField lookupParentField, List<PageContext> subRecords) {
        SubFormContext<PageContext> subFormContext = new SubFormContext<>();
        subFormContext.setFieldId(lookupParentField.getFieldId());
        subFormContext.setData(subRecords);
        record.setRelations(Collections.singletonMap(subModuleName, Collections.singletonList(subFormContext)));
    }

    private void handleQuestions (QAndATemplateContext template, PageContext page, FacilioField questionPageField) throws Exception {
        List<QuestionContext> pageQuestions = page.getQuestions();
        if (CollectionUtils.isNotEmpty(pageQuestions)) {
            Map<String, List<SubFormContext>> questions = new HashMap<>();
            int questionSequence = 1;
            for (QuestionContext question : pageQuestions) {
                question.setParent(template);
                question.setPosition(questionSequence++);
                addToRelation(questions, question, questionPageField);
            }
            page.setRelations(questions);
        }
        page.setQuestions(null);
    }

    private void addToRelation (Map<String, List<SubFormContext>> questionRelations, QuestionContext question, FacilioField questionPageField) throws Exception {
        V3Util.throwRestException(question.getQuestionType() == null, ErrorCode.VALIDATION_ERROR, "Question type cannot be null");
        String moduleName = question.getQuestionType().getSubModuleName();
        List<SubFormContext> questions = questionRelations.get(moduleName);
        if (questions == null) {
            SubFormContext<QuestionContext> subFormContext = new SubFormContext<>();
            subFormContext.setFieldId(questionPageField.getFieldId());
            questions = Collections.singletonList(subFormContext);
            List<QuestionContext> questionList = new ArrayList<>();
            subFormContext.setData(questionList);
        }
        questions.get(0).getData().add(question);
    }
}
