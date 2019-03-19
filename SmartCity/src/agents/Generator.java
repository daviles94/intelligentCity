
package agents;

import java.util.Random;

import classOntology.TransferEnergy;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import ontology.SmartCityOntology;

public class Generator extends Agent {
	private int stat = 1; // 0:shutdown - 1:awake
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();

	float[] generated = new float[2];

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");

		configureOntology();

		addBehaviour(new TickerBehaviour(this, 10 * 1000) {
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
	}

	public void configureOntology() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
	}

	public void control() {
		if (stat == 1) {
			generated = generateEnergy(getLocalName());
			sendEnergy(generated[0], "khZ");
		}
	}

	public float[] generateEnergy(String TipoPlanta) {
		float[] generated = new float[2];
		Random randomGenerator = new Random();
		float energiaGenerada = 0;
		float contaminacion = 0;

		if (TipoPlanta.equals("plantaEolica")) {
			energiaGenerada = randomGenerator.nextInt(100);
			contaminacion = energiaGenerada / 5;
		}

		if (TipoPlanta.equals("plantaNuclear")) {
			energiaGenerada = randomGenerator.nextInt(800);
			contaminacion = energiaGenerada / 2;
		}

		if (TipoPlanta.equals("plantaTermicaSolar")) {
			energiaGenerada = randomGenerator.nextInt(150);
			contaminacion = energiaGenerada / 4;
		}

		if (TipoPlanta.equals("plantaGeotermica")) {
			energiaGenerada = randomGenerator.nextInt(300);
			contaminacion = energiaGenerada / 3;
		}

		if (TipoPlanta.equals("plantaHidroelectrica")) {
			energiaGenerada = randomGenerator.nextInt(200);
			contaminacion = energiaGenerada / 3;
		}

		generated[0] = energiaGenerada;
		generated[1] = contaminacion;

		return generated;
	}

	public void sendEnergy(float energia, String unit) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("centralgenerador", AID.ISLOCALNAME));
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());

		TransferEnergy energy = new TransferEnergy();
		energy.setAmount(energia);
		energy.setUnit(unit);
		energy.setSender(getLocalName());

		try {
			msg.setContentObject(energy);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		send(msg);
	}

	public void receiveMessage() {
		ACLMessage msg = receive();
		if (msg != null) {

		} else {
		}

	}

}
