package simulator.LocationAlgorithms;

import simulator.LocationAlgorithms.EvaluationShapes.EvaluationArcher;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationShape;
import javafx.util.Pair;
import simulator.AgentViewDetails;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

public class IntersectionLocationAlgorithm implements ILocationAlgorithm {

    private double measurementDeviation;

    @Override
    public EvaluatedLocationResult CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {

        List<EvaluationArcher> evaluatedArchers = GetEvaluatedPositionsFromAllAgents(viewDetailPairs);

        //java.util.Collections.sort(evaluatedArchers);

        // TODO: fix -- take the center of the intersection instead of the first shape's center
        Position smallestOvalPosition = null;
        if(!evaluatedArchers.isEmpty()) {
            smallestOvalPosition = evaluatedArchers.get(0).center;
        }

        // Create Evaluated Location Result
        EvaluatedLocationResult evaluatedLocationResult = new EvaluatedLocationResult();
        evaluatedLocationResult.position = smallestOvalPosition;

        // Convert to shapes
        evaluatedLocationResult.evaluationShapes = new ArrayList<>();
        for (EvaluationShape shape: evaluatedArchers) {
            evaluatedLocationResult.evaluationShapes.add(shape);
        }
        return evaluatedLocationResult;
    }

    private List<EvaluationArcher> GetEvaluatedPositionsFromAllAgents(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {
        List<EvaluationArcher> evaluatedOvals = new ArrayList<EvaluationArcher>();
        for (Pair<AgentViewDetails, AgentViewDetails> pair :viewDetailPairs) {

            // Calculate errors
            double rangeError = 0;
            try {
                rangeError = this.calculateRangeError(pair);
            } catch (Exception e) {
                e.printStackTrace();
            }
            double spanError = this.calculateSpanErrorInDegrees(pair);

            EvaluationArcher evaluatedOval;
            // TODO: fix - calculate when other see me...
            if (pair.getKey() != null) {
                evaluatedOval = new EvaluationArcher(pair.getKey().myPosition, pair.getKey().azimuthToOtherAgent, pair.getKey().distanceToOtherAgent, rangeError, spanError);
            }
            else{
                double correctAzimuth = Util.set0to359(pair.getValue().azimuthToOtherAgent + 180);
                evaluatedOval = new EvaluationArcher(pair.getValue().myPosition, correctAzimuth, pair.getValue().distanceToOtherAgent, rangeError, spanError);
                //evaluatedOval.center = Util.calulatePositionByAzimuthAndDistance(pair.getValue().myPosition, correctAzimuth, pair.getValue().distanceToOtherAgent);
            }

            evaluatedOvals.add(evaluatedOval);
        }

        return evaluatedOvals;
    }

    private double calculateRangeError(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair) throws Exception{

        double error;
        if(viewDetailPair.getKey() != null && viewDetailPair.getValue() != null)
        {
            error = Math.abs(viewDetailPair.getKey().distanceToOtherAgent - viewDetailPair.getValue().distanceToOtherAgent);
        }
        else if (viewDetailPair.getKey() != null)
        {
            error = measurementDeviation * viewDetailPair.getKey().distanceToOtherAgent / 100;
        }
        else if (viewDetailPair.getValue() != null){
            error = measurementDeviation * viewDetailPair.getValue().distanceToOtherAgent / 100;
        }
        else {
            throw new Exception("Robots Not See Each Other");
        }
        return error;
    }

    private double calculateSpanErrorInDegrees(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        // default
        double error = Util.ConvertBetweenPercentageDeviationAndTrueDeviationByDefinition(measurementDeviation);

        if (viewDetailPair.getValue() != null && viewDetailPair.getKey() != null) {
            // TODO: check this very good!!!
            error = Math.abs(viewDetailPair.getKey().azimuthToOtherAgent -
                    Util.set0to359(viewDetailPair.getValue().azimuthToOtherAgent + 180));
            return error;
        }

        return error;
    }

    @Override
    public void setCVariableInFunction(double c) {

    }

    @Override
    public void setMeasurementDeviation(double deviation) {
        this.measurementDeviation = deviation;
    }

}
