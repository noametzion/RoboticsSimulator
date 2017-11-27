package simulator;
import simulator.LocationAlgorithms.ILocationAlgorithm;
import simulator.MovementAlgorithms.MovementAlgorithm;
import view.Position;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
        for (Agent agent: agents) {
            agent.setEvaluatedPosition(new Position(agent.getPosition().x, agent.getPosition().y));
        }
    }

    public void Step(double newSpeed) {

        movementAlgorithm.MakeStep(this.getAgents(), newSpeed);
        HashMap<Integer, Position> evaluatedPositions = this.getEvaluatedPositions();


        for (Agent agent : this.agents) {
            //if (!Double.isNaN(positionsEvaluations.get(agent.serialNumber).x) &&
            // !Double.isNaN(positionsEvaluations.get(agent.serialNumber).y)) {
            if (((DefensingAgent) agent).myTurnToMove == true) {
                agent.setEvaluatedPosition(evaluatedPositions.get(agent.serialNumber));
            }
        }
        if (movementAlgorithm.ShouldChangeMove) {
            movementAlgorithm.ChangeMove(this.agents);
        }

//        System.out.println("----------------------");
//        DecimalFormat df = new DecimalFormat("#.##");
//        for (Agent agent : this.agents) {
//            System.out.println(agent.getSerialNumber());
//            System.out.println(df.format(agent.getPosition().x) + " , " + df.format(agent.getPosition().y) + "    REAL");
//            System.out.println(df.format(agent.getEvaluatedPosition().x) + " , " + df.format(agent.getEvaluatedPosition().y) + "    EVALUATION");
//        }
    }
    public double getDistanceFromActualLocation(Agent agent){
        if(!Double.isNaN(agent.getEvaluatedPosition().x) && agent.getEvaluatedPosition().x!=0) {
            double xDistance = agent.getPosition().x - agent.getEvaluatedPosition().x;
            double yDistance = agent.getPosition().y - agent.getEvaluatedPosition().y;

            double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
            return distance;
        }
        else
            return 0;
    }
    public double getSumAgentsDistanceFromActualLocation(){
        double sumDistance=0;
        int count=0;
        double distance=0;
        for (Agent a: agents) {
            distance=getDistanceFromActualLocation(a);
            if(distance>0){
                sumDistance+=distance;
                count++;
            }

        }
        return sumDistance/count;
    }




    public HashMap<Integer, Position> getEvaluatedPositions(){
        // Get all agents view details
        List<Pair<AgentViewDetails, AgentViewDetails>> detections = this.getDetections();

        // TODO: use map
        List<Pair<Integer,List<Pair<AgentViewDetails,AgentViewDetails>>>>  detectionsBySerialNumber = this.SplitDetectionsByAgents(detections);

        HashMap<Integer,Position> positionsEvaluations = new HashMap<>();
        for (Pair<Integer, List<Pair<AgentViewDetails, AgentViewDetails>>> currentAgentDetections : detectionsBySerialNumber)
        {
            List<Pair<AgentViewDetails, AgentViewDetails>> correctKeyValueList= getCorrectKeyValuePairsList(currentAgentDetections.getKey().intValue(),currentAgentDetections.getValue());
            Position evaluatedPos = locationAlgorithm.CalculateEvaluatedPosition(correctKeyValueList);
            positionsEvaluations.put(currentAgentDetections.getKey() ,evaluatedPos);
        }

        return positionsEvaluations;
    }
    private List<Pair<AgentViewDetails, AgentViewDetails>> getCorrectKeyValuePairsList(int agentId,List<Pair<AgentViewDetails, AgentViewDetails>> oldList){
        List<Pair<AgentViewDetails, AgentViewDetails>> correctKeyValueList= new ArrayList<>();
        for(Pair<AgentViewDetails, AgentViewDetails> pair: oldList) {
            if (pair.getKey() != null && pair.getKey().agentNumberFrom == agentId) {
                AgentViewDetails newKey = pair.getValue();
                AgentViewDetails newValue = pair.getKey();
                correctKeyValueList.add(new Pair<>(newKey, newValue));
            } else
                correctKeyValueList.add(pair);
        }
        return correctKeyValueList;
    }

    private List<Pair<AgentViewDetails, AgentViewDetails>> getDetections() {
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
                    //break;
                }
                if(viewDetailsPair.getValue() != null && viewDetailsPair.getValue().getAgentNumberTo == i)
                {
                    currentAgentViewsList.add(viewDetailsPair);
                    //break;
                }
            }
            detectionsBySerialNumber.add(new Pair<>(i, currentAgentViewsList));
        }
        return detectionsBySerialNumber;
    }
}
