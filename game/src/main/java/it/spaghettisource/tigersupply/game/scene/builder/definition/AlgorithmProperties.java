package it.spaghettisource.tigersupply.game.scene.builder.definition;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AlgorithmProperties {

	private HashMap<String, Object> properties = new HashMap<String, Object>( );
	private HashMap<String, List<PointDefinition>> 	listProperties = new HashMap<String, List<PointDefinition>>( );	


	public String getString( String name ) {
		return ( String ) properties.get( name );
	}


	public void setString( String name, String value ) {
		properties.put( name, value );
	}

	public List<PointDefinition> getListPoints( String name ) {
		return listProperties.get( name );
	}

	public void setListPoints( String name, List<PointDefinition> list ) {
		listProperties.put( name, list );
	}
	
	
	public HashMap<String, Object> getSingleProperties(){
		return properties;
	}
	
	public HashMap<String, List<PointDefinition>> getListProperties(){
		return listProperties;
	}
		
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		Set<String> keys = properties.keySet();
		buffer.append("properties-> keys:");
		for (String key : keys) {
			buffer.append(key+";"+properties.get(key)+" - ");
		}

		return buffer.toString();
	}		  


}
