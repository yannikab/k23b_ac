package k23b.am;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import k23b.am.cc.AdminCC;
import k23b.am.cc.AgentCC;
import k23b.am.cc.JobCC;
import k23b.am.cc.RequestCC;
import k23b.am.cc.ResultCC;
import k23b.am.cc.UserCC;
import k23b.am.dao.ConnectionSingleton;
import k23b.am.rest.JobContainerMessageBodyWriter;
import k23b.am.rest.ResultListMessageBodyReader;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.SrvException;

/**
 * The application for the Aggregator Manager
 *
 */
public class App extends Application {

    private static final Logger log = Logger.getLogger(App.class);

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/am/";
    // public static final String BASE_URI = "http://192.168.1.15:8080/am/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * 
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers in k23b.am package
        final ResourceConfig rc = new ResourceConfig()
                .packages("k23b.am.rest")
                .register(JobContainerMessageBodyWriter.class)
                .register(ResultListMessageBodyReader.class);

        // create and start a new instance of grizzly http server exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * 
     * @param args arguments array
     */

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {

        PropertyConfigurator.configure("log4j.properties");

        log.info("");
        log.info("Starting Aggregator Manager.");
        log.info("Working directory: " + System.getProperty("user.dir"));

        Settings.load();

        if (Settings.getCreateSchema())
            createSchema();

        ConnectionSingleton.setDbUrl(Settings.getDbUrl());
        ConnectionSingleton.setDbUser(Settings.getDbUser());
        ConnectionSingleton.setDbPass(Settings.getDbPass());

        if (Settings.getCacheEnabled()) {
            RequestCC.initCache(Settings.getCacheSize());
            AdminCC.initCache(Settings.getCacheSize());
            AgentCC.initCache(Settings.getCacheSize());
            JobCC.initCache(Settings.getCacheSize());
            ResultCC.initCache(Settings.getCacheSize());
            UserCC.initCache(Settings.getCacheSize());
        }

        if (Settings.getCreateAdmins())
            createAdmins();

        final HttpServer server = startServer();

        System.out.println(String.format("Jersey app started with WADL available at " + "%sapplication.wadl", BASE_URI));

        launch(args);

        server.stop();
    }

    private static void createSchema() {

        try {

            Connection c = DriverManager.getConnection(Settings.getDbUrl(), Settings.getDbUser(), Settings.getDbPass());

            new ScriptRunner(c).runScript("drop.sql");
            new ScriptRunner(c).runScript("amdb.sql");

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAdmins() {

        try {

            String password = "12345";
            String username;

            username = "Yannis";
            AdminSrv.create(username, password);

            username = "Thanos";
            AdminSrv.create(username, password);

            username = "Nikos";
            AdminSrv.create(username, password);

            username = "Takis";
            AdminSrv.create(username, password);

        } catch (SrvException e) {
            e.printStackTrace();
        }
    }

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Aggregator Manager");

        initRootLayout();
    }

    private void initRootLayout() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("/fxml/RootPane.fxml"));

        try {

            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
