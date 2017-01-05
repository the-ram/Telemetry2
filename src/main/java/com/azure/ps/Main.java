package com.azure.ps;

/**
 * Main class.
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/telemetry/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
//    public static HttpServer startServer() {
//        // create a resource config that scans for JAX-RS resources and providers
//        // in com.azure.ps package
//        final ResourceConfig rc = new ResourceConfig().packages("com.azure.ps.controllers", "com.azure.ps.interceptors");
//        //rc.getP.add(CorsSupportFilter.class);
//        rc.register(JerseyPropertiesFeature.class);
//        rc.property(JerseyPropertiesFeature.RESOURCE_PATH, "application.properties");
//
//        //logger.info("Now running the registration process in resource config");
//
//        rc.register(new AbstractBinder() {
//            @Override
//            public void configure() {
//                bindFactory(EventHubClientFactory.class).to(EventHubClient.class)
//                        .in(RequestScoped.class);
//            }
//        });
//
//        // create and start a new instance of grizzly http server
//        // exposing the Jersey application at BASE_URI
//        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
//    }
//
//    /**
//     * Main method.
//     *
//     * @param args
//     * @throws IOException
//     */
//    public static void main(String[] args) throws IOException {
//        final HttpServer server = startServer();
//        System.out.println(String.format("Jersey app started with WADL available at "
//                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
//        System.in.read();
//        server.stop();
//    }
}

