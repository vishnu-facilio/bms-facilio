package com.facilio.bmsconsole.jobs;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.CostAssetsContext;
import com.facilio.bmsconsole.context.CostContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.CostAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class CostCalculatorJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(CostCalculatorJob.class.getName());
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			List<CostContext> costs = CostAPI.getAllCosts();
			if (costs != null && !costs.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				Map<String, List<ReadingContext>> costReadings = new HashMap<>();
				for (CostContext cost : costs) {
					if (cost.getAssets() != null && !cost.getAssets().isEmpty()) {
						FacilioModule costReadingModule = modBean.getModule(cost.getReadingId());
						List<FacilioField> fields = modBean.getAllFields(costReadingModule.getName());
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
						FacilioField totalCostField = fieldMap.get("totalCost");
						List<ReadingContext> readings = new ArrayList<>();
						
						Map<String, ReadingDataMeta> rdmMap = getRDMMap(cost, totalCostField);
						for (CostAssetsContext asset : cost.getAssets()) {
							try {
								ReadingDataMeta rdm = rdmMap.get(asset.getAssetId()+"_"+totalCostField.getFieldId());
								ZonedDateTime startZdt = DateTimeUtil.getZonedDateTime(rdm.getTtime()).plusDays(1); //Calculating from next day after last calculated day
								long startTime = -1; 
								long firstBillTime = -1;
								if ((Double)rdm.getValue() == -1) {
									startTime = DateTimeUtil.getDayStartTime(getBillStartZdt(asset));
									firstBillTime = startTime;
								}
								else {
									startTime = DateTimeUtil.getDayStartTime(startZdt);
									firstBillTime = DateTimeUtil.getDayStartTime(CostAPI.getBillStartTime(asset, startZdt));
								}
								long endTime = DateTimeUtil.getDayStartTime() - 1;
								FacilioContext context = new FacilioContext();
								context.put(FacilioConstants.ContextNames.COST_FIRST_BILL_TIME, firstBillTime);
								context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
								context.put(FacilioConstants.ContextNames.COST, cost);
								context.put(FacilioConstants.ContextNames.COST_ASSET, asset);
								context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
								
								Chain calculateCostChain = FacilioChainFactory.calculateCostChain();
								calculateCostChain.execute(context);
								
								List<ReadingContext> assetCostReadings = (List<ReadingContext>) context.get(FacilioConstants.ContextNames.COST_READINGS);
								if (assetCostReadings != null && !assetCostReadings.isEmpty()) {
									readings.addAll(assetCostReadings);
								}
							}
							catch (Exception e) {
								LOGGER.error(cost.getName()+" Cost Calculation failed for asset : "+asset.getAssetId(), e);
								CommonCommandUtil.emailException(CostCalculatorJob.class.getName(), cost.getName()+" Cost Calculation failed for asset : "+asset.getAssetId(), e);
							}
						}
						
						if (!readings.isEmpty()) {
							LOGGER.info(cost.getName() + " Cost Readings size : " + readings.size());
							costReadings.put(costReadingModule.getName(), readings);
						}
					}
				}
				
				if (!costReadings.isEmpty()) {
					FacilioContext context = new FacilioContext();
					context.put(FacilioConstants.ContextNames.READINGS_MAP, costReadings);
					context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
					context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
					Chain addReading = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
					addReading.execute(context);
					
					LOGGER.info("Successfully added Cost Readings!!");
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Cost Calculator Job failed.", e);
			CommonCommandUtil.emailException(CostCalculatorJob.class.getName(), "Cost Calculator Job failed", e);
		}
	}
	
	private ZonedDateTime getBillStartZdt(CostAssetsContext asset) {
		ZonedDateTime zdt = DateTimeUtil.getDateTime().minusDays(1); //Always calculating for prev day
		return CostAPI.getBillStartTime(asset, zdt);
	}
	
	private Map<String, ReadingDataMeta> getRDMMap(CostContext cost, FacilioField totalCostField) throws Exception {
		
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		for (CostAssetsContext asset : cost.getAssets()) {
			rdmPairs.add(Pair.of(asset.getAssetId(), totalCostField));
		}
		
		Map<String, ReadingDataMeta> rdmMap = ReadingsAPI.getReadingDataMetaMap(rdmPairs);
		return rdmMap;
	}
}
