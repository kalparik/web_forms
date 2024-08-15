/*
 * File:     RequestMethod
 * Package:  org.dromakin.server.request
 * Project:  netology_http_web
 *
 * Created by dromakin as 28.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.28
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin.server.request;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromakin.exceptions.RequestMethodException;

@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
public enum RequestMethod {
    GET("GET"),
    POST("POST");

    private final String method;

    public static RequestMethod getRequestMethodByString(String method) throws RequestMethodException {
        RequestMethod requestMethod;

        switch (method) {
            case "GET":
                requestMethod = RequestMethod.GET;
                break;

            case "POST":
                requestMethod = RequestMethod.POST;
                break;

            default:
                throw new RequestMethodException("Unknown method!");
        }

        return requestMethod;
    }
}
