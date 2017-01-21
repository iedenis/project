package project;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.IOException;
import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
//AIzaSyCTJuS325EPe5H7PKiwhWHztDKq-8CLYro
public class PDF {
	static String dest = System.getProperty("user.dir")+"/report.pdf";
	static String imPath = "";
	File f = new File(imPath);

	public static void main(String[] args) throws Exception {
		
		
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();
		Font font=new Font(Font.FontFamily.HELVETICA,20);
		imPath=args[0];
		Image im = Image.getInstance(imPath);
		//Image im = Image.getInstance("/home/fox/project/project/src/project/image.jpg");
		im.scaleAbsolute(480, 300);
		Paragraph paragraph = new Paragraph("Hello police!!",font);
		Paragraph paragraph1 = new Paragraph();
		paragraph.setAlignment(Element.ALIGN_CENTER);
		
		paragraph1.add("According to your database this car is stolen. Here are coordinates and the picture of the car");
		JpegGeoTagReader jpegGeoTagReader = new JpegGeoTagReader();
		File photoFile = new File(imPath);
		GeoTag geoTag = jpegGeoTagReader.readMetadata(photoFile);
		//double altitude = geoTag.getAltitude();
		double latitude = geoTag.getLatitude();
		double longitude = geoTag.getLongitude();
		System.out.println("latitude " + latitude + " longitude " + longitude);
		Image map = Image.getInstance(
				"https://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+"&zoom=15&markers=color:blue%size=mid|"+latitude+","+longitude+"&size=400x400&maptype=roadmap18&key=AIzaSyCTJuS325EPe5H7PKiwhWHztDKq-8CLYro");
		document.add(paragraph);
		document.add(paragraph1);

		document.add(im);
		document.add(map);
		document.close();
		
		System.out.println("LOG: report has been created in directory: "+ dest);
		
		SendMail mail=new SendMail("id321582918@gmail.com", "ievlev85", "denis.ievlev@gmail.com",dest);
		mail.sendMail();
	}

}
