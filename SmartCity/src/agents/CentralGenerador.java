
package agents;

import java.util.ArrayList;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import ontology.SmartCityOntology;
import classOntology.GeneratorInfo;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class CentralGenerador extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private ArrayList<ACLMessage> queu = new ArrayList<ACLMessage>();
	private ArrayList<GeneratorInfo> generators = new ArrayList<GeneratorInfo>();
	private AID[] generatorsAID;

	protected void setup() {
		System.out.println("Agent: " + getLocalName() + " started.");
		
		addBehaviour(new OneShotBehaviour(this) {
			@Override
			public void action() {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("generate-energy");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					generatorsAID = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						generatorsAID[i] = result[i].getName();
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				
			}
		} );
		
		addBehaviour(new RequestInfoFromAgents());

		addBehaviour(new GetMessages());
		addBehaviour(new SendEnergyOrder());
	}
	
	

	private class GetMessages extends CyclicBehaviour {
		private boolean getinfo = true;
		private MessageTemplate mt;
		private int repliesCnt = 0;

		@Override
		public void action() {
			if(getinfo) {
				getMessagesInfo();
			} else {
				listenEnergyOrders();
			}
		}
		
		private void listenEnergyOrders() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.CFP) {
				printInformationRequest(msg);
				queu.add(msg);
			}
		}
		
		private void getMessagesInfo() {
			mt = MessageTemplate.MatchConversationId("inform-kwh-pollution");
			ACLMessage reply = myAgent.receive(mt);

			if (reply != null) {
				System.out.println("Recibo la informacion de " + reply.getSender().getLocalName());
				if (reply.getPerformative() == ACLMessage.INFORM) {
					GeneratorInfo gen;
					try {
						gen = (GeneratorInfo) reply.getContentObject();
						generators.add(gen);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
				}
				repliesCnt++;
			}
			if(repliesCnt == generatorsAID.length) {
				getinfo = false;
			}
		}
	}
	
	private void printInformationRequest( ACLMessage msg) {
		System.out.println(
				"#########################################################################################################################################################\n"
						+ getLocalName() + ": " + "El consumidor " + msg.getSender().getLocalName()
						+ " necesita " + msg.getContent() + " kwh"
						+ "\n#########################################################################################################################################################\n\n\n");

	}

	private class SendEnergyOrder extends CyclicBehaviour {
		private int step = 0;

		@Override
		public void action() {

			if(queu.size()>0 && generators.size() > 0) {
				ACLMessage msg = queu.get(0);
				queu.remove(0);
				AID aid = studyConditions(Float.parseFloat(msg.getContent()));
				msg.clearAllReceiver();
				msg.addReceiver(aid);
				send(msg);
			}
		}
	}

	private AID studyConditions(float askedkwh) {
		GeneratorInfo generatorinfo = generators.get(0);
		for(int i=1;i<generators.size(); i++) {
			GeneratorInfo generator = generators.get(i);
			if(generator.getKwh() > askedkwh && generator.getPollution() < generatorinfo.getPollution()) {
				generatorinfo = generator;
			} else if (generator.getPollution() < generatorinfo.getPollution()) {
				generatorinfo = generator;
			}
		}
		return new AID(generatorinfo.getName(), AID.ISLOCALNAME);
	}
	
	private class RequestInfoFromAgents extends Behaviour {
		private boolean done = false;

		public void action() {
			ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
			for (int i = 0; i < generatorsAID.length; ++i) {
				inform.addReceiver(generatorsAID[i]);
			} 
			inform.setConversationId("inform-kwh-pollution");
			inform.setReplyWith("inform"+System.currentTimeMillis());
			inform.setInReplyTo(getLocalName());
			myAgent.send(inform);
			done = true;
		}

		@Override
		public boolean done() {
			return done;
		}

	}
}
