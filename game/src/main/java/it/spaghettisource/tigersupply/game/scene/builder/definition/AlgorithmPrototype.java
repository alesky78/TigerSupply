package it.spaghettisource.tigersupply.game.scene.builder.definition;

public class AlgorithmPrototype {

	private String name;
	private String className;
	private AlgorithmProperties properties;
	
	public AlgorithmPrototype(String name, String className) {
		super();
		this.name = name;
		this.className = className;
		properties = new AlgorithmProperties();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public AlgorithmProperties getProperties() {
		return properties;
	}

	public void setProperties(AlgorithmProperties properties) {
		this.properties = properties;
	}	

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("AlgorithmPrototype-> name:"+name+" className:"+className+" "+properties);		
		return buffer.toString();
	}		
	
	
}
