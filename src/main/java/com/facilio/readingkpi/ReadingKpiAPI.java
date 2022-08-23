package com.facilio.readingkpi;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

import java.util.Collections;
import java.util.List;

public class ReadingKpiAPI {
    public static List<ReadingKPIContext> getReadingKpi(Long recordId) throws Exception {
        String moduleName = FacilioConstants.ReadingKpi.READING_KPI;
        FacilioContext summary = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
        List<ReadingKPIContext> readingKpis = Constants.getRecordListFromContext(summary, moduleName);
        return readingKpis;
    }
}
