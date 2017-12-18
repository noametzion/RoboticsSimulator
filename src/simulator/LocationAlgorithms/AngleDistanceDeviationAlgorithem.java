package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.AgentViewDetails;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationShape;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

public class AngleDistanceDeviationAlgorithem implements ILocationAlgorithm {

    private double c;

    @Override
    public EvaluatedLocationResult CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {
        List<PositionGrade> evaluatedPositions = GetEvaluatedDistanceAndPositionsFromAllAgents(viewDetailPairs);

        double sumGrade = 0;
        double sumWeight=0;
        double sumX = 0;
        double sumY = 0;

        for (PositionGrade pg:evaluatedPositions) {
            sumGrade += pg.grade;
        }
        for (PositionGrade pg:evaluatedPositions) {
            pg.weight= sumGrade/pg.grade;
        }
        for (PositionGrade pg:evaluatedPositions) {
            sumWeight += pg.weight;
        }
        for (PositionGrade pg:evaluatedPositions){
            sumX += pg.weight/sumWeight * pg.position.x;
            sumY += pg.weight/sumWeight * pg.position.y;
        }

        EvaluatedLocationResult evaluatedLocationResult = new EvaluatedLocationResult();
        evaluatedLocationResult.position = new Position(sumX, sumY);

        return evaluatedLocationResult;
    }
    @Override
    public void setCVariableInFunction(double c) {
        this.c=c;
    }

    @Override
    public void setMeasurementDeviation(double deviation) {

    }

    private List<PositionGrade> GetEvaluatedDistanceAndPositionsFromAllAgents(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs){
        List<PositionGrade> evaluatedDistanceAndPositions = new ArrayList<>();
        for (Pair<AgentViewDetails, AgentViewDetails> viewDetailPair:viewDetailPairs) {
            if(viewDetailPair.getKey().myTurnToMove==false) {
                double distanceAvg = calculateAverageDistance(viewDetailPair);
                double angelAvg = calculateAverageAngel(viewDetailPair);
                Position evaluatedPosition;
                if (viewDetailPair.getKey() != null) {
                    evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getKey().myPosition, angelAvg, distanceAvg);
                } else {
                    evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getValue().myPosition, angelAvg, distanceAvg);
                }

                double sumDistance = calculateSumDistance(viewDetailPair);
                double beta = Math.abs(calculateDifferenceDistance(viewDetailPair));
                double inverseSumDistance = 1 / sumDistance;
                double alfa = Math.abs(calculateDifferenceAngel(viewDetailPair));
                double grade = ((sumDistance * alfa) / c) + (c * (inverseSumDistance * beta));
                PositionGrade pg = new PositionGrade(evaluatedPosition, grade);
                evaluatedDistanceAndPositions.add(pg);
            }
        }
        return evaluatedDistanceAndPositions;
    }


    private class PositionGrade{
        Position position;
        double grade;
        double weight;
        private PositionGrade(Position position, double grade){
           this.position= position;
           this.grade = grade;
        }


    }
    private double calculateAverageDistance(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        double avg = 0;
        int counter = 0;
        if(viewDetailPair.getKey() != null)
        {
            avg += viewDetailPair.getKey().distanceToOtherAgent;
            counter ++;
        }
        if(viewDetailPair.getValue() != null)
        {
            avg += viewDetailPair.getValue().distanceToOtherAgent;
            counter++;
        }
        return avg/counter;
    }
    private double calculateSumDistance(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        double sum = 0;

        if(viewDetailPair.getKey() != null && viewDetailPair.getValue()==null)
            sum = viewDetailPair.getKey().distanceToOtherAgent*2;
        else if(viewDetailPair.getKey() == null && viewDetailPair.getValue()!=null)
            sum = viewDetailPair.getValue().distanceToOtherAgent*2;
        else
            sum= viewDetailPair.getKey().distanceToOtherAgent + viewDetailPair.getValue().distanceToOtherAgent;
        return sum;
    }
    private double calculateDifferenceDistance(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){

        if(viewDetailPair.getKey() != null && viewDetailPair.getValue()==null)
            return viewDetailPair.getKey().deviation/100*viewDetailPair.getKey().distanceToOtherAgent*2;
        else if(viewDetailPair.getKey() == null && viewDetailPair.getValue()!=null)
            return viewDetailPair.getValue().deviation/100*viewDetailPair.getValue().distanceToOtherAgent*2;
        else
        return viewDetailPair.getKey().distanceToOtherAgent - viewDetailPair.getValue().distanceToOtherAgent;
    }
    private double calculateDifferenceAngel(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){

        if(viewDetailPair.getKey() != null && viewDetailPair.getValue()==null)
            return viewDetailPair.getKey().deviation*2;
        else if(viewDetailPair.getKey() == null && viewDetailPair.getValue()!=null)
            return viewDetailPair.getValue().deviation*2;
        else
            return viewDetailPair.getKey().azimuthToOtherAgent - Util.set0to359(viewDetailPair.getValue().azimuthToOtherAgent + 180);
    }

    private double calculateAverageAngel(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
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
}
