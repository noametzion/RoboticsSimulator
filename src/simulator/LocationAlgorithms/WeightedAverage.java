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
            double distanceAvg = (viewDetailPair.getKey().distanceToOtherAgent + viewDetailPair.getValue().distanceToOtherAgent) / 2;
            double angel1 = Util.getDegDiff(viewDetailPair.getKey().azimuthToOtherAgent, viewDetailPair.getKey().myHeading);
            double angel2 = Util.set0to359(Util.getDegDiff(viewDetailPair.getValue().azimuthToOtherAgent, viewDetailPair.getKey().myHeading) + 180);
            double angelAvg = (angel1 + angel2 )/ 2;
            Position evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getKey().myPosition,angelAvg,distanceAvg);
            DistancePosition dp = new DistancePosition(evaluatedPosition,distanceAvg);
            evaluatedDistanceAndPositions.add(dp);
        }
        return evaluatedDistanceAndPositions;
    }
    private class DistancePosition{
        Position position;
        double distance;

        private DistancePosition(Position p, double distance){
            this.position = position;
            this.distance=distance;
        }

    }
}
