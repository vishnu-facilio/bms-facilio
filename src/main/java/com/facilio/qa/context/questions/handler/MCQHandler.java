package com.facilio.qa.context.questions.handler;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.BaseMCQContext;
import com.facilio.qa.context.questions.MCQOptionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j
@AllArgsConstructor
public class MCQHandler<Q extends BaseMCQContext> implements QuestionHandler<Q> {

    private String moduleName;

    @Override
    public boolean showSummaryOfResponses(QuestionContext question) throws Exception {
        return true;
    }

    @Override
    public boolean hideShowResponseButton(QuestionContext question) throws Exception {
        return true;
    }

    @Override
    public void validateSave(List<Q> questions) throws Exception {
        for (BaseMCQContext question : questions) {
            V3Util.throwRestException(question.getOptions().stream().filter(MCQOptionContext::otherEnabled).count() > 1, ErrorCode.VALIDATION_ERROR, "errors.qa.mcqHandler.optionCheck",true,null);
            V3Util.throwRestException(CollectionUtils.isEmpty(question.getOptions()) || question.getOptions().size() < 2, ErrorCode.VALIDATION_ERROR, "errors.qa.mcqHandler.optionCountCheck",true,null);
            //V3Util.throwRestException(question.getOptions().stream().filter(MCQOptionContext::otherEnabled).count() > 1, ErrorCode.VALIDATION_ERROR, "Only one other option can be present",true,null);
            //V3Util.throwRestException(CollectionUtils.isEmpty(question.getOptions()) || question.getOptions().size() < 2, ErrorCode.VALIDATION_ERROR, "Minimum 2 options needed for MCQs",true,null);
        }
    }

    @Override
    public void afterSave(List<Q> questions) throws Exception {
        List<MCQOptionContext> options = questions.stream()
                                            .flatMap(q -> setDefaultPropsForOptions(q).stream())
                                            .collect(Collectors.toList())
                                            ;
        addOptions(options);
    }

    @Override
    public void validateUpdate(List<Q> questions) throws Exception {
        for (BaseMCQContext question : questions) {
            V3Util.throwRestException(question.getOptions() != null && question.getOptions().size() < 2, ErrorCode.VALIDATION_ERROR, "errors.qa.mcqHandler.optionCountCheck",true,null);
            V3Util.throwRestException(question.getOptions().stream().filter(MCQOptionContext::otherEnabled).count() > 1, ErrorCode.VALIDATION_ERROR, "errors.qa.mcqHandler.optionCheck",true,null);
            //V3Util.throwRestException(question.getOptions() != null && question.getOptions().size() < 2, ErrorCode.VALIDATION_ERROR, "Minimum 2 options needed for MCQs",true,null);
            //V3Util.throwRestException(question.getOptions().stream().filter(MCQOptionContext::otherEnabled).count() > 1, ErrorCode.VALIDATION_ERROR, "Only one other option can be present",true,null);
        }
    }

    @Override
    public void afterUpdate(List<Q> questions) throws Exception {
        List<Long> idsToBeDeleted = new ArrayList<>();
        List<MCQOptionContext> toBeUpdated = new ArrayList<>();
        List<MCQOptionContext> toBeAdded = new ArrayList<>();
        for (BaseMCQContext question : questions) {
            if (question.getOptions() != null) {
                List<MCQOptionContext> options = setDefaultPropsForOptions(question);
                List<MCQOptionContext> updateList = options.stream().filter(o -> o.getId() != -1).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(updateList)) {
                    toBeUpdated.addAll(updateList);
                }

                List<MCQOptionContext> addList = options.stream().filter(o -> o.getId() == -1).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(addList)) {
                    toBeAdded.addAll(addList);
                }

                List<Long> unUsedOptions = fetchUnUsedOptions(question.getId(), updateList.stream().mapToLong(MCQOptionContext::getId).boxed().collect(Collectors.toList()));
                if (unUsedOptions != null) {
                    idsToBeDeleted.addAll(unUsedOptions);
                }
            }
        }
        if (!idsToBeDeleted.isEmpty()) {
            V3RecordAPI.deleteRecordsById(moduleName, idsToBeDeleted);
        }
        if (!toBeUpdated.isEmpty()) {
            updateOptions(toBeUpdated);
        }
        if (!toBeAdded.isEmpty()) {
            addOptions(toBeAdded);
        }
    }

    @Override
    public void afterFetch(List<Q> questions) throws Exception {
        List<MCQOptionContext> options = fetchOptions(questions);
        QAndAUtil.<Q, MCQOptionContext>splitAndSetChildrenInParent(questions,
                                                options,
                                                MCQOptionContext::getParentId,
                                                (o, p) -> o.setParentId(null), // Looks like an hack, but it's okay I guess
                                                Q::setOptions,
                                                this::removeDefaultProps
                                            );
    }

    private void removeDefaultProps(MCQOptionContext option) {
        QAndAUtil.setDefaultPropsAsNullToReduceRespSize(option);
        option.setPosition(null);
    }

    private List<MCQOptionContext> setDefaultPropsForOptions(BaseMCQContext question) {
        // Moving other option to last if present
        List<MCQOptionContext> options = question.getOptions();
        OptionalInt indexOpt = IntStream.range(0, question.getOptions().size()).filter(i -> options.get(i).otherEnabled()).findFirst();
        if (indexOpt.isPresent() && indexOpt.getAsInt() < (options.size()-1)) { // Adding other option at last
            MCQOptionContext option = options.remove(indexOpt.getAsInt());
            options.add(option);
        }

        IntStream.range(0, question.getOptions().size())
            .forEach(idx -> {
                MCQOptionContext option = question.getOptions().get(idx);
                option.setParentId(question.getId());
                option.setPosition(idx+1);
            });
        return question.getOptions();
    }

    private void addOptions (List<MCQOptionContext> options) throws Exception {
        new InsertRecordBuilder<MCQOptionContext>()
            .moduleName(moduleName)
            .fields(Constants.getModBean().getAllFields(moduleName))
            .addRecords(options)
            .save();
    }

    private List<Long> fetchUnUsedOptions (long parentId, List<Long> usedOptions) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        FacilioField idField = FieldFactory.getIdField(module);
        SelectRecordsBuilder<MCQOptionContext> optionsSelect = new SelectRecordsBuilder<MCQOptionContext>()
                                            .module(module)
                                            .select(Collections.singletonList(idField))
                                            .andCondition(CriteriaAPI.getCondition(modBean.getField("parentId", module.getName()), String.valueOf(parentId), NumberOperators.EQUALS));

        if (CollectionUtils.isNotEmpty(usedOptions)) {
            optionsSelect.andCondition(CriteriaAPI.getCondition(idField, usedOptions, NumberOperators.NOT_EQUALS));
        }
        List<Map<String, Object>> props = optionsSelect.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
            return props.stream().map(prop -> (Long) prop.get("id")).collect(Collectors.toList());
        }
        return null;
    }

    private int updateOptions (List<MCQOptionContext> options) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        FacilioField moduleField = FieldFactory.getModuleIdField(module), idField = FieldFactory.getIdField(module);

        List<GenericUpdateRecordBuilder.BatchUpdateContext> updateBatch = new ArrayList<>(options.size());
        for (MCQOptionContext option : options) {
            GenericUpdateRecordBuilder.BatchUpdateContext updateContext = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateContext.addUpdateValue("label", option.getLabel());
            updateContext.addUpdateValue("position", option.getPosition());

            updateContext.addWhereValue(moduleField.getName(), module.getModuleId());
            updateContext.addWhereValue(idField.getName(), option.getId());

            updateBatch.add(updateContext);
        }

        List<FacilioField> whereFields = new ArrayList<>(2);
        whereFields.add(moduleField);
        whereFields.add(idField);
        List<FacilioField> fields = modBean.getAllFields(module.getName()).stream().filter(f -> !f.getName().equals("parentId")).collect(Collectors.toList()); //Not updating parent
        return new GenericUpdateRecordBuilder()
                .fields(fields)
                .table(module.getTableName())
                .batchUpdate(whereFields, updateBatch);
    }

    private List<MCQOptionContext> fetchOptions (List<Q> questions) throws Exception {
        List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName);
        FacilioField parentIdField = FieldFactory.filterField(fields, "parentId");
        return new SelectRecordsBuilder<MCQOptionContext>()
                .moduleName(moduleName)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(parentIdField, questions.stream().mapToLong(BaseMCQContext::getId).boxed().collect(Collectors.toList()), NumberOperators.EQUALS))
                .beanClass(MCQOptionContext.class)
                .orderBy("parentId, position asc")
                .get();
    }
}
