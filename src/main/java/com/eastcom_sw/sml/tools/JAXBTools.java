package com.eastcom_sw.sml.tools;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 
 * @author wen
 *
 */
public class JAXBTools {

	public static Object unmarshal(Object xmlReader,Class<?>... cs) throws JAXBException{
		Reader reader=null;
		Object obj=null;
		Unmarshaller shaller=null;
		 if(null==xmlReader) throw new IllegalArgumentException("place set the first augument to File,String,InputStream");
		JAXBContext jc = JAXBContext.newInstance(cs);
		shaller=jc.createUnmarshaller();
		if(xmlReader instanceof String){
		reader=new StringReader(String.valueOf(xmlReader));
		obj=shaller.unmarshal(reader);
		}else if(xmlReader instanceof InputStream){
			obj=shaller.unmarshal((InputStream)xmlReader);
		}else if(xmlReader instanceof File){
			obj=shaller.unmarshal((File)xmlReader);
		}
		
		return obj;
	}

	public static void marshal(Object jaxbElement,Object type,Class<?>... cs) throws JAXBException{
		Marshaller shaller=null;
		if(jaxbElement==null) throw new IllegalArgumentException("the jaxbElement is not null");
		JAXBContext jc = JAXBContext.newInstance(cs);
		shaller=jc.createMarshaller();
		if(type instanceof Writer){
			shaller.marshal(jaxbElement,(Writer)type);
		}else if(type instanceof OutputStream){
			shaller.marshal(jaxbElement,(OutputStream)type);
		}
	}
}
