package it.spaghettisource.tigersupply.game.scene.builder;


import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import it.spaghettisource.tigersupply.game.scene.builder.definition.ActionDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmProperties;
import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.CompletionEvent;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Image;
import it.spaghettisource.tigersupply.game.scene.builder.definition.PointDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.MessageDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Scale;
import it.spaghettisource.tigersupply.game.scene.builder.definition.ScriptDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Speed;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Step;
import it.spaghettisource.tigersupply.game.scene.builder.definition.WindowDefinition;


/**
 * implementation of the sax parser for the level
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EnemyDataBuilderSaxXml extends DefaultHandler implements EnemyDataBuilder {

	private final String TAG_STEP = "step";
	private final String TAG_ACTION = "action";
	private final String TAG_COMPLETION_EVENT = "completionEvent";
	private final String TAG_ENEMY = "enemy";	

	private final String TAG_ENEMY_PROTO ="enemyPrototype";
	private final String TAG_ENEMY_SPEED ="speed";
	private final String TAG_ENEMY_IMAGE ="image";
	private final String TAG_ENEMY_SCALE ="scale";	
	
	private final String TAG_ALGO_PROTO ="algorithmPrototype";
	private final String TAG_ALGO_PROPERTIES ="algorithmProperties";
	private final String TAG_ALGO_PROPERTY_SIMPLE ="property";
	private final String TAG_ALGO_PROPERTY_LIST_POINT ="listPoints";
	private final String TAG_ALGO_PROPERTY_LIST_POINT_ENTRY ="point";		

	private final String TAG_SCRIPT ="script";
	private final String TAG_WINDOW ="window";
	private final String TAG_MESSAGE ="message";
	private final String TAG_LINE ="line";
	
	private String sourceFile;
	
	private List<Step> steps;
	private Step actualStep;
	private ActionDefinition actualAction;
	
	private List<EnemyPrototype> enemyPrototypes;
	private EnemyPrototype actualEnemyPrototype;	
	
	private List<AlgorithmPrototype> algorithmsPrototypes;
	private AlgorithmPrototype actualAlgorithmsPrototype;	
	private AlgorithmProperties actualAlgoPropertie;
	private List<PointDefinition> actualListOfPoints;		

	private List<ScriptDefinition> scripts;
	private ScriptDefinition actualScript;
	private MessageDefinition actualMessage;
	private StringBuilder lineBuffer;
	private boolean capturingLine;
	
	
	public EnemyDataBuilderSaxXml(String sourceFile){		
		this.sourceFile =sourceFile;
		steps = new ArrayList<Step>();
		enemyPrototypes = new ArrayList<EnemyPrototype>();
		algorithmsPrototypes = new ArrayList<AlgorithmPrototype>();
		scripts = new ArrayList<ScriptDefinition>();
	}

	public void parse() throws Exception{

		SAXParserFactory spf = SAXParserFactory.newInstance();
	    spf.setNamespaceAware(true);
	    SAXParser saxParser = spf.newSAXParser();
	    XMLReader xmlReader = saxParser.getXMLReader();
	    xmlReader.setContentHandler(this);
	    InputSource source = new InputSource(EnemyDataBuilderSaxXml.class.getClassLoader().getResourceAsStream(sourceFile));
	    System.out.println("load enemy data from file:"+sourceFile);
	    xmlReader.parse(source);
	}

	public void startDocument() throws SAXException {	
	}

	public void startElement(String namespaceURI,String localName,String qName, Attributes atts) throws SAXException {
		if(localName.equals(TAG_STEP)){	//it is a new step
			actualStep = new Step();
			steps.add(actualStep);
		}else if(localName.equals(TAG_ACTION)){	//it is a new action inside the current step
			actualAction = new ActionDefinition(atts.getValue("type"));
			for (int i = 0; i < atts.getLength(); i++) {
				String attrName = atts.getLocalName(i);
				if(!"type".equals(attrName)){
					actualAction.setProperty(attrName, atts.getValue(i));
				}
			}
			actualStep.addAction(actualAction);
		}else if(localName.equals(TAG_COMPLETION_EVENT)){	//it is the completion event of the current step
			CompletionEvent completion = new CompletionEvent(atts.getValue("name"), atts.getValue("time"));
			actualStep.setCompletion(completion);
		}else if(localName.equals(TAG_ENEMY)){	//it is an enemy inside the current action
			EnemyDefinition enemy = new EnemyDefinition(atts.getValue("enemyPrototype"), atts.getValue("algorithmPrototype"), atts.getValue("posX"), atts.getValue("posY"),atts.getValue("posZ"));
			actualAction.addEnemy(enemy);
		}else if(localName.equals(TAG_ENEMY_PROTO)){	//it is a new enemy prototype
			actualEnemyPrototype = new EnemyPrototype(atts.getValue("name"), atts.getValue("type"), atts.getValue("class"));
			enemyPrototypes.add(actualEnemyPrototype);
		}else if(localName.equals(TAG_ENEMY_SPEED)){	//it is speed of new enemy prototype
			Speed speed = new Speed(atts.getValue("x"), atts.getValue("y"));
			actualEnemyPrototype.setSpeed(speed);
		}else if(localName.equals(TAG_ENEMY_IMAGE)){	//it is image of new enemy prototype
			Image img = new Image(atts.getValue("alias"));
			actualEnemyPrototype.setImage(img);
		}else if(localName.equals(TAG_ENEMY_SCALE)){	//it is image of new enemy prototype
			Scale scl = new Scale(atts.getValue("value"));
			actualEnemyPrototype.setScale(scl);
		}else if(localName.equals(TAG_ALGO_PROTO)){	//it is a new algorithm prototype
			actualAlgorithmsPrototype = new AlgorithmPrototype(atts.getValue("name"), atts.getValue("class"));
			algorithmsPrototypes.add(actualAlgorithmsPrototype);
		}else if(localName.equals(TAG_ALGO_PROPERTIES)){	//it is properties of algorithm prototype
			actualAlgoPropertie = new AlgorithmProperties();
			actualAlgorithmsPrototype.setProperties(actualAlgoPropertie);
		}else if(localName.equals(TAG_ALGO_PROPERTY_SIMPLE)){	//it is property simple of properteies
			actualAlgoPropertie.setString(atts.getValue("name"), atts.getValue("value"));
		}else if(localName.equals(TAG_ALGO_PROPERTY_LIST_POINT)){	//it is property list of properteies
			actualListOfPoints = new ArrayList<PointDefinition>();
			actualAlgoPropertie.setListPoints(atts.getValue("name"), actualListOfPoints);
		}else if(localName.equals(TAG_ALGO_PROPERTY_LIST_POINT_ENTRY)){		//it is property list of properteies
			actualListOfPoints.add(new PointDefinition(atts.getValue("posX"),atts.getValue("posY")));
		}else if(localName.equals(TAG_SCRIPT)){	//it is a new dialogue script
			actualScript = new ScriptDefinition(atts.getValue("name"));
			scripts.add(actualScript);
		}else if(localName.equals(TAG_WINDOW)){	//it is the window of the current script
			WindowDefinition window = new WindowDefinition(atts.getValue("posX"), atts.getValue("posY"), atts.getValue("width"), atts.getValue("height"), atts.getValue("portraitWidth"), atts.getValue("charDelay"));
			actualScript.setWindow(window);
		}else if(localName.equals(TAG_MESSAGE)){	//it is a new message inside the current script
			actualMessage = new MessageDefinition(atts.getValue("speaker"), atts.getValue("portrait"));
			actualScript.addMessage(actualMessage);
		}else if(localName.equals(TAG_LINE)){	//it is a text line inside the current message
			lineBuffer = new StringBuilder();
			capturingLine = true;
		}
	}	

	public void characters(char[] ch, int start, int length) throws SAXException {
		if(capturingLine){
			lineBuffer.append(ch, start, length);
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if(localName.equals(TAG_LINE)){	//finalize the current text line
			actualMessage.addLine(lineBuffer.toString());
			capturingLine = false;
		}
	}

	public void endDocument() throws SAXException {
	}		

	public List<Step> buildSteps() {
		return steps;
	}

	public List<EnemyPrototype> buildEnemyPrototypes() {
		return enemyPrototypes;
	}

	public List<AlgorithmPrototype> buildAlgorithmPrototypes() {
		return algorithmsPrototypes;
	}

	public List<ScriptDefinition> buildScripts() {
		return scripts;
	}

}
