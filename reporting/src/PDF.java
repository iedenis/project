import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Denis Ievlev
 * @author Samer Hadeed
 *
 * Class PDF represents creating PDF file.<br>
 * This is the main class
 */

public class PDF {
    static String dest = System.getProperty("user.dir") + "/report.pdf";
    static String imPath = "";
    File f = new File(imPath);

    public static void main(String[] args) throws Exception {


        Document document = new Document();
      PdfWriter writer= PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        Font font = new Font(Font.FontFamily.HELVETICA, 20);
        imPath = args[0];
        Image im = Image.getInstance(imPath);
        //String backgroundImagePath=System.getProperty("user.dir"));
        Image backgroundImage = Image.getInstance("background.jpg");
       // PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
        PdfContentByte canvas = writer.getDirectContentUnder();
        canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0.6f);
        canvas.setGState(state);
        backgroundImage.scaleAbsolute(PageSize.A4);
        backgroundImage.setAbsolutePosition(0,0);
        canvas.addImage(backgroundImage);
        canvas.restoreState();


        im.scaleAbsolute(480, 300);
        Paragraph paragraph = new Paragraph("Hello police!!", font);
        Paragraph paragraph1 = new Paragraph();

        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph1.add("*********************************************************************************************************\n");
        paragraph1.add("According to your database this car is stolen. Here are coordinates and the picture of the car");

        // parsing an image metadata to get GPS coordinates
        javaxt.io.Image image = new javaxt.io.Image(imPath);
        double[] gps = image.getGPSCoordinate();
        System.out.print("Coordinates " + gps[0] + " " + gps[1]);
        double latitude = gps[1];
        double longitude = gps[0];

        // Using Google maps API to see the location where the image had been taken
        Image map = Image.getInstance(
                "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude +
                        "&zoom=15&markers=color:blue%size=mid|" + latitude + "," + longitude +
                        "&size=480x300&maptype=roadmap18&key=AIzaSyCTJuS325EPe5H7PKiwhWHztDKq-8CLYro");

        map.scaleAbsolute(480, 300);
        document.add(paragraph);
        document.add(paragraph1);

        document.add(im);
        document.add(map);
        document.close();

        System.out.println("LOG: report has been created in directory: " + dest);

        SendMail mail = new SendMail("id321582918@gmail.com", "ievlev85", "denis.ievlev@gmail.com", dest);
        mail.sendMail();
    }

}
