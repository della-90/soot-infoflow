package soot.jimple.infoflow.problems.conditions;

import java.util.HashSet;
import java.util.Set;

import soot.jimple.Stmt;

/**
 * This class represents a set of condition alternatives, i.e. it is satisfied
 * if at least one of the alternatives is satisfied.
 * 
 * @author Nicola Dellarocca
 *
 */
public class ConditionAlternatives implements ICondition {

	private Set<ICondition> mAlternatives;

	public ConditionAlternatives() {
		mAlternatives = new HashSet<>();
	}

	public ConditionAlternatives(Set<ICondition> alternatives) {
		this.mAlternatives = new HashSet<>(alternatives);
	}

	public boolean addAlternative(ICondition alternative) {
		return this.mAlternatives.add(alternative);
	}

	@Override
	public boolean isSatisfied(Stmt stmt) {
		for (ICondition a : mAlternatives) {
			if (a.isSatisfied(stmt))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(ICondition other) {
		if (other == this)
			return true;
		if (!(other instanceof ConditionAlternatives))
			return false;
		
		ConditionAlternatives o = (ConditionAlternatives) other;
		return this.mAlternatives.equals(o.mAlternatives);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		int n = mAlternatives.size();
		for (ICondition c : mAlternatives) {
			builder.append(c);
			if (--n > 0)
				builder.append(" OR ");
		}
		
		return builder.toString();
	}
}
