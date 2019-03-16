
package agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class CentralGenerador extends Agent {

  protected void setup() {
  	System.out.println("Hola soy el agente Central Generador"+getLocalName());
  	
  	// Make this agent terminate
  	doDelete();
  } 
}

