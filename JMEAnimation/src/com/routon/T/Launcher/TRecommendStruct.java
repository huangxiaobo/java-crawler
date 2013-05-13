package com.routon.T.Launcher;

public class TRecommendStruct {
	private int type ;
	private int rid;
	private String name;
	private float grade;
	private String small_img;
	private String small_poster;
	private String big_poster;
	private String detail;
	private String update_info;
	
	public TRecommendStruct(){
		this.type = -1;
		this.rid = -1;
	}
	
	public int getType(){
		return this.type;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public String getName(){
		 return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public int getRid(){
		return this.rid;
	}
	
	public void setRid(int rid){
		this.rid = rid;
	}
	
	public String getSmalImg(){
		return this.small_img;
	}
	
	public void setSmallImg(String small_img){
		this.small_img = small_img;
	}
	
	public String getSmallPoster(){
		return this.small_poster;
	}
	
	public void setSmallPoster(String small_poster){
		this.small_poster = small_poster;
	}
	
	public String getBigPoster(){
		return this.big_poster;
	}
	
	public void setBigPoster(String big_poster){
		this.big_poster = big_poster;
	}
	
	public String getDetail(){
		return this.detail;
	}
	
	public void setDetail(String detail){
		this.detail = detail;
	}
	
	public String getUpdateInfo(){
		return this.update_info;
	}
	
	public void setUpdateInfo(String update_info){
		this.update_info = update_info;
	}
	
	public float getGrade(){
		return this.grade;
	}
	
	public void setGrade(float grade){
		this.grade = grade;
	}
	
	@Override
	public String toString() {
		return "type:" + type + "rid:" + rid + "name:" + name + " grade:" + grade + ", small_img" + this.small_img +"small_poster:" + this.small_poster +"big_poster:" + this.big_poster
				+"detail:" + this.detail +"update_info:" + this.update_info;
	}

}
