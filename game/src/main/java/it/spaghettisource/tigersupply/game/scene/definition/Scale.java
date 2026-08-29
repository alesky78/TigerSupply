package it.spaghettisource.tigersupply.game.scene.definition;

public class Scale {
	
	private String scale;

	
	public Scale(String scale) {
		this.scale = scale;
	}

	public float getScale() {
		return Float.parseFloat(scale);
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("scale-> scale:"+scale);		
		return buffer.toString();
	}		

}
