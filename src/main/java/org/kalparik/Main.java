package org.dromakin;

import org.apache.http.NameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ServerException;
import org.dromakin.server.Server;
import org.dromakin.server.request.RequestMethod;
import org.dromakin.server.response.Code;
import org.dromakin.server.response.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.dromakin.server.response.Response.HTTP_CONTENT_TYPE_TEXT_PLAIN;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js", "/messages");

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.setupServer();

            for (String s : validPaths) {
                server.addHandler(RequestMethod.GET, s, (request, out) -> {
                    try {

                        if (request.getUrlPath().equals("/messages")) {

                            logger.debug("Request: {} accepted!", request.getUrlPath());

                            StringBuilder responseMsg = new StringBuilder();
                            for (NameValuePair param : request.getPostParams()) {
                                logger.debug("Param: {}, value: {}", param.getName(), request.getPostParams(param.getName()));
                                responseMsg.append("Param: ").append(param.getName())
                                        .append(", value: ").append(request.getPostParams(param.getName()))
                                        .append(Response.END_LINE);
                            }

                            out.write((
                                    (
                                            Response.builder()
                                                    .code(Code.OK)
                                                    .contentType(HTTP_CONTENT_TYPE_TEXT_PLAIN)
                                                    .contentLength((long) responseMsg.length())
                                                    .build()
                                    )
                                            .getResponse()
                            ).getBytes());

                            out.write(responseMsg.toString().getBytes());

                            logger.info("Request: {} - {}", request.getUrlPath(), Code.OK);

                        } else {

                            final var filePath = Paths.get(Server.getPathToStaticFiles().toString(), request.getUrlPath());
                            final var contentType = Files.probeContentType(filePath);

                            if (request.getUrlPath().equals("/classic.html")) {

                                logger.debug("Request: {} accepted!", request.getUrlPath());

                                final var template = Files.readString(filePath);
                                final var content = template.replace("{time}", LocalDateTime.now().toString()).getBytes();
                                out.write((
                                        (
                                                Response.builder()
                                                        .code(Code.OK)
                                                        .contentType(contentType)
                                                        .contentLength((long) content.length)
                                                        .build()
                                        )
                                                .getResponse()
                                ).getBytes());

                                out.write(content);

                                logger.info("Request: {} - {}", request.getUrlPath(), Code.OK);

                            } else {

                                logger.debug("Request: {} accepted!", request.getUrlPath());

                                final var contentLength = Files.size(filePath);
                                out.write((
                                        (
                                                Response.builder()
                                                        .code(Code.OK)
                                                        .contentType(contentType)
                                                        .contentLength(contentLength)
                                                        .build()
                                        )
                                                .getResponse()
                                ).getBytes());
                                Files.copy(filePath, out);

                                logger.info("Request: {} - {}", request.getUrlPath(), Code.OK);

                            }

                            for (NameValuePair param : request.getPostParams()) {
                                String msg = String.format("Param: %s, value: %s\n", param.getName(), request.getPostParams(param.getName()));
                                logger.debug("Query: {}", msg);
                            }

                        }

                        out.flush();

                    } catch (IOException e) {
                        logger.error("Request: {} - {}", request.getUrlPath(), Code.INTERNAL_SERVER_ERROR);
                        if (e.getCause() != null)
                            e.getCause().printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
            }

            server.start();

        } catch (ServerException e) {
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            throw new RuntimeException(e);
        }
    }
}


