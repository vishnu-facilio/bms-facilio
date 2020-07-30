package com.facilio.bmsconsoleV3.interfaces;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.announcement.AnnouncementContext;

import java.util.List;

public interface AnnouncementSharedPeople {
    List<V3PeopleContext> getPeople(Long id) throws Exception;
}
