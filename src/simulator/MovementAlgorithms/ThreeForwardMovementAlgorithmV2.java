package simulator.MovementAlgorithms;

import simulator.AgentType;
import simulator.DefensingAgent;

import java.util.ArrayList;

public class ThreeForwardMovementAlgorithmV2 extends MovementAlgorithm {
    @Override
    public void MakeStep(ArrayList<DefensingAgent> agents, double newSpeed) {

    }

    @Override
    public void SetAgentsTypes(ArrayList<DefensingAgent> agents) {
        agents.get(1).myTurnToMove = true;
        agents.get(0).myType = AgentType.guarding;
        agents.get(1).myType = AgentType.leading;
        agents.get(2).myType = AgentType.guarding;

    }

    @Override
    public void ChangeMove(ArrayList<DefensingAgent> agents) {

    }

    @Override
    protected void CheckDanger(DefensingAgent agent) {

    }
}
