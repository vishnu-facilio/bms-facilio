package com.facilio.services.pdf;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PDFOptions extends ExportOptions {
    private int width;
    private int height;
    private boolean landscape;
    private boolean screen;
    private int scale;
    private String pageRanges;
    private String format;
    private String headerTemplate;
    private String footerTemplate;
    private boolean defaultFooter;
    private boolean omitBackground;
    private boolean printBackground;
    private Map<String,String> margin;
}
