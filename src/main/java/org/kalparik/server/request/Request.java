/*
 * File:     Request
 * Package:  org.dromakin.server
 * Project:  netology_http_web
 *
 * Created by dromakin as 27.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.27
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin.server.request;


import lombok.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
public class Request {
    private final RequestMethod requestMethod;
    private final String url;
    private final String protocol;
    private final List<String> body = new ArrayList<>();
    private final Map<String, String> headers = new HashMap<>();
    private URI uri;
    private List<NameValuePair> queryParams;

    public void initQueries() {
        this.uri = URI.create(this.url);
        this.queryParams = URLEncodedUtils.parse(this.uri, StandardCharsets.UTF_8);
    }

    public String getUrlPath() {
        return uri.getPath();
    }

    public String getPostParams(String name) {
        for (NameValuePair param : queryParams) {
            if (param.getName().equals(name)) {
                return param.getValue();
            }
        }
        return null;
    }

    public List<NameValuePair> getPostParams() {
        return queryParams;
    }

    public void addHeader(String header, String value) {
        if (!this.headers.containsKey(header)) {
            this.headers.put(header, value);
        }
    }

    public void addBody(List<String> msgs) {
        this.body.addAll(msgs);
    }

    public void addBody(String msg) {
        this.body.add(msg);
    }

}


