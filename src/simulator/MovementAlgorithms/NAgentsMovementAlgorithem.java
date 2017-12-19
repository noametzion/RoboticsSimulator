package simulator.MovementAlgorithms;

import simulator.AgentType;
import simulator.DefensingAgent;
import simulator.DetectionSensor;
import view.Position;

import java.util.ArrayList;

public class NAgentsMovementAlgorithem extends MovementAlgorithm {
    private int numOfAgents;
    private int numOfStepsAgentMoved;
    @Override
    public void MakeStep(ArrayList<DefensingAgent> agents, double newSpeed) {

        boolean isDangerDetected = false;
        numOfStepsAgentMoved++;
        for(DefensingAgent df : agents) {
            df.dangerDetected=false;
            df.setSpeed(newSpeed);
            df.step();
            CheckDanger(df);
            if (df.dangerDetected)
                isDangerDetected = true;
        }
        if (isDangerDetected) {
            ShouldChangeMove = true;
        }
    }

    @Override
    public void SetAgentsTypes(ArrayList<DefensingAgent> agents) {
         numOfStepsAgentMoved=0;
        agents.get(0).myTurnToMove=true;

    }

    @Override
    public void ChangeMove(ArrayList<DefensingAgent> agents) {
        int agentMoving=0;
        numOfStepsAgentMoved=0;
        for(DefensingAgent df: agents){
            if(df.myTurnToMove){
                agentMoving=df.getSerialNumber();
                df.myTurnToMove=false;
                break;
            }
        }
        ShouldChangeMove=false;
        agentMoving = (agentMoving% numOfAgents)+1;
        agents.get(agentMoving-1).myTurnToMove=true;
    }

    @Override
    protected void CheckDanger(DefensingAgent agent) {
        ArrayList<DetectionSensor.Detection> allDetections = agent.detect();
        for (DetectionSensor.Detection detection : allDetections) {
            if ((detection.hisTurnToMove==true && agent.detectionSensor.sensorRange - detection.range < agent.detectionSensor.sensorRange/2) || (numOfStepsAgentMoved>=agent.detectionSensor.sensorRange*1.5) )
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
