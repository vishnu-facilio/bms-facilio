package com.facilio.bmsconsoleV3.context.autocadfileimport;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AutoCadFileImportContext  {
        private static final long serialVersionUID = 1L;
        Long orgId;
        Long Id;
        String filename;
        String comment;
        Long fileId;
        Long sourceType;
        Long importedTime;
        Long importedBy;
        Long modifiedTime;
        Long modifiedBY;
        List<AutoCadImportLayerContext> layers;
        public enum sourceType {
            AUTOCAD_PLUGIN,
            API,
            WEB;

            public int getValue() {
                return ordinal() + 1;
            }

            public static AutoCadFileImportContext.sourceType valueOf(int value) {
                if (value > 0 && value <= values().length) {
                    return values()[value - 1];
                }
                return null;
            }
        }

    }
