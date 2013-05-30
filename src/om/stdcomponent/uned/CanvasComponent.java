package om.stdcomponent.uned;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.graph.GraphFormatException;
import om.graph.uned.GraphFormatRuntimeException;
import om.graph.uned.World;
import om.helper.uned.AnswerChecking;
import om.helper.uned.Tester;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

import org.w3c.dom.Element;

import util.xml.XML;
import util.xml.XMLException;

// UNED: 24-07-2011 - dballestin
/**
 * Represents a Java BufferedImage so that you can draw graphics from Java code.
 * Also includes graph support, but you're perfectly well able to use it for
 * drawing your own graphics too.<br/><br/>
 * Tests can be defined with properties. They include answer's component id (answerid), 
 * minimum number of attempts needed (attemptsmin), maximum number of attempts allowed (attemptsmax)
 * and some other tests defined with test property:<br/><br/>
 * <h2>Tests</h2>
 * <table border="1">
 * <tr><th>Test</th><th>Description</th></tr>
 * <tr><td>true</td><td><b>(default)</b> Always pass the test</td></tr>
 * <tr><td>right</td><td>User selected right answer</td></tr>
 * <tr><td>notright</td><td>User not selected right answer (selected wrong answer or passed)</td></tr>
 * <tr><td>wrong</td><td>User selected wrong answer</td></tr>
 * <tr><td>notwrong</td><td>User not selected wrong answer (selected right answer or passed)</td></tr>
 * <tr><td>passed</td><td>User passed the question</td></tr>
 * <tr><td>notpassed</td><td>User not passed the question (selected right or wrong answer)</td></tr>
 * <tr><td>random</td><td>Allows testing value of a random component</td></tr>
 * <tr><td>numcmp</td><td>Allows testing numeric comparisons</td></tr>
 * </table>
 * <br/>
 * <h2>XML usage</h2>
 * <pre>&lt;canvas alt="Fancy graph" width="200" height="200"&gt;
 *  &lt;world id='w1' px='10' py='10' pw='180' ph='180'
 *    xleft='-1.0' xright='1.0' ytop='1.0' ybottom='-1.0'&gt;
 *   &lt;xAxis ticks='0.2,0.1' numbers='0.5' omitNumbers='0.0' tickSide='both'/&gt;
 *   &lt;yAxis ticks='0.2,0.1' numbers='0.5' omitNumbers='0.0' tickSide='both'/&gt;
 *  &lt;/world&gt;
 *  &lt;marker x='10' y='10'/&gt;
 *  &lt;marker x='30' y='10' labelJS='label=x+","+y;' world='w1'/&gt;
 *  &lt;markerline from='0' to='1' labelJS='label=x1+','+y1;' world='w1'/&gt;
 * &lt;/canvas&gt;</pre>
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from
 * output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates interactive
 * features</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>alt</td><td>(string)</td><td>Alternative text for those who can't read
 * the bitmap</td></tr>
 * <tr><td>width</td><td>(int)</td><td>Width in pixels</td></tr>
 * <tr><td>height</td><td>(int)</td><td>Height in pixels</td></tr>
 * <tr><td>type</td><td>png / jpg</td><td>Compression format used for output
 * (JPG for photos, otherwise PNG)</td></tr>
 * <tr><td>antialias</td><td>(boolean)</td><td>Whether to anti-alias text and
 * graphics (default true)</td></tr>
 * <tr><td>filePath</td><td>(string)</td><td>Optional path to image file
 * (relative to class) to initialise canvas with</td></tr>
 * <tr><td>requirebg</td><td>(boolean)</td><td>If true, includes background image
 * even if question is being viewed in 'fixed colour' mode; otherwise does not.
 * Setting requirebg='yes' also makes any graph use normal colours even if the
 * question is in fixed-colour accessibility mode; be aware that this option
 * is bad for accessibility.
 * </td></tr>
 * <tr><td>markerimage</td><td>(string)</td><td>If specified, use a custom marker image
 * instead of the standard cross-hairs.
 * If you specify markerimage="arrow", you must supply images arrow15.gif, arrow23.gif,
 * arrow31.gif, arrow15d.gif, arrow23d.gif, arrow31d.gif. These must be square images
 * with size 15, 23 and 31 pixels respectively. The 'd' images are for the disabled state.
 * </td></tr>
 * <tr><td>markerhotspot</td><td>(string)</td><td>Specify the position of the
 * marker hotspot within the image, in the form x,y|x,y|x,y, for the three sizes.
 * If you omit this, the hotspot is in the centre of the image, that is 8,8|11,11|15,15.
 * </td></tr>
 * <tr><td>test</td><td>(string)</td><td>Specifies the test for display testing (see <b>Tests</b>).
 * </td></tr>
 * <tr><td>answer</td><td>(string)</td><td>Specifies the answer's components for display testing.
 * <br/><br/> 
 * This is a list with answer's component ids separated by comma ',' (OR operator) or plus symbol '+' 
 * (AND operator).<br/><br/>
 * Moreover answer's component ids can include a selector using with the format
 * "id[selector]" used in some answer's components (e.g. editfields) to specify a single answer value
 * valid for them."</td></tr>
 * <tr><td>attemptsmin</td><td>(int)</td><td>Specifies the minimum number of attempts for display 
 * testing</td></tr>
 * <tr><td>attemptsmax</td><td>(int)</td><td>Specifies the maximum number of attempts for display 
 * testing</td></tr>
 * <tr><td>selectedanswersmin</td><td>(int)</td><td>Specifies the minimum number of selected answers 
 * for display testing</td></tr>
 * <tr><td>selectedanswersmax</td><td>(int)</td><td>Specifies the maximum number of selected answers 
 * for display testing</td></tr>
 * <tr><td>selectedrightanswersmin</td><td>(int)</td><td>Specifies the minimum number of selected right 
 * answers for display testing</td></tr>
 * <tr><td>selectedrightanswersmax</td><td>(int)</td><td>Specifies the maximum number of selected right 
 * answers for display testing</td></tr>
 * <tr><td>selectedwronganswersmin</td><td>(int)</td><td>Specifies the minimum number of selected wrong 
 * answers for display testing</td></tr>
 * <tr><td>selectedwronganswersmax</td><td>(int)</td><td>Specifies the maximum number of selected wrong 
 * answers for display testing</td></tr>
 * <tr><td>unselectedanswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * answers for display testing</td></tr>
 * <tr><td>unselectedanswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * answers for display testing</td></tr>
 * <tr><td>unselectedrightanswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * right answers for display testing</td></tr>
 * <tr><td>unselectedrightanswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * right answers for display testing</td></tr>
 * <tr><td>unselectedwronganswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * wrong answers for display testing</td></tr>
 * <tr><td>unselectedwronganswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * wrong answers for display testing</td></tr>
 * <tr><td>rightdistancemin</td><td>(int)</td><td>Specifies the minimum distance to the right answer 
 * for display testing</td></tr>
 * <tr><td>rightdistancemax</td><td>(int)</td><td>Specifies the maximum distance to the right answer 
 * for display testing</td></tr>
 * <tr><td>answertype</td><td>(string)</td><td>Specifies the answer's type for display testing with 
 * editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the answertype property from the answer's component
 * </td></tr>
 * <tr><td>casesensitive</td><td>(string)</td><td>Specifies if the answer is considered to be 
 * case sensitive (yes) or case insensitive (no) for display testing with editfield or advancedfield 
 * components.<br/>
 * If it is null (default value) then it is used the casesensitive property from the answer's component
 * </td></tr>
 * <tr><td>trim</td><td>(string)</td><td>Specifies if starting and ending whitespaces from answer are
 * ignored for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the trim property from the answer's component</td></tr>
 * <tr><td>strip</td><td>(string)</td><td>Specifies if all whitespaces from answer are ignored 
 * for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the strip property from the answer's component</td></tr>
 * <tr><td>singlespaces</td><td>(string)</td><td>Specifies if together whitespaces from answer 
 * are considered as a single whitespace for display testing with editfield or advancedfield 
 * components.<br/>
 * If it is null (default value) then it is used the singlespaces property from the answer's component
 * </td></tr>
 * <tr><td>newlinespace</td><td>(string)</td><td>Specifies if new line characters are considered 
 * as whitespaces for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the newlinespace property from the answer's component. 
 * <br/><br/>
 * Note that trim, strip and singlespaces properties are affected if this property is set to yes.
 * </td></tr>
 * <tr><td>ignore</td><td>(string)</td><td>Specifies text ocurrences (separated by commas) 
 * to ignore from answer for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the ignore property from the answer's component
 * </td></tr>
 * <tr><td>ignoreregexp</td><td>(string)</td><td>Specifies a regular expression to ignore text 
 * ocurrences matching it.<br/>
 * If it is null (default value) then it is used the ignoreregexp property from the answer's component
 * </td></tr>
 * <tr><td>ignoreemptylines</td><td>(string)</td><td>Specifies if empty lines from answer are ignored 
 * for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the ignoreemptylines property from the 
 * answer's component</td></tr>
 * <tr><td>tolerance</td><td>(string)</td><td>Specifies error tolerance used in numeric comparisons,
 * by default null</td></tr>
 * </table>
 * <h2>Contents</h2>
 * <h3>Graphs</h3>
 * <p>
 * You can include any number of &lt;world&gt; elements. See the {@link om.graph}
 * package for more information. These are painted in the order you include them;
 * they are painted automatically the first time it's sent, but if you make other
 * changes to the graphs you'll need to use the manual repaint() method.
 * </p>
 * <h3>Interactive features</h3>
 * <p>
 * You can have any number of &lt;marker&gt; elements. These are crosshairs that
 * the user can drag around on the canvas. They can be dragged anywhere on the
 * canvas (but not off it). The X/Y positions of the marker centre can be obtained
 * in code. Marker positions are always in pixel co-ordinates, at the moment,
 * and not world co-ordinates.
 * </p>
 * <p>
 * If you've got at least 2 markers you can also have &lt;markerline&gt; elements.
 * Specify a 'from' and 'to' marker and it'll draw a line between the two markers
 * (the user moves the two markers as usual by dragging each one).
 * </p>
 * <p>
 * You can add a label to the centre of the line by specifying
 * 'labelJS' which contains JavaScript statements that should set a 'label' variable based
 * on the x1, y1, x2, and y2 variables (see XML usage example above). JavaScript is not
 * exactly like Java so be careful if you don't know it. If you make JavaScript
 * errors in the attribute, they will carry through as JS errors at runtime.
 * </p>
 * <p>
 * x1,y1,x2, and y2 are in pixel co-ordinates unless you specify
 * a 'world' attribute. Then they are in world co-ordinates.
 * </p>
 * <p>
 * Labels on markers appear above and right of the marker. They are set in the
 * same way, but for obvious reasons you have x,y instead of x1,y1,x2,y2.
 * <p>
 * (All mentions of pixel co-ordinates above are supposed to relate to 'original-size'
 * pixels, i.e. if accessibility zoom is turned on, the same numbers should work
 * as if it wasn't.)
 * </p>
 */
public class CanvasComponent extends om.stdcomponent.CanvasComponent implements Testable
{
	/** 
	 * Value that can be used at an attribute's placeholder of a graph item to avoid setting 
	 * its referred attribute
	 */
	public final static String PLACEHOLDER_NULL_VALUE="null"; 
	
	/** 
	 * Character used to separate world identifier from marker co-ordinate to set marker co-ordinates
	 * expressed in those world co-ordinates (instead of express them in pixels).<br/><br/>
	 * Be careful that world must be declared in XML before the marker or it will not be recognized.
	 */
	public final static char MARKER_WORLD_COORDINATE_SEPARATOR=':';
	
	/**
	 * Map with some color constants to allow use them with getColour(String sConstant) method.
	 */
	private final static Map<String,Color> COLOR_CONSTANTS;
	static
	{
		COLOR_CONSTANTS=new HashMap<String,Color>();
		COLOR_CONSTANTS.put("black",Color.BLACK);
		COLOR_CONSTANTS.put("blue",Color.BLUE);
		COLOR_CONSTANTS.put("cyan",Color.CYAN);
		COLOR_CONSTANTS.put("darkGray",Color.DARK_GRAY);
		COLOR_CONSTANTS.put("green",Color.GREEN);
		COLOR_CONSTANTS.put("lightGray",Color.LIGHT_GRAY);
		COLOR_CONSTANTS.put("magenta",Color.MAGENTA);
		COLOR_CONSTANTS.put("orange",Color.ORANGE);
		COLOR_CONSTANTS.put("pink",Color.PINK);
		COLOR_CONSTANTS.put("red",Color.RED);
		COLOR_CONSTANTS.put("white",Color.WHITE);
		COLOR_CONSTANTS.put("yellow",Color.YELLOW);
	}
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_ALT,PROPERTY_WIDTH,PROPERTY_HEIGHT,PROPERTY_TYPE,
		PROPERTY_ANTIALIAS,PROPERTY_FILEPATH,PROPERTY_REQUIREBG,PROPERTY_MARKERIMAGE,PROPERTY_MARKERHOTSPOT,
		PROPERTY_TEST,PROPERTY_ANSWER,PROPERTY_ATTEMPTSMIN,PROPERTY_ATTEMPTSMAX,PROPERTY_SELECTEDANSWERSMIN,
		PROPERTY_SELECTEDANSWERSMAX,PROPERTY_SELECTEDRIGHTANSWERSMIN,PROPERTY_SELECTEDRIGHTANSWERSMAX,
		PROPERTY_SELECTEDWRONGANSWERSMIN,PROPERTY_SELECTEDWRONGANSWERSMAX,PROPERTY_UNSELECTEDANSWERSMIN,
		PROPERTY_UNSELECTEDANSWERSMAX,PROPERTY_UNSELECTEDRIGHTANSWERSMIN,PROPERTY_UNSELECTEDRIGHTANSWERSMAX,
		PROPERTY_UNSELECTEDWRONGANSWERSMIN,PROPERTY_UNSELECTEDWRONGANSWERSMAX,PROPERTY_RIGHTDISTANCEMIN,
		PROPERTY_RIGHTDISTANCEMAX,PROPERTY_ANSWERTYPE,PROPERTY_CASESENSITIVE,PROPERTY_TRIM,PROPERTY_STRIP,
		PROPERTY_SINGLESPACES,PROPERTY_NEWLINESPACE,PROPERTY_IGNORE,PROPERTY_IGNOREREGEXP,
		PROPERTY_IGNOREEMPTYLINES,PROPERTY_TOLERANCE
	};
	
	/** Regular expression restricting canvas types */
	private static final String PROPERTYRESTRICTION_TYPE="(png|jpg)";
	
	/** Map with restrictions of properties that need to initialize placeholders */
	private static final Map<String,String> PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS;
	static
	{
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS=new HashMap<String,String>();
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.put(PROPERTY_LANG,PROPERTYRESTRICTION_LANG);
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.put(PROPERTY_TYPE,PROPERTYRESTRICTION_TYPE);
	}
	
	/** Placeholders */
	private Map<String,String> placeholders=new HashMap<String,String>();
	
	/** Specific properties checks */
	private Map<String,PropertyCheck> checks=new HashMap<String,PropertyCheck>();
	
	// UNED: 31-08-2011 - dballestin
	/** 
	 * Information stored on each marker<br/><br/>
	 * This version of Marker allows to use placeholders for its attributes except with the world attribute.
	 */
	public static class Marker extends om.stdcomponent.CanvasComponent.Marker
	{
		/** Attribute name for X co-ord */
		private final static String ATTRIBUTE_X="x";
		
		/** Attribute name for Y co-ord */
		private final static String ATTRIBUTE_Y="y";
		
		/** Attribute name for Javascript expression for label text */
		private final static String ATTRIBUTE_LABELJS="labelJS";
		
		/** Attribute name for ID of world for co-ordinates in label expression */
		private final static String ATTRIBUTE_WORLD="world";
		
		/** Attribute name for state flag that indicates if this marker is displayed or not */
		private final static String ATTRIBUTE_DISPLAY="display";
		
		/** Canvas */
		private CanvasComponent canvas=null;
		
		/** Placeholders */
		private Map<String,String> placeholders=new HashMap<String,String>();
		
		/** State flag that indicates if this marker is displayed or not */
		private boolean display=true;
		
		/**
		 * @return Canvas
		 */
		public CanvasComponent getCanvas()
		{
			return canvas;
		}
		
		/**
		 * Set canvas
		 * @param canvas Canvas
		 */
		public void setCanvas(CanvasComponent canvas)
		{
			this.canvas = canvas;
		}
		
		@Override
		public int getX()
		{
			int iX=super.getX();
			if (placeholders.containsKey(ATTRIBUTE_X))
			{
				String placeholderX=placeholders.get(ATTRIBUTE_X);
				StandardQuestion sq=(StandardQuestion)canvas.getQuestion();
				if (sq.isPlaceholdersInitialized())
				{
					String sX=sq.applyPlaceholders(placeholderX);
					int iSeparator=AnswerChecking.indexOfCharacter(
							sX,MARKER_WORLD_COORDINATE_SEPARATOR,AnswerChecking.getEscapeSequences());
					if (iSeparator==-1)
					{
						try
						{
							iX=Integer.parseInt(sX);
						}
						catch (NumberFormatException nfe)
						{
							throw new GraphFormatRuntimeException(
								"<canvas>: x marker attribute not valid integer");
						}
					}
					else
					{
						World world=null;
						try
						{
							world=(World)getCanvas().getWorld(
									AnswerChecking.replaceEscapeChars(sX.substring(0,iSeparator)));
						}
						catch (OmDeveloperException ode)
						{
							throw new GraphFormatRuntimeException(ode.getMessage(),ode);
						}
						if (iSeparator+1<sX.length())
						{
							try
							{
								double dX=Double.parseDouble(
										AnswerChecking.replaceEscapeChars(sX.substring(iSeparator+1)));
								iX=world.convertX(dX);
							}
							catch (NumberFormatException nfe)
							{
								throw new GraphFormatRuntimeException(
										"<canvas>: x marker attribute has not a valid number for co-ordinate part");
							}
						}
						else
						{
							throw new GraphFormatRuntimeException(
									"<canvas>: x marker attribute has not a valid number for co-ordinate part");
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<marker>: Placeholder ");
					error.append(placeholderX);
					error.append(" for x can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			return iX;
		}
		
		@Override
		public void setX(int iX)
		{
			super.setX(iX);
			placeholders.remove(ATTRIBUTE_X);
		}
		
		@Override
		public int getY()
		{
			int iY=super.getY();
			if (placeholders.containsKey(ATTRIBUTE_Y))
			{
				String placeholderY=placeholders.get(ATTRIBUTE_Y);
				StandardQuestion sq=(StandardQuestion)canvas.getQuestion();
				if (sq.isPlaceholdersInitialized())
				{
					String sY=sq.applyPlaceholders(placeholderY);
					int iSeparator=AnswerChecking.indexOfCharacter(
							sY,MARKER_WORLD_COORDINATE_SEPARATOR,AnswerChecking.getEscapeSequences());
					if (iSeparator==-1)
					{
						try
						{
							iY=Integer.parseInt(sY);
						}
						catch (NumberFormatException nfe)
						{
							throw new GraphFormatRuntimeException(
								"<canvas>: y marker attribute not valid integer");
						}
					}
					else
					{
						World world=null;
						try
						{
							world=(World)getCanvas().getWorld(
									AnswerChecking.replaceEscapeChars(sY.substring(0,iSeparator)));
						}
						catch (OmDeveloperException ode)
						{
							throw new GraphFormatRuntimeException(ode.getMessage(),ode);
						}
						if (iSeparator+1<sY.length())
						{
							try
							{
								double dY=Double.parseDouble(
										AnswerChecking.replaceEscapeChars(sY.substring(iSeparator+1)));
								iY=world.convertY(dY);
							}
							catch (NumberFormatException nfe)
							{
								throw new GraphFormatRuntimeException(
										"<canvas>: y marker attribute has not a valid number for co-ordinate part");
							}
						}
						else
						{
							throw new GraphFormatRuntimeException(
									"<canvas>: y marker attribute has not a valid number for co-ordinate part");
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<marker>: Placeholder ");
					error.append(placeholderY);
					error.append(" for y can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			return iY;
		}
		
		@Override
		public void setY(int iY)
		{
			super.setY(iY);
			placeholders.remove(ATTRIBUTE_Y);
		}
		
		@Override
		public String getLabelJS()
		{
			String sLabelJS=super.getLabelJS();
			if (placeholders.containsKey(ATTRIBUTE_LABELJS))
			{
				String placeholderLabelJS=placeholders.get(ATTRIBUTE_LABELJS);
				StandardQuestion sq=(StandardQuestion)canvas.getQuestion();
				if (sq.isPlaceholdersInitialized())
				{
					sLabelJS=sq.applyPlaceholders(placeholderLabelJS);
				}
				else
				{
					sLabelJS=placeholderLabelJS;
				}
			}
			return sLabelJS;
		}
		
		@Override
		public void setLabelJS(String sLabelJS)
		{
			super.setLabelJS(sLabelJS);
			placeholders.remove(ATTRIBUTE_LABELJS);
		}
		
		/**
		 * @return State flag that indicates if this marker is displayed or not
		 */
		public boolean isDisplay()
		{
			boolean bDisplay=display;
			if (placeholders.containsKey(ATTRIBUTE_DISPLAY))
			{
				String placeholderDisplay=placeholders.get(ATTRIBUTE_DISPLAY);
				StandardQuestion sq=(StandardQuestion)canvas.getQuestion();
				if (sq.isPlaceholdersInitialized())
				{
					String sDisplay=sq.applyPlaceholders(placeholderDisplay);
					if (!sDisplay.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						if (sDisplay.equals("yes"))
						{
							bDisplay=true;
						}
						else if (sDisplay.equals("no"))
						{
							bDisplay=false;
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<marker>: Unexpected boolean value for display: ");
							error.append(sDisplay);
							throw new GraphFormatRuntimeException(error.toString());							
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<marker>: Placeholder ");
					error.append(placeholderDisplay);
					error.append(" for display can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			return bDisplay;
		}
		
		/**
		 * Set State flag that indicates if this marker is displayed or not
		 * @param display State flag that indicates if this marker is displayed or not
		 */
		public void setDisplay(boolean display)
		{
			this.display=display;
			placeholders.remove(ATTRIBUTE_DISPLAY);
		}
	}
	
	// UNED: 31-08-2011 - dballestin
	/** 
	 * Information stored for line between markers<br/><br/>
	 * This version of MarkerLine allows to use placeholders for its attributes except with the world attribute.
	 */
	public static class MarkerLine extends om.stdcomponent.CanvasComponent.MarkerLine
	{
		/** Attribute name for index of "from" marker */
		private final static String ATTRIBUTE_FROM="from";
		
		/** Attribute name for index of "to" marker */
		private final static String ATTRIBUTE_TO="to";
		
		/** Attribute name for Javascript expression for label text */
		private final static String ATTRIBUTE_LABELJS="labelJS";
		
		/** Attribute name for ID of world for co-ordinates in label expression */
		private final static String ATTRIBUTE_WORLD="world";
		
		/** Attribute name for state flag that indicates if this markerline is displayed or not */
		private final static String ATTRIBUTE_DISPLAY="display";
		
		/** Canvas */
		private CanvasComponent canvas=null;
		
		/** Placeholders */
		private Map<String,String> placeholders=new HashMap<String,String>();
		
		/** State flag that indicates if this markerline is displayed or not */
		private boolean display=true;
		
		/**
		 * @return Canvas
		 */
		public CanvasComponent getCanvas()
		{
			return canvas;
		}
		
		/**
		 * Set canvas
		 * @param canvas Canvas
		 */
		public void setCanvas(CanvasComponent canvas)
		{
			this.canvas = canvas;
		}
		
		@Override
		public int getFrom()
		{
			int iFrom=super.getFrom();
			if (placeholders.containsKey(ATTRIBUTE_FROM))
			{
				String placeholderFrom=placeholders.get(ATTRIBUTE_FROM);
				StandardQuestion sq=(StandardQuestion)canvas.getQuestion();
				if (sq.isPlaceholdersInitialized())
				{
					try
					{
						iFrom=Integer.parseInt(sq.applyPlaceholders(placeholderFrom));
						if (iFrom<0 || iFrom>=canvas.getMarkers().size())
						{
							throw new GraphFormatRuntimeException(
									"<canvas>: from for markerline is out of range (remember markers start at 0)");
						}
					}
					catch (NumberFormatException nfe)
					{
						throw new GraphFormatRuntimeException(
								"<canvas>: from marker attribute not valid integer");
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<marker>: Placeholder ");
					error.append(placeholderFrom);
					error.append(" for from can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			return iFrom;
		}
		
		@Override
		public void setFrom(int iFrom)
		{
			super.setFrom(iFrom);
			placeholders.remove(ATTRIBUTE_FROM);
		}
		
		@Override
		public int getTo()
		{
			int iTo=super.getTo();
			if (placeholders.containsKey(ATTRIBUTE_TO))
			{
				String placeholderTo=placeholders.get(ATTRIBUTE_TO);
				StandardQuestion sq=(StandardQuestion)canvas.getQuestion();
				if (sq.isPlaceholdersInitialized())
				{
					try
					{
						iTo=Integer.parseInt(sq.applyPlaceholders(placeholderTo));
						if (iTo<0 || iTo>=canvas.getMarkers().size())
						{
							throw new GraphFormatRuntimeException(
									"<canvas>: to for markerline is out of range (remember markers start at 0)");
						}
					}
					catch (NumberFormatException nfe)
					{
						throw new GraphFormatRuntimeException(
								"<canvas>: to marker attribute not valid integer");
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<marker>: Placeholder ");
					error.append(placeholderTo);
					error.append(" for to can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			return iTo;
		}
		
		@Override
		public void setTo(int iTo)
		{
			super.setTo(iTo);
			placeholders.remove(ATTRIBUTE_TO);
		}
		
		@Override
		public String getLabelJS()
		{
			String sLabelJS=super.getLabelJS();
			if (placeholders.containsKey(ATTRIBUTE_LABELJS))
			{
				String placeholderLabelJS=placeholders.get(ATTRIBUTE_LABELJS);
				StandardQuestion sq=(StandardQuestion)canvas.getQuestion();
				if (sq.isPlaceholdersInitialized())
				{
					sLabelJS=sq.applyPlaceholders(placeholderLabelJS);
				}
				else
				{
					sLabelJS=placeholderLabelJS;
				}
			}
			return sLabelJS;
		}
		
		@Override
		public void setLabelJS(String sLabelJS)
		{
			super.setLabelJS(sLabelJS);
			placeholders.remove(ATTRIBUTE_LABELJS);
		}
		
		/**
		 * @return State flag that indicates if this marker line is displayed or not
		 */
		public boolean isDisplay()
		{
			boolean bDisplay=display;
			if (placeholders.containsKey(ATTRIBUTE_DISPLAY))
			{
				String placeholderDisplay=placeholders.get(ATTRIBUTE_DISPLAY);
				StandardQuestion sq=(StandardQuestion)canvas.getQuestion();
				if (sq.isPlaceholdersInitialized())
				{
					String sDisplay=sq.applyPlaceholders(placeholderDisplay);
					if (!sDisplay.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						if (sDisplay.equals("yes"))
						{
							bDisplay=true;
						}
						else if (sDisplay.equals("no"))
						{
							bDisplay=false;
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<marker>: Unexpected boolean value for display: ");
							error.append(sDisplay);
							throw new GraphFormatRuntimeException(error.toString());							
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<marker>: Placeholder ");
					error.append(placeholderDisplay);
					error.append(" for display can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			if (bDisplay)
			{
				List<om.stdcomponent.CanvasComponent.Marker> markers=getCanvas().getMarkers();
				bDisplay=((Marker)markers.get(getFrom())).isDisplay() && ((Marker)markers.get(getTo())).isDisplay();
			}
			return bDisplay;
		}
		
		/**
		 * Set State flag that indicates if this marker line is displayed or not.<br/><br/>
		 * @param display State flag that indicates if this marker line is displayed or not
		 */
		public void setDisplay(boolean display)
		{
			this.display=display;
			placeholders.remove(ATTRIBUTE_DISPLAY);
		}
	}
	
	/**
	 * Get list of graph worlds, in paint order
	 * @return List of graph worlds, in paint order
	 */
	@Override
	public List<om.graph.World> getWorlds()
	{
		return super.getWorlds();
	}
	
	/**
	 * Get list of markers
	 * @return List of markers
	 */
	@Override
	public List<om.stdcomponent.CanvasComponent.Marker> getMarkers()
	{
		return super.getMarkers();
	}
	
	@Override
	public List<om.stdcomponent.CanvasComponent.MarkerLine> getLines()
	{
		return super.getLines();
	}
	
	/**
	 * Adds a new marker at given co-ordinates.
	 * @param iX X pixel co-ordinate
	 * @param iY Y pixel co-ordinate
	 * @param sLabelJS JavaScript label (may be null)
	 * @param sWorld World ID for converting co-ordinates in JS expression (may be null)
	 */
	@Override
	public void addMarker(int iX,int iY,String sLabelJS,String sWorld)
	{
		addMarker(iX,iY,sLabelJS,sWorld,true);
	}
	
	/**
	 * Adds a new marker at given co-ordinates.
	 * @param iX X pixel co-ordinate
	 * @param iY Y pixel co-ordinate
	 * @param sLabelJS JavaScript label (may be null)
	 * @param sWorld World ID for converting co-ordinates in JS expression (may be null)
	 * @param display Whether to display this marker or not
	 */
	public void addMarker(int iX,int iY,String sLabelJS,String sWorld,boolean display)
	{
		Marker m=new Marker();
		m.setX(iX);
		m.setY(iY);
		m.setLabelJS(sLabelJS==null?"":sLabelJS);
		m.setWorld(sWorld);
		m.setDisplay(display);
		getMarkers().add(m);
	}
	
	/**
	 * Adds a markerline.
	 * @param iFromIndex Index (0-based) of one marker
	 * @param iToIndex Index (0-based) of other marker
	 * @param sLabelJS JavaScript label (may be null)
	 * @param sWorld World ID for converting co-ordinates in JS expression (may be null)
	 */
	@Override
	public void addLine(int iFromIndex,int iToIndex,String sLabelJS,String sWorld)
	{
		addLine(iFromIndex,iToIndex,sLabelJS,sWorld,true);
	}
	
	/**
	 * Adds a markerline.
	 * @param iFromIndex Index (0-based) of one marker
	 * @param iToIndex Index (0-based) of other marker
	 * @param sLabelJS JavaScript label (may be null)
	 * @param sWorld World ID for converting co-ordinates in JS expression (may be null)
	 * @param display Whether to display this marker or not
	 */
	public void addLine(int iFromIndex,int iToIndex,String sLabelJS,String sWorld,
			boolean display)
	{
		MarkerLine ml=new MarkerLine();
		ml.setFrom(iFromIndex);
		ml.setTo(iToIndex);
		ml.setLabelJS(sLabelJS==null?"":sLabelJS);
		ml.setWorld(sWorld);
		ml.setDisplay(display);
		getLines().add(ml);
	}
	
	
	/**
	 * Initialises children based on the given XML element.
	 * <br/><br/>
	 * Note that om.stdcomponent.uned components don't allow to check properties from this method, because properties 
	 * aren't still defined due to initializations changes to support placeholders.<br/><br/>
	 * If you override this method and you really need to check properties you need to move that checkings to 
	 * other method (some good candidates depending on your needs are: initializeSpecific, produceVisibleOutput or 
	 * init method from question class if you are overriding generic question class).
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error initialising the children
	 */
	@Override
	protected void initChildren(Element eThis) throws OmException
	{
		// We don't clear it now because properties aren't still set
		//clear();
		
		Element[] aeChildren=XML.getChildren(eThis);
		for (int iChild=0;iChild<aeChildren.length;iChild++)
		{
			Element e=aeChildren[iChild];
			String sTag=e.getTagName();
			if(sTag.equals("world"))
			{
				// Create the world
				World w;
				try
				{
					w=new World(this,e,(StandardQuestion)getQuestion());
				}
				catch (GraphFormatException gfe)
				{
					throw new OmFormatException(gfe.getMessage(),gfe);
				}
				getWorlds().add(w);

				// We don't paint it now because properties aren't still set
				//w.paint(getGraphics());
			}
			else if (sTag.equals("marker"))
			{
				Marker m=new Marker();
				m.setCanvas(this);
				try
				{
					String sX=XML.getRequiredAttribute(e,Marker.ATTRIBUTE_X);
					if (StandardQuestion.containsPlaceholder(sX))
					{
						m.placeholders.put(Marker.ATTRIBUTE_X,sX);
					}
					else
					{
						int iSeparator=AnswerChecking.indexOfCharacter(
								sX,MARKER_WORLD_COORDINATE_SEPARATOR,AnswerChecking.getEscapeSequences());
						if (iSeparator==-1)
						{
							try
							{
								int iX=Integer.parseInt(sX);
								m.setX(iX);
							}
							catch (NumberFormatException nfe)
							{
								throw new OmFormatException(
										"<canvas>: x marker attribute not valid integer");
							}
						}
						else
						{
							World world=(World)getWorld(
									AnswerChecking.replaceEscapeChars(sX.substring(0,iSeparator)));
							if (iSeparator+1<sX.length())
							{
								try
								{
									double dX=Double.parseDouble(AnswerChecking.replaceEscapeChars(
											sX.substring(iSeparator+1)));
									int iX=world.convertX(dX);
									m.setX(iX);
								}
								catch (NumberFormatException nfe)
								{
									throw new OmFormatException(
									"<canvas>: x marker attribute has not a valid number for co-ordinate part");
								}
							}
							else
							{
								throw new OmFormatException(
										"<canvas>: x marker attribute has not a valid number for co-ordinate part");
							}
						}
					}
				}
				catch (XMLException xe)
				{
					throw new OmFormatException("<canvas>: Missing x attribute for marker");
				}
				try
				{
					String sY=XML.getRequiredAttribute(e,Marker.ATTRIBUTE_Y);
					if (StandardQuestion.containsPlaceholder(sY))
					{
						m.placeholders.put(Marker.ATTRIBUTE_Y,sY);
					}
					else
					{
						int iSeparator=AnswerChecking.indexOfCharacter(
								sY,MARKER_WORLD_COORDINATE_SEPARATOR,AnswerChecking.getEscapeSequences());
						if (iSeparator==-1)
						{
							try
							{
								int iY=Integer.parseInt(sY);
								m.setY(iY);
							}
							catch (NumberFormatException nfe)
							{
								throw new OmFormatException(
										"<canvas>: y marker attribute not valid integer");
							}
						}
						else
						{
							World world=(World)getWorld(
									AnswerChecking.replaceEscapeChars(sY.substring(0,iSeparator)));
							if (iSeparator+1<sY.length())
							{
								try
								{
									double dY=Double.parseDouble(AnswerChecking.replaceEscapeChars(
											sY.substring(iSeparator+1)));
									int iY=world.convertY(dY);
									m.setY(iY);
								}
								catch (NumberFormatException nfe)
								{
									throw new OmFormatException(
									"<canvas>: y marker attribute has not a valid number for co-ordinate part");
								}
							}
							else
							{
								throw new OmFormatException(
										"<canvas>: y marker attribute has not a valid number for co-ordinate part");
							}
						}
					}
				}
				catch (XMLException xe)
				{
					throw new OmFormatException("<canvas>: Missing y attribute for marker");
				}
				
				if (e.hasAttribute(Marker.ATTRIBUTE_LABELJS))
				{
					String sLabelJS=e.getAttribute(Marker.ATTRIBUTE_LABELJS);
					if (StandardQuestion.containsPlaceholder(sLabelJS))
					{
						m.placeholders.put(Marker.ATTRIBUTE_LABELJS,sLabelJS);
					}
					else
					{
						m.setLabelJS(sLabelJS);
					}
				}
				else
				{
					m.setLabelJS("");
				}
				
				m.setWorld(e.hasAttribute(Marker.ATTRIBUTE_WORLD)?e.getAttribute(Marker.ATTRIBUTE_WORLD):null);
				
				if (e.hasAttribute(Marker.ATTRIBUTE_DISPLAY))
				{
					String display=e.getAttribute(Marker.ATTRIBUTE_DISPLAY);
					if (StandardQuestion.containsPlaceholder(display))
					{
						m.setDisplay(true);
						m.placeholders.put(Marker.ATTRIBUTE_DISPLAY,display);
					}
					else if (display.equals("yes"))
					{
						m.setDisplay(true);
					}
					else if (display.equals("no"))
					{
						m.setDisplay(false);
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<canvas>: display for marker has unexpected boolean value: ");
						error.append(display);
						throw new OmFormatException(error.toString());							
					}
				}
				
				getMarkers().add(m);
			}
			else if (sTag.equals("markerline"))
			{
				MarkerLine ml=new MarkerLine();
				ml.setCanvas(this);
				try
				{
					String sFrom=XML.getRequiredAttribute(e,MarkerLine.ATTRIBUTE_FROM);
					if (StandardQuestion.containsPlaceholder(sFrom))
					{
						ml.placeholders.put(MarkerLine.ATTRIBUTE_FROM,sFrom);
					}
					else
					{
						try
						{
							int iFrom=Integer.parseInt(sFrom);
							ml.setFrom(iFrom);
							if (ml.getFrom()<0 || ml.getFrom()>=getMarkers().size())
							{
								throw new OmFormatException(
										"<canvas>: from for markerline is out of range (remember markers start at 0)");
							}
						}
						catch (NumberFormatException nfe)
						{
							throw new OmFormatException("<canvas>: from markerline attribute not valid integer");
						}
					}
				}
				catch (XMLException xe)
				{
					throw new OmFormatException("<canvas>: Missing from attribute for markerline");
				}
				try
				{
					String sTo=XML.getRequiredAttribute(e,MarkerLine.ATTRIBUTE_TO);
					if (StandardQuestion.containsPlaceholder(sTo))
					{
						ml.placeholders.put(MarkerLine.ATTRIBUTE_TO,sTo);
					}
					else
					{
						try
						{
							int iTo=Integer.parseInt(sTo);
							ml.setTo(iTo);
							if (ml.getTo()<0 || ml.getTo()>=getMarkers().size())
							{
								throw new OmFormatException(
										"<canvas>: to for markerline is out of range (remember markers start at 0)");
							}
						}
						catch (NumberFormatException nfe)
						{
							throw new OmFormatException("<canvas>: to markerline attribute not valid integer");
						}
					}
				}
				catch (XMLException xe)
				{
					throw new OmFormatException("<canvas>: Missing to attribute for markerline");
				}
				
				if (e.hasAttribute(MarkerLine.ATTRIBUTE_LABELJS))
				{
					String sLabelJS=e.getAttribute(MarkerLine.ATTRIBUTE_LABELJS);
					if (StandardQuestion.containsPlaceholder(sLabelJS))
					{
						ml.placeholders.put(MarkerLine.ATTRIBUTE_LABELJS,sLabelJS);
					}
					else
					{
						ml.setLabelJS(sLabelJS);
					}
				}
				else
				{
					ml.setLabelJS("");
				}
				
				ml.setWorld(e.hasAttribute(MarkerLine.ATTRIBUTE_WORLD)?
						e.getAttribute(MarkerLine.ATTRIBUTE_WORLD):null);
				
				if (e.hasAttribute(MarkerLine.ATTRIBUTE_DISPLAY))
				{
					String display=e.getAttribute(MarkerLine.ATTRIBUTE_DISPLAY);
					if (StandardQuestion.containsPlaceholder(display))
					{
						ml.setDisplay(true);
						ml.placeholders.put(MarkerLine.ATTRIBUTE_DISPLAY,display);
					}
					else if (display.equals("yes"))
					{
						ml.setDisplay(true);
					}
					else if (display.equals("no"))
					{
						ml.setDisplay(false);
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<canvas>: display for markerline has unexpected boolean value: ");
						error.append(display);
						throw new OmFormatException(error.toString());							
					}
				}
				
				getLines().add(ml);
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<canvas>: Unexpected content (accepts <world>, <marker>, <markerline>): <");
				error.append(sTag);
				error.append('>');
				throw new OmFormatException(error.toString());
			}
		}
	}
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineString(PROPERTY_TEST);
		setString(PROPERTY_TEST,Tester.TEST_TRUE);
		
		defineString(PROPERTY_ANSWER);
		setString(PROPERTY_ANSWER,null);
		
		defineInteger(PROPERTY_ATTEMPTSMIN);
		setInteger(PROPERTY_ATTEMPTSMIN,0);
		
		defineInteger(PROPERTY_ATTEMPTSMAX);
		setInteger(PROPERTY_ATTEMPTSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_SELECTEDANSWERSMIN);
		setInteger(PROPERTY_SELECTEDANSWERSMIN,0);
		
		defineInteger(PROPERTY_SELECTEDANSWERSMAX);
		setInteger(PROPERTY_SELECTEDANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_SELECTEDRIGHTANSWERSMIN);
		setInteger(PROPERTY_SELECTEDRIGHTANSWERSMIN,0);
		
		defineInteger(PROPERTY_SELECTEDRIGHTANSWERSMAX);
		setInteger(PROPERTY_SELECTEDRIGHTANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_SELECTEDWRONGANSWERSMIN);
		setInteger(PROPERTY_SELECTEDWRONGANSWERSMIN,0);
		
		defineInteger(PROPERTY_SELECTEDWRONGANSWERSMAX);
		setInteger(PROPERTY_SELECTEDWRONGANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_UNSELECTEDANSWERSMIN);
		setInteger(PROPERTY_UNSELECTEDANSWERSMIN,0);
		
		defineInteger(PROPERTY_UNSELECTEDANSWERSMAX);
		setInteger(PROPERTY_UNSELECTEDANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMIN);
		setInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMIN,0);
		
		defineInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMAX);
		setInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_UNSELECTEDWRONGANSWERSMIN);
		setInteger(PROPERTY_UNSELECTEDWRONGANSWERSMIN,0);
		
		defineInteger(PROPERTY_UNSELECTEDWRONGANSWERSMAX);
		setInteger(PROPERTY_UNSELECTEDWRONGANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_RIGHTDISTANCEMIN);
		setInteger(PROPERTY_RIGHTDISTANCEMIN,0);
		
		defineInteger(PROPERTY_RIGHTDISTANCEMAX);
		setInteger(PROPERTY_RIGHTDISTANCEMAX,Integer.MAX_VALUE);
		
		defineString(PROPERTY_ANSWERTYPE);
		setString(PROPERTY_ANSWERTYPE,null);
		
		defineString(PROPERTY_CASESENSITIVE);
		setString(PROPERTY_CASESENSITIVE,null);
		
		defineString(PROPERTY_TRIM);
		setString(PROPERTY_TRIM,null);
		
		defineString(PROPERTY_STRIP);
		setString(PROPERTY_STRIP,null);
		
		defineString(PROPERTY_SINGLESPACES);
		setString(PROPERTY_SINGLESPACES,null);
		
		defineString(PROPERTY_NEWLINESPACE);
		setString(PROPERTY_NEWLINESPACE,null);
		
		defineString(PROPERTY_IGNORE);
		setString(PROPERTY_IGNORE,null);
		
		defineString(PROPERTY_IGNOREREGEXP);
		setString(PROPERTY_IGNOREREGEXP,null);
		
		defineString(PROPERTY_IGNOREEMPTYLINES);
		setString(PROPERTY_IGNOREEMPTYLINES,null);
		
		defineString(PROPERTY_TOLERANCE);
		setString(PROPERTY_TOLERANCE,null);
	}
	
	/** @return Test for display testing */
	@Override
	public String getTest()
	{
		try
		{
			return getString(PROPERTY_TEST);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the test for display testing.
	 * @param test Test for display testing
	 */
	@Override
	public void setTest(String test)
	{
		try
		{
			setString(PROPERTY_TEST,test);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return Answer's components for display testing separated by comma ',' 
	 * (OR operator) or plus symbol '+' (AND operator)
	 */
	@Override
	public String getAnswer()
	{
		try
		{
			return getString(PROPERTY_ANSWER);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the answer's components for display testing separated by comma ',' (OR operator) 
	 * or plus symbol '+' (AND operator), set it to null if you want to remove definition.
	 * @param answer Answer's components for display testing
	 */
	@Override
	public void setAnswer(String answer)
	{
		try
		{
			setString(PROPERTY_ANSWER,answer);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of attempts for display testing */
	@Override
	public int getAttemptsMin()
	{
		try
		{
			return getInteger(PROPERTY_ATTEMPTSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of attempts for display testing.
	 * @param attemptsMin Minimum number of attempts for display testing
	 */
	@Override
	public void setAttemptsMin(int attemptsMin)
	{
		try
		{
			setInteger(PROPERTY_ATTEMPTSMIN,attemptsMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of attempts for display testing */
	@Override
	public int getAttemptsMax()
	{
		try
		{
			return getInteger(PROPERTY_ATTEMPTSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of attempts for display testing.
	 * @param attemptsMax Maximum number of attempts for display testing
	 */
	@Override
	public void setAttemptsMax(int attemptsMax)
	{
		try
		{
			setInteger(PROPERTY_ATTEMPTSMAX,attemptsMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of selected answers for display testing */
	@Override
	public int getSelectedAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of selected answers for display testing.
	 * @param selectedAnswersMin Minimum number of selected answers for display testing 
	 */
	@Override
	public void setSelectedAnswersMin(int selectedAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDANSWERSMIN,selectedAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of selected answers for display testing */
	@Override
	public int getSelectedAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of selected answers for display testing.
	 * @param selectedAnswersMax Maximum number of selected answers for display testing 
	 */
	@Override
	public void setSelectedAnswersMax(int selectedAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDANSWERSMAX,selectedAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of selected right answers for display testing */
	@Override
	public int getSelectedRightAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDRIGHTANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of selected right answers for display testing.
	 * @param selectedRightAnswersMin Minimum number of selected right answers for display testing 
	 */
	@Override
	public void setSelectedRightAnswersMin(int selectedRightAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDRIGHTANSWERSMIN,selectedRightAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of selected right answers for display testing */
	@Override
	public int getSelectedRightAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDRIGHTANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of selected right answers for display testing.
	 * @param selectedRightAnswersMax Maximum number of selected right answers for display testing 
	 */
	@Override
	public void setSelectedRightAnswersMax(int selectedRightAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDRIGHTANSWERSMAX,selectedRightAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of selected wrong answers for display testing */
	@Override
	public int getSelectedWrongAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDWRONGANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of selected wrong answers for display testing.
	 * @param selectedWrongAnswersMin Minimum number of selected wrong answers for display testing 
	 */
	@Override
	public void setSelectedWrongtAnswersMin(int selectedWrongAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDWRONGANSWERSMIN,selectedWrongAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of selected wrong answers for display testing */
	@Override
	public int getSelectedWrongAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDWRONGANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of selected wrong answers for display testing.
	 * @param selectedWrongAnswersMax Maximum number of selected wrong answers for display testing 
	 */
	@Override
	public void setSelectedWrongAnswersMax(int selectedWrongAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDWRONGANSWERSMAX,selectedWrongAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of unselected answers for display testing */
	@Override
	public int getUnselectedAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of unselected answers for display testing.
	 * @param unselectedAnswersMin Minimum number of unselected answers for display testing 
	 */
	@Override
	public void setUnselectedAnswersMin(int unselectedAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDANSWERSMIN,unselectedAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of unselected answers for display testing */
	@Override
	public int getUnselectedAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of unselected answers for display testing.
	 * @param unselectedAnswersMax Maximum number of unselected answers for display testing 
	 */
	@Override
	public void setUnselectedAnswersMax(int unselectedAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDANSWERSMAX,unselectedAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of unselected right answers for display testing */
	@Override
	public int getUnselectedRightAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of unselected right answers for display testing.
	 * @param unselectedRightAnswersMin Minimum number of selected right answers for display testing 
	 */
	@Override
	public void setUnselectedRightAnswersMin(int unselectedRightAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMIN,unselectedRightAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of unselected right answers for display testing */
	@Override
	public int getUnselectedRightAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of unselected right answers for display testing.
	 * @param unselectedRightAnswersMax Maximum number of unselected right answers for display testing 
	 */
	@Override
	public void setUnselectedRightAnswersMax(int unselectedRightAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMAX,unselectedRightAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of unselected wrong answers for display testing */
	@Override
	public int getUnselectedWrongAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDWRONGANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of unselected wrong answers for display testing.
	 * @param unselectedWrongAnswersMin Minimum number of unselected wrong answers for display testing 
	 */
	@Override
	public void setUnselectedWrongtAnswersMin(int unselectedWrongAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDWRONGANSWERSMIN,unselectedWrongAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of unselected wrong answers for display testing */
	@Override
	public int getUnselectedWrongAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDWRONGANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of unselected wrong answers for display testing.
	 * @param unselectedWrongAnswersMax Maximum number of unselected wrong answers for display testing 
	 */
	@Override
	public void setUnselectedWrongAnswersMax(int unselectedWrongAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDWRONGANSWERSMAX,unselectedWrongAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum distance to the right answer for display testing */
	@Override
	public int getRightDistanceMin()
	{
		try
		{
			return getInteger(PROPERTY_RIGHTDISTANCEMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum distance to the right answer for display testing.
	 * @param rightDistanceMin Minimum distance to the right answer for display testing 
	 */
	@Override
	public void setRightDistanceMin(int rightDistanceMin)
	{
		try
		{
			setInteger(PROPERTY_RIGHTDISTANCEMIN,rightDistanceMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum distance to the right answer for display testing */
	@Override
	public int getRightDistanceMax()
	{
		try
		{
			return getInteger(PROPERTY_RIGHTDISTANCEMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum distance to the right answer for display testing.
	 * @param rightDistanceMax Maximum distance to the right answer for display testing 
	 */
	@Override
	public void setRightDistanceMax(int rightDistanceMax)
	{
		try
		{
			setInteger(PROPERTY_RIGHTDISTANCEMAX,rightDistanceMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return Answer's type for display testing with editfield or advancedfield 
	 * components.<br/>
	 * If it is null (default value) then it is used the answertype property from the 
	 * answer's component
	 */
	@Override
	public String getAnswerType()
	{
		try
		{
			return getString(PROPERTY_ANSWERTYPE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the answer's type for display testing with editfield or advancedfield components.<br/>
	 * If it is set to null (default value) then it will be used the answertype property from the 
	 * answer's component.
	 * @param answertype Answer's type for display testing or null to use the answertype property from 
	 * the answer's component
	 */
	@Override
	public void setAnswerType(String answertype)
	{
		try
		{
			setString(PROPERTY_ANSWERTYPE,answertype);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if considering the answer to be case sensitive for display testing with editfield 
	 * or advancedfield components, "no" if it is considered to be case insensitive 
	 * or null if it is used the answertype property from the answer's component
	 */
	@Override
	public String getCaseSensitive()
	{
		try
		{
			return getString(PROPERTY_CASESENSITIVE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if the answer is going to be considered case sensitive (true) or case insensitive (false)
	 * for display testing with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the casesensitive property from the 
	 * answer's component (string)
	 * @param casesensitive "yes" if the answer is going to be considered case sensitive 
	 * for display testing with editfield or advancedfield components, "no" if it is going 
	 * to be considered case insensitive or null to use the answertype property from 
	 * the answer's component
	 */
	@Override
	public void setCaseSensitive(String casesensitive)
	{
		try
		{
			setString(PROPERTY_CASESENSITIVE,casesensitive);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if starting and ending whitespaces from answer are ignored for display testing 
	 * with editfield or advancedfield components, "no" if they are not ignored 
	 * or null if it is used the trim property from the answer's component
	 */
	@Override
	public String getTrim()
	{
		try
		{
			return getString(PROPERTY_TRIM);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if starting and ending whitespaces from answer are ignored for display testing 
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the trim property from the answer's component 
	 * (string)
	 * @param trim "yes" if starting and ending whitespaces from answer are going to be ignored 
	 * for display testing with editfield or advancedfield components, 
	 * "no" if they are not going to be ignored or null to use the trim property from the 
	 * answer's component
	 */
	@Override
	public void setTrim(String trim)
	{
		try
		{
			setString(PROPERTY_TRIM,trim);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if all whitespaces from answer are ignored for display testing with 
	 * editfield or advancedfield components, "no" if they are not ignored 
	 * or null if it is used the strip property from the answer's component
	 */
	@Override
	public String getStrip()
	{
		try
		{
			return getString(PROPERTY_STRIP);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if all whitespaces from answer are ignored for display testing with editfield or 
	 * advancedfield components.<br/>
	 * If it is null (default value) then it is used the strip property from the answer's component 
	 * (string)
	 * @param strip "yes" if all whitespaces from answer are going to be ignored for display testing 
	 * with editfield or advancedfield components, "no" if they are not going to be ignored 
	 * or null to use the strip property from the answer's component
	 */
	@Override
	public void setStrip(String strip)
	{
		try
		{
			setString(PROPERTY_STRIP,strip);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if together whitespaces from answer are considered as single whitespaces 
	 * for display testing with editfield or advancedfield components, 
	 * "no" if they are not considered as single whitespaces or null if it is used 
	 * the singlespaces property from the answer's component
	 */
	@Override
	public String getSingleSpaces()
	{
		try
		{
			return getString(PROPERTY_SINGLESPACES);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if together whitespaces from answer are considered as single whitespaces 
	 * for display testing with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the singlespaces property from the 
	 * answer's component (string)
	 * @param singlespaces "yes" if together whitespaces from answer are going to be considered as 
	 * single whitespaces for display testing with editfield or advancedfield components, 
	 * "no" if they are not going to be considered as single whitespaces or null to use 
	 * the singlespaces property from the answer's component
	 */
	@Override
	public void setSingleSpaces(String singlespaces)
	{
		try
		{
			setString(PROPERTY_SINGLESPACES,singlespaces);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if new line characters from answer are considered as whitespaces 
	 * for display testing with editfield components, "no" if they are not considered as whitespaces 
	 * or null if it is used the newlinespace property from the answer's component
	 */
	@Override
	public String getNewLineSpace()
	{
		try
		{
			return getString(PROPERTY_NEWLINESPACE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if new line characters from answer are considered as whitespaces for display testing 
	 * with editfield components.<br/>
	 * If it is null (default value) then it is used the newlinespace property from the 
	 * answer's component (string)
	 * @param newlinespace "yes" if new line characters from answer are going to be considered as 
	 * whitespaces for display testing with editfield components, "no" if they are not going 
	 * to be considered as whitespaces or null to use the newlinespace property from 
	 * the answer's component
	 */
	@Override
	public void setNewLineSpace(String newlinespace)
	{
		try
		{
			setString(PROPERTY_NEWLINESPACE,newlinespace);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Text ocurrences (separated by commas) to ignore from answer for answer checking 
	 * with editfield or advancedfield components
	 */
	@Override
	public String getIgnore()
	{
		try
		{
			return getString(PROPERTY_IGNORE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set text ocurrences (separated by commas) to ignore from answer for answer checking
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the ignore property from the 
	 * answer's component (string)
	 * @param ignore Text ocurrences (separated by commas) to ignore from answer
	 */
	@Override
	public void setIgnore(String ignore)
	{
		try
		{
			setString(PROPERTY_IGNORE,ignore);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Regular expression to ignore matched text ocurrences from answer for answer checking 
	 * with editfield or advancedfield components
	 */
	@Override
	public String getIgnoreRegExp()
	{
		try
		{
			return getString(PROPERTY_IGNOREREGEXP);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set regular expression to ignore matched text ocurrences from answer for answer checking
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the ignoreregexp property from the 
	 * answer's component (string)
	 * @param ignoreRegExp Regular expression to ignore matched text ocurrences from answer
	 */
	@Override
	public void setIgnoreRegExp(String ignoreRegExp)
	{
		try
		{
			setString(PROPERTY_IGNOREREGEXP,ignoreRegExp);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if empty lines from answer are ignored for display testing with 
	 * editfield components, "no" if they are not ignored or null if it is used 
	 * the ignoreemptylines property from the answer's component
	 */
	@Override
	public String getIgnoreEmptyLines()
	{
		try
		{
			return getString(PROPERTY_IGNOREEMPTYLINES);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if empty lines characters from answer are ignored for display testing with 
	 * editfield components.<br/>
	 * If it is null (default value) then it is used the ignoreemptylines property from the 
	 * answer's component (string)
	 * @param ignoreemptylines "yes" if empty lines from answer are ignored for display testing 
	 * with editfield components, "no" if they are not going to be ignored 
	 * or null to use the ignoreemptylines property from the answer's component
	 */
	@Override
	public void setIgnoreEmptyLines(String ignoreemptylines)
	{
		try
		{
			setString(PROPERTY_IGNOREEMPTYLINES,ignoreemptylines);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	@Override
	public String getTolerance()
	{
		try
		{
			return getString(PROPERTY_TOLERANCE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	@Override
	public void setTolerance(String tolerance)
	{
		try
		{
			setString(PROPERTY_TOLERANCE,tolerance);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	@Override
	public String getString(String sName) throws OmDeveloperException
	{
		String sValue=null;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			sValue=super.getString(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				sValue=sq.applyPlaceholders(placeholder);
			}
			else
			{
				sValue=placeholder;
			}
			
			// Check properties with restrictions
			if (PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.containsKey(sName))
			{
				String restriction=PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.get(sName);
				if (!sValue.matches(restriction))
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' has an invalid value ");
					error.append(sValue);
					throw new OmFormatException(error.toString());
				}
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(sValue);
			}
		}
		return sValue;
	}
	
	@Override
	public String setString(String sName,String sValue) throws OmDeveloperException
	{
		boolean isOldValueNull=false;
		String sOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			sOldValue=getString(sName);
			isOldValueNull=sOldValue==null;
		}
		String sOldAux=super.setString(sName,sValue);
		placeholders.remove(sName);
		return isOldValueNull?null:sOldValue==null?sOldAux:sOldValue;
	}
	
	@Override
	public int getInteger(String sName) throws OmDeveloperException
	{
		int iValue=-1;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			iValue=super.getInteger(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				try
				{
					iValue=Integer.parseInt(placeholderReplaced);
				}
				catch (NumberFormatException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' is not a valid integer");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Integer(iValue));
			}
		}
		return iValue;
	}
	
	@Override
	public int setInteger(String sName,int iValue) throws OmDeveloperException
	{
		Integer iOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			iOldValue=new Integer(getInteger(sName));
		}
		int iOldAux=super.setInteger(sName,iValue);
		placeholders.remove(sName);
		return iOldValue==null?iOldAux:iOldValue.intValue();
	}
	
	@Override
	public boolean getBoolean(String sName) throws OmDeveloperException
	{
		boolean bValue=false;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			bValue=super.getBoolean(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				if (placeholderReplaced.equals("yes"))
				{
					bValue=true;
				}
				else if (placeholderReplaced.equals("no"))
				{
					bValue=false;
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' must be either 'yes' or 'no'");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Boolean(bValue));
			}
		}
		return bValue;
	}
	
	@Override
	public boolean setBoolean(String sName,boolean bValue) throws OmDeveloperException
	{
		Boolean bOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			bOldValue=new Boolean(getBoolean(sName));
		}
		boolean bOldAux=super.setBoolean(sName,bValue);
		placeholders.remove(sName);
		return bOldValue==null?bOldAux:bOldValue.booleanValue();
	}
	
	@Override
	public double getDouble(String sName) throws OmDeveloperException
	{
		double dValue=0.0;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			dValue=super.getDouble(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				try
				{
					dValue=Double.parseDouble(placeholderReplaced);
				}
				catch (NumberFormatException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' is not a valid double");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Double(dValue));
			}
		}
		return dValue;
	}
	
	@Override
	public double setDouble(String sName,double dValue) throws OmDeveloperException
	{
		Double dOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			dOldValue=new Double(getDouble(sName));
		}
		double dOldAux=super.setDouble(sName,dValue);
		placeholders.remove(sName);
		return dOldValue==null?dOldAux:dOldValue.doubleValue();
	}
	
	/**
	 * Get list of required attribute names; applied by setPropertiesFrom() that have not been set with
	 * a placeholder<br/><br/>
	 * <b>IMPORTANT</b>: Note that only required properties that have not been set with a placeholder are
	 * included in the list.<br/><br/>
	 * The reason for doing it that way is because properties set with placeholders has been removed from XML 
	 * before calling setPropertiesFrom() to avoid a format error, but still setPropertiesFrom() will throw 
	 * an error if any of the required properties from this list are not present in XML, so in order 
	 * to avoid that error we need to return only required attributes that have not been set with a 
	 * placeholder.<br/><br/>
	 * If you want to override this method you must be careful when including properties that allow 
	 * placeholders.
	 * @return List of required attribute names; applied by setPropertiesFrom() that have not been set with
	 * a placeholder
	 */
	@Override
	protected String[] getRequiredAttributes()
	{
		List<String> requiredAttributes=new ArrayList<String>();
		for (String requiredAttribute:super.getRequiredAttributes())
		{
			if (!placeholders.containsKey(requiredAttribute))
			{
				requiredAttributes.add(requiredAttribute);
			}
		}
		return requiredAttributes.toArray(new String[0]);
	}
	
	@Override
	public void init(QComponent parent,QDocument qd,Element eThis,boolean bImplicit) throws OmException
	{
		Map<String,String> removedAttributes=new HashMap<String,String>();
		
		// First we need to define and set 'id' before calling super.init
		if (!bImplicit && eThis.hasAttribute(PROPERTY_ID))
		{
			String id=eThis.getAttribute(PROPERTY_ID);
			
			// First we need to define 'id' property
			defineString(PROPERTY_ID,PROPERTYRESTRICTION_ID);
			
			// As 'id' property doesn't allow placeholders we can set it using superclass method
			super.setString(PROPERTY_ID,id);
			
			// We remove attribute 'id' before calling setPropertiesFrom method to avoid setting it again
			eThis.removeAttribute(PROPERTY_ID);
			removedAttributes.put(PROPERTY_ID,id);
		}
		
		// We do this trick to initialize placeholders before calling setPropertiesFrom method
		super.init(parent,qd,eThis,true);
		
		// Initialize placeholders needed
		if (!bImplicit)
		{
			for (String property:PROPERTIES_TO_INITIALIZE_PLACEHOLDERS)
			{
				if (eThis.hasAttribute(property))
				{
					String propertyValue=eThis.getAttribute(property);
					if (StandardQuestion.containsPlaceholder(propertyValue))
					{
						// We add a placeholder for this property
						placeholders.put(property,propertyValue);
						
						// We set this property with some value (null for example) to achieve that OM considers 
						// that it is set.
						// We need to do this because overriding isPropertySet method from 
						// om.stdquestion.QComponent it is not enough to achieve it because checkSetProperty 
						// private method from same class does the same check without calling it
						Class<?> type=getPropertyType(property);
						if (type.equals(String.class))
						{
							super.setString(property,null);
						}
						else if (type.equals(Integer.class))
						{
							super.setInteger(property,-1);
						}
						else if (type.equals(Double.class))
						{
							super.setDouble(property,0.0);
						}
						else if (type.equals(Boolean.class))
						{
							super.setBoolean(property,false);
						}
						
						// We remove attribute before calling setPropertiesFrom method to avoid a format error
						eThis.removeAttribute(property);
						removedAttributes.put(property,propertyValue);
					}
				}
			}
			setPropertiesFrom(eThis);
			
			// After calling setPropertiesFrom method we need to set again removed attributes
			for (Map.Entry<String,String> removedAttribute:removedAttributes.entrySet())
			{
				eThis.setAttribute(removedAttribute.getKey(),removedAttribute.getValue());
			}
		}
		
		// Specific initializations
		initializeSpecific(eThis);
		
		// Do now specific checks on properties set without using placeholders
		for (Map.Entry<String,PropertyCheck> check:checks.entrySet())
		{
			if (!placeholders.containsKey(check.getKey()))
			{
				Object value=null;
				Class<?> type=getPropertyType(check.getKey());
				if (type.equals(String.class))
				{
					value=super.getString(check.getKey());
				}
				else if (type.equals(Integer.class))
				{
					value=new Integer(super.getInteger(check.getKey()));
				}
				else if (type.equals(Double.class))
				{
					value=new Double(super.getDouble(check.getKey()));
				}
				else if (type.equals(Boolean.class))
				{
					value=new Boolean(super.getBoolean(check.getKey()));
				}
				check.getValue().check(value);
			}
		}
	}
	
	/**
	 * Does nothing.<br/><br/>
	 * Note that this method has been declared 'final' because we don't want it to be overriden.<br/><br/>
	 * The reason for doing that is that om.stdcomponent.uned components don't allow to check properties
	 * from this method, because properties aren't still defined due to initializations changes to support
	 * placeholders.<br/><br/>
	 * If you need specific initializations you can override initializeSpecific method instead.
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	@Override
	protected final void initSpecific(Element eThis) throws OmException
	{
	}
	
	/**
	 * Carries out initialisation specific to component.<br/><br/>
	 * Override this method if you need any.<br/><br/>
	 * However if you need to check some properties instead of checking them inside directly it is recommended 
	 * that you call "addSpecificCheck" method once for every property to be checked with an implementation of the
	 * PropertyCheck interface providing desired check.<br/><br/>
	 * The reason to doing it that way is to be sure that checks will work well even if you set a property using
	 * placeholders.<br/><br/>
	 * Default implementation of this method does nothing.
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	protected void initializeSpecific(Element eThis) throws OmException
	{
	}
	
	/**
	 * Add a specific check for some property.<br/><br/>
	 * Obviously property must be defined before calling this method.<br/><br/>
	 * It is recommended that you invoke this method from initializeSpecific method onece for every property with
	 * specific checks.
	 * @param propertyName Property's name
	 * @param propertyCheck Property's check
	 * @throws OmException
	 */
	protected void addSpecificCheck(String propertyName,PropertyCheck propertyCheck) throws OmException
	{
		if (!isPropertyDefined(propertyName))
		{
			StringBuffer error=new StringBuffer();
			error.append('<');
			error.append(getTagName());
			error.append(">: property '");
			error.append(propertyName);
			error.append("' has not been defined");
			throw new OmDeveloperException(error.toString());
		}
		if (propertyCheck==null)
		{
			StringBuffer error=new StringBuffer();
			error.append('<');
			error.append(getTagName());
			error.append(">: property check implementation for '");
			error.append(propertyName);
			error.append("' has not been provided");
			throw new OmDeveloperException(error.toString());
		}
		checks.put(propertyName,propertyCheck);
	}
	
	/**
	 * Builds up markers that are going to be displayed.
	 * @param qc Output variable that accepts question content
	 * @param bPlain True if in plain mode
	 * @param eEnsureSpaces &lt;div&gt; tag corresponding to this canvas
	 * @param dZoom Zoom
	 * @param sMarkerPrefix Marker prefix
	 * @throws OmException If whatever error occurs
	 */
	@Override
	protected void produceMarkers(QContent qc,boolean bPlain,Element eEnsureSpaces,double dZoom,
			String sMarkerPrefix) throws OmException
	{
		int index=0;
		for (om.stdcomponent.CanvasComponent.Marker m:getMarkers())
		{
			if (((Marker)m).isDisplay())
			{
				produceMarker(qc,bPlain,eEnsureSpaces,dZoom,sMarkerPrefix,m,index);
			}
			index++;
		}
	}
	
	@Override
	protected void produceMarker(QContent qc,boolean bPlain,Element eEnsureSpaces,double dZoom,
			String sMarkerPrefix,om.stdcomponent.CanvasComponent.Marker m,int index) throws OmException
	{
		Element eMarker=XML.createChild(eEnsureSpaces,"img");
		
		StringBuffer id=new StringBuffer();
		id.append(QDocument.ID_PREFIX);
		id.append(getID());
		id.append("_marker");
		id.append(index);
		eMarker.setAttribute("id",id.toString());
		
		StringBuffer src=new StringBuffer();
		src.append("%%RESOURCES%%/");
		src.append(sMarkerPrefix);
		if (!isEnabled())
		{
			src.append('d');
		}
		src.append(".gif");
		eMarker.setAttribute("src",src.toString());
		
		eMarker.setAttribute("class","canvasmarker");
		
		if(isEnabled())
		{
			eMarker.setAttribute("tabindex","0");
		}
		
		Element eScript=XML.createChild(eEnsureSpaces,"script");
		eScript.setAttribute("type","text/javascript");
		om.graph.World w=m.getWorld()==null?null:getWorld(m.getWorld());
		
		StringBuffer markerInitialization=new StringBuffer();
		markerInitialization.append("addOnLoad(function() { canvasMarkerInit('");
		markerInitialization.append(getID());
		markerInitialization.append("','");
		markerInitialization.append(QDocument.ID_PREFIX);
		markerInitialization.append("','");
		markerInitialization.append(m.getLabelJS().replaceAll("'","\\\\'"));
		markerInitialization.append("',");
		markerInitialization.append(getWorldFactors(w,dZoom));
		
		// Note that now we add the marker's index as a new argument to the canvasMarkerInit function
		// (this is an important change respect to om.stdcomponent.CanvasComponent implementation needed
		// because several changes have been done in JavaScript file 
		// om.stdcomponent.uned.CanvasComponent.js)
		markerInitialization.append(',');
		markerInitialization.append(index);
		
		markerInitialization.append("); });");
		XML.createText(eScript,markerInitialization.toString());
		
		StringBuffer name=new StringBuffer();
		name.append(QDocument.ID_PREFIX);
		name.append("canvasmarker_");
		name.append(getID());
		name.append('_');
		name.append(index);
		
		Element eInputX=XML.createChild(eEnsureSpaces,"input");
		eInputX.setAttribute("type","hidden");
		eInputX.setAttribute("value",Integer.toString((int)(m.getX()*dZoom)));
		StringBuffer nameX=new StringBuffer(name);
		nameX.append('x');
		eInputX.setAttribute("name",nameX.toString());
		eInputX.setAttribute("id",eInputX.getAttribute("name"));
		
		Element eInputY=XML.createChild(eEnsureSpaces,"input");
		eInputY.setAttribute("type","hidden");
		eInputY.setAttribute("value",Integer.toString((int)(m.getY()*dZoom)));
		StringBuffer nameY=new StringBuffer(name);
		nameY.append('y');
		eInputY.setAttribute("name",nameY.toString());
		eInputY.setAttribute("id",eInputY.getAttribute("name"));
		
		if(isEnabled())
		{
			qc.informFocusable(id.toString(),bPlain);
		}
	}
	
	/**
	 * Builds up marker lines that are going to be displayed.
	 * @param eEnsureSpaces &lt;div&gt; tag corresponding to this canvas
	 * @param dZoom Zoom
	 * @throws OmException If whatever error occurs
	 */
	@Override
	protected void produceMarkerLines(Element eEnsureSpaces,double dZoom)throws OmException
	{
		for (om.stdcomponent.CanvasComponent.MarkerLine ml:getLines())
		{
			if (((MarkerLine)ml).isDisplay())
			{
				produceMarkerLine(eEnsureSpaces,dZoom,ml);
			}
		}
	}
	
	/**
	 * Checks if there are markers to display.
	 * @return true if there markers to display, false otherwise
	 */
	@Override
	protected boolean hasDisplayedMarkers()
	{
		boolean displayedMarkers=false;
		for (om.stdcomponent.CanvasComponent.Marker m:getMarkers())
		{
			if (((Marker)m).isDisplay())
			{
				displayedMarkers=true;
				break;
			}
		}
		return displayedMarkers;
	}
	
	@Override
	public Color getColour(String sConstant)
	{
		Color c=super.getColour(sConstant);
		if (c==null && COLOR_CONSTANTS.containsKey(sConstant))
		{
			c=COLOR_CONSTANTS.get(sConstant);
		}
		return c;
	}
	
	@Override
	public void produceOutput(QContent qc, boolean bInit, boolean bPlain) throws OmException
	{
		boolean isVisible=isDisplayed();
		if (isVisible)
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			boolean questionTimeTesting=!sq.isQuestionTimeTestingException(this) && 
					!sq.isQuestionTimeTestingNestedException(this);
			if (questionTimeTesting)
			{
				for (QComponent qcAncestor:getAncestors()) {
					if (sq.isQuestionTimeTestingNestedException(qcAncestor))
					{
						questionTimeTesting=false;
						break;
					}
				}
			}
			if (questionTimeTesting)
			{
				String idAnswer=sq.peekForIdAnswer();
				isVisible=idAnswer==null?test():test(idAnswer);
			}
		}
		if (isVisible)
		{
			produceVisibleOutput(qc,bInit,bPlain);
		}
	}
	
	/**
	 * Tests if this canvas component match all conditions to be displayed.
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if this canvas component match all conditions to be displayed, false otherwise
	 */
	@Override
	public boolean test(String idAnswer)
	{
		return Tester.testAll((StandardQuestion)getQuestion(),this,idAnswer);
	}
	
	/**
	 * Tests if this canvas component match all conditions to be displayed.
	 * @return true if this canvas component match all conditions to be displayed, false otherwise
	 */
	@Override
	public boolean test()
	{
		return Tester.testAll((StandardQuestion)getQuestion(),this);
	}
}
