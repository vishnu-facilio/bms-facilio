
package com.facilio.bmsconsole.page.factory;

import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Organization;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;

public class AssetPageFactory extends PageFactory {
	
	private static final Logger LOGGER = LogManager.getLogger(AssetPageFactory.class.getName());
	
	public static Page getAssetPage(AssetContext asset) throws Exception {
		Organization organization = AccountUtil.getCurrentOrg();
		boolean isEmicoolOrg = organization.getOrgId() == 24l && StringUtils.isNotEmpty(organization.getDomain()) && organization.getDomain().equals("emicool");
		Page page = new Page();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(asset.getModuleId());
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addPrimaryDetailsWidget(tab1Sec1);
		
		// Temp...needs to move to db
		if (AccountUtil.getCurrentOrg().getOrgId() == 210 && asset.getId() == 1145442l) {
			addSupplyTemperatureChartWidget(tab1Sec1);
			addSupplyFeedbackChartWidget(tab1Sec1);
			addSupplyStatusChartWidget(tab1Sec1);
		}

		if(AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getAppCategory() != ApplicationContext.AppCategory.PORTALS.getIndex()) {
			addWoCountWidget(tab1Sec1);
			addAlarmCountWidget(tab1Sec1);
			addFailureMetricWidget(tab1Sec1);
		}
		//special check added ECM Ooredo account to see required fields in place of secondaryDetailsWidget in client portal alone
		boolean addedSummaryWidgetForClientPortalOfECMOoredo = addSummaryWidgetForClientPortalOfECMOOredo(module, tab1Sec1);

		if(!addedSummaryWidgetForClientPortalOfECMOoredo) {
			if (AccountUtil.getCurrentOrg().getOrgId() == 436) {
				addCustomSecondaryDetailsWidget(tab1Sec1);
			} else {
				addSecondaryDetailsWidget(tab1Sec1);
			}
		}
		
		addCommonSubModuleWidget(tab1Sec1, module, asset);
		
		
		if (AccountUtil.getCurrentOrg().getOrgId() == 183) {
			if (asset.getCategory().getId() == 2938) {
				addAssetReadingChart(tab1Sec1, 2127, "MDB Consumption");
				addAssetReadingChart(tab1Sec1, 4037, "Energy Reading Trend");
			}
			else if (asset.getCategory().getId() == 2949) {
				addAssetReadingChart(tab1Sec1, 2128, "Water Consumption");
				addAssetReadingChart(tab1Sec1, 4042, "Water Meter Reading Trend");
			}
			else if (asset.getCategory().getId() == 3027 && (asset.getId() == 1275717 ||  asset.getId() == 1275718 || asset.getId() == 127519)) {
				addAssetReadingChart(tab1Sec1, 4034, "Chilled Water Consumption");
				addAssetReadingChart(tab1Sec1, 4038, "Chilled Water Reading Trend");
				addAssetReadingChart(tab1Sec1, 4041, "Chilled Water Flow Trend");
				addAssetReadingChart(tab1Sec1, 4039, "Temperature Trend");
			}
		}
		
		
		Tab tab2 = page.new Tab("maintenance");
		page.addTab(tab2);
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		addNextPmWidget(tab2Sec1);
		addWoDetailsWidget(tab2Sec1);
		addRecentlyClosedWidget(tab2Sec1);
		
		Section tab2Sec2 = page.new Section("plannedWorkorder");
		tab2.addSection(tab2Sec2);
		addPlannedWoWidget(tab2Sec2);
		
		Section tab2Sec3 = page.new Section("unplannedWorkorder");
		tab2.addSection(tab2Sec3);
		addUnPlannedWoWidget(tab2Sec3);

		if(AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getAppCategory() != ApplicationContext.AppCategory.PORTALS.getIndex()) {
			Tab tab3 = page.new Tab("readings");
			page.addTab(tab3);

			Section tab3Sec1 = page.new Section();
			tab3.addSection(tab3Sec1);

			addReadingWidget(tab3Sec1);

			if (AccountUtil.isFeatureEnabled(FeatureLicense.CONTROL_ACTIONS)) {
				Section tab3Sec2 = page.new Section();
				tab3.addSection(tab3Sec2);
				addCommandsWidget(tab3Sec2);
			}

			Tab tab4 = page.new Tab("performance");
			page.addTab(tab4);

			Section tab4Sec1 = page.new Section();
			tab4.addSection(tab4Sec1);

			addAlarmInsightsWidget(tab4Sec1);
			addLastDownTimeWidget(tab4Sec1);
			addOverallDowntimeWidget(tab4Sec1);


			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ContextNames.ASSET_BREAKDOWN));

			Criteria breakdownCriteria = new Criteria();
			breakdownCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("asset"), String.valueOf(asset.getId()), NumberOperators.EQUALS));

			addFailureRateWidget(tab4Sec1, breakdownCriteria);
			addAvgTtrWidget(tab4Sec1, breakdownCriteria);


			// ----- Financial Tab Start --------
			Tab tab7 = page.new Tab("financial");
			if(!isEmicoolOrg) {
				page.addTab(tab7);
			}
			Section tab7Sec1 = page.new Section();
			tab7.addSection(tab7Sec1);

			addAssetCostDetailsWidget(tab7Sec1);

			Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(modBean.getAllFields(ContextNames.WORK_ORDER));
			Map<String, FacilioField> woCostFieldMap = FieldFactory.getAsMap(modBean.getAllFields(ContextNames.WORKORDER_COST));

			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(woCostFieldMap.get("parentId"), String.valueOf(""), NumberOperators.EQUALS));
			addCostBreakupWidget(tab7Sec1, criteria);

			criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(woFieldMap.get("resource"), String.valueOf(asset.getId()), NumberOperators.EQUALS));
			addMaintenanceCostTrendWidget(tab7Sec1, criteria);

			if (AssetDepreciationAPI.getDepreciationOfAsset(asset.getId()) != null) {
				addDepreciationScheduleWidget(tab7Sec1);
			}
			addDepreciationCostTrendWidget(tab7Sec1, asset, modBean);

			// ----- Financial Tab End --------


			// if (AccountUtil.isFeatureEnabled(FeatureLicense.GRAPHICS)) {
			if ((isDemoOrg() && asset.isConnected() && AccountUtil.isFeatureEnabled(FeatureLicense.GRAPHICS))
					|| (AccountUtil.getCurrentOrg().getOrgId() == 75 && module.getName().equals("fahu"))
					|| (AccountUtil.getCurrentOrg().getOrgId() == 253)
					|| (AccountUtil.getCurrentOrg().getOrgId() == 323)
					|| (AccountUtil.getCurrentOrg().getOrgId() == 297l && (module.getName().equals("ahu") || module.getName().equals("vav")))
			) {

				Tab tab6 = page.new Tab("graphics", "graphics");
				page.addTab(tab6);

				Section tab6Sec1 = page.new Section();
				tab6.addSection(tab6Sec1);

				addGraphicsWidget(tab6Sec1);
			}

			if (asset.getGeoLocationEnabled() != null && asset.getGeoLocationEnabled() && !asset.isConnected() && AccountUtil.getCurrentOrg().getOrgId() == 155) {
				Tab tab8 = page.new Tab("assetMovement");
				page.addTab(tab8);
				Section tab8Sec1 = page.new Section();
				tab8.addSection(tab8Sec1);
				addAssetMovementsWidget(tab8Sec1);
			}


			if (AccountUtil.isFeatureEnabled(FeatureLicense.CONTRACT) && AccountUtil.getCurrentOrg().getOrgId() == 155) {
				Tab tab5 = page.new Tab("contracts");
				page.addTab(tab5);

				Section tab5Sec1 = page.new Section();
				tab5.addSection(tab5Sec1);

				addContractWidget(tab5Sec1);
			}

			if (AccountUtil.isFeatureEnabled(FeatureLicense.INVENTORY)) {
				if (asset.getRotatingItem() != null && asset.getRotatingItem().getId() > 0) {

					Tab tab9 = page.new Tab("inventory usage");
					page.addTab(tab9);

					Section tab5Sec1 = page.new Section();
					tab9.addSection(tab5Sec1);
					addInventoryTransactionsWidget(tab5Sec1, "itemTransactions");
				} else if (asset.getRotatingTool() != null && asset.getRotatingTool().getId() > 0) {

					Tab tab9 = page.new Tab("inventory usage");
					page.addTab(tab9);

					Section tab5Sec1 = page.new Section();
					tab9.addSection(tab5Sec1);
					addInventoryTransactionsWidget(tab5Sec1, "toolTransactions");
				}
				if (AccountUtil.isFeatureEnabled(FeatureLicense.ASSET_SPARE_PARTS)) {
					Tab tab10 = page.new Tab("spare parts");
					page.addTab(tab10);
					Section sparePartsSection = page.new Section();
					tab10.addSection(sparePartsSection);
					addSparePartsWidget(sparePartsSection);
				}

			}

			FacilioModule assetModule = modBean.getModule(ContextNames.ASSET);
			if (AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
				addSafetyPlanTab(page, asset);
			}

				Tab tab11 = page.new Tab("Related");
				List<Long> moduleIds = new ArrayList<>();
				moduleIds.add(module.getModuleId());
				if (asset.getCategory() != null) {
					V3AssetCategoryContext category = V3RecordAPI.getRecord(ContextNames.ASSET_CATEGORY, asset.getCategory().getId(), V3AssetCategoryContext.class);
					if (category != null) {
						moduleIds.add(category.getAssetModuleID());
					}
				}
				boolean isRelationshipAdded = addRelationshipSection(page, tab11, moduleIds);

				Section tab11Sec1 = getRelatedListSectionObj(page);
				tab11.addSection(tab11Sec1);

				addRelatedListWidgets(tab11Sec1, assetModule.getModuleId());
				if ((tab11Sec1.getWidgets() != null && !tab11Sec1.getWidgets().isEmpty()) || isRelationshipAdded) {
					page.addTab(tab11);
				}
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLASSIFICATION)){
			addClassificationTab(page);
		}
		if(!AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)) {

			Tab tab10 = page.new Tab("history", "history");
			page.addTab(tab10);

			Section tab10Sec1 = page.new Section();
			tab10.addSection(tab10Sec1);

			addHistoryWidget(tab10Sec1);

		}
		if(AccountUtil.getCurrentApp() != null && AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)) {
			Tab tab11 = page.new Tab("Related");
			List<Long> moduleIds = new ArrayList<>();
			moduleIds.add(module.getModuleId());
			if (asset.getCategory() != null) {
				V3AssetCategoryContext category = V3RecordAPI.getRecord(ContextNames.ASSET_CATEGORY, asset.getCategory().getId(), V3AssetCategoryContext.class);
				if (category != null) {
					moduleIds.add(category.getAssetModuleID());
				}
			}
			FacilioModule assetModule = modBean.getModule(ContextNames.ASSET);

			boolean isRelationshipAdded = addRelationshipSection(page, tab11, moduleIds);

			Section tab11Sec1 = getRelatedListSectionObj(page);
			tab11.addSection(tab11Sec1);

			addRelatedListWidgets(tab11Sec1, assetModule.getModuleId());
			page.addTab(tab11);
		}

		return page;
	}

	private static boolean isDemoOrg() {
		return AccountUtil.getCurrentOrg().getOrgId() == 210 || AccountUtil.getCurrentOrg().getOrgId() == 321; 
	}
	
	private static void addPrimaryDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 6);
		pageWidget.addToWidgetParams("showOperatingHours", true);
		section.addWidget(pageWidget);
	}
	
	private static void addWoCountWidget(Section section) {
		PageWidget workorderWidget = getCountModuleWidget(FacilioConstants.ContextNames.WORK_ORDER);
		workorderWidget.addToLayoutParams(section, 7, 8);
		section.addWidget(workorderWidget);
	}
	
	private static void addAlarmCountWidget(Section section) {
		PageWidget alarmWidget = getCountModuleWidget(FacilioConstants.ContextNames.ALARM);
		alarmWidget.addToLayoutParams(section, 7, 8);
		section.addWidget(alarmWidget);
	}

	private static boolean addSummaryWidgetForClientPortalOfECMOOredo(FacilioModule module, Section tab1Sec1) throws Exception {
        try {
			boolean isEcmOoredoClientSummaryWidget = AccountUtil.getCurrentOrg().getOrgId() == 552 && AccountUtil.getCurrentOrg().getDomain().equals("ecmooredoo") && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomain.AppDomainType.CLIENT_PORTAL;
            if (isEcmOoredoClientSummaryWidget) {
                SummaryWidget pageWidget = SummaryWidgetUtil.getMainSummaryWidgetForApp(module.getModuleId());
                if (pageWidget != null) {
                    PageWidget newSummaryFieldsWidget = new PageWidget(WidgetType.SUMMARY_FIELDS_WIDGET, FacilioConstants.WidgetNames.MAIN_SUMMARY_WIDGET);
                    newSummaryFieldsWidget.addToLayoutParams(tab1Sec1, 24, 5);
                    tab1Sec1.addWidget(newSummaryFieldsWidget);
                    return true;
                }
            }
        } catch (Exception e) {
			LOGGER.info("Error occured while adding ECM Ooredo Summary widget for client portal");
		}
		return false;
	}
	
	private static void addFailureMetricWidget(Section section) {
		PageWidget fddWidget = new PageWidget(WidgetType.CARD);
		fddWidget.addToLayoutParams(section, 10, 8);
		fddWidget.addCardType(CardType.FAILURE_METRICS);
		section.addWidget(fddWidget);
	}
	
	private static void addSecondaryDetailsWidget(Section section) {
		// col1Sec2.setName("overview");
		// col1Sec2.setDisplayName("common.page.overview");
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}
	
	private static void addCustomSecondaryDetailsWidget(Section section) {
		// col1Sec2.setName("overview");
		// col1Sec2.setDisplayName("common.page.overview");
		PageWidget detailsWidget = new PageWidget(WidgetType.CUSTOM_SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}
	
	private static void addNextPmWidget(Section section) {
		PageWidget nextPmWidget = new PageWidget(WidgetType.CARD);
		nextPmWidget.addToLayoutParams(section, 8, 7);
		nextPmWidget.addCardType(CardType.NEXT_PM);
		section.addWidget(nextPmWidget);
	}
	
	private static void addWoDetailsWidget(Section section) {
		PageWidget woDetailsWidget = new PageWidget(WidgetType.CARD);
		woDetailsWidget.addToLayoutParams(section, 8, 7);
		woDetailsWidget.addCardType(CardType.WO_DETAILS);
		section.addWidget(woDetailsWidget);
	}
	
	private static void addRecentlyClosedWidget(Section section) {
		PageWidget recentlyClosedWidget = new PageWidget(WidgetType.CARD);
		recentlyClosedWidget.addToLayoutParams(section, 8, 7);
		recentlyClosedWidget.addCardType(CardType.RECENTLY_CLOSED_PM);
		section.addWidget(recentlyClosedWidget);
	}
	
	private static void addPlannedWoWidget(Section section) {
		PageWidget plannedWidget = new PageWidget(WidgetType.LIST, "plannedWorkorder");
		plannedWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(plannedWidget);
	}
	
	private static void addUnPlannedWoWidget(Section section) {
		PageWidget unPlannedWidget = new PageWidget(WidgetType.LIST, "unPlannedWorkorder");
		unPlannedWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(unPlannedWidget);
	}
	
	private static void addReadingWidget(Section section) {
		PageWidget readingsWidget = new PageWidget(WidgetType.LIST, "readings");
		readingsWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(readingsWidget);
	}
	private static void addCommandsWidget(Section section) {
		PageWidget readingsWidget = new PageWidget(WidgetType.COMMANDS_WIDGET);
		readingsWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(readingsWidget);
	}

	private static void addSparePartsWidget(Section section) {
		PageWidget sparePartsWidget = new PageWidget(WidgetType.SPARE_PARTS);
		sparePartsWidget.addToLayoutParams(section, 24, 11);
		section.addWidget(sparePartsWidget);
	}
	private static void addAssetMovementsWidget(Section section) {
		PageWidget assetMovementsWidget = new PageWidget(WidgetType.LIST, "assetMovements");
		assetMovementsWidget.addToLayoutParams(section, 24, 50);
		section.addWidget(assetMovementsWidget);
	}
	
	private static void addCommandWidget(Section section, long assetId) {
		
		List<ReadingDataMeta> writableReadings = null;
		try {

			boolean hasPermission = ControlActionUtil.hasControlPermission();
			if (!hasPermission) {
				return;
			}

			writableReadings = ReadingsAPI.getReadingDataMetaList(Collections.singletonList(assetId), null, true, ReadingType.WRITE);
		
			if (CollectionUtils.isNotEmpty(writableReadings)) {
				for(ReadingDataMeta rdm : writableReadings) {
					
					ReadingsAPI.convertUnitForRdmData(rdm);
					
					PageWidget commandWidget = new PageWidget(WidgetType.CARD);
					commandWidget.addToLayoutParams(section, 6, 6);
					commandWidget.addCardType(CardType.SET_COMMAND);
					
					JSONObject obj = new JSONObject();
					obj.put("field", rdm.getField());
					obj.put("value", rdm.getValue());
					try {
						obj.put("inputUnit", FieldUtil.getAsProperties(rdm.getUnitEnum()));
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						LOGGER.error("Error in converting unit", e);
					}
	
					commandWidget.addToWidgetParams("data", obj);
					section.addWidget(commandWidget);
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("Error in fetching writable readings", e);
		}
		
	}
	
	private static void addAssetCostDetailsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 5);
		cardWidget.addCardType(CardType.ASSET_COST_DETAILS);
		section.addWidget(cardWidget);
	}
	private static void addCostBreakupWidget(Section section, Criteria criteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 11);
		cardWidget.addCardType(CardType.COST_BREAKUP);
		addChartParams(null,cardWidget, "costType", "cost", criteria);
		section.addWidget(cardWidget);
	}
	
	private static void addDepreciationScheduleWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 7);
		cardWidget.addCardType(CardType.DEPRECIATION_SCHEDULE);
		section.addWidget(cardWidget);
	}
	
	private static void addDepreciationCostTrendWidget(Section tab7Sec1, AssetContext asset, ModuleBean modBean) throws Exception {
		if (AssetDepreciationAPI.getDepreciationOfAsset(asset.getId()) != null) {
			AssetDepreciationContext assetDepreciation = AssetDepreciationAPI.getDepreciationOfAsset(asset.getId());
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ContextNames.ASSET_DEPRECIATION_CALCULATION));
			Criteria depreciationCostCriteria = new Criteria();
			depreciationCostCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("asset"), String.valueOf(asset.getId()), NumberOperators.EQUALS));
			FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
			Long startDate = (long) FieldUtil.getValue(asset, assetDepreciation.getStartDateFieldId(), assetModule);
			if (startDate == null || startDate == -1) {
                throw new IllegalArgumentException("Start date cannot be empty");
            } else {
			startDate = DateTimeUtil.getMonthStartTimeOf(startDate);
			ZonedDateTime endDate = DateTimeUtil.getDateTime(startDate);
			List<Long> DateValue = new ArrayList<>();
			DateValue.add(startDate);
			switch(assetDepreciation.getFrequencyTypeEnum()) {
				case MONTHLY:
					endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(1*assetDepreciation.getFrequency(), ChronoUnit.MONTHS));
					DateValue.add(endDate.toInstant().toEpochMilli());
					addDepreciationCostTrendWidget(tab7Sec1, depreciationCostCriteria,DateOperators.CURRENT_N_MONTH,DateValue,DateAggregateOperator.MONTHANDYEAR);
					break;
				case QUARTERLY:
					endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(3*assetDepreciation.getFrequency(), ChronoUnit.MONTHS));
					DateValue.add(endDate.toInstant().toEpochMilli());
					addDepreciationCostTrendWidget(tab7Sec1, depreciationCostCriteria,DateOperators.CURRENT_N_QUARTER,DateValue,DateAggregateOperator.QUARTERLY);
					break;
				case HALF_YEARLY:
					endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(6*assetDepreciation.getFrequency(), ChronoUnit.MONTHS));
					DateValue.add(endDate.toInstant().toEpochMilli());
					addDepreciationCostTrendWidget(tab7Sec1, depreciationCostCriteria,DateOperators.CURRENT_N_QUARTER,DateValue,DateAggregateOperator.QUARTERLY);
					break;
				case YEARLY:
					endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(1*assetDepreciation.getFrequency(), ChronoUnit.YEARS));
					DateValue.add(endDate.toInstant().toEpochMilli());
					addDepreciationCostTrendWidget(tab7Sec1, depreciationCostCriteria,DateOperators.CURRENT_N_YEAR,DateValue,DateAggregateOperator.YEAR);
					break;
				default:
					endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(1*assetDepreciation.getFrequency(), ChronoUnit.MONTHS));
					DateValue.add(endDate.toInstant().toEpochMilli());
					addDepreciationCostTrendWidget(tab7Sec1, depreciationCostCriteria,DateOperators.CURRENT_N_MONTH,DateValue,DateAggregateOperator.MONTHANDYEAR);
					break;
				}
            }
		}
	}
	
	private static void addMaintenanceCostTrendWidget(Section section, Criteria criteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "maintenanceCostTrend");
		cardWidget.addToLayoutParams(section, 24, 13);
		cardWidget.addCardType(CardType.MAINTENANCE_COST_TREND);
		addChartParams(cardWidget, "createdTime","Created Time", "totalCost","Total Cost","plannedvsunplanned", criteria);
		section.addWidget(cardWidget);
	}
	
	private static void addDepreciationCostTrendWidget(Section section, Criteria criteria, DateOperators dateoperator,List<Long> dateOperatorValue, DateAggregateOperator xAggr) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "depreciationCostTrend");
		cardWidget.addToLayoutParams(section, 24, 12);
		cardWidget.addCardType(CardType.DEPRECIATION_COST_TREND);
		List<String> yFields = new ArrayList<>();
		yFields.add("currentPrice");
		yFields.add("depreciatedAmount");
		addChartParams(cardWidget, "line", xAggr, "calculatedDate", NumberAggregateOperator.SUM, yFields, null , dateoperator, dateOperatorValue, criteria);
		section.addWidget(cardWidget);
	}
	
	private static void addAssetLifeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addCardType(CardType.ASSET_LIFE);
		section.addWidget(cardWidget);
	}
	
	private static void addAlarmInsightsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 11);
		cardWidget.addCardType(CardType.ALARM_INSIGHTS);
		section.addWidget(cardWidget);
	}
	
	private static void addLastDownTimeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "lastReportedDownTime");
		cardWidget.addToLayoutParams(section, 12, 5);
		cardWidget.addCardType(CardType.LAST_DOWNTIME);
		section.addWidget(cardWidget);
	}
	
	private static void addOverallDowntimeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "overallDownTime");
		cardWidget.addToLayoutParams(section, 12, 5);
		cardWidget.addCardType(CardType.OVERALL_DOWNTIME);
		section.addWidget(cardWidget);
	}
	
	private static void addFailureRateWidget(Section section, Criteria breakdownCriteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "failureRate");
		cardWidget.addToLayoutParams(section, 12, 12);
		cardWidget.addCardType(CardType.FAILURE_RATE);
		
		addChartParams(cardWidget, "fromtime", "timeBetweenFailure", breakdownCriteria);
		
		section.addWidget(cardWidget);
	}
	
	private static void addAvgTtrWidget(Section section, Criteria breakdownCriteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "avgTtr");
		cardWidget.addToLayoutParams(section, 12, 12);
		cardWidget.addCardType(CardType.AVG_TTR);
		
		addChartParams(cardWidget, "fromtime", "duration", breakdownCriteria);
		
		section.addWidget(cardWidget);
	}
	
	
	private static void addGraphicsWidget(Section section) {
		PageWidget widget = new PageWidget(WidgetType.GRAPHICS);
		widget.addToLayoutParams(section, 24, 3);
		section.addWidget(widget);
	}
	
	private static void addContractWidget(Section section) {
		PageWidget contractsWidget = new PageWidget(WidgetType.LIST, "contracts");
		contractsWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(contractsWidget);
	}

	private static void addInventoryTransactionsWidget(Section section, String moduleName) {
		PageWidget pageWidget = new PageWidget(WidgetType.LIST, moduleName);
		pageWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(pageWidget);
	}

	private static void addSupplyTemperatureChartWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "supplyTemperature");
		cardWidget.addToLayoutParams(section, 24, 8);
		cardWidget.addCardType(CardType.ANALYTICS_CHART);
		
		section.addWidget(cardWidget);
	}
	private static void addSupplyFeedbackChartWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "supplyFeedback");
		cardWidget.addToLayoutParams(section, 12, 7);
		cardWidget.addCardType(CardType.ANALYTICS_CHART);
		
		section.addWidget(cardWidget);
	}
	private static void addSupplyStatusChartWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "supplyStatus");
		cardWidget.addToLayoutParams(section, 12, 7);
		cardWidget.addCardType(CardType.ANALYTICS_CHART);
		
		section.addWidget(cardWidget);
	}
	private static void addSafetyPlanHazardsWidget(Section section) {
		PageWidget widget = new PageWidget(WidgetType.LIST, "safetyPlanPrecautions");
		widget.addToLayoutParams(section, 24, 10);
		section.addWidget(widget);
	}
	private static void addAssetHazardWidget(Section section) {
		PageWidget widget = new PageWidget(WidgetType.LIST, "commonHazardList");
		widget.addToLayoutParams(section, 24, 10);
		section.addWidget(widget);
	}
	public static void addRelatedListWidget(Section section, String moduleName, long parenModuleId, String moduleDisplayName) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == parenModuleId)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fields)) {
			for (FacilioField field : fields) {
				PageWidget relatedListWidget = new PageWidget(WidgetType.RELATED_LIST);
				JSONObject relatedList = new JSONObject();
				module.setDisplayName(moduleDisplayName);
				relatedList.put("module", module);
				relatedList.put("field", field);
				relatedListWidget.setRelatedList(relatedList);
				relatedListWidget.addToLayoutParams(section, 24, 10);
				section.addWidget(relatedListWidget);
			}
		}
	}
	private static Page.Section addSafetyPlanTab(Page page, AssetContext asset) throws Exception {
		Page.Tab safetyPlanTab = page.new Tab("safety plan");
		page.addTab(safetyPlanTab);

		Page.Section safetyPlanSection = page.new Section();
		safetyPlanTab.addSection(safetyPlanSection);

		// hazards widget
		PageWidget hazards = new PageWidget(PageWidget.WidgetType.SAFETYPLAY_HAZARD);
		hazards.addToLayoutParams(0, 0, 24, 9);
		safetyPlanSection.addWidget(hazards);

		// precautions widget
		PageWidget widget = new PageWidget(WidgetType.LIST, "safetyPlanPrecautions");
		widget.addToLayoutParams(safetyPlanSection, 24, 9);
		safetyPlanSection.addWidget(widget);

		return safetyPlanSection;
	}
	private static void addClassificationTab(Page page){
		Page.Tab tab = page.new Tab("Specification");
		page.addTab(tab);
		Page.Section tab1Sec1 = page.new Section();
		tab.addSection(tab1Sec1);
		PageWidget classificationWidget = new PageWidget(PageWidget.WidgetType.CLASSIFICATION);
		classificationWidget.setName("Classification");
		classificationWidget.addToLayoutParams(tab1Sec1, 24, 8);
		classificationWidget.addToWidgetParams("activityModuleName", ContextNames.ASSET_ACTIVITY);
		tab1Sec1.addWidget(classificationWidget);
	}
}

