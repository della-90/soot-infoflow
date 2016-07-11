/**
 * 
 */
package soot.jimple.infoflow.problems.conditions;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

/**
 * 
 * @author Nicola Dellarocca
 *
 */
public abstract class DepthFirstSearcher<Node> extends AbstractSearch<Node> {

	/**
	 * A LIFO data structure holding the nodes to visit (ordered in a
	 * visit-order fashion).
	 */
	protected Deque<Node> mNodesToVisit;

	/**
	 * {@inheritDoc}
	 */
	public DepthFirstSearcher(IInfoflowCFG cfg) {
		super(cfg);
		mNodesToVisit = new ArrayDeque<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Node getNextScheduledNode() {
		if (mNodesToVisit.isEmpty())
			return null;
		return mNodesToVisit.pop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void scheduleNodesVisit(Collection<Node> nodes) {
		for (Node node : nodes) {
			mNodesToVisit.push(node);
		}
	}

}
