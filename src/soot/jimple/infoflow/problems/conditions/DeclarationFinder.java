package soot.jimple.infoflow.problems.conditions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

/**
 * Performs a breadth-first backwards search looking for the statement
 * responsible for the assignment of a variable.
 * 
 * @author Nicola Dellarocca
 *
 */
public class DeclarationFinder extends BreadthFirstSearch<Unit> {

	protected Value value;

	/**
	 * Creates an object that can be used to search for the declaration of a
	 * variable.
	 * 
	 * @param cfg
	 *            The control-flow graph on which the search should be
	 *            performed.
	 * @param value
	 *            The value for which this object will perform a declaration
	 *            search.
	 */
	public DeclarationFinder(IInfoflowCFG cfg, Value value) {
		super(cfg);
		this.value = value;
	}

	@Override
	protected Collection<Unit> nextNodes(Unit current) {
		Set<Unit> result = new HashSet<>();

		Collection<Unit> predecessors = cfg.getPredsOf(current);

		if (!predecessors.isEmpty()) {
			// Navigate the CFG backwards
			result.addAll(cfg.getPredsOf(current));
		} else {
			// If it is a start node, let's add the caller of this method
			Collection<Unit> callers = cfg.getCallersOf(
					cfg.getMethodOf(current));
			result.addAll(callers);
		}

		return result;
	}

	@Override
	protected boolean isResult(Unit node) {
		if (node instanceof AssignStmt) {
			AssignStmt assign = (AssignStmt) node;

			return assign	.getLeftOp()
							.equals(this.value);
		}
		return false;
	}

}
