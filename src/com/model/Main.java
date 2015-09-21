package com.model;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Main {
public static void main(String[] args) {

	  try {
			InputStream fis = new Main().getClass()
				    .getResourceAsStream("../../scaffold.xml");
		//File file = new File("scaffold.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(Workspace.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);


		Object unmarshal = jaxbUnMarshaller.unmarshal(fis);
		
		jaxbMarshaller.marshal(unmarshal, System.out);
	      } catch (JAXBException e) {
		e.printStackTrace();
	      }

	}
}
