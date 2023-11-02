package name.parsak;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class FaceTrack {

	//private static Scalar colorRed = new Scalar(255,0,0);
	private static Scalar colorGreen = new Scalar(0,255,0);
	//private static Scalar colorBlue = new Scalar(0,0,255);

	private static Mat frame;
	private static Mat grayframe;
	//private static final String haarcascade = "haarcascade_frontalface_alt.xml";
	private static final String haarcascade = "haarcascade_frontalface_improved.xml";
	
	private static boolean loaded = false;
	
	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		CascadeClassifier cascade = new CascadeClassifier();
		
		try {
			ClassLoader classLoader = FaceTrack.class.getClassLoader();
			File file = new File(classLoader.getResource(haarcascade).getFile());
			
			if (cascade.load(file.getAbsolutePath())) {
				loaded = true;
			} else {
				System.out.println("Not Loaded");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame jframe = new JFrame("Face Track");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jframe.setSize(100, 100);
		JLabel videoone = new JLabel();
		JLabel videotwo = new JLabel();
		
		JPanel jPanel = new JPanel();
		jPanel.add(videoone);
		jPanel.add(videotwo);
		jframe.add(jPanel);
		jframe.setVisible(true);
		
		VideoCapture camera = new VideoCapture(0);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
        	@Override
        	public void run() {
        		camera.release();
        	}
        });
		
		if (!camera.isOpened()) {
			System.out.println("Camera is not open");
		} else  {
			frame = new Mat();
			grayframe = new Mat();
			
			if (camera.read(frame)) {
				jframe.setSize(frame.width() + 50, frame.height() + 50);
				while (true) {
					
					// Try to read camera
					boolean read_camera = false;
					try {read_camera = camera.read(frame);} catch (Exception e) {}
					
					if (read_camera) {
						frame = ColorBGR(frame);
						grayframe = ColorGray(frame);
						
						// Detect faces in grayscale
						if (loaded) {
								MatOfRect faceDetections = new MatOfRect();
								cascade.detectMultiScale(grayframe, faceDetections);
								for (Rect rect : faceDetections.toArray()) {
									DrawRectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),colorGreen);
									//System.out.println(rect.x +" "+ rect.y);
								}
						}
						
						// Display frame
						videoone.setIcon(new ImageIcon(mat2Img(frame)));
					 }
				}
			}
		}
	}
	/**************************************************************************************************
	*/
	public static Mat Sobel(Mat source) {
		Mat result = new Mat();
		Imgproc.Sobel(source, result, source.depth(), 2, 1);
		return result;
	}
	/**************************************************************************************************
	* Draw Circle
	*/
	public static void DrawCircle(Mat source, Point center, int radius, Scalar color) {
		Imgproc.circle(source, center, radius, color);
	}
	/**************************************************************************************************
	* Draw Circle
	*/
	public static void DrawRectangle(Mat source, Point pt1, Point pt2, Scalar color) {
		Imgproc.rectangle(source, pt1, pt2, color);
	}
	/**************************************************************************************************
	 * Draw Line
	 */
	public static void DrawLine(Mat source, Point pt1, Point pt2, Scalar color) {
		Imgproc.line(source,pt1,pt2,color);
	}
	public static void DrawArrow(Mat source, Point pt1, Point pt2, Scalar color) {
		Imgproc.arrowedLine(source,pt1,pt2,color);
	}
	/**************************************************************************************************
	 * Change Color
	 */
	public static Mat ChangeColor(Mat source, int code) {
		Mat result = new Mat();
		Imgproc.cvtColor(source, result, code);
		return result;
	}
	public static Mat ColorBGR(Mat source)  {return ChangeColor(source,Imgproc.COLOR_RGB2BGR );}
	public static Mat ColorGray(Mat source) {return ChangeColor(source,Imgproc.COLOR_RGB2GRAY );}
	public static Mat ColorLab(Mat source)  {return ChangeColor(source,Imgproc.COLOR_RGB2Lab );}
	public static Mat ColorLuv(Mat source)  {return ChangeColor(source,Imgproc.COLOR_RGB2Luv );}
	public static Mat ColorYUV(Mat source)  {return ChangeColor(source,Imgproc.COLOR_RGB2YUV );}
	public static Mat ColorHSV(Mat source)  {return ChangeColor(source,Imgproc.COLOR_RGB2HSV );}	
	public static Mat ColorXYZ(Mat source)  {return ChangeColor(source,Imgproc.COLOR_RGB2XYZ );}

	/**************************************************************************************************
	 * Resize
	 */	
	public static Mat Resize(Mat source, Size size) {
		Mat result = new Mat();
		Imgproc.resize(source, result, size);
		return result;
	}
	public static Mat Resize(Mat source, int width, int height) {
		return Resize(source, new Size(width,height));
	}

	/**************************************************************************************************
	 * Mat To Buffered Image
	 */
	public static BufferedImage mat2Img(Mat in)
	{
	    BufferedImage out;
	    byte[] data = new byte[in.width() * in.height() * (int)in.elemSize()];
	    int type;
	    in.get(0, 0, data);
	    if(in.channels() == 1) type = BufferedImage.TYPE_BYTE_GRAY;
	    else	type = BufferedImage.TYPE_3BYTE_BGR;
	    out = new BufferedImage(in.width(), in.height(), type);
	    out.getRaster().setDataElements(0, 0, in.width(), in.height(), data);
	    return out;
	} 
	/**************************************************************************************************
	*/
}
