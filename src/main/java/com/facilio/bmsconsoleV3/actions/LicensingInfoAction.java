package com.facilio.bmsconsoleV3.actions;


import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;

import com.facilio.bmsconsoleV3.context.licensinginfo.LicenseInfoContext;

import com.facilio.bmsconsoleV3.util.LicensingInfoUtil;

import com.facilio.chain.FacilioChain;

import com.facilio.chain.FacilioContext;

import com.facilio.constants.FacilioConstants;

import com.facilio.v3.V3Action;


import java.util.Map;


public class LicensingInfoAction extends V3Action {
    private LicenseInfoContext licenseContext;
    public LicenseInfoContext getLicenseContext() {
        return licenseContext;
    }
    public void setLicenseContext(LicenseInfoContext licenseContext) {
        this.licenseContext = licenseContext;
    }

    public String add() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getAddLicensingInfoChain();
        FacilioContext context = chain.getContext();
        Long usedCount = LicensingInfoUtil.getCurrentCount(licenseContext,String.valueOf(licenseContext.getLicensingTypeEnum()));
        licenseContext.setUsedLicense(usedCount);
        context.put("licensingInfo",licenseContext);
        chain.execute();
        setData("LicenseId", context.get("LicenseId"));
        return SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getFetchLicensingInfoChain();
        FacilioContext context = chain.getContext();
        chain.execute();
        setData(FacilioConstants.ContextNames.LICENSING_INFO,context.get(FacilioConstants.ContextNames.LICENSING_INFO));
        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getDeleteLicensingInfoChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.LICENSING_INFO_IDS,licenseContext.getLicenseInfoIds());
        chain.execute();
        setData("licensingInfo", context.get("licensingInfo"));
        return SUCCESS;
    }

    public String update() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUpdateLicensingInfoChain();
        FacilioContext context = chain.getContext();
        context.put("licensingInfo",licenseContext);
        chain.execute();
        setData("licensingInfo", context.get("licensingInfo"));
        return SUCCESS;
    }
}