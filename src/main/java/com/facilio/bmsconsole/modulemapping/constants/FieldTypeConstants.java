package com.facilio.bmsconsole.modulemapping.constants;

import com.facilio.modules.FieldType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FieldTypeConstants {

    public static final Set<FieldType> LOOKUP_TYPES = new HashSet<>(Arrays.asList(
            FieldType.LOOKUP, FieldType.MULTI_LOOKUP
    ));

    public static final Set<FieldType> DATE_FIELD_TYPES = new HashSet<>(Arrays.asList(
            FieldType.DATE, FieldType.DATE_TIME
    ));
    public static final Set<FieldType> STRING_FIELD_TYPES = new HashSet<>(Arrays.asList(
            FieldType.STRING, FieldType.BIG_STRING, FieldType.LARGE_TEXT
    ));

    public static final Set<FieldType> SINGLE_ENUM_FIELD_TYPES = new HashSet<>(Arrays.asList(
            FieldType.ENUM, FieldType.SYSTEM_ENUM, FieldType.STRING_SYSTEM_ENUM
    ));

    public static final Set<FieldType> ENUM_FIELD_TYPES = new HashSet<>(Arrays.asList(
            FieldType.MULTI_ENUM
    ));
    public static final Set<FieldType> NUMERIC_FIELD_TYPES = new HashSet<>(Arrays.asList(
            FieldType.DECIMAL, FieldType.NUMBER
    ));
    public static final Set<FieldType> VALID_DECIMAL_FIELD_TYPES = new HashSet<>();
    ;
    public static final Set<FieldType> VALID_NUMBER_FIELD_TYPES = new HashSet<>();
    ;
    public static final Set<FieldType> VALID_BOOLEAN_FIELD_TYPES = new HashSet<>();
    ;
    public static final Set<FieldType> VALID_DATE_FIELD_TYPES = new HashSet<>();
    ;
    public static final Set<FieldType> VALID_LOOKUP_FIELD_TYPES = new HashSet<>();
    public static final Set<FieldType> VALID_ENUM_FIELD_TYPES = new HashSet<>();
    public static final Set<FieldType> VALID_ID_FIELD_TYPES = new HashSet<>();
    public static final Set<FieldType> VALID_MULTI_ENUM_FIELD_TYPES = new HashSet<>();
    public static final Set<FieldType> VALID_STRING_SYSTEM_ENUM_FIELD_TYPES = new HashSet<>();
    public static final Set<FieldType> VALID_CURRENCY_FIELD_TYPES = new HashSet<>();
    public static final Set<FieldType> VALID_MULTI_CURRENCY_FIELD_TYPES = new HashSet<>();
    public static final Set<FieldType> VALID_MULTI_LOOKUP_FIELD_TYPES = new HashSet<>();
    public static final Set<FieldType> VALID_URL_FIELD_TYPE = new HashSet<>();
    ;

    static {

        /* Enum Field Types */

        ENUM_FIELD_TYPES.addAll(SINGLE_ENUM_FIELD_TYPES);

        /* Valid Decimal Field Types */

        VALID_DECIMAL_FIELD_TYPES.addAll(NUMERIC_FIELD_TYPES);
        VALID_DECIMAL_FIELD_TYPES.addAll(STRING_FIELD_TYPES);

        /* Valid Number Field Types */

        VALID_NUMBER_FIELD_TYPES.addAll(NUMERIC_FIELD_TYPES);
        VALID_NUMBER_FIELD_TYPES.addAll(STRING_FIELD_TYPES);
        VALID_NUMBER_FIELD_TYPES.addAll(LOOKUP_TYPES);

        /* Valid Boolean Field Types */

        VALID_BOOLEAN_FIELD_TYPES.add(FieldType.BOOLEAN);
        VALID_BOOLEAN_FIELD_TYPES.addAll(ENUM_FIELD_TYPES);
        VALID_BOOLEAN_FIELD_TYPES.addAll(STRING_FIELD_TYPES);
        VALID_BOOLEAN_FIELD_TYPES.addAll(LOOKUP_TYPES);

        /* Valid Date and Date Time Field Types */

        VALID_DATE_FIELD_TYPES.addAll(DATE_FIELD_TYPES);
        VALID_DATE_FIELD_TYPES.addAll(NUMERIC_FIELD_TYPES);
        VALID_DATE_FIELD_TYPES.addAll(STRING_FIELD_TYPES);

        /* Valid Lookup Field Types */

        VALID_LOOKUP_FIELD_TYPES.addAll(STRING_FIELD_TYPES);
        VALID_LOOKUP_FIELD_TYPES.addAll(LOOKUP_TYPES);
        VALID_LOOKUP_FIELD_TYPES.addAll(ENUM_FIELD_TYPES);

        /* Valid Multi Lookup Field Types */

        VALID_MULTI_LOOKUP_FIELD_TYPES.addAll(STRING_FIELD_TYPES);
        VALID_MULTI_LOOKUP_FIELD_TYPES.add(FieldType.MULTI_LOOKUP);
        VALID_MULTI_LOOKUP_FIELD_TYPES.add(FieldType.MULTI_ENUM);

        /* Valid Enum Field Types */

        VALID_ENUM_FIELD_TYPES.addAll(ENUM_FIELD_TYPES);
        VALID_ENUM_FIELD_TYPES.addAll(LOOKUP_TYPES);
        VALID_ENUM_FIELD_TYPES.addAll(STRING_FIELD_TYPES);
        VALID_ENUM_FIELD_TYPES.add(FieldType.STRING_SYSTEM_ENUM);

        /* Valid ID Field Types */

        VALID_ID_FIELD_TYPES.addAll(NUMERIC_FIELD_TYPES);
        VALID_ID_FIELD_TYPES.addAll(STRING_FIELD_TYPES);
        VALID_ID_FIELD_TYPES.add(FieldType.LOOKUP);

        /* Valid Multi Enum Field Types */

        VALID_MULTI_ENUM_FIELD_TYPES.addAll(STRING_FIELD_TYPES);
        VALID_MULTI_ENUM_FIELD_TYPES.add(FieldType.MULTI_LOOKUP);
        VALID_MULTI_ENUM_FIELD_TYPES.add(FieldType.MULTI_ENUM);

        /* Valid String System Enum Field Types */

        VALID_STRING_SYSTEM_ENUM_FIELD_TYPES.add(FieldType.STRING_SYSTEM_ENUM);
        VALID_STRING_SYSTEM_ENUM_FIELD_TYPES.addAll(STRING_FIELD_TYPES);

        /* Valid Currency Field Types */

        VALID_CURRENCY_FIELD_TYPES.add(FieldType.CURRENCY_FIELD);
        VALID_CURRENCY_FIELD_TYPES.add(FieldType.MULTI_CURRENCY_FIELD);
        VALID_CURRENCY_FIELD_TYPES.addAll(STRING_FIELD_TYPES);

        /* Valid Multi Currency Field Types */

        VALID_MULTI_CURRENCY_FIELD_TYPES.add(FieldType.MULTI_CURRENCY_FIELD);
        VALID_MULTI_CURRENCY_FIELD_TYPES.addAll(STRING_FIELD_TYPES);

        /* Valid URL Field Type */

        VALID_URL_FIELD_TYPE.add(FieldType.URL_FIELD);
        VALID_URL_FIELD_TYPE.addAll(STRING_FIELD_TYPES);


    }

}
