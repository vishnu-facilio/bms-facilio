package com.facilio.bmsconsole.modulemapping;

import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.modulemapping.util.ServiceRequestModuleMapping;
import com.facilio.bmsconsole.modulemapping.util.WorkOrderModuleMapping;
import com.facilio.v3.annotation.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Config
public class ModuleMappingAPI {

    /**
     * DON'T UNCOMMENT THIS.THIS IS FOR REFERENCE
     *
     * @ModuleMappingConfig("serviceRequest") public static Supplier<ModuleMappings> getServiceRequestMappings() {
     * return () -> new ModuleMappings("serviceRequestToWoConversion", "Convert to Work order")
     * .addModuleMapping(ServiceRequestModuleMapping.serviceRequestToWoTemplate())
     * .moduleMappingDone()
     * .addModuleMapping(ServiceRequestModuleMapping.serviceRequestToWoSecondTemplate())
     * .moduleMappingDone()
     * .addModuleMapping(ServiceRequestModuleMapping.serviceRequestToWoThirdTemplate())
     * .moduleMappingDone()
     * ;
     * }
     * @ModuleMappingConfig("workorder") public static Supplier<ModuleMappings> getWorkorderMappings() {
     * return () -> new ModuleMappings("WoToSR", "Test")
     * .addModuleMapping(WorkOrderModuleMapping.workOrderToSrMappingTemplate())
     * .moduleMappingDone();
     * <p>
     * }
     **/

    @ModuleMappingConfig("serviceRequest")
    public static Supplier<ModuleMappings> getServiceRequestMappings() {
        return null;
    }
}

