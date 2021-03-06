import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;

public class SendMail {
    java.util.Properties props = System.getProperties();
    String host = "mail.smtp.host";
    private String from;
    private String to;
    private String filePath;
    private String password;

    /**
     * Constructor
     * @param from email of sending address
     * @param password The email password
     * @param to email of destination address
     * @param filePath The path of the included file
     */
    public SendMail(String from, String password, String to, String filePath) {
        this.from = from;
        this.password = password;
        this.to = to;
        this.filePath = filePath;
    }

    /**
     * Method for sending an email.
     */
    public void sendMail() {
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
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
            bodyPart2.setText("This automatic email was sent from Automatic Report System program");

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
}
