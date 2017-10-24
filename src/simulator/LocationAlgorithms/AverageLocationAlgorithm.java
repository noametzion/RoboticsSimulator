package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.AgentViewDetails;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by נועם on 10/24/2017.
 */
public class AverageLocationAlgorithm implements ILocationAlgorithm {
    @Override
    public Position CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {

        List<Position> evaluatedPositions = GetEvaluatedPositionsFromAllAgents(viewDetailPairs);

        double sumX = 0;
        double sumY = 0;

        for (Position pos:evaluatedPositions) {
            sumX += pos.x;
            sumY += pos.y;
        }

        Position finalEvaluatedPosition = new Position(sumX / evaluatedPositions.size(), sumY / evaluatedPositions.size());

        return finalEvaluatedPosition;
    }

    private List<Position> GetEvaluatedPositionsFromAllAgents(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs){
        List<Position> evaluatedPositions = new ArrayList<>();
        for (Pair<AgentViewDetails, AgentViewDetails> viewDetailPair:viewDetailPairs) {
            double distanceAvg = (viewDetailPair.getKey().distanceToOtherAgent + viewDetailPair.getValue().distanceToOtherAgent) / 2;
            double angel1 = Util.getDegDiff(viewDetailPair.getKey().azimuthToOtherAgent, viewDetailPair.getKey().myHeading);
            double angel2 = Util.set0to359(Util.getDegDiff(viewDetailPair.getValue().azimuthToOtherAgent, viewDetailPair.getKey().myHeading) + 180);
            double angelAvg = (angel1 + angel2 )/ 2;
            Position evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getKey().myPosition,angelAvg,distanceAvg);
            evaluatedPositions.add(evaluatedPosition);
        }
        return evaluatedPositions;
    }

}
