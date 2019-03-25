
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
import jade.wrapper.AgentController;
import ontology.SmartCityOntology;

public class Consumidor extends Agent {
	private int stat = 1; // 0:shutdown - 1:awake
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private float kwh = 14;
	private float kwhstored = 0;
	Random randomGenerator = new Random();

	protected void setup() {
		
		System.out.println("Agent: " + getLocalName() + " started.");
		if(getLocalName().equals("casa")) {
			kwh = 150;
		} else if(getLocalName().equals("restaurante")) {
			kwh = 1000;
		}
		configureOntology();

		addBehaviour(new TickerBehaviour(this, randomGenerator.nextInt(100000)) {
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
		    	  if(kwhstored > kwh) {
			        kwhstored -= kwh;
			        System.out.println("Estoy gastando energia me queda " + kwhstored);
		    	  } 
		      } 
		    });
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
			msg.setContent(Float.toString(kwh));
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
				System.out.println("Soy " + this.getLocalName() +" recibido " + energiarecibida.getAmount() + " de " + msg.getSender().getLocalName());
				kwhstored += energiarecibida.getAmount();
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		}

	}

}
