package it.spaghettisource.tigersupply.engine.impl.scene.definition;

public class EnemyPrototype {
	
	private String name;
	private String type;
	private String className;
	private Image image;
	private Speed speed;
	private Scale scale;	

	public EnemyPrototype(String name, String type, String className) {
		this.name = name;
		this.type = type;
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
	
	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		this.scale = scale;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("EnemyPrototype-> name:"+name+" type:"+type+" class:"+className +" "+speed+" "+image+" "+scale);				
		return buffer.toString();
	}	
	
	
}
