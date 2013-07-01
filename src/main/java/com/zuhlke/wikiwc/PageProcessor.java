package com.zuhlke.wikiwc;

import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.mongodb.BasicDBObject;

public class PageProcessor implements Processor {

	private static Logger logger = LoggerFactory.getLogger(PageProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		BasicDBObject page = exchange.getIn().getBody(BasicDBObject.class);
        String xml = page.getString("p");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(new InputSource(new StringReader(xml)));
        
        XPathFactory xpf = XPathFactory.newInstance();
        XPathExpression textExpr = xpf.newXPath().compile("revision/text/text()");
        XPathExpression titleExpr = xpf.newXPath().compile("title/text()");
        
        String  title = titleExpr.evaluate(document.getDocumentElement());
        String  wikiText = textExpr.evaluate(document.getDocumentElement());
        WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/", "http://www.mywiki.com/wiki/");
        String plainText = wikiModel.render(new PlainTextConverter(), wikiText);
        plainText = plainText.replaceAll("\\W+", " ").replaceAll("\\s+", " ").toLowerCase();
        
        page.put("t", title);
        page.put("p", plainText);
        
        boolean validText = (plainText.length() > 0);
		logger.info("{} with title {}", (validText ? "Importing" : "Ignoring") + " Page " + page.getInt("i"), title);
		
		exchange.getOut().setHeader("validText", validText);
		exchange.getOut().setHeader("title", title);
		exchange.getOut().setBody(page);
	}

}
