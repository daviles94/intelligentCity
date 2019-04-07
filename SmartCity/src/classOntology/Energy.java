package classOntology;

import jade.content.Concept;

public class Energy implements Concept {
	private String unit;
	private float amount;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Energy(float amount) {
		super();
		this.unit = "kwh";
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Energy [" + amount + ", " + unit + "]";
	}
		

}
