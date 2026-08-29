package it.spaghettisource.tigersupply.engine.impl.scene.definition;

public class EnemyDefinition {

	private String enemyPrototype;
	private String algoritmPrototype;
	private String posX;
	private String posY;
	private String posZ;	
	
	public EnemyDefinition(String enemyPrototype, String algoritmPrototype, String posX,String posY,String posZ) {
		this.enemyPrototype = enemyPrototype;
		this.algoritmPrototype = algoritmPrototype;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;		
	}
	
	public String getEnemyPrototype() {
		return enemyPrototype;
	}
	public void setEnemyPrototype(String enemyPrototype) {
		this.enemyPrototype = enemyPrototype;
	}
	public String getAlgoritmPrototype() {
		return algoritmPrototype;
	}
	public void setAlgoritmPrototype(String algoritmPrototype) {
		this.algoritmPrototype = algoritmPrototype;
	}
	public int getPosX() {
		return Integer.parseInt(posX);
	}
	public void setPosX(String posX) {
		this.posX = posX;
	}
	public int getPosY() {
		return Integer.parseInt(posY);
	}
	public void setPosY(String posY) {
		this.posY = posY;
	}	

	public int getPosZ() {
		return Integer.parseInt(posZ);
	}

	public void setPosZ(String posZ) {
		this.posZ = posZ;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("enemy-> type:"+enemyPrototype+" algo:"+algoritmPrototype+" x:"+posX+" y:"+posY);		
		return buffer.toString();
	}	


}
