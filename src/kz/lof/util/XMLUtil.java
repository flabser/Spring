package kz.lof.util;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import kz.lof.server.Server;

import org.w3c.dom.*;
import org.xml.sax.SAXException;


public class XMLUtil {	
	public static XPathExpression typeExpr;
	public static XPathExpression idExpr;
	public static XPathExpression xsltFileExpr;
	public static XPathExpression isOnExpr;
	public static XPathExpression descrExpr;
	public static String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	private static XPath xpath;
	private static DocumentBuilder builder;

	public static void init(){		
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		try {
			typeExpr = xpath.compile("/rule/@type");
			idExpr = xpath.compile("/rule/@id");
			xsltFileExpr = xpath.compile("/rule/@xslt");
			isOnExpr = xpath.compile("/rule/@ison");
			descrExpr = xpath.compile("/rule/description");

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();			
			domFactory.setNamespaceAware(true); // never forget this!		
			builder = domFactory.newDocumentBuilder();		

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Document getDOMDocument(String filePath){
		try {
			File docFile = new File(filePath);				
			DocumentBuilderFactory pageFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder pageBuilder;
			pageBuilder = pageFactory.newDocumentBuilder();
			return pageBuilder.parse(docFile.toString());
		} catch (ParserConfigurationException e) {		
			Server.logger.errorLogEntry(e);
		} catch (SAXException e) {
			Server.logger.errorLogEntry(e);
		} catch (IOException e) {
			Server.logger.errorLogEntry(e);
		}
		return null;
	}

	public static String getRuleType(String filePath){
		String ruleType = "";
		try {				
			NodeList nodes = (NodeList)XMLUtil.typeExpr.evaluate(builder.parse(filePath), XPathConstants.NODESET);
			Node node = nodes.item(0);							
			ruleType = node.getFirstChild().getTextContent();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ruleType;
	}

	public static XPathExpression compileXPath(String pattern){
		XPathExpression exp = null;
		try {
			exp = xpath.compile(pattern);
		} catch (XPathExpressionException e) {	
			e.printStackTrace();
		}
		return exp;
	}

	public static String getTextContent(Document doc, String xPathExpression){
		String textResult = "";
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {				
			Object result = xpath.compile(xPathExpression).evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			Node node = nodes.item(0);
			if (node!=null)textResult = node.getTextContent().trim();

		} catch (Exception e) {
			Server.logger.warningLogEntry("Error of the rule file structure (" + xPathExpression +")");
		}
		return textResult;
	}

	public static int getNumberContent(Document doc, String xPathExpression){
		int intResult = 0;
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {				
			Object result = xpath.compile(xPathExpression).evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			Node node = nodes.item(0);
			if (node!=null)intResult = Integer.parseInt(node.getTextContent());

		} catch (Exception e) {
			Server.logger.warningLogEntry("Error of the rule file structure (" + xPathExpression +")");
		}
		return intResult;
	}

	public static String getTextContent(Node parentNode, String xPathExpression){
		String textResult = "";
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {				
			Object result = xpath.compile(xPathExpression).evaluate(parentNode, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			Node node = nodes.item(0);
			if (node!=null){
				textResult = node.getTextContent().trim();
			}


		} catch (Exception e) {
			Server.logger.warningLogEntry("Error of the rule file structure (" + xPathExpression +")");
		}
		return textResult;
	}

	
	public static int getNumberContent(Node parentNode, String xPathExpression, int defaultResult){
		int intResult = 0;
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {				
			Object result = xpath.compile(xPathExpression).evaluate(parentNode, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			Node node = nodes.item(0);
			if (node!=null){
				intResult = Integer.parseInt(node.getTextContent());
			}


		} catch (Exception e) {
			intResult = defaultResult;
		}
		return intResult;
	}
	
	public static String getTextContent(Node parentNode, String xPathExpression, boolean toUpperCase, String defaultResult){
		String textResult = defaultResult;
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {				
			Object result = xpath.compile(xPathExpression).evaluate(parentNode, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			Node node = nodes.item(0);
			if (node!=null){
				if (toUpperCase){
					textResult = node.getTextContent().toUpperCase().trim();
				}else{
					textResult = node.getTextContent().trim();
				}
			}

		} catch (Exception e) {
			Server.logger.warningLogEntry("Error of the rule file structure (" + xPathExpression +")");
		}
		return textResult;
	}

	public static NodeList getNodeList(Document doc, String xPathExpression){
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {				
			Object result = xpath.compile(xPathExpression).evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			return nodes;				
		} catch (Exception e) {
			Server.logger.warningLogEntry("Error of the rule file structure (" + xPathExpression +")");
			return null;
		}		
	}

	public static NodeList getNodeList(Node node, String xPathExpression){
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {				
			Object result = xpath.compile(xPathExpression).evaluate(node, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			return nodes;				
		} catch (Exception e) {
			Server.logger.warningLogEntry("Error of the rule file structure (" + xPathExpression +")");
			return null;
		}		
	}

	public static Node getNode(Node parentNode, String xPathExpression){

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {				
			Object result = xpath.compile(xPathExpression).evaluate(parentNode, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			Node node = nodes.item(0);
			return node;

		} catch (Exception e) {
			Server.logger.warningLogEntry("Error of the rule file structure (" + xPathExpression +")");
		}
		return null;
	}

	public static String getAsTagValue(String value){
		try{
			String val = value.replace("&", "&amp;");	
			val = val.replace("\n", "");
			val = val.replace("\r", "");
			val = val.replace("\"", "&quot;");
			val = val.replace("<", "&lt;").replace(">", "&gt;");	
			return  val;
		} catch (Exception e) {
			//AppEnv.logger.warningLogEntry("null)");
			return "null";
		}
	}
	
	public static String getAsTagValueForCode(String value){
		try{
			String val = value.replace("\"", "&quot;");
			val = val.replace("<", "&lt;").replace(">", "&gt;");	
			
			return  val;
		} catch (Exception e) {
			//AppEnv.logger.warningLogEntry("null)");
			return "null";
		}
	}

	public static String getAsAttribute(String attrName, String value){
		String val = value.replace("&", "&amp;");
		val = val.replace("\"", "&quot;");
		val = val.replace("<", "&lt;").replace(">", "&gt;");
		return " " + attrName + "=\"" + val + "\" ";
	}

}
