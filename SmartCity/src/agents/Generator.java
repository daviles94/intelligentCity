
package agents;

import java.util.Random;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import ontology.SmartCityOntology;

public class Generator extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();

	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		System.out.println("Agent " + getLocalName() + " started.");

//		// Add the CyclicBehaviour
//		addBehaviour(new CyclicBehaviour(this) {
//			public void action() {
//		        Random randomGenerator = new Random();
//		    	int energy = randomGenerator.nextInt(100);
//				System.out.println("He generado " + energy + "kW");
//			}
//		});

	}
}
