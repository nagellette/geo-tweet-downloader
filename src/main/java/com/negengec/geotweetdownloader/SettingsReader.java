package com.negengec.geotweetdownloader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class SettingsReader {

    String consumerKey;
    String consumerSecret;
    String accessTokken;
    String accessTokkenSecret;
    String dbHostUrl;
    String dbHostPort;
    String dbName;
    String dbUser;
    String dbUserPassword;

    public void readSettings() {

        //TODO: relative path definition with OS check should move to another class, duplicate with writer class
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

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("defaultSettings");
            Node nNode = nList.item(0);
            System.out.println("Current Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                setConsumerKey(eElement.getElementsByTagName("consumerKey")
                        .item(0).getTextContent());
                setConsumerSecret(eElement
                        .getElementsByTagName("consumerSecret").item(0)
                        .getTextContent());
                setAccessTokken(eElement.getElementsByTagName("accessTokken")
                        .item(0).getTextContent());
                setAccessTokkenSecret(eElement
                        .getElementsByTagName("accessTokkenSecret").item(0)
                        .getTextContent());
                setDbHostUrl(eElement.getElementsByTagName("dbHostUrl").item(0)
                        .getTextContent());
                setDbHostPort(eElement.getElementsByTagName("dbHostPort")
                        .item(0).getTextContent());
                setDbName(eElement.getElementsByTagName("dbName").item(0)
                        .getTextContent());
                setDbUser(eElement.getElementsByTagName("dbUser").item(0)
                        .getTextContent());
                setDbUserPassword(eElement
                        .getElementsByTagName("dbUserPassword").item(0)
                        .getTextContent());
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getConsumerKey() {
        return consumerKey;
    }

    // Setters
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getAccessTokken() {
        return accessTokken;
    }

    public void setAccessTokken(String accessTokken) {
        this.accessTokken = accessTokken;
    }

    public String getAccessTokkenSecret() {
        return accessTokkenSecret;
    }

    public void setAccessTokkenSecret(String accessTokkenSecret) {
        this.accessTokkenSecret = accessTokkenSecret;
    }

    public String getDbHostUrl() {
        return dbHostUrl;
    }

    public void setDbHostUrl(String dbHostUrl) {
        this.dbHostUrl = dbHostUrl;
    }

    public String getDbHostPort() {
        return dbHostPort;
    }

    public void setDbHostPort(String dbHostPort) {
        this.dbHostPort = dbHostPort;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbUserPassword() {
        return dbUserPassword;
    }

    public void setDbUserPassword(String dbUserPassword) {
        this.dbUserPassword = dbUserPassword;
    }

}
