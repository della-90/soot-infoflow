package soot.jimple.infoflow.problems.conditions;

import java.util.Arrays;
import java.util.List;

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.UnknownType;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.util.Switch;

/**
 * Represents a condition that must be satisfied from the taint analysis.
 * 
 * @author Nicola Dellarocca
 *
 */
public class Condition implements ICondition {

	public static class Placeholder extends Constant {

		// We use a singleton to ease comparisons
		private static Placeholder instance = new Placeholder();

		private Placeholder() {
		}

		public static Placeholder v() {
			return instance;
		}

		@Override
		public Type getType() {
			return UnknownType.v();
		}

		@Override
		public void apply(Switch arg0) {
			return;
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}

		@Override
		public String toString() {
			return "_";
		}

	}

	/**
	 * Represents a parameter value to be ignored for the condition
	 * satisfaction. It is a singleton, so it can be compared through the "=="
	 * operator.
	 */
	public static final Object PLACEHOLDER = null;

	private String clazz;
	private String returnType;
	private String methodName;
	private Constant[] args;

	/**
	 * 
	 * @param clazz
	 * @param returnType
	 * @param name
	 * @param args
	 * @throws IllegalArgumentException
	 *             if clazz, returnType or name are null.
	 */
	public Condition(String clazz, String returnType, String name,
			Constant[] args) throws IllegalArgumentException {
		if (clazz == null || returnType == null || name == null) {
			throw new IllegalArgumentException();
		}

		this.clazz = clazz;
		this.returnType = returnType;
		this.methodName = name;
		this.args = args;

	}

	@Override
	public boolean isSatisfied(Stmt stmt) {
		if (stmt == null) {
			throw new IllegalArgumentException(
					"You must provide a valid statement");
		}

		if (stmt.containsInvokeExpr()) {
			InvokeExpr ie = stmt.getInvokeExpr();
			SootMethod sm = ie.getMethod();
			SootClass sc = sm.getDeclaringClass();
			List<Value> argList = ie.getArgs();

			return sc	.getName()
						.equals(this.clazz)
					&& sm	.getReturnType()
							.toString()
							.equals(this.returnType)
					&& sm	.getName()
							.equals(this.methodName)
					&& checkParams(argList);

		}
		return false;
	}

	/**
	 * Checks whether the provided argument list is compatible to the ones
	 * passed to the constructor of this object.
	 * 
	 * @param argList
	 * @return
	 */
	private boolean checkParams(List<Value> argList) {
		// No argument provided
		if (argList == null || argList.size() == 0) {
			return this.args == null || this.args.length == 0;
		}

		// Arguments must be equal in number
		if (argList.size() != this.args.length)
			return false;

		for (int i = 0; i < this.args.length; i++) {
			Constant arg = this.args[i];

			// We will ignore placeholders
			if (arg.equals(Placeholder.v())) {
				continue;
			}

			// If corresponding arguments are different, return false
			if (!arg.equals(argList.get(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(ICondition other) {
		if (this == other)
			return true;

		if (!(other instanceof Condition))
			return false;

		Condition o = (Condition) other;

		// Check fields
		return o.clazz.equals(this.clazz)
				&& o.returnType.equals(this.returnType)
				&& o.methodName.equals(this.methodName)
				&& Arrays.equals(this.args, o.args);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder('<');
		builder.append(clazz);
		builder.append(": ");
		builder.append(returnType);
		builder.append(' ');
		builder.append(methodName);
		builder.append('(');
		for (int i = 0; i < args.length; i++) {
			builder.append(args[i]);
			if (i < args.length - 1) {
				builder.append(',');
			}
		}
		builder.append(')');
		builder.append('>');
		return builder.toString();
	}
}