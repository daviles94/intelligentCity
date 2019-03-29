
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
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import ontology.SmartCityOntology;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Generator extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private float kwh = 14;
	private float pollution = 10;
	private boolean weather = false;

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");
		settingConditions(getLocalName());	
		getAID().addUserDefinedSlot("kwh", Float.toString(kwh));
		getAID().addUserDefinedSlot("pollution", Float.toString(pollution));
		System.out.println(getAID());
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("generate-energy");
		sd.setName("JADE-generate-energy");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		configureOntology();

		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				receiveMessage();
			}
		});
	}
	
	private void settingConditions(String TipoPlanta) {
		
		if (TipoPlanta.equals("plantaEolica")) {
			kwh = 60;
			pollution = 10;
			weather = true;
		}

		if (TipoPlanta.equals("plantaNuclear")) {
			kwh = 200;
			pollution = 120;
		}

		if (TipoPlanta.equals("plantaTermicaSolar")) {
			kwh = 80;
			pollution = 20;
			weather = true;
		}

		if (TipoPlanta.equals("plantaGeotermica")) {
			kwh = 100;
			pollution = 50;
		}

		if (TipoPlanta.equals("plantaHidroelectrica")) {
			kwh = 60;
			pollution = 30;
		}
	}
	
	public void setConditionsByWeather(int status) {
		if(status != 1) {
			kwh = (float) (kwh / 1.05);
		} else {
			kwh = (float) (kwh * 1.05);
		}
	}

	public void configureOntology() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
	}

	public float[] generateEnergy(String TipoPlanta) {
		float[] generated = new float[2];
		generated[0] = kwh;
		generated[1] = pollution;

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
