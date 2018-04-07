package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.AgentViewDetails;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

public class FixEstimationAlgorithm implements ILocationAlgorithm {
    @Override
    public EvaluatedLocationResult CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {
        List<Position> list=getEvaluatedPositionsFromAllPairs(viewDetailPairs);
        double averageX=0;
        double averageY=0;

        for (Position pos: list){
            averageX+=pos.x;
            averageY+=pos.y;
        }
        averageX/= list.size();
        averageY/= list.size();

        EvaluatedLocationResult elr= new EvaluatedLocationResult();
        elr.position= new Position(averageX,averageY);
        return elr;
    }

    protected List<Position> getEvaluatedPositionsFromAllPairs(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs){
        List<Position> list = new ArrayList<>();
        for (Pair<AgentViewDetails, AgentViewDetails> pair:viewDetailPairs) {
            if(pair.getKey()!=null && pair.getValue()!= null && pair.getValue().myTurnToMove){
                Position moverEstimation= Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition,pair.getKey().azimuthToOtherAgent,pair.getKey().distanceToOtherAgent);
                Position standingEstimation = Util.calulatePositionByAzimuthAndDistance(moverEstimation,pair.getValue().azimuthToOtherAgent,pair.getValue() .distanceToOtherAgent);
                double azimuthDifference = Math.abs(Util.set0to359(pair.getValue().azimuthToOtherAgent + 180)- pair.getKey().azimuthToOtherAgent);
                double distanceDifference = Math.abs(pair.getKey().distanceToOtherAgent - pair.getValue().distanceToOtherAgent);
                double azimuthStandingPosToEstimation= azimuthFromStandingPosToStandingEstimation(pair,standingEstimation);
                //set the fixing to 60%-40%
                Position fixedMoverEstimationPos= fixMovingPosition(azimuthStandingPosToEstimation,pair.getKey(),azimuthDifference,distanceDifference,0.6);
                list.add(fixedMoverEstimationPos);
            }
        }
        if(list.size()==0){
            for (Pair<AgentViewDetails, AgentViewDetails> pair:viewDetailPairs){
               Position position;
                if(pair.getKey()!=null){
                   position= Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition,pair.getKey().azimuthToOtherAgent,pair.getKey().distanceToOtherAgent);
               }
               else
                   position= Util.calulatePositionByAzimuthAndDistance(pair.getValue().otherPosition,Util.set0to359(pair.getValue().azimuthToOtherAgent+180),pair.getValue().distanceToOtherAgent);
                list.add(position);
            }

        }
        return list;
    }

    @Override
    public void setCVariableInFunction(double c) {

    }

    @Override
    public void setMeasurementDeviation(double deviation) {

    }
    protected double azimuthFromStandingPosToStandingEstimation(Pair<AgentViewDetails, AgentViewDetails> pair,Position standingEstimation){
        double azimuthBeforeCorrection=Util.getAzimuth(pair.getKey().myPosition.x,pair.getKey().myPosition.y,standingEstimation.x, standingEstimation.y);
        double azimuthWithCorrectionToNorth= Util.set0to359(azimuthBeforeCorrection-pair.getKey().azimuthToOtherAgent);
        return azimuthWithCorrectionToNorth;
    }

    protected Position fixMovingPosition(double azimuthStandingPosToEstimation,AgentViewDetails agv , double azimuthDifference, double distanceDifference, double fixingPercentage){
            double fixedDistance;
            double fixedAzimuth;
               if(azimuthStandingPosToEstimation>0 && azimuthStandingPosToEstimation<=90){
                   fixedAzimuth= agv.azimuthToOtherAgent- fixingPercentage*azimuthDifference;
                   fixedDistance= agv.distanceToOtherAgent- fixingPercentage*distanceDifference;
               }
               else if(azimuthStandingPosToEstimation>90 && azimuthStandingPosToEstimation<=180){
                   fixedAzimuth= agv.azimuthToOtherAgent-fixingPercentage*azimuthDifference;
                   fixedDistance= agv.distanceToOtherAgent+ (1-fixingPercentage)*distanceDifference;
               }
               else if(azimuthStandingPosToEstimation>180 && azimuthStandingPosToEstimation<=270){
                   fixedAzimuth= agv.azimuthToOtherAgent+(1-fixingPercentage)*azimuthDifference;
                   fixedDistance= agv.distanceToOtherAgent+(1-fixingPercentage)*distanceDifference;
               }
               else {
                   fixedAzimuth= agv.azimuthToOtherAgent+(1-fixingPercentage)*azimuthDifference;
                   fixedDistance= agv.distanceToOtherAgent- fixingPercentage*distanceDifference;
               }

               Position pos= Util.calulatePositionByAzimuthAndDistance(agv.myPosition,fixedAzimuth,fixedDistance);


               return pos;



    }
}
