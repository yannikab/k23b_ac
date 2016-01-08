package k23b.am.rest;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

/**
 * 	Helps tracking down errors with the marshalling / unmarshalling of XML classes.
 */

public class ResultValidationEventHandler implements ValidationEventHandler {

    @Override
    public boolean handleEvent(ValidationEvent event) {

        if (event.getSeverity() == ValidationEvent.FATAL_ERROR || event.getSeverity() == ValidationEvent.ERROR) {
            ValidationEventLocator locator = event.getLocator();
            // Print message from valdation event
            System.out.println("Invalid Result document: " + locator.getURL());
            System.out.println("Error: " + event.getMessage());
            // Output line and column number
            System.out.println("Error at column " + locator.getColumnNumber() + ", line " + locator.getLineNumber());
        }
        return true;
    }

}
