
package agents;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Manager extends Agent {

	protected void setup() {
		AgentsCreation();
	}

	public void AgentsCreation() {
		ContainerController cc = getContainerController();
		AgentController plantaEolica;
		AgentController plantaNuclear;
		AgentController plantaTermicaSolar;
		AgentController plantaGeotermica;
		AgentController plantaHidroelectrica;

		AgentController centralGenerador;

		AgentController casa;
		AgentController restaurante;
		AgentController supermercado;
		AgentController peluqueria;
		AgentController ecocasa;

		// centralAmacenador;
		// centralGestor;
		// cosumidor;
		// centralEcoCosumidor;
		// centralEcoCosumidor centralConsumidor;

		try {
			plantaEolica = cc.createNewAgent("plantaEolica", "agents.Generator", null);
			plantaNuclear = cc.createNewAgent("plantaNuclear", "agents.Generator", null);
			plantaTermicaSolar = cc.createNewAgent("plantaTermicaSolar", "agents.Generator", null);
			plantaGeotermica = cc.createNewAgent("plantaGeotermica", "agents.Generator", null);
			plantaHidroelectrica = cc.createNewAgent("plantaHidroelectrica", "agents.Generator", null);

			centralGenerador = cc.createNewAgent("centralGenerador", "agents.CentralGenerador", null);

			casa = cc.createNewAgent("casa", "agents.Consumidor", null);
			restaurante = cc.createNewAgent("restaurante", "agents.Consumidor", null);
			supermercado = cc.createNewAgent("supermercado", "agents.Consumidor", null);
			peluqueria = cc.createNewAgent("peluqueria", "agents.Consumidor", null);
			ecocasa = cc.createNewAgent("ecocasa", "agents.Ecoconsumidor", null);

			// ac = cc.createNewAgent("centralAmacenador", "agents.CentralGenerador", null);
			// ac = cc.createNewAgent("centralGestor", "agents.CentralGenerador", null);
			// ac = cc.createNewAgent("cosumidor", "agents.CentralGenerador", null);
			// ac = cc.createNewAgent("centralEcoCosumidor", "agents.CentralGenerador",
			// null);
			// ac = cc.createNewAgent("centralConsumidor", "agents.CentralGenerador", null);

			plantaEolica.start();
			plantaNuclear.start();
//			plantaTermicaSolar.start();
//			plantaGeotermica.start();
//			plantaHidroelectrica.start();

			centralGenerador.start();

			casa.start();
			restaurante.start();
			supermercado.start();
			peluqueria.start();
			ecocasa.start();
			
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
