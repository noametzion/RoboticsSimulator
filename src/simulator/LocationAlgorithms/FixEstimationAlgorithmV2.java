package simulator.LocationAlgorithms;

import simulator.AgentViewDetails;
import utils.Util;
import view.Position;

public class FixEstimationAlgorithmV2 extends FixEstimationAlgorithm {
    @Override
    protected Position fixMovingPosition(double azimuthStandingPosToEstimation, AgentViewDetails agv , double azimuthDifference, double distanceDifference, double fixingPercentage){
        double fixedDistance;
        double fixedAzimuth;
        if(azimuthStandingPosToEstimation>0 && azimuthStandingPosToEstimation<=90){
            fixedAzimuth= agv.azimuthToOtherAgent- azimuthDifference;
            fixedDistance= agv.distanceToOtherAgent- distanceDifference;

        }
        else if(azimuthStandingPosToEstimation>90 && azimuthStandingPosToEstimation<=180){
            fixedAzimuth= agv.azimuthToOtherAgent-azimuthDifference;
            fixedDistance= agv.distanceToOtherAgent;
        }
        else if(azimuthStandingPosToEstimation>180 && azimuthStandingPosToEstimation<=270){
            fixedAzimuth= agv.azimuthToOtherAgent;
            fixedDistance= agv.distanceToOtherAgent;
        }
        else {
            fixedAzimuth= agv.azimuthToOtherAgent;
            fixedDistance= agv.distanceToOtherAgent-distanceDifference;
        }

        Position pos= Util.calulatePositionByAzimuthAndDistance(agv.myPosition,fixedAzimuth,fixedDistance);


        return pos;



    }
}
