package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.AgentViewDetails;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

public class AngleDistanceDeviationAlgorithem implements ILocationAlgorithm {
    public double angleDeviation;
    public AngleDistanceDeviationAlgorithem(double angleDeviation){
        this.angleDeviation=angleDeviation;
    }
    @Override
    public Position CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {
        List<PositionGrade> evaluatedPositions = GetEvaluatedDistanceAndPositionsFromAllAgents(viewDetailPairs);

        double sumGrade = 0;
        double sumX = 0;
        double sumY = 0;

        for (PositionGrade pg:evaluatedPositions) {
            sumGrade += pg.grade;
        }
        for (PositionGrade pg:evaluatedPositions){
            sumX += pg.grade/sumGrade * pg.position.x;
            sumY += pg.grade/sumGrade * pg.position.y;
        }

        Position finalEvaluatedPosition = new Position(sumX, sumY);

        return finalEvaluatedPosition;
    }

    private List<PositionGrade> GetEvaluatedDistanceAndPositionsFromAllAgents(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs){
        List<PositionGrade> evaluatedDistanceAndPositions = new ArrayList<>();
        for (Pair<AgentViewDetails, AgentViewDetails> viewDetailPair:viewDetailPairs) {
            double distanceAvg = (viewDetailPair.getKey().distanceToOtherAgent + viewDetailPair.getValue().distanceToOtherAgent) / 2;
            double angel1 = Util.getDegDiff(viewDetailPair.getKey().azimuthToOtherAgent, viewDetailPair.getKey().myHeading);
            double angel2 = Util.set0to359(Util.getDegDiff(viewDetailPair.getValue().azimuthToOtherAgent, viewDetailPair.getKey().myHeading) + 180);
            double angelAvg = (angel1 + angel2 )/ 2;
            Position evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getKey().myPosition,angelAvg,distanceAvg);

            double sumDistance = viewDetailPair.getKey().distanceToOtherAgent + viewDetailPair.getValue().distanceToOtherAgent;
            double beta = Math.abs(viewDetailPair.getKey().distanceToOtherAgent - viewDetailPair.getValue().distanceToOtherAgent);
            double inverseSumDistance = 1 / sumDistance;
            double grade = (sumDistance * angleDeviation) + (inverseSumDistance * beta);
            PositionGrade pg = new PositionGrade(evaluatedPosition,grade);
            evaluatedDistanceAndPositions.add(pg);
        }
        return evaluatedDistanceAndPositions;
    }


    private class PositionGrade{
        Position position;
        double grade;
        private PositionGrade(Position position, double grade){
           this.position= position;
           this.grade = grade;
        }


    }
}
