/*
 * File:     Response
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
package org.dromakin.server.response;

import lombok.Builder;

@Builder
public class Response {
    public static final String END_LINE = "\r\n";
    public static final String DELIMITER = " ";
    private static final String HTTP = "HTTP/1.1";
    private static final String HTTP_CONTENT_TYPE = "Content-Type:";
    public static final String HTTP_CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    private static final String HTTP_CONTENT_LENGTH = "Content-Length:";
    private static final String HTTP_CONNECTION = "Connection:";
    private static final String HTTP_CONNECTION_CLOSE = HTTP_CONNECTION + DELIMITER + "close" + END_LINE;

    private final Code code;
    private final String contentType;
    private final Long contentLength;

    public String getResponse() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HTTP).append(DELIMITER);

        switch (code) {
            case OK:
                stringBuilder
                        .append(Code.OK.getCode()).append(END_LINE)
                        .append(HTTP_CONTENT_TYPE).append(DELIMITER).append(contentType).append(END_LINE)
                        .append(HTTP_CONTENT_LENGTH).append(DELIMITER).append(contentLength).append(END_LINE);
                break;

            case NOT_FOUND:
                stringBuilder
                        .append(Code.NOT_FOUND.getCode()).append(END_LINE)
                        .append(HTTP_CONTENT_LENGTH).append(DELIMITER).append(0).append(END_LINE);
                break;

            case INVALID:
                stringBuilder
                        .append(Code.INVALID.getCode()).append(END_LINE)
                        .append(HTTP_CONTENT_LENGTH).append(DELIMITER).append(0).append(END_LINE);
                break;

            case FORBIDDEN:
                stringBuilder
                        .append(Code.FORBIDDEN.getCode()).append(END_LINE)
                        .append(HTTP_CONTENT_LENGTH).append(DELIMITER).append(0).append(END_LINE);
                break;

            case INTERNAL_SERVER_ERROR:
                stringBuilder
                        .append(Code.INTERNAL_SERVER_ERROR.getCode()).append(END_LINE)
                        .append(HTTP_CONTENT_LENGTH).append(DELIMITER).append(0).append(END_LINE);
                break;
        }

        stringBuilder.append(HTTP_CONNECTION_CLOSE).append(END_LINE);

        return stringBuilder.toString();
    }

}
