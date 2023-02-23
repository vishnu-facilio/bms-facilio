package com.facilio.weather.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.weather.util.WeatherAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class GetCurrentWeatherDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long stationId = (long) context.get("stationId");
        long siteId = (long) context.get("siteId");

        V3Util.throwRestException(stationId == 0 && siteId == 0, ErrorCode.VALIDATION_ERROR,
                "either stationId or siteId has to be given");

        JSONObject data = new JSONObject();
        if(stationId == 0) {
            stationId = WeatherAPI.getStationIdForSiteId(siteId);
            if(stationId == 0) {
                String errMsg = "given siteId is not associated with any weather station, siteId :: "+siteId;
                LOGGER.error(errMsg);
                context.put("code", -1);
                context.put("message", errMsg);
            }
        }
        List<ReadingContext> records = getTodayWeatherRecords(stationId);
        if(!records.isEmpty()) {
            data = FieldUtil.getAsJSON(records.get(0));
        }
        context.put("data", data);
        return false;
    }

    private List<ReadingContext> getTodayWeatherRecords(Long stationId) throws Exception {
        if(stationId == 0) {
            return new ArrayList<>();
        }
        long startTime = DateTimeUtil.getDayStartTime();
        long currTime = DateTimeUtil.getCurrenTime();

        ModuleBean modBean = Constants.getModBean();
        String moduleName = FacilioConstants.ContextNames.NEW_WEATHER_READING;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        FacilioField ttime = fieldMap.get("ttime");

        Condition startTimeCond = CriteriaAPI.getCondition(ttime, startTime+"", NumberOperators.GREATER_THAN_EQUAL);
        Condition endTimeCond = CriteriaAPI.getCondition(ttime, currTime+"", NumberOperators.LESS_THAN_EQUAL);
        Condition stationCond = CriteriaAPI.getCondition(fieldMap.get("parentId"), stationId+"", NumberOperators.EQUALS);

        SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
                .beanClass(ReadingContext.class)
                .table(module.getTableName())
                .module(module)
                .select(fields)
                .andCondition(stationCond)
                .andCondition(startTimeCond)
                .andCondition(endTimeCond)
                .orderBy("ID DESC")
                .limit(1);

        return builder.get();
    }
}
