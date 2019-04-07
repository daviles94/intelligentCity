
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

public class Generator extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private float kwh = 14;
	private float pollution = 10;
	private boolean weather = false;

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");
		settingConditions(getLocalName());	

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
		
		addBehaviour(new ResponsePollutionInform());

		addBehaviour(new ResponseEnergy());
	}
	
	private void settingConditions(String TipoPlanta) {
		
		if (TipoPlanta.equals("plantaEolica")) {
			kwh = 260;
			pollution = 10;
			weather = true;
		}

		if (TipoPlanta.equals("plantaNuclear")) {
			kwh = 2000;
			pollution = 420;
		}

		if (TipoPlanta.equals("plantaTermicaSolar")) {
			kwh = 300;
			pollution = 330;
			weather = true;
		}

		if (TipoPlanta.equals("plantaGeotermica")) {
			kwh = 700;
			pollution = 300;
		}

		if (TipoPlanta.equals("plantaHidroelectrica")) {
			kwh = 600;
			pollution = 100;
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

	public float generateEnergy() {
		return kwh;
	}
	
	private class ResponseEnergy extends CyclicBehaviour {

		@Override
		public void action() {
			receiveMessage();
		}
		
		private void receiveMessage() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				ACLMessage reply = msg.createReply();
				Energy energy = new Energy(generateEnergy());

				try {
					reply.setContentObject(energy);
					System.out.println(
							"#########################################################################################################################################################\n"
									+ getLocalName() + ": " + energy + " kwh" + " a " + msg.getSender().getLocalName()
									+ "\n#########################################################################################################################################################\n\n\n");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				send(reply);
			} else {
			}

		}
	}

	
	
	private class ResponsePollutionInform extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null) {
				ACLMessage reply = msg.createReply();

				GeneratorInfo info = new GeneratorInfo();
				info.setKwh(kwh);
				info.setPollution(pollution);
				info.setName(getLocalName());
				
				try {
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContentObject(info);
				} catch(Exception e) {
					System.out.println("\n****************ERROR " + myAgent.getLocalName() + "\n************************");
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
		}
	}

}
