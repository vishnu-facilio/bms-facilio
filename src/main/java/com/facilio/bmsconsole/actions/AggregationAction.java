package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.bmsconsole.util.AggregationAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class AggregationAction extends FacilioAction {

    private AggregationMetaContext aggregationMeta;
    public AggregationMetaContext getAggregationMeta() {
        return aggregationMeta;
    }
    public void setAggregationMeta(AggregationMetaContext aggregationMeta) {
        this.aggregationMeta = aggregationMeta;
    }

    public String addOrUpdateAggregation() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateAggregation();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.AGGREGATION_META, aggregationMeta);
        chain.execute();

        setResult(FacilioConstants.ContextNames.AGGREGATION_META, context.get(FacilioConstants.ContextNames.AGGREGATION_META));

        return SUCCESS;
    }

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private long moduleId = -1;
    public long getModuleId() {
        return moduleId;
    }
    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    private Long startTime;
    public Long getStartTime() {
        return startTime;
    }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    private Long endTime;
    public Long getEndTime() {
        return endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String runAggregationJob() throws Exception {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new FacilioCommand() {
            @Override
            public boolean executeCommand(Context context) throws Exception {
                Long parentId = (Long) context.get(FacilioConstants.ContextNames.ID);
                long moduleId = (long) context.get(FacilioConstants.ContextNames.MODULE_ID);
                Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
                Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleId);

                AggregationAPI.aggregateData(module, Collections.singletonList(parentId), startTime, endTime);
                return false;
            }
        });
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.MODULE_ID, moduleId);
        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);

        chain.execute();

        return SUCCESS;
    }
}
