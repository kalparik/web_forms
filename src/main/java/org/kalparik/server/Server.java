/*
 * File:     Server
 * Package:  org.dromakin
 * Project:  netology_http_web
 *
 * Created by dromakin as 24.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.24
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin.server;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ServerException;
import org.dromakin.server.handlers.Handler;
import org.dromakin.server.request.RequestHandlers;
import org.dromakin.server.request.RequestMapperThread;
import org.dromakin.server.request.RequestMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    public static final String STATIC_FILES_FOLDER = "static";
    public static final String STATIC_PUBLIC_FILES_FOLDER = STATIC_FILES_FOLDER + "/public";
    private static final String NAME_SERVER_PROPERTIES_FILE = "server.properties";
    private ExecutorService executorService;
    private final RequestHandlers requestHandlers;
    private final Path pathToPropertyFile;
    private boolean isSetup;

    @Getter(AccessLevel.PUBLIC)
    private Integer port;
    @Getter(AccessLevel.PUBLIC)
    private Integer maxThreadPool;

    public Server() {
        this.pathToPropertyFile = getResourceFile();
        this.requestHandlers = new RequestHandlers();
        this.isSetup = false;
    }

    private Path getResourceFile() {
        URL url = Server.class.getClassLoader().getResource(NAME_SERVER_PROPERTIES_FILE);

        if (url == null) {
            throw new IllegalArgumentException(NAME_SERVER_PROPERTIES_FILE + " is not found!");
        }

        return Paths.get(url.getFile());
    }

    private void loadSettings() throws ServerException {
        try (InputStream input = Files.newInputStream(this.pathToPropertyFile)) {
            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            this.port = Integer.parseInt(prop.getProperty("server.port"));
            this.maxThreadPool = Integer.parseInt(prop.getProperty("server.thread.pool.max"));

        } catch (IOException e) {
            String errMsg = "Can't load settings from properties file!";
            logger.error(errMsg);
            throw new ServerException(errMsg, e);
        }
    }

    public void setupServer() throws ServerException {
        logger.info("Setting up the server...");
        loadSettings();
        this.executorService = Executors.newFixedThreadPool(this.maxThreadPool);
        this.isSetup = true;
    }

    public void start() throws ServerException {
        if (!this.isSetup) {
            String msgErr = "Server is not setup!";
            logger.error(msgErr);
            throw new ServerException(msgErr);
        }

        logger.info("Start server on port: " + this.port);
        try (final var serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final var socket = serverSocket.accept();
                    processConnection(socket);
                } catch (IOException e) {
                    String msgErr = "Error while process connection in thread!";
                    logger.error(msgErr);
                    throw new ServerException(msgErr);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    public void addHandler(RequestMethod method, String page, Handler handler) throws ServerException {
        switch (method) {
            case GET:
                this.requestHandlers.getHandlerGETRequests().put(page, handler);
                break;

            case POST:
                this.requestHandlers.getHandlerPOSTRequests().put(page, handler);
                break;

            default:
                String msgErr = "Unknown method!";
                logger.error(msgErr);
                throw new ServerException(msgErr);
        }
    }

    private void processConnection(Socket socket) {
        RequestMapperThread requestMapperThread = new RequestMapperThread(socket, this.requestHandlers);
        executorService.submit(requestMapperThread);
    }

    public void stop() {
        logger.info("Stop server...");
        this.executorService.shutdown();
    }


    public static Path getPathToStaticFiles() {
        URL url = Server.class.getClassLoader().getResource(NAME_SERVER_PROPERTIES_FILE);

        if (url == null) {
            throw new IllegalArgumentException("Folder " + STATIC_PUBLIC_FILES_FOLDER + " is not found!");
        }

        Path pathToResourceFolder = Paths.get(url.getFile()).getParent();

        return Paths.get(pathToResourceFolder.toString(), STATIC_PUBLIC_FILES_FOLDER);
    }

}
