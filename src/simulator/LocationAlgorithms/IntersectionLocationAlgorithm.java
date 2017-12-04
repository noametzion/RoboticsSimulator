package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.AgentViewDetails;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

/**
 * Created by נועם on 12/3/2017.
 */
public class IntersectionLocationAlgorithm implements ILocationAlgorithm {

    private double measurementDeviation;

    @Override
    public Position CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {

        List<EvaluationOval> evaluatedOvals = GetEvaluatedPositionsFromAllAgents(viewDetailPairs);

        // TODO: check well
        java.util.Collections.sort(evaluatedOvals);

        Position smallestOvalPosition = null;
        if(!evaluatedOvals.isEmpty()) {
            smallestOvalPosition = evaluatedOvals.get(0).center;
        }
        return smallestOvalPosition;
    }

    private List<EvaluationOval> GetEvaluatedPositionsFromAllAgents(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {
        List<EvaluationOval> evaluatedOvals = new ArrayList<EvaluationOval>();
        for (Pair<AgentViewDetails, AgentViewDetails> pair :viewDetailPairs) {
            double distanceAvg = this.CalculateAverageDistance(pair);
            double distanceError = this.CalculateDistanceError(pair);
            double angelAvg = this.CalculateAverageAngel(pair);
            double angelError = this.calculateAngelError(pair);

            EvaluationOval evaluatedOval = new EvaluationOval();
            // TODO: fix - calculate when other see me...
            if (pair.getKey() != null) {
                evaluatedOval.center = Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, angelAvg, distanceAvg);
            }
            else{
                evaluatedOval.center = Util.calulatePositionByAzimuthAndDistance(pair.getValue().myPosition, angelAvg, distanceAvg);
            }
            evaluatedOval.rangeRadius = distanceError;
            evaluatedOval.spanRadius = angelError;
            evaluatedOvals.add(evaluatedOval);
        }

        return evaluatedOvals;
    }

    private double CalculateAverageDistance(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        double sum = 0;
        int counter = 0;
        if(viewDetailPair.getKey() != null)
        {
            sum += viewDetailPair.getKey().distanceToOtherAgent;
            counter ++;
        }
        if(viewDetailPair.getValue() != null)
        {
            sum += viewDetailPair.getValue().distanceToOtherAgent;
            counter++;
        }

        double avg = sum/counter;
        return avg;
    }

    private double CalculateDistanceError(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        double sum = 0;
        double sub = 0;
        int counter = 0;
        if(viewDetailPair.getKey() != null)
        {
            sum += viewDetailPair.getKey().distanceToOtherAgent;
            sub += viewDetailPair.getKey().distanceToOtherAgent;
            counter ++;
        }
        if(viewDetailPair.getValue() != null)
        {
            sum += viewDetailPair.getValue().distanceToOtherAgent;
            sub -= viewDetailPair.getValue().distanceToOtherAgent;
            counter++;
        }

        double avg = sum/counter;

        // Calculate percentage of deviation -> default
        double error = measurementDeviation * avg / 100;
        if (counter == 2)
        {
            error = Math.abs(sub / 2);
            return error;

        }
        return error;
    }

    private double CalculateAverageAngel(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        double avgAngel = 0;
        if (viewDetailPair.getValue() != null && viewDetailPair.getKey() != null) {
            avgAngel += viewDetailPair.getKey().azimuthToOtherAgent;
            avgAngel += Util.set0to359(viewDetailPair.getValue().azimuthToOtherAgent + 180);
            avgAngel = avgAngel/2;
        }
        else if(viewDetailPair.getKey() != null)
        {
            //avgAngel = Util.set0to359(Util.getDegDiff(viewDetailPair.getValue().azimuthToOtherAgent, viewDetailPair.getValue().myHeading));
            avgAngel=viewDetailPair.getKey().azimuthToOtherAgent;
        }
        else if(viewDetailPair.getValue() != null)
        {
            //avgAngel = Util.set0to359(Util.getDegDiff(viewDetailPair.getKey().azimuthToOtherAgent, viewDetailPair.getKey().myHeading));
            avgAngel=viewDetailPair.getValue().azimuthToOtherAgent;
        }
        return avgAngel;
    }

    private double calculateAngelError(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        // default
        double error = Util.ConvertBetweenPercentageDeviationAndTrueDeviationByDefinition(measurementDeviation);

        if (viewDetailPair.getValue() != null && viewDetailPair.getKey() != null) {
            // TODO: check this very good!!!
            double subAbs = Math.abs(viewDetailPair.getKey().azimuthToOtherAgent -
                    Util.set0to359(viewDetailPair.getValue().azimuthToOtherAgent + 180));
            return subAbs / 2;
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

    private class EvaluationOval implements Comparable<EvaluationOval>{
        private Position center;
        private double rangeRadius;
        private double spanRadius;

        public double GetOvalArea(){

            double area = rangeRadius * spanRadius * Math.PI;
            return area;
        }

        @Override
        public int compareTo(EvaluationOval o) {
            double myArea = GetOvalArea();
            double oArea = o.GetOvalArea();

            if (myArea > oArea)
            {
                return 1;
            }
            else if (myArea < oArea)
            {
                return -1;
            }
            return 0;
        }
    }
}
