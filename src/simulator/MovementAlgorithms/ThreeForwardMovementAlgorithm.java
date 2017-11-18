package simulator.MovementAlgorithms;
import java.util.ArrayList;
import simulator.DefensingAgent;
import simulator.AgentType;
import simulator.DetectionSensor;

/**
 * Created by נועם on 10/4/2017.
 */
public class ThreeForwardMovementAlgorithm extends MovementAlgorithm {

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

        // Change move if in danger
        if (isDangerDetected) {
            this.ShouldChangeMove = true;
            this.TypeOfAgentWhoDetectTheDanger = typeOfAgentWhoDetectTheDanger;
            //changeMove(typeOfAgentWhoDetectTheDanger, agents);
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
                    da.turnAround();
                }
                da.dangerDetected = false;
            }
        }
        else {
            for (DefensingAgent da : agents) {
                if (da.myType == AgentType.leading) {
                    da.myTurnToMove = true;
                    da.turnAround();
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
        if (agent.myType == AgentType.guarding) {
            ArrayList<DetectionSensor.Detection> allDetections = agent.detect();
            for (DetectionSensor.Detection detection : allDetections) {
                if (agent.detectionSensor.sensorRange - detection.range < 2) {
                    //out.println("danger");
                    agent.dangerDetected = true;
                }
            }
        }
        else
        {
            ArrayList<DetectionSensor.Detection> allDetections = agent.detect();
            double maxAzimuth = agent.getHeading() + ((agent.detectionSensor.sensorSpan / 2) - 3);
            double minAzimuth = 360 - agent.getHeading() - ((agent.detectionSensor.sensorSpan / 2) - 3);
            for (DetectionSensor.Detection detection : allDetections) {
                if (detection.azimuth >= maxAzimuth || detection.azimuth <= minAzimuth) {
                    //out.println("danger");
                    agent.dangerDetected = true;
                }
            }
        }
    }
}
