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

    public List<Pair<Integer, Position>> GetEvaluatedPositions(){
        // Get all agents view details
        List<Pair<AgentViewDetails, AgentViewDetails>> detections = this.GetDetections();

        List<Pair<Integer,List<Pair<AgentViewDetails,AgentViewDetails>>>>  detectionsBySerialNumber = this.SplitDetectionsByAgents(detections);

        List<Pair<Integer,Position>> positionsEvaluations = new ArrayList<>();
        for (Pair<Integer, List<Pair<AgentViewDetails, AgentViewDetails>>> currentAgentDetections : detectionsBySerialNumber)
        {
            Position evaluatedPos = locationAlgorithm.CalculateEvaluatedPosition(currentAgentDetections.getValue());
            positionsEvaluations.add(new Pair(currentAgentDetections.getKey() ,evaluatedPos));

        }
        return positionsEvaluations;
    }

    private List<Pair<AgentViewDetails, AgentViewDetails>> GetDetections() {
        // rows - agent, cols - other agents, [i][i] - > correct position
        List<Pair<AgentViewDetails, AgentViewDetails>> detections = new ArrayList<>();

        // we stopped here - we need to add identifiers to agent beccause there is a difference between the list of detections
        // when we detect gaurding agent and leading agent

        AgentViewDetails [][] agentsViewDetails = new AgentViewDetails[agents.size()][agents.size()];

        for (DefensingAgent agent : this.getAgents()) {
            ArrayList<DetectionSensor.Detection> agentDetections = agent.detect();
            for (DetectionSensor.Detection agentDetection : agentDetections) {
                AgentViewDetails details = new AgentViewDetails(agent.getSerialNumber(),
                        agentDetection.agentSerialNumber, agent.getPosition(), agent.getHeading(),
                        agentDetection.azimuthWithError(), agentDetection.rangeWithError() );
                agentsViewDetails[agent.getSerialNumber() - 1][agentDetection.agentSerialNumber - 1] = details;
            }
        }

        for (int i =0; i < agents.size(); i++) {
            for (int j = i + 1; j < agents.size(); j++)
            {
                if (agentsViewDetails[i][j] != null || agentsViewDetails[j][i] != null ) {
                    detections.add(new Pair(agentsViewDetails[i][j], agentsViewDetails[j][i]));
                }
            }
        }
        return detections;
    }

    private List<Pair<Integer,List<Pair<AgentViewDetails,AgentViewDetails>>>> SplitDetectionsByAgents(List<Pair<AgentViewDetails, AgentViewDetails>> detections) {
        List<Pair<Integer,List<Pair<AgentViewDetails,AgentViewDetails>>>> detectionsBySerialNumber = new ArrayList();

        for (int i = 1; i <= agents.size(); i++)
        {
            List<Pair<AgentViewDetails,AgentViewDetails>> currentAgentViewsList = new ArrayList<>();
            for (Pair<AgentViewDetails, AgentViewDetails> viewDetailsPair:detections) {
                if(viewDetailsPair.getKey() != null && viewDetailsPair.getKey().getAgentNumberTo == i)
                {
                    currentAgentViewsList.add(viewDetailsPair);
                    break;
                }
                if(viewDetailsPair.getValue() != null && viewDetailsPair.getValue().getAgentNumberTo == i)
                {
                    currentAgentViewsList.add(viewDetailsPair);
                    break;
                }
            }
            detectionsBySerialNumber.add(new Pair<>(i, currentAgentViewsList));
        }
        return detectionsBySerialNumber;
    }
}
