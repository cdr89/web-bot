package it.caldesi.webbot.model.instruction.block;

import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;

@ArgumentType(type = Type.INTEGER)
public class ForTimesBlock extends EvaluableBlock {

	public static final String NAME = "forTimes";

	static int instanceIdCounter = 0;
	protected int instanceId;

	public ForTimesBlock() {
		super(NAME);
		this.instanceId = instanceIdCounter++;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + instanceId;
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
		ForTimesBlock other = (ForTimesBlock) obj;
		if (instanceId != other.instanceId)
			return false;
		return true;
	}

}
