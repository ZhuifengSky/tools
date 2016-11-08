package com.mailSend;

public class SendMailBean {

	private String nickName;
	private String subject;
	private String content;
	private String recieveUsers;
	
	public SendMailBean() {
		// TODO Auto-generated constructor stub
	}
	public SendMailBean(String nickName, String subject, String content,
			String recieveUsers) {
		super();
		this.nickName = nickName;
		this.subject = subject;
		this.content = content;
		this.recieveUsers = recieveUsers;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRecieveUsers() {
		return recieveUsers;
	}
	public void setRecieveUsers(String recieveUsers) {
		this.recieveUsers = recieveUsers;
	}
	
	
}
