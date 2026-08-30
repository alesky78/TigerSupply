package it.spaghettisource.tigersupply.game.scene.definition;

public class EnemyDefinition {

	private String enemyPrototype;
	private String algorithmPrototype;
	private String posX;
	private String posY;
	private String posZ;	
	
	public EnemyDefinition(String enemyPrototype, String algorithmPrototype, String posX,String posY,String posZ) {
		this.enemyPrototype = enemyPrototype;
		this.algorithmPrototype = algorithmPrototype;
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
	public String getAlgorithmPrototype() {
		return algorithmPrototype;
	}
	public void setAlgorithmPrototype(String algorithmPrototype) {
		this.algorithmPrototype = algorithmPrototype;
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
		buffer.append("enemy-> type:"+enemyPrototype+" algo:"+algorithmPrototype+" x:"+posX+" y:"+posY);		
		return buffer.toString();
	}	


}
