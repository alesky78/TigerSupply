package it.spaghettisource.tigersupply.game.scene.definition;

public class PointDefinition {

	private String x,y;

	public PointDefinition(String x, String y) {
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
	
	
	
	
	
}
