package kz.lof.servlets;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import kz.lof.exception.TransformatorException;
import kz.lof.exception.TransformatorExceptionType;

import org.w3c.dom.*;
import net.sf.saxon.s9api.*;


public class SaxonTransformator implements IServletTransformator{

	public void toTrans(HttpServletResponse response, File xslFileObj,String xmlText) throws IOException, SaxonApiException, TransformatorException{
		XsltExecutable exp = null;
		XdmNode source = null;
		Processor proc = new Processor(false);		  
		XsltCompiler comp = proc.newXsltCompiler();

		try{
			exp = comp.compile(new StreamSource(xslFileObj));
		}catch (SaxonApiException sae) {
			throw new TransformatorException(TransformatorExceptionType.COMPILATION_ERROR_OR_FILE_DOES_NOT_EXIST, xslFileObj);
		}
		try{
			//System.out.println(xmlText);
			source = proc.newDocumentBuilder().build(new StreamSource(new StringReader(xmlText)));
		}catch (SaxonApiException sae) {
			throw new TransformatorException(TransformatorExceptionType.XML_CONTENT_ERROR, xmlText);
		}
		
		XsltTransformer trans = exp.load();          
		Serializer out = new Serializer();  
		ServletOutputStream sos = response.getOutputStream();
		out.setOutputStream(sos);          
		trans.setInitialContextNode(source);          
		trans.setDestination(out);   
		trans.transform();		  
		sos.close();
	}

	public String toTrans(File xslFileObj, String xmlText) throws IOException, SaxonApiException{

		//		System.out.println(xmlText);
		Processor proc = new Processor(false);		  
		XsltCompiler comp = proc.newXsltCompiler();       
		XsltExecutable exp = comp.compile(new StreamSource(xslFileObj));          
		XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new StringReader(xmlText)));         
		XsltTransformer trans = exp.load();          
		Serializer out = new Serializer();		
		ByteArrayOutputStream outPlace = new ByteArrayOutputStream();
		out.setOutputStream(outPlace);
		//	out.setOutputFile(new File("c:\\tmp\\result_.html"));
		trans.setInitialContextNode(source);          
		trans.setDestination(out); 
		trans.transform();		
		outPlace.close();
		return outPlace.toString();

	}


	public ArrayList<String> getFormWords(String stylesheet) {
		ArrayList<String> words = new ArrayList<String>();		
		try {			
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); 
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(stylesheet);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("//@select[contains(.,'/dictionary/')]");



			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			//	System.out.println(nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				String w = node.getNodeValue();
				//System.out.println(w.split("/dictionary/")[1]);
				words.add(w.split("/dictionary/")[1]);
			}


		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}	
		return words;
	}

	public static void main(String[] args) throws IOException, SaxonApiException{
		SaxonTransformator  st = new SaxonTransformator();
		String xmlText = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<message><tender><name>hrth</name>" +
		"<method>Запрос ценовых предложений посредством электронных закупок</method>" +
		"<organizer>Акимат города Алматы</organizer>" +
		"<place>г. Алматы</place>" +
		"<publishdate></publishdate>" +
		"<lot><pos>1</pos><name>gergerg</name><category>Лицензионное программное обеспечение</category><remark></remark></lot>" +
		"<lot><pos>2</pos><name>gjhhghjghj</name><category></category><remark></remark></lot>" +
		"</tender></message>";
		File xslFileObj = new File("C:"+File.separator+"workspace"+File.separator+"SmartDoc"+File.separator+"memo.xsl");
		String res = st.toTrans(xslFileObj, xmlText);
		System.out.println(res);
	}


}
