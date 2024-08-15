/*
 * File:     RequestHandlers
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dromakin.server.handlers.Handler;

import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Getter(AccessLevel.PUBLIC)
public class RequestHandlers {

    private volatile ConcurrentHashMap<String, Handler> handlerGETRequests = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, Handler> handlerPOSTRequests = new ConcurrentHashMap<>();

    // TODO currently not available
    //    private volatile ConcurrentHashMap<String, Handler> handlerPUTRequests = new ConcurrentHashMap<>();
    //    private volatile ConcurrentHashMap<String, Handler> handlerDELETERequests = new ConcurrentHashMap<>();

}
