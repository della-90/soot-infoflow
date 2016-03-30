package soot.jimple.infoflow.problems.conditions;

import soot.jimple.Stmt;

/**
 * A contract that all conditions must respect.
 * 
 * @author Nicola Dellarocca
 *
 */
public interface ICondition {

	/**
	 * Checks whether the provided statement makes the condition satisfied or
	 * not.
	 * 
	 * @param stmt
	 *            The statement to check.
	 * @return {@code true} if it is satisfied, {@code false} otherwise.
	 */
	public boolean isSatisfied(Stmt stmt);

	/**
	 * Checks whether this object is equal to {@code other}.
	 * 
	 * @param other
	 *            The object to test for equality.
	 * @return {@code true} if they are equal, {@code false} otherwise.
	 */
	public boolean equals(ICondition other);

}
