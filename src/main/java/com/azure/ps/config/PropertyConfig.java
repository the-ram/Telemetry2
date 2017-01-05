//package com.azure.ps.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.ws.rs.core.Application;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.HashSet;
//import java.util.Properties;
//import java.util.Set;
//
///**
// * Created by adithya on 1/1/17.
// */
//public class PropertyConfig extends Application {
//
//    public static final Properties properties = new Properties();
//    private static final String PROPERTIES_FILE = "application.properties";
//    private static final Logger logger = LoggerFactory.getLogger(PropertyConfig.class);
//
//    private void readProperties() {
//        InputStream propertyStream = getClass().getResourceAsStream(PROPERTIES_FILE);
//        if (propertyStream != null) {
//            try {
//                logger.info("Loading property stream");
//                properties.load(propertyStream);
//                logger.info("Number of properties loaded " + properties.size());
//            } catch (IOException e) {
//                logger.error("Unable to load any properties");
//                logger.error(e.toString());
//            }
//        }
//    }
//
//    @Override
//    public Set<Class<?>> getClasses() {
//        readProperties();
//        Set<Class<?>> classes = new HashSet<Class<?>>();
//        classes.add(PropertyConfig.class);
//        return classes;
//    }
//}
