package view;

import org.eclipse.swt.widgets.Listener;


public interface SimulationTask {

	void step(double newSpeed);
	
	void addSimDoneListener(Listener listener);
}
