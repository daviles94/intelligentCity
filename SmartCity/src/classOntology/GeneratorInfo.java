package classOntology;

import jade.content.Concept;

public class GeneratorInfo implements Concept {
	private String name;
	private float kwh;
	private float pollution;
	
	@Override
	public String toString() {
		return "GeneratorInfo [name=" + name + "]";
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getKwh() {
		return kwh;
	}
	public void setKwh(float kwh) {
		this.kwh = kwh;
	}
	public float getPollution() {
		return pollution;
	}
	public void setPollution(float pollution) {
		this.pollution = pollution;
	}

}
