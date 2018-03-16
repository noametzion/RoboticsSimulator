package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.AgentViewDetails;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationArcPolygon;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationShape;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by נועם on 3/5/2018.
 */
public class InterserctionShapeLocationAlgorithm implements ILocationAlgorithm{

    private double measurementDeviation;
    private double spanError;

    @Override
    public EvaluatedLocationResult CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {

        spanError = Util.ConvertBetweenPercentageDeviationAndTrueDeviationByDefinition(measurementDeviation);

        List<EvaluationShape> evaluationShapes = CreateAllEvaluationPolygons(viewDetailPairs);

        Position evaluatedPosition = GetIntersectionPoint(evaluationShapes);

        EvaluatedLocationResult result = new EvaluatedLocationResult();
        result.evaluationShapes = evaluationShapes;
        result.position = evaluatedPosition;

        return result;
    }

    private Position GetIntersectionPoint(List<EvaluationShape> evaluationShapes) {
        return evaluationShapes.get(0).getPosition();
    }

    private List<EvaluationShape> CreateAllEvaluationPolygons(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {
        List<EvaluationShape> evaluationShapes = new ArrayList<>();
        for (Pair<AgentViewDetails, AgentViewDetails> viewDetailPair:
             viewDetailPairs) {

            AgentViewDetails key = viewDetailPair.getKey();
            AgentViewDetails value = viewDetailPair.getValue();

            if (key != null) {
                // To avoid evaluation by unknown position;
                if(key.myPosition != null)
                {
                    double rangeError = measurementDeviation * key.distanceToOtherAgent / 100;
                    evaluationShapes.add(new EvaluationArcPolygon(key.otherPosition, key.myPosition, key.azimuthToOtherAgent, key.distanceToOtherAgent, rangeError, spanError));
                }
            }

            if (value != null){
                // To avoid evaluation by unknown position;
                if(key.otherPosition != null)
                {
                    double correctAzimuth = Util.set0to359(value.azimuthToOtherAgent + 180);
                    double rangeError = measurementDeviation * key.distanceToOtherAgent / 100;
                    evaluationShapes.add(new EvaluationArcPolygon(value.myPosition, value.otherPosition, correctAzimuth, value.distanceToOtherAgent, rangeError, spanError));
                }
            }
        }
        return evaluationShapes;
    }

    @Override
    public void setCVariableInFunction(double c) {

    }

    @Override
    public void setMeasurementDeviation(double deviation) {
        this.measurementDeviation = deviation;
    }

    @Override
    public void setNumberOfAgents(int numberOfAgents) {

    }
}
