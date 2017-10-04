package simulator.MovementAlgorithms;
import java.util.ArrayList;
import simulator.DefensingAgent;

/**
 * Created by נועם on 10/4/2017.
 */
public abstract class MovementAlgorithm {
    public abstract void MakeStep(ArrayList<DefensingAgent> agents, double newSpeed);

    public abstract void SetAgentsTypes(ArrayList<DefensingAgent> agents);
}
