package com.facilio.qa.command;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BaseSchedulerSingleInstanceJobAction extends V3Action {

    List<Long> jobIds;
    String jobName;

    public void runScheduler() throws Exception {

        if(!jobIds.isEmpty()){
            for(long jobId:jobIds){
                FacilioChain baseScheculeJobChain = TransactionChainFactoryV3.BaseSchedulerSingleInstanceJobChain();
                FacilioContext context = baseScheculeJobChain.getContext();
                context.put(FacilioConstants.ContextNames.JOB_ID, jobId);
                context.put(FacilioConstants.ContextNames.JOB_NAME, jobName);

                baseScheculeJobChain.execute();
            }

        }


    }
}
