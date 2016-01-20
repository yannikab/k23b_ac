package k23b.am.rest;

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
 * An implementation of the MessageBodyReader Interface for the ResultList.
 * 
 */

public class ResultContainerMessageBodyReader implements MessageBodyReader<ResultContainer> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		
		return type == ResultContainer.class;
	}

	@Override
	public ResultContainer readFrom(Class<ResultContainer> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ResultContainer.class);
			ResultContainer resList = (ResultContainer) jaxbContext.createUnmarshaller().unmarshal(entityStream);
			return resList;
		} catch (JAXBException jaxbException) {
			throw new ProcessingException("Error deserializing ResultList.", jaxbException);
		}
	}

}
