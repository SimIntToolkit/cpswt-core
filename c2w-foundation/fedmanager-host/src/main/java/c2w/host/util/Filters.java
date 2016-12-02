package c2w.host.util;

import spark.*;

/**
 * Filters for Spark app
 */
public class Filters {

    /**
     * If a user manually manipulates paths and forgets to add
     * a trailing slash, redirect the user to the correct path
     */
    public static Filter addTrailingSlashes = (Request request, Response response) -> {
        if (!request.pathInfo().endsWith("/")) {
            response.redirect(request.pathInfo() + "/");
        }
    };
}
