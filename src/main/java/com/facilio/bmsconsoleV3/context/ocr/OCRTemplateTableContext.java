package com.facilio.bmsconsoleV3.context.ocr;

import java.io.File;

import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OCRTemplateTableContext extends V3Context {

	OCRTemplateContext template;
	
	private File tablefile;
	private long tablefileId;
    private String tablefileUrl;
    private String tablefileFileName;
    private  String tablefileContentType;
}
