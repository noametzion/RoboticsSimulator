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

        // TODO: fix
        Position smallestOvalPosition = null;
        if(!evaluatedArchers.isEmpty()) {
            smallestOvalPosition = evaluatedArchers.get(0).center;
        }

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


            EvaluationArcher evaluatedOval;
            // TODO: fix - calculate when other see me...
            if (pair.getKey() != null) {
                evaluatedOval = new EvaluationArcher(pair.getKey().myPosition, pair.getKey().azimuthToOtherAgent, pair.getKey().distanceToOtherAgent);
                //evaluatedOval.center = Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, pair.getKey().azimuthToOtherAgent, pair.getKey().distanceToOtherAgent);
                //evaluatedOval.topPoint = Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, pair.getKey().azimuthToOtherAgent, pair.getKey().distanceToOtherAgent + evaluatedOval.rangeError);
                //evaluatedOval.bottomPoint =Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, pair.getKey().azimuthToOtherAgent, pair.getKey().distanceToOtherAgent - evaluatedOval.rangeError);
                //evaluatedOval.rightPoint = Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, Util.set0to359(pair.getKey().azimuthToOtherAgent + evaluatedOval.spanError), pair.getKey().distanceToOtherAgent);
                //evaluatedOval.leftPoint = Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, Util.set0to359(pair.getKey().azimuthToOtherAgent - evaluatedOval.spanError), pair.getKey().distanceToOtherAgent);
            }
            else{
                double correctAzimuth = Util.set0to359(pair.getValue().azimuthToOtherAgent + 180);
                evaluatedOval = new EvaluationArcher(pair.getValue().myPosition, correctAzimuth, pair.getValue().distanceToOtherAgent);
                //evaluatedOval.center = Util.calulatePositionByAzimuthAndDistance(pair.getValue().myPosition, correctAzimuth, pair.getValue().distanceToOtherAgent);
            }

            // Calculate errors
            evaluatedOval.rangeError = this.calculateRangeError(pair);
            evaluatedOval.spanError = this.calculateSpanErrorInDegrees(pair);

            evaluatedOvals.add(evaluatedOval);
        }

        return evaluatedOvals;
    }

    private double calculateRangeError(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        if(viewDetailPair.getKey() != null && viewDetailPair.getValue() != null)
        {
            double error = Math.abs(viewDetailPair.getKey().distanceToOtherAgent - viewDetailPair.getValue().distanceToOtherAgent);
            return error;
        }
        return measurementDeviation;
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
