package com.routon.T.Launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.routon.T.Launcher.Tpanel;

import android.util.Log;
import android.util.Xml;

public class Tpullpanelparse implements Tpanelparse{
	
	private ArrayList<Tpanel> panelData;
	private int xmlStatus;//1:初始状态　２：表示下载停止　４：下载中　８：下载成功　16：数据ＯＫ
	
	public Tpullpanelparse()
	{
		this.xmlStatus = 1<< 0;
	}
	
	public int getXmlStatus()
	{
		return this.xmlStatus;
	}
	
	public void setXmlStatus(int status)
	{
		this.xmlStatus = status;
	}
	
	public ArrayList<Tpanel> getData()
	{
		return this.panelData;
	}
	
	public boolean setXmlFile(String filepath)
	{
		try
		{
			File file = new File(filepath);
			this.panelData = (ArrayList<Tpanel>) parse(new FileInputStream(file));
			if (this.panelData != null)
			{
				this.xmlStatus = 1<<4;
				return true;
			}
			else 
			{
				this.xmlStatus = 1 << 1;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
		@Override
		public List<Tpanel> parse(InputStream is) throws Exception{
			List<Tpanel> panels = null;
			Tpanel mpanel = null;
			
			XmlPullParser parser = Xml.newPullParser();//由android.util.Xml创建一个实例
			parser.setInput(is,"UTF-8");
	
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT){
			//	Log.i("zhenghui","eventType="+eventType);
				switch(eventType){
				case XmlPullParser.START_DOCUMENT:
					panels = new ArrayList<Tpanel>();
					break;
				case XmlPullParser.START_TAG:
					//Log.i("zhenghui","parser.getName()="+parser.getName());
					if(parser.getName().equals("panel")){
						mpanel = new Tpanel();
						mpanel.setId(parser.getAttributeValue(null, "id"));
						mpanel.setName(parser.getAttributeValue(null, "name"));
						int flag = 1;
                        int boot = 0;
                        String strFlag = parser.getAttributeValue(null,"flag");
                        String strBoot = parser.getAttributeValue(null,"Boot");
                        if (strFlag != null)
                                flag = Integer.parseInt(strFlag);
                        if (strBoot != null)
                                boot = Integer.parseInt(strBoot);
                        mpanel.setFlag(flag);
                        mpanel.setBoot(boot);
					}
					else if(parser.getName().equals("menu")){
//						  String id1=parser.getAttributeValue(null, "id");
//	                        Log.i("zhenghui222","id="+id1);
						//eventType = parser.next();
                        String id=parser.getAttributeValue(null, "id");
                        String name=parser.getAttributeValue(null, "name");
                        String icon=parser.getAttributeValue(null, "icon");
                        String tm=parser.getAttributeValue(null, "tm");
//                        int flag =Integer.parseInt(parser.getAttributeValue(null,"flag"));
//                        int boot =Integer.parseInt(parser.getAttributeValue(null,"Boot"));
                        int flag1 = 1;
                        int boot1 = 0;
                        String strFlag = parser.getAttributeValue(null,"flag");
                        String strBoot = parser.getAttributeValue(null,"Boot");
                        if (strFlag != null)
                                flag1 = Integer.parseInt(strFlag);
                        if (strBoot != null)
                                boot1 = Integer.parseInt(strBoot);
                        mpanel.menuadd(id, name, icon, tm, flag1, boot1);

					}
					else if(parser.getName().equals("")){
						eventType = parser.next();
						panels.add(mpanel);
						mpanel=null;
					}
					break;
				case XmlPullParser.TEXT:
					break;
				case XmlPullParser.END_TAG:
					if(parser.getName().equals("panel")){
						panels.add(mpanel);
						mpanel=null;
					}
					break;
				}
				eventType = parser.next();
			}
			return panels;
		}
		
		   /**
         * Create a XML file from the panel list
         * @param panels panel list returned by Tpullpanelparse.parse()
         * @param filePath the XML file to write to
         * @return true if created, or false otherwise
         */
        public boolean createPanelXML(List<Tpanel> panels, String filePath) {
                if (panels != null) {
                        XmlSerializer serializer = Xml.newSerializer();
                        FileWriter writer;
                        try {
                                File fileXML = new File(filePath);
                                // if the parent path of the file doesn't exist
                                // then create it first
                                if (!fileXML.getParentFile().exists()) {
                                        fileXML.getParentFile().mkdirs();
                                }
                                writer = new FileWriter(fileXML);
                                serializer.setOutput(writer);
                                serializer.startDocument("UTF-8", true);
                                // <jl-hd-message result-code="OK">
                                serializer.text("\n"); // for format controlling
                                serializer.startTag("", "jl-hd-message");
                                serializer.attribute("", "result-code", "OK");
                                // <panel-list>
                                serializer.text("\n\t");
                                serializer.startTag("", "panel-list");
                                for (Tpanel mpanel : panels) {
                                        serializer.text("\n\t\t");
                                        serializer.startTag("", "panel");
                                        serializer.attribute("", "id",  (mpanel.getId() == null) ? "" : mpanel.getId());
                                        serializer.attribute("", "name", (mpanel.getName() == null) ? "" : mpanel.getName());
                                        serializer.attribute("", "flag", mpanel.getFlag() + "");
                                        serializer.attribute("", "boot", mpanel.getBoot() + "");
                                        ArrayList<panelmenu> menulist = mpanel.getmenulist();
                                        if (menulist != null && menulist.size() > 0) {
                                                for (panelmenu menu: menulist) {
                                                        serializer.text("\n\t\t\t");
                                                        serializer.startTag("", "menu");
                                                        serializer.attribute("", "id", (menu.getmenuId() == null) ? "" : menu.getmenuId());
                                                        serializer.attribute("", "name", (menu.getmenuName() == null) ? "" : menu.getmenuName());
                                                        serializer.attribute("", "icon", (menu.getmenuIcon() == null) ? "" : menu.getmenuIcon());
                                                        serializer.attribute("", "tm", (menu.getmenuTm() == null) ? "" : menu.getmenuTm());
                                                        serializer.attribute("", "flag", menu.getmenuFlag() + "");
                                                        serializer.attribute("", "boot", menu.getmenuBoot()  + "");
                                                        serializer.endTag("", "menu");
                                                }
                                                serializer.text("\n\t\t");
                                        }
                                        serializer.endTag("", "panel");
                                }
                                // </panel-list>
                                serializer.text("\n\t");
                                serializer.endTag("", "panel-list");
                                // </jl-hd-message>
                                serializer.text("\n");
                                serializer.endTag("", "jl-hd-message");
                                serializer.text("\n");
                                serializer.endDocument();
                                serializer.flush();
                                writer.close();
                                return true;
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
                return false;
        }

		
		
		 @Override  
		    public String serialize(List<Tpanel> panels) throws Exception {  
		          
		        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例  
		        StringWriter writer = new StringWriter();  
		        serializer.setOutput(writer);   //设置输出方向为writer  
		        serializer.startDocument("UTF-8", true);  
		        serializer.startTag("", "panels");  
		        for (Tpanel  mpanel:panels) {  
		            serializer.startTag("", "Tpanel");  
		            serializer.attribute("", "id", mpanel.getId() + "");   
		            serializer.attribute("", "name", mpanel.getName() + "");   
		            serializer.attribute("","flag",mpanel.getFlag() + "");  
		            serializer.attribute("","boot",mpanel.getBoot() + "");  
		            serializer.endTag("", "Tpanel");  
		        }  
		        serializer.endTag("", "Tpanel-list");  
		        serializer.endDocument();  
		          
		        return writer.toString();  
		    }

		
}
