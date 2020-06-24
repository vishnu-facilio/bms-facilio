package com.facilio.apiv3;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.apiv3.sample.*;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.insurance.AssociateVendorToInsuranceCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.LoadInsuranceLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.*;
import com.facilio.bmsconsoleV3.commands.tenant.FillTenantsLookupCommand;
import com.facilio.bmsconsoleV3.commands.tenant.ValidateTenantSpaceCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.CheckforPeopleDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.LoadTenantcontactLookupsCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddOrUpdateLocationForVendorCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.LoadVendorLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.LoadVisitorLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.GetTriggerForRecurringLogCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.LoadVisitorLoggingLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.LoadWorkorderLookupsAfterFetchcommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.ComputeScheduleForWorkPermitCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitRecurringInfoCommandV3;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.DefaultInit;

import java.util.function.Supplier;

@Config
public class APIv3Config {

    @Module("custom_test")
    // we are returning a lambda so as to generate new V3Config object each
    // and every time a request happens
    public static Supplier<V3Config> customTest() {
        return () -> new V3Config(CustomModuleData.class)
                .create()
                    .init(new DefaultInit())
                    .beforeSave(new SampleBeforeSaveCommand())
                    .afterSave(new SampleAfterSaveCommand())
                    .afterTransaction(new SampleAfterTransactionCommand())
                .summary()
                    .afterFetch(new SampleAfterFetchCommand())
                .list()
                    .beforeFetch(new SampleBeforeFetchCommand())
                    .afterFetch(new SampleAfterFetchCommand())
                    .showStateFlowList()
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
    public static Supplier<V3Config> getAssetDepreciation() {
        return () -> new V3Config(AssetDepreciationContext.class)
                .create()
                    .beforeSave(new ValidateAssetDepreciationCommand())
                .summary()
                    .afterFetch(new AssetDepreciationFetchAssetDetailsCommand())
                .build();
    }

    @Module("quotation")
    public static Supplier<V3Config> getQuotation() {
        return () -> new V3Config(QuotationContext.class)

                .create()
                .beforeSave(TransactionChainFactoryV3.getQuotationBeforeSaveChain())
                .afterSave(TransactionChainFactoryV3.getQuotationAfterSaveChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTATION_ACTIVITY))

                .update()
                .beforeSave(new QuotationValidationAndCostCalculationCommand())
                .afterSave(TransactionChainFactoryV3.getQuotationAfterUpdateChain())
                .afterTransaction(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTATION_ACTIVITY))

                .summary()
                .beforeFetch(new QuotationFillLookupFields())
                .afterFetch(new QuotationFillDetailsCommand())

                .list()
                .beforeFetch(new QuotationFillLookupFields())
                .showStateFlowList()

                .build();
    }

    @Module("tax")
    public static Supplier<V3Config> getTax() {
        return () -> new V3Config(TaxContext.class)

                .create()
                .beforeSave(new TaxValidationCommand())
                .afterSave(new InsertTaxGroupsCommand())

                .summary()
                .afterFetch(new TaxFillDetailsCommand())

                .list()
                .afterFetch(new TaxFillDetailsCommand())

                .build();
        /*
            Using add handler for update too send with id for Updating
            Bulk Add is Supported
            Only single update is Supported (For Bulk update maintain old record id vs new record id map)

            Tax Group Update => Sets current group as inactive and creates a new group

            Tax Individual Update => 1. Current tax is set as Inactive,
                                     2. List of All Groups with the updating tax as child will be set as inactive and new Tax
                                        groups will be added with new tax id
         */
    }

    @Module("workpermit")
    public static Supplier<V3Config> getWorkPermit() {
        return () -> new V3Config(V3WorkPermitContext.class)
                .create()
                    .beforeSave(new ComputeScheduleForWorkPermitCommandV3())
                    .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnCreateChain())
                .update()
                    .afterSave(TransactionChainFactoryV3.getWorkPermitAfterSaveOnUpdateChain())
                .list()
                    .beforeFetch(new LoadWorkPermitLookUpsCommandV3())
                    .showStateFlowList()
                .summary()
                    .beforeFetch(new LoadWorkPermitLookUpsCommandV3())
                    .afterFetch(new LoadWorkPermitRecurringInfoCommandV3())
                .build();
    }

    @Module("insurance")
    public static Supplier<V3Config> getInsurance() {
        return () -> new V3Config(V3InsuranceContext.class)
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
    public static Supplier<V3Config> getVisitorLogging() {
        return () -> new V3Config(V3VisitorLoggingContext.class)
                .create()
                .beforeSave(TransactionChainFactoryV3.getVisitorLoggingBeforeSaveOnCreateChain())
                .afterTransaction(TransactionChainFactoryV3.getVisitorLoggingAfterSaveOnCreateChain())
                .update()
                   .beforeSave(TransactionChainFactoryV3.getVisitorLoggingBeforeSaveOnUpdateChain())
                   .afterTransaction(TransactionChainFactoryV3.getVisitorLoggingAfterSaveOnUpdateChain())
                .list()
                    .beforeFetch(ReadOnlyChainFactoryV3.getVisitorLoggingBeforeFetchOnListChain())
                    .showStateFlowList()
                .summary()
                    .beforeFetch(new LoadVisitorLoggingLookupCommandV3())
                    .afterFetch(new GetTriggerForRecurringLogCommandV3())
                .build();
    }

    @Module("visitor")
    public static Supplier<V3Config> getVisitor() {
        return () -> new V3Config(V3VisitorContext.class)
                .create()
                  .beforeSave(TransactionChainFactoryV3.getVisitorBeforeSaveOnAddChain())
                  .afterSave(TransactionChainFactoryV3.getVisitorAfterSaveOnAddChain())
                .update()
                  .beforeSave(TransactionChainFactoryV3.getVisitorBeforeSaveOnAddChain())
                .list()
                  .beforeFetch(new LoadVisitorLookUpCommandV3())
                .summary()
                  .beforeFetch(new LoadVisitorLookUpCommandV3())
                .build();
    }

    @Module("vendors")
    public static Supplier<V3Config> getVendor() {
        return () -> new V3Config(V3VendorContext.class)
                .create()
                  .beforeSave(new AddOrUpdateLocationForVendorCommandV3())
                  .afterSave(TransactionChainFactoryV3.getVendorsAfterSaveChain())
                .update()
                  .beforeSave(new AddOrUpdateLocationForVendorCommandV3())
                  .afterSave(TransactionChainFactoryV3.getVendorsAfterSaveChain())
                .list()
                  .beforeFetch(new LoadVendorLookupCommandV3())
                  .showStateFlowList()
                .summary()
                  .beforeFetch(new LoadVendorLookupCommandV3())
                .build();
    }

    @Module("tenant")
    public static Supplier<V3Config> getTenant() {
        return () -> new V3Config(V3TenantContext.class)
                .create()
                  .beforeSave(new ValidateTenantSpaceCommandV3())
                  .afterSave(TransactionChainFactoryV3.getTenantAfterSaveChain())
                .update()
                  .beforeSave(new ValidateTenantSpaceCommandV3())
                  .afterSave(TransactionChainFactoryV3.getTenantAfterSaveChain())
                .list()
                  .afterFetch(ReadOnlyChainFactoryV3.getTenantsAfterFetchOnListChain())
                .summary()
                    .beforeFetch(new FillTenantsLookupCommand())
                  .afterFetch(ReadOnlyChainFactoryV3.getTenantsAfterFetchOnSummaryChain())
                .build();
    }

    @Module("workorder")
    public static Supplier<V3Config> getWorkorder() {
        return () -> new V3Config(V3WorkOrderContext.class)
                .create()
                  .beforeSave(TransactionChainFactoryV3.getWorkorderBeforeSaveChain())
                  .afterSave(TransactionChainFactoryV3.getWorkorderAfterSaveChain())
                .update()
                  .beforeSave(TransactionChainFactoryV3.getWorkorderBeforeUpdateChain())
                  .afterSave(TransactionChainFactoryV3.getWorkorderAfterUpdateChain(true))
                .list()
                  .afterFetch(new LoadWorkorderLookupsAfterFetchcommandV3())
                  .showStateFlowList()
                .summary()
                  .afterFetch(ReadOnlyChainFactoryV3.getWorkorderAfterFetchOnSummaryChain())
                .build();
    }

    @Module("tenantcontact")
    public static Supplier<V3Config> getTenantContact() {
        return () -> new V3Config(V3TenantContactContext.class)
                .create()
                    .beforeSave(TransactionChainFactoryV3.getTenantContactBeforeSaveChain())
                    .afterSave(TransactionChainFactoryV3.getTenantContactAfterSaveChain())
                .update()
                    .beforeSave(new CheckforPeopleDuplicationCommandV3())
		            .afterSave(TransactionChainFactoryV3.getTenantContactAfterUpdateChain())
                .list()
                    .beforeFetch(new LoadTenantcontactLookupsCommandV3())
                .summary()
                    .beforeFetch(new LoadTenantcontactLookupsCommandV3())
                .build();
    }

}

