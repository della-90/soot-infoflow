/**
 * 
 */
package soot.jimple.infoflow.problems.conditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

/**
 * A class that performs a backwards analysis looking for the declaration of a
 * variable with a constant value.
 * 
 * @author Nicola Dellarocca
 *
 */
public class ConstantDeclarationFinder extends DeclarationFinder {

	/**
	 * {@inheritDoc}
	 */
	public ConstantDeclarationFinder(IInfoflowCFG cfg, Value value) {
		super(cfg, value);
		
		System.out.println("*** Created finder with value: "+value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isResult(Unit node) {
		if (node instanceof AssignStmt) {
			final AssignStmt assign = (AssignStmt) node;

			// If the left operand is the one we're looking for...
			if (assign	.getLeftOp()
						.equals(this.value)) {
				// ... check if the right operand is a constant value
				Value rightOp = assign.getRightOp();
				if (rightOp instanceof Constant) {
					return true;
				} else if (rightOp instanceof Local) {
					/*
					 * If it is a local (i.e. a local variable), search for the
					 * declaration of that variable
					 */
					ConstantDeclarationFinder innerFinder = new ConstantDeclarationFinder(
							cfg, assign.getLeftOp());
					Set<Unit> innerResults = innerFinder.search(node, true);
					if (!innerResults.isEmpty()) {
						this.result.addAll(innerResults);
						return true;
					}
					return false;
				} else if (rightOp instanceof NewExpr) {
					/*
					 *  The right operand is an object creation expression.
					 *  We need to look at the constructor
					 */
					DepthFirstSearcher<Unit> searcher = new DepthFirstSearcher<Unit>(cfg) {
						
						@Override
						protected Collection<Unit> nextNodes(Unit current) {
							return new ArrayList<>(cfg.getSuccsOf(current));
						}
						
						@Override
						protected boolean isResult(Unit node) {
							if (node instanceof InvokeStmt && ((InvokeStmt)node).getInvokeExpr() instanceof InstanceInvokeExpr) {
								InstanceInvokeExpr iie = (InstanceInvokeExpr) ((InvokeStmt) node).getInvokeExpr();
								
								return iie.getBase().equals(assign.getLeftOp());
							}
							return false;
						}
					};
					
					Set<Unit> innerResults = searcher.search(assign, true);
					this.result.addAll(innerResults);
//					throw new Error("Right operand is a NewExpr: not implemented yet");
				}
			}
		}

		return false;
	}

}
