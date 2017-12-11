package simulator.MovementAlgorithms;

import simulator.DefensingAgent;
import simulator.DetectionSensor;
import view.Position;

import java.util.ArrayList;

public class NAgentsMovementAlgorithem extends MovementAlgorithm {
    private int numOfAgents;
    @Override
    public void MakeStep(ArrayList<DefensingAgent> agents, double newSpeed) {

    }

    @Override
    public void SetAgentsTypes(ArrayList<DefensingAgent> agents) {
            agents.get(0).myTurnToMove=true;
    }

    @Override
    public void ChangeMove(ArrayList<DefensingAgent> agents) {
        int agentMoving=0;
        for(DefensingAgent df: agents){
            if(df.myTurnToMove=true){
                agentMoving=df.getSerialNumber();
                df.myTurnToMove=false;
                break;
            }
        }
        agentMoving = (agentMoving +1)% numOfAgents;
        agents.get(agentMoving).myTurnToMove=true;
    }

    @Override
    protected void CheckDanger(DefensingAgent agent) {
        ArrayList<DetectionSensor.Detection> allDetections = agent.detect();
        for (DetectionSensor.Detection detection : allDetections) {
            if (detection.hisTurnToMove==true && agent.detectionSensor.sensorRange - detection.range < 1 )
                agent.dangerDetected = true;
        }

    }

    @Override
    public ArrayList<Position> CalculateAgentPositions(double positionDistance, int sensorRange, int sensorSpan) {
        ArrayList<Position> agentPositions= new ArrayList<>();
        double y=0;
        double x= (int)numOfAgents/2 * -2;
        for(int i=0;i<numOfAgents;i++){
            agentPositions.add(new Position(x,y));
            x+=2;
        }
        return agentPositions;
    }

    @Override
    public void setNumOfAgents(int num) {
        numOfAgents=num;
    }
}
