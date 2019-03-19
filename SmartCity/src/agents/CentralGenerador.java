
package agents;

import classOntology.TransferEnergy;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import ontology.SmartCityOntology;

public class CentralGenerador extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");

		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				// Receive the other agent message
				ACLMessage msg = receive();
				if (msg != null) {
					try {

						TransferEnergy energiarecibida = (TransferEnergy) msg.getContentObject();
						System.out.println(energiarecibida.getSender() + " Me han enviado "
								+ energiarecibida.getAmount() + energiarecibida.getUnit());
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					// AQUI SE DEBERÁ TRATAR SI SE APAGA O NO
					ACLMessage msgApagar = new ACLMessage(ACLMessage.INFORM);
					msgApagar.addReceiver(new AID("plantaTermicaSolar", AID.ISLOCALNAME));
					msgApagar.setLanguage(codec.getName());
					msgApagar.setOntology(ontology.getName());

					try {
						msgApagar.setContentObject("Apagate cabron");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					send(msgApagar);

				}
			}
		});
	}

}
