package it.caldesi.webbot.context;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;

public class Context {

	private static List<String> instructionTypes;
	private static ObservableList<String> observableInstructionTypes;

	private static List<Class<?>> instructionClassList;
	private static Map<String, Class<?>> instructionByType;

	private static String PACKAGE_INSTRUCTION = "it.caldesi.webbot.model.instruction";
	private static String FIELD_INSTRUCTION_NAME = "NAME";

	public static ChangeListener<State> recordListener;

	public static void loadContext() throws Exception {
		instructionClassList = Utils.getClassesForPackage(PACKAGE_INSTRUCTION);
		instructionTypes = new LinkedList<>();
		instructionByType = new HashMap<>();

		for (Class<?> c : instructionClassList) {
			if (!Modifier.isAbstract(c.getModifiers())) {
				try {
					String instructionName = (String) Utils.getFieldValue(c, FIELD_INSTRUCTION_NAME);
					instructionTypes.add(instructionName);
					instructionByType.put(instructionName, c);
				} catch (Exception e) {
					System.out.println("Cannot find field " + FIELD_INSTRUCTION_NAME + " on type: " + c.getName());
				}
			}
		}
		instructionTypes.sort(null);
		observableInstructionTypes = FXCollections.observableArrayList(instructionTypes);
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
