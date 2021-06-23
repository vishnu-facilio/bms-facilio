package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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

        Map<String, Integer> deleteCount = new HashMap<>();
        deleteModuleRecords(recordIds, moduleName, deleteCount);
        Constants.setCountMap(context, deleteCount);
        return false;
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
            throw new RESTException(ErrorCode.VALIDATION_ERROR);
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

        //deleting multiselectfield value
        // No need to delete any of the multi record values here because we are only marking as deleted
        // They should be handled when we are deleting things from trash

        // Commenting the below lines since nothing is done here. deleteBuilder.delete will only delete records

//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        List<FacilioField> fields = modBean.getAllFields(moduleName);
//        List<MultiLookupField> multiLookups = new ArrayList<MultiLookupField>();
//
//            for(FacilioField field : fields) {
//                if (field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
//                    multiLookups.add((MultiLookupField) field);
//                }
//            }
//            if(CollectionUtils.isNotEmpty(multiLookups)){
//                new DeleteRecordBuilder().deleteSupplements(multiLookups);
//            }
     }

    private int deleteRows(FacilioModule module, List<Long> rowIds) throws Exception {
        DeleteRecordBuilder builder = new DeleteRecordBuilder()
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(rowIds, module))
                ;

        if (module.isTrashEnabled()) {
            return builder.markAsDelete();
        }
        else {
            return builder.delete();
        }
    }

    private List<Long> checkLookupData(FacilioModule parentModule, FacilioModule childModule, List<FacilioField> relatedLookupFields, List<Long> recordIds) throws Exception {
        List<Long> rowIds = new ArrayList<>();
        if (!LookupSpecialTypeUtil.isSpecialType(parentModule.getName()) && CollectionUtils.isNotEmpty(recordIds)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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
