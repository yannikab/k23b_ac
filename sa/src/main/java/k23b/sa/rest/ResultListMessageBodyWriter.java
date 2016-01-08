package k23b.sa.rest;

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
 * An implementation of the MessageBodyWriter Interface for the ResultList.
 * 
 */

@Provider
@Produces(MediaType.APPLICATION_XML)
public class ResultListMessageBodyWriter implements MessageBodyWriter<ResultList> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == ResultList.class;
    }

    @Override
    public long getSize(ResultList t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(ResultList t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ResultList.class);

            // serialize the entity ResultList to the entity output stream
            jaxbContext.createMarshaller().marshal(t, entityStream);
        } catch (JAXBException jaxbException) {
            throw new ProcessingException(
                    "Error serializing ResultList to the output stream", jaxbException);
        }

    }

}
