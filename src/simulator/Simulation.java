package simulator;
import simulator.MovementAlgorithms.MovementAlgorithm;

import java.util.ArrayList;
/**
 * Created by נועם on 8/30/2017.
 */
public class Simulation {
    public MovementAlgorithm movementAlgorithm;
    private ArrayList<DefensingAgent> agents;
    public ArrayList<DefensingAgent> getAgents() {
        return this.agents; }
    public void addAgent(DefensingAgent agent) {
        if (agents == null) {
            agents = new ArrayList<DefensingAgent>();
        }
        this.agents.add(agent);
    }

    public void InitSimulationSettings()
    {
        movementAlgorithm.SetAgentsTypes(agents);
    }

    public void Step(double newSpeed){
        movementAlgorithm.MakeStep(this.getAgents(), newSpeed);
    }
}
