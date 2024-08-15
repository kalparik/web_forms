/*
 * File:     Code
 * Package:  org.dromakin.server.response
 * Project:  netology_http_web
 *
 * Created by dromakin as 28.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.28
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin.server.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Code {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    INVALID(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    BAD_GATEWAY(502);


    private final int code;

    public String getCode() {
        return code + " " + OK;
    }

}
