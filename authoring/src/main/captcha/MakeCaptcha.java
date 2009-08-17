/*
 * Scantegrity - Successor to Punchscan, a High Integrity Voting System
 * Copyright (C) 2006  Richard Carback, David Chaum, Jeremy Clark, 
 * Aleks Essex, Stefan Popoveniuc, and Jeremy Robin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.scantegrity.authoring.captcha;

import java.io.FileOutputStream;
import java.util.Properties;

import nl.captcha.servlet.CaptchaProducer;
import nl.captcha.servlet.DefaultCaptchaIml;
import nl.captcha.text.TextProducer;
import nl.captcha.text.imp.DefaultTextCreator;

import org.scantegrity.common.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MakeCaptcha {
	
	public final static String SIMPLE_CAPCHA_TEXTPRODUCER = "cap.text.producer";
	public final static String SIMPLE_CAPTCHA_TEXTPRODUCER_CHARR = "cap.char.arr";
	public final static String SIMPLE_CAPTCHA_TEXTPRODUCER_CHARRL = "cap.char.arr.l";
	public final static String SIMPLE_CAPTCHA_TEXTPRODUCER_FONTA = "cap.font.arr";
	public final static String SIMPLE_CAPTCHA_TEXTPRODUCER_FONTS = "cap.font.size";
	public final static String SIMPLE_CAPTCHA_TEXTPRODUCER_FONTC = "cap.font.color";
	
	
	public final static String SIMPLE_CAPTCHA_PRODUCER = "cap.producer";
	public final static String SIMPLE_CAPTCHA_OBSCURIFICATOR = "cap.obscurificator";
	public final static String SIMPLE_CAPTCHA_BOX = "cap.border";
	public final static String SIMPLE_CAPTCHA_BOX_C = "cap.border.c";
	public final static String SIMPLE_CAPTCHA_BOX_TH = "cap.border.th";

	
	private Properties props = null;
	private TextProducer textProducer = null;
	private CaptchaProducer captcha = null;
	
	private CaptchaProducer p=null;
	
	public static String ConfigFile="D:/kit/simplecaptcha/simplecaptchawar_src/Copy of captchaWar/WebContent/WEB-INF/web.xml";
	
	public MakeCaptcha(String fileToParse) throws Exception {
		props = new Properties();
		
		Document doc=Util.DomParse(fileToParse);
		NodeList params=doc.getElementsByTagName("init-param");
		for (int i=0;i<params.getLength();i++) {
			Node param=params.item(i);
			String key = null;
			String value = null;
			for (Node child=param.getFirstChild();child!=null;child=child.getNextSibling()) {
				if (child.getNodeName().compareTo("param-name")==0) {
					key=child.getFirstChild().getNodeValue();
				}
				if (child.getNodeName().compareTo("param-value")==0) {
					value=child.getFirstChild().getNodeValue();
				}
			}
			//System.out.println(key+" "+value);
			props.put(key,value);
		}		
		if (props.containsKey(SIMPLE_CAPCHA_TEXTPRODUCER)) {
			String producer = props.getProperty(SIMPLE_CAPCHA_TEXTPRODUCER);
			if (producer != null && !producer.equals("")) {
				try {
					textProducer = (TextProducer) Class.forName(producer).newInstance();
					textProducer.setProperties(props);
				}catch (Exception e) {}
			}
			if (textProducer == null) {
				textProducer = new DefaultTextCreator();
				textProducer.setProperties(props);
			}
		}
		
		if (props.containsKey(SIMPLE_CAPTCHA_PRODUCER)) {
			String producer = props.getProperty(SIMPLE_CAPTCHA_PRODUCER);
			if (producer != null && !producer.equals("")) {
				try {
					captcha = (CaptchaProducer) Class.forName(producer).newInstance();
					captcha.setProperties(props);
				}catch (Exception e) {}
			}
			if (captcha == null) {
				captcha = new DefaultCaptchaIml(props);
			}
		}
		
		p = new DefaultCaptchaIml(props);
	}
	
	public void make(String text,String file) throws Exception {
		FileOutputStream fos=new FileOutputStream(file);
		p.createImage(fos, text);
		fos.close();		
	}
	
	public static void main(String[] args) throws Exception {
		MakeCaptcha mk=new MakeCaptcha(ConfigFile);
		mk.make("Vora", "StefanCaptcha.jpg");
	}

}