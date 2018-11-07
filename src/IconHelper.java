import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IconHelper {
    /**
     * Given a url to an image file, sets the window icon to that image. (I was learning JFrame)
     * @param urlString url to image file
     */
    public static void setIconImageFromUrl(JFrame frame, String urlString) {
        try {
            frame.setIconImage(getBufferedImageFromUrl(urlString));
        } catch (MalformedURLException error) {
            System.out.println("Malformed URL, check formatting/n" + error);
        } catch (IOException error) {
            System.out.println("Connection error. Failed to download window icon from internet. Check your connection or image url.");
        }
    }

    /**
     * Given a url to an image file, returns a BufferedImage of that file
     * @param urlString url to image file
     * @return BufferedImage of image file
     * @throws MalformedURLException url format is incorrect
     * @throws IOException Connection issue. Either no connection or incorrect url.
     */
    private static BufferedImage getBufferedImageFromUrl(String urlString) throws MalformedURLException, IOException {
        URL imageUrl = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:37.0) Gecko/20100101 Firefox/37.0");
        return ImageIO.read(connection.getInputStream());
    }
}
