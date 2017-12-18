package simulator.LocationAlgorithms.EvaluationShapes;

import utils.Util;
import view.Position;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.events.PaintEvent;
import view.SimDrawable;


/**
 * Created by נועם on 12/14/2017.
 */
public class EvaluationArcher extends EvaluationShape {
    public Position center;
    public double rangeError;
    public double spanError;
    public Position topPoint;
    public Position bottomPoint;
    public Position leftPoint;
    public Position rightPoint;

    public Position featurePosition;
    public double featureAzimuthToCenter;

    public EvaluationArcher(Position featurePosition, double featureAzimuthToCenter, double featureRangeToCenter) {
        this.featurePosition = featurePosition;
        this.featureAzimuthToCenter = featureAzimuthToCenter;
        this.featureRangeToCenter = featureRangeToCenter;

        this.center = Util.calulatePositionByAzimuthAndDistance(featurePosition, featureAzimuthToCenter, featureRangeToCenter);
        //evaluatedOval.topPoint = Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, pair.getKey().azimuthToOtherAgent, pair.getKey().distanceToOtherAgent + evaluatedOval.rangeError);
        //evaluatedOval.bottomPoint =Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, pair.getKey().azimuthToOtherAgent, pair.getKey().distanceToOtherAgent - evaluatedOval.rangeError);
        //evaluatedOval.rightPoint = Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, Util.set0to359(pair.getKey().azimuthToOtherAgent + evaluatedOval.spanError), pair.getKey().distanceToOtherAgent);
        //evaluatedOval.leftPoint = Util.calulatePositionByAzimuthAndDistance(pair.getKey().myPosition, Util.set0to359(pair.getKey().azimuthToOtherAgent - evaluatedOval.spanError), pair.getKey().distanceToOtherAgent);
    }

    public double featureRangeToCenter;

    @Override
    public Position getPosition() {
        return featurePosition;
    }

    @Override
    public void draw(PaintEvent e, int x, int y, int r, int x0, int y0) {


        int x00 = x0 -(x0/r)*r;
        int y00 =y0 -(y0/r)*r;

        double cx= this.getPosition().x;
        double cy= this.getPosition().y;
        int xOfFeature = (int)Math.round(x00+cx*r);
        int yOfFeature = (int)Math.round(y00+cy*r);

        //, (int)Math.round(x0+cx*zoom), (int)Math.round(y0+cy*zoom),

                // TODO: implement
//        int x1=(int)Math.round(x+ sensorRange *r*Math.cos(Math.toRadians(heading-90- sensorSpan /2)));
//        int y1=(int)Math.round(y+ sensorRange *r*Math.sin(Math.toRadians(heading-90- sensorSpan /2)));
//
//        int x2=(int)Math.round(x+ sensorRange *r*Math.cos(Math.toRadians(heading-90+ sensorSpan /2)));
//        int y2=(int)Math.round(y+ sensorRange *r*Math.sin(Math.toRadians(heading-90+ sensorSpan /2)));
//
//        e.gc.setForeground(new Color(null, 200,0,0));
//        e.gc.drawLine(x, y, x1, y1);
//        e.gc.drawLine(x, y, x2, y2);
//        double rr= sensorRange *r;
//        e.gc.drawArc((int)Math.round(x-rr), (int)Math.round(y-rr), (int)Math.round(rr*2),(int)Math.round( rr*2), (int)Math.round(180-heading-90- sensorSpan /2), (int)Math.round(sensorSpan));

        //int centerX=(int)Math.round(center.x);
        //int centerY=(int)Math.round(center.y);


        e.gc.setForeground(new Color(null, 0, 255, 0));
        e.gc.drawOval(xOfFeature-(r)/2, yOfFeature-(r)/2, r, r);
        int x1=(int)Math.round(xOfFeature+(r/3) * Math.cos(Math.toRadians(90)));
        int y1=(int)Math.round(yOfFeature+(r/3) * Math.sin(Math.toRadians(90)));
        e.gc.drawLine(xOfFeature, yOfFeature, x1, y1);



       // e.gc.setForeground(new Color(null, 0, 255, 0));
       // e.gc.drawPoint(xOfFeature, yOfFeature);
    }
}