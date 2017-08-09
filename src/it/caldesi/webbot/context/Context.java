package it.caldesi.webbot.context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import it.caldesi.webbot.model.annotations.AssignableInstruction;
import it.caldesi.webbot.model.annotations.EventInstruction;
import it.caldesi.webbot.model.annotations.InvisibleInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.annotations.NoDelayInstruction;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import it.caldesi.webbot.model.annotations.UIInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class Context {

	private static List<String> instructionTypes = new LinkedList<>();
	private static ObservableList<String> observableInstructionTypes;

	private static List<Class<?>> instructionClassList = new LinkedList<>();
	private static Map<String, Class<?>> instructionByType = new HashMap<>();
	private static Map<String, List<Class<?>>> instructionByEventType = new HashMap<>();

	// fields
	private static Set<Class<?>> hasNoArgument = new HashSet<>();
	private static Set<Class<?>> hasNoTarget = new HashSet<>();
	private static Set<Class<?>> hasNoDelay = new HashSet<>();
	private static Set<Class<?>> assignable = new HashSet<>();
	private static Set<Class<?>> uiInstructions = new HashSet<>();
	private static Map<String, Type> argumentTypes = new HashMap<>();
	private static Set<String> onlyPositiveIntegerArgument = new HashSet<>();

	private static String[] PACKAGES_INSTRUCTION = { //
			"it.caldesi.webbot.model.instruction", //
			"it.caldesi.webbot.model.instruction.js", //
			"it.caldesi.webbot.model.instruction.js.event", //
			"it.caldesi.webbot.model.instruction.block" //
	};
	private static String FIELD_INSTRUCTION_NAME = "NAME";

	private static Stage primaryStage;

	public static void loadContext(Stage primaryStage) throws Exception {
		Context.primaryStage = primaryStage;
		for (String pack : PACKAGES_INSTRUCTION) {
			instructionClassList.addAll(Utils.getClassesForPackage(pack, false));
		}

		for (Class<?> c : instructionClassList) {
			if (!Modifier.isAbstract(c.getModifiers()) && !c.isAnnotationPresent(InvisibleInstruction.class)) {
				try {
					String instructionName = (String) Utils.getFieldValue(c, FIELD_INSTRUCTION_NAME);
					instructionTypes.add(instructionName);
					instructionByType.put(instructionName, c);
					if (c.isAnnotationPresent(NoArgumentInstruction.class))
						hasNoArgument.add(c);
					else if (c.isAnnotationPresent(ArgumentType.class)) {
						Annotation argumentTypeAnnotation = c.getAnnotation(ArgumentType.class);
						ArgumentType argumentType = (ArgumentType) argumentTypeAnnotation;
						argumentTypes.put(instructionName, argumentType.type());
						if (argumentType.type() == Type.INTEGER) {
							if (argumentType.onlyPositive()) {
								onlyPositiveIntegerArgument.add(instructionName);
							}
						}
					}
					if (c.isAnnotationPresent(EventInstruction.class)) {
						Annotation eventInstructionAnnotation = c.getAnnotation(EventInstruction.class);
						EventInstruction eventInstruction = (EventInstruction) eventInstructionAnnotation;
						String eventName = eventInstruction.name();
						List<Class<?>> list = instructionByEventType.get(eventName);
						if (list == null) {
							list = new LinkedList<>();
							instructionByEventType.put(eventName, list);
						}
						list.add(c);
					}
					if (c.isAnnotationPresent(NoTargetInstruction.class))
						hasNoTarget.add(c);
					if (c.isAnnotationPresent(NoDelayInstruction.class))
						hasNoDelay.add(c);
					if (c.isAnnotationPresent(AssignableInstruction.class))
						assignable.add(c);
					if (c.isAnnotationPresent(UIInstruction.class))
						uiInstructions.add(c);
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

	public static boolean hasNoDelay(Class<?> instrClass) {
		return hasNoDelay.contains(instrClass);
	}

	public static boolean isAssignable(Class<?> instrClass) {
		return assignable.contains(instrClass);
	}

	public static boolean hasNoArgument(String actionName) {
		Class<?> instrClass = getInstructionByType(actionName);
		return hasNoArgument.contains(instrClass);
	}

	public static boolean hasNoTarget(String actionName) {
		Class<?> instrClass = getInstructionByType(actionName);
		return hasNoTarget.contains(instrClass);
	}

	public static boolean hasNoDelay(String actionName) {
		Class<?> instrClass = getInstructionByType(actionName);
		return hasNoDelay.contains(instrClass);
	}

	public static boolean isAssignable(String actionName) {
		Class<?> instrClass = getInstructionByType(actionName);
		return assignable.contains(instrClass);
	}

	public static boolean isUIInstruction(Class<?> instrClass) {
		return uiInstructions.contains(instrClass);
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

	public static List<Class<?>> getInstructionByEventType(String eventName) {
		return instructionByEventType.get(eventName);
	}

	public static Type getArgumentType(String instructionType) {
		return argumentTypes.get(instructionType);
	}

	public static boolean onlyPositiveInteger(String instructionType) {
		return onlyPositiveIntegerArgument.contains(instructionType);
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

	public static List<Instruction<?>> getInstructionInstanceByEventType(String eventName) {
		try {
			List<Instruction<?>> list = new LinkedList<>();

			List<Class<?>> instructionByEventTypeList = getInstructionByEventType(eventName);
			for (Class<?> c : instructionByEventTypeList) {
				list.add((Instruction<?>) c.newInstance());
			}

			return list;
		} catch (Exception e) {
			System.out.println("Cannot get instruction instance from event type: " + eventName);
			e.printStackTrace();
		}
		return null;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static Set<String> getEventTypes() {
		return instructionByEventType.keySet();
	}

}
