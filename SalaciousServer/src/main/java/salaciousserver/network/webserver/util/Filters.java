package salaciousserver.network.webserver.util;

import io.javalin.http.Handler;

import static salaciousserver.network.webserver.util.RequestUtil.*;

public class Filters {

    // Locale change can be initiated from any page
    // The locale is extracted from the request and saved to the user's session
    public static Handler handleLocaleChange = ctx -> {
        if (getQueryLocale(ctx) != null) {
            ctx.sessionAttribute("locale", getQueryLocale(ctx));
            ctx.redirect(ctx.path());
        }
    };
    // If a user manually manipulates paths and forgets to add
    // a trailing slash, redirect the user to the correct path
    public static Handler stripTrailingSlashes = ctx -> {
        String request = ctx.req.getPathInfo();
        if (!request.equals("/") && request.endsWith("/")) {
            ctx.redirect(request.substring(0, request.length() - 1));
        }
    };

}
