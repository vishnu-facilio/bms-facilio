package com.facilio.apiv3;

import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

import java.util.function.Supplier;

@Config
public class ExtendedQAndAV3Config {

    /*
    All modules extending Q and A template should make sure to include the following

    .create()
    .beforeSave(QAndATransactionChainFactory.commonQAndABeforeSave())

    .summary()
    .afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndATemplateFetch())

    Make sure to add them directly or include them in the chain of corresponding hooks
     */


    @Module(FacilioConstants.Inspection.INSPECTION_TEMPLATE)
    public static Supplier<V3Config> getInspection() {
        return () -> new V3Config(InspectionTemplateContext.class, null)
                .create()
                .beforeSave(QAndATransactionChainFactory.commonQAndABeforeSave())
                .afterSave(QAndATransactionChainFactory.inspectionAfterSaveChain())
                .update()
                .afterSave(QAndATransactionChainFactory.inspectionAfterUpdateChain())
//                .list()
//                .delete()
                .summary()
                .afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndATemplateFetch())
                .build();
    }

    @Module(FacilioConstants.Inspection.INSPECTION_RESPONSE)
    public static Supplier<V3Config> getInspectionResponse() {
        return () -> new V3Config(InspectionResponseContext.class, null)
                .build();
    }
}
