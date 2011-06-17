package org.vamdc.validator.interfaces;

/**
 * Interface for document element 
 * @author doronin
 *
 */
public abstract class DocumentElement {
	public static enum ElementTypes{
		Atom,
		AtomicState,
		Molecule,
		MolecularState,
		Particle,
		Solid,
		CollisionalTransition,
		RadiativeTransition,
		NonRadiativeTransition,
		Source,
		Method,
		Function
	}
	
	/**
	 * @return current element name
	 */
	public abstract String getName();
	
	/**
	 * @return current element first symbol line
	 */
	public abstract long getFirstLine();
	
	/**
	 * @return current element first symbol column
	 */
	public abstract int getFirstCol();
	
	/**
	 * @return current element last symbol line in document
	 */
	public abstract long getLastLine();
	
	/**
	 * @return current element last symbol column in document
	 */
	public abstract int getLastCol();
}
