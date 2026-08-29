package it.spaghettisource.tigersupply.engine.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * this is a dynabean pojo implementation
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class DynaProperties {

	private HashMap<String, Object> properties;

	public DynaProperties(){
		properties = new HashMap<String, Object>( );
	}
	
	public DynaProperties(HashMap<String, Object> properties){
		this.properties = properties;
	}	
	
	
	public boolean contains( String name ) {
		return properties.containsKey( name );
	}

	public List<?> getList( String name ) {
		return ( List<?> ) properties.get( name );
	}

	public Map<?, ?> getMap( String name ) {
		return ( Map<?, ?> ) properties.get( name );
	}

	public String getString( String name ) {
		return ( String ) properties.get( name );
	}

	public int getInt( String name ) {
		return ( ( Integer ) properties.get( name ) ).intValue( );
	}

	public long getLong( String name ) {
		return ( ( Long ) properties.get( name ) ).longValue( );
	}
	
	public float getFloat( String name ) {
		return ( ( Float ) properties.get( name ) ).floatValue();
	}	
	
	public double getDouble( String name ) {
		return ( ( Double ) properties.get( name ) ).doubleValue();
	}		

	public boolean getBoolean( String name ) {
		return ( ( Boolean ) properties.get( name ) ).booleanValue( );
	}
	
	public Object getObject( String name ) {
		return properties.get( name );
	}	

	public void setList( String name, List<?> list ) {
		properties.put( name, list );
	}

	public void setMap( String name, Map<?, ?> map ) {
		properties.put( name, map );
	}

	public void setString( String name, String value ) {
		properties.put( name, value );
	}

	public void setInt( String name, int value ) {
		properties.put( name, new Integer( value ) );
	}

	public void setLong( String name, long value ) {
		properties.put( name, new Long( value ) );
	}

	public void setDouble( String name, double value ) {
		properties.put( name, new Double( value ) );
	}	  
	
	public void setFloat( String name, float value ) {
		properties.put( name, new Float( value ) );
	}		

	public void setBoolean( String name, boolean value ) {
		properties.put( name, new Boolean( value ) );
	}
	
	public void setObject( String name, Object value ) {
		properties.put(name,value);
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
