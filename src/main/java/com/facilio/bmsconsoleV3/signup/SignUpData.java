package com.facilio.bmsconsoleV3.signup;

import java.util.*;
import com.facilio.bmsconsole.context.ApplicationContext;

public abstract class SignUpData {
    public abstract void addData() throws Exception;

    public void addViews(List<ApplicationContext> allApplications) throws Exception{}

    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        return new ArrayList<>();
    }
}
