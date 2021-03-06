package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.Agent;
import simulator.AgentViewDetails;
import simulator.DefensingAgent;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationShape;
import view.Position;

import java.util.List;

/**
 * Created by נועם on 10/24/2017.
 */
public interface ILocationAlgorithm {
    // first - other see moving
    // second - moving see other
    EvaluatedLocationResult CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs);
    void setCVariableInFunction(double c);
    void setMeasurementDeviation(double deviation);
}
