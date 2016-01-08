package k23b.am.view;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.scene.control.TreeItem;

public class XmlTreeParser {

    private static final Logger log = Logger.getLogger(XmlTreeParser.class);

    DocumentBuilder db;

    public XmlTreeParser() {

        try {

            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        } catch (ParserConfigurationException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            db = null;
        }
    }

    public TreeItem<String> treeItemForXml(String xml) {

        Document document = parseXML(xml);

        if (document == null) {
            return null;
        }

        Element element = document.getDocumentElement();

        TreeItem<String> treeItem = treeItemForElement(element, element.getTagName());

        treeItem.setExpanded(true);

        return treeItem;
    }

    private Document parseXML(String xml) {

        if (db == null)
            return null;

        try {

            StringReader sr = new StringReader(xml);

            InputSource is = new InputSource(sr);

            return db.parse(is);

        } catch (SAXException e) {
            // e.printStackTrace();
            log.error(e.getMessage());
        } catch (IOException e) {
            // e.printStackTrace();
            log.error(e.getMessage());
        }

        return null;
    }

    private TreeItem<String> treeItemForElement(Element element, String name) {

        TreeItem<String> treeItem = new TreeItem<String>(name);

        treeItem.setExpanded(false);

        NamedNodeMap attributes = element.getAttributes();

        int attrs = attributes.getLength();

        for (int i = 0; i < attrs; i++) {

            Node node = attributes.item(i);

            TreeItem<String> subItem = new TreeItem<String>(node.getNodeName() + "=" + node.getNodeValue());
            treeItem.getChildren().add(subItem);
        }

        NodeList childNodes = element.getChildNodes();

        int nodes = childNodes.getLength();

        for (int i = 0; i < nodes; i++) {

            Node node = childNodes.item(i);

            if (!(node instanceof Element))
                continue;

            TreeItem<String> subItem = treeItemForElement((Element) node, ((Element) node).getTagName());
            treeItem.getChildren().add(subItem);
        }

        return treeItem;
    }
}
