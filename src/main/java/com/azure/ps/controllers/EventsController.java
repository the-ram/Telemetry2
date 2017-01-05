package com.azure.ps.controllers;

import com.azure.ps.ext.BatchSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.psamsotha.jersey.properties.Prop;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by adithya on 31/12/16.
 */
@Path("/events")
public class EventsController {

    private static final Logger logger = LoggerFactory.getLogger(EventsController.class);

    @Prop("config.post.to.eventhub")
    private String postToEventHub;

    private EventHubClient eventHubClient = null;

    @Inject
    public EventsController(EventHubClient eventHubClient) {
        this.eventHubClient = eventHubClient;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postEvents(@Context HttpHeaders httpHeaders, String jsonEvent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> eventsMap = mapper.readValue(jsonEvent, Map.class);
            ArrayList<Map<String, Object>> events = (ArrayList<Map<String, Object>>) eventsMap.get("events");
            String deviceId = httpHeaders.getHeaderString("x-device-id");

            if (events != null && events.size() > 0) {
                logger.info("DEVICE-TRACE : " + deviceId + " :: " + events.size());

                if (Boolean.parseBoolean(postToEventHub)) {
                    try {
                        BatchSender sender = BatchSender.create(this.eventHubClient);
                        for (Map<String, Object> eventMap : events) {
                            String json = new ObjectMapper().writeValueAsString(eventMap);
                            logger.debug("Now parsing message : " + json);
                            EventData data = new EventData(json.getBytes());
                            String eventName = eventMap.get("eventName").toString();
                            logger.debug("Step - 4 Event Name " + eventName);

                            data.getProperties().put("receivedAt",
                                    String.valueOf(Instant.now().getEpochSecond()));

                            data.getProperties().put("eventName", eventName);
                            sender.send(data, deviceId);
                        }
                    } catch (Exception ex) {
                        logger.error("Uncaught Exception connecting to event hub " + ex.toString());
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                                entity(ex.getMessage()).
                                build();
                    }
                    return Response.status(Response.Status.CREATED).
                            header("x-api-version", "1.0").
                            header("x-api-build", "1.0").
                            build();
                } else {
                    return Response.status(Response.Status.OK).
                            header("x-api-version", "1.0").
                            header("x-api-build", "1.0").
                            build();
                }
            } else {
                logger.error("NOT-WELL-FORMED", "Not well formed request");
                return Response.status(Response.Status.BAD_REQUEST).entity("No events to process").build();
            }
        } catch (IOException exception) {
            logger.error("UNHANDLED EXCEPTION : " + exception.toString());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
