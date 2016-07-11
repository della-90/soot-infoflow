package soot.jimple.infoflow.cfg;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import soot.jimple.infoflow.InfoflowConfiguration.CallgraphAlgorithm;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.options.Options;

/**
 * A thread-safe container for an {@link IInfoflowCFG} instance.
 * 
 * @author Nicola Dellarocca
 *
 */
public class SharedCfg {

	/**
	 * This class holds the parameters of a CFG build request.
	 * 
	 * @author Nicola Dellarocca
	 *
	 */
	private static class BuildOptions {

		private CallgraphAlgorithm algorithm;
		private boolean enableExceptions;
		private String appPath;
		private String libPath;

		private BuildOptions(CallgraphAlgorithm algorithm,
				boolean enableExceptions, String appPath, String libPath) {
			super();
			this.algorithm = algorithm;
			this.enableExceptions = enableExceptions;
			this.appPath = appPath;
			this.libPath = libPath;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((algorithm == null) ? 0 : algorithm.hashCode());
			result = prime * result
					+ ((appPath == null) ? 0 : appPath.hashCode());
			result = prime * result + (enableExceptions ? 1231 : 1237);
			result = prime * result
					+ ((libPath == null) ? 0 : libPath.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BuildOptions other = (BuildOptions) obj;
			if (algorithm != other.algorithm)
				return false;
			if (appPath == null) {
				if (other.appPath != null)
					return false;
			} else if (!appPath.equals(other.appPath))
				return false;
			if (enableExceptions != other.enableExceptions)
				return false;
			if (libPath == null) {
				if (other.libPath != null)
					return false;
			} else if (!libPath.equals(other.libPath))
				return false;
			return true;
		}

	}

	// Contains the CFG
	private static IInfoflowCFG cfg;

	// Contains last build options
	private static BuildOptions lastBuildOptions;

	/**
	 * Returns the CFG that has been created with the specified options.
	 * 
	 * @param algorithm
	 *            The algorithm to use to build the CFG.
	 * @param enableExceptions
	 *            If exceptions should be tracked.
	 * @param appPath
	 *            The path to the APK to analyze.
	 * @param libPath
	 *            The path to the "android.jar" to use to build the CFG.
	 * @param forceGeneration
	 *            If it is set to <code>true</code>, it will always generate a
	 *            new CFG instance, otherwise it will reuse a previously created
	 *            instance (if available).
	 * @return The generated CFG.
	 */
	public synchronized static IInfoflowCFG getCfg(CallgraphAlgorithm algorithm,
			boolean enableExceptions, String appPath, String libPath,
			boolean forceGeneration) {

		BuildOptions options = new BuildOptions(algorithm, enableExceptions,
				appPath, libPath);

		/*
		 * If the CFG has not been build yet or if last build options are
		 * different from this build's one, compute the CFG
		 */
		if (forceGeneration || SharedCfg.cfg == null
				|| !options.equals(lastBuildOptions)) {
			IInfoflowCFG tmp = buildCfg(algorithm,
					enableExceptions,
					appPath,
					libPath);

			SharedCfg.setCfg(tmp);
		}

		return SharedCfg.cfg;
	}

	/**
	 * This method is responsible of the creation of the CFG. For the parameters
	 * list and return type documentation please see
	 * {@link #getCfg(CallgraphAlgorithm, boolean, String, String)}.
	 * 
	 * @param algorithm
	 * @param enableExceptions
	 * @param appPath
	 * @param libPath
	 * @return
	 */
	private synchronized static IInfoflowCFG buildCfg(
			CallgraphAlgorithm algorithm, boolean enableExceptions,
			String appPath, String libPath) {
		// This build is the last build
		lastBuildOptions = new BuildOptions(algorithm, enableExceptions,
				appPath, libPath);

		// Initialize soot in order to create the CFG
		SharedCfg.initializeSoot(appPath, libPath);

		// Create and return the CFG
		DefaultBiDiICFGFactory factory = new DefaultBiDiICFGFactory();
		factory.setIsAndroid(true);
		return factory.buildBiDirICFG(algorithm, enableExceptions);
	}

	/**
	 * Resets and initializes soot.
	 * 
	 * @param appPath
	 *            The path to the APK to analyze
	 * @param libPath
	 *            The path to the android.jar used to analyze the APK.
	 */
	private synchronized static void initializeSoot(String appPath,
			String libPath) {
		soot.G.reset();

		Options	.v()
				.set_no_bodies_for_excluded(true);
		Options	.v()
				.set_allow_phantom_refs(true);
		Options	.v()
				.set_output_format(Options.output_format_none);
		Options	.v()
				.set_soot_classpath(libPath);

		List<String> processDirs = new LinkedList<String>();
		for (String ap : appPath.split(File.pathSeparator))
			processDirs.add(ap);
		Options	.v()
				.set_process_dir(processDirs);

	}

	/**
	 * Sets the CFG and notifies any possible thread waiting for it to be non-
	 * <code>null</code>.
	 * 
	 * @param cfg
	 *            The CFG to set.
	 */
	public synchronized static void setCfg(IInfoflowCFG cfg) {
		System.out.println("**************** Setting the CFG");
		// Sets the CFG
		SharedCfg.cfg = cfg;

		// Notify all threads for the CFG generation
		SharedCfg.class.notifyAll();
	}

	/**
	 * Waits until the CFG is generated (i.e. it is not <code>null</code>).
	 * 
	 * @return The CFG or <code>null</code> in case of
	 *         {@link InterruptedException}.
	 */
	public synchronized static IInfoflowCFG waitForCfg() {
		try {
			// Wait until the CFG is generated
			while (SharedCfg.cfg == null) {
				SharedCfg.class.wait();
			}
			return SharedCfg.cfg;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
