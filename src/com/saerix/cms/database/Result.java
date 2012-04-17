package com.saerix.cms.database;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Result {
	Model model;
	
	private ArrayList<Row> result = new ArrayList<Row>();
	
	public int length = 0;
	
	Result(Model model, ResultSet rs, Class<? extends Row> rowclass) throws SQLException {
		this.model = model;
		while(rs.next()) {
			length++;
			try {
				result.add(rowclass.newInstance().set(model, rs));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Row getRow() {
		if(result.size() > 0)
			return result.get(0);
		else
			return null;
	}
	
	public List<? extends Row> getRows() {
		return result;
	}
	
	public String xml(Set<String> fields) throws DatabaseException {
		try {
			
			StringWriter sresult = new StringWriter();
			StreamResult streamResult = new StreamResult(sresult);
			
			SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			TransformerHandler hd = tf.newTransformerHandler();
			Transformer serializer = hd.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			hd.setResult(streamResult);
			hd.startDocument();
			
			writeRow(hd, this, fields);
			
			hd.endDocument();
			
			return sresult.toString();
		}
		catch(SAXException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static void writeRow(TransformerHandler hd, Result result, Set<String> fields) throws SAXException, DatabaseException {
		HashMap<String, Method> methods = new HashMap<String, Method>();
		
		for(Method method : result.model.getRowClass().getDeclaredMethods()) {
			XML xml = method.getAnnotation(XML.class);
			if(xml != null) {
				if(method.getParameterTypes().length > 0)
					throw new DatabaseException("Trying to xml the method "+method.getName()+" in the class "+result.model.getRowClass().getName()+" but the method have parameters.");
				
				if(!fields.contains(xml.rowname()))
					continue;
				
				methods.put(xml.rowname(), method);
			}
		}
		
		Set<String> overidden = new HashSet<String>();
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "num_rows", "CDATA", Integer.toString(result.length));
		hd.startElement("","","result",atts);
		atts.clear();
		
		for(Row row : result.getRows()) {
			hd.startElement("", "", "row", atts);
			for(Entry<String, Object> entry : row.getAllValues()) {
				if(fields.contains(entry.getKey())) {
					Method method = methods.remove(entry.getKey());
					String value;
					if(method != null) {
						try {
							overidden.add(entry.getKey());
							Object object = method.invoke(row);
							if(object == null)
								value = "null";
							else if(object instanceof Result) {
								hd.startElement("", "", entry.getKey(), atts);
								writeRow(hd, (Result) object, fields);
								hd.endElement("", "", entry.getKey());
								continue;
							}
							else 
								value = object.toString();
						} catch (Exception e) {
							throw (DatabaseException) new DatabaseException().initCause(e);
						}
					}
					else {
						Object object = row.getValue(entry.getKey());
						if(object != null)
							value = object.toString();
						else
							value = "null";
					}
					hd.startElement("", "", entry.getKey(), atts);
					hd.characters(value.toCharArray(), 0, value.length());
					hd.endElement("", "", entry.getKey());
				}
			}
			for(Entry<String, Method> entry : methods.entrySet()) {
				if(overidden.contains(entry.getKey()))
					continue;
				
				try {
					String value;
					Object object = entry.getValue().invoke(row);
					if(object == null)
						value = "null";
					else if(object instanceof Result) {
						hd.startElement("", "", entry.getKey(), atts);
						writeRow(hd, (Result) object, fields);
						hd.endElement("", "", entry.getKey());
						continue;
					}
					else 
						value = object.toString();
					
					hd.startElement("", "", entry.getKey(), atts);
					hd.characters(value.toCharArray(), 0, value.length());
					hd.endElement("", "", entry.getKey());
				} catch (Exception e) {
					throw (DatabaseException) new DatabaseException().initCause(e);
				}
			}
			
			hd.endElement("", "", "row");
		}
		
		hd.endElement("", "", "result");
	}
}
