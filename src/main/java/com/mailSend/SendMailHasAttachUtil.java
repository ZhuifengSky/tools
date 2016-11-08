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
     * 初始化方法
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
        session.setDebug(debug);//开启后有调试信息
        message = new MimeMessage(session);
    }

    /**
     * 发送邮件
     *
     * @param subject
     *            邮件主题
     * @param sendHtml
     *            邮件内容
     * @param receiveUser
     *            收件人地址
     */
    public boolean doSendHtmlEmail(String subject, String sendHtml,String fileName,String receiveUser) {
    	try {
            // 发件人
            //InternetAddress from = new InternetAddress(sender_username);
            // 下面这个是设置发送人的Nick name
            InternetAddress from= new InternetAddress(MimeUtility.encodeWord(nickName)+" <"+sender_username+">");
            message.setFrom(from);
            // 收件人
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);//还可以有CC、BCC
            // 抄送人
            //message.setRecipient(Message.RecipientType.CC, new InternetAddress("13752381963@163.com"));
            // 暗送人
            //message.setRecipient(Message.RecipientType.BCC, new InternetAddress("zhangwu@yy.com"));
            // 邮件主题
            message.setSubject(subject);
            String content = sendHtml.toString();
            // 邮件内容,也可以使纯文本"text/plain"
            message.setContent(content, "text/html;charset=UTF-8");
            file = new Vector();
            file.addElement(fileName);
            Multipart mp = new MimeMultipart();
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setContent(content.toString(), "text/html;charset=gb2312");
            mp.addBodyPart(mbp);
            if(!file.isEmpty()){//有附件
                Enumeration efile=file.elements();
                while(efile.hasMoreElements()){
                    mbp=new MimeBodyPart();
                    fileName=efile.nextElement().toString(); //选择出每一个附件名
                    FileDataSource fds=new FileDataSource(fileName); //得到数据源
                    mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart
                    mbp.setFileName(fds.getName());  //得到文件名同样至入BodyPart
                    mp.addBodyPart(mbp);
                }
                file.removeAllElements();
                message.setContent(mp); //Multipart加入到信件
                message.setSentDate(new Date());     //设置信件头的发送日期
                // 保存邮件
                message.saveChanges();
                transport = session.getTransport("smtp");
                // smtp验证，就是你用来发邮件的邮箱用户名密码
                transport.connect(mailHost,mailPort, sender_username, sender_password);
                // 发送
                transport.sendMessage(message, message.getAllRecipients());
                return true;
	        }else{
	            	return false;
	        } 
        } catch (AddressException e) {
        	   System.out.println(receiveUser+"地址解析异常!");
        	   return false;
		}catch (UnsupportedEncodingException e1) {
			   return false;
		}catch (AuthenticationFailedException e2) {
			System.out.println("发件人邮箱密码校验出错!");
			return false;
		}catch (IOException e3) {
			System.out.println(receiveUser+"--附件未找到!");
			   return false;
		}catch (MessagingException e4) {
			System.out.println(receiveUser+"--附件未找到!");
			   return false;
		}finally {
			    if(transport!=null){
			        try {
			            transport.close();
			        } catch (MessagingException e) {
			            e.printStackTrace();
			            System.out.println("程序异常!");
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
    	 fileWrite.execute("-------"+DateUtil.format(new Date(), DateUtil.BOTH)+"邮件发送-----------\r\n 失败人员:", exportFilePath+DateUtil.format(new Date(), DateUtil.DATE)+"_faildUser.txt");
		 fileWrite.execute("-------"+DateUtil.format(new Date(), DateUtil.BOTH)+"邮件发送-----------\r\n 成功人员:", exportFilePath+DateUtil.format(new Date(), DateUtil.DATE)+"_successUser.txt");
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
             System.out.println("资源文件读取出现错误!");
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
