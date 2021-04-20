package com.facilio.qa.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LineItemField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.util.FacilioUtil;

import java.util.ArrayList;
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

        addQuestionSubModules(question);
        addAnswerSubModules(answer);
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
        fields.add(FieldFactory.getDefaultField("responseModuleId", "Response Module", "RESPONSE_MODULE_ID", FieldType.NUMBER));

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
        SystemEnumField typeField = (SystemEnumField) FieldFactory.getDefaultField("type", "Type", "QUESTION_TYPE", FieldType.SYSTEM_ENUM);
        typeField.setEnumName("QuestionType");
        fields.add(typeField);
        fields.add(FieldFactory.getDefaultField("answerFieldId", "Answer Field", "ANSWER_FIELD_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("photoRequired", "Is Photo Required?", "PHOTO_REQUIRED", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("showComments", "Show comments box?", "SHOW_COMMENTS", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("commentsLabel", "Comments Label", "COMMENTS_LABEL", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("mandatory", "Is Mandatory?", "MANDATORY", FieldType.BOOLEAN));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructResponse (FacilioModule qAndA) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.RESPONSE,
                "Q And A Responses",
                "Q_And_A_Responses",
                FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                true);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("totalQuestions", "Total Questions", "TOTAL_QUESTIONS", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("totalAnswered", "Total Answered", "TOTAL_ANSWERED", FieldType.NUMBER));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructAnswer (FacilioModule qAndA, FacilioModule response, FacilioModule question) {
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
        fields.add(FieldFactory.getDefaultField("enumAnswer", "Enum Answer", "ENUM_ANSWER", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("numberAnswer", "Number Answer", "NUMBER_ANSWER", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("decimalAnswer", "Decimal Answer", "DECIMAL_ANSWER", FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("booleanAnswer", "Boolean Answer", "BOOLEAN_ANSWER", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("shortAnswer", "Short Answer", "SHORT_ANSWER", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("longAnswer", "Long Answer", "LONG_ANSWER", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("fileAnswer", "File Answer", "FILE_ANSWER", FieldType.FILE));

        module.setFields(fields);
        return module;
    }

    private void addQuestionSubModules(FacilioModule question) throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
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

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, false);
        addModuleChain.execute();
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
        fields.add(FieldFactory.getDefaultField("showOtherOption", "Show Other Option", "SHOW_OTHER_OPTION", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("otherOptionLabel", "Other Option Label", "OTHER_OPTION_LABEL", FieldType.STRING));

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
        fields.add(FieldFactory.getDefaultField("showOtherOption", "Show Other Option", "SHOW_OTHER_OPTION", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("otherOptionLabel", "Other Option Label", "OTHER_OPTION_LABEL", FieldType.STRING));

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

        return fields;
    }

    private FacilioModule constructFileUploadQuestion (FacilioModule question) {
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

    private FacilioModule constructMultiFileUploadQuestion (FacilioModule question) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.Questions.MULTI_FILE_UPLOAD_QUESTION,
                "Q And A Multi File Upload Questions",
                "Q_And_A_Multi_File_Upload_Questions",
                FacilioModule.ModuleType.SUB_ENTITY,
                question);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("fileType", "File Type", "FILE_TYPE", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("remarksForEachFile", "Remarks for each file", "REMARKS_FOR_EACH_FILE", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("remarksLabel", "Remarks Label", "REMARKS_LABEL", FieldType.STRING));

        module.setFields(fields);
        return module;
    }

    private FacilioModule constructBooleanQuestion (FacilioModule question) {
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

    private void addAnswerSubModules(FacilioModule answer) throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule multiFileAnswer = constructMultiFileAnswer(answer);
        modules.add(multiFileAnswer);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.getContext().put(FacilioConstants.Module.IGNORE_MODIFIED_SYS_FIELDS, true);
        addModuleChain.execute();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        addAnswerLineItemFields(modBean, answer, multiFileAnswer);
    }

    private FacilioModule constructMultiFileAnswer (FacilioModule answer) {
        FacilioModule module = new FacilioModule(FacilioConstants.QAndA.MULTI_FILE_ANSWER,
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

    private void addAnswerLineItemFields(ModuleBean modBean, FacilioModule answer, FacilioModule multiAnswer) throws Exception {
        LookupField parentField = (LookupField) modBean.getField("parent", multiAnswer.getName());
        FacilioUtil.throwIllegalArgumentException(parentField == null, "Parent field shouldn't be null for Answer module. This shouldn't happen");

        LineItemField lineItemField = (LineItemField) FieldFactory.getDefaultField("multiFileAnswer", "Multi File Answer", null, FieldType.LINE_ITEM);
        lineItemField.setChildModule(multiAnswer);
        lineItemField.setChildLookupField(parentField);
        lineItemField.setModule(answer);

        modBean.addField(lineItemField);
    }
}
