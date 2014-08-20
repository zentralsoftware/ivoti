package com.c1info;

import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.imgscalr.Scalr;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ImageGridPanel;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

import com.c1info.io.Find.Finder;

public class MyFrame extends JFrame implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1706929608603987262L;
	private JPanel contentPane;
	private JComboBox<String> algo;
	private JSpinner angle;
	private JSpinner pixel;
	private JScrollPane scrollPane;
	private List<Path> inputFiles = new ArrayList<Path>();
	private Path startingDir = Paths.get("src/test/resources/cropTest");
	private String pattern = "*04_6*.jpg";	
	
	private JPanel imagePanel;
	private JSpinner scale;
	private JLabel lblAngle;
	private JLabel lblPixel;
	private JLabel lblAlgo;
	private JLabel lblScale;
	private JSpinner brightness;
	private JLabel lblNewLabel;
	private JSpinner contrast;
	private JLabel lblContrast;
	private JSpinner clusterSize;
	
	private SpinnerModel angleModel;
	private SpinnerNumberModel pixelModel;
	private SpinnerNumberModel scaleModel;
	private SpinnerNumberModel brightnessModel;
	private SpinnerNumberModel contrastModel;
	private SpinnerNumberModel clusterSizeModel;
	
	private int scaleValue = 1;
	private int angleValue = 1;
	private int pixelValue = 1;
	private double brightnessValue = 1d;
	private double contrastValue = 1d;
	private int clusterSizeValue = 1;
	private JLabel lblClusterSize;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFrame frame = new MyFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public MyFrame() throws IOException {
		setTitle("C1");
		init();
	}

	protected void init() throws IOException
	{
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		inputFiles = finder.getFoundFiles();	
		initGui();
		initValues();
		loadAndProcess();
	}
	
	private void initValues() {
		angleValue = (int)angleModel.getValue();
		pixelValue = (int)pixelModel.getValue();		
		scaleValue = (int)scaleModel.getValue();
		brightnessValue = (double)brightnessModel.getValue();
		contrastValue = (double)contrastModel.getValue();
		clusterSizeValue = (int)clusterSizeModel.getValue();
	}

	protected void initGui()
	{
		initGuiModel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 856, 432);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
				
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblAlgo = new JLabel("Algo");
		panel.add(lblAlgo);
		
		algo = new JComboBox<String>();
		lblAlgo.setLabelFor(algo);
		algo.setModel(new DefaultComboBoxModel<String>(new String[] {"Polygon", "Ellipse"}));
		panel.add(algo);
		
		lblAngle = new JLabel("Angle");
		panel.add(lblAngle);
		
		angle = new JSpinner(angleModel);
		angle.addChangeListener(this);
		lblAngle.setLabelFor(angle);
		panel.add(angle);
		
		lblPixel = new JLabel("Pixel");
		panel.add(lblPixel);
		
		pixel = new JSpinner(pixelModel);
		pixel.addChangeListener(this);
		lblPixel.setLabelFor(pixel);
		panel.add(pixel);
		
		lblScale = new JLabel("Scale");
		panel.add(lblScale);
		
		scale = new JSpinner(scaleModel);
		scale.addChangeListener(this);
		lblScale.setLabelFor(scale);
		panel.add(scale);
		
		lblNewLabel = new JLabel("Brightness");
		panel.add(lblNewLabel);
		
		brightness = new JSpinner(brightnessModel);
		brightness.addChangeListener(this);
		panel.add(brightness);
		
		lblContrast = new JLabel("Contrast");
		panel.add(lblContrast);
		
		contrast = new JSpinner(contrastModel);
		contrast.addChangeListener(this);
		panel.add(contrast);
		
		lblClusterSize = new JLabel("Cluster Size");
		panel.add(lblClusterSize);
		
		clusterSize = new JSpinner(clusterSizeModel);
		clusterSize.addChangeListener(this);
		panel.add(clusterSize);

		imagePanel = new JPanel();
		imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
		
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(1200, 200));
		contentPane.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(imagePanel);

	}
	
	protected void initGuiModel()
	{
		angleModel = new SpinnerNumberModel(5, 1, 90, 1);
		pixelModel = new SpinnerNumberModel(2, 1, 100, 1);
		scaleModel = new SpinnerNumberModel(2, 1, 10, 1);
		brightnessModel = new SpinnerNumberModel(3f, 0.1f, 20f, 0.1f);
		contrastModel = new SpinnerNumberModel(1.1f, 0.1, 10f, 0.1f);
		clusterSizeModel = new SpinnerNumberModel(5, 5, 100, 5);
	}
	
	protected void resetContent()
	{
		imagePanel = new JPanel();
		imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(imagePanel);
	}
	
	protected void loadAndProcess() throws IOException
	{
		for (int i=0;i<inputFiles.size();i++)
		{
			Path file = inputFiles.get(i);
			processContour(file.toFile().getAbsolutePath());			
		}	
	}

	protected void processContour(String absolutePath) throws IOException
	{
		BufferedImage image = UtilImageIO.loadImage(absolutePath);
		Snippet snippet = Snippet.getInstance(image, scaleValue, pixelValue, angleValue, contrastValue, brightnessValue);
		printHistogram(snippet);
		BufferedImage boundImg = Util.duplicate(snippet.getScaled());		
		visualize(image, snippet.getScaled(), snippet.getBw(), snippet.getVisualBinary(), boundImg,
				snippet);				
	}

	private void visualize(BufferedImage image, BufferedImage scaledImage,
			BufferedImage blacknwhite, BufferedImage visualBinary,
			BufferedImage boundImg, Snippet snippet) {
		ImageGridPanel gui = new ImageGridPanel(1,5);
		gui.setImage(0,0,image);
		gui.setImage(0,1,scaledImage);
		gui.setImage(0,2,blacknwhite);
		gui.setImage(0,3,visualBinary);		
		gui.setImage(0,4,boundImg);		
		gui.setPreferredSize(new Dimension(scaleValue*visualBinary.getWidth(), visualBinary.getHeight()));	
		imagePanel.add(gui);
		
		Color[] colors = new Color[]{Color.BLUE, Color.GREEN, Color.PINK, Color.MAGENTA};
		Graphics2D g1 = gui.getImage(0,3).createGraphics();
		g1.setStroke(new BasicStroke(3f));				
		int colorIndex = 0;
		for(Bounding b: snippet.getBoundings())
		{
			g1.setColor(colors[colorIndex%4]);
			VisualizeShapes.drawPolygon(b.getExternalVertexes(), true, g1);			
			g1.setColor(Color.YELLOW);
			for (List<PointIndex_I32> vertexes : b.getInternalVertexes())
			{
				VisualizeShapes.drawPolygon(vertexes,true,g1);
			}
			colorIndex++;
		}
				
		colorIndex = 0;
		Graphics2D g2 = gui.getImage(0,4).createGraphics();
		g2.setStroke(new BasicStroke(3f));				
		Histogram histogram = new Histogram(snippet, clusterSizeValue);		
		Map<Double, List<Bounding>> data = histogram.getData();
		for(Map.Entry<Double, List<Bounding>> entry:data.entrySet())
		{
			List<Bounding> boundings = entry.getValue();
			Color color = Util.getRandomColor();
			for (Bounding bounding:boundings)
			{
				if (bounding.getMeanIntensity() < 255.0d)
				{
					List<Point2D_I32> points = getPointIntFromRect(bounding.getRectangle());
					g2.setColor(color);
					VisualizeShapes.drawPolygon(points,true,g2);
					drawPoints(g2, bounding.getRectangle());
				}
			}
		}							
	}
	
	private void drawPoints(Graphics2D g2, Rectangle rectangle)
	{
		Point p0 = rectangle.p0;
		g2.setColor(Color.cyan);
		drawPoint(g2,p0);
		Point p1 = rectangle.p1;
		g2.setColor(Color.magenta);
		drawPoint(g2,p1);
		Point p2 = rectangle.p2;
		g2.setColor(Color.red);
		drawPoint(g2,p2);
		Point p3 = rectangle.p3;
		g2.setColor(Color.green);
		drawPoint(g2,p3);
	}
	
	private void drawPoint(Graphics2D g2, Point point)
	{
		g2.drawOval(point.x, point.y, 4, 4);
	}	
	
	protected double getRectangleThreshold(BufferedImage image, Rectangle rectangle)
	{
		Polygon p = new Polygon();
		p.addPoint(rectangle.p0.x, rectangle.p0.y);
		p.addPoint(rectangle.p1.x, rectangle.p1.y);
		p.addPoint(rectangle.p2.x, rectangle.p2.y);
		p.addPoint(rectangle.p3.x, rectangle.p3.y);		
		
		return 0;
	}
	
	protected List<Point2D_I32> getPointIntFromRect(Rectangle rectangle)
	{
		List<Point2D_I32> list = new ArrayList<Point2D_I32>();
		List<Point> points = rectangle.getPoints();
		for (Point p:points)
		{
			Point2D_I32 point = new Point2D_I32(p.x, p.y);
			list.add(point);
		}
		return list;
	}
	
	private void printHistogram(Snippet snippet)
	{
		Histogram histogram = new Histogram(snippet, clusterSizeValue);		
		Map<Double, List<Bounding>> data = histogram.getData();
		System.err.println("step: " + histogram.getStep());
		System.err.println("mean: " + histogram.mean());
		System.err.println("min: " + histogram.getMin());
		System.err.println("max: " + histogram.getMax());
		SortedSet<Double> keys = new TreeSet<Double>(data.keySet());
		for(Double key:keys)
		{
			System.err.println(key + " : " + data.get(key).size());
			List<Bounding> value = data.get(key);
			for (Bounding bounding:value)
			{
				System.err.println(bounding.getRectangle() + "," + bounding.getMeanIntensity() + ",");
			}
		}		

	}
	
	protected List<Point2D_I32> getIntInstances(List<Point2D_F64> points)
	{
		List<Point2D_I32> list = new ArrayList<Point2D_I32>();
		for (int i=0;i<points.size();i++)
		{
			Point2D_F64 orig = points.get(i);
			Point2D_I32 p = new Point2D_I32((int)orig.x, (int)orig.y);
			list.add(p);
		}
		return list;
	}
	
	protected List<Point2D_F64> getInstances(List<PointIndex_I32> points)
	{
		List<Point2D_F64> list = new ArrayList<Point2D_F64>();
		for (PointIndex_I32 point:points)
		{
			list.add(getInstance(point));
		}
		return list;
	}
	
	protected Point2D_F64 getInstance(PointIndex_I32 point)
	{
		Point2D_F64 p = new Point2D_F64(point.getX(), point.getY());
		return p;
	}
	
	protected BufferedImage convert(BufferedImage image) throws IOException
	{			
		RescaleOp rescaleOp = new RescaleOp((float)contrastValue, (float)brightnessValue, null);
		rescaleOp.filter(image, image);		
		ImageUInt8 binary = getBinary(image);
		BufferedImage visualBinary = VisualizeBinaryData.renderBinary(binary, null);
		return visualBinary;		
	}	
	
	protected ImageUInt8 getBinary(BufferedImage image) throws IOException
	{					
		// convert into a usable format
		ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
		ImageUInt8 binary = new ImageUInt8(input.width,input.height);
		ImageUInt8 filtered = new ImageUInt8(input.width,input.height);

		// the mean pixel value is often a reasonable threshold when creating a binary image
		double mean = ImageStatistics.mean(input);

		// create a binary image by thresholding
		ThresholdImageOps.threshold(input,binary,(float)mean,true);

		// reduce noise with some filtering
		BinaryImageOps.removePointNoise(binary, filtered);		
		return filtered;
		
	}		
	
	public BufferedImage scaled(BufferedImage orig, int scale)
	{
		if (scale > 1)
		{
			BufferedImage after = Scalr.resize(orig, scale*orig.getWidth(), scale*orig.getHeight(), Scalr.OP_GRAYSCALE);
			return after;
		} else
		{
			return orig;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(scale))
		{
			scaleValue = (int)scaleModel.getValue();
		} else if (e.getSource().equals(angle))
		{
			angleValue = (int)angleModel.getValue();
		} else if (e.getSource().equals(pixel))
		{
			pixelValue = (int)pixelModel.getValue();
		} else if (e.getSource().equals(brightness))
		{
			brightnessValue = (double)brightnessModel.getValue();
		} else if (e.getSource().equals(contrast))
		{
			contrastValue = (double)contrastModel.getValue();
		} else if (e.getSource().equals(clusterSize))
		{
			clusterSizeValue = (int)clusterSizeModel.getValue();
		}  else
		{
			return;
		}
		
		try {
			resetContent();
			loadAndProcess();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
