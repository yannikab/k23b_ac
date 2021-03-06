package k23b.am.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * An implementation of the MessageBodyWriter Interface for the ResultContainer.
 * 
 */

@Provider
@Produces(MediaType.APPLICATION_XML)
public class ResultContainerMessageBodyWriter implements MessageBodyWriter<ResultContainer> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == ResultContainer.class;
    }

    @Override
    public long getSize(ResultContainer t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(ResultContainer t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {

        try {
            
            JAXBContext jaxbContext = JAXBContext.newInstance(ResultContainer.class);

            // serialize the entity JobList to the entity output stream
            jaxbContext.createMarshaller().marshal(t, entityStream);

        } catch (JAXBException jaxbException) {
            throw new ProcessingException("Error serializing ResultContainer to the output stream", jaxbException);
        }
    }
}
