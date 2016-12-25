package com.zestedesavoir.zestwriter.model;

import java.util.regex.Pattern;

public class Constant {
    public static final String DEFAULT_INTRODUCTION_FILENAME="introduction.md";
    public static final String DEFAULT_CONCLUSION_FILENAME="conclusion.md";
    public static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    public static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    public static final String USER_AGENT = "Mozilla/5.0";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";
    public static final String CSRF_ZDS_KEY = "csrfmiddlewaretoken";
    public static final String CSRF_COOKIE_KEY = "csrftoken";
}
