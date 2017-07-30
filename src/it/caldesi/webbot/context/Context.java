package it.caldesi.webbot.context;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.caldesi.webbot.model.annotations.InvisibleInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Context {

	private static List<String> instructionTypes;
	private static ObservableList<String> observableInstructionTypes;

	private static List<Class<?>> instructionClassList;
	private static Map<String, Class<?>> instructionByType;

	// fields
	private static Set<Class<?>> hasNoArgument;
	private static Set<Class<?>> hasNoTarget;

	private static String PACKAGE_INSTRUCTION = "it.caldesi.webbot.model.instruction";
	private static String FIELD_INSTRUCTION_NAME = "NAME";

	public static void loadContext() throws Exception {
		instructionClassList = Utils.getClassesForPackage(PACKAGE_INSTRUCTION, false);
		instructionTypes = new LinkedList<>();
		instructionByType = new HashMap<>();
		hasNoArgument = new HashSet<>();
		hasNoTarget = new HashSet<>();

		for (Class<?> c : instructionClassList) {
			if (!Modifier.isAbstract(c.getModifiers()) && !c.isAnnotationPresent(InvisibleInstruction.class)) {
				try {
					String instructionName = (String) Utils.getFieldValue(c, FIELD_INSTRUCTION_NAME);
					instructionTypes.add(instructionName);
					instructionByType.put(instructionName, c);
					if (c.isAnnotationPresent(NoArgumentInstruction.class))
						hasNoArgument.add(c);
					if (c.isAnnotationPresent(NoTargetInstruction.class))
						hasNoTarget.add(c);
				} catch (Exception e) {
					System.out.println("Cannot find field " + FIELD_INSTRUCTION_NAME + " on type: " + c.getName());
				}
			}
		}
		instructionTypes.sort(null);
		observableInstructionTypes = FXCollections.observableArrayList(instructionTypes);
	}

	public static boolean hasNoArgument(Class<?> instrClass) {
		return hasNoArgument.contains(instrClass);
	}

	public static boolean hasNoTarget(Class<?> instrClass) {
		return hasNoTarget.contains(instrClass);
	}

	public static boolean hasNoArgument(String actionName) {
		Class<?> instrClass = getInstructionByType(actionName);
		return hasNoArgument.contains(instrClass);
	}

	public static boolean hasNoTarget(String actionName) {
		Class<?> instrClass = getInstructionByType(actionName);
		return hasNoTarget.contains(instrClass);
	}

	public static ObservableList<String> getInstructionsObservableList() {
		return observableInstructionTypes;
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
			System.out.println("Cannot get instance for instruction type: " + type);
			e.printStackTrace();
		}
		return null;
	}

}
