package com.saerix.cms.database;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
	private ArrayList<Row> result = new ArrayList<Row>();
	
	public int length = 0;
	
	Result(ResultSet rs, Class<? extends Row> rowclass) throws SQLException {
		while(rs.next()) {
			length++;
			try {
				result.add(rowclass.newInstance().set(rs));
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
	
	public String xml(Set<String> fields) {
		try {
			
			StringWriter sresult = new StringWriter();
			StreamResult streamResult = new StreamResult(sresult);
			
			SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			TransformerHandler hd = tf.newTransformerHandler();
			Transformer serializer = hd.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			hd.setResult(streamResult);
			hd.startDocument();
			
			AttributesImpl atts = new AttributesImpl();
			
			atts.addAttribute("", "", "num_rows", "CDATA", Integer.toString(length));
			hd.startElement("","","result",atts);
			atts.clear();
			
			for(Row row : result) {
				hd.startElement("", "", "row", atts);
				for(Entry<String, Object> entry : row.getAllValues()) {
					if(fields.contains(entry.getKey())) {
						hd.startElement("", "", entry.getKey(), atts);
						String value = entry.getValue().toString();
						hd.characters(value.toCharArray(), 0, value.length());
						hd.endElement("", "", entry.getKey());
					}
				}
				hd.endElement("", "", "row");
			}
			
			hd.endElement("", "", "result");
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
}
