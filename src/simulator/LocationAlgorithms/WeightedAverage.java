package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.AgentViewDetails;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WeightedAverage implements ILocationAlgorithm {

    @Override
    public Position CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {

        List<DistancePosition> evaluatedDistancePositions = GetEvaluatedDistanceAndPositionsFromAllAgents(viewDetailPairs);

        double sumDistance = 0;
        double sumX = 0;
        double sumY = 0;

        for (DistancePosition dp:evaluatedDistancePositions) {
            sumDistance += dp.distance;
        }
        for (DistancePosition dp:evaluatedDistancePositions){
            sumX += dp.distance/sumDistance * dp.position.x;
            sumY += dp.distance/sumDistance * dp.position.y;
        }

        Position finalEvaluatedPosition = new Position(sumX, sumY);

        return finalEvaluatedPosition;
    }
    private List<DistancePosition> GetEvaluatedDistanceAndPositionsFromAllAgents(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs){
        List<DistancePosition> evaluatedDistanceAndPositions = new ArrayList<>();
        for (Pair<AgentViewDetails, AgentViewDetails> viewDetailPair:viewDetailPairs) {
            double distanceAvg = calculateAverageDistance(viewDetailPair);
            double angelAvg = calculateAverageAngel(viewDetailPair);
            Position evaluatedPosition;
            if (viewDetailPair.getKey() != null) {
                evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getKey().myPosition, angelAvg, distanceAvg);
            }
            else{
                evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getValue().myPosition, angelAvg, distanceAvg);
            }
            DistancePosition dp = new DistancePosition(evaluatedPosition,distanceAvg);
            evaluatedDistanceAndPositions.add(dp);
        }
        return evaluatedDistanceAndPositions;
    }
    private class DistancePosition{
        public Position position;
        public double distance;

        private DistancePosition(Position p, double distance){
            this.position = p;
            this.distance=distance;
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
