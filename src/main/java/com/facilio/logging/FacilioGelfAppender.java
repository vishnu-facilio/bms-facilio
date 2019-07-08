package com.facilio.logging;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.graylog2.log.GelfAppender;

import java.io.File;

public class FacilioGelfAppender extends GelfAppender {

    private static final String DEFAULT_ORG_USER_ID = "-1";
    private static final File ROOT_FILE = new File("/");
    private static long lastFreeSpaceCheckedTime = System.currentTimeMillis();
    private static final long FREE_SPACE_THRESHOLD = 10000000000L;
    private static long freeSpace = ROOT_FILE.getFreeSpace();

    public void append(LoggingEvent event) {
        if((lastFreeSpaceCheckedTime + 300_000L) < System.currentTimeMillis()) {
            lastFreeSpaceCheckedTime = System.currentTimeMillis();
            freeSpace = ROOT_FILE.getFreeSpace();
        }
        if(!(
                (   (event.getLevel().toInt() > Level.DEBUG_INT)
                        || (AccountUtil.getCurrentAccount() != null && (event.getLevel().toInt() >= AccountUtil.getCurrentAccount().getLevel().toInt()))
                )
                        && (freeSpace > FREE_SPACE_THRESHOLD)
        )) {
            return;
        }

        Organization org = AccountUtil.getCurrentOrg();
        if(org != null) {
            event.setProperty("orgId", String.valueOf(org.getOrgId()));
        } else {
            event.setProperty("orgId", DEFAULT_ORG_USER_ID);
        }
        User user = AccountUtil.getCurrentUser();
        if (user != null) {
            event.setProperty("userId", String.valueOf(user.getOuid()));
        } else {
            event.setProperty("userId", DEFAULT_ORG_USER_ID);
        }
        try {
            ThrowableInformation information = event.getThrowableInformation();
            if (information != null) {
                Throwable throwable = information.getThrowable();
                String exceptionType = throwable.getClass().getName();
                event.setProperty("exception", exceptionType);
                if( AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestUri() != null) {
                    event.setProperty("req_uri", AccountUtil.getCurrentAccount().getRequestUri());
                }
                if( AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestParams() != null) {
                    event.setProperty("req_params", AccountUtil.getCurrentAccount().getRequestParams());
                }
            } else {
                event.setProperty("exception", "-");
                event.setProperty("req_uri", "-");
                event.setProperty("req_params", "-");
            }
        } catch (Exception e) {
            event.setProperty("exception", "LogAppenderException");
        }
        super.append(event);
    }
}
