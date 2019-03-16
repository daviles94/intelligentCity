package ontology;

import classOntology.Energy;
import jade.content.onto.*;
import jade.content.schema.*;

public class SmartCityOntology extends BeanOntology {
	private static String ONTOLOGY_NAME;
	private static Ontology theInstance = new SmartCityOntology(ONTOLOGY_NAME);

	public static Ontology getInstance() {
		return theInstance;
	}

	private SmartCityOntology(String name) {
		super(name);
		try {
			add("classOntology");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
