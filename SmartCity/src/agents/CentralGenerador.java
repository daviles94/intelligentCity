
package agents;

import classOntology.Energy;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class CentralGenerador extends Agent {

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");

		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				// Receive the other agent message
				ACLMessage msg = receive();
				if (msg != null) {
					try {
						Energy energiarecibida = (Energy) msg.getContentObject();
						System.out.println("Me han enviado " + energiarecibida.getAmount() + "KhZ");
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else
					block();
			}
		});
	}

}
