package com.mailSend;


import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SendMailHasAttachUtil {

    private MimeMessage message;
    private Session session;
    private Transport transport;
    private String mailHost="";
    private int mailPort;
    private String sender_username="";
    private String sender_password="";
    private String smtp_auth = "";
    private Vector file;
    private String nickName;
    private SendMailBean mailBean = this.readConfig();
    private Properties properties = new Properties();
   
    /*
     * ��ʼ������
     */
    public SendMailHasAttachUtil(boolean debug) {
        InputStream in = SendMailHasAttachUtil.class.getResourceAsStream("/mailConfig.properties");
        try {
            properties.load(in);
            this.mailHost = properties.getProperty("mail.smtp.host");
            this.mailPort = Integer.parseInt(properties.getProperty("mail.smtp.port"));
            this.sender_username = properties.getProperty("mail.sender.username");
            this.sender_password = properties.getProperty("mail.sender.password");
            this.smtp_auth = properties.getProperty("mail.smtp.auth");
            this.nickName = mailBean.getNickName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Authenticator auth = new MyAuthenticator(sender_username, sender_password);
        session = Session.getDefaultInstance(properties, auth);
        session.setDebug(debug);//�������е�����Ϣ
        message = new MimeMessage(session);
    }

    /**
     * �����ʼ�
     *
     * @param subject
     *            �ʼ�����
     * @param sendHtml
     *            �ʼ�����
     * @param receiveUser
     *            �ռ��˵�ַ
     */
    public boolean doSendHtmlEmail(String subject, String sendHtml,String fileName,String receiveUser) {
    	try {
            // ������
            //InternetAddress from = new InternetAddress(sender_username);
            // ������������÷����˵�Nick name
            InternetAddress from= new InternetAddress(MimeUtility.encodeWord(nickName)+" <"+sender_username+">");
            message.setFrom(from);
            // �ռ���
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);//��������CC��BCC
            // ������
            //message.setRecipient(Message.RecipientType.CC, new InternetAddress("13752381963@163.com"));
            // ������
            //message.setRecipient(Message.RecipientType.BCC, new InternetAddress("zhangwu@yy.com"));
            // �ʼ�����
            message.setSubject(subject);
            String content = sendHtml.toString();
            // �ʼ�����,Ҳ����ʹ���ı�"text/plain"
            message.setContent(content, "text/html;charset=UTF-8");
            file = new Vector();
            file.addElement(fileName);
            Multipart mp = new MimeMultipart();
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setContent(content.toString(), "text/html;charset=gb2312");
            mp.addBodyPart(mbp);
            if(!file.isEmpty()){//�и���
                Enumeration efile=file.elements();
                while(efile.hasMoreElements()){
                    mbp=new MimeBodyPart();
                    fileName=efile.nextElement().toString(); //ѡ���ÿһ��������
                    FileDataSource fds=new FileDataSource(fileName); //�õ�����Դ
                    mbp.setDataHandler(new DataHandler(fds)); //�õ�������������BodyPart
                    mbp.setFileName(fds.getName());  //�õ��ļ���ͬ������BodyPart
                    mp.addBodyPart(mbp);
                }
                file.removeAllElements();
                message.setContent(mp); //Multipart���뵽�ż�
                message.setSentDate(new Date());     //�����ż�ͷ�ķ�������
                // �����ʼ�
                message.saveChanges();
                transport = session.getTransport("smtp");
                // smtp��֤���������������ʼ��������û�������
                transport.connect(mailHost,mailPort, sender_username, sender_password);
                // ����
                transport.sendMessage(message, message.getAllRecipients());
                return true;
	        }else{
	            	return false;
	        } 
        } catch (AddressException e) {
        	   System.out.println(receiveUser+"��ַ�����쳣!");
        	   return false;
		}catch (UnsupportedEncodingException e1) {
			   return false;
		}catch (AuthenticationFailedException e2) {
			System.out.println("��������������У�����!");
			return false;
		}catch (IOException e3) {
			System.out.println(receiveUser+"--����δ�ҵ�!");
			   return false;
		}catch (MessagingException e4) {
			System.out.println(receiveUser+"--����δ�ҵ�!");
			   return false;
		}finally {
			    if(transport!=null){
			        try {
			            transport.close();
			        } catch (MessagingException e) {
			            e.printStackTrace();
			            System.out.println("�����쳣!");
			        }
			    }
		}               
   }

public void sendUserFile(String subject,String content,String userNameStr){ 
    	InputStream in = SendMailHasAttachUtil.class.getClassLoader().getResourceAsStream("mailConfig.properties");
         try {
             properties.load(in);
             String filePath = properties.getProperty("send.file.path");
             String exportFilePath = properties.getProperty("exportFile.path");
             String emailSuffix = properties.getProperty("email.suffix");
             String fileSuffix = properties.getProperty("send.file.suffix");
        
    	 String[] userNames = userNameStr.split(",");
    	 SendMailHasAttachUtil se = new SendMailHasAttachUtil(false);
    	 FileWrite fileWrite = new FileWrite();
    	 fileWrite.execute("\r\n", exportFilePath+DateUtil.format(new Date(), DateUtil.DATE)+"_faildUser.txt");
		 fileWrite.execute("\r\n", exportFilePath+DateUtil.format(new Date(), DateUtil.DATE)+"_successUser.txt");
    	 fileWrite.execute("-------"+DateUtil.format(new Date(), DateUtil.BOTH)+"�ʼ�����-----------\r\n ʧ����Ա:", exportFilePath+DateUtil.format(new Date(), DateUtil.DATE)+"_faildUser.txt");
		 fileWrite.execute("-------"+DateUtil.format(new Date(), DateUtil.BOTH)+"�ʼ�����-----------\r\n �ɹ���Ա:", exportFilePath+DateUtil.format(new Date(), DateUtil.DATE)+"_successUser.txt");
    	 for (int i = 0; i < userNames.length; i++) {
			String userName = userNames[i];
			content = content.replaceAll("#user#",userName);
			content = content.replaceAll("#tab#","&emsp;");
			content = content.replaceAll("#enter#","<br>");
			content = content.replaceAll("#space#","&nbsp;");
				
			boolean s = se.doSendHtmlEmail(subject, content,filePath+userName+fileSuffix, userName+emailSuffix);
			if (!s) {
				fileWrite.execute(userName, exportFilePath+DateUtil.format(new Date(), DateUtil.DATE)+"_faildUser.txt");
			}else{
				fileWrite.execute(userName, exportFilePath+DateUtil.format(new Date(), DateUtil.DATE)+"_successUser.txt");
			}
		   }
         } catch (IOException e) {
             e.printStackTrace();
             System.out.println("��Դ�ļ���ȡ���ִ���!");
         }
    }

    public static SendMailBean readConfig(){
    	SendMailBean mailBean = null;
    	File configFile = new File("sendConfig.xml");
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(configFile);
			Element query = doc.getRootElement().element("mail");
			mailBean = new SendMailBean(query.elementText("nickName"),query.elementText("subject"),query.elementText("content"),query.elementText("recieveUsers"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mailBean;
    }
}
