package com.zuhlke.wikiwc;

import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routes extends SpringRouteBuilder {

	private static Logger logger = LoggerFactory.getLogger(Routes.class);
	
	public void configure() throws Exception {
		
		logger.info("Creating routes!");
		
		errorHandler(deadLetterChannel("log:errors?showAll=true").maximumRedeliveries(0));

		from("file:wikidump?noop=true")
			.id("dumpIn")
				.split().tokenizeXML("page").streaming()
					.process(new DumpProcessor())
				  		.process(new PageProcessor())
			  				.filter(header("validText").isEqualTo(true))
			  					.to("mongodb:mDb?database={{mongodb.wikiDb}}&collection={{mongodb.pageCollection}}&operation=insert");
			
	}

}
