package project;

import java.io.IOException;

import javax.mail.Authenticator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {
	java.util.Properties props = System.getProperties();
	private String from;
	private String to;
	private String filePath;
	String host = "mail.smtp.host";
	private String password;

	public SendMail(String from, String password, String to, String filePath) {
		this.from = from;
		this.password = password;
		this.to = to;
		this.filePath = filePath;
	}

	public void sendMail() {
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		// props.put("mail.smtp.socketFactory.port", "25");
		// props.put("mail.smtp.socketFactory.class",
		// "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		};

		Session session = Session.getInstance(props, auth);

		MimeMessage message = new MimeMessage(session);
		try {
			// attachment for email
			MimeBodyPart bodypart1 = new MimeBodyPart();
			try {
				bodypart1.attachFile(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			MimeBodyPart bodyPart2 = new MimeBodyPart();
			bodyPart2.setText("This is test email from java");

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(bodypart1);
			mp.addBodyPart(bodyPart2);

			message.setFrom(new InternetAddress(from));
			System.out.println("LOG: Set From");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Stolen car report");

			message.setContent(mp);

			System.out.println("LOG: subject set");
			System.out.println("sending email...");
			// message.setText("This is test email from java" );

			Transport.send(message);
			System.out.println("LOG: email sent successfully :)");

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// SendMail mail = new SendMail();
		// mail.sendMail();
	}
}
