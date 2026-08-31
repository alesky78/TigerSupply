package it.spaghettisource.tigersupply.engine.entity.logic;


/**
 * Base class for {@link UpdateAlgorithm} implementations, providing small helpers to parse the textual
 * values held by a {@link it.spaghettisource.tigersupply.engine.utils.DynaProperties} during
 * {@link #init(it.spaghettisource.tigersupply.engine.utils.DynaProperties)}.
 *
 * @author Alessandro D'Ottavio
 *
 */
public abstract class AbstractUpdateAlgorithm implements UpdateAlgorithm {

	/**
	 * Parses a property value as an {@code int}.
	 *
	 * @param value the textual value to parse
	 * @return the parsed integer
	 * @throws NumberFormatException if {@code value} is not a valid integer
	 */
	public int getInt(String value){
		return Integer.parseInt(value);
	}

	/**
	 * Parses a property value as a {@code double}.
	 *
	 * @param value the textual value to parse
	 * @return the parsed double
	 * @throws NumberFormatException if {@code value} is not a valid double
	 */
	public double getDouble(String value){
		return Double.parseDouble(value);
	}	
	
	/**
	 * Parses a property value as a {@code float}.
	 *
	 * @param value the textual value to parse
	 * @return the parsed float
	 * @throws NumberFormatException if {@code value} is not a valid float
	 */
	public float getFloat(String value){
		return Float.parseFloat(value);
	}		
	
	
}
