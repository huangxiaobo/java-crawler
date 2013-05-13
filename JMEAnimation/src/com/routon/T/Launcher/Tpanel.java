package com.routon.T.Launcher;

import java.util.ArrayList;

/*******定义一级挡板**********/
public class Tpanel {
		private String id;//数组串，
		private String name;//挡板名字
		private int flag;  //挡板属性；0，1，2
		private int Flag; //挡板属性；0，1
		private int boot;//启动模式：0，1，2
		private String android_exec;//android启动命令
		private ArrayList<panelmenu> menulist=null;
		private String Style;
		private String Text;
		private String AnimationPic;
		private String MODULE_ID;
		private String Exec;
		private String Args;
		private String Type;
		private String Cursor;
		
		public Tpanel(){
			menulist = new ArrayList<panelmenu>();
		}
		
		public String getId(){
			return id;
		}
		
		public void setId(String id){
	//		Log.i("zhenghui","id="+id);
			this.id = id;
		}
		
		public String getName(){
			 return name;
		}
		
		public void setName(String name){
		//	Log.i("zhenghui","name="+name);
			this.name = name;
		}
		
		public int getFlag(){
			return flag;
		}
		
		public void setFlag(int flag){
			this.flag = flag;
		}
		
		public int getFlagIni(){
			return Flag;
		}
		
		public void setFlagIni(int flag){
			this.Flag = flag;
		}
		
		public int getBoot(){
			return boot;
		}
		
		public void setBoot(int boot){
			this.boot = boot;
		}
		
		public String getAndroidExec(){
			return android_exec;
		}
		
		public void setAndroidExec(String android_exec){
			this.android_exec=android_exec;
		}
		
		public void menuadd(String id, String name, String icon, String tm, int flag,int boot){
			panelmenu menu = new panelmenu();
			menu.setmenuId(id);
			menu.setmenuName(name);
			menu.setmenuIcon(icon);
            menu.setmenuTm(tm);
			menu.setmenuFlag(flag);
			menu.setmenuBoot(boot);
			menulist.add(menu);
		}
		
		public ArrayList<panelmenu> getmenulist(){
			return menulist;
		}
		
		public panelmenu menuget(int i){
				panelmenu  menu= menulist.get(i);
				return menu;
		}
	
		public String getStyle(){
			return Style;
		}
		
		public void setStyle(String Style){
			this.Style = Style;
		}
		
		public String getText(){
			return Text;
		}
		
		public void setText(String Text){
			this.Text = Text;
		}
		public String getAnimationPic(){
			return AnimationPic;
		}
		
		public void setAnimationPic(String AnimationPic){
			this.AnimationPic = AnimationPic;
		}
		public String getMODULE_ID(){
			return MODULE_ID;
		}
		
		public void setMODULE_ID(String MODULE_ID){
			this.MODULE_ID = MODULE_ID;
		}
		public String getExec(){
			return Exec;
		}
		
		public void setExec(String Exec){
			this.Exec = Exec;
		}
		
		public String getArgs(){
			return Args;
		}
		
		public void setArgs(String Args){
			this.Args = Args;
		}
		
		public String getType(){
			return Type;
		}
		
		public void setType(String Type){
			this.Type = Type;
		}
		
		public String getCursor(){
			return Cursor;
		}
		
		public void setCursor(String Cursor){
			this.Cursor = Cursor;
		}
		
		@Override
		public String toString() {
			return "id:" + id + "name:" + name + " flag:" + flag + ", Boot" + boot +"Style:" + Style +"Text:" + Text + "AnimationPic:" +AnimationPic
					+"MODULE_ID:" +MODULE_ID +"Exec:" + Exec + "Args:"+ Args +"Type:" +Type +"Cursor:"+Cursor;
		}
}

/***********定义二级子菜单**********/
class panelmenu{
	private String id;//menu数组串，
	private String name;//menu名字
	private String icon;
    private String tm;
	private int flag;  //menu属性；0，1，2
	private int boot;//menu启动模式：0，1，2
	private String android_exec;//android启动命令
	private String Text;
	private String MODULE_ID;
	private String Exec;
	private String Args;
	private String Type;
	private String Cursor;
	
	
	public String getmenuId(){
		return id;
	}
	
	public void setmenuId(String id){
		this.id = id;
	}
	
	public String getmenuName(){
		 return name;
	}
	
	public void setmenuName(String name){
		this.name = name;
	}
	
	 public String getmenuIcon(){
          return icon;
	 }

	 public void setmenuIcon(String icon){
	         this.icon = icon;
	 }
	
	 public String getmenuTm(){
	          return tm;
	 }
	
	 public void setmenuTm(String tm){
	         this.tm = tm;
	 }
	
	public int getmenuFlag(){
		return flag;
	}
	
	public void setmenuFlag(int flag){
		this.flag = flag;
	}
	
	public int getmenuBoot(){
		return boot;
	}
	
	public void setmenuBoot(int boot){
		this.boot = boot;
	}
	
	public String getmenuAndroidExec(){
		return android_exec;
	}
	
	public void setmenuAndroidExec(String android_exec){
		this.android_exec=android_exec;
	}
	
	
	public String getmenuText(){
		return Text;
	}
	
	public void setmenuText(String Text){
		this.Text = Text;
	}

	public String getmenuMODULE_ID(){
		return MODULE_ID;
	}
	
	public void setmenuMODULE_ID(String MODULE_ID){
		this.MODULE_ID = MODULE_ID;
	}
	public String getmenuExec(){
		return Exec;
	}
	
	public void setmenuExec(String Exec){
		this.Exec = Exec;
	}
	
	public String getmenuArgs(){
		return Args;
	}
	
	public void setmenuArgs(String Args){
		this.Args = Args;
	}
	
	public String  getmenuType(){
		return Type;
	}
	
	public void setmenuType(String Type){
		this.Type = Type;
	}
	
	public String  getmenuCursor(){
		return Cursor;
	}
	
	public void setmenuCursor(String Cursor){
		this.Cursor = Cursor;
	}
	
	@Override
	public String toString() {
		return "id:" + id + "name:" + name + " flag:" + flag + ", Boot" + boot  +"Text:" + Text 
				+"MODULE_ID:" +MODULE_ID +"Exec:" + Exec + "Args:"+ Args +"Type:" +Type +"Cursor:"+Cursor;
	}
	
}