package org.softwire.training;

import dagger.ObjectGraph;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.JdbiException;
import org.softwire.training.api.core.ExceptionMapper;
import org.softwire.training.api.core.JsonResponseTransformer;
import org.softwire.training.api.models.ErrorCode;
import org.softwire.training.api.models.FailedRequestException;
import org.softwire.training.api.routes.v1.*;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;

import javax.inject.Inject;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static spark.Spark.*;

public class AgentDiscoveriesApplication implements Runnable {

    private ResponseTransformer responseTransformer = new JsonResponseTransformer();

    @Inject Configuration config;
    @Inject Flyway flyway;

    @Inject TokenRoutes tokenRoutes;

    @Inject AgentsRoutes agentsRoutes;
    @Inject LocationsRoutes locationsRoutes;
    @Inject RegionsRoutes regionsRoutes;
    @Inject LocationStatusReportsRoutes locationStatusReportsRoutes;
    @Inject UsersRoutes usersRoutes;

    @Override
    public void run() {
        // First run any DB migrations as necessary
        flyway.migrate();

        // Configure Java Spark to run server on port 8080
        port(config.getInt("server.port"));

        // Setup of all the routes
        path("/v1", () -> {
            // Endpoint used to get an authorisation token
            post("/token", tokenRoutes::createToken, responseTransformer);

            path("/api", () -> {
                before("/*", tokenRoutes::validateToken);

                path("/agents", this::agentsRouteGroup);
                path("/regions", this::regionsRouteGroup);
                path("/reports/locationstatuses", this::reportsRouteGroup);
                setupBasicEntityCrudRoutes("/locations", locationsRoutes);
                setupBasicEntityCrudRoutes("/users", usersRoutes);

                // API endpoint to initiate shutdown
                put("/operations/shutdown", this::shutdown);
            });

            ExceptionMapper exceptionMapper = new ExceptionMapper();
            exception(FailedRequestException.class, exceptionMapper::handleInvalidRequestException);
            exception(JdbiException.class, exceptionMapper::handleDatabaseException);

            // Ensure response has appropriate content type
            notFound("{\"errorCode\": \"1005\", \"message\": \"Not found\"}");
            after("/*", (req, res) -> {
                if (res.type() == null) {
                    // If content type not already set to be JSON
                    res.type("application/json;charset=utf-8");
                }
            });
        });

        get("/healthcheck", (req, res) -> "Server started okay!");
    }

    private void agentsRouteGroup() {
        post("", agentsRoutes::createAgent, responseTransformer);
        put("/:id", (req, res) -> agentsRoutes.updateAgent(req, res, idParamAsInt(req)), responseTransformer);
        delete("/:id", (req, res) -> agentsRoutes.deleteAgent(req, res, idParamAsInt(req)), responseTransformer);
    }

    private void regionsRouteGroup() {
        post("", regionsRoutes::createRegion, responseTransformer);
        get("/:id", (req, res) -> regionsRoutes.readRegion(req, res, idParamAsInt(req)));
        delete("/:id", (req, res) -> regionsRoutes.deleteRegion(req, res, idParamAsInt(req)), responseTransformer);
    }

    private void reportsRouteGroup() {
        post("", locationStatusReportsRoutes::createReport, responseTransformer);
        get("/:id", (req, res) -> locationStatusReportsRoutes.readReport(req, res, idParamAsInt(req)), responseTransformer);
        delete("/:id", (req, res) -> locationStatusReportsRoutes.deleteReport(req, res, idParamAsInt(req)), responseTransformer);
        get("", locationStatusReportsRoutes::searchReports, responseTransformer);
    }

    private void setupBasicEntityCrudRoutes(String path, EntityCRUDRoutes entityCRUDRoutes) {
        path(path, () -> {
            post("", entityCRUDRoutes::createEntity, responseTransformer);
            get("/:id", (req, res) -> entityCRUDRoutes.readEntity(req, res, idParamAsInt(req)), responseTransformer);
            put("/:id", (req, res) -> entityCRUDRoutes.updateEntity(req, res, idParamAsInt(req)), responseTransformer);
            delete("/:id", (req, res) -> entityCRUDRoutes.deleteEntity(req, res, idParamAsInt(req)), responseTransformer);
        });
    }

    private int idParamAsInt(Request request) throws FailedRequestException {
        try {
            return Integer.parseInt(request.params("id"));
        } catch (NumberFormatException exception) {
            throw new FailedRequestException(ErrorCode.INVALID_INPUT, "Entity ID was not an integer");
        }
    }

    private String shutdown(Request req, Response res) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        };
        timer.schedule(task, 2000);

        res.status(204);
        return "";
    }

    public static void main(String[] args) {
        // For running the application normally use just the config.properties file
        runAppWithConfiguration(getConfiguration(new File("config.properties")));
    }

    private static void runAppWithConfiguration(Configuration config) {
        // Bootstrap the Dagger object graph and create the application
        ObjectGraph objectGraph = ObjectGraph.create(new AgentDiscoveriesModule(config));
        AgentDiscoveriesApplication app = objectGraph.get(AgentDiscoveriesApplication.class);
        app.run();
    }

    private static Configuration getConfiguration(File configFile) {
        try {
            return new Configurations().properties(configFile);
        } catch (ConfigurationException exception) {
            throw new RuntimeException("Invalid configuration", exception);
        }
    }
}

