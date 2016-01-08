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
 * An implementation of the MessageBodyReader Interface for the JobList.
 * 
 */

public class JobListMessageBodyReader implements MessageBodyReader<JobList> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == JobList.class;
    }

    @Override
    public JobList readFrom(Class<JobList> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                    throws IOException, WebApplicationException {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JobList.class);
            JobList jl = (JobList) jaxbContext.createUnmarshaller().unmarshal(entityStream);
            return jl;
        } catch (JAXBException jaxbException) {
            throw new ProcessingException("Error deserializing JobList.", jaxbException);
        }

    }

}
