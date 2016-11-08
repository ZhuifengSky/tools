package com.mailSend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileWrite {
	private Logger logger;
	public  String execute(String str, String dstPath){
		FileWriter writer = null;
		BufferedWriter bufferedWriter = null;
		if (str!=null && dstPath!=null) {
			try {
				writer = new FileWriter(dstPath,true);
				bufferedWriter = new BufferedWriter(writer);
				bufferedWriter.write(str+",");			
			}catch (IOException e) {
				logger.error("�ļ�д����ִ���");
				e.printStackTrace();
			}finally{
				try {
					bufferedWriter.flush();
					bufferedWriter.close();
				} catch (IOException e) {
					logger.error("�ļ�д��رճ��ִ���");
					e.printStackTrace();
				}
				
			}
			return "success";
		}		
		return "faild";		
	}
}
