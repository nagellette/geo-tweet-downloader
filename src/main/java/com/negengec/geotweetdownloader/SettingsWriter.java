package com.negengec.geotweetdownloader;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
			String dbUserPassword) {

		try {
			File xmlFile = new File("twitter_download.properties");
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

				eElement.getElementsByTagName("consumerKey").item(0)
						.setTextContent(consumerKey);
				// System.out.println(consumerKey + "----" + eElement.getElementsByTagName("consumerKey").item(0).getTextContent());
				eElement.getElementsByTagName("consumerSecret").item(0)
						.setTextContent(consumerSecret);
				eElement.getElementsByTagName("accessTokken").item(0)
						.setTextContent(accessTokken);
				eElement.getElementsByTagName("accessTokkenSecret").item(0)
						.setTextContent(accessTokkenSecret);
				eElement.getElementsByTagName("dbHostUrl").item(0)
						.setTextContent(dbHostUrl);
				eElement.getElementsByTagName("dbHostPort").item(0)
						.setTextContent(dbHostPort);
				eElement.getElementsByTagName("dbName").item(0)
						.setTextContent(dbName);
				eElement.getElementsByTagName("dbUser").item(0)
						.setTextContent(dbUser);
				eElement.getElementsByTagName("dbUserPassword").item(0)
						.setTextContent(dbUserPassword);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}
}
