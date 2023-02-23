package com.facilio.weather.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GetWeatherReadingFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        String moduleName = FacilioConstants.ContextNames.NEW_WEATHER_READING;
        List<FacilioField> fields = ReadingsAPI.filterSystemFields(modBean.getAllFields(moduleName));
        List<String> unwantedFields = Arrays.asList(
                //not needed in analytics page
                "icon",
                "summary",
                //in openweather weather api, these values aren't calculated
                "dewPoint",
                "windBearing",
                "precipitationProbability",
                "precipitationIntensityError",
                "uvIndex",
                "nearestStormDistance",
                "nearestStormBearing"
        );
        fields = fields.stream()
                .filter(row ->  !unwantedFields.contains(row.getName()))
                .collect(Collectors.toList());
        context.put("fields", fields);
        return false;
    }
}
