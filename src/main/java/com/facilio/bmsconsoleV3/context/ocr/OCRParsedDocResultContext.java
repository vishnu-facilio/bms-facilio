package com.facilio.bmsconsoleV3.context.ocr;

import java.io.File;
import java.util.List;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OCRParsedDocResultContext extends V3Context  {

	OCRTemplateContext ocrTemplate;
	
	private File document;
    private File parsedForm;
    private File parsedRawText;
    
    long sourceDocId;
    long sourceModuleId;
    
    private long documentId;
    private String documentUrl;
    private String documentFileName;
    private  String documentContentType;
    
    private long parsedFormId;
    private String parsedFormUrl;
    private String parsedFormFileName;
    private  String parsedFormContentType;
    
    private long parsedRawTextId;
    private String parsedRawTextUrl;
    private String parsedRawTextFileName;
    private  String parsedRawTextContentType;
    
    List<OCRParsedResultTableContext> tables;
}
