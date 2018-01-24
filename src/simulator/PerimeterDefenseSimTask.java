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
	double c;

	public PerimeterDefenseSimTask(String scenatioFileName, int deviation, int movementDeviation,double c) throws IllegalArgumentException{
		Drawables.reset();
		try {
			// Create simulation
			simulation = new Simulation();
			this.c=c;
			// Read simulation scenario
			BufferedReader in=new BufferedReader(new FileReader(scenatioFileName));
			String line;

			// Position for 3 agents
			// TODO: upgrade to generic number
			double positionDistance =Double.parseDouble(in.readLine().split(" ")[2]);
			int sensorSpan = Integer.parseInt(in.readLine().split(" ")[2]);
			int sensorRange = Integer.parseInt(in.readLine().split(" ")[2]);
			/////////////////////////////////////////////////////////////////////////////
			///////deviation for experiemnt                    /////
			////////////////////////////////////////////////////////////////////////////
			Double.parseDouble(in.readLine().split(" ")[2]);
			double sensorDeviation = deviation;
			// Read movement algorithm
			line=in.readLine();
			String movementAlgorithmName = line.split(" ")[2];
			simulation.movementAlgorithm = movementAlgorithmCreator.Create(movementAlgorithmName);
			//double sensorDeviation = Double.parseDouble(in.readLine().split(" ")[2]);


			// Get Error type
			line=in.readLine();
			String errorTypeName = line.split(" ")[1];
			ErrorType errorType;
			if (errorTypeName.equals("fixed"))
			{
				errorType = ErrorType.Fixed;
			}
			else if(errorTypeName.equals("random"))
			{
				errorType = ErrorType.Ramdom;
			}
			else {
				throw new IllegalArgumentException("ERROR Should be FIXED or RANDOM");
			}

			// Create agents
			int numberOfAgents=Integer.parseInt(in.readLine().split(" ")[1]);
			simulation.movementAlgorithm.setNumOfAgents(numberOfAgents);
			ArrayList<Position> agentPositions = simulation.movementAlgorithm.CalculateAgentPositions(positionDistance, sensorRange, sensorSpan);
			for(int i=0;i<numberOfAgents;i++)
			{
				DetectionSensor sensor = new DetectionSensor(0, sensorRange, sensorSpan ,minRange, deviation, errorType);
				simulation.addAgent(new DefensingAgent(agentPositions.get(i).x, agentPositions.get(i).y , 0 , sensor, movementDeviation));
			}

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
//			String movementAlgorithmName = line.split(" ")[2];
//			simulation.movementAlgorithm = movementAlgorithmCreator.Create(movementAlgorithmName);

			// Read movement algorithm
//			line = in.readLine();
			String locationAlgorithmName = line.split(" ")[2];
			simulation.locationAlgorithm = locationAlgorithmCreator.Create(locationAlgorithmName);
			simulation.locationAlgorithm.setCVariableInFunction(c);
			simulation.locationAlgorithm.setMeasurementDeviation(deviation);
			simulation.InitSimulationSettings();

			for(DefensingAgent df : simulation.getAgents())
				Drawables.drawables.add(df);

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@Override
	public Simulation getSimulation() {
		return simulation;
	}
}
