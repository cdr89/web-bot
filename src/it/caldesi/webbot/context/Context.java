package it.caldesi.webbot.context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.utils.Utils;

public class Context {

	private static List<Class<?>> instructionClassList;
	private static List<String> instructionTypes;
	private static Map<String, Class<?>> instructionByType;

	private static String PACKAGE_INSTRUCTION = "it.caldesi.webbot.model.instruction";
	private static String FIELD_INSTRUCTION_NAME = "actionName";

	static {
		try {
			loadContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadContext() throws Exception {
		instructionClassList = Utils.getClassesForPackage(PACKAGE_INSTRUCTION);
		instructionTypes = new LinkedList<>();
		instructionByType = new HashMap<>();

		for (Class<?> c : instructionClassList) {
			String instructionName = (String) Utils.getFieldValue(c, FIELD_INSTRUCTION_NAME);
			instructionTypes.add(instructionName);
			instructionByType.put(instructionName, c);
		}
		instructionTypes.sort(null);
	}

	public static List<String> getInstructionTypes() {
		return instructionTypes;
	}

	public static Class<?> getInstructionByType(String type) {
		return instructionByType.get(type);
	}

	public static Instruction<?> getInstructionInstanceByType(String type) {
		try {
			return (Instruction<?>) getInstructionByType(type).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
