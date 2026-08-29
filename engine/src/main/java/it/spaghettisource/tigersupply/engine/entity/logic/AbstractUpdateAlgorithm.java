package it.spaghettisource.tigersupply.engine.entity.logic;


public abstract class AbstractUpdateAlgorithm implements UpdateAlgorithm {

	public int getInt(String value){
		return Integer.parseInt(value);
	}

	public double getDouble(String value){
		return Double.parseDouble(value);
	}	
	
	public float getFloat(String value){
		return Float.parseFloat(value);
	}		
	
	
}
