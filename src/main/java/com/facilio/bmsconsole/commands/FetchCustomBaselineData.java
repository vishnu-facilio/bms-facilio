package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.ReportContext;
import org.apache.commons.chain.Context;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchCustomBaselineData extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(FetchCustomBaselineData.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(context.containsKey("scatterConfig") || context.containsKey("report") ) {
            JSONParser jsonParser = new JSONParser();

            JSONObject scatterConfig;
            if(context.containsKey("scatterConfig")){
                scatterConfig = (JSONObject) jsonParser.parse(String.valueOf(context.get("scatterConfig")));
            }
            else if (context.containsKey("report")){
                ReportContext report = (ReportContext)  context.get("report");
                JSONObject reportStateJson =  report.getReportState();
                if(reportStateJson != null && reportStateJson.containsKey("scatterConfig")) {
                    scatterConfig = (JSONObject) jsonParser.parse(String.valueOf(reportStateJson.get("scatterConfig")));
                } else {
                    return false;
                }
            } else {
                return false;
            }
            JSONObject properties = (JSONObject) jsonParser.parse(String.valueOf(scatterConfig.get("properties")));
            if(properties.containsKey("baselineData"))
            {
                JSONObject baselineData = (JSONObject) jsonParser.parse(String.valueOf(properties.get("baselineData")));
                if(baselineData != null && baselineData.containsKey("data")) {
                    JSONArray data = (JSONArray) jsonParser.parse(String.valueOf(baselineData.get("data")));
                    List<Map<String, Object>> resultArray = new ArrayList();;
                    for(Object i : data){
                        Long id = (Long) i;
                        FacilioModule module = ModuleFactory.getScatterGraphLineModule();
                        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                                .select(FieldFactory.getScatterGraphFields())
                                .table(module.getTableName())
                                .andCustomWhere("ID = ?", id);
                        List<Map<String, Object>> result = select.get();
                        if(result.size() != 0) {
                            resultArray.add(result.get(0));
                        }
                    }
                    context.put("baselineData", resultArray);
                    if(baselineData.containsKey("colors")){
                        context.put("baselineDataColors", baselineData.get("colors"));
                    }
                }
            }
        }
        long orgId = AccountUtil.getCurrentOrg().getId();
        if(orgId == 6l) {
            LOGGER.info("FetchCustomBaselineData is" + context);
        }
        return false;
    }
}
