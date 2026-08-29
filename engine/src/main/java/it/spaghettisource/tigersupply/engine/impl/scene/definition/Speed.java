package it.spaghettisource.tigersupply.engine.impl.scene.definition;

public class Speed {
	
	private String x;
	private String y;
	
	public Speed(String x, String y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return Integer.parseInt(x);
	}

	public void setX(String x) {
		this.x = x;
	}

	public int getY() {
		return Integer.parseInt(y);
	}

	public void setY(String y) {
		this.y = y;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("speed-> x:"+x+" y:"+y);		
		return buffer.toString();
	}		

}
