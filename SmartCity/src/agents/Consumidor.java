
package agents;

import java.util.Random;

import classOntology.Energy;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import ontology.SmartCityOntology;

public class Consumidor extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private float kwh = 14;
	private float kwhstored = 0;
	Random randomGenerator = new Random();

	protected void setup() {

		System.out.println("Agent: " + getLocalName() + " started.");
		settingConditions(getLocalName());
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

		addBehaviour(new WakerBehaviour(this, 60 * 1000) {
			protected void handleElapsedTimeout() {
				if (kwhstored > kwh) {
					kwhstored -= kwh;
					System.out.println(
							"#########################################################################################################################################################\n"
									+ getLocalName() + ": " + "Gastando energ�a. Energ�a restante:" + kwhstored
									+ "\n#########################################################################################################################################################\n\n\n");
				}
			}
		});
	}
	
	private void settingConditions(String TipoPlanta) {
		
		if (TipoPlanta.equals("casa")) {
			kwh = 100;
			kwhstored = 80;
		}

		if (TipoPlanta.equals("restaurante")) {
			kwh = 200;
			kwhstored = 100;
		}

		if (TipoPlanta.equals("edificio")) {
			kwh = 500;
			kwhstored = 400;
		}

		if (TipoPlanta.equals("supermercado")) {
			kwh = 1000;
			kwhstored = 700;
		}

	}

	private boolean checkUmbral() {
		return kwhstored < kwh * 2;
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
			Energy energiapedida = new Energy(kwh);
			msg.setContentObject(energiapedida);
			System.out.println(
					"#########################################################################################################################################################\n"
							+ getLocalName() + ": " + "Solicito :" + kwh + " kwh"
							+ "\n#########################################################################################################################################################\n\n\n");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		send(msg);

	}

	public void receiveMessage() {
		ACLMessage msg = receive();
		if (msg != null) {
			Energy energiarecibida;
			try {
				energiarecibida = (Energy) msg.getContentObject();
				System.out.println(
						"#########################################################################################################################################################\n"
								+ getLocalName() + ": " + "He recibido " + energiarecibida + " kwh de "
								+ msg.getSender().getLocalName()
								+ "\n#########################################################################################################################################################\n\n\n");

				kwhstored += energiarecibida.getAmount();
			} catch (Exception e) {
				System.out.println(msg.getContent());
				e.printStackTrace();
			}
		}
	}

}
