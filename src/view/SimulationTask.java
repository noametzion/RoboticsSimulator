package view;

import org.eclipse.swt.widgets.Listener;
import simulator.Simulation;


public interface SimulationTask {

	void step(double newSpeed);
	
	void addSimDoneListener(Listener listener);

	Simulation getSimulation();

}
