package com.netease.tdg;


import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

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
	
	public PlatformConfigParser()
	{
		m_variables = new HashMap<String, Variable>();
		m_options = new HashMap<String, Boolean>();
		
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
			Log.e("NeoXDevice", "PlatformConfigParser parse failed!");
		}
	}
	
	
}
