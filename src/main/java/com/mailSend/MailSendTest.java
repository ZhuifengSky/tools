package com.mailSend;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MailSendTest {

	public static void main(String[] args) {
		System.out.println("�ʼ����Ϳ�ʼִ��!");
		Properties properties = new Properties();
		SendMailHasAttachUtil sendMailUtil = new SendMailHasAttachUtil(false);
		InputStream in = SendMailHasAttachUtil.class.getResourceAsStream("/mailConfig.properties");
		SendMailBean mailBean = SendMailHasAttachUtil.readConfig();
		try {
            properties.load(in);
            String subject = mailBean.getSubject();
            String content = mailBean.getContent();
            String userNameStr = mailBean.getRecieveUsers();
            sendMailUtil.sendUserFile(subject, content, userNameStr);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("��Դ�ļ���ȡ����!");
        }
        System.out.println("�ʼ�����ִ�н���!");
	}
}
