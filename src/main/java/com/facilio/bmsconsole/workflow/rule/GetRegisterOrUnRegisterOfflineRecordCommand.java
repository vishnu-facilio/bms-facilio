package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;


public class GetRegisterOrUnRegisterOfflineRecordCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(GetRegisterOrUnRegisterOfflineRecordCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Long userId = AccountUtil.getCurrentUser().getId();
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String mobileInstanceId = (String) context.get(FacilioConstants.ContextNames.USER_MOBILE_SETTING);
        String appLinkName = AccountUtil.getCurrentApp().getLinkName();
        boolean isRegister = (boolean) context.get("isRegister");

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule module = modBean.getModule(moduleName);

        if(module == null) {
            throw new IllegalArgumentException("Module name cannot be empty");
        }

        Long userMobileSettingId = getUserMobileSettingInstance(userId, appLinkName, mobileInstanceId);
        Long moduleId = module.getModuleId();

        if(userMobileSettingId < 0){
            LOGGER.info("Mobile Instance is not valid");
            return false;
        }

        HashMap<String, Object> offlineRecord = new HashMap<>();
        offlineRecord.put("moduleId", moduleId);
        offlineRecord.put("recordId", recordId);
        offlineRecord.put("registeredTime", System.currentTimeMillis());
        offlineRecord.put("userMobileSettingId", userMobileSettingId);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", String.valueOf(recordId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("USER_MOBILE_SETTING_ID", "userMobileSettingId", String.valueOf(userMobileSettingId), NumberOperators.EQUALS));

        if (isRegister) {
            boolean isRecordRegistered = isRecordRegistered(criteria);
            if (!isRecordRegistered) {
                GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getOfflineRecordRegisterModule().getTableName())
                        .fields(FieldFactory.getOfflineRecordRegisterFields())
                        .addRecord(offlineRecord);
                insertRecordBuilder.save();

                context.put(FacilioConstants.ContextNames.RECORD, offlineRecord);
            }
        } else {
            GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getOfflineRecordRegisterModule().getTableName())
                    .andCriteria(criteria);

            int rowsUpdated = deleteBuilder.delete();
            context.put(FacilioConstants.ContextNames.COUNT, rowsUpdated);
        }

        return false;
    }

    public static Long getUserMobileSettingInstance(Long userId,String appLinkName,String mobileInstanceId) throws Exception {
        List<UserMobileSetting> mobileInstanceSettings = NotificationAPI.getMobileInstanceIDs(Collections.singletonList(userId),appLinkName);

        if(CollectionUtils.isNotEmpty(mobileInstanceSettings)){
            mobileInstanceSettings =  mobileInstanceSettings.stream().filter(mobileSetting -> mobileSetting.getMobileInstanceId().equals(mobileInstanceId)).collect(Collectors.toList());
            return CollectionUtils.isNotEmpty(mobileInstanceSettings) ? mobileInstanceSettings.get(0).getUserMobileSettingId() : -1L;
        }

        return -1L;
    }

    public static boolean isRecordRegistered(Criteria criteria) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getOfflineRecordRegisterModule().getTableName())
                .select(FieldFactory.getOfflineRecordRegisterFields())
                .andCriteria(criteria);

        List<Map<String,Object>> result = selectBuilder.get();
        return CollectionUtils.isNotEmpty(result);
    }
}
