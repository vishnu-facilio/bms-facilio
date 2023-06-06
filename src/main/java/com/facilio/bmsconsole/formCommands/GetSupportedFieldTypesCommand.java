package com.facilio.bmsconsole.formCommands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.*;

public class GetSupportedFieldTypesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        LinkedList<FacilioField> defaultFormFields = new LinkedList<>();

        FacilioField singleLineField = new FacilioField();
        singleLineField.setDisplayName("Single Line");
        singleLineField.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        singleLineField.setDataType(FieldType.STRING);
        singleLineField.setRequired(false);

        StringField multiLineField = new StringField();
        multiLineField.setDisplayName("Multi Line");
        multiLineField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        multiLineField.setDataType(FieldType.STRING);
        multiLineField.setRequired(false);

        EnumField pickListField = new EnumField();
        pickListField.setDisplayName("Pick List");
        pickListField.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        pickListField.setDataType(FieldType.ENUM);
        pickListField.setRequired(false);

        EnumFieldValue choiceOneValue = new EnumFieldValue<>();
        choiceOneValue.setIndex(1);
        choiceOneValue.setValue("Choice 1");
        EnumFieldValue choiceTwoValue = new EnumFieldValue<>();
        choiceTwoValue.setIndex(2);
        choiceTwoValue.setValue("Choice 2");
        pickListField.setValues(Arrays.asList(choiceOneValue,choiceTwoValue));


        MultiEnumField multiEnumField = new MultiEnumField();
        multiEnumField.setDisplayName("Multi Select");
        multiEnumField.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        multiEnumField.setDataType(FieldType.MULTI_ENUM);
        multiEnumField.setRequired(false);
        multiEnumField.setValues(Arrays.asList(choiceOneValue,choiceTwoValue));


        BooleanField booleanField = new BooleanField();
        booleanField.setDisplayName("Boolean");
        booleanField.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        booleanField.setDataType(FieldType.BOOLEAN);
        booleanField.setRequired(false);


        DateField dateField = new DateField();
        dateField.setDisplayName("Date");
        dateField.setDisplayType(FacilioField.FieldDisplayType.DATE);
        dateField.setDataType(FieldType.DATE);
        dateField.setRequired(false);


        FacilioField dateAndTimeField = new FacilioField();
        dateField.setDisplayName("Datetime");
        dateField.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        dateField.setDataType(FieldType.DATE_TIME);
        dateField.setRequired(false);


        FacilioField timeField = new FacilioField();
        timeField.setDisplayName("Time");
        timeField.setDisplayType(FacilioField.FieldDisplayType.TIME);
        timeField.setDataType(FieldType.NUMBER);
        timeField.setRequired(false);


        NumberField numberField = new NumberField();
        numberField.setDisplayName("Number");
        numberField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        numberField.setDataType(FieldType.NUMBER);
        numberField.setRequired(false);


        NumberField decimalField = new NumberField();
        decimalField.setDisplayName("Decimal");
        decimalField.setDisplayType(FacilioField.FieldDisplayType.DECIMAL);
        decimalField.setDataType(FieldType.DECIMAL);
        decimalField.setRequired(false);


        LookupField lookupField = new LookupField();
        lookupField.setDisplayName("Lookup");
        lookupField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        lookupField.setDataType(FieldType.LOOKUP);
        lookupField.setRequired(false);


        FacilioField durationField = new FacilioField();
        durationField.setDisplayName("Duration");
        durationField.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        durationField.setDataType(FieldType.NUMBER);
        durationField.setRequired(false);


        FileField fileField = new FileField();
        fileField.setDisplayName("File Upload");
        fileField.setDisplayType(FacilioField.FieldDisplayType.FILE);
        fileField.setDataType(FieldType.FILE);
        fileField.setRequired(false);


        FacilioField signatureField = new FacilioField();
        signatureField.setDisplayName("Signature");
        signatureField.setDisplayType(FacilioField.FieldDisplayType.SIGNATURE);
        signatureField.setDataType(FieldType.FILE);
        signatureField.setRequired(false);


        FacilioField notesField = new FacilioField();
        notesField.setDisplayName("Add Notes");
        notesField.setDisplayType(FacilioField.FieldDisplayType.NOTES);
        notesField.setRequired(false);


        UrlField urlField = new UrlField();
        urlField.setDisplayName("Url Field");
        urlField.setDisplayType(FacilioField.FieldDisplayType.URL_FIELD);
        urlField.setDataType(FieldType.URL_FIELD);
        urlField.setTarget(UrlField.UrlTarget._blank);
        urlField.setShowAlt(true);
        urlField.setRequired(false);


        FacilioField richTextField = new FacilioField();
        richTextField.setDisplayName("Rich Text");
        richTextField.setDisplayType(FacilioField.FieldDisplayType.RICH_TEXT);
        richTextField.setDataType(FieldType.LARGE_TEXT);
        richTextField.setRequired(false);


        CurrencyField currencyField = new CurrencyField();
        currencyField.setDisplayName("Currency");
        currencyField.setDisplayType(FacilioField.FieldDisplayType.CURRENCY);
        currencyField.setDataType(FieldType.CURRENCY_FIELD);
        currencyField.setRequired(false);


        StringField emailField = new StringField();
        emailField.setDisplayName("Email");
        emailField.setDisplayType(FacilioField.FieldDisplayType.EMAIL);
        emailField.setDataType(FieldType.STRING);
        emailField.setRequired(false);


        FacilioField locationField = new FacilioField();
        locationField.setDisplayName("Location");
        locationField.setDisplayType(FacilioField.FieldDisplayType.GEO_LOCATION);
        locationField.setDataType(FieldType.LOOKUP);
        locationField.setRequired(false);



        defaultFormFields.add(singleLineField);
        defaultFormFields.add(multiLineField);
        defaultFormFields.add(pickListField);
        defaultFormFields.add(multiEnumField);
        defaultFormFields.add(booleanField);
        defaultFormFields.add(dateField);
        defaultFormFields.add(dateAndTimeField);
        defaultFormFields.add(timeField);
        defaultFormFields.add(numberField);
        defaultFormFields.add(decimalField);
        defaultFormFields.add(lookupField);
        defaultFormFields.add(durationField);
        defaultFormFields.add(fileField);
        defaultFormFields.add(signatureField);
        defaultFormFields.add(notesField);
        defaultFormFields.add(urlField);
        defaultFormFields.add(richTextField);
        defaultFormFields.add(currencyField);
        defaultFormFields.add(emailField);
        defaultFormFields.add(locationField);


        context.put(FacilioConstants.ContextNames.FORM_FIELD_List,defaultFormFields);

        return false;
    }
}
