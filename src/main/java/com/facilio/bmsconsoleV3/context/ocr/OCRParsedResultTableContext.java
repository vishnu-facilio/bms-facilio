package com.facilio.bmsconsoleV3.context.ocr;

import java.io.File;

import com.facilio.bmsconsoleV3.context.ocr.OCRParsedDocResultContext;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OCRParsedResultTableContext extends V3Context {

	OCRParsedDocResultContext docResult;
	
	private File tablefile;
	private long tablefileId;
    private String tablefileUrl;
    private String tablefileFileName;
    private  String tablefileContentType;
}
