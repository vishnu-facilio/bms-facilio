package com.facilio.interceptors;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts.beanvalidation.constraints.ValidationGroup;
import org.apache.struts.beanvalidation.validation.constant.ValidatorConstants;
import org.apache.struts.beanvalidation.validation.interceptor.BeanValidationManager;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FacilioBeanValidationInterceptor extends MethodFilterInterceptor {

    private static final Logger LOG = LogManager.getLogger(FacilioBeanValidationInterceptor.class);

    protected BeanValidationManager beanValidationManager;
    protected TextProviderFactory textProviderFactory;
    protected boolean convertToUtf8 = false;
    protected String convertFromEncoding = "ISO-8859-1";
    private Set<ConstraintViolation<Object>> constraintViolations = new HashSet<>();

    @Inject()
    public void setBeanValidationManager(BeanValidationManager beanValidationManager) {
        this.beanValidationManager = beanValidationManager;
    }

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    @Inject(value = ValidatorConstants.CONVERT_MESSAGE_TO_UTF8, required = false)
    public void setConvertToUtf8(String convertToUtf8) {
        this.convertToUtf8 = BooleanUtils.toBoolean(convertToUtf8);
    }

    @Inject(value = ValidatorConstants.CONVERT_MESSAGE_FROM, required = false)
    public void setConvertFromEncoding(String convertFromEncoding) {
        this.convertFromEncoding = convertFromEncoding;
    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Validator validator = this.beanValidationManager.getValidator();
        constraintViolations.clear();
        if (validator == null) {
            LOG.debug("There is no Bean Validator configured in class path. Skipping Bean validation..");
            return invocation.invoke();
        }
        LOG.debug("Starting bean validation using validator: "+ validator.getClass());

        Object action = invocation.getAction();
        ActionProxy actionProxy = invocation.getProxy();
        String methodName = actionProxy.getMethod();

        LOG.debug("Validating "+ invocation.getProxy().getNamespace() +"/"+ invocation.getProxy().getActionName()+" with method: " + methodName);

        if (null == MethodUtils.getAnnotation(getActionMethod(action.getClass(), methodName), SkipValidation.class,
                true, true)) {
            Class<?>[] validationGroup = getValidationGroups(action, methodName);
            // performing bean validation on action
            performBeanValidation(action, validator, validationGroup);
        }

        if(constraintViolations.isEmpty()) {
            return invocation.invoke();
        } else {
            return Action.ERROR;
        }
    }

    protected Class<?>[] getValidationGroups(Object action, String methodName) throws NoSuchMethodException {
        ValidationGroup validationGroup = MethodUtils.getAnnotation(getActionMethod(action.getClass(), methodName), ValidationGroup.class, true, true);
        return validationGroup == null ? new Class[]{Default.class} : validationGroup.value();
    }

    protected void performBeanValidation(Object action, Validator validator, Class<?>[] groups) {
        LOG.trace("Initiating bean validation.. with groups "+ Arrays.toString(groups));
        if (action instanceof ModelDriven) {
            LOG.trace("Performing validation on model..");
            Object model = (Object)((ModelDriven<?>) action).getModel();
            constraintViolations = validator.validate(model, groups);
        } else {
            LOG.trace("Performing validation on action..");
            constraintViolations = validator.validate(action, groups);
        }
        addBeanValidationErrors(constraintViolations, action);
    }

    @SuppressWarnings("nls")
    protected void addBeanValidationErrors(Set<ConstraintViolation<Object>> constraintViolations, Object action) {
        if (constraintViolations != null) {
            ValidatorContext validatorContext = new DelegatingValidatorContext(action, textProviderFactory);
            for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
                String key = constraintViolation.getMessage();
                String message = key;
                try {
                    message = validatorContext.getText(key);
                    if (convertToUtf8 && StringUtils.isNotBlank(message)) {
                        message = new String(message.getBytes(convertFromEncoding), "UTF-8");
                    }
                } catch (Exception e) {
                    LOG.error("Error while trying to fetch message: "+ key, e);
                }

                if (isActionError(constraintViolation)) {
                    LOG.debug("Adding action error : "+ message);
                    validatorContext.addActionError(message);
                } else {
                    FacilioBeanValidationInterceptor.ValidationError validationError = buildBeanValidationError(constraintViolation, message);
                    String fieldName = validationError.getFieldName();
                    if (action instanceof ModelDriven && fieldName.startsWith(ValidatorConstants.MODELDRIVEN_PREFIX)) {
                        fieldName = fieldName.replace("model.", ValidatorConstants.EMPTY_SPACE);
                    }
                    LOG.debug("Adding field error: "+ fieldName + ", with message : "+ validationError.getMessage());
                    validatorContext.addFieldError(fieldName, validationError.getMessage());
                }
            }
        }
    }

    protected FacilioBeanValidationInterceptor.ValidationError buildBeanValidationError(ConstraintViolation<Object> violation, String message) {
        if (violation.getPropertyPath().iterator().next().getName() != null) {
            String fieldName = violation.getPropertyPath().toString();
            String finalMessage = StringUtils.removeStart(message, fieldName + ValidatorConstants.FIELD_SEPERATOR);
            return new FacilioBeanValidationInterceptor.ValidationError(fieldName, finalMessage);
        }
        return null;
    }

    /**
     * Decide if a violation should be added to the fieldErrors or actionErrors
     *
     * @param violation the violation
     *
     * @return true if violation should be added to the fieldErrors or actionErrors
     */
    protected boolean isActionError(ConstraintViolation<Object> violation) {
        return violation.getLeafBean() == violation.getInvalidValue();
    }

    /**
     * This is copied from DefaultActionInvocation
     *
     * @param actionClass the action class
     * @param methodName the method name
     *
     * @return Method
     *
     * @throws NoSuchMethodException if no method with this name was found
     */
    protected Method getActionMethod(Class<?> actionClass, String methodName) throws NoSuchMethodException {
        Method method;

        method = actionClass.getMethod(methodName);

        return method;
    }

    /**
     * Inner class for validation error
     * Nice concept taken from  Oval plugin.
     */

    class ValidationError {
        private final String fieldName;
        private final String message;

        ValidationError(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public String getMessage() {
            return this.message;
        }
    }

}
