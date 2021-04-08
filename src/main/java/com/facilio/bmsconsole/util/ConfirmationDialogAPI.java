package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfirmationDialogAPI {

    public static void addConfirmationDialogs(List<ConfirmationDialogContext> confirmationDialogs, long parentId) throws Exception {
        if (CollectionUtils.isEmpty(confirmationDialogs)) {
            return;
        }

        for (ConfirmationDialogContext confirmationDialog : confirmationDialogs) {
            confirmationDialog.setParentId(parentId);
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                .fields(FieldFactory.getConfirmationDialogFields());
        List<Map<String, Object>> mapList =
                FieldUtil.getAsMapList(confirmationDialogs, ConfirmationDialogContext.class);
        builder.addRecords(mapList);
        builder.save();

        for (int i = 0; i < confirmationDialogs.size(); i++) {
            confirmationDialogs.get(i).setId((long) mapList.get(i).get("id"));
        }
    }

    public static List<ConfirmationDialogContext> getConfirmationDialogs(long parentId, boolean fetchChildren) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                .select(FieldFactory.getConfirmationDialogFields())
                .andCondition(CriteriaAPI.getCondition("parentId", "PARENT_ID", String.valueOf(parentId), NumberOperators.EQUALS));
        List<ConfirmationDialogContext> confirmationDialogs = FieldUtil.getAsBeanListFromMapList(builder.get(), ConfirmationDialogContext.class);
        if (fetchChildren && CollectionUtils.isNotEmpty(confirmationDialogs)) {
            List<Long> criteriaIds = confirmationDialogs.stream().filter(confirmationDialog -> confirmationDialog.getCriteriaId() > 0)
                    .map(ConfirmationDialogContext::getCriteriaId).collect(Collectors.toList());
            Map<Long, Criteria> criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);

            for (ConfirmationDialogContext dialogContext: confirmationDialogs) {
                if (dialogContext.getCriteriaId() > 0) {
                    dialogContext.setCriteria(criteriaMap.get(dialogContext.getCriteriaId()));
                }
            }
        }
        return confirmationDialogs;
    }

    public static void deleteConfirmationDialogs(long parentId) throws Exception {
        List<ConfirmationDialogContext> confirmationDialogs = getConfirmationDialogs(parentId, false);

        if (CollectionUtils.isNotEmpty(confirmationDialogs)) {
            for (ConfirmationDialogContext confirmationDialog : confirmationDialogs) {
                if (confirmationDialog.getCriteriaId() > 0) {
                    CriteriaAPI.deleteCriteria(confirmationDialog.getCriteriaId());
                }
            }

            List<Long> dialogIds = confirmationDialogs.stream().map(ConfirmationDialogContext::getId).collect(Collectors.toList());
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getConfirmationDialogModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(dialogIds, ModuleFactory.getConfirmationDialogModule()));
            builder.delete();
        }
    }
}
