package simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.swt.widgets.Listener;

import simulator.DetectionSensor.Detection;
//import simulator.MBDtest.InterceptProcess;
import utils.Util;
import view.Drawables;
import view.Position;
import view.SimulationTask;

public class PerimeterDefenseSimTask implements SimulationTask {
	
	ArrayList<DefensingAgent> agents;
	private Listener simDoneListener;
	int minRange = 0;

	public PerimeterDefenseSimTask(String scenatioFileName) {
		Drawables.reset();		
		try {
			// Read simulation scenario
			BufferedReader in=new BufferedReader(new FileReader(scenatioFileName));
			String line;

			// Position for 3 agents
			// TODO: upgrade to generic number
			double positionDistance =Double.parseDouble(in.readLine().split(" ")[2]);
			int sensorSpan = Integer.parseInt(in.readLine().split(" ")[2]);
			int sensorRange = Integer.parseInt(in.readLine().split(" ")[2]);
			ArrayList<Position> agentPositions = CaculateAgentPositions(positionDistance, sensorRange, sensorSpan);

			// Create agents
			int numberOfAgents=Integer.parseInt(in.readLine().split(" ")[1]);
			agents=new ArrayList<DefensingAgent>(numberOfAgents);
			for(int i=0;i<numberOfAgents;i++)
				agents.add(new DefensingAgent(agentPositions.get(i).x, agentPositions.get(i).y , 0 , new DetectionSensor(0, sensorRange, sensorSpan ,minRange)));

			// Set agents configuration
			int i=0;
			in.readLine();
			while((line=in.readLine()).startsWith("\t")){
				String[] sp=line.split("\t");
				agents.get(i).setRotationSpeed(Double.parseDouble(sp[1]));
				agents.get(i).setRotationError(Double.parseDouble(sp[2]));
				agents.get(i).setDetectionRange(sensorRange);
				agents.get(i).setDetectionDeviation(Double.parseDouble(sp[3]));
				agents.get(i).setSerialNumber(i);
				i++;
			}

			// TODO: not here
			agents.get(1).myTurnToMove = true;
			agents.get(1).detectionSensor.span += 20;
			agents.get(0).myType = agentType.guarding;
			agents.get(1).myType = agentType.leading;
			agents.get(2).myType = agentType.guarding;

			for(DefensingAgent df : agents)
				Drawables.drawables.add(df);		

			in.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	private ArrayList<Position> CaculateAgentPositions(double positionDistance, int sensorRange, int sensorSpan) {
		ArrayList<Position> agentsPositions = new ArrayList<Position>();
		double maxDistaceBetweenTwoAgents = sensorRange * Math.cos(Math.toRadians(90 - (sensorSpan/2)));
		double distaceBetweenTwoAgents = maxDistaceBetweenTwoAgents * positionDistance;
		agentsPositions.add(new Position(distaceBetweenTwoAgents, 0));
		agentsPositions.add(new Position(0, 0));
		agentsPositions.add(new Position(-distaceBetweenTwoAgents, 0));
		return agentsPositions;
	}


	@Override
	public void step(double newSpeed) {

		boolean isDangerDetected = false;
		agentType typeOfAgentWhoDetectTheDanger = agentType.guarding;

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
			changeMove(typeOfAgentWhoDetectTheDanger);
		}

		// finish
		if(simDoneListener!=null){
			simDoneListener.handleEvent(null);
		}
	}

	private void changeMove(agentType typeOfAgentWhoDetectTheDanger)
	{
		if (typeOfAgentWhoDetectTheDanger == agentType.guarding) {
			for (DefensingAgent da : agents) {
				if (da.myType == agentType.guarding) {
					da.myTurnToMove = true;
				} else if (da.myType == agentType.leading) {
					da.myTurnToMove = false;
					da.turnAround();
				}
				da.dangerDetected = false;
			}
		}
		else {
			for (DefensingAgent da : agents) {
				if (da.myType == agentType.leading) {
					da.myTurnToMove = true;
					da.turnAround();
				} else if (da.myType == agentType.guarding) {
					da.myTurnToMove = false;
				}
				da.dangerDetected = false;
			}
		}
	}

	private void sortByThreat(ArrayList<Detection> detectedFoes) {
		Collections.sort(detectedFoes,new Comparator<Detection>() {

			@Override
			public int compare(Detection d1, Detection d2) {
				double t1=Util.getTime(minRange,d1);
				double t2=Util.getTime(minRange,d2);
				return (int)(t1-t2);
			}
		});
		
	}

	@Override
	public void addSimDoneListener(Listener listener) {
		simDoneListener=listener;		
	}


}
