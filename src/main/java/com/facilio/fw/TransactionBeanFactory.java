package com.facilio.fw;

public class TransactionBeanFactory extends BeanFactory {

    private static final boolean ENABLE_TRANSACTION = true;

    public static Object lookup(String beanName) throws InstantiationException, IllegalAccessException {
        return lookup(beanName, ENABLE_TRANSACTION);
    }

    public static Object lookup(String beanName, Long orgid) throws InstantiationException, IllegalAccessException {
        return lookup(beanName, orgid, ENABLE_TRANSACTION);
    }

    public static Object lookup(String beanName, String domainName) throws Exception {
        return lookup(beanName, domainName, ENABLE_TRANSACTION);
    }

}
