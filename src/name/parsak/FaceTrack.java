package name.parsak;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lwjgl.Version;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class FaceTrack {

	public static AtomicReference<Float> xNorm = new AtomicReference<>(0.0f);
	public static AtomicReference<Float> yNorm = new AtomicReference<>(0.0f);


	//private static Scalar colorRed = new Scalar(255,0,0);
	private static Scalar colorGreen = new Scalar(0,255,0);
	//private static Scalar colorBlue = new Scalar(0,0,255);

	private static Mat frame;
	private static Mat grayframe;
	//private static final String haarcascade = "haarcascade_frontalface_alt.xml";
	private static final String haarcascade = "haarcascade_frontalface_improved.xml";
	
	private static boolean loaded = false;
	
	public static void main(String[] args) {

		//new FaceTrack().run();
		new Thread(new Runnable() {
			@Override
			public void run() {
				runFaceTracking();
			}
		}).start();



	}


	public static void runFaceTracking(){

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
								//System.out.println(rect.x/frame.size().width +" "+ rect.y/frame.size().height);
								xNorm.updateAndGet(n -> (float) ((rect.x+rect.width/2)/frame.size().width)-0.5f);
								yNorm.updateAndGet(n -> (float) ((rect.y+rect.height/3)/frame.size().height)-0.5f);

							}
						}

						// Display frame
						videoone.setIcon(new ImageIcon(mat2Img(frame)));
					}
				}
			}
		}
	}

	// The window handle
	private long window;


	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
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
