package com.facilio.bmsconsole.widgetConfig;

import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.v3.annotation.Config;

import java.util.function.Supplier;

@Config
public class APIWidgetConfig {

//@WidgetType(PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET)
//public static Supplier<WidgetConfig> getSummaryWidgetCRUD() {
//    return () ->  new WidgetConfig()
//                .create()
//                .saveCommand()
//                .update()
//                .updateCommand()
//                .summary()
//                .fetchCommand()
//                .delete()
//                .deleteCommand()
//                .build();
//}
    @WidgetType(PageWidget.WidgetType.WIDGET_GROUP)
    public static Supplier<WidgetConfig> getWidgetGroupCRUD() {
        return () -> new WidgetConfig()
                .create()
                .saveCommand(TransactionChainFactory.getCreateWidgetGroupChain())
                .summary()
                .fetchCommand(ReadOnlyChainFactory.getWidgetGroupChain())
                .build();
    }

    @WidgetType(PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET)
    public static Supplier<WidgetConfig> getSummaryWidgetCRUD() {
        return () -> new WidgetConfig()
                .create()
                .saveCommand(TransactionChainFactory.getAddSummaryWidgetChainInPage())
                .update()
                .updateCommand(TransactionChainFactory.getUpdateSummaryWidgetChainInPage())
                .summary()
                .fetchCommand(ReadOnlyChainFactory.getSummaryWidgetForPageWidgetChain())
                .build();
    }
    @WidgetType(PageWidget.WidgetType.BULK_RELATED_LIST)
    public static Supplier<WidgetConfig> getBulkRelatedListWidgetCRUD() {
        return () ->  new WidgetConfig()
                .create()
                .saveCommand(TransactionChainFactory.getAddBulkRelatedListCommand())
                .update()
                .updateCommand(TransactionChainFactory.getUpdateBulkRelatedListCommand())
                .summary()
                .fetchCommand(ReadOnlyChainFactory.getBulkRelatedListChain())
                .build();
    }
    @WidgetType(PageWidget.WidgetType.RELATED_LIST)
    public static Supplier<WidgetConfig> getRelatedListWidgetCRUD() {
        return () ->  new WidgetConfig()
                .create()
                .saveCommand(TransactionChainFactory.getAddOrUpdateRelatedListWidgetDetailChain())
                .update()
                .updateCommand(TransactionChainFactory.getAddOrUpdateRelatedListWidgetDetailChain())
                .summary()
                .fetchCommand(ReadOnlyChainFactory.getRelatedListWidgetDetailChain())
                .build();
    }
    @WidgetType(PageWidget.WidgetType.RELATIONSHIP_WIDGET)
    public static Supplier<WidgetConfig> getRelationshipWidgetCRUD() {
        return () ->  new WidgetConfig()
                .create()
                .saveCommand(TransactionChainFactory.getAddOrUpdateRelationshipWidgetDetailChain())
                .update()
                .updateCommand(TransactionChainFactory.getAddOrUpdateRelationshipWidgetDetailChain())
                .summary()
                .fetchCommand(ReadOnlyChainFactory.getRelationshipWidgetDetailChain())
                .build();
    }
    @WidgetType(PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET)
    public static Supplier<WidgetConfig> getBulkRelationShipWidgetCRUD() {
        return () ->  new WidgetConfig()
                .create()
                .saveCommand(TransactionChainFactory.getAddBulkRelationshipWidgetCommand())
                .update()
                .updateCommand(TransactionChainFactory.getUpdateBulkRelationshipWidgetCommand())
                .summary()
                .fetchCommand(ReadOnlyChainFactory.getBulkRelationShipWidgetChain())
                .build();
    }


}
