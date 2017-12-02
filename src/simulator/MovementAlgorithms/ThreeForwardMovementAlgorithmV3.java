package simulator.MovementAlgorithms;

import simulator.AgentType;
import simulator.DefensingAgent;
import simulator.DetectionSensor;

import java.util.ArrayList;

public class ThreeForwardMovementAlgorithmV3 extends MovementAlgorithm {
    @Override
    public void SetAgentsTypes(ArrayList<DefensingAgent> agents) {
        agents.get(1).myTurnToMove = true;
        agents.get(0).myType = AgentType.guarding;
        agents.get(1).myType = AgentType.leading;
        agents.get(2).myType = AgentType.guarding;
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
          if(detection.defensingAgent.myTurnToMove)
                agent.dangerDetected = true;
        }
    }
}
