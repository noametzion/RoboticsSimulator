package utils;

import simulator.DetectionSensor.Detection;
import view.Drawables;
import view.Position;
import view.SimDrawable;

import java.util.Random;

public class Util {

	public static double getAzimuth(double x,double y,double gx,double gy){
		double azimuth=90+180/Math.PI * Math.atan2( gy-y,gx-x);
		if(azimuth<0)
			azimuth=360+azimuth;
		return azimuth;

	}

	public static double getRange(double x, double y, double gx, double gy) {
		return Math.sqrt((gx-x)*(gx-x)+(gy-y)*(gy-y));
	}

	public static double getTime(double minRange,Detection d){
		double av=d.speed*Math.cos(Math.toRadians(Util.getDegDiff(d.azimuth,d.heading)));
		if(av<0){
			return (d.range-minRange)/Math.abs(av);
		}
		else
			return Double.POSITIVE_INFINITY;
	}


	public static double set0to359(double deg){
		while (deg < 0 || deg >= 360) {
			if (deg < 0)
				deg += 360;
			if (deg >= 360)
				deg = deg % 360;
		}
		return deg;
	}
	public static boolean isInSector(double deg,double heading,double span) {
		double startDeg=set0to359(heading-span/2);
		double stopDeg=set0to359(heading+span/2);
		return  (deg>=startDeg && deg<=stopDeg)||
				(deg>=startDeg && deg<360 && stopDeg<startDeg)||
				(deg>=0 && deg<stopDeg && stopDeg<startDeg)||
				(startDeg==stopDeg && span>0);
	}

	public static double getDegDiff(double startDeg,double goalDeg){
		double d=goalDeg-startDeg;
		if(d>180)
			d=-(360-d);
		return d;
	}

	// Angel - > already with heading 180
//	public static Position calulatePositionByAzimuthAndDistance(Position posA, double angelAToB, double distanceAToB){
//		double xB =  posA.x + distanceAToB * Math.sin(Math.toRadians(angelAToB));
//		double yB =  posA.y + distanceAToB * Math.cos(Math.toRadians(angelAToB));
//		Position posB = new Position(xB, yB);
//		return posB;
//
//	}
	public static Position calulatePositionByAzimuthAndDistance(Position posA, double angelAToB, double distanceAToB){
		double xB=0;
		double yB=0;
		if(angelAToB<=90) {
			xB = posA.x + distanceAToB * Math.sin(Math.toRadians(angelAToB));
			yB = posA.y - distanceAToB * Math.cos(Math.toRadians(angelAToB));
		}
		else if(angelAToB>90 && angelAToB<=180){
			xB = posA.x + distanceAToB * Math.sin(Math.toRadians(180-angelAToB));
			yB = posA.y + distanceAToB * Math.cos(Math.toRadians(180-angelAToB));
		}
		else if(angelAToB>180 && angelAToB<=270){
			xB = posA.x - distanceAToB * Math.sin(Math.toRadians(angelAToB-180));
			yB = posA.y + distanceAToB * Math.cos(Math.toRadians(angelAToB-180));
		}
		else{
			xB = posA.x - distanceAToB * Math.sin(Math.toRadians(360-angelAToB));
			yB = posA.y - distanceAToB * Math.cos(Math.toRadians(360-angelAToB));
		}
		Position posB = new Position(xB, yB);
		return posB;

	}

	public static double GetRandomError(int errorPercent)
	{
		if (errorPercent != 0) {
			Random rand = new Random();
			int n = rand.nextInt(errorPercent * 2) + (100 - errorPercent);
			double error = n * 0.01;
			return error;
		}
		return 1;
	}

	public static boolean isIntersect(Position sensorPosition, double heading, double span, double range, Position detectionEvaluatedPosition){
		double minRange = 0;
		double r=Math.sqrt((sensorPosition.x-detectionEvaluatedPosition.x)*(sensorPosition.x-detectionEvaluatedPosition.x) +
				(sensorPosition.y-detectionEvaluatedPosition.y)*(sensorPosition.y-detectionEvaluatedPosition.y));
		if(r>minRange && r<= range){
			double azimuth=Util.getAzimuth(sensorPosition.x, sensorPosition.y, detectionEvaluatedPosition.x,detectionEvaluatedPosition.y);
			if(Util.isInSector(azimuth,heading, span)){
				return true;
			}
		}
		return false;
	}

	public static Position getStepForwardBufferedPosition(Position p, double heading, double buffer){
		double x1=p.x + buffer * Math.cos(Math.toRadians(heading-90));
		double y1=p.y + buffer * Math.sin(Math.toRadians(heading-90));
		return new Position(x1, y1);
	}

}
