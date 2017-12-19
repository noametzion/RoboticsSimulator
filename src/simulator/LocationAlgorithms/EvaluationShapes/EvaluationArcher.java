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
    public Position featurePosition;
    public double featureAzimuthToCenter;
    public double featureRangeToCenter;

    public double rangeError;
    public double spanError;

    public Position topPoint;
    public Position topRightPoint;
    public Position rightPoint;
    public Position bottomRightPoint;
    public Position bottomPoint;
    public Position bottomLeftPoint;
    public Position leftPoint;
    public Position topLeftPoint;

    // TODO: implement with is opposite default param = false;
    public EvaluationArcher(Position featurePosition, double featureAzimuthToCenter, double featureRangeToCenter, double rangeError, double spanError) {
        this.featurePosition = featurePosition;
        this.featureAzimuthToCenter = featureAzimuthToCenter;
        this.featureRangeToCenter = featureRangeToCenter;
        this.rangeError = rangeError;
        this.spanError = spanError;

        initEvaluationPoints();
    }

    private void initEvaluationPoints(){

        this.center = Util.calulatePositionByAzimuthAndDistance(featurePosition, featureAzimuthToCenter, featureRangeToCenter);

        this.topPoint = Util.calulatePositionByAzimuthAndDistance(featurePosition, featureAzimuthToCenter, featureRangeToCenter + rangeError);
        this.bottomPoint = Util.calulatePositionByAzimuthAndDistance(featurePosition, featureAzimuthToCenter, featureRangeToCenter - rangeError);
        this.rightPoint = Util.calulatePositionByAzimuthAndDistance(featurePosition, Util.set0to359(featureAzimuthToCenter + spanError), featureRangeToCenter);
        this.leftPoint = Util.calulatePositionByAzimuthAndDistance(featurePosition, Util.set0to359(featureAzimuthToCenter - spanError), featureRangeToCenter);
        this.topLeftPoint = Util.calulatePositionByAzimuthAndDistance(featurePosition, Util.set0to359(featureAzimuthToCenter - spanError), featureRangeToCenter + rangeError);
        this.bottomLeftPoint = Util.calulatePositionByAzimuthAndDistance(featurePosition, Util.set0to359(featureAzimuthToCenter - spanError), featureRangeToCenter - rangeError);
        this.topRightPoint = Util.calulatePositionByAzimuthAndDistance(featurePosition, Util.set0to359(featureAzimuthToCenter + spanError), featureRangeToCenter + rangeError);
        this.bottomRightPoint = Util.calulatePositionByAzimuthAndDistance(featurePosition, Util.set0to359(featureAzimuthToCenter + spanError), featureRangeToCenter - rangeError);
    }

    @Override
    public Position getPosition() {
        return null;
    }

    @Override
    public void draw(PaintEvent e, int x, int y, int r, int x0, int y0) {

        int fx = (int)Math.round(x0+featurePosition.x*r);
        int fy = (int)Math.round(y0+featurePosition.y*r);

        int cx = (int)Math.round(x0+center.x*r);
        int cy = (int)Math.round(y0+center.y*r);

        int tpx = (int)Math.round(x0+topPoint.x*r);
        int tpy = (int)Math.round(y0+topPoint.y*r);

        int trpx = (int)Math.round(x0+topRightPoint.x*r);
        int trpy = (int)Math.round(y0+topRightPoint.y*r);

        int rpx = (int)Math.round(x0+rightPoint.x*r);
        int rpy = (int)Math.round(y0+rightPoint.y*r);

        int brpx = (int)Math.round(x0+bottomRightPoint.x*r);
        int brpy = (int)Math.round(y0+bottomRightPoint.y*r);

        int bpx = (int)Math.round(x0+bottomPoint.x*r);
        int bpy = (int)Math.round(y0+bottomPoint.y*r);

        int blpx = (int)Math.round(x0+bottomLeftPoint.x*r);
        int blpy = (int)Math.round(y0+bottomLeftPoint.y*r);

        int lpx = (int)Math.round(x0+leftPoint.x*r);
        int lpy = (int)Math.round(y0+leftPoint.y*r);

        int tlpx = (int)Math.round(x0+topLeftPoint.x*r);
        int tlpy = (int)Math.round(y0+topLeftPoint.y*r);

        e.gc.setForeground(new Color(null, 0, 150, 150));
        e.gc.drawOval(cx-(r)/2, cy-(r)/2, r , r);

        e.gc.setForeground(new Color(null, 0, 255, 0));
        //e.gc.drawOval(cx-(r)/2, cy-(r)/2, r , r);
        e.gc.drawLine(tpx, tpy, trpx, trpy);
        e.gc.drawLine(trpx, trpy, rpx, rpy);
        e.gc.drawLine(rpx, rpy, brpx, brpy);
        e.gc.drawLine(brpx, brpy, bpx, bpy);
        e.gc.drawLine(bpx, bpy, blpx, blpy);
        e.gc.drawLine(blpx, blpy, lpx, lpy);
        e.gc.drawLine(lpx, lpy, tlpx, tlpy);
        e.gc.drawLine(tlpx, tlpy, tpx, tpy);

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






       // e.gc.setForeground(new Color(null, 0, 255, 0));
       // e.gc.drawPoint(xOfFeature, yOfFeature);
    }
}