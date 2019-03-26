
package agents;

import java.util.Random;
//import jade.domain.DFService;
//import jade.domain.FIPAException;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;

import classOntology.Energy;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import ontology.SmartCityOntology;

public class Generator extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();

	float[] generated = new float[2];

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");

		configureOntology();

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

	public void receiveMessage() {
		ACLMessage msg = receive();
		if (msg != null) {

			System.out.println(
					"#########################################################################################################################################################\n"
							+ getLocalName() + ": " + msg.getContent() + " kwh" + " a " + msg.getSender().getLocalName()
							+ "\n#########################################################################################################################################################\n\n\n");

			ACLMessage reply = msg.createReply();
			Energy energy = new Energy();
			energy.setAmount(generateEnergy(this.getLocalName())[0]);
			energy.setUnit("Kwh");

			try {
				reply.setContentObject(energy);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			send(reply);
		} else {
		}

	}

}
