package com.facilio.auth.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacilioCookie {

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
                        cookie.setDomain(domain.substring(1));
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

    public static void addUserCookie(HttpServletResponse response, String key, String value, String domain) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain(domain.substring(1));
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static Cookie getCookie(String key, String value, String domain) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain(domain.substring(1));
        cookie.setHttpOnly(true);
        return cookie;
    }

    public static Cookie getCookie(String key, String value, String domain, int expiry){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain(domain.substring(1));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiry);
        return cookie;
    }
}
