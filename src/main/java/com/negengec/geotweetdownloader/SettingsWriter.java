package com.negengec.geotweetdownloader;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class SettingsWriter {

    String consumerKey;
    String consumerSecret;
    String accessTokken;
    String accessTokkenSecret;
    String dbHostUrl;
    String dbHostPort;
    String dbName;
    String dbUser;
    String dbUserPassword;

    public void writeSettings(String consumerKey, String consumerSecret,
                              String accessTokken, String accessTokkenSecret, String dbHostUrl,
                              String dbHostPort, String dbName, String dbUser,
                              String dbUserPassword) throws TransformerException {

        //TODO: relative path definition with OS check should move to another class, duplicate with reader class
        //TODO: Mac OS should be added

        String absoluteFilePath = "";

        try {
            String fileName = "twitter_download.properties";
            System.out.println(System.getProperty("os.name"));
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                absoluteFilePath = "." + File.separator + "target" + File.separator + "conf" + File.separator + fileName;
                System.out.println(absoluteFilePath);
            } else if (System.getProperty("os.name").toLowerCase().contains("windows")){
                absoluteFilePath = "conf" + File.separator + fileName;
                System.out.println(absoluteFilePath);
            } else {
                System.out.println("Your operating system does not supported. Abort.");
                System.exit(0);
            }

            File xmlFile = new File(absoluteFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = null;

            try {
                doc = dBuilder.parse(xmlFile);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Node defaultSettings = doc.getElementsByTagName("defaultSettings")
                    .item(0);
            NodeList list = defaultSettings.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);

                if ("consumerKey".equals(node.getNodeName())) {
                    node.setTextContent(consumerKey);
                }

                if ("consumerSecret".equals(node.getNodeName())) {
                    node.setTextContent(consumerSecret);
                }

                if ("accessTokken".equals(node.getNodeName())) {
                    node.setTextContent(accessTokken);
                }

                if ("accessTokkenSecret".equals(node.getNodeName())) {
                    node.setTextContent(accessTokkenSecret);
                }

                if ("dbHostUrl".equals(node.getNodeName())) {
                    node.setTextContent(dbHostUrl);
                }

                if ("dbHostPort".equals(node.getNodeName())) {
                    node.setTextContent(dbHostPort);
                }

                if ("dbName".equals(node.getNodeName())) {
                    node.setTextContent(dbName);
                }

                if ("dbUser".equals(node.getNodeName())) {
                    node.setTextContent(dbUser);
                }

                if ("dbUserPassword".equals(node.getNodeName())) {
                    node.setTextContent(dbUserPassword);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("twitter_download.properties"));
                transformer.transform(source, result);

            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }
}
