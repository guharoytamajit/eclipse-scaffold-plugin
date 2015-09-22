package com.model;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class SchemaGen {
	public static void main(String[] args) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Workspace.class);
		jaxbContext.generateSchema(new SchemaOutputResolver() {

			public Result createOutput(String namespaceURI,
					String suggestedFileName) throws IOException {
				File file = new File("src/out.xsd");
				StreamResult result = new StreamResult(file);
				result.setSystemId(file.toURI().toURL().toString());
				return result;
			}

		});
	}
}
