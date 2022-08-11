package com.facilio.bmsconsoleV3.commands;

import com.facilio.agentv2.commands.FetchAgentDetailsCommand;
import com.facilio.agentv2.commands.FetchMessageSourcesCommand;
import com.facilio.bmsconsole.automation.command.ListGlobalVariableCommand;
import com.facilio.bmsconsole.automation.command.ListGlobalVariableGroupCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.commands.module.GetSortableFieldsCommand;
import com.facilio.bmsconsoleV3.commands.asset.AssetSupplementsSupplyCommand;
import com.facilio.bmsconsoleV3.commands.asset.CheckPMForAssetsCommandV3;
import com.facilio.bmsconsoleV3.commands.building.BuildingFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.employee.LoadEmployeeLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.floorplan.*;
import com.facilio.bmsconsole.commands.page.GetSummaryFieldsCommand;
import com.facilio.bmsconsoleV3.commands.facility.GetFacilityAvailabilityCommandV3;
import com.facilio.bmsconsoleV3.commands.homepage.getHomePageCommand;
import com.facilio.bmsconsoleV3.commands.homepage.getHomePageWidgetDataCommand;
import com.facilio.bmsconsoleV3.commands.jobplan.AddPlannerIdFilterCriteriaCommand;
import com.facilio.bmsconsoleV3.commands.people.FetchScopingForPeopleCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.AddDefaultCriteriaForQuoteFetchCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.QuotationFillLookupFields;
import com.facilio.bmsconsoleV3.commands.readingimportapp.FetchMyReadingImportDataList;
import com.facilio.bmsconsoleV3.commands.readingimportapp.FetchReadingImportDataById;
import com.facilio.bmsconsoleV3.commands.readingimportapp.FetchReadingImportDataList;
import com.facilio.bmsconsoleV3.commands.site.SiteFillLookupFieldsCommand;
import com.facilio.bmsconsoleV3.commands.tenant.LoadTenantLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.SetTenantSpaceAndContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.tenantcontact.LoadTenantcontactLookupsCommandV3;
import com.facilio.bmsconsoleV3.commands.usernotification.AddUserCriteriaMyNotification;
import com.facilio.bmsconsoleV3.commands.usernotification.FetchUnSeenNotificationCommand;
import com.facilio.bmsconsoleV3.commands.vendorcontact.LoadVendorContactLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.LoadRecordIdForPassCodeCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.SetInviteStatusConditionForVisitsListCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.LoadVisitorLoggingLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.SetInviteConditionForVisitsListCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.*;
import com.facilio.chain.FacilioChain;
import com.facilio.relation.command.AppendRelationFilterCommand;
import com.facilio.relation.command.UpdateRelationSupplementsCommand;
import com.facilio.relation.command.ValidateRelationParamCommand;

public class ReadOnlyChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain getVisitorLoggingBeforeFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetInviteConditionForVisitsListCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
    
    public static FacilioChain getInviteVisitorLogBeforeFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetInviteStatusConditionForVisitsListCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
    
    public static FacilioChain getVisitorLogBeforeFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
    
    public static FacilioChain getInviteVisitorBeforeFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
 
    public static FacilioChain getBaseVisitBeforeFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }

    public static FacilioChain getTenantsAfterFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTenantLookUpsCommandV3());
        return c;
    }

    public static FacilioChain getTenantsAfterFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTenantLookUpsCommandV3());
        c.addCommand(new SetTenantSpaceAndContactsCommandV3());
        return c;
    }

    public static FacilioChain getWorkorderAfterFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadWorkorderLookupsAfterFetchcommandV3());
        c.addCommand(new LoadWorkorderRelatedModulesCommandV3());
        c.addCommand(new GetworkorderHazardsCommandV3());
        //c.addCommand(new GetAvailableStateCommand());
        //c.addCommand(new GetTaskInputDataCommand());
        //c.addCommand(new FetchApprovalRulesCommand());
        c.addCommand(new FetchSourceTypeDetailsForWorkorderCommandV3());
        c.addCommand(new FetchStateflowAndSlaCommandV3());

        return c;
    }

    public static FacilioChain getQuoteBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddDefaultCriteriaForQuoteFetchCommandV3());
        c.addCommand(new QuotationFillLookupFields());
        return c;
    }

    public static FacilioChain getUserNotificationBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchUnSeenNotificationCommand());
        c.addCommand(new AddUserCriteriaMyNotification());
        return c;
    }

    public static FacilioChain getFacilityAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetFacilityAvailabilityCommandV3());
        return c;
    }

    public static FacilioChain getControlGroupSlotsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchControlGroupSlotCommmand());
        c.addCommand(new ComputeControlGroupSlotCommmand());
        return c;
    }
    
    public static FacilioChain getTenantListToBePublished() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchTenantsToBePublishedCommmand());
        return c;
    }
    
    public static FacilioChain getSummaryFieldsCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(ReadOnlyChainFactory.fetchModuleDataDetailsChain());
        c.addCommand(new GetSummaryFieldsCommand());
        return c;
    }
    
    public static FacilioChain getFloorplanFacilitiesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchFloorplanFacilitiesCommmand());
        return c;
    }
    
    public static FacilioChain getFloorplanMapByTypeChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchFloorplanMapByTypeCommmand());
        return c;
    }
    
    public static FacilioChain floorplanListSearchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterCommand());
        c.addCommand(new FloorplanDetailsListCommand());
        return c;
    }

    public static FacilioChain getListGlobalVariableGroupChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ListGlobalVariableGroupCommand());
        return chain;
    }

    public static FacilioChain getListGlobalVariableChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new ListGlobalVariableCommand());
        return chain;
        
    }
    public static FacilioChain getfloorplanViewerObjectChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getIndoorFloorPlanViewerCommand());
        c.addCommand(new getFloorplanLayerCommand());
        return c;
    }
    public static FacilioChain getfloorplanBookingObjectChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getIndoorFloorPlanBookingViewerCommand());
        c.addCommand(new FetchFloorplanFacilitiesCommmand());
        c.addCommand(new getIndoorFloorPlanBookingResultCommands());
        c.addCommand(new getFloorplanLayerCommand());
        return c;
    }
    public static FacilioChain getfloorplanPropertiesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getIndoorFloorPlanPropertiesCommand());
        return c;
    }
    public static FacilioChain getfloorplanBookingPropertiesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getIndoorFloorPlanPropertiesCommand());
        c.addCommand(new FetchFloorplanFacilitiesCommmand());
        c.addCommand(new getFloorplanPropertiesBookingResultCommands());
        
        return c;
    }
    
    public static FacilioChain getSortableFieldsCommand() {
        FacilioChain c = getDefaultChain();
		c.addCommand(FacilioChainFactory.getFieldsByAccessType());
        c.addCommand(new GetSortableFieldsCommand());
        return c;
    }

    public static FacilioChain getRelationDataListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateRelationParamCommand());
        c.addCommand(new AppendRelationFilterCommand());
        return c;
    }

    public static FacilioChain updateRelationSupplementsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateRelationSupplementsCommand());
        return c;
    }
    
    public static FacilioChain getAgentDetailsCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchAgentDetailsCommand());
        return c;
    }

    public static FacilioChain getPeopleRoleAndScopingCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchRolesForPeopleCommandV3());
        c.addCommand(new FetchScopingForPeopleCommandV3());
        c.addCommand(new AddSpaceDetailsToTenantContact());
        return c;
    }

    public static FacilioChain getFetchSiteFilterChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new SiteFillLookupFieldsCommand());
        c.addCommand(new FetchSpaceExcludingAccessibleSpacesCommandV3());
        c.addCommand(new AddPlannerIdFilterCriteriaCommand());
        return c;
    }

    public static FacilioChain getFetchBuildingFilterChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new BuildingFillLookupFieldsCommand());
        c.addCommand(new FetchSpaceExcludingAccessibleSpacesCommandV3());
        c.addCommand(new AddPlannerIdFilterCriteriaCommand());
        return c;
    }
    public static FacilioChain getMessageSourcesCommand() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchMessageSourcesCommand());
        return c;
    }
    public static FacilioChain getReadingImportData() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchReadingImportDataById());
        return c;
    }

    public static FacilioChain getReadingImportDataList() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterForNonModulesCommand());
        c.addCommand(new FetchReadingImportDataList());
        return c;
    }
    public static FacilioChain getMyReadingImportDataList() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterForNonModulesCommand());
        c.addCommand(new FetchMyReadingImportDataList());

        return c;
    }
    public static FacilioChain getHomepageChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getHomePageCommand());
        return c;
    }

    public static FacilioChain getWidgetDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new getHomePageWidgetDataCommand());
        return c;
    }


    public static FacilioChain getWeatherStationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchWeatherStationCommand());
        return c;
    }
    public static FacilioChain getTenantContactBeforeFetchChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JoinAndFetchAnnouncementPeopleCommandV3());
        chain.addCommand(new LoadTenantcontactLookupsCommandV3());
        return chain;
    }
    public static FacilioChain getVendorContactBeforeFetchChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JoinAndFetchAnnouncementPeopleCommandV3());
        chain.addCommand(new LoadVendorContactLookupCommandV3());
        return chain;
    }
    public static FacilioChain getEmployeeBeforeFetchChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new JoinAndFetchAnnouncementPeopleCommandV3());
        chain.addCommand(new LoadEmployeeLookupCommandV3());
        return chain;
    }
}
