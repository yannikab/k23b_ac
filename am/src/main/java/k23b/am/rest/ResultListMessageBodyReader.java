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

public class ResultListMessageBodyReader implements MessageBodyReader<ResultList> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		
		return type == ResultList.class;
	}

	@Override
	public ResultList readFrom(Class<ResultList> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ResultList.class);
			ResultList resList = (ResultList) jaxbContext.createUnmarshaller().unmarshal(entityStream);
			return resList;
		} catch (JAXBException jaxbException) {
			throw new ProcessingException("Error deserializing ResultList.", jaxbException);
		}
	}

}
