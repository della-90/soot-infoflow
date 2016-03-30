package soot.jimple.infoflow.problems.conditions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import soot.BooleanType;
import soot.dava.internal.javaRep.DIntConstant;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.problems.conditions.Condition.Placeholder;

public class ConditionParser {

	public static final String DEFAULT_CONDITIONS_FILE_NAME = "Conditions.txt";
	private static final String REGEX = "^(?<index>\\d+)\\s*->\\s*<\\s*(?<class>.+?)\\s*:\\s*(?<return>.+)\\s+(?<name>.+?)\\s*\\((?<params>.*)\\)\\s*>$";

	private ConditionSet conditions;
	private List<String> data;

	public static ConditionParser fromFile(String fileName) throws IOException {
		ConditionParser parser = new ConditionParser();
		parser.readFile(fileName);
		return parser;
	}

	public static ConditionParser fromStringList(List<String> data)
			throws IOException {
		ConditionParser parser = new ConditionParser(data);
		return parser;
	}

	protected ConditionParser() {
	}

	private ConditionParser(List<String> data) {
		this.data = data;
	}

	private void readFile(String filename) throws IOException {
		String line;
		this.data = new LinkedList<>();

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		
		while ((line = reader.readLine()) != null) {
			this.data.add(line);
		}
		reader.close();
	}

	public ConditionSet getConditionSet() {
		if (this.conditions == null) {
			parse();
		}
		return this.conditions;
	}

	private void parse() {
		this.conditions = new ConditionSet();
		Map<Integer, List<Condition>> mappings = new HashMap<>();

		Pattern pattern = Pattern.compile(ConditionParser.REGEX);

		for (String line : data) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			Matcher matcher = pattern.matcher(line);

			if (matcher.matches()) {
				Condition condition = parseCondition(matcher);

				// It will not fail thanks to regex
				int index = Integer.parseInt(matcher.group("index"));
				List<Condition> ithConditions = mappings.get(index);
				
				// If no list is associated to the key, create a new one
				if (ithConditions == null) {
					ithConditions = new LinkedList<>();
					mappings.put(index, ithConditions);
				}
				ithConditions.add(condition);
			}
		}

		buildResultsFromMappings(mappings);
	}

	private void buildResultsFromMappings(
			Map<Integer, List<Condition>> mappings) {
		for (Integer key : mappings.keySet()) {
			List<Condition> conditions = mappings.get(key);
			if (conditions.size() == 0) {
				// This should be an unnecessary check
				continue;
			}

			if (conditions.size() == 1) {
				this.conditions.addCondition(conditions.get(0));
			} else {
				ConditionAlternatives alternatives = new ConditionAlternatives();
				for (Condition c : conditions) {
					alternatives.addAlternative(c);
				}
				this.conditions.addCondition(alternatives);
			}
		}
	}

	private Condition parseCondition(Matcher matcher) {
		String clazz = matcher.group("class");
		String returnType = matcher.group("return");
		String name = matcher.group("name");
		String paramsString = matcher	.group("params")
										.trim();

		Constant[] params;
		if (!paramsString.isEmpty()) {
			StringTokenizer tokenizer = new StringTokenizer(paramsString, ",");
			params = new Constant[tokenizer.countTokens()];
			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken()
										.trim();

				// Try to parse it
				params[i++] = parseToken(token);
			}
		} else {
			params = new Constant[0];
		}
		return new Condition(clazz, returnType, name, params);
	}

	/**
	 * Tries to parse a String, converting it to the corresponding constant value.
	 * Currently this method supports only: placeholder (i.e. the character '_'), int, long, String and boolean value.
	 * 
	 * @param token
	 * @return
	 */
	private Constant parseToken(String token) {
		// Check if it is the placeholder
		if (token.equals("_")) {
			return Placeholder.v();
		}
		
		// Try to parse as int or long
		try {
			Long l = Long.parseLong(token);
			if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
				return IntConstant.v(l.intValue());
			} else {
				return LongConstant.v(l.longValue());
			}
		} catch (NumberFormatException e) {
			// Could not convert to int/long
		}
		
		// Try to see if it is a String
		if (token.charAt(0) == '"' && token.charAt(token.length()-1) == '"') {
			return StringConstant.v(token);
		}
		
		// Try to see if it is a boolean
		if (token.equalsIgnoreCase("true")) {
			return IntConstant.v(1);
		} else if (token.equalsIgnoreCase("false")) {
			return IntConstant.v(0);
		}
		
		// Otherwise fall back to "null", which means that this parameter is ignored
		return null;
	}
}
