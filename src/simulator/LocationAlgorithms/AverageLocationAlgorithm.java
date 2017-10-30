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
            double distanceAvg = this.CalculateAverageDistance(viewDetailPair);
            double angelAvg = CalculateAverageAngel(viewDetailPair);

            Position evaluatedPosition;
            if (viewDetailPair.getKey() != null) {
                evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getKey().myPosition, angelAvg, distanceAvg);
            }
            else{
                evaluatedPosition = Util.calulatePositionByAzimuthAndDistance(viewDetailPair.getValue().myPosition, angelAvg, distanceAvg);
            }
            evaluatedPositions.add(evaluatedPosition);
        }
        return evaluatedPositions;
    }


    private double CalculateAverageDistance(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
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

    private double CalculateAverageAngel(Pair<AgentViewDetails, AgentViewDetails> viewDetailPair){
        double avgAngel = 0;
        if (viewDetailPair.getValue() != null && viewDetailPair.getKey() != null) {
            avgAngel += Util.getDegDiff(viewDetailPair.getKey().azimuthToOtherAgent, viewDetailPair.getKey().myHeading);
            avgAngel += Util.set0to359(Util.getDegDiff(viewDetailPair.getValue().azimuthToOtherAgent, viewDetailPair.getValue().myHeading) + 180);
            avgAngel = avgAngel/2;
        }
        else if(viewDetailPair.getValue() != null)
        {
            avgAngel = Util.set0to359(Util.getDegDiff(viewDetailPair.getValue().azimuthToOtherAgent, viewDetailPair.getValue().myHeading));
        }
        else if(viewDetailPair.getKey() != null)
        {
            avgAngel = Util.set0to359(Util.getDegDiff(viewDetailPair.getKey().azimuthToOtherAgent, viewDetailPair.getKey().myHeading));
        }
        return avgAngel;
    }
}
