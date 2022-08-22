package com.facilio.classification.command;

import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetClassificationAppliedModulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ClassificationContext> classificationList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);
        if (CollectionUtils.isEmpty(classificationList)) {
            return false;
        }

        Set<Long> ids = classificationList.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toSet());
        Map<Long, Set<Long>> appliedToModules = getAppliedToModules(ids);
        if (MapUtils.isNotEmpty(appliedToModules)) {
            for (ClassificationContext classification : classificationList) {
                classification.setAppliedModuleIds(appliedToModules.get(classification.getId()));
            }
        }
        return false;
    }

    private Map<Long, Set<Long>> getAppliedToModules(Set<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getClassificationAppliedModules().getTableName())
                .select(FieldFactory.getClassificationAppliedModulesFields())
                .andCondition(CriteriaAPI.getCondition("CLASSIFICATION_ID", "classificationId", StringUtils.join(ids, ","), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();
        Map<Long, Set<Long>> collectionModuleMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Object> map : maps) {
                Long moduleId = (Long) map.get("moduleId");
                Long classificationId = (Long) map.get("classificationId");

                Set<Long> moduleIds = collectionModuleMap.get(classificationId);
                if (moduleIds == null) {
                    moduleIds = new HashSet<>();
                    collectionModuleMap.put(classificationId, moduleIds);
                }
                moduleIds.add(moduleId);
            }
        }
        return collectionModuleMap;
    }
}
