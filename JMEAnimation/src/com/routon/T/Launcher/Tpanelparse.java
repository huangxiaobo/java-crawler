package com.routon.T.Launcher;

import java.io.InputStream;
import java.util.List;

public interface Tpanelparse{
	/***解析输入流 得到panel对象集合****/
	public List<Tpanel> parse(InputStream is) throws Exception;

	/***序列化panel对象集合，得到XML形式的字符串***/
	public String serialize(List<Tpanel> panels) throws Exception;
}