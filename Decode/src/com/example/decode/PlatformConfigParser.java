package com.example.decode;


import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import android.content.Context;
import android.util.*;

public class PlatformConfigParser 
{
	public class Variable
	{
		protected String m_name;
		
		public Variable(String name)
		{
			m_name = name;
		}
		
		public boolean evaluate(String predicate, String object)
		{
			return false;
		}
		
		public String getName()
		{
			return m_name;
		}
	}
	
	public class IntVariable extends Variable
	{
		protected int m_value;
		
		public IntVariable(String name, int value)
		{
			super(name);
			m_value = value;
		}
		
		public int getValue()
		{
			return m_value;
		}
		
		public boolean evaluate(String predicate, String object)
		{
			int v = 0;
			try
			{
				v = Integer.parseInt(object);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			if (predicate.equals("=="))
			{
				return m_value == v;
			}
			else if (predicate.equals("!="))
			{
				return m_value != v;
			}
			else if (predicate.equals(">="))
			{
				return m_value >= v;
			}
			else if (predicate.equals(">"))
			{
				return m_value > v;
			}
			else if (predicate.equals("<="))
			{
				return m_value <= v;
			}
			else if (predicate.equals("<"))
			{
				return m_value < v;
			}
			else
			{
				Log.e("NeoXDevice", "Unrecognized predicate " + predicate);
			}
			return false;
		}
		
	}
	
	public class StringVariable extends Variable
	{
		protected String m_value;
		public StringVariable(String name, String value)
		{
			super(name);
			m_value = value.toLowerCase(Locale.getDefault());
		}
		
		public String getValue()
		{
			return m_value;
		}
		
		public boolean evaluate(String predicate, String object)
		{
			object = object.toLowerCase(Locale.getDefault());
			if (predicate.equals("=="))
			{
				return m_value.equals(object);
			}
			else if (predicate.equals("!="))
			{
				return !m_value.equals(object);
			}
			else if (predicate.equals("contain"))
			{
				return m_value.contains(object);
			}
			else if (predicate.equals("startwith"))
			{
				return m_value.startsWith(object);
			}
			else if (predicate.equals("endwith"))
			{
				return m_value.endsWith(object);
			}
			else if (predicate.equals("not contain"))
			{
				return !m_value.contains(object);
			}
			else if (predicate.equals("not startwith"))
			{
				return !m_value.startsWith(object);
			}
			else if (predicate.equals("not endwith"))
			{
				return !m_value.endsWith(object);
			}
			else
			{
				Log.e("NeoXDevice", "Unrecognized predicate " + predicate);
			}
			return false;
		}		
	}
	
	private HashMap<String, Variable> m_variables;
	
	private HashMap<String, Boolean> m_options;
	
	private Context m_context;
	
	public PlatformConfigParser(Context context)
	{
		m_variables = new HashMap<String, Variable>();
		m_options = new HashMap<String, Boolean>();
		m_context = context;
	}
	
	public void addVariable(Variable v)
	{
		m_variables.put(v.getName(), v);
	}
	
	public void addVariable(String name, int i)
	{
		m_variables.put(name, new IntVariable(name, i));
	}
	
	public void addVariable(String name, String s)
	{
		m_variables.put(name, new StringVariable(name, s));
	}
	
	public HashMap<String, Boolean> getOptions()
	{
		return m_options;
	}
	
	class XMLHandler extends DefaultHandler
	{
		private HashMap<String, Variable> m_variables;
		private HashMap<String, Boolean> m_options;
		public final static int UNKNOWN = 0;
		public final static int AND = 1;
		public final static int OR = 2;
		
		private Stack<Integer> m_condition_group;
		private Stack<Boolean> m_condition;
		private String m_config;
		private boolean m_option;
		
		public XMLHandler(HashMap<String, Variable> v, HashMap<String, Boolean> o)
		{
			m_variables = v;
			m_options = o;
			m_condition_group = new Stack<Integer>();
			m_condition = new Stack<Boolean>();
			m_config = null;
			m_option = false;
		}
		
		@Override
	    public void startElement(String uri, String localName, String qName, Attributes attributes) 
	    		throws SAXException 
	    {
			if (qName.equals("Config"))
			{
				m_condition_group.clear();
				m_condition.clear();
				m_config = attributes.getValue("name");
			}
			else if(qName.equals("ConditionGroup"))
			{
				String t = attributes.getValue("type");
				int c = UNKNOWN;
				if (t.equals("and"))
				{
					c = AND;
					m_option = true;
				}
				else
				{
					c = OR;
					m_option = false;
				}
				m_condition_group.push(Integer.valueOf(c));
				m_condition.push(Boolean.valueOf(m_option));
			}
			else if(qName.equals("Condition"))
			{
				int c = m_condition_group.peek().intValue();
				if (c == AND)
				{
					if (m_option)
					{
						String subject = attributes.getValue("subject");
						String predicate = attributes.getValue("predicate");
						String object = attributes.getValue("object");
						
						Variable v = m_variables.get(subject);
						m_option = m_option && v.evaluate(predicate, object);
					}
				}
				else
				{
					if (!m_option)
					{
						String subject = attributes.getValue("subject");
						String predicate = attributes.getValue("predicate");
						String object = attributes.getValue("object");
						
						Variable v = m_variables.get(subject);
						m_option = m_option || v.evaluate(predicate, object);
					}
				}
			}
	    }
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException 
		{
			if (qName.equals("Config"))
			{
				m_options.put(m_config, Boolean.valueOf(m_option));
				m_config = null;
			}
			else if(qName.equals("ConditionGroup"))
			{
				m_condition_group.pop();
				m_condition.pop();
				if (!m_condition_group.empty())
				{
					int c = m_condition_group.peek().intValue();
					if (c == AND)
					{
						m_option = m_option && m_condition.peek().booleanValue();
					}
					else
					{
						m_option = m_option || m_condition.peek().booleanValue();
					}
				}
			}
		}
	}
	
	private File encryptFile(InputStream inputStream) {
		byte[] buffer = new byte[1024];
		int readCount = 0;
		try {
			File outputFile = createTempFile();
			FileOutputStream outputstream = new FileOutputStream(outputFile);

			while ((readCount = inputStream.read(buffer)) > 0) {
				encryptData(buffer);
				outputstream.write(buffer, 0, readCount);
			}

			inputStream.close();
			outputstream.flush();
			outputstream.close();

			return outputFile;
		} catch (Exception e) {
			Log.e("NeoXDevice", "PlatformConfigParser encryptFile failed!:" + e);
		}
		return null;
	}

	private File decryptFile(InputStream inputStream) {
		byte[] buffer = new byte[1024];
		int readCount = 0;
		try {
			File decryptedFile = createTempFile();
			FileOutputStream outputStream = new FileOutputStream(decryptedFile);

			while ((readCount = inputStream.read(buffer)) > 0) {
				decryptData(buffer);
				outputStream.write(buffer, 0, readCount);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

			return decryptedFile;
		} catch (Exception e) {
			Log.e("NeoXDevice", "PlatformConfigParser decryptFile failed!:" + e);
		}
		return null;
	}

	private File createTempFile() {
		try {
			File outputDir = m_context.getCacheDir(); // context being the
														// Activity pointer
			File outputFile = File.createTempFile("EncrytedPlatformConfig",
					"xml", outputDir);
			Log.d("amw", outputFile.getAbsolutePath());
			if (!outputFile.exists()) {
				File parent = outputFile.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
				outputFile.createNewFile();
			}
			return outputFile;
		} catch (Exception e) {
			;
		}
		return null;
	}

	private void encryptData(byte[] data) {
		for (int i = 0; i < data.length; ++i) {
			data[i] ^= 0xff;
		}
	}

	private void decryptData(byte[] data) {
		encryptData(data);
	}
	
	public void parse(InputStream is, boolean needDecrypt) {
		File decryptedFile = null;
		try {
			if (needDecrypt) {
				decryptedFile = decryptFile(is);
				is = new FileInputStream(decryptedFile);
			}
			
			parse(is);
			
			if (needDecrypt && decryptedFile != null) {
				decryptedFile.delete();
			}
		} catch (Exception e) {
			Log.e("NeoXDevice", "PlatformConfigParser parse failed!");
		}
	
	}
	
	public void parse(InputStream is)
	{
		m_options.clear();
		try
		{
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            XMLHandler handler = new XMLHandler(m_variables, m_options);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(is));
		}
		catch(Exception e)
		{
			Log.e("NeoXDevice", "PlatformConfigParser parse failed!:" + e);
		}
	}
	
	
}
