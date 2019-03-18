
package agents;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Manager extends Agent {

	protected void setup() {
		// System.out.println("Agente: " + getLocalName());

		AgentsCreation();

	}

	public void AgentsCreation() {
		ContainerController cc = getContainerController();
		AgentController plantaEolica;
//		AgentController plantaNuclear;
//		AgentController plantaTermicaSolar;
//		AgentController plantaGeotermica;
//		AgentController plantaHidroelectrica;
		AgentController centralGenerador;
		// centralAmacenador;
		// centralGestor;
		// cosumidor;
		// centralEcoCosumidor;
		// centralEcoCosumidor centralConsumidor;

		try {
			plantaEolica = cc.createNewAgent("plantaEolica", "agents.Generator", null);
//			plantaNuclear = cc.createNewAgent("plantaNuclear", "agents.Generator", null);
//			plantaTermicaSolar = cc.createNewAgent("plantaTermicaSolar", "agents.Generator", null);
//			plantaGeotermica = cc.createNewAgent("plantaGeotermica", "agents.Generator", null);
//			plantaHidroelectrica = cc.createNewAgent("plantaHidroelectrica", "agents.Generator", null);

			centralGenerador = cc.createNewAgent("centralGenerador", "agents.CentralGenerador", null);
			// ac = cc.createNewAgent("centralAmacenador", "agents.CentralGenerador", null);
			// ac = cc.createNewAgent("centralGestor", "agents.CentralGenerador", null);
			// ac = cc.createNewAgent("cosumidor", "agents.CentralGenerador", null);
			// ac = cc.createNewAgent("centralEcoCosumidor", "agents.CentralGenerador",
			// null);
			// ac = cc.createNewAgent("centralConsumidor", "agents.CentralGenerador", null);

			plantaEolica.start();
//			plantaNuclear.start();
//			plantaTermicaSolar.start();
//			plantaGeotermica.start();
//			plantaHidroelectrica.start();
			centralGenerador.start();

		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
