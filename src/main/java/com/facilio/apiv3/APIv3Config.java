package com.facilio.apiv3;

import com.facilio.apiv3.sample.*;
import com.facilio.bmsconsole.commands.AssetDepreciationFetchAssetDetailsCommand;
import com.facilio.bmsconsole.commands.ExecuteWorkFlowsBusinessLogicInPostTransactionCommand;
import com.facilio.bmsconsole.commands.ValidateAssetDepreciationCommand;
import com.facilio.bmsconsole.commands.quotation.*;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.quotation.QuotationContext;
import com.facilio.bmsconsole.context.quotation.TaxContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.GetRecordIdsFromRecordMapCommandV3;
import com.facilio.bmsconsoleV3.commands.GetStateflowsForModuleDataListCommandV3;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.insurance.AssociateVendorToInsuranceCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.LoadInsuranceLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.*;
import com.facilio.bmsconsoleV3.commands.workpermit.ComputeScheduleForWorkPermitCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitRecurringInfoCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3;
import com.facilio.bmsconsoleV3.context.V3InsuranceContext;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.V3WorkPermitContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.DefaultInit;
import org.apache.commons.chain.Context;

import java.util.Collections;

import static com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3.getWorkPermitAfterSaveOnUpdateChain;

@Config
public class APIv3Config {

    @Module("custom_test")
    public static V3Config customTest() {
        return new V3Config(ModuleBaseWithCustomFields.class)
                .create()
                    .init(new DefaultInit())
                    .beforeSave(new SampleBeforeSaveCommand())
                    .afterSave(new SampleAfterSaveCommand())
                    .afterTransaction(new SampleAfterTransactionCommand())
                .summary()
                    .afterFetch(new SampleAfterFetchCommand())
                .list()
                    .afterFetch(new SampleAfterFetchCommand())
                .update()
                    .init(new DefaultInit())
                    .beforeSave(new SampleBeforeSaveCommand())
                    .afterSave(new SampleAfterSaveCommand())
                    .afterTransaction(new SampleAfterTransactionCommand())
                .delete()
                    .beforeDelete(new SampleBeforeDeleteCommand())
                    .afterDelete(new SampleAfterDeleteCommand())
                    .afterTransaction(new SampleAfterTransactionCommand())
                .build();
    }

    @Module("assetdepreciation")
    public static V3Config getAssetDepreciation() {
        return new V3Config(AssetDepreciationContext.class)
                .create()
                    .beforeSave(new ValidateAssetDepreciationCommand())
                .summary()
                    .afterFetch(new AssetDepreciationFetchAssetDetailsCommand())
                .build();
    }

    @Module("quotation")
    public static V3Config getQuotation() {
        return new V3Config(QuotationContext.class)

                .create()
                .beforeSave(new QuotationValidationAndCostCalculationCommand())
                .afterSave(new InsertQuotationLineItemsCommand())

                .update()
                .beforeSave(new QuotationValidationAndCostCalculationCommand())
                .afterSave(new InsertQuotationLineItemsCommand())

                .summary()
                .beforeFetch(new QuotationFillLookupFields())
                .afterFetch(new QuotationFillDetailsCommand())

                .build();
    }

    @Module("tax")
    public static V3Config getTax() {
        return new V3Config(TaxContext.class)

                .create()
                .beforeSave(new TaxValidationCommand())
                .afterSave(new InsertTaxGroupsCommand())

                .summary()
                .afterFetch(new TaxFillDetailsCommand())

                .list()
                .afterFetch(new TaxFillDetailsCommand())

                .build();
    }

    @Module("workpermit")
    public static V3Config getWorkPermit() {
        return new V3Config(V3WorkPermitContext.class)
                .create()
                    .beforeSave(new ComputeScheduleForWorkPermitCommandV3())
                    .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnCreateChain())
                .update()
                    .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnUpdateChain())
                .list()
                    .beforeFetch(new LoadWorkPermitLookUpsCommandV3())
                  //.afterFetch(new GetStateflowsForModuleDataListCommandV3())
                    //need to set the above command's response in action class result(will be a v3 feature)
                .summary()
                    .beforeFetch(new LoadWorkPermitLookUpsCommandV3())
                    .afterFetch(new LoadWorkPermitRecurringInfoCommandV3())
                .build();
    }

    @Module("insurance")
    public static V3Config getInsurance() {
        return new V3Config(V3InsuranceContext.class)
                .create()
                    .beforeSave(new AssociateVendorToInsuranceCommandV3())
                    .afterSave(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand())
                .update()
                .list()
                    .beforeFetch(new LoadInsuranceLookUpCommandV3())
                .summary()
                    .beforeFetch(new LoadInsuranceLookUpCommandV3())
                .build();
    }

    @Module("visitorlogging")
    public static V3Config getVisitorLogging() {
        return new V3Config(V3VisitorLoggingContext.class)
                .create()
                .beforeSave(TransactionChainFactoryV3.getVisitorLoggingBeforeSaveOnCreateChain())
                .afterSave(TransactionChainFactoryV3.getVisitorLoggingAfterSaveOnCreateChain())
                .update()
                   .beforeSave(TransactionChainFactoryV3.getVisitorLoggingBeforeSaveOnUpdateChain())
                   .afterSave(TransactionChainFactoryV3.getVisitorLoggingAfterSaveOnUpdateChain())
                .list()
                    .beforeFetch(ReadOnlyChainFactoryV3.getVisitorLoggingBeforeFetchOnListChain())
                .summary()
                    .beforeFetch(new LoadVisitorLoggingLookupCommandV3())
                    .afterFetch(new GetTriggerForRecurringLogCommandV3())
                .build();
    }

}

