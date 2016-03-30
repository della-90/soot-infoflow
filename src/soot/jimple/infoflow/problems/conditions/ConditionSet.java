package soot.jimple.infoflow.problems.conditions;

import java.util.HashSet;
import java.util.Set;

import soot.Unit;
import soot.jimple.Stmt;

/**
 * Represents a set of {@link Condition}s. It is satisfied only if all the
 * condition stored inside this object are satisfied.
 * 
 * @author Nicola Dellarocca
 *
 */
public class ConditionSet {

	private Set<ICondition> mConditions;

	/**
	 * Creates an empty set of conditions.
	 */
	public ConditionSet() {
		mConditions = new HashSet<>();
	}

	/**
	 * Creates a new set of conditions by copying the set specified as a
	 * parameter.
	 * 
	 * @param conditions
	 *            The set of conditions to initially add to this object.
	 */
	public ConditionSet(Set<ICondition> conditions) {
		this.mConditions = new HashSet<>(conditions);
	}

	/**
	 * Adds the condition to the condition set only if it is not already
	 * present.
	 * 
	 * @param c
	 *            The condition to add.
	 * @return {@code true} if {@code c} has been successfully inserted,
	 *         {@code false} otherwise (e.g. because an equal condition was
	 *         already present)
	 */
	public boolean addCondition(ICondition c) {
		return this.mConditions.add(c);
	}

	/**
	 * Checks whether all the conditions stored inside this object are
	 * satisfied.
	 * 
	 * @return <code>false</code> if at least one condition is not satisfied,
	 *         <code>true</code> otherwise.
	 */

	public boolean isSatisfied(Unit unit) {
		if (!(unit instanceof Stmt)) {
			return false;
		}

		Stmt stmt = (Stmt) unit;

		for (ICondition c : mConditions) {
			/*
			 * If there is at least an unsatisfied condition then we return
			 * false
			 */
			if (!c.isSatisfied(stmt)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append('{');
		builder.append('\n');

		for (ICondition c : this.mConditions) {
			builder.append('\t');
			builder.append(c.toString());
			builder.append('\n');
		}
		builder.append('}');
		return builder.toString();
	}

}
