package com.facilio.bmsconsole.automation.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class AddOrUpdateGlobalVariableGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        GlobalVariableGroupContext variableGroup = (GlobalVariableGroupContext) context.get(FacilioConstants.ContextNames.GLOBAL_VARIABLE_GROUP);

        if (StringUtils.isEmpty(variableGroup.getName())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Group name is mandatory");
        }
        if (StringUtils.isEmpty(variableGroup.getLinkName())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Group link name is mandatory");
        }
        checkLinkNameDuplication(variableGroup);

        variableGroup.setModifiedBy(AccountUtil.getCurrentUser().getId());
        variableGroup.setModifiedTime(System.currentTimeMillis());
        if (variableGroup.getId() <= 0) {
            variableGroup.setCreatedBy(AccountUtil.getCurrentUser().getId());
            variableGroup.setCreatedTime(System.currentTimeMillis());
            addGlobalVariableGroup(variableGroup);
        } else {
            updateGlobalVariableGroup(variableGroup);
        }
        LRUCache.getGlobalVariableCache().remove(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getOrgId()));
        return false;
    }

    private void checkLinkNameDuplication(GlobalVariableGroupContext variableGroup) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .select(FieldFactory.getGlobalVariableGroupFields())
                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", variableGroup.getLinkName(), StringOperators.IS));
        if (variableGroup.getId() > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(variableGroup.getId()), NumberOperators.NOT_EQUALS));
        }
        List<Map<String, Object>> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            // already link name found in other group
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Link name already found");
        }
    }

    private void addGlobalVariableGroup(GlobalVariableGroupContext variableGroup) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .fields(FieldFactory.getGlobalVariableGroupFields());
        long id = builder.insert(FieldUtil.getAsProperties(variableGroup));
        variableGroup.setId(id);
    }

    private void updateGlobalVariableGroup(GlobalVariableGroupContext variableGroup) throws Exception {
        if (variableGroup.getId() <= 0) {
            throw new IllegalArgumentException("Cannot update the record");
        }
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .fields(FieldFactory.getGlobalVariableGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(variableGroup.getId(), ModuleFactory.getGlobalVariableGroupModule()));
        builder.update(FieldUtil.getAsProperties(variableGroup));
    }
}
