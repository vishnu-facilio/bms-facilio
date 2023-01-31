
package com.facilio.bmsconsole.page.factory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.page.RelatedListContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.communityfeatures.AdminDocumentsContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BMSAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.HazardContext;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.MultiVariateAnomalyAlarm;
import com.facilio.bmsconsole.context.OperationAlarmContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PrecautionContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.context.SafetyPlanContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.context.sensor.SensorRollUpAlarmContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.forms.FormSection.SectionType;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.DealsAndOffersContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NeighbourhoodContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestShipmentContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.mv.context.MVProjectWrapper;

public class PageFactory {


	public static Page getPage(FacilioModule module, Object record, boolean isApproval) throws Exception {
		if (isApproval) {
			return ApprovalPageFactory.getApprovalPage(module, (ModuleBaseWithCustomFields) record);
		}

		switch(module.getName()) {
			case ContextNames.ASSET:
				return AssetPageFactory.getAssetPage((AssetContext) record);
			case ContextNames.TENANT:
				return TenantPageFactory.getTenantPage((TenantContext) record, module);
			case ContextNames.FAILURE_CLASS:
				return FailureClassPageFactory.getFailureClassPage((V3FailureClassContext) record);
			case ContextNames.READING_RULE_MODULE:
			case ContextNames.NEW_READING_RULE_MODULE:
				return RulePageFactory.getRulePage((AlarmRuleContext) record);
			case FacilioConstants.ReadingRules.NEW_READING_RULE:
				return RulePageFactory.getNewRulePage((AlarmRuleContext) record);
			case ContextNames.MV_PROJECT_MODULE:
				return MVProjectPageFactory.getMVProjectPage((MVProjectWrapper) record);
			case ContextNames.ML_ANOMALY_ALARM:
				return MLAnomalyPageFactory.getMLAlarmPage((BaseAlarmContext) record);
			case ContextNames.FORMULA_FIELD:
				return KPIPageFacory.getKpiPage((FormulaFieldContext) record);
			case ContextNames.READING_ALARM:
			case ContextNames.NEW_READING_ALARM:
				return  ReadingAlarmPageFactory.getReadingAlarmPage((ReadingAlarm) record, module);
			case ContextNames.BMS_ALARM:
				return  BMSAlarmPageFactory.getBmsAlarmPage((BMSAlarmContext) record, module);

			case ContextNames.SENSOR_ROLLUP_ALARM:
				return  SensorAlarmPageFactory.getSensorAlarmPage((SensorRollUpAlarmContext) record, module);
			case ContextNames.MULTIVARIATE_ANOMALY_ALARM:
				return MultiVariateAnomalyAlarmPageFactory.getMultiVariateAnomalyAlarmPage((MultiVariateAnomalyAlarm) record, module);
			case ContextNames.OPERATION_ALARM:
				return OperationalAlarmPageFactory.getOperationalAlarmPage((OperationAlarmContext) record, module);
			case ContextNames.AGENT_ALARM:
				return AgentAlarmPageFactory.getAgentAlarmPage((BaseAlarmContext) record);
			case ContextNames.WorkPermit.WORKPERMIT:
				return WorkpermitPageFactory.getWorkPermitPage((WorkPermitContext) record);
			case ContextNames.VENDORS:
				return VendorPageFactory.getVendorPage((VendorContext) record);
			case ContextNames.SHIFT:
				return ShiftPageFactory.getShiftPage((Shift) record);
			case ContextNames.WORK_ORDER:
				AppDomain domain = AccountUtil.getCurrentUser().getAppDomain();
				if (domain != null &&
						domain.getAppDomainTypeEnum() == AppDomain.AppDomainType.FACILIO) {
					return WorkorderPageFactory.getWorkorderPage((WorkOrderContext) record);
				}
				return TenantWorkRequestPageFactory.getWorkorderPage((WorkOrderContext) record);
			case ContextNames.VISITOR_LOGGING:
				return VisitorLoggingPageFactory.getVisitorLoggingPage((VisitorLoggingContext) record);
			case ContextNames.INSURANCE:
				return InsurancePageFactory.getInsurancePage((InsuranceContext) record);
			case ContextNames.SAFETY_PLAN:
				return SafetyPlanPageFactory.getSafetyPlanPage((SafetyPlanContext) record, module);
			case ContextNames.HAZARD:
				return SafetyPlanPageFactory.getHazardPage((HazardContext) record, module);
			case ContextNames.PRECAUTION:
				return SafetyPlanPageFactory.getPrecautionPage((PrecautionContext) record, module);
			case ContextNames.SITE:
				if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SITE_SUMMARY) && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WEATHER_INTEGRATION)){
				return NewSpaceManagementPageFactory.getSitePage((SiteContext) record, module);
				}
				return SpaceManagementPageFactory.getSitePage((SiteContext) record, module);
			case ContextNames.CLIENT:
				return ClientPageFactory.getclientPage((ClientContext) record, module);
			case ContextNames.BUILDING:
				return SpaceManagementPageFactory.getBuildingPage((BuildingContext) record, module);
			case ContextNames.FLOOR:
				return SpaceManagementPageFactory.getFloorPage((FloorContext) record, module);
			case ContextNames.SPACE:
				return SpaceManagementPageFactory.getSpacePage((SpaceContext) record, module);
			case ContextNames.READING_TEMPLATE_MODULE:
				return TemplatePageFactory.getTemplatePage((DefaultTemplate) record);
			case ContextNames.TENANT_UNIT_SPACE:
				return TenantUnitSpacePageFactory.getTenantUnitSpacePage((TenantUnitSpaceContext) record, module);
			case ContextNames.QUOTE:
				return QuotationPageFactory.getQuotationPage((QuotationContext) record, module);
			case ContextNames.VISITOR_LOG:
				return VisitorLogPageFactory.getVisitsPage((VisitorLogContextV3)record, module);
			case ContextNames.INVITE_VISITOR:
				return InviteVisitorPageFactory.getInvitesPage((InviteVisitorContextV3)record, module);
			case ContextNames.Tenant.ANNOUNCEMENT:
				return AnnouncementPageFactory.getAnnouncementPage((AnnouncementContext) record, module);
			case ContextNames.Tenant.AUDIENCE:
				return AudiencePageFactory.getAudiencePage((AudienceContext) record, module);
			case ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS:
				return PeopleAnnouncementPageFactory.getPeopleAnnouncementPage((PeopleAnnouncementContext) record, module);
			case ContextNames.Tenant.NEIGHBOURHOOD:
				return CommunityFeaturesPageFactory.getNeighbourhoodPageFactory((NeighbourhoodContext) record, module);
			case ContextNames.Tenant.DEALS_AND_OFFERS:
				return CommunityFeaturesPageFactory.getDealsAndOffersPageFactory((DealsAndOffersContext) record, module);
			case ContextNames.Tenant.NEWS_AND_INFORMATION:
				return CommunityFeaturesPageFactory.getNewsAndInformationPageFactory((NewsAndInformationContext)record, module);
			case ContextNames.Budget.BUDGET:
				return BudgetPageFactory.getBudgetPage();
			case ContextNames.PURCHASE_REQUEST:
				return PurchaseModulesPageFactory.getPrPage((V3PurchaseRequestContext) record, module);
			case ContextNames.PURCHASE_ORDER:
				return PurchaseModulesPageFactory.getPoPage((V3PurchaseOrderContext) record, module);
			case ContextNames.FacilityBooking.FACILITY:
				return FacilityModulesPageFactory.getFacilityPage((FacilityContext) record, module);
			case ContextNames.FacilityBooking.FACILITY_BOOKING:
				return FacilityModulesPageFactory.getFacilityBookingPage((V3FacilityBookingContext) record, module);
				
			case FacilioConstants.Inspection.INSPECTION_TEMPLATE:
				return InspectionPageFactory.getInspectionTemplatePage((InspectionTemplateContext) record, module);
			case FacilioConstants.Inspection.INSPECTION_RESPONSE:
				return InspectionPageFactory.getInspectionResponsePage((InspectionResponseContext) record, module);
				
			case FacilioConstants.Induction.INDUCTION_TEMPLATE:
				return InductionPageFactory.getInductionTemplatePage((InductionTemplateContext) record, module);
			case FacilioConstants.Induction.INDUCTION_RESPONSE:
				return InductionPageFactory.getInductionResponsePage((InductionResponseContext) record, module);
				
			case FacilioConstants.Survey.SURVEY_TEMPLATE:
				return SurveyPageFactory.getSurveyTemplatePage((SurveyTemplateContext) record, module);
			case FacilioConstants.Survey.SURVEY_RESPONSE:
				return SurveyPageFactory.getSurveyResponsePage((SurveyResponseContext) record, module);
				
			case ContextNames.SERVICE_REQUEST:
				return ServiceRequestPageFactory.getServiceRequestPage((V3ServiceRequestContext) record, module);
			case ContextNames.LOCKERS:
				return LockersPageFactory.getLockersPage((V3LockersContext) record, module);
			case ContextNames.ROOMS:
				return RoomsPageFactory.getRoomsPage((V3RoomsContext) record, module);
			case ContextNames.PARKING_STALL:
				return ParkingStallPageFactory.getParkingStallPage((V3ParkingStallContext) record, module);
			case ContextNames.Floorplan.DESKS:
				return DesksPageFactory.getDesksPage((V3DeskContext) record, module);
			case ContextNames.DELIVERIES:
				return CustomModulePageFactory.getCustomModulePage((V3DeliveriesContext) record, module);
			case ContextNames.MOVES:
				return MovesPageFactory.getMovesPage((V3MovesContext) record, module);
			case ContextNames.DEPARTMENT:
				return DepartmentPageFactory.getDepartmentPage((V3DepartmentContext) record, module);
			case ContextNames.INVENTORY_REQUEST:
				return InventoryRequestPageFactory.getInventoryRequestPage((V3InventoryRequestContext) record, module);
			case ContextNames.TERMS_AND_CONDITIONS:
				return TermsAndConditionsPageFactory.getTermsAndConditionsPage((V3TermsAndConditionContext) record, module);
			case ContextNames.SERVICE:
				return ServicePageFactory.getServicePage((V3ServiceContext) record, module);
			case ContextNames.TRANSFER_REQUEST:
				return TransferRequestPageFactory.getTransferRequestPage((V3TransferRequestContext) record, module);
			case ContextNames.TRANSFER_REQUEST_SHIPMENT:
				return TransferRequestShipmentPageFactory.getTransferRequestShipmentPage((V3TransferRequestShipmentContext) record, module);
			case ContextNames.PEOPLE:
				return PeopleModulesPageFactory.getPeopleModulePage((PeopleContext) record, module);
			case ContextNames.CONTACT_DIRECTORY:
				return CommunityFeaturesPageFactory.getContactDirecoryPage((ContactDirectoryContext) record, module);
			case ContextNames.ADMIN_DOCUMENTS:
				return CommunityFeaturesPageFactory.getAdminDocumentsPage((AdminDocumentsContext) record, module);
			case ContextNames.STORE_ROOM:
				return StoreRoomPageFactory.getStoreRoomPage((StoreRoomContext) record, module);
			case ContextNames.ITEM_TYPES:
				return ItemTypesPageFactory.getItemTypesPage((ItemTypesContext) record, module);
			case ContextNames.TOOL_TYPES:
				return ToolTypesPageFactory.getToolTypesPage((ToolTypesContext) record, module);
			case ContextNames.ITEM:
				return ItemPageFactory.getItemPage((ItemContext) record, module);
			case ContextNames.TOOL:
				return ToolPageFactory.getToolPage((ToolContext) record, module);
			case ContextNames.RECEIVABLE:
				return ReceivablePageFactory.getReceivablePage((ReceivableContext) record, module);
			case ContextNames.REQUEST_FOR_QUOTATION:
				return RequestForQuotationPageFactory.getRequestForQuotationPage((V3RequestForQuotationContext) record, module);
			case ContextNames.VENDOR_QUOTES:
				return VendorQuotesPageFactory.getVendorQuotesPage((V3VendorQuotesContext) record, module);
			case ContextNames.JOB_PLAN:
				return JobPlanPageFactory.getJobPlanPage((JobPlanContext) record, module);
			case ContextNames.PLANNEDMAINTENANCE:
				return PlannedMaintenancePageFactory.getPlannedMaintenancePage((PlannedMaintenance) record);
			case ContextNames.SPACE_BOOKING:
				return  SpaceBookingPageFactory.getSpaceBookingPage((V3SpaceBookingContext) record, module);
			case ContextNames.ROUTES:
				return  RoutePageFactory.getRoutePage((RoutesContext) record, module);
		}
		if (module.getExtendModule() == null) {	// temp
			// etisalat changes will be changed to standard method
			switch(module.getName()) {
			case ContextNames.BILL_ALERT:
				return BillAlertPageFactory.getBillAlertPage((ModuleBaseWithCustomFields) record, module);
			case ContextNames.BILL_INVOICE:
				return BillInvoicePageFactory.getBillInvoicePage((ModuleBaseWithCustomFields) record, module);
			case ContextNames.BILL_TARIFF:
				return BillTariffPageFactory.getBillTariffPage((ModuleBaseWithCustomFields) record, module);
			case ContextNames.BILL_UTILITY:
				return BillUtilityPageFactory.getBillUtilityPage((ModuleBaseWithCustomFields) record, module);

			default:
				return CustomModulePageFactory.getCustomModulePage((ModuleBaseWithCustomFields) record, module);	
			}
			//
		}
		return null;
	}

	protected static PageWidget addCommonSubModuleWidget(Section section, FacilioModule module, ModuleBaseWithCustomFields record) throws Exception {
		return addCommonSubModuleWidget(section, module, record,null, false);
	}

	protected static PageWidget addCommonSubModuleWidget(Section section, FacilioModule module, ModuleBaseWithCustomFields record, HashMap<String, String> titleMap) throws Exception {
		return addCommonSubModuleWidget(section, module, record,titleMap, false);
	}
	
	protected static PageWidget addCommonSubModuleWidget(Section section, FacilioModule module, ModuleBaseWithCustomFields record, HashMap<String, String> titleMap, Boolean notifyRequestorNeeded, WidgetType... widgetTypes) throws Exception {
		
		List<Long> moduleIds = module.getExtendedModuleIds();
		
		if (moduleIds != null) {
			long recordId = record.getId();
			List<ConnectedAppWidgetContext> widgets = ConnectedAppAPI.getPageWidgets(moduleIds, recordId, ConnectedAppWidgetContext.EntityType.RELATED_LIST);
			if (widgets != null && !widgets.isEmpty()) {
				for (ConnectedAppWidgetContext widget : widgets) {
					PageWidget pageWidget = new PageWidget(WidgetType.CONNNECTED_APP);
					pageWidget.addToLayoutParams(section, 24, 5);
					pageWidget.addToWidgetParams("widgetId", widget.getId());
					pageWidget.addToWidgetParams("name", widget.getWidgetName());
					section.addWidget(pageWidget);
				}
			}
		}

		PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
		subModuleGroup.addToLayoutParams(section, 24, 8);
		subModuleGroup.addToWidgetParams("type", WidgetGroupType.TAB);
		section.addWidget(subModuleGroup);
		
		PageWidget notesWidget = new PageWidget();
		notesWidget.setWidgetType(WidgetType.COMMENT);

		if(notifyRequestorNeeded != null && notifyRequestorNeeded){
			notesWidget.addToWidgetParams("canShowNotifyRequestor", true);
		}

		PageWidget attachmentWidget = new PageWidget();
		attachmentWidget.setWidgetType(WidgetType.ATTACHMENT);
		
		if (titleMap != null) {
			String notesTitle = (String) titleMap.get("notes");
			String documentsTitle = (String) titleMap.get("documents");
			if(notesTitle != null) {
				notesWidget.setTitle(notesTitle);
			}
			if(documentsTitle != null) {
				attachmentWidget.setTitle(documentsTitle);
			}
		}
		
		List<WidgetType> widgetTypeList = null;
		if (widgetTypes.length > 0) {
			widgetTypeList = Arrays.asList(widgetTypes);
		}
		if (widgetTypeList == null || widgetTypeList.contains(WidgetType.COMMENT)) {
			subModuleGroup.addToWidget(notesWidget);
		}
		if (widgetTypeList == null || widgetTypeList.contains(WidgetType.ATTACHMENT)) {
			subModuleGroup.addToWidget(attachmentWidget);
		}

		return subModuleGroup;
	}
	
	protected static void addAssetReadingChart(Section section, long reportId, String title) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "assetReadingWidget");
		cardWidget.addToLayoutParams(section, 24, 14);
		cardWidget.setTitle(title);
		cardWidget.addToWidgetParams("reportId", reportId);
		cardWidget.addCardType(CardType.ASSET_READING_WIDGET);
		section.addWidget(cardWidget);
	}
	
	
	protected static void addHistoryWidget(Section section) {
		PageWidget historyWidget = new PageWidget(WidgetType.HISTORY);
		historyWidget.addToLayoutParams(section, 24, 3);
		section.addWidget(historyWidget);
	}

	protected static PageWidget getCountModuleWidget(String module) {
		PageWidget widget = new PageWidget();
		widget.setWidgetType(WidgetType.COUNT);
		widget.addToWidgetParams("module", module);
		return widget;
	}

	private static PageWidget getListModuleWidget(String module) {
		PageWidget widget = new PageWidget();
		widget.setWidgetType(WidgetType.LIST);
		widget.addToWidgetParams("module", module);
		return widget;
	}


	private static void addAssetPerformanceWidgets(Section section) {
		PageWidget cardWidget = new PageWidget();
		cardWidget.setWidgetType(WidgetType.CARD);

		cardWidget.addToLayoutParams(section, 8, 2);

		addReadingCard(cardWidget, "runHours", "chillerreading");

		section.addWidget(cardWidget);
	}

	private static void addReadingCard(PageWidget cardWidget, String fieldName, String moduleName) {
		addReadingCard(cardWidget, fieldName, moduleName, 3, 22, 20);
	}
	
	@SuppressWarnings("unchecked")
	protected static void addTimeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addCardType(CardType.TIME);
		section.addWidget(cardWidget);
	}
	
	protected static void addDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(pageWidget);
	}
	
	protected static void addRelatedListWidgets(Section section, long moduleId) throws Exception {
		addRelatedListWidgets(section, moduleId, null, false);
	}

	protected static void addRelatedListWidgets(Section section, long moduleId, List<String> relatedModules, boolean include) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> subModules =
				modBean.getSubModules(moduleId, FacilioModule.ModuleType.BASE_ENTITY,
						FacilioModule.ModuleType.Q_AND_A_RESPONSE,
						FacilioModule.ModuleType.Q_AND_A
								);

		if (CollectionUtils.isNotEmpty(subModules)) {
			for (FacilioModule subModule : subModules) {
				if(subModule.isModuleHidden()) {
					continue;
				}
				if (CollectionUtils.isNotEmpty(relatedModules)) {
					if (relatedModules.contains(subModule.getName()) && !include) {
						continue;
					}
				}
				if(altayerDisableRelatedList(subModule)) {
					continue;
				}
				List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
				List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == moduleId)).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(fields)) {
					for (FacilioField field : fields) {

						if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_RELATED_LIST)) {
							PageWidget relatedListWidget = new PageWidget(WidgetType.NEW_RELATED_LIST);
							JSONObject relatedList = new JSONObject();
							relatedList.put("module", subModule);
							relatedList.put("field", field);

							relatedList.put("isEditable", !(RelatedListContext.EDIT_DISABLED_MODULES.contains(subModule.getName())));
							relatedList.put("isDeletable", !(RelatedListContext.DELETE_DISABLED_MODULES.contains(subModule.getName())));
							relatedList.put("isCreateAllowed", RelatedListContext.CREATE_ENABLED_MODULES.contains(subModule.getName()));

							relatedListWidget.setRelatedList(relatedList);
							relatedListWidget.addToLayoutParams(section, 24, 8);
							section.addWidget(relatedListWidget);
						}
						else {
							PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
							JSONObject relatedList = new JSONObject();
							relatedList.put("module", subModule);
							relatedList.put("field", field);
							relatedListWidget.setRelatedList(relatedList);
							relatedListWidget.addToLayoutParams(section, 24, 8);
							section.addWidget(relatedListWidget);
						}
					}
				}
			}
		}

	}

	protected static Section getRelatedListSectionObj(Page page) {
		Section section = page.new Section("Related List", "List of all related records across modules");
		return section;
	}

	protected static boolean addRelationshipSection(Page page, Page.Tab tab, long moduleId) throws Exception{
		return addRelationshipSection(page, tab, Collections.singletonList(moduleId));
	}
	protected static boolean addRelationshipSection(Page page, Page.Tab tab, List<Long> moduleIds) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Section relationshipSection = page.new Section("Relationships", "List of relationships and types between records across modules");
		boolean isRelationshipAdded = false;
		for(Long moduleId : moduleIds) {
			List<RelationRequestContext> relations = RelationUtil.getAllRelations(modBean.getModule(moduleId));
			if (CollectionUtils.isNotEmpty(relations)) {
				isRelationshipAdded = true;
				for (RelationRequestContext relation : relations) {
					PageWidget relationWidget = new PageWidget(WidgetType.RELATIONSHIP_WIDGET);
					relationWidget.setRelation(relation);
					relationWidget.addToLayoutParams(relationshipSection, 24, 8);
					relationshipSection.addWidget(relationWidget);
				}
			}
		}
		if(isRelationshipAdded) {
			tab.addSection(relationshipSection);
		}
		return isRelationshipAdded;
	}

	public static void getRelatedListMeta(Section section, long moduleId, List<String> relatedModules, boolean include) throws Exception {
		addRelatedListWidgets(section, moduleId, relatedModules, include);
	}

	static void addSubModuleRelatedListWidget(Page.Section section, String moduleName, long parenModuleId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fields)) {
			for (FacilioField field : fields) {
				PageWidget relatedListWidget = new PageWidget(PageWidget.WidgetType.RELATED_LIST);
				JSONObject relatedList = new JSONObject();
				relatedList.put("module", module);
				relatedList.put("field", field);
				relatedListWidget.setRelatedList(relatedList);
				relatedListWidget.addToLayoutParams(section, 24, 10);
				section.addWidget(relatedListWidget);
			}
		}
	}
	
	protected static void addRelatedCountWidget(Section section, int yPos, List<String> modules) {
		PageWidget cardWidget = new PageWidget(WidgetType.RELATED_COUNT);
		cardWidget.addToLayoutParams(0, yPos, 16, 4);
		section.addWidget(cardWidget);
	}

	protected static void addNewRelatedCountWidget(Section section, List<String> modules) {
		PageWidget cardWidget = new PageWidget(WidgetType.NEW_RELATED_COUNT);
		cardWidget.addToLayoutParams(section, 14, 5);
		section.addWidget(cardWidget);
	}
	
	protected static void addSecondaryDetailWidget(Section section) throws Exception {
		addSecondaryDetailsWidget(section, null, null, null, SummaryOrderType.FORM);
	}
	
	protected static void addSecondaryDetailsWidget(Section section, ModuleBaseWithCustomFields record, FacilioModule module, List<String> formRelModules, SummaryOrderType orderType) throws Exception {
		if (orderType == SummaryOrderType.FORM_SECTION) {
			long moduleId = module.getModuleId();
			FacilioForm form = FormsAPI.fetchForm(record.getFormId(), module.getName());
			List<FormSection> sections = form.getSections();
			for (FormSection formSection: sections) {
				if (formSection.getSectionTypeEnum() == SectionType.SUB_FORM) {
					String moduleName = formSection.getSubForm().getModule().getName();
					formRelModules.add(moduleName);
					addRelatedListWidgets(section, moduleId, Collections.singletonList(moduleName), true);
				}
				else {
					if (formSection.getName() == null || 
						formSection.getFields().stream().filter(field -> field.getHideField() == null || !field.getHideField()).count() == 0l) {
						continue;
					}
					PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
					detailsWidget.addToLayoutParams(section, 24, 7);
					detailsWidget.addToWidgetParams("formSectionId", formSection.getId());
					detailsWidget.addToWidgetParams("orderType", orderType.getIndex());
					section.addWidget(detailsWidget);
				}
			}
		}
		else {
			boolean isNewSummaryWidget = false;
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SUMMARY_WIDGET)) {
				CustomPageWidget pageWidget = CustomPageAPI.getMainSummaryWidget(module.getModuleId());
				if(pageWidget != null){
					isNewSummaryWidget = true;
					PageWidget newSummaryFieldsWidget = new PageWidget(WidgetType.SUMMARY_FIELDS_WIDGET, FacilioConstants.WidgetNames.MAIN_SUMMARY_WIDGET);
					newSummaryFieldsWidget.addToLayoutParams(section, 24, 8);
					section.addWidget(newSummaryFieldsWidget);
				}
			}
			if(!isNewSummaryWidget) {
				PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
				detailsWidget.addToLayoutParams(section, 24, 7);
				detailsWidget.addToWidgetParams("orderType", orderType.getIndex());
				section.addWidget(detailsWidget);
			}
		}
	}
	
	private static void addReadingCard(PageWidget cardWidget, String fieldName, String moduleName, long aggregateFunc, long dateOperator, long xAggr) {
		cardWidget.addToWidgetParams("staticKey", "readingWithGraphCard");
		JSONObject paramsJson = new JSONObject();
		paramsJson.put("aggregateFunc", aggregateFunc);
		paramsJson.put("dateOperator", dateOperator);
		paramsJson.put("fieldName", fieldName);
		paramsJson.put("moduleName", moduleName);
		paramsJson.put("xAggr", xAggr);
		cardWidget.addToWidgetParams("paramsJson", paramsJson);
	}
	
	protected static void addChartParams(PageWidget widget, String xFieldName, String yFieldName, Criteria criteria) {
		addChartParams(widget, xFieldName, yFieldName, null, criteria);
	}
	
	protected static void addChartParams(String groupByFieldName,PageWidget widget, String xFieldName, String yFieldName , Criteria criteria) {
		addChartParams(widget, "line", DateAggregateOperator.MONTHANDYEAR, xFieldName, NumberAggregateOperator.SUM, yFieldName, groupByFieldName , DateOperators.CURRENT_YEAR, null, criteria);
	}
	
	protected static void addChartParams(PageWidget widget, String xFieldName,AggregateOperator xAggr, String yFieldName,AggregateOperator yAggr, Criteria criteria) {
		addChartParams(widget, "line", xAggr, xFieldName, yAggr, yFieldName, null , DateOperators.CURRENT_YEAR, null, criteria);
	}
	
	protected static void addChartParams(PageWidget widget, String xFieldName, String yFieldName,String groupByFieldName, Criteria criteria) {
		addChartParams(widget, "line", DateAggregateOperator.MONTHANDYEAR, xFieldName, NumberAggregateOperator.AVERAGE, yFieldName, groupByFieldName , DateOperators.CURRENT_YEAR, null, criteria);
	}
	
	@SuppressWarnings("unchecked")
	protected static void addChartParams(PageWidget widget, String chartType, AggregateOperator xAggr, String xFieldName, AggregateOperator yAggr, 
			String yFieldName,String groupByFieldName, DateOperators dateOperator, String dateOperatorValue, Criteria criteria) {
		JSONObject obj = new JSONObject();
		obj.put("chartType", chartType);
		
		JSONObject xField = new JSONObject();
		xField.put("aggr", xAggr.getValue());
		xField.put("fieldName", xFieldName);
		obj.put("xField", xField);
		
		JSONObject yField = new JSONObject();
		yField.put("aggr", yAggr.getValue());
		yField.put("fieldName", yFieldName);
		obj.put("yField", yField);
		
		JSONObject groupBy = new JSONObject();
		groupBy.put("fieldName", groupByFieldName);
		obj.put("groupBy", groupBy);
		
		obj.put("dateOperator", dateOperator.getOperatorId());
		obj.put("dateOperatorValue", dateOperatorValue);
		obj.put("criteria", criteria);
		
		widget.addToWidgetParams("chartParams", obj);
	}
	
	@SuppressWarnings("unchecked")
	protected static void addChartParams(PageWidget widget, String chartType, AggregateOperator xAggr, String xFieldName, AggregateOperator yAggr, 
			List<String> yFieldNameArray,String groupByFieldName, DateOperators dateOperator, String dateOperatorValue, Criteria criteria) {
		JSONObject obj = new JSONObject();
		obj.put("chartType", chartType);
		
		JSONObject xField = new JSONObject();
		xField.put("aggr", xAggr.getValue());
		xField.put("fieldName", xFieldName);
		obj.put("xField", xField);
		
		if( yFieldNameArray != null) {
			
			org.json.simple.JSONArray yFields = new JSONArray();
			
			for(String yFieldName:yFieldNameArray) {
				if(yFieldName != null) {
					JSONObject yField = new JSONObject();
					yField.put("aggr", yAggr.getValue());
					yField.put("fieldName", yFieldName);
					yFields.add(yField);
				} else {
					yFields.add(null);
				}
			}
			
			obj.put("yField", yFields);
			obj.put("isMultipleMetric",true);
			
			} else {
				obj.put("yField", null);
			}
		
		JSONObject groupBy = new JSONObject();
		groupBy.put("fieldName", groupByFieldName);
		obj.put("groupBy", groupBy);
		
		obj.put("dateOperator", dateOperator.getOperatorId());
		obj.put("dateOperatorValue", dateOperatorValue);
		obj.put("criteria", criteria);
		
		widget.addToWidgetParams("chartParams", obj);
	}
	
	@SuppressWarnings("unchecked")
	protected static void addChartParams(PageWidget widget, String chartType, AggregateOperator xAggr, String xFieldName, AggregateOperator yAggr, 
			List<String> yFieldNameArray,String groupByFieldName, DateOperators dateOperator, List<Long> dateOperatorValue, Criteria criteria) {
		JSONObject obj = new JSONObject();
		obj.put("chartType", chartType);
		
		JSONObject xField = new JSONObject();
		xField.put("aggr", xAggr.getValue());
		xField.put("fieldName", xFieldName);
		obj.put("xField", xField);
		
		if( yFieldNameArray != null) {
			
			org.json.simple.JSONArray yFields = new JSONArray();
			
			for(String yFieldName:yFieldNameArray) {
				if(yFieldName != null) {
					JSONObject yField = new JSONObject();
					yField.put("aggr", yAggr.getValue());
					yField.put("fieldName", yFieldName);
					yFields.add(yField);
				} else {
					yFields.add(null);
				}
			}
			
			obj.put("yField", yFields);
			obj.put("isMultipleMetric",true);
			
			} else {
				obj.put("yField", null);
			}
		
		JSONObject groupBy = new JSONObject();
		groupBy.put("fieldName", groupByFieldName);
		obj.put("groupBy", groupBy);
		
		obj.put("dateOperator", dateOperator.getOperatorId());
		obj.put("dateOperatorValue", dateOperatorValue);
		obj.put("criteria", criteria);
		
		widget.addToWidgetParams("chartParams", obj);
	}
	
	@SuppressWarnings("unchecked")
	protected static void addChartParams(PageWidget widget, String chartType, AggregateOperator xAggr, String xFieldName, AggregateOperator yAggr, 
			List<String> yFieldNameArray,String groupByFieldName, DateOperators dateOperator, List<Long> dateOperatorValue, Criteria criteria, String dateFieldName, String moduleName) {
		JSONObject obj = new JSONObject();
		obj.put("chartType", chartType);
		
		JSONObject xField = new JSONObject();
		if(xAggr != null) {
			xField.put("aggr", xAggr.getValue());
		}
		xField.put("fieldName", xFieldName);
		obj.put("xField", xField);
		
		if(dateFieldName != null) {
			JSONObject dateField = new JSONObject();
			dateField.put("fieldName", dateFieldName);
			obj.put("dateField", dateField);
		}
		
		if( yFieldNameArray != null) {
			
		org.json.simple.JSONArray yFields = new JSONArray();
		
		for(String yFieldName:yFieldNameArray) {
			if(yFieldName != null) {
				JSONObject yField = new JSONObject();
				yField.put("aggr", yAggr.getValue());
				yField.put("fieldName", yFieldName);
				yFields.add(yField);
			} else {
				yFields.add(null);
			}
		}
		
		obj.put("yField", yFields);
		obj.put("isMultipleMetric",true);
		
		} else {
			obj.put("yField", null);
		}
		
		JSONObject groupBy = new JSONObject();
		groupBy.put("fieldName", groupByFieldName);
		obj.put("groupBy", groupBy);
		
		obj.put("dateOperator", dateOperator.getOperatorId());
		obj.put("dateOperatorValue", dateOperatorValue);
		obj.put("criteria", criteria);
		obj.put("moduleName", moduleName);
		
		widget.addToWidgetParams("chartParams", obj);
	}
	
	public enum SummaryOrderType implements FacilioIntEnum {
		FORM,
		FORM_SECTION,
		ALPHA,
		CUSTOM
		;

		public String getValue() {
			return name();
		}

		public static SummaryOrderType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
		
	}

	public static boolean altayerDisableRelatedList(FacilioModule module) throws Exception {
		List<String> modNames = Arrays.asList("custom_payment","custom_receipts","custom_contracts","custom_referral","serviceRequest");
		if(AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 418l) {
			if(module.getName().equals(ContextNames.PLANNEDMAINTENANCE)) {
				return true;
			}
			if(AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
				if (modNames.contains(module.getName())) {
					boolean hasPerm = false;
					if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null) {
						hasPerm = PermissionUtil.currentUserHasPermission(module.getName(), FacilioConstants.ContextNames.READ_PERMISSION, AccountUtil.getCurrentUser().getRole());
						if(AccountUtil.getCurrentUser().getRole().isPrevileged()) {
							hasPerm = true;
						}
					}
					return !hasPerm;
				}
			} else if(AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)) {
				if (modNames.contains(module.getName())) {
					List<Long> tabIds = ApplicationApi.getTabForModules(AccountUtil.getCurrentApp().getId(),module.getModuleId());
					if(CollectionUtils.isNotEmpty(tabIds)) {
						boolean hasPerm = false;
						if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null) {
							hasPerm = PermissionUtil.currentUserHasPermission(tabIds.get(0), module.getName(), FacilioConstants.ContextNames.READ_PERMISSION, AccountUtil.getCurrentUser().getRole(),null);
							if(AccountUtil.getCurrentUser().getRole().isPrevileged()) {
								hasPerm = true;
							}
						}
						return !hasPerm;
					}
				}
			}
		}
		return false;
	}
}