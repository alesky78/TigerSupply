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

import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmProperties;
import it.spaghettisource.tigersupply.game.scene.builder.definition.AlgorithmPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.EnemyPrototype;
import it.spaghettisource.tigersupply.game.scene.builder.definition.GenerateEvent;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Horde;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Image;
import it.spaghettisource.tigersupply.game.scene.builder.definition.PointDefinition;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Scale;
import it.spaghettisource.tigersupply.game.scene.builder.definition.Speed;


/**
 * implementation of the sax parser for the level
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class EnemyDataBuilderSaxXml extends DefaultHandler implements EnemyDataBuilder {

	private final String TAG_HORDER = "horde";
	private final String TAG_GENERATE_EVENT = "generateEvent";
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
	
	private String sourceFile;
	
	private List<Horde> hordes;
	private Horde actualHorde;
	
	private List<EnemyPrototype> enemyPrototypes;
	private EnemyPrototype actualEnemyPrototype;	
	
	private List<AlgorithmPrototype> algorithmsPrototypes;
	private AlgorithmPrototype actualAlgorithmsPrototype;	
	private AlgorithmProperties actualAlgoPropertie;
	private List<PointDefinition> actualListOfPoints;		
	
	
	public EnemyDataBuilderSaxXml(String sourceFile){		
		this.sourceFile =sourceFile;
		hordes = new ArrayList<Horde>();
		enemyPrototypes = new ArrayList<EnemyPrototype>();
		algorithmsPrototypes = new ArrayList<AlgorithmPrototype>();
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
		if(localName.equals(TAG_HORDER)){	//it is a new horde
			actualHorde = new Horde();
			hordes.add(actualHorde);
		}else if(localName.equals(TAG_GENERATE_EVENT)){	//it is a new generateEvent
			GenerateEvent event = new GenerateEvent(atts.getValue("name"), atts.getValue("time"));
			actualHorde.setEvent(event);
		}else if(localName.equals(TAG_ENEMY)){	//it is a enemy
			EnemyDefinition enemy = new EnemyDefinition(atts.getValue("enemyPrototype"), atts.getValue("algorithmPrototype"), atts.getValue("posX"), atts.getValue("posY"),atts.getValue("posZ"));
			actualHorde.addEnemy(enemy);
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
		}
	}	

	public void endDocument() throws SAXException {
	}		

	public List<Horde> buildHordes() {
		return hordes;
	}

	public List<EnemyPrototype> buildEnemyPrototypes() {
		return enemyPrototypes;
	}

	public List<AlgorithmPrototype> buildAlgorithmPrototypes() {
		return algorithmsPrototypes;
	}

}
