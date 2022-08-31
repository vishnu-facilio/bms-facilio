package com.facilio.classification.command;

import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AfterSaveClassificationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ClassificationContext> classificationList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);

        if (CollectionUtils.isEmpty(classificationList)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR);
        }

        // only add while adding
        for (ClassificationContext classificationContext : classificationList) {
            addAppliedModulesEntry(classificationContext.getId(), classificationContext.getAppliedModuleIds());
        }
        return false;
    }

    private void addAppliedModulesEntry(long classificationId, Set<Long> appliedModuleIds) throws Exception {
        // This can be added only when classification creation.
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getClassificationAppliedModules().getTableName())
                .fields(FieldFactory.getClassificationAppliedModulesFields());
        for (Long moduleId: appliedModuleIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("moduleId", moduleId);
            map.put("classificationId", classificationId);
            builder.addRecord(map);
        }
        builder.save();
    }

}
