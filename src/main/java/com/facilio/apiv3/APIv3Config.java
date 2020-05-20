package com.facilio.apiv3;

import com.facilio.apiv3.sample.*;
import com.facilio.bmsconsole.commands.AssetDepreciationFetchAssetDetailsCommand;
import com.facilio.bmsconsole.commands.ValidateAssetDepreciationCommand;
import com.facilio.bmsconsole.commands.quotation.*;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.quotation.QuotationContext;
import com.facilio.bmsconsole.context.quotation.TaxContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.DefaultInit;

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

                .summary()
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
}
