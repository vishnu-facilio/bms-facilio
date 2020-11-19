package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.impact.BaseAlarmImpactContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

import java.util.Map;

public class ImpactAction extends FacilioAction {

    private Map<String, Object> alarmImpact;
    public Map<String, Object> getAlarmImpact() {
        return alarmImpact;
    }
    public void setAlarmImpact(Map<String, Object> alarmImpact) {
        this.alarmImpact = alarmImpact;
    }

    public String addOrUpdateImpact() throws Exception {
        BaseAlarmImpactContext baseImpact = FieldUtil.getAsBeanFromMap(alarmImpact, BaseAlarmImpactContext.class);
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateAlarmImpactChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ALARM_IMPACT, baseImpact);
        chain.execute();

        setResult(FacilioConstants.ContextNames.ALARM_IMPACT, context.get(FacilioConstants.ContextNames.ALARM_IMPACT));

        return SUCCESS;
    }

    private Long assetCategoryId;
    public Long getAssetCategoryId() {
        return assetCategoryId;
    }
    public void setAssetCategoryId(Long assetCategoryId) {
        this.assetCategoryId = assetCategoryId;
    }

    public String getAlarmImpacts() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAlarmImpactsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ASSET_CATEGORY_ID, assetCategoryId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.ALARM_IMPACT_LIST, context.get(FacilioConstants.ContextNames.ALARM_IMPACT_LIST));

        return SUCCESS;
    }

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String deleteAlarmImpact() throws Exception {
        FacilioChain chain = TransactionChainFactory.deleteAlarmImpactChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        return SUCCESS;
    }
}
