package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class DeleteCommand extends FacilioCommand {
    private static int RESTRICT = 0;
    private static int SET_NULL = 1;
    private static int CASCADE = 2;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> recordIds = Constants.getRecordIds(context);

        getDeletedRecords(moduleName, recordIds, context);

        Map<String, Integer> deleteCount = new HashMap<>();
        deleteModuleRecords(recordIds, moduleName, deleteCount);
        Constants.setCountMap(context, deleteCount);
        return false;
    }

    private void getDeletedRecords(String moduleName, List<Long> recordIds, Context context) throws Exception {
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = bean.getModule(moduleName);

        V3Config v3Config = Constants.getV3Config(context);
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .module(module)
                .beanClass(beanClass)
                .select(Collections.singletonList(bean.getPrimaryField(moduleName)))
                .andCondition(CriteriaAPI.getIdCondition(recordIds, module));
        List<ModuleBaseWithCustomFields> deletedRecords = builder.get();

        Constants.setDeletedRecords(context, deletedRecords);
    }

    private void deleteModuleRecords(List<Long> recordIds,  String moduleName, Map<String, Integer> deleteCount) throws Exception {
        int count = 0;
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = bean.getModule(moduleName);
        Map<FacilioModule, List<FacilioField>> relatedLookupFields = bean.getRelatedLookupFields(module.getModuleId());
        List<Pair<FacilioModule, Integer>> subModules = bean.getSubModulesWithDeleteType(module.getModuleId(),
                FacilioModule.ModuleType.CUSTOM, FacilioModule.ModuleType.BASE_ENTITY);

        count += deleteRows(module, recordIds);

        List<FacilioModule> cascadeModules = new ArrayList<>();
        List<FacilioModule> throwErrorModules = new ArrayList<>();
        for (Pair<FacilioModule, Integer> subModule: subModules) {
            if (subModule.getRight() == RESTRICT) {
              throwErrorModules.add(subModule.getLeft());
            } else if (subModule.getRight() == CASCADE) {
                cascadeModules.add(subModule.getLeft());
            }
        }

        if (!throwErrorModules.isEmpty()) {
            for (FacilioModule restrictModule : throwErrorModules) {
                List<FacilioField> fields = relatedLookupFields.get(restrictModule);
                if (CollectionUtils.isEmpty(fields)) {
                    continue;
                }
                List<Long> childRecordIds = checkLookupData(module, restrictModule, fields, recordIds);
                if (CollectionUtils.isNotEmpty(childRecordIds)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "The sub-module " + restrictModule.getDisplayName() + " has data associated. Please delete them manually before deleting");
                }
            }
        }

        for (FacilioModule cascadeModule: cascadeModules) {
            List<FacilioField> fields = relatedLookupFields.get(cascadeModule);
            if (CollectionUtils.isEmpty(fields)) {
                continue;
            }
            List<Long> rowIds = checkLookupData(module, cascadeModule, fields, recordIds);
            if (CollectionUtils.isEmpty(rowIds)) {
                continue;
            }
            deleteModuleRecords(rowIds, cascadeModule.getName(), deleteCount);
        }
        deleteCount.put(moduleName, count);
     }

    private int deleteRows(FacilioModule module, List<Long> rowIds) throws Exception {
        DeleteRecordBuilder builder = new DeleteRecordBuilder()
                .module(module)
                ;

        if (module.isTrashEnabled()) {
            builder.andCondition(CriteriaAPI.getIdCondition(rowIds, module));
            return builder.markAsDelete();
        }
        else {
            return builder.batchDeleteById(rowIds);
        }
    }

    private List<Long> checkLookupData(FacilioModule parentModule, FacilioModule childModule, List<FacilioField> relatedLookupFields, List<Long> recordIds) throws Exception {
        List<Long> rowIds = new ArrayList<>();
        if (!LookupSpecialTypeUtil.isSpecialType(parentModule.getName()) && CollectionUtils.isNotEmpty(recordIds)) {
            String idString = StringUtils.join(recordIds, ",");
            if (CollectionUtils.isNotEmpty(relatedLookupFields)) {
                List<FacilioField> fields = new ArrayList<>();
                fields.add(FieldFactory.getIdField(childModule));
                for (FacilioField f : relatedLookupFields) {
                    SelectRecordsBuilder builder = new SelectRecordsBuilder()
                            .module(childModule)
                            .select(fields)
                            .andCondition(CriteriaAPI.getCondition(f, idString, NumberOperators.EQUALS));
                    List<Map<String, Object>> data = builder.getAsProps();
                    if (CollectionUtils.isNotEmpty(data)) {
                        data.forEach(i -> rowIds.add((long) i.get("id")));
                    }
                }
            }
        }
        return rowIds;
    }
}
