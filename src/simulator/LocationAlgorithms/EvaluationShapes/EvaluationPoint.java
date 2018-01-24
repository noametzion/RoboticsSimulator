package simulator.LocationAlgorithms.EvaluationShapes;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import simulator.Agent;
import simulator.DefensingAgent;
import utils.Util;
import view.Position;

import java.util.ArrayList;


/**
 * Created by נועם on 12/14/2017.
 */
public class EvaluationPoint extends EvaluationShape {
    public Position center;
    public Position featurePosition;
    public double featureAzimuthToCenter;
    public double featureRangeToCenter;
    public int featureAgentNumber;

    // Feature = other agent, center = evaluated agent
    public EvaluationPoint(int featureAgentNumber, Position featurePosition, double featureAzimuthToCenter, double featureRangeToCenter){

        this.featureAgentNumber = featureAgentNumber;
        this.featurePosition = featurePosition;
        this.featureAzimuthToCenter = featureAzimuthToCenter;
        this.featureRangeToCenter = featureRangeToCenter;

        initEvaluationPoints();
    }

    private void initEvaluationPoints(){
        this.center = Util.calulatePositionByAzimuthAndDistance(featurePosition, featureAzimuthToCenter, featureRangeToCenter);
    }

    public void recalculateEvaluationPointsWithError(double rangeError, double azimuthError){
        double newFeatureRangeToCenter = featureRangeToCenter + rangeError;
        double newFeatureAzimuthToCenter = Util.set0to359(featureAzimuthToCenter + azimuthError);
        this.center = Util.calulatePositionByAzimuthAndDistance(featurePosition, newFeatureAzimuthToCenter, newFeatureRangeToCenter);
    }

    @Override
    public Position getPosition() {
        return null;
    }

    @Override
    public void draw(PaintEvent e, int x, int y, int r, int x0, int y0) {

        int cx = (int)Math.round(x0+center.x*r);
        int cy = (int)Math.round(y0+center.y*r);

        e.gc.setForeground(new Color(null, 100, 0, 150));
        e.gc.drawOval(cx-(r)/2, cy-(r)/2, r , r);
    }

    @Override
    public ArrayList<Position> GetEvaluationShapePositionsByOrder() {
        ArrayList<Position> shape = new ArrayList<>();
        shape.add(center);
        return  shape;
    }
}