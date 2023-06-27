package com.facilio.bmsconsoleV3.commands.ODataReadings;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class GetODataReadingFieldsCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(GetODataReadingFieldsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ODataReadingsContext context1 = (ODataReadingsContext) context.get(FacilioConstants.ContextNames.ODATA_READING_VIEW);
        String fieldObj = context1.getReadingFields();
        List<String> fieldNames = new ArrayList<>();
        Map<String,List<Long>> moduleVsFieldsMap = new HashMap<>();
        if(fieldObj != null&& !fieldObj.isEmpty() && !fieldObj.equalsIgnoreCase("null")&& !fieldObj.equalsIgnoreCase("[]")) {
            LOGGER.info("fieldobj not empty");
            JSONParser jsonParser = new JSONParser();
            List<String> resObj = (List<String>) jsonParser.parse(fieldObj);
            for(int i=0;i< resObj.size();i++) {
                Map<String,Object> resMap = (Map<String, Object>) jsonParser.parse(resObj.get(i));
                fieldNames.add((String) resMap.get("fieldName"));
                Long moduleId = (Long) resMap.get("moduleId");
                String modulName = moduleBean.getModule(moduleId).getName();
                if (moduleVsFieldsMap.isEmpty()) {
                    moduleVsFieldsMap.put(modulName, new ArrayList<>());
                }
                if (moduleVsFieldsMap.get(modulName) == null || moduleVsFieldsMap.get(modulName).isEmpty()) {
                    moduleVsFieldsMap.put(modulName, new ArrayList<>());
                }
                List<Long> currModule = moduleVsFieldsMap.get(modulName);
                currModule.add((Long) resMap.get("fieldId"));
                moduleVsFieldsMap.put(modulName, currModule);
            }
        }
        context.put(FacilioConstants.ContextNames.MODULE_AND_FIELDS,moduleVsFieldsMap);
        return false;
    }
}