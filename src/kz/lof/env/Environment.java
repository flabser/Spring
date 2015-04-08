package kz.lof.env;

import kz.lof.constants.OrgType;
import kz.lof.constants.RunMode;
import kz.lof.dataengine.sys.ISystemDatabase;
import kz.lof.log.Logger;
import kz.lof.scheduler.IScheduledProcess;
import kz.lof.scheduler.ScheduleSettings;
import kz.lof.scheduler.Scheduler;
import kz.lof.server.Server;
import kz.lof.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Environment {
    public static boolean verboseLogging;
    public static int serverVersion = 7;
    public static String serverName;
    public static String hostName;
    public static int httpPort = 15045;
    public static boolean noWSAuth = false;
    public static String httpSchema = "http";
    public static boolean adminConsoleEnable;
    public static String tmpDir;
    public static ArrayList<String> fileToDelete = new ArrayList<String>();
    public static HashMap<String, String> mimeHash = new HashMap<String, String>();
    public static Logger logger;
    public static int delaySchedulerStart;
    public static ScheduleSettings scheduleSettings;
    public static Boolean isSSLEnable = false;
    public static int secureHttpPort;
    public static String keyPwd = "";
    public static String keyStore = "";
    public static String trustStore;
    public static String trustStorePwd;
    public static boolean isClientSSLAuthEnable;
    public static ISystemDatabase systemBase;
    //public static IDatabase dataBase;
    public static Scheduler scheduler;

    public static Map<OrgType, Organization> orgMap;

    private static ArrayList<IScheduledProcess> scheduledRules = new ArrayList<IScheduledProcess>();


    public static void init() {
        logger = Server.logger;
        initProcess();
    }

    private static void initProcess() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser saxParser = factory.newSAXParser();
            SAXHandler cfgXMLhandler = new SAXHandler();
            File file = new File("cfg.xml");
            saxParser.parse(file, cfgXMLhandler);
            Document xmlDocument = getDocument();

            logger.normalLogEntry("Initialize runtime environment");
            initMimeTypes();

            hostName = XMLUtil.getTextContent(xmlDocument, "/spring/hostname");
            if (hostName.trim().equals("")) {
                hostName = getHostName();
            }

            serverName = XMLUtil.getTextContent(xmlDocument, "/spring/name");
            String portAsText = XMLUtil.getTextContent(xmlDocument, "/spring/port");
            try {
                httpPort = Integer.parseInt(portAsText);
                logger.normalLogEntry("WebServer is going to use port: " + httpPort);
            } catch (NumberFormatException nfe) {
                logger.normalLogEntry("WebServer is going to use standart port");
            }
            
            try {
            	String auth = XMLUtil.getTextContent(xmlDocument, "/spring/no-ws-auth");
            	if ("true".equalsIgnoreCase(auth)) {
            		noWSAuth = true;
            	}
            } catch (Exception e) {
            	noWSAuth = false;
            }

            try {
                if (XMLUtil.getTextContent(xmlDocument, "/spring/adminapp/@mode").equalsIgnoreCase("on")) {
                    adminConsoleEnable = true;
                }
            } catch (Exception nfe) {
                adminConsoleEnable = false;
            }

            try {
                delaySchedulerStart = Integer.parseInt(XMLUtil.getTextContent(xmlDocument, "/spring/scheduler/startdelaymin"));
            } catch (Exception nfe) {
                delaySchedulerStart = 1;
            }

            try {
                isSSLEnable = XMLUtil.getTextContent(xmlDocument, "/spring/ssl/@mode").equalsIgnoreCase("on");
                if (isSSLEnable) {
                    String sslPort = XMLUtil.getTextContent(xmlDocument, "/spring/ssl/port");
                    try {
                        secureHttpPort = Integer.parseInt(sslPort);
                    } catch (NumberFormatException nfe) {
                        secureHttpPort = 15055;
                    }
                    keyPwd = XMLUtil.getTextContent(xmlDocument, "/spring/ssl/keypass");
                    keyStore = XMLUtil.getTextContent(xmlDocument, "/spring/ssl/keystore");
                    isClientSSLAuthEnable = XMLUtil.getTextContent(xmlDocument, "/spring/ssl/clientauth/@mode").equalsIgnoreCase("on");
                    if (isClientSSLAuthEnable) {
                        trustStore = XMLUtil.getTextContent(xmlDocument, "/spring/ssl/clientauth/truststorefile");
                        trustStorePwd = XMLUtil.getTextContent(xmlDocument, "/spring/ssl/clientauth/truststorepass");
                    }
                    //logger.normalLogEntry("SSL is enabled. keyPass: " + keyPwd +", keyStore:" + keyStore);
                    logger.normalLogEntry("TLS is enabled");
                    httpSchema = "https";
                }
            } catch (Exception ex) {
                logger.normalLogEntry("TLS configiration error");
                isSSLEnable = false;
                keyPwd = "";
                keyStore = "";
            }


            try {
                String res = XMLUtil.getTextContent(xmlDocument, "/spring/logging/verbose");
                if (res.equalsIgnoreCase("true")) {
                    verboseLogging = true;
                    logger.warningLogEntry("Verbose logging is turned on");
                }
            } catch (Exception e) {
                verboseLogging = false;
            }


            File tmp = new File("tmp");
            if (!tmp.exists()) {
                tmp.mkdir();
            }

            tmpDir = tmp.getAbsolutePath();

            try {
                NodeList dbList = XMLUtil.getNodeList(xmlDocument, "/spring/organization");
                if (dbList.getLength() > 0) {
                    orgMap = new HashMap<OrgType, Organization>();
                    for (int i = 0; i < dbList.getLength(); i++) {
                        Node node = dbList.item(i);
            				
                        Organization org = new Organization();
                        if (XMLUtil.getTextContent(node,"@mode").equalsIgnoreCase("off")) org.isOn = RunMode.OFF; 
                        org.setOrgType(OrgType.valueOf(XMLUtil.getTextContent(node, "orgtype")));
                        org.setOrgName(XMLUtil.getTextContent(node, "name"));
                        org.setDriver(XMLUtil.getTextContent(node, "driver"));
                        org.setConnectionURL(XMLUtil.getTextContent(node, "url"));
                        org.setDBUserName(XMLUtil.getTextContent(node, "username"));
                        org.setDBPassword(XMLUtil.getTextContent(node, "password"));
                        if (!org.getConnectionURL().equals("")) {
                            org.databaseEnable = true;
                        }

                        org.setConvDriver(XMLUtil.getTextContent(node, "conversion/driver"));
                        org.setConvConnectionURL(XMLUtil.getTextContent(node, "conversion/url"));
                        org.setConvDbUserName(XMLUtil.getTextContent(node, "conversion/username"));
                        org.setConvDbPassword(XMLUtil.getTextContent(node, "conversion/password"));
                        org.setConvFilePath(XMLUtil.getTextContent(node, "conversion/filepath"));
                        if (!org.getConvConnectionURL().equals("")) {
                            org.convDatabaseEnable = true;
                        }

                        org.setLoadHandlerClass(XMLUtil.getTextContent(node, "load/handlerclass"));
                        org.setLoadFilePath(XMLUtil.getTextContent(node, "load/filepath"));
                        if (!org.getLoadFilePath().equals("")) {
                            org.loadFileEnable = true;
                        }


                        Node schedNode = XMLUtil.getNode(node, "load/scheduler");
                        if (org.isOn == RunMode.ON && schedNode != null && (!org.getLoadHandlerClass().equals(""))) {
                            org.scheduleSettings = new ScheduleSettings(schedNode);
                            scheduledRules.add((IScheduledProcess) org);
                        }

                        logger.normalLogEntry("Load settings of " + org); 
                        orgMap.put(OrgType.valueOf(XMLUtil.getTextContent(node, "orgtype")), org);
                        		
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Server.logger.warningLogEntry("Unable to determine parameters of organization");              
            }
           
        } catch (SAXException se) {
            logger.errorLogEntry(se);
        } catch (ParserConfigurationException pce) {
            logger.errorLogEntry(pce);
        } catch (IOException ioe) {
            logger.errorLogEntry(ioe);
        }
    }

    private static Document getDocument() {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            builder = domFactory.newDocumentBuilder();
            return builder.parse("cfg.xml");
        } catch (SAXException e) {
            logger.errorLogEntry(e);
        } catch (IOException e) {
            logger.errorLogEntry(e);
        } catch (ParserConfigurationException e) {
            logger.errorLogEntry(e);
        }
        return null;
    }


    private static String getHostName() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return addr.getHostName();
    }

    public static void shutdown(OrgType orgType) {
        Environment.orgMap.get(orgType).getidb().shutdown();
        //dataBase.shutdown();
    }

    public static ArrayList<IScheduledProcess> getScheduledRules() {
        return scheduledRules;
    }

    public static void initMimeTypes() {
        mimeHash.put("pdf", "application/pdf");
        mimeHash.put("doc", "application/msword");
        mimeHash.put("xls", "application/vnd.ms-excel");
        mimeHash.put("tif", "image/tiff");
        mimeHash.put("rtf", "application/msword");
        mimeHash.put("gif", "image/gif");
        mimeHash.put("jpg", "image/jpeg");
        mimeHash.put("html", "text/html");
        mimeHash.put("zip", "application/zip");
        mimeHash.put("rar", "application/x-rar-compressed");
        //mimeHash.put("xls", "application/x-msexcel");
    }

}

