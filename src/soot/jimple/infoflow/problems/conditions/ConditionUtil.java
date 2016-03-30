package soot.jimple.infoflow.problems.conditions;

import soot.Unit;

public class ConditionUtil {
	
	protected static ConditionSet conditions;
	
	public static void setConditions(ConditionSet conditions) {
		ConditionUtil.conditions = conditions;
	}
	
	public static boolean doesSatisfyConditions(Unit stmt) throws IllegalStateException {
		if (stmt == null)
			throw new IllegalStateException();
		
		return ConditionUtil.conditions.isSatisfied(stmt);
	}
	
}
