package com.facilio.qa.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddQAndAModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule qAndA = constructQAndA();
        modules.add(qAndA);

        FacilioModule page = constructPage(qAndA);
        modules.add(page);

        FacilioModule question = constructQuestion(qAndA, page);
        modules.add(question);

        FacilioModule response = constructResponse(qAndA);
        modules.add(response);

        FacilioModule answer = constructAnswer(qAndA, response, question);
        modules.add(answer);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        ModuleBean modBean = Constants.getModBean();
        addQuestionSubModules(question);
        addAnswerSubModules(modBean, answer);
        addRollUpFields(modBean, qAndA, response, page, question, answer);

        changeSysCreatedTimeFieldDisplayName(response);

        addAnswerAttachmentModule(answer);
        
    }

	private void changeSysCreatedTimeFieldDisplayName(FacilioModule response) throws Exception {

        ModuleBean modBean = Constants.getModBean();

        FacilioField createdTimeField = modBean.getField("sysCreatedTime", response.getName());

        createdTimeField.setDisplayName("System Created Time");
        modBean.updateField(createdTimeField);
    }

    private void addAnswerAttachmentModule(FacilioModule answer) throws Exception {

        FacilioModule module = new FacilioModule();
        module.setName(FacilioConstants.QAndA.Answers.ATTACHMENT);
        module.setDisplayName(answer.getDisplayName() + " Attachments");
        module.setTableName("Q_And_A_Answer_Attachments");
        module.setType(FacilioModule.ModuleType.ATTACHMENTS);

        List<FacilioField> fields = new ArrayList<FacilioField>();

        FacilioField attachmentCreatedTime = FieldFactory.getField("createdTime", "Created Time", "CREATED_TIME", module, FieldType.NUMBER);
        fields.add(attachmentCreatedTime);

        FileField fileField = (FileField) FieldFactory.getField("file", "File ID", "FILE_ID", module, FieldType.FILE);
        fileField.setDefault(true);
        fields.add(fileField);

        LookupField attachmentParentId = (LookupField) FieldFactory.getField("parent", "PARENT Id", "ANSWER_ID", module, FieldType.LOOKUP);
        attachmentParentId.setDefault(true);
        attachmentParentId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        attachmentParentId.setLookupModule(answer);
        attachmentParentId.setMainField(Boolean.TRUE);
        fields.add(attachmentParentId);
        module.setFields(fields);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();


        Constants.getModBean().addSubModule(answer.getModuleId(), module.getModuleId());
    }

    private FacilioModule constructQAndA() {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE,
                "Q And A Templates",
                "Q_And_A_Templates",
                FacilioModule.ModuleType.Q_AND_A,
                true);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        SystemEnumField typeField = (SystemEnumField) FieldFactory.getDefaultField("type", "Type", "Q_AND_A_TYPE", FieldType.SYSTEM_ENUM);
        typeField.setEnumName("QAndAType");
        fields.add(typeField);
        fields.add(FieldFactory.getDefaultField("totalPages", "Total Pages", "TOTAL_PAGES", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("totalQuestions", "Total Questions", "TOTAL_QUESTIONS", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("totalResponses", "Total Responses", "TOTAL_RESPONSES", FieldType.NUMBER));
        BooleanField isPublished = (BooleanField) FieldFactory.getDefaultField("isPublished", "Published", "IS_PUBLISHED", FieldType.BOOLEAN);
        fields.add(isPublished);
        fields.add(FieldFactory.getBaseModuleSystemField("formId",module));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructPage(FacilioModule qAndA) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.PAGE,
                "Page",
                "Q_And_A_Pages",
                FacilioModule.ModuleType.SUB_ENTITY,
                true);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        fields.add(FieldFactory.getDefaultField("position", "Position", "POSITION_VAL", FieldType.NUMBER));
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(qAndA);
        fields.add(parentField);

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructQuestion(FacilioModule qAndA, FacilioModule page) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.QUESTION,
                "Questions",
                "Q_And_A_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                true);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("question", "Question", "QUESTION", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        fields.add(FieldFactory.getDefaultField("position", "Position", "POSITION_VAL", FieldType.NUMBER));
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(qAndA);
        fields.add(parentField);
        LookupField groupField = (LookupField) FieldFactory.getDefaultField("page", "Page", "PAGE_ID", FieldType.LOOKUP);
        groupField.setLookupModule(page);
        fields.add(groupField);
        StringSystemEnumField typeField = (StringSystemEnumField) FieldFactory.getDefaultField("questionType", "Type", "QUESTION_TYPE", FieldType.STRING_SYSTEM_ENUM);
        typeField.setEnumName("QuestionType");
        fields.add(typeField);
        fields.add(FieldFactory.getDefaultField("commentsLabel", "Comments Label", "COMMENTS_LABEL", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("attachmentLabel", "Attachments Label", "ATTACHMENTS_LABEL", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("mandatory", "Is Mandatory?", "MANDATORY", FieldType.BOOLEAN));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructResponse(FacilioModule qAndA) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.RESPONSE,
                "Q And A Responses",
                "Q_And_A_Responses",
                FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                true);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING,true));
        LookupField templateField = (LookupField) FieldFactory.getDefaultField("template", "Parent Template", "TEMPLATE_ID", FieldType.LOOKUP);
        templateField.setLookupModule(qAndA);
        fields.add(templateField);
        SystemEnumField typeField = (SystemEnumField) FieldFactory.getDefaultField("type", "Type", "Q_AND_A_TYPE", FieldType.SYSTEM_ENUM);
        typeField.setEnumName("QAndAType");
        fields.add(typeField);
        SystemEnumField statusField = (SystemEnumField) FieldFactory.getDefaultField("responseStatus", "Completion Status", "RESPONSE_STATUS", FieldType.SYSTEM_ENUM);
        statusField.setEnumName("QAndAResponseStatus");
        fields.add(statusField);
        fields.add(FieldFactory.getDefaultField("totalAnswered", "Total Answered", "TOTAL_ANSWERED", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("fullScore", "Full Score", "FULL_SCORE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("totalScore", "Total Score", "TOTAL_SCORE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("scorePercent", "Score percent", "SCORE_PERCENT", FieldType.DECIMAL));
        fields.add(FieldFactory.getBaseModuleSystemField("formId",module));
		fields.add(FieldFactory.getDefaultField("expiryDate","Expiry Date","EXPIRY_DATE",FieldType.DATE_TIME));
		fields.add(FieldFactory.getDefaultField("isRetakeAllowed","Is Retake Allowed","IS_RETAKE_ALLOWED",FieldType.BOOLEAN));
		fields.add(FieldFactory.getDefaultField("retakeExpiry","Retake Expiry","RETAKE_EXPIRY",FieldType.DATE_TIME));
		fields.add(FieldFactory.getDefaultField("retakeExpiryDuration","Retake Expiry Duration","RETAKE_EXPIRY_DURATION",FieldType.NUMBER));
        module.setFields(fields);
        return module;
    }

    private FacilioModule constructAnswer(FacilioModule qAndA, FacilioModule response, FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.ANSWER,
                "Q And A Answer",
                "Q_And_A_Answers",
                FacilioModule.ModuleType.SUB_ENTITY,
                false);

        List<FacilioField> fields = new ArrayList<>();
        LookupField questionField = (LookupField) FieldFactory.getDefaultField("question", "Question", "QUESTION_ID", FieldType.LOOKUP, true);
        questionField.setLookupModule(question);
        fields.add(questionField);
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(qAndA);
        fields.add(parentField);
        LookupField responseField = (LookupField) FieldFactory.getDefaultField("response", "Response", "RESPONSE_ID", FieldType.LOOKUP);
        responseField.setLookupModule(response);
        fields.add(responseField);
        fields.add(FieldFactory.getDefaultField("comments", "Comments", "COMMENTS", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        fields.add(FieldFactory.getDefaultField("enumAnswer", "Enum Answer", "ENUM_ANSWER", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("enumOtherAnswer", "Enum Other Answer", "ENUM_OTHER_ANSWER", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("numberAnswer", "Number Answer", "NUMBER_ANSWER", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("decimalAnswer", "Decimal Answer", "DECIMAL_ANSWER", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("booleanAnswer", "Boolean Answer", "BOOLEAN_ANSWER", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("shortAnswer", "Short Answer", "SHORT_ANSWER", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("longAnswer", "Long Answer", "LONG_ANSWER", FieldType.BIG_STRING));
        fields.add(FieldFactory.getDefaultField("dateTimeAnswer", "Date/Time Answer", "DATE_TIME_ANSWER", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("fileAnswer", "File Answer", "FILE_ANSWER", FieldType.FILE));
        fields.add(FieldFactory.getDefaultField("ratingAnswer", "Rating Answer", "RATING_ANSWER", FieldType.NUMBER));

        fields.add(FieldFactory.getDefaultField("fullScore", "Full Score", "FULL_SCORE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("score", "Score", "SCORE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("scorePercent", "Score percent", "SCORE_PERCENT", FieldType.DECIMAL));

        module.setFields(fields);
        return module;
    }

    private void addQuestionSubModules(FacilioModule question) throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule headingQuestion = constructHeadingQuestion(question);
        modules.add(headingQuestion);
        FacilioModule headingRichText = constructHeadingRichTextField(headingQuestion);
        modules.add(headingRichText);
        modules.add(constructNumberQuestion(question));
        modules.add(constructDecimalQuestion(question));
        modules.add(constructShortStringQuestion(question));
        modules.add(constructLongStringQuestion(question));
        modules.add(constructDateTimeQuestion(question));
        modules.add(constructFileUploadQuestion(question));
        modules.add(constructMultiFileUploadQuestion(question));
        modules.add(constructBooleanQuestion(question));
        modules.add(constructMCQSingle(question));
        modules.add(constructMCQSingleOption());
        modules.add(constructMCQMulti(question));
        modules.add(constructMCQMultiOption());
        modules.add(constructRatingQuestion(question));


        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, false);
        addModuleChain.execute();

        addRichTextField(headingQuestion, headingRichText);
        
        addMatrixRelatedModules(question);
    }

    public void addMatrixRelatedModules(FacilioModule question) throws Exception {
    	
    	List<FacilioModule> modules = new ArrayList<FacilioModule>();
    	
    	 FacilioModule questionModule = new FacilioModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION,
                 "Q And A Matrix Questions",
                 "Q_And_A_Matrix_Questions",
                 FacilioModule.ModuleType.SUB_ENTITY,
                 question);

         List<FacilioField> questionFields = new ArrayList<>();
         questionFields.add(FieldFactory.getDefaultField("answerModuleId", "Answer Module", "ANSWER_MODULEID", FieldType.NUMBER));
         questionModule.setFields(questionFields);
         
         modules.add(questionModule);
         
         
         FacilioModule multiQuestionModule = new FacilioModule(FacilioConstants.QAndA.Questions.MULTI_QUESTION,
                 "Q And A Multi Questions",
                 "Q_And_A_Matrix_Questions",
                 FacilioModule.ModuleType.SUB_ENTITY,
                 question);

         List<FacilioField> multiQuestionFields = new ArrayList<>();
         multiQuestionFields.add(FieldFactory.getDefaultField("answerModuleId", "Answer Module", "ANSWER_MODULEID", FieldType.NUMBER));
         multiQuestionModule.setFields(multiQuestionFields);
         
         modules.add(multiQuestionModule);

         FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
         
         addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
         addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, false);
         addModuleChain.execute();
         
         modules = new ArrayList<FacilioModule>();
         
         FacilioModule questionRowModule = new FacilioModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_ROW,
                 "Q And A Matrix Questions Row",
                 "Q_And_A_Matrix_Questions_Row",
                 FacilioModule.ModuleType.SUB_ENTITY,true);

         List<FacilioField> questionRowFields = new ArrayList<>();
         
         questionRowFields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING));
         
         questionRowFields.add(FieldFactory.getDefaultField("parentId", "Parent Id", "PARENT_ID", FieldType.NUMBER));
         
         questionRowFields.add(FieldFactory.getDefaultField("mandatory", "Mandatory", "MANDATORY", FieldType.BOOLEAN));
         
         questionRowModule.setFields(questionRowFields);

         modules.add(questionRowModule);
    	
         
         FacilioModule questionColumnModule = new FacilioModule(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN,
                 "Q And A Matrix Questions Column",
                 "Q_And_A_Matrix_Questions_Column",
                 FacilioModule.ModuleType.SUB_ENTITY,true);

         List<FacilioField> questionColumnFields = new ArrayList<>();
         
         questionColumnFields.add(FieldFactory.getDefaultField("parentId", "Parent Id", "PARENT_ID", FieldType.NUMBER));
         
         questionColumnFields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING));
         
         questionColumnFields.add(FieldFactory.getDefaultField("fieldId", "Field", "FIELDID", FieldType.NUMBER));
         
         questionColumnFields.add(FieldFactory.getDefaultField("mandatory", "Mandatory", "MANDATORY", FieldType.BOOLEAN));
         
         questionColumnFields.add(FieldFactory.getDefaultField("meta", "Meta", "META_JSON", FieldType.STRING));
         
         questionColumnModule.setFields(questionColumnFields);

         modules.add(questionColumnModule);
         
         addModuleChain = TransactionChainFactory.addSystemModuleChain();
         
         addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
         addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
         addModuleChain.execute();
         
	}

	private void addRichTextField(FacilioModule headingQuestion, FacilioModule richText) throws Exception {
        LargeTextField field = (LargeTextField) FieldFactory.getDefaultField("richText", "Rich Text", null, FieldType.LARGE_TEXT);
        field.setModule(headingQuestion);
        field.setRelModuleId(richText.getModuleId());

        Constants.getModBean().addField(field);
    }

    private FacilioModule constructHeadingRichTextField(FacilioModule headingQuestion) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.HEADING_QUESTION_RICH_TEXT,
                "Q And A Heading Question Rich Text",
                "Q_And_A_Heading_Question_Rich_Text",
                FacilioModule.ModuleType.SUB_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parentId", "Parent ID", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(headingQuestion);
        fields.add(parentField);
        fields.add(FieldFactory.getDefaultField("fileId", "File ID", "FILE_ID", FieldType.NUMBER));

        module.setFields(fields);

        return module;
    }

    private FacilioModule constructHeadingQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.HEADING_QUESTION,
                "Q And A Heading Questions",
                "Q_And_A_Heading_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        return module;
    }

    private FacilioModule constructNumberQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.NUMBER_QUESTION,
                "Q And A Number Questions",
                "Q_And_A_Number_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("minValue", "Min Value", "MIN_VALUE", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("maxValue", "Max Value", "MAX_VALUE", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("placeHolder", "Place Holder", "PLACE_HOLDER", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructDecimalQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.DECIMAL_QUESTION,
                "Q And A Decimal Questions",
                "Q_And_A_Decimal_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("minValue", "Min Value", "MIN_VALUE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("maxValue", "Max Value", "MAX_VALUE", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("placeHolder", "Place Holder", "PLACE_HOLDER", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructShortStringQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.SHORT_STRING_QUESTION,
                "Q And A Short String Questions",
                "Q_And_A_Short_String_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("maxLength", "Max Length", "MAX_LENGTH", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("placeHolder", "Place Holder", "PLACE_HOLDER", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructLongStringQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.LONG_STRING_QUESTION,
                "Q And A Long String Questions",
                "Q_And_A_Long_String_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("maxLength", "Max Length", "MAX_LENGTH", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("placeHolder", "Place Holder", "PLACE_HOLDER", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructDateTimeQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.DATE_TIME_QUESTION,
                "Q And A Date/Time Questions",
                "Q_And_A_Date_Time_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("showTime", "Show Time", "SHOW_TIME", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("placeHolder", "Place Holder", "PLACE_HOLDER", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructMCQSingle(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.MCQ_SINGLE,
                "Q And A MCQ Single",
                "Q_And_A_MCQ_Single",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("displayFormat", "Display Format", "DISPLAY_FORMAT", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructMCQMulti(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.MCQ_MULTI,
                "Q And A MCQ Multi",
                "Q_And_A_MCQ_Multi",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("displayFormat", "Display Format", "DISPLAY_FORMAT", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructMCQSingleOption() {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.MCQ_SINGLE_OPTIONS,
                "Q And A MCQ Single Options",
                "Q_And_A_MCQ_Single_Options",
                FacilioModule.ModuleType.SUB_ENTITY);

        module.setFields(constructMcqOptionFields());
        return module;
    }

    private FacilioModule constructMCQMultiOption() {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.MCQ_MULTI_OPTIONS,
                "Q And A MCQ Multi Options",
                "Q_And_A_MCQ_Multi_Options",
                FacilioModule.ModuleType.SUB_ENTITY);

        module.setFields(constructMcqOptionFields());
        return module;
    }

    private List<FacilioField> constructMcqOptionFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("parentId", "Parent Id", "PARENT_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("label", "Label", "LABEL", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("position", "Position", "POSITION_VAL", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("other", "Is Other?", "IS_OTHER", FieldType.BOOLEAN));

        return fields;
    }

    private FacilioModule constructFileUploadQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.FILE_UPLOAD_QUESTION,
                "Q And A File Upload Questions",
                "Q_And_A_File_Upload_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("fileType", "File Type", "FILE_TYPE", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructMultiFileUploadQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.MULTI_FILE_UPLOAD_QUESTION,
                "Q And A Multi File Upload Questions",
                "Q_And_A_Multi_File_Upload_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("fileType", "File Type", "FILE_TYPE", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("individualRemarksLabel", "Individual Remarks Label", "INDIVIDUAL_REMARKS_LABEL", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructBooleanQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.BOOLEAN_QUESTION,
                "Q And A Boolean Questions",
                "Q_And_A_Boolean_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("displayFormat", "Display Format", "DISPLAY_FORMAT", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("trueLabel", "True Label", "TRUE_LABEL", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("falseLabel", "False Label", "FALSE_LABEL", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private void addAnswerSubModules(ModuleBean modBean, FacilioModule answer) throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule multiFileAnswer = constructMultiFileAnswer(answer);
        modules.add(multiFileAnswer);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.getContext().put(FacilioConstants.Module.IGNORE_MODIFIED_SYS_FIELDS, true);
        addModuleChain.execute();
        addMultiFileAnswerLineItemField(modBean, answer, multiFileAnswer);
        addMultiEnumLookupField(modBean, answer);
    }

    private FacilioModule constructMultiFileAnswer(FacilioModule answer) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Answers.MULTI_FILE_ANSWER,
                "Q And A Multi File Answers",
                "Q_And_A_Multi_File_Answers",
                FacilioModule.ModuleType.SUB_ENTITY,
                false);

        List<FacilioField> fields = new ArrayList<>();
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(answer);
        fields.add(parentField);
        fields.add(FieldFactory.getDefaultField("fileAnswer", "File Answer", "FILE_ANSWER", FieldType.FILE));
        fields.add(FieldFactory.getDefaultField("remarks", "Remarks", "REMARKS", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));

        module.setFields(fields);
        return module;
    }

    private void addMultiFileAnswerLineItemField(ModuleBean modBean, FacilioModule answer, FacilioModule multiAnswer) throws Exception {
        LookupField parentField = (LookupField) modBean.getField("parent", multiAnswer.getName());
        FacilioUtil.throwIllegalArgumentException(parentField == null, "Parent field shouldn't be null for Answer module. This shouldn't happen");

        LineItemField lineItemField = (LineItemField) FieldFactory.getDefaultField("multiFileAnswer", "Multi File Answer", null, FieldType.LINE_ITEM);
        lineItemField.setChildModule(multiAnswer);
        lineItemField.setChildLookupField(parentField);
        lineItemField.setModule(answer);

        modBean.addField(lineItemField);
    }

    private void addMultiEnumLookupField(ModuleBean modBean, FacilioModule answer) throws Exception {
        FacilioModule mcqMultiOption = modBean.getModule(FacilioConstants.QAndA.Questions.MCQ_MULTI_OPTIONS);
        FacilioUtil.throwIllegalArgumentException(mcqMultiOption == null, "Mcq multi field option shouldn't be null. This shouldn't happen");

        MultiLookupField multiEnumAnswerField = (MultiLookupField) FieldFactory.getDefaultField("multiEnumAnswer", "Multi Enum Answer", null, FieldType.MULTI_LOOKUP);
        multiEnumAnswerField.setModule(answer);
        multiEnumAnswerField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiEnumAnswerField.setLookupModule(mcqMultiOption);

        FacilioModule relModule = new FacilioModule(FacilioConstants.QAndA.Answers.MCQ_MULTI_ANSWER_REL, "MCQ Multi Answer Rel", "Q_And_A_MCQ_Multi_Answer_Rel", FacilioModule.ModuleType.LOOKUP_REL_MODULE);
        multiEnumAnswerField.setRelModule(relModule);

        modBean.addField(multiEnumAnswerField);
    }

    public static void addRollUpFields(ModuleBean modBean, FacilioModule qAndA, FacilioModule response, FacilioModule page, FacilioModule question, FacilioModule answer) throws Exception {
        FacilioField pageParentField = modBean.getField("parent", page.getName());
        FacilioUtil.throwIllegalArgumentException(pageParentField == null, "Parent field of page cannot be null. This shouldn't happen");
        FacilioField questionParentField = modBean.getField("parent", question.getName());
        FacilioUtil.throwIllegalArgumentException(questionParentField == null, "Parent field of question cannot be null. This shouldn't happen");
        FacilioField answerResponseField = modBean.getField("response", answer.getName());
        FacilioUtil.throwIllegalArgumentException(answerResponseField == null, "Response field of answer cannot be null. This shouldn't happen");
        FacilioField totalPagesField = modBean.getField("totalPages", qAndA.getName());
        FacilioUtil.throwIllegalArgumentException(totalPagesField == null, "totalPages field of template cannot be null. This shouldn't happen");
        FacilioField totalQuestionsField = modBean.getField("totalQuestions", qAndA.getName());
        FacilioUtil.throwIllegalArgumentException(totalQuestionsField == null, "totalQuestions field of template cannot be null. This shouldn't happen");
        FacilioField totalAnsweredField = modBean.getField("totalAnswered", response.getName());
        FacilioUtil.throwIllegalArgumentException(totalAnsweredField == null, "totalAnswered field of response cannot be null. This shouldn't happen");

        List<RollUpField> rollUpFields = new ArrayList<>();
        rollUpFields.add(constructRollUpField("Total Pages", page, pageParentField, qAndA, totalPagesField, null));
        rollUpFields.add(constructRollUpField("Total Questions", question, questionParentField, qAndA, totalQuestionsField, null));
        rollUpFields.add(constructRollUpField("Total Answered Questions", answer, answerResponseField, response, totalAnsweredField, null));
        RollUpFieldUtil.addRollUpField(rollUpFields);

    }

    public static RollUpField constructRollUpField(String desc, FacilioModule childModule, FacilioField childLookupField, FacilioModule parentModule, FacilioField parentRollupField, Condition condition) throws Exception {
        RollUpField rollUp = new RollUpField();
        rollUp.setDescription(desc);
        rollUp.setAggregateFunctionId(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue());
        rollUp.setChildModuleId(childModule.getModuleId());
        rollUp.setChildFieldId(childLookupField.getFieldId());
        rollUp.setParentModuleId(parentModule.getModuleId());
        rollUp.setParentRollUpFieldId(parentRollupField.getFieldId());
        rollUp.setIsSystemRollUpField(true);
        if (condition != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(condition);
            rollUp.setChildCriteriaId(CriteriaAPI.addCriteria(criteria));
        }
        return rollUp;
    }

    private FacilioModule constructRatingQuestion(FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.RATING_QUESTION,
                "Q And A Rating Questions",
                "Q_And_A_Rating_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("ratingScale", "Rating Scale", "RATING_SCALE", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("displayFormat", "Display Format", "DISPLAY_FORMAT", FieldType.STRING));

        module.setFields(fields);
        return module;
    }
}
