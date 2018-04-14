package simulator.LocationAlgorithms.EvaluationShapes;

import IGradable.IGradable;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by נועם on 3/5/2018.
 */
public class EvaluationArcPolygon extends EvaluationShape implements IGradable {

    public Position center;
    //public Position realCenter;
    public Position featurePosition;
    public double featureAzimuthToCenter;
    public double featureRangeToCenter;

    public double rangeError;
    public double spanError;

    public Geometry evaluationPolygon;

    public List<Position> randomPositionsToDraw = new ArrayList<>();

    public EvaluationArcPolygon(Position center, Position featurePosition, double featureAzimuthToCenter, double featureRangeToCenter, double rangeError, double spanError) {
        //this.realCenter = center;
        this.center = Util.calulatePositionByAzimuthAndDistance(featurePosition, featureAzimuthToCenter, featureRangeToCenter);

        this.featurePosition = featurePosition;
        this.featureAzimuthToCenter = featureAzimuthToCenter;
        this.featureRangeToCenter = featureRangeToCenter;
        this.rangeError = rangeError;
        this.spanError = spanError;

        this.evaluationPolygon = this.createEvaluationArcPolygon(featurePosition, featureRangeToCenter, featureAzimuthToCenter);
    }

    @Override
    public ArrayList<Position> GetEvaluationShapePositionsByOrder() {
        return null;
    }

    @Override
    public Position getPosition() {
        return center;
    }

    public Geometry getEvaluationPolygon(){return evaluationPolygon;}

    @Override
    public void draw(PaintEvent e, int x, int y, int r, int x0, int y0) {
        //int cx = (int)Math.round(x0+center.x*r);
        //int cy = (int)Math.round(y0+center.y*r);

        int nPoints = this.evaluationPolygon.getCoordinates().length;
        int points[] = new int[nPoints * 2];

        for (int i = 0; i < nPoints; i++){
            points[2 * i] = (int) Math.round(x0 + r * this.evaluationPolygon.getCoordinates()[i].x);
            points[(2 * i) + 1] = (int) Math.round(y0 + r * this.evaluationPolygon.getCoordinates()[i].y);
        }

        e.gc.setForeground(new Color(null, 100, 0, 100));
        e.gc.drawPolygon(points);

        e.gc.setForeground(new Color(null, 0, 200, 0));
        for (Position p: randomPositionsToDraw) {
            e.gc.drawPoint((int)Math.round(x0 + r * p.x), (int)Math.round(y0 + r *p.y));
        }

        e.gc.setForeground(new Color(null, 200, 0, 200));
        e.gc.drawOval((int)Math.round(x0 + r * best.x), (int)Math.round(y0 + r *best.y), 5, 5);

        //int cx = (int)Math.round(x0+center.x*r);
        //int cy = (int)Math.round(y0+center.y*r);

        //int lx = (int)Math.round(x0+featurePosition.x*r);
        //int ly = (int)Math.round(y0+featurePosition.y*r);

        //e.gc.setForeground(new Color(null, 0, 100, 0));
        //e.gc.drawLine(cx, cy, lx, ly);
    }

    private Geometry createEvaluationArcPolygon(Position featurePositionToEvaluateFrom, double range, double span){
        double smallRadius = range - rangeError;
        double largeRadius = range + rangeError;
        double startAngleDeg = Util.set0to359(span - spanError);
        double angleSizeDeg = spanError * 2;
        double startAngleRad = Math.toRadians(startAngleDeg);
        double angleSizeRad = Math.toRadians(angleSizeDeg);

        Geometry largeCircle = this.CreateArcPolygon(featurePositionToEvaluateFrom, largeRadius, startAngleRad, angleSizeRad);
        Geometry smallCircle = this.CreateArcPolygon(featurePositionToEvaluateFrom, smallRadius, startAngleRad, angleSizeRad);

        Geometry difference = largeCircle.difference(smallCircle);

        return difference;
    }

    private Geometry CreateArcPolygon(Position featurePositionToEvaluateFrom, double radius, double startAngle, double angleSize){
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(32);
        shapeFactory.setCentre(new Coordinate(featurePositionToEvaluateFrom.x, featurePositionToEvaluateFrom.y));
        shapeFactory.setSize(radius * 2);
        shapeFactory.setRotation(Math.toRadians(-90));
        //shapeFactory.setRotation(Math.toRadians());
        Geometry arcPolygon = shapeFactory.createArcPolygon(startAngle,angleSize);
        return arcPolygon;
    }

    @Override
    public double getGrade(Position position) throws Exception {
        randomPositionsToDraw.add(position);

        double positionDistanceFromFeature = this.calculateDistanceFromFeature(position);
        double positionAngleFromFeature = this.calculateAngleFromFeature(position, positionDistanceFromFeature);

        double differenceDistance = Math.abs(featureRangeToCenter - positionDistanceFromFeature);
        double differenceAngle = this.calculateDifferenceBetweenAngles(featureAzimuthToCenter, positionAngleFromFeature);

        double angleGrade = this.getAngleGrade(differenceAngle);
        double distanceGrade = this.getDistanceGrade(differenceDistance);

        double finalGrade = angleGrade * distanceGrade;
        return finalGrade;
    }

    private double getDistanceGrade(double differenceDistance) {
        if (differenceDistance > rangeError){
        // out of polygon
            return 0;
        }
        double grade = 1 - ( differenceDistance / rangeError );
        return grade;
    }

    private double getAngleGrade(double differenceAngle) {
        if (differenceAngle > rangeError){
        //out of polygon{
            return 0;
        }
        double grade = 1 - ( differenceAngle / spanError );
        return grade;
    }

    private double calculateDifferenceBetweenAngles(double angle, double otherAngle) {
        double diff = Math.abs(angle - otherAngle);
        if (diff > 180)
        {
            diff = diff - 180;
        }
        return diff;
    }

    private double calculateDistanceFromFeature(Position position) {
        double d = Math.sqrt(Math.pow(featurePosition.x - position.x, 2) + Math.pow(featurePosition.y - position.y, 2));
        return d;
    }

    private double calculateAngleFromFeature(Position position, double distanceFromFeature) throws Exception{
        double angle;
        if((position.x > featurePosition.x) && (position.y > featurePosition.y)) {
            double sinA = Math.abs(position.y - featurePosition.y) / distanceFromFeature;
            angle = Math.toDegrees(Math.asin(sinA)) + 90;
        }
        else if ((position.x > featurePosition.x) && (position.y < featurePosition.y)){
            double sinA = Math.abs(position.x - featurePosition.x) / distanceFromFeature;
            angle = Math.toDegrees(Math.asin(sinA));
        }
        else if ((position.x <  featurePosition.x) && (position.y < featurePosition.y)){
            double sinA = Math.abs(position.y - featurePosition.y) / distanceFromFeature;
            angle = Math.toDegrees(Math.asin(sinA)) + 270;
        }
        else if ((position.x < featurePosition.x) && (position.y > featurePosition.y)){
            double sinA = Math.abs(position.x - featurePosition.x) / distanceFromFeature;
            angle = Math.toDegrees(Math.asin(sinA)) + 180;
        }
        else {
            throw new Exception("Bug!! not supposed to happen!!!");
        }
        return angle;
    }

    @Override
    public List<Position> GetRandomPositions(){
        List<Position> randomPositions = new ArrayList<>();
        // TODO: implement other option
        randomPositions.add(center);

        int scaleBy = 10;
        double rangeScale = rangeError / scaleBy;
        double spanScale = spanError / scaleBy;

        for (int i = 0; i < scaleBy; i ++ ) // span
        {
            for (int j = 0; j < scaleBy; j++) // range
            {
                Position positionToAdd1 = Util.calulatePositionByAzimuthAndDistance(featurePosition,
                        featureAzimuthToCenter + spanScale * i,
                        featureRangeToCenter + rangeScale * j);
                Position positionToAdd2 = Util.calulatePositionByAzimuthAndDistance(featurePosition,
                        featureAzimuthToCenter - spanScale * i,
                        featureRangeToCenter + rangeScale * j);
                Position positionToAdd3 = Util.calulatePositionByAzimuthAndDistance(featurePosition,
                        featureAzimuthToCenter + spanScale * i,
                        featureRangeToCenter - rangeScale * j);
                Position positionToAdd4 = Util.calulatePositionByAzimuthAndDistance(featurePosition,
                        featureAzimuthToCenter - spanScale * i,
                        featureRangeToCenter - rangeScale * j);
                randomPositions.add(positionToAdd1);
                randomPositions.add(positionToAdd2);
                randomPositions.add(positionToAdd3);
                randomPositions.add(positionToAdd4);
            }
        }
        return randomPositions;
    }

    private Position best;

    public void setBest(Position bestPosition){
        this.best =bestPosition;
    }

}
