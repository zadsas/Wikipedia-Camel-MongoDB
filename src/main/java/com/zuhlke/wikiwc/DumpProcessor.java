package com.zuhlke.wikiwc;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.mongodb.BasicDBObject;

public class DumpProcessor implements Processor {

	private int globalPageCount = 0;
	
	@Override
	public void process(Exchange exchange) throws Exception {
    	String page = exchange.getIn().getBody(String.class);
    	BasicDBObject pageObj = new BasicDBObject();
    	pageObj.put("p", page);
    	pageObj.put("i", ++globalPageCount);
    	exchange.getOut().setBody(pageObj);
	}

}
