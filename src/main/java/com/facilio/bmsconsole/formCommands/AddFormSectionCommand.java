package com.facilio.bmsconsole.formCommands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.enums.Version;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AddFormSectionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(form.getModule().getName());

        form.setModule(module);

        if(CollectionUtils.isNotEmpty(form.getSections())){
            context.put(FacilioConstants.ContextNames.FORM,form);
            return false;
        }
        String moduleName = form.getModule().getName();

        List<FormSection> sections = new ArrayList<>();
        List<FormField> defaultFields = new ArrayList<>();
        List<FormField> lineItemFields = new ArrayList<>();
        List<FormField> billingAddressFields = new ArrayList<>();
        List<FormField> shippingAddressFields = new ArrayList<>();
        List<FormField> signatureFields = new ArrayList<>();
        List<FormField> budgetAmountFields = new ArrayList<>();
        List<FormField> slotAvailabilityFields = new ArrayList<>();
        List<FormField> timeSlotFields = new ArrayList<>();
        List<FormField> attendeeFields = new ArrayList<>();
        List<FormField> taskFields = new ArrayList<>();
        FormSection defaultSection = new FormSection();
        FormSection shippingSection = new FormSection();
        FormSection lineItemSection = new FormSection();
        FormSection taskSection = new FormSection();
        FormSection billingSection = new FormSection();
        FormSection notesSection = new FormSection();
        FormSection budgetAmount = new FormSection();
        FormSection availabilitySection = new FormSection();
        FormSection timeSlotSection = new FormSection();
        FormSection attendeeSection = new FormSection();

        int i = 1;

        switch (moduleName) {
            case FacilioConstants.ContextNames.WORK_ORDER:

                form.setSections(sections);

                defaultSection.setName("WORKORDER");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                if (form.getAppLinkName() == FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP) {
                    defaultSection.setShowLabel(true);
                }
                sections.add(defaultSection);
                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.TASKS) {
                        taskFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });

                if (form.getAppLinkName() != FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP && !taskFields.isEmpty()) {

                    taskSection.setName("TASKS");
                    taskSection.setSequenceNumber(i++);
                    taskSection.setFields(taskFields);
                    taskSection.setShowLabel(true);

                    sections.add(taskSection);
                }
                break;
            case FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION:

                form.setSections(sections);

                defaultSection.setName("Request For Quotation");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                shippingSection.setName("Shipping Address");
                shippingSection.setSequenceNumber(i++);
                shippingSection.setFields(shippingAddressFields);
                shippingSection.setShowLabel(true);

                lineItemSection.setName("Line Items");
                lineItemSection.setSequenceNumber(i++);
                lineItemSection.setFields(lineItemFields);
                lineItemSection.setShowLabel(true);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.LINEITEMS) {
                        lineItemFields.add(field);
                    } else if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.SADDRESS && field.getDisplayName().equals("SHIPPING ADDRESS")) {
                        shippingAddressFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });

                sections.add(defaultSection);
                sections.add(shippingSection);
                sections.add(lineItemSection);
                break;
            case (FacilioConstants.ContextNames.PURCHASE_ORDER):
            case FacilioConstants.ContextNames.PURCHASE_REQUEST:

                form.setSections(sections);

                defaultSection.setName("Purchase Order");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                billingSection.setName("Billing Address");
                billingSection.setSequenceNumber(i++);
                billingSection.setFields(billingAddressFields);
                billingSection.setShowLabel(true);

                shippingSection.setName("Shipping Address");
                shippingSection.setSequenceNumber(i++);
                shippingSection.setFields(shippingAddressFields);
                shippingSection.setShowLabel(true);

                lineItemSection.setName("Line Items");
                lineItemSection.setSequenceNumber(i++);
                lineItemSection.setFields(lineItemFields);
                lineItemSection.setShowLabel(true);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.LINEITEMS) {
                        lineItemFields.add(field);
                    } else if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.SADDRESS && field.getDisplayName().equals("BILLING ADDRESS")) {
                        billingAddressFields.add(field);
                    } else if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.SADDRESS && field.getDisplayName().equals("SHIPPING ADDRESS")) {
                        shippingAddressFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });

                sections.add(defaultSection);
                sections.add(billingSection);
                sections.add(shippingSection);
                sections.add(lineItemSection);
                break;


            case FacilioConstants.ContextNames.QUOTE:

                form.setSections(sections);

                defaultSection.setName("QUOTE INFORMATION");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                billingSection.setName("Billing Address");
                billingSection.setSequenceNumber(i++);
                billingSection.setFields(billingAddressFields);
                billingSection.setShowLabel(false);

                lineItemSection.setName("QUOTE ITEMS");
                lineItemSection.setSequenceNumber(i++);
                lineItemSection.setFields(lineItemFields);
                lineItemSection.setShowLabel(true);

                notesSection.setName("NOTES");
                notesSection.setSequenceNumber(i++);
                notesSection.setFields(signatureFields);
                notesSection.setShowLabel(false);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.QUOTE_LINE_ITEMS) {
                        lineItemFields.add(field);
                    } else if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.QUOTE_ADDRESS && field.getName().equals("billToAddress")) {
                        billingAddressFields.add(field);
                    } else if (Arrays.asList("notes").contains(field.getName())) {
                        signatureFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });

                sections.add(defaultSection);
                sections.add(billingSection);
                sections.add(lineItemSection);
                sections.add(notesSection);
                break;


            case FacilioConstants.ContextNames.INVOICE:

                form.setSections(sections);

                defaultSection.setName("INVOICE INFORMATION");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                billingSection.setName("Billing Address");
                billingSection.setSequenceNumber(i++);
                billingSection.setFields(billingAddressFields);
                billingSection.setShowLabel(false);

                billingSection.setName("Shipping Address");
                billingSection.setSequenceNumber(i++);
                billingSection.setFields(shippingAddressFields);
                billingSection.setShowLabel(false);

                lineItemSection.setName("INVOICE ITEMS");
                lineItemSection.setSequenceNumber(i++);
                lineItemSection.setFields(lineItemFields);
                lineItemSection.setShowLabel(true);

                notesSection.setName("NOTES");
                notesSection.setSequenceNumber(i++);
                notesSection.setFields(signatureFields);
                notesSection.setShowLabel(false);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.LINEITEMS) {
                        lineItemFields.add(field);
                    } else if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.SADDRESS && field.getName().equals("billToAddress")) {
                        billingAddressFields.add(field);
                    }else if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.SADDRESS && field.getName().equals("shipToAddress")) {
                        shippingAddressFields.add(field);
                    } else if (Arrays.asList("notes").contains(field.getName())) {
                        signatureFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });

                sections.add(defaultSection);
                sections.add(billingSection);
                sections.add(lineItemSection);
                sections.add(notesSection);
                break;
            case FacilioConstants.ContextNames.WorkPermit.WORKPERMIT:

                form.setSections(sections);

                defaultSection.setName("PERMIT INFORMATION");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() != FacilioField.FieldDisplayType.PERMIT_CHECKLIST) {
                        defaultFields.add(field);
                    }
                });
                sections.add(defaultSection);
                break;
            case FacilioConstants.ContextNames.Budget.BUDGET:

                form.setSections(sections);

                defaultSection.setName("BUDGET DETAILS");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                budgetAmount.setName("BUDGET AMOUNTS");
                budgetAmount.setSequenceNumber(i++);
                budgetAmount.setFields(budgetAmountFields);
                budgetAmount.setShowLabel(true);


                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.BUDGET_AMOUNT) {
                        budgetAmountFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });
                sections.add(defaultSection);
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(budgetAmountFields)) {
                    sections.add(budgetAmount);
                }
                break;
            case FacilioConstants.ContextNames.FacilityBooking.FACILITY:

                form.setSections(sections);

                defaultSection.setName("DETAILS");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(false);

                availabilitySection.setName("AVAILABILITY");
                availabilitySection.setSequenceNumber(i++);
                availabilitySection.setFields(slotAvailabilityFields);
                availabilitySection.setShowLabel(true);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.FACILITY_AVAILABILITY) {
                        slotAvailabilityFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });
                sections.add(defaultSection);
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(slotAvailabilityFields)) {
                    sections.add(availabilitySection);
                }
                break;
            case FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING:

                form.setSections(sections);

                defaultSection.setName("DETAILS");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(false);

                timeSlotSection.setName("TIME SLOTS");
                timeSlotSection.setSequenceNumber(i++);
                timeSlotSection.setFields(timeSlotFields);
                timeSlotSection.setShowLabel(false);

                attendeeSection.setName("ATTENDEES");
                attendeeSection.setSequenceNumber(i++);
                attendeeSection.setFields(attendeeFields);
                attendeeSection.setShowLabel(true);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.FACILITY_BOOKING_SLOTS) {
                        timeSlotFields.add(field);
                    } else if (Arrays.asList("internalAttendees").contains(field.getName())) {
                        attendeeFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });
                sections.add(defaultSection);
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(timeSlotFields)) {
                    sections.add(timeSlotSection);
                }
                if (CollectionUtils.isNotEmpty(attendeeFields)) {
                    sections.add(attendeeSection);
                }
                break;
            case FacilioConstants.ContextNames.INVENTORY_REQUEST:

                form.setSections(sections);

                defaultSection.setName("Inventory Request");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                lineItemSection.setName("Line Items");
                lineItemSection.setSequenceNumber(i++);
                lineItemSection.setFields(lineItemFields);
                lineItemSection.setShowLabel(true);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.INVREQUEST_LINE_ITEMS) {
                        lineItemFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });

                sections.add(defaultSection);
                sections.add(lineItemSection);
                break;
            case FacilioConstants.ContextNames.TRANSFER_REQUEST:

                form.setSections(sections);

                defaultSection.setName("Transfer Request");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                lineItemSection.setName("Line Items");
                lineItemSection.setSequenceNumber(i++);
                lineItemSection.setFields(lineItemFields);
                lineItemSection.setShowLabel(true);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.INVREQUEST_LINE_ITEMS) {
                        lineItemFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });

                sections.add(defaultSection);
                sections.add(lineItemSection);
                break;
            case FacilioConstants.ContextNames.JOB_PLAN:

                form.setSections(sections);

                defaultSection.setName("Scope");
                defaultSection.setSequenceNumber(i++);
                defaultSection.setFields(defaultFields);
                defaultSection.setShowLabel(true);

                taskSection.setName("TASKS");
                taskSection.setSequenceNumber(i++);
                taskSection.setFields(taskFields);
                taskSection.setShowLabel(true);

                form.getFields().forEach(field -> {
                    if (field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.JP_TASK) {
                        taskFields.add(field);
                    } else {
                        defaultFields.add(field);
                    }
                });

                sections.add(defaultSection);
                sections.add(taskSection);
                break;
            default:
                if (form.getSections() == null && form.getFields() != null) {
                    FormSection section = new FormSection("Default", i++, form.getFields(), false);
                    form.setSections(Collections.singletonList(section));
                }
                break;
        }
        context.put(FacilioConstants.ContextNames.FORM,form);
        return false;
    }
}

