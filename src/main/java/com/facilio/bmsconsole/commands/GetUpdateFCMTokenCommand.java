package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetUpdateFCMTokenCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String oldFcmToken = (String) context.get(FacilioConstants.ContextNames.OLD_FCM_TOKEN);
        String newFcmToken = (String) context.get(FacilioConstants.ContextNames.NEW_FCM_TOKEN);
        String appLinkName = AccountUtil.getCurrentApp().getLinkName();
        Long userId = AccountUtil.getCurrentUser().getId();
        FacilioModule module = IAMAccountConstants.getUserMobileSettingModule();

        if(userId == null) return false;

        List<UserMobileSetting> userMobilesSettings = IAMUserUtil.getUserMobileSettingInstanceIds(Collections.singletonList(userId),appLinkName);

        UserMobileSetting mobileSetting = null;
        for(UserMobileSetting userMobilesSetting:userMobilesSettings){
            if(userMobilesSetting.getMobileInstanceId().equals(oldFcmToken)){
                mobileSetting = userMobilesSetting;
                break;
            }
        }

        if(mobileSetting == null) throw new IllegalArgumentException("Invalid old Fcm token");

        HashMap<String,Object> updateValue = new HashMap<>();
        updateValue.put("mobileInstanceId",newFcmToken);

        int count = IAMUserUtil.updateUserMobileFcmToken(mobileSetting,module,updateValue);

        if(count > 0) {
            mobileSetting.setMobileInstanceId(newFcmToken);
            context.put(FacilioConstants.ContextNames.USER_MOBILE_SETTING, mobileSetting);
        }else throw new IllegalArgumentException("Failed to update Fcm token");

        return false;
    }
}
