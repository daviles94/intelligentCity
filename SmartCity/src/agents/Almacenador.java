
package agents;

import java.util.Random;
//import jade.domain.DFService;
//import jade.domain.FIPAException;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;

import jade.domain.FIPAAgentManagement.Property;
import classOntology.Energy;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import classOntology.GeneratorInfo;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import ontology.SmartCityOntology;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Almacenador extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private float kwhstored = 2100;
	private float realstoredkwh = 2100;

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");
		configureOntology();
		addBehaviour(new StoreBehaviour());
	}

	private boolean checkUmbral() {
		float ten_percent = (float) 0.1 * kwhstored;
		return realstoredkwh <= ten_percent;
	}

	public void configureOntology() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
	}

	private boolean canGiveEnergy(float requestkwh) {
		return realstoredkwh > requestkwh;
	}

	private class StoreBehaviour extends CyclicBehaviour {
		private boolean requestingEnergy = true;

		@Override
		public void action() {
			if (!checkUmbral()) {
				receiveMessageToSendEnergy();
			} else {
				if (requestingEnergy) {
					System.out.println(getLocalName() + " Pidiendo energia...");
					requestEnergy();
				} else {
					receiveMessageToGetEnery();
				}
			}
		}

		private void receiveMessageToSendEnergy() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				Energy energy;
				try {
					energy = (Energy) msg.getContentObject();
					float requestedkwh = energy.getAmount();

					if (canGiveEnergy(requestedkwh)) {
						ACLMessage reply = msg.createReply();

						reply.setContentObject(energy);
						send(reply);
						realstoredkwh -= requestedkwh;
						System.out.println(
								"#########################################################################################################################################################\n"
										+ getLocalName() + ": " + msg.getContentObject() + " kwh" + " a "
										+ msg.getSender().getLocalName() + " me quedan " + Float.toString(realstoredkwh)
										+ "\n#########################################################################################################################################################\n\n\n");

					} else {
						ACLMessage reply = msg.createReply();
						msg.clearAllReceiver();
						msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
						msg.addReceiver(new AID("centralgenerador", AID.ISLOCALNAME));
						send(reply);
						System.out.println(getLocalName() + " no puedo enviar " + Float.toString(requestedkwh));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

		private void receiveMessageToGetEnery() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				Energy energiarecibida;
				try {
					energiarecibida = (Energy) msg.getContentObject();
					System.out.println(
							"#########################################################################################################################################################\n"
									+ getLocalName() + ": " + "He recibido " + energiarecibida.getAmount() + " kwh de "
									+ msg.getSender().getLocalName()
									+ "\n#########################################################################################################################################################\n\n\n");

					realstoredkwh += energiarecibida.getAmount();
					ACLMessage msginform = new ACLMessage(ACLMessage.INFORM);
					msginform.addReceiver(new AID("centralgenerador", AID.ISLOCALNAME));
					msginform.setContent("Tengo energia");
					send(msginform);
					requestingEnergy = false;
				} catch (Exception e) {
					System.out.println(msg);
				}
			}

		}

		private void requestEnergy() {
			ACLMessage msg = new ACLMessage(ACLMessage.CFP);
			msg.addReceiver(new AID("centralgenerador", AID.ISLOCALNAME));
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());
			msg.setReplyWith(getLocalName());
			try {
				Energy energy = new Energy(kwhstored - realstoredkwh);
				msg.setContentObject(energy);
				System.out.println(
						"#########################################################################################################################################################\n"
								+ getLocalName() + ": " + msg.getContentObject() + " kwh" + " a centralgenerador"
								+ "\n#########################################################################################################################################################\n\n\n");

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			send(msg);
			requestingEnergy = false;
		}
	}

}
