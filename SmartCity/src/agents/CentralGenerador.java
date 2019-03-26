
package agents;

import java.util.ArrayList;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import ontology.SmartCityOntology;

public class CentralGenerador extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private ArrayList<ACLMessage> queu = new ArrayList<ACLMessage>();

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");

		addBehaviour(new ListenEnergyOrder());
		addBehaviour(new SendEnergyOrder());
	}

	private class ListenEnergyOrder extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.CFP) {
				System.out.println(
						"#########################################################################################################################################################\n"
								+ getLocalName() + ": " + "El consumidor " + msg.getSender().getLocalName()
								+ " necesita " + msg.getContent() + " kwh"
								+ "\n#########################################################################################################################################################\n\n\n");

				queu.add(msg);
			}
		}
	}

	private class SendEnergyOrder extends CyclicBehaviour {

		@Override
		public void action() {
			if (queu.size() > 0) {
				ACLMessage msg = queu.get(0);
				queu.remove(0);
				AID aid = studyConditions(Float.parseFloat(msg.getContent()));
				msg.clearAllReceiver();
				msg.addReceiver(aid);
				send(msg);
			}
		}
	}

	private AID studyConditions(float kwh) {
		if (kwh > 200) {
			return new AID("plantaNuclear", AID.ISLOCALNAME);
		} else {
			return new AID("plantaEolica", AID.ISLOCALNAME);
		}
	}
}
