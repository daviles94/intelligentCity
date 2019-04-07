
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
import classOntology.Energy;
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
	private boolean sendAlmacenador = true;

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
		private MessageTemplate mt;
		private int repliesCnt = 0;

		@Override
		public void action() {
			getMessagesInfo();
			listenEnergyOrders();
		}
		
		private void listenEnergyOrders() {
			MessageTemplate mtmp = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mtmp);
			if (msg != null) {
				printInformationRequest(msg);
				queu.add(msg);
			}
		}
		
		private void getMessagesInfo() {
			mt = MessageTemplate.MatchConversationId("inform-kwh-pollution");
			ACLMessage reply = myAgent.receive(mt);

			if (reply != null) {
				System.out.println("Recibo la informacion de " + reply.getSender().getLocalName());
				try {
					GeneratorInfo gen;
					gen = (GeneratorInfo) reply.getContentObject();
					generators.add(gen);
					repliesCnt++;
					System.out.println(generators);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				
			}
			mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage inform = myAgent.receive(mt);
			
			if(inform != null && inform.getSender().getLocalName().equals("almacenador")) {
				sendAlmacenador = true;
			}
			
		}
	}
	
	private void printInformationRequest( ACLMessage msg) {
		try {
			System.out.println(
					"#########################################################################################################################################################\n"
							+ getLocalName() + ": " + "El consumidor " + msg.getSender().getLocalName()
							+ " necesita " + msg.getContentObject() + " kwh"
							+ "\n#########################################################################################################################################################\n\n\n");
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

	}

	private class SendEnergyOrder extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null && msg.getSender().getLocalName().equals("almacenador")) {
				System.out.println(getLocalName() + " El almacenador no ha podido enviar la peticion");
				sendAlmacenador = false;
			}
			if(queu.size()>0) {
				ACLMessage msgqueu = queu.get(0);
				queu.remove(0);
				if(msgqueu.getSender().getLocalName().equals("almacenador")) {
					sendRequestToGenerators(msgqueu);
					sendAlmacenador = false;
				}
				else {
					if(sendAlmacenador) {
						System.out.println("\n#######################\n"+getLocalName() + " envio la peticion al almacen\n###############");
						AID almacenador = new AID("almacenador", AID.ISLOCALNAME);
						msgqueu.clearAllReceiver();
						msgqueu.addReceiver(almacenador);
						send(msgqueu);
					} else if(generators.size() > 0){
						System.out.println("\n#######################\n"+getLocalName() + " envio la peticion a los generadores\n###############");
						sendRequestToGenerators(msgqueu);
					} else {
						System.out.println("ERROR");
					}
				}			
			}

		}
		
		private void sendRequestToGenerators(ACLMessage msg) {
			Energy energy;
			try {
				energy = (Energy) msg.getContentObject();
				AID aid = studyConditions(energy.getAmount());
				System.out.println("\n==================\n"+getLocalName() + " le envio la peticion a "+ aid.getLocalName() + "\n==================");
				msg.clearAllReceiver();
				msg.addReceiver(aid);
				send(msg);
			} catch (UnreadableException e) {
				e.printStackTrace();
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
