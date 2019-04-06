
package agents;

import java.util.Random;

import classOntology.Energy;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import ontology.SmartCityOntology;

public class Ecoconsumidor extends Consumidor {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private float kwh = 200;
	private float generatekwh = 10;
	private float kwhstored = 80;
	
	Random randomGenerator = new Random();

	protected void setup() {

		System.out.println("Agent: " + getLocalName() + " started.");
		configureOntology();

		addBehaviour(new TickerBehaviour(this, 60 * 1000) {
			@Override
			protected void onTick() {
				control();
			}
		});

		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				receiveMessage();
			}
		});

		addBehaviour(new WakerBehaviour(this, 30 * 1000) {
			protected void handleElapsedTimeout() {
				if (kwhstored > kwh) {
					kwhstored -= kwh;
					System.out.println(
							"#########################################################################################################################################################\n"
									+ "[ECO] " + getLocalName() + ": " + "Gastando energía. Energía restante:" + kwhstored
									+ "\n#########################################################################################################################################################\n\n\n");
				}
				generateEnergy();
			}
		});
	}

	private boolean checkUmbral() {
		return kwhstored < kwh * 2;
	}
	
	private void generateEnergy() {
		//TODO: check weather
		kwhstored += generatekwh;
	}

	public void configureOntology() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
	}

	public void control() {
		if (this.checkUmbral()) {
			this.requestEnergy();
		}
	}

	public void requestEnergy() {
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		msg.addReceiver(new AID("centralgenerador", AID.ISLOCALNAME));
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		try {
			msg.setContent(Float.toString(kwh));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		send(msg);

		System.out.println(
				"#########################################################################################################################################################\n"
						+ getLocalName() + ": " + "Solicito :" + kwh + " kwh"
						+ "\n#########################################################################################################################################################\n\n\n");
	}

	public void receiveMessage() {
		ACLMessage msg = receive();
		if (msg != null) {
			Energy energiarecibida;
			try {
				energiarecibida = (Energy) msg.getContentObject();
				System.out.println(
						"#########################################################################################################################################################\n"
								+ getLocalName() + ": " + "He recibido " + energiarecibida.getAmount() + " kwh de "
								+ msg.getSender().getLocalName()
								+ "\n#########################################################################################################################################################\n\n\n");

				kwhstored += energiarecibida.getAmount();
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		}

	}

}
