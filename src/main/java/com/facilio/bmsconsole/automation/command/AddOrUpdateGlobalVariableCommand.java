package com.facilio.bmsconsole.automation.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.automation.context.GlobalVariableContext;
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

public class AddOrUpdateGlobalVariableCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        GlobalVariableContext variable = (GlobalVariableContext) context.get(FacilioConstants.ContextNames.GLOBAL_VARIABLE);

        GlobalVariableGroupContext variableGroup = GlobalVariableUtil.getVariableGroup(variable.getGroupId());
        if (variableGroup == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid group");
        }

        if (StringUtils.isEmpty(variable.getName())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Variable name is mandatory");
        }
        if (StringUtils.isEmpty(variable.getLinkName())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Variable link name is mandatory");
        }
        if (variable.getTypeEnum() == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Variable type cannot be empty");
        }
        if (variable.getValue() == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Value is empty or unsupported");
        }
        checkDuplicateLinkName(variable);

        variable.setModifiedBy(AccountUtil.getCurrentUser().getId());
        variable.setModifiedTime(System.currentTimeMillis());

        if (variable.getId() < 0) {
            variable.setCreatedBy(AccountUtil.getCurrentUser().getId());
            variable.setCreatedTime(System.currentTimeMillis());
            addGlobalVariable(variable);
        } else {
            updateGlobalVariable(variable);
        }

        LRUCache.getGlobalVariableCache().remove(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getOrgId()));
        return false;
    }

    private void checkDuplicateLinkName(GlobalVariableContext variable) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableModule().getTableName())
                .select(FieldFactory.getGlobalVariableFields())
                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", variable.getLinkName(), StringOperators.IS));
        if (variable.getId() > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(variable.getId()), NumberOperators.NOT_EQUALS));
        }
        List<Map<String, Object>> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            // already link name found in other group
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Link name already found");
        }
    }

    private void addGlobalVariable(GlobalVariableContext variable) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getGlobalVariableModule().getTableName())
                .fields(FieldFactory.getGlobalVariableFields());
        long id = builder.insert(FieldUtil.getAsProperties(variable));
        variable.setId(id);
    }

    private void updateGlobalVariable(GlobalVariableContext variable) throws Exception {
        if (variable.getId() <= 0) {
            throw new IllegalArgumentException("Cannot update the record");
        }
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getGlobalVariableModule().getTableName())
                .fields(FieldFactory.getGlobalVariableFields())
                .andCondition(CriteriaAPI.getIdCondition(variable.getId(), ModuleFactory.getGlobalVariableModule()));
        builder.update(FieldUtil.getAsProperties(variable));
    }
}
