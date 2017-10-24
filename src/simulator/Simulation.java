package simulator;
import simulator.LocationAlgorithms.ILocationAlgorithm;
import simulator.MovementAlgorithms.MovementAlgorithm;
import view.Position;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 * Created by נועם on 8/30/2017.
 */
public class Simulation {
    public MovementAlgorithm movementAlgorithm;
    public ILocationAlgorithm locationAlgorithm;

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

    public Position GetEvaluatedPosition(){
        // Get all agents view details
        List<Pair<AgentViewDetails, AgentViewDetails>> detections = this.GetDetections();
        return locationAlgorithm.CalculateEvaluatedPosition(detections);
    }

    private List<Pair<AgentViewDetails, AgentViewDetails>> GetDetections() {
        // rows - agent, cols - other agents, [i][i] - > correct position
        List<Pair<AgentViewDetails, AgentViewDetails>> detections = new ArrayList<>();

        // we stopped here - we need to add identifiers to agent beccause there is a difference between the list of detections
        // when we detect gaurding agent and leading agent

//        for (DefensingAgent agent : this.getAgents()) {
//            if(agent.myTurnToMove == false)
//            ArrayList<DetectionSensor.Detection> agentDetections = agent.detect();
//            for (DetectionSensor.Detection agentDetection :agentDetections) {
//                if
//                double evaluation = agentDetection.
//            }
//        }

        return detections;
    }


}
