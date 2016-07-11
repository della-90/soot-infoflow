package soot.jimple.infoflow.problems.conditions;

import soot.SootClass;

/**
 * Contains some utility methods to deal with {@link SootClass} instances.
 * 
 * @author Nicola Dellarocca
 *
 */
public class SootClassUtil {

	public static boolean isOrExtendsClass(SootClass sc, Class<?> clazz) {
		return SootClassUtil.isOrExtendsClass(sc, clazz.getName());
	}

	public static boolean isOrExtendsClass(SootClass sc, String clazz) {
		SootClass current = sc;

		while (!current	.getName()
						.equals(clazz)
				&& current.hasSuperclass()) {
			current = current.getSuperclass();
		}

		return current	.getName()
						.equals(clazz);
	}

	public static boolean implementsInterface(SootClass sc, String clazz) {
		SootClass current = sc;

		while (!current.implementsInterface(clazz)
				&& current.hasSuperclass()) {
			current = current.getSuperclass();
		}

		return current.implementsInterface(clazz);
	}

	public static boolean implementsInterface(SootClass sc, Class<?> clazz) {
		return SootClassUtil.implementsInterface(sc, clazz.getName());
	}
}
