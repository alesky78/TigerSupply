package it.spaghettisource.tigersupply.engine.impl.scene.definition;

public class Image {
	
	private String alias;

	public Image(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("image-> alias:"+alias);		
		return buffer.toString();
	}	
	
}
