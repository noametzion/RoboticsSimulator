package simulator.MovementAlgorithms;

import simulator.AgentType;
import simulator.DefensingAgent;
import simulator.DetectionSensor;
import view.Position;

import java.util.ArrayList;

public class ThreeForwardMovementAlgorithmV2 extends MovementAlgorithm {
    @Override
    public void SetAgentsTypes(ArrayList<DefensingAgent> agents) {
        agents.get(1).myTurnToMove = true;
        agents.get(0).myType = AgentType.guarding;
        agents.get(1).myType = AgentType.leading;
        agents.get(2).myType = AgentType.guarding;
    }
    @Override
    public void setNumOfAgents(int num){

    }

    @Override
    public void MakeStep(ArrayList<DefensingAgent> agents, double newSpeed) {

        boolean isDangerDetected = false;
        AgentType typeOfAgentWhoDetectTheDanger = AgentType.guarding;

        for(DefensingAgent df : agents) {
            df.setSpeed(newSpeed);
            df.step();
            this.CheckDanger(df);
            if (df.dangerDetected)
            {
                isDangerDetected = true;
                typeOfAgentWhoDetectTheDanger = df.myType;
            }
        }


        if (isDangerDetected) {
            this.ShouldChangeMove = true;
            this.TypeOfAgentWhoDetectTheDanger = typeOfAgentWhoDetectTheDanger;

        }
    }

    public void ChangeMove(ArrayList<DefensingAgent> agents)
    {
        if (this.TypeOfAgentWhoDetectTheDanger == AgentType.guarding) {
            for (DefensingAgent da : agents) {
                if (da.myType == AgentType.guarding) {
                    da.myTurnToMove = true;
                } else if (da.myType == AgentType.leading) {
                    da.myTurnToMove = false;
                }
                da.dangerDetected = false;
            }
        }
        else {
            for (DefensingAgent da : agents) {
                if (da.myType == AgentType.leading) {
                    da.myTurnToMove = true;

                } else if (da.myType == AgentType.guarding) {
                    da.myTurnToMove = false;
                }
                da.dangerDetected = false;
            }
        }

        this.ShouldChangeMove = false;
        this.TypeOfAgentWhoDetectTheDanger = null;
    }

    @Override
    protected void CheckDanger(DefensingAgent agent) {

        ArrayList<DetectionSensor.Detection> allDetections = agent.detect();
        for (DetectionSensor.Detection detection : allDetections) {
            if (agent.detectionSensor.sensorRange - detection.range < 1 && detection.hisTurnToMove==true)
                agent.dangerDetected = true;
        }
    }
    @Override
    public ArrayList<Position> CalculateAgentPositions(double positionDistance, int sensorRange, int sensorSpan) {
        ArrayList<Position> agentsPositions = new ArrayList<Position>();
        double maxDistaceBetweenTwoAgents = sensorRange * Math.cos(Math.toRadians(90 - (sensorSpan/2)));
        double distaceBetweenTwoAgents = maxDistaceBetweenTwoAgents * positionDistance;
        agentsPositions.add(new Position(distaceBetweenTwoAgents, 0));
        agentsPositions.add(new Position(0, 0));
        agentsPositions.add(new Position(-distaceBetweenTwoAgents, 0));
        return agentsPositions;
    }
}
