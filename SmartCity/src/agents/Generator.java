
package agents;

import java.util.Random;

import classOntology.Energy;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import ontology.SmartCityOntology;

public class Generator extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");

		configureOntology();

		addBehaviour(new TickerBehaviour(this, 10 * 1000) {
			@Override
			protected void onTick() {
				// Generate random Price
				Random randomGenerator = new Random();
				int khz = randomGenerator.nextInt(100);
				// TODO Auto-generated method stub
				sendEnergy(khz);
			}
		});
	}

	public void configureOntology() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
	}

	public void sendEnergy(float khz) {

		// Preparación del mensaje
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("centralgenerador", AID.ISLOCALNAME));
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());

		// Preparación del contenido
		Energy energy = new Energy();
		energy.setAmount(khz);

		try {
			msg.setContentObject(energy);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		send(msg);

	}

}
