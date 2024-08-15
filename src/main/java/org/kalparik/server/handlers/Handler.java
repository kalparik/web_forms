/*
 * File:     Handler
 * Package:  org.dromakin.server.handlers
 * Project:  netology_http_web
 *
 * Created by dromakin as 28.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.28
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin.server.handlers;

import org.dromakin.server.request.Request;

import java.io.BufferedOutputStream;

public interface Handler {
    void handle(Request request, BufferedOutputStream responseStream);
}
