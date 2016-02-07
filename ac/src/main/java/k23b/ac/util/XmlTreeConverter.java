package k23b.ac.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import k23b.ac.services.Logger;

/**
 * Used to convert the XML form of a Result into HTML
 */
public class XmlTreeConverter {

    private DocumentBuilder db;

    public XmlTreeConverter() {

        try {

            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Logger.info(this.toString(), "DocumentBuilder created.");

        } catch (ParserConfigurationException e) {
            // e.printStackTrace();
            Logger.error(this.toString(), e.getMessage());

            db = null;
        }
    }

    private Document parseXML(String xml) {

        if (xml == null)
            return null;

        if (db == null)
            return null;

        try {

            StringReader sr = new StringReader(xml);

            InputSource is = new InputSource(sr);

            return db.parse(is);

        } catch (SAXException e) {
            // e.printStackTrace();
            Logger.error(this.toString(), e.getMessage());
        } catch (IOException e) {
            // e.printStackTrace();
            Logger.error(this.toString(), e.getMessage());
        }

        return null;
    }

    private Document documentForXml(String xml) {

        Document in = parseXML(xml);

        if (in == null)
            return null;

        Document out = db.newDocument();

        Element outRoot = out.createElement("ul");
        outRoot.setAttribute("class", "mktree");
        outRoot.setAttribute("id", "tree");
        out.appendChild(outRoot);

        Element li = listItemForElement(out, in.getDocumentElement());
        li.setAttribute("class", "liOpen");
        // li.setAttribute("style", "margin-left: 0px;");
        outRoot.appendChild(li);

        return out;
    }

    private Element listItemForElement(Document doc, Element element) {

        Element listItem = doc.createElement("li");
        listItem.setTextContent(element.getTagName());

        Element unorderedList = doc.createElement("ul");

        NamedNodeMap attributes = element.getAttributes();

        int attrs = attributes.getLength();

        NodeList childNodes = element.getChildNodes();

        int nodes = childNodes.getLength();

        if (attrs > 0 || nodes > 0)
            listItem.appendChild(unorderedList);

        for (int i = 0; i < attrs; i++) {

            Node node = attributes.item(i);

            Element li = doc.createElement("li");
            li.setTextContent(node.getNodeName() + " = " + node.getNodeValue());
            unorderedList.appendChild(li);
        }

        for (int i = 0; i < nodes; i++) {

            Node node = childNodes.item(i);

            if (!(node instanceof Element))
                continue;

            Element subListItem = listItemForElement(doc, (Element) node);
            unorderedList.appendChild(subListItem);
        }

        return listItem;
    }

    private void printDocument(Document doc, OutputStream out) {

        TransformerFactory tf = TransformerFactory.newInstance();

        Transformer transformer;

        try {

            transformer = tf.newTransformer();

        } catch (TransformerConfigurationException e) {
            // e.printStackTrace();
            Logger.error(this.toString(), e.getMessage());
            return;
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        try {

            transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));

        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            Logger.error(this.toString(), e.getMessage());
            return;
        } catch (TransformerException e) {
            // e.printStackTrace();
            Logger.error(this.toString(), e.getMessage());
            return;
        }
    }

    public String stringForXml(String xml) {

        Document doc = documentForXml(xml);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        printDocument(doc, baos);

        try {

            return baos.toString("UTF-8");

        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            Logger.error(this.toString(), e.getMessage());
        }

        return null;
    }
}
