package com.mailSend;

/**
 * ©уеп╤о
 * @author pc-zw
 *
 */
public class NullJudgeUtil {

	public static boolean isNull(Object object){
		if (object==null || object.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isNotNull(Object object){
		if (object!=null && !object.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	public static void main(String[] args) {
		String str="45719|999|395571598|1473063512";
		String[] s = str.split("\\|");
		System.out.println(s.length);
	}
}
