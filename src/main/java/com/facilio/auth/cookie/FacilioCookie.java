package com.facilio.auth.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacilioCookie {
	
	public static final String CSRF_TOKEN_COOKIE = "fc.csrfToken";

    public static String getUserCookie(HttpServletRequest request, String key) {
        Cookie cookies[] = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(key)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static boolean eraseUserCookie(HttpServletRequest request, HttpServletResponse response, String key, String domain) {
        Cookie cookies[] = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(key)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    if (domain != null) {
                        cookie.setDomain(domain.startsWith(".") ? domain.substring(1) : domain);
                    }
                    cookie.setMaxAge(0);
                    cookie.setSecure(true);
                    response.addCookie(cookie);
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void addOrgDomainCookie(String domain, HttpServletResponse response) {
		Cookie cookie = new Cookie("fc.currentOrg", domain);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 10); // Make the cookie 10 year
		cookie.setPath("/");
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}
    
    public static void addCurrentSiteCookie(String site, HttpServletResponse response) {
    		Cookie sitecookie = new Cookie("fc.currentSite", site);
		sitecookie.setMaxAge(60 * 60 * 24 * 30);
		sitecookie.setPath("/");
		sitecookie.setSecure(true);
		response.addCookie(sitecookie);
    }
    
}
