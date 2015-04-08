package kz.lof.env;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import kz.lof.dataengine.IDatabase;
import kz.lof.dataengine.sys.ISystemDatabase;
import kz.lof.log.Logger;
import kz.lof.scheduler.IScheduledProcess;
import kz.lof.scheduler.ScheduleSettings;
import kz.lof.scheduler.Scheduler;
import kz.lof.server.Server;
import kz.lof.util.XMLUtil;

public class EnvTest {


	public static void main(String[] args) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			SAXParser saxParser = factory.newSAXParser();		
			SAXHandler cfgXMLhandler = new SAXHandler();	
			File file = new File("cfg.xml");				
			saxParser.parse(file,cfgXMLhandler);	
			Document xmlDocument = getDocument();
			String hostName = XMLUtil.getTextContent(xmlDocument, "/spring/name");
			print(hostName);
			NodeList nl = XMLUtil.getNodeList(xmlDocument, "/spring/database");
			print(nl.getLength());
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				Node childNode = XMLUtil.getNode(n, "name");
				print(childNode.getTextContent());	
				/*print(XMLUtil.getTextContent(n, "/name"));
				print(XMLUtil.getTextContent(n, "driver"));
				print(XMLUtil.getTextContent(n, "username"));
				print(XMLUtil.getTextContent(n, "password"));*/
			}
		}catch(SAXException se){
			se.printStackTrace();
		}catch(ParserConfigurationException pce){
			pce.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	private static void print(Object str) {
		System.out.println(str);
	}

	private static Document getDocument(){
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();			
			DocumentBuilder builder;

			builder = domFactory.newDocumentBuilder();					
			return builder.parse("cfg.xml");	
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (ParserConfigurationException e) {
			e.printStackTrace();				
		}
		return null;
	}

}
