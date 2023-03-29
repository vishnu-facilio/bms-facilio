package com.facilio.wmsv2.bean;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.weather.commands.WeatherTransactionChainFactory;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.time.ZoneId;

@Log4j
public class LongTasksBeanImpl implements LongTasksBean {

    @Override
    public void addBulkWeatherStationMigration(JSONObject data) throws Exception {
        FacilioChain chain = WeatherTransactionChainFactory.addWeatherStationMigratinChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        //Send email
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject json = new JSONObject();
        json.put("sender", EmailClient.getFromEmail("info"));
        json.put("to", data.get("emailAddress"));
        json.put("_tracking", false);
        json.put("subject" , String.valueOf(orgId) + " - Bulk weather station migration status");
        StringBuilder body = new StringBuilder();
        body.append("Details : \nStart Time : ").append(data.get("startTime"))
                .append("\nCompleted Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
                .append("\nStatus : ")
                .append(context.get("message"));
        json.put("message", body.toString());
        FacilioFactory.getEmailClient().sendMailWithoutTracking(json, null);
    }

}
