package k23b.sa.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * An implementation of the MessageBodyReader Interface for the JobContainer.
 * 
 */

public class JobContainerMessageBodyReader implements MessageBodyReader<JobContainer> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == JobContainer.class;
    }

    @Override
    public JobContainer readFrom(Class<JobContainer> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                    throws IOException, WebApplicationException {

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(JobContainer.class);
            JobContainer jl = (JobContainer) jaxbContext.createUnmarshaller().unmarshal(entityStream);
            return jl;

        } catch (JAXBException jaxbException) {
            throw new ProcessingException("Error deserializing JobContainer.", jaxbException);
        }
    }
}
