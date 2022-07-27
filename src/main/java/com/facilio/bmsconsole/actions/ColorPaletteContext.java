package com.facilio.bmsconsole.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColorPaletteContext {
    private long id = -1;
    private String keyName;
    private String colorCode;
    private long userId;
}
