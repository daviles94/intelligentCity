
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
import classOntology.Generator;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class CentralGenerador extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = SmartCityOntology.getInstance();
	private ArrayList<ACLMessage> queu = new ArrayList<ACLMessage>();
	private ArrayList<Generator> generators = new ArrayList<Generator>();
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
		
		addBehaviour(new GetConditions());

		addBehaviour(new ListenEnergyOrder());
		addBehaviour(new SendEnergyOrder());
	}
	
	

	private class ListenEnergyOrder extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.CFP) {
				printInformationRequest(msg);
				queu.add(msg);
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
			switch(step) {
			case 0:			
				if (queu.size() > 0) {

				}
				step ++;
				break;
			case 1:
				if(queu.size()>0) {
					ACLMessage msg = queu.get(0);
					queu.remove(0);
					AID aid = studyConditions(Float.parseFloat(msg.getContent()));
					msg.clearAllReceiver();
					msg.addReceiver(aid);
					send(msg);
				}
				break;
			}
		}
	}

	private AID studyConditions(float kwh) {
		if (kwh > 200) {
			return new AID("plantaNuclear", AID.ISLOCALNAME);
		} else {
			return new AID("plantaEolica", AID.ISLOCALNAME);
		}
	}
	
	private class GetConditions extends Behaviour {
		private int step = 0;
		private MessageTemplate mt;
		private int repliesCnt = 0;

		public void action() {
			switch (step) {
			case 0:
				ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
				for (int i = 0; i < generatorsAID.length; ++i) {
					inform.addReceiver(generatorsAID[i]);
				} 
				inform.setConversationId("inform-kwh-pollution");
				inform.setReplyWith("inform"+System.currentTimeMillis());
				myAgent.send(inform);
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(inform.getReplyWith()));
				step = 1;
				break;
			case 1:
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.INFORM) {
						Generator gen;
						try {
							gen = (Generator) reply.getContentObject();
							generators.add(gen);
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
						
					}
					repliesCnt++;
					if (repliesCnt == generatorsAID.length) {
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			}        
		}

		public boolean done() {
			if (step == 2 && repliesCnt == 0) {
				System.out.println("I can't find generators");
			}
			return step == 2;
		}
	}
}
