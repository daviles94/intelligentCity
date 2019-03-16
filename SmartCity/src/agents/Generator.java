
package agents;

import jade.core.Agent;


public class Generator extends Agent {

  protected void setup() {
  	System.out.println("Hello World! My name is "+getLocalName());
  	
  	// Make this agent terminate
  	doDelete();
  } 
}

