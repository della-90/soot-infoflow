/**
 * 
 */
package soot.jimple.infoflow.problems.conditions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import polyglot.ast.Stmt;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

/**
 * 
 * @author Nicola Dellarocca
 *
 * @param <Node>
 */
public abstract class AbstractSearch<Node> {

	protected IInfoflowCFG mCfg;

	protected Set<Node> mAlreadyVisited;

	protected Set<Node> mResults;
	
	protected NodeVisitedListener<Node> listener;
	
	/**
	 * 
	 * @author Nicola Dellarocca
	 *
	 * @param <Node>
	 */
	public interface NodeVisitedListener<Node> {
		void visited(Node node);
	}

	/**
	 * Creates the searcher.
	 * 
	 * @param cfg
	 *            The control-flow graph on which perform the search.
	 */
	public AbstractSearch(IInfoflowCFG cfg) {
		this.mCfg = cfg;

		mAlreadyVisited = new HashSet<>();
		mResults = new HashSet<>();
	}

	/**
	 * Resets the object to its original state
	 */
	public void reset() {
		this.mAlreadyVisited.clear();
		this.mResults.clear();
	}

	/**
	 * Gets the next node(s) that should be visited starting from the current
	 * (typically the successors or predecessors).
	 * 
	 * @param current
	 *            The current node.
	 * @return The collection of nodes to be visited after <code>current</code>.
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
	 * Returns the successive node to visit. Note that if the node has already
	 * been visited, it will not be visited again.
	 * 
	 * Subclasses can implement this method in different ways to perform
	 * different kinds of searches. For instance one can use a FIFO data
	 * structure to allow a breadth-first search or a LIFO data structure to
	 * implement a depth-first search.
	 * 
	 * This method is automatically invoked by {@link AbstractSearch} class, so
	 * you should not use it.
	 * 
	 * @return The next node to inspect.
	 */
	protected abstract Node getNextScheduledNode();

	/**
	 * Schedules nodes for a visit that will occur later. The nodes are exactly
	 * the same returned by {@link #nextNodes(Object)}.
	 * 
	 * This method is automatically invoked by {@link AbstractSearch} class, so
	 * you should use it.
	 * 
	 * @param nodes
	 *            The nodes to schedule.
	 */
	protected abstract void scheduleNodesVisit(Collection<Node> nodes);
	
	/**
	 * Sets a listener that will be called whenever a Node is visited.
	 * @param listener
	 */
	public void setListener(NodeVisitedListener<Node> listener) {
		this.listener = listener;
	}

	/**
	 * Performs the search.
	 * 
	 * @param start
	 *            The starting node.
	 * @param stopFirstResult
	 *            <code>true</code> if the search should stop after the first
	 *            result is found.
	 * @return The set of nodes marked as result.
	 */
	public Set<Node> search(Node start, boolean stopFirstResult) {
		scheduleNodesVisit(Collections.singleton(start));

		Node current;
		while ((current = getNextScheduledNode()) != null) {
			// Skip already visited nodes
			if (!(mAlreadyVisited.add(current)))
				continue;

			if (listener != null) {
				listener.visited(current);
			}
			
			// Check if it is a result
			if (isResult(current)) {
				mResults.add(current);

				if (stopFirstResult) {
					break;
				}
			}

			// Get next nodes
			Collection<Node> nextNodes = nextNodes(current);
			
			// Remove already visited nodes
			nextNodes.removeAll(mAlreadyVisited);
			
			scheduleNodesVisit(nextNodes);
		}

		return mResults;
	}

}