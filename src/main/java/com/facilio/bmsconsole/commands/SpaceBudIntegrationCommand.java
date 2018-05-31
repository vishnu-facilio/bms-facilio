package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;

public class SpaceBudIntegrationCommand implements Command {
	
	private static final Logger logger = LogManager.getLogger(SpaceBudIntegrationCommand.class.getName());

	private static final String POST_URL = "http://13.228.175.41:8086/onboarding/eventcontroller/event";
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (AccountUtil.getCurrentOrg().getOrgId() == 78) {
			
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");
			headers.put("access_token", "f4800a0a30b0e00484ae1f7f65f45956");
			
			JSONObject content = new JSONObject();
			Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
			if (readingMap.containsKey(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
				List<ReadingContext> readings = readingMap.get(FacilioConstants.ContextNames.ENERGY_DATA_READING);
				for (ReadingContext reading : readings) {
					if (reading.getParentId() == 2266 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_101_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2268 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_102_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2270 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_103_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2272 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_104_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2274 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_201_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2276 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_202_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2278 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_203_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2280 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_204_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2282 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_301_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2284 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_302_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2286 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_303_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2288 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_304_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2290 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_402A_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2292 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_402B_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2294 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_403_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2296 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_404_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2298 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_501_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2300 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_502_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2303 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_503_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2305 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_504_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2307 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_601_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2309 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_602_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2311 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_603_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2313 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_604_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2315 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_701A_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2317 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_702A_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2319 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_703_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2321 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_704_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2323 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_801_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2325 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_802_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2327 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_803_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2329 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_804_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2331 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_901B_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2333 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_902B_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2335 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_903A_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2337 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_903B_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2340 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_1001B_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2342 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_1002_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2344 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_1003A_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
					else if (reading.getParentId() == 2346 && reading.getReading("totalConsumptionDelta") != null) {
						content.put("EM_1003B_EB_KWH", reading.getReading("totalConsumptionDelta"));
					}
				}
			}
			if (!content.isEmpty()) {
				logger.log(Level.INFO, "SpaceBudIntegrationCommand POST content " + content.toJSONString());
				String result = AwsUtil.doHttpPost(POST_URL, headers, null, content.toJSONString());
				logger.log(Level.INFO, "SpaceBudIntegrationCommand POST DONE " + result);
			}
		}
		return false;
	}
}
