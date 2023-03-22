package com.facilio.multiImport.importFileWriter;

import java.util.List;
import java.util.Map;

public interface AbstractSheetWriter {
    AbstractSheetWriter setHeaderColurs(Map<Integer,Short> columnIndexVsColour) throws Exception;
    AbstractSheetWriter createHeader(Map<Integer,String> headerMap) throws Exception;
    AbstractSheetWriter setDateFormatColumnIndex(Map<Integer,String> columnIndexVsDateFormat);

    void writeData(List<Map<String, Object>> dataList) throws Exception;
}
