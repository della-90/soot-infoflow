package soot.jimple.infoflow.problems.conditions;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

/**
 * 
 * A generic class that performs a breadth-first search on a control-flow graph
 * (i.e. the data structure containing Nodes is FIFO).
 * 
 * @author Nicola Dellarocca
 *
 * @param <Node>
 *            The type of nodes to visit in the control-flow graph (e.g.
 *            {@link Unit} or {@link SootMethod}).
 */
public abstract class BreadthFirstSearch<Node> {

	protected IInfoflowCFG cfg;

	protected Set<Node> alreadyVisited;
	protected Queue<Node> nodesToVisit;
	protected Set<Node> result;

	/**
	 * Creates the search object.
	 * 
	 * @param cfg
	 *            The control-flow graph on which perform the search.
	 */
	public BreadthFirstSearch(IInfoflowCFG cfg) {
		this.cfg = cfg;

		alreadyVisited = new HashSet<>();
		nodesToVisit = new ArrayDeque<>();
		result = new HashSet<>();
	}

	/**
	 * Resets the object to its original state
	 */
	public void reset() {
		alreadyVisited.clear();
		nodesToVisit.clear();
		result.clear();
	}

	/**
	 * Gets the next node(s) starting from the current,
	 * 
	 * @param current
	 *            The current node.
	 * @return The collection of nodes to be visited later.
	 */
	protected abstract Collection<Node> nextNodes(Node current);

	/**
	 * Checks if the current node should be added to the result set.
	 * 
	 * @param node
	 *            The current node.
	 * @return <code>true</code> if it is a result, <code>false</code>
	 *         otherwise.
	 */
	protected abstract boolean isResult(Node node);

	/**
	 * Performs the breadth-first search.
	 * 
	 * @param start
	 *            The starting node.
	 * @param stopFirstResult
	 *            <code>true</code> if the search should stop after the first
	 *            result is found.
	 * @return The set of nodes marked as result.
	 */
	public Set<Node> search(Node start, boolean stopFirstResult) {
		nodesToVisit.add(start);

		while (!nodesToVisit.isEmpty()) {
			Node current = nodesToVisit.remove();

			// Skip already visited nodes
			if (!(alreadyVisited.add(current)))
				continue;

			// Check if it is a result
			if (isResult(current)) {
				result.add(current);

				if (stopFirstResult) {
					break;
				}
			}

			// Get next nodes
			Collection<Node> nextNodes = nextNodes(current);
			for (Node nextNode : nextNodes) {
				// Check if it has been already visited
				if (!alreadyVisited.contains(nextNode)) {
					nodesToVisit.add(nextNode);
				}
			}
		}

		return result;
	}

}
