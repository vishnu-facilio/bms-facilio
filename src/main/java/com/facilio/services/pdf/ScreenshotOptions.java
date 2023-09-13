package com.facilio.services.pdf;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScreenshotOptions extends ExportOptions {
    private String selector;
    private boolean fullPage;
    private String format;
    private boolean omitBackground;
    private int quality;
    private int vw = 800; // viewport width
    private int vh = 768; // viewport height
}
