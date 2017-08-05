package it.caldesi.webbot.model.instruction.block;

public abstract class ForBlock extends EvaluableBlock {

	static int instanceIdCounter = 0;
	protected int instanceId;

	public ForBlock() {
		super();
		this.instanceId = instanceIdCounter++;
	}

	public ForBlock(String name) {
		super(name);
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
		ForBlock other = (ForBlock) obj;
		if (instanceId != other.instanceId)
			return false;
		return true;
	}

}
