/*
 * File:     RequestMapper
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.RequestMethodException;
import org.dromakin.server.handlers.Handler;
import org.dromakin.server.response.Code;
import org.dromakin.server.response.Response;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RequestMapperThread implements Runnable {
    private static final Logger logger = LogManager.getLogger(RequestMapperThread.class);
    private BufferedReader in;
    private BufferedOutputStream out;
    private final Socket socket;
    private final RequestHandlers requestHandlers;

    public RequestMapperThread(Socket socket, RequestHandlers requestHandlers) {
        this.socket = socket;
        this.requestHandlers = requestHandlers;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try (socket) {
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                // just close socket
                return;
            }

            Request request = Request.builder()
                    .requestMethod(RequestMethod.getRequestMethodByString(parts[0]))
                    .url(parts[1])
                    .protocol(parts[2])
                    .build();

            request.initQueries();

            // reading HTTP Headers
            while (in.ready()) {
                String s = in.readLine();
                if (s.length() == 0) {
                    break;
                }

                String key = s.substring(0, s.indexOf(':'));
                String value = s.substring(s.indexOf(':') + 1).trim();

                request.getHeaders().put(key, value);
            }

            // reading request body
            while (in.ready()) {
                String s = in.readLine();
                request.addBody(s);
            }

            processRequest(request);
        } catch (IOException | RequestMethodException e) {
            logger.error(e.getMessage(), e);
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void processRequest(Request request) {
        Handler handler;

        switch (request.getRequestMethod()) {
            case GET:
                handler = this.requestHandlers.getHandlerGETRequests().get(request.getUrlPath());

                if (handler != null) {

                    if (request.getPostParams().isEmpty()) {
                        logger.info("Processing [GET] request: " + request.getUrlPath());
                    } else {
                        logger.info("Processing [GET] request: {} with params!", request.getUrlPath());
                    }

                    handler.handle(request, out);

                } else {
                    sendErrorResponse(Code.NOT_FOUND);
                }

                break;

            case POST:
                handler = this.requestHandlers.getHandlerPOSTRequests().get(request.getUrlPath());

                if (handler != null) {

                    if (request.getPostParams().isEmpty()) {
                        logger.info("Processing [POST] request: " + request.getUrlPath());
                    } else {
                        logger.info("Processing [POST] request: {} with params!", request.getUrlPath());
                    }

                    handler.handle(request, out);

                } else {
                    sendErrorResponse(Code.NOT_FOUND);
                }

                break;

            default:
                sendErrorResponse(Code.INVALID);
                break;
        }
    }


    private void sendErrorResponse(Code code) {
        try {
            switch (code) {
                case INVALID:
                case FORBIDDEN:
                case NOT_FOUND:
                case INTERNAL_SERVER_ERROR:
                    out.write(
                            (
                                    Response.builder()
                                            .code(code)
                                            .build()
                            )
                                    .getResponse()
                                    .getBytes()
                    );
                    out.flush();
                    break;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
