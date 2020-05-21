package com.facilio.apiv3;

import com.facilio.apiv3.sample.*;
import com.facilio.bmsconsole.commands.AssetDepreciationFetchAssetDetailsCommand;
import com.facilio.bmsconsole.commands.ValidateAssetDepreciationCommand;
import com.facilio.bmsconsole.commands.quotation.*;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.quotation.QuotationContext;
import com.facilio.bmsconsole.context.quotation.TaxContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.GetRecordIdsFromRecordMapCommandV3;
import com.facilio.bmsconsoleV3.commands.GetStateflowsForModuleDataListCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.AssociateVendorToInsuranceCommandV3;
import com.facilio.bmsconsoleV3.commands.insurance.LoadInsuranceLookUpCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.*;
import com.facilio.bmsconsoleV3.commands.workpermit.ComputeScheduleForWorkPermitCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitRecurringInfoCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3;
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
                    .afterSave(new FacilioCommand() {
                        @Override
                        public boolean executeCommand(Context context) throws Exception {
                            new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW).executeCommand(context);
                            new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand().executeCommand(context);
                            new RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3().executeCommand(context);
                            return false;
                        }
                    })
                .update()
                    .afterSave(new FacilioCommand() {
                        @Override
                        public boolean executeCommand(Context context) throws Exception {
                            new GetRecordIdsFromRecordMapCommandV3().executeCommand(context);
                            new LoadWorkPermitLookUpsCommandV3().executeCommand(context);
                            new GenericGetModuleDataListCommand().executeCommand(context);
                            new ChangeApprovalStatusForModuleDataCommand().executeCommand(context);
                            new VerifyApprovalCommand().executeCommand(context);
                            new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW).executeCommand(context);
                            new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand().executeCommand(context);
                            new RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3().executeCommand(context);
                            return false;
                        }
                    })
                .list()
                    .beforeFetch(new LoadWorkPermitLookUpsCommandV3())
                    .afterFetch(new GetStateflowsForModuleDataListCommandV3())
                    //need to set the above command's response in action class result
                .summary()
                    .beforeFetch(new LoadWorkPermitLookUpsCommandV3())
                    .afterFetch(new LoadWorkPermitRecurringInfoCommandV3())
                .build();
    }

    @Module("insurance")
    public static V3Config getInsurance() {
        return new V3Config(V3WorkPermitContext.class)
                .create()
                    .beforeSave(new AssociateVendorToInsuranceCommandV3())
                    .afterSave(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand().executeCommand(context);
                        return false;
                    }
                })
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
                .beforeSave(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        new PutOldVisitRecordsInContextCommandV3().executeCommand(context);
                        new AddNewVisitorWhileLoggingCommandV3().executeCommand(context);
                        new AddOrUpdateVisitorFromVisitsCommandV3().executeCommand(context);
                        new CheckForWatchListRecordCommandV3().executeCommand(context);
                        return false;
                    }
                })
                .afterSave(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        new UpdateVisitorInviteRelArrivedStateCommandV3().executeCommand(context);
                        new ChangeVisitorInviteStateCommandV3().executeCommand(context);
                        new ForkChainToInstantJobCommand()
                                .addCommand(new AddNdaForVisitorLogCommandV3())
                                .addCommand(new GenerateQrInviteUrlCommandV3())
                                .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION))
                                .addCommand(new VisitorFaceRecognitionCommandV3()).executeCommand(context);
                        new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand();
                        return false;
                    }
                })
                .update()
                   .beforeSave(new FacilioCommand() {
                        @Override
                        public boolean executeCommand(Context context) throws Exception {
                            new PutOldVisitRecordsInContextCommandV3().executeCommand(context);
                            new AddOrUpdateVisitorFromVisitsCommandV3().executeCommand(context);
                            return false;
                        }
                    })
                .afterSave(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        new GetRecordIdsFromRecordMapCommandV3().executeCommand(context);
                        new LoadWorkPermitLookUpsCommandV3().executeCommand(context);
                        new GenericGetModuleDataListCommand().executeCommand(context);
                        new ChangeVisitorInviteStateCommandV3().executeCommand(context);
                        new ForkChainToInstantJobCommand()
                                .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION));
                        new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand();
                        return false;
                    }
                })
                .list()
                    .beforeFetch(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                       new SetInviteConditionForVisitsListCommandV3().executeCommand(context);
                       new LoadVisitorLoggingLookupCommandV3().executeCommand(context);
                       return false;
                     }
                   })
                .summary()
                .beforeFetch(new LoadVisitorLoggingLookupCommandV3())
                .afterFetch(new GetTriggerForRecurringLogCommandV3())
                .build();
    }

}

