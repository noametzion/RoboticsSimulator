package simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javafx.util.Pair;
import org.eclipse.swt.widgets.Listener;

import simulator.DetectionSensor.Detection;
//import simulator.MBDtest.InterceptProcess;
import simulator.LocationAlgorithms.LocationAlgorithmCreator;
import simulator.MovementAlgorithms.MovementAlgorithmCreator;
import utils.Util;
import view.Drawables;
import view.Position;
import view.SimulationTask;

public class PerimeterDefenseSimTask implements SimulationTask {
	private Simulation simulation;
	private MovementAlgorithmCreator movementAlgorithmCreator = new MovementAlgorithmCreator();
	private LocationAlgorithmCreator locationAlgorithmCreator = new LocationAlgorithmCreator();
	private Listener simDoneListener;
	int minRange = 0;

	public PerimeterDefenseSimTask(String scenatioFileName) {
		Drawables.reset();
		try {
			// Create simulation
			simulation = new Simulation();

			// Read simulation scenario
			BufferedReader in=new BufferedReader(new FileReader(scenatioFileName));
			String line;

			// Position for 3 agents
			// TODO: upgrade to generic number
			double positionDistance =Double.parseDouble(in.readLine().split(" ")[2]);
			int sensorSpan = Integer.parseInt(in.readLine().split(" ")[2]);
			int sensorRange = Integer.parseInt(in.readLine().split(" ")[2]);
			ArrayList<Position> agentPositions = CalculateAgentPositions(positionDistance, sensorRange, sensorSpan);

			// Create agents
			int numberOfAgents=Integer.parseInt(in.readLine().split(" ")[1]);
			//agents=new ArrayList<DefensingAgent>(numberOfAgents);
			for(int i=0;i<numberOfAgents;i++)

				simulation.addAgent(new DefensingAgent(agentPositions.get(i).x, agentPositions.get(i).y , 0 , new DetectionSensor(0, sensorRange, sensorSpan ,minRange)));

			// Set agents configuration
			int i=0;
			in.readLine();
			while((line=in.readLine()).startsWith("\t")){
				String[] sp1=line.split("\t");
				String[] sp = sp1[1].split(",");
				simulation.getAgents().get(i).setRotationSpeed(Double.parseDouble(sp[0]));
				simulation.getAgents().get(i).setRotationError(Double.parseDouble(sp[1]));
				simulation.getAgents().get(i).setDetectionRange(sensorRange);
				simulation.getAgents().get(i).setDetectionDeviation(Double.parseDouble(sp[2]));
				simulation.getAgents().get(i).setSerialNumber(i+1);
				i++;
			}

			// Read movement algorithm
			String movementAlgorithmName = line.split(" ")[2];
			simulation.movementAlgorithm = movementAlgorithmCreator.Create(movementAlgorithmName);

			// Read movement algorithm
			line = in.readLine();
			String locationAlgorithmName = line.split(" ")[2];
			simulation.locationAlgorithm = locationAlgorithmCreator.Create(locationAlgorithmName);

			simulation.InitSimulationSettings();

			for(DefensingAgent df : simulation.getAgents())
				Drawables.drawables.add(df);

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Position> CalculateAgentPositions(double positionDistance, int sensorRange, int sensorSpan) {
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

		simulation.Step(newSpeed);

		// finish
		if(simDoneListener!=null){
			simDoneListener.handleEvent(null);
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
