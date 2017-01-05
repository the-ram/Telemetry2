package com.azure.ps.interceptors;

import jersey.repackaged.com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by adithya on 31/12/16.
 */
@Provider
public class GzipReaderInterceptor implements ReaderInterceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {

        MultivaluedMap<String, String> headers = context.getHeaders();

        List<String> contentEncoding = MoreObjects.firstNonNull(
                headers.get(HttpHeaders.CONTENT_ENCODING), new ArrayList<String>());

        for (String s : contentEncoding) {
            if (s.contains("gzip")) {
                logger.info("Decompressing GZIP");
                final InputStream originalInputStream = context.getInputStream();
                context.setInputStream(new GZIPInputStream(originalInputStream));
                break;
            }
        }
        logger.info("Decompressing GZIP completed");
        return context.proceed();
    }
}
