/**
 * 
 */
package soot.jimple.infoflow.problems.conditions;

import soot.Value;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;

/**
 * 11 mar 2016
 * 
 * @author Nicola Dellarocca
 *
 */
public class ValueUtil {

	public static Object extractValue(Value val) {
		if (val instanceof StringConstant) {
			return ((StringConstant) val).value;
		} else if (val instanceof IntConstant) {
			return ((IntConstant) val).value;
		} else if (val instanceof LongConstant) {
			return ((LongConstant) val).value;
		} else if (val instanceof DoubleConstant) {
			return ((DoubleConstant) val).value;
		} else if (val instanceof FloatConstant) {
			return ((FloatConstant) val).value;
		} else {
			throw new IllegalArgumentException(
					"Only constant values are currently supported");
		}
	}
}
