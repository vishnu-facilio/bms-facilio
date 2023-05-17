package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class OfflineRecordAction extends FacilioAction{
    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private String userMobileInstanceId;
    public String getUserMobileInstanceId() {
        return userMobileInstanceId;
    }
    public void setUserMobileInstanceId(String userMobileInstanceId) {
        this.userMobileInstanceId = userMobileInstanceId;
    }

    private long recordId = -1;
    public long getRecordId() {
        return recordId;
    }
    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    private void updateOfflineRecordContext(FacilioContext context, boolean isRegister) {
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.USER_MOBILE_SETTING, userMobileInstanceId);
        context.put("isRegister",isRegister);
    }

    public String registerOfflineRecord() throws Exception {
        FacilioChain chain = TransactionChainFactory.getRegisterOrUnRegisterOfflineRecordChain();
        FacilioContext context = chain.getContext();

        updateOfflineRecordContext(context,true);

        chain.execute();
        setResult(FacilioConstants.ContextNames.REGISTERED_OFFLINE_RECORD,context.get(FacilioConstants.ContextNames.RECORD));

        return SUCCESS;
    }

    public String unRegisterOfflineRecord() throws Exception {
        FacilioChain chain = TransactionChainFactory.getRegisterOrUnRegisterOfflineRecordChain();
        FacilioContext context = chain.getContext();

        updateOfflineRecordContext(context,false);

        chain.execute();
        setResult(FacilioConstants.ContextNames.COUNT,context.get(FacilioConstants.ContextNames.COUNT));

        return SUCCESS;
    }

}
