package com.facilio.odataservice.action;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.odataservice.util.ODataReadingViewsUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class ODataReadingsDataAction extends V3Action {
    private static final Logger LOGGER = LogManager.getLogger(ODataReadingsDataAction.class.getName());;
    String readingViewName;

    public ODataReadingsDataAction(String name) {
       setReadingViewName(name);
    }

    public  ODataReadingsContext getContext() throws Exception {
        return ODataReadingViewsUtil.getReadingView(readingViewName);
    }


    public  List<Map<String,Object>> getReadingData(String name) throws Exception{
        readingViewName = name;
        FacilioChain chain = ReadOnlyChainFactoryV3.getODataReadingsChain();
        FacilioContext context = chain.getContext();
        ODataReadingsContext readingsContext = getContext();
        context.put(FacilioConstants.ContextNames.ODATA_READING_VIEW, readingsContext);
        chain.execute();
        setData("data",context.get("result"));
        return (List<Map<String, Object>>) getData();
    }
}
