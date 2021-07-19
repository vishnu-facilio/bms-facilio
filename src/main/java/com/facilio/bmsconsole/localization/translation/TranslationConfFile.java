package com.facilio.bmsconsole.localization.translation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TranslationConfFile {
    private String prefix;
    private List<String> suffix;
}
