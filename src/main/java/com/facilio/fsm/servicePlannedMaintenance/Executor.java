package com.facilio.fsm.servicePlannedMaintenance;

import com.facilio.fsm.context.ServiceOrderContext;
import org.apache.commons.chain.Context;
import java.util.List;

public interface Executor {
    List<ServiceOrderContext> execute(Context context) throws Exception;
}
