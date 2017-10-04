package simulator.MovementAlgorithms;
import java.util.ArrayList;
import simulator.DefensingAgent;
import simulator.AgentType;

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
            if (df.dangerDetected)
            {
                isDangerDetected = true;
                typeOfAgentWhoDetectTheDanger = df.myType;
            }
        }

        // Change move if in danger
        if (isDangerDetected) {
            changeMove(typeOfAgentWhoDetectTheDanger, agents);
        }
    }

    private void changeMove(AgentType typeOfAgentWhoDetectTheDanger, ArrayList<DefensingAgent> agents)
    {
        if (typeOfAgentWhoDetectTheDanger == AgentType.guarding) {
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
    }
}