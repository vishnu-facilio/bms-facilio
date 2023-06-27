package com.facilio.odataservice.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.odataservice.util.ODataReadingViewsUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class ODataReadingsDataAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(ODataReadingsDataAction.class.getName());;
    static String readingViewName;
    public static void setReadingViewName(String name){
        readingViewName = name;
    }
    static List<Map<String, Object>> data;

    public static ODataReadingsContext getContext() throws Exception {
        return ODataReadingViewsUtil.getReadingView(readingViewName);
    }


    private static void setData(List<Map<String, Object>> result) {
        data = result;
    }
    public static List<Map<String, Object>> getData(String name) throws Exception{
        readingViewName = name;
        FacilioChain chain = ReadOnlyChainFactoryV3.getODataReadingsChain();
        FacilioContext context = chain.getContext();
        ODataReadingsContext readingsContext = getContext();
        context.put(FacilioConstants.ContextNames.ODATA_READING_VIEW, readingsContext);
        chain.execute();
        setData((List<Map<String, Object>>) context.get("result"));
        return data;
    }
}
