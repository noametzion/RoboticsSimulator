package simulator;

import java.util.ArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import simulator.Agent.HeadingDependent;
import utils.Util;
import view.Drawables;
import view.Position;
import view.SimDrawable;
import java.util.Random;

public class DetectionSensor implements HeadingDependent {

	public class Detection {
		public int agentSerialNumber;
		public Position position;
		public double heading;
		public double speed;
		public double range;
		public double azimuth;
		public int errorPercent;
		public DefensingAgent defensingAgent;

		public Detection(int errorPercent) {
			this.errorPercent = errorPercent;
		}

		public double azimuthWithError() {

			return azimuth + Util.GetRandomAzimuthError(this.errorPercent);
		}

		public double rangeWithError() {
			return range * Util.GetRandomError(errorPercent);
		}



}

	double heading;	// degrees
	public double sensorRange;
	double minRange;
	public double sensorSpan;		// degrees
	int offset;
	double deviation;
	int errorPercent;

	public DetectionSensor(int headingOffset,double range, double span,double minRange, int errorPercent) {
		this.offset=headingOffset;
		this.sensorRange =range;
		this.sensorSpan =span;
		this.minRange=minRange;
		this.errorPercent=errorPercent;

	}



	public void draw(PaintEvent e, int x, int y, int r){
		if(sensorSpan <360){
			int x1=(int)Math.round(x+ sensorRange *r*Math.cos(Math.toRadians(heading-90- sensorSpan /2)));
			int y1=(int)Math.round(y+ sensorRange *r*Math.sin(Math.toRadians(heading-90- sensorSpan /2)));

			int x2=(int)Math.round(x+ sensorRange *r*Math.cos(Math.toRadians(heading-90+ sensorSpan /2)));
			int y2=(int)Math.round(y+ sensorRange *r*Math.sin(Math.toRadians(heading-90+ sensorSpan /2)));

			e.gc.setForeground(new Color(null, 200,0,0));
			e.gc.drawLine(x, y, x1, y1);
			e.gc.drawLine(x, y, x2, y2);
			double rr= sensorRange *r;
			e.gc.drawArc((int)Math.round(x-rr), (int)Math.round(y-rr), (int)Math.round(rr*2),(int)Math.round( rr*2), (int)Math.round(180-heading-90- sensorSpan /2), (int)Math.round(sensorSpan));
		} else{
			e.gc.setForeground(new Color(null, 200,0,0));
			e.gc.drawOval((int)Math.round(x- sensorRange *r), (int)Math.round(y- sensorRange *r), (int)Math.round(sensorRange *r*2), (int)Math.round(sensorRange *r*2));
		}
	}



	public ArrayList<Detection> detect(Position p){
		ArrayList<Detection> d=new ArrayList<DetectionSensor.Detection>();
		for(SimDrawable sd : Drawables.drawables){
			Position fp=sd.getPosition();
			double r=Math.sqrt((p.x-fp.x)*(p.x-fp.x) + (p.y-fp.y)*(p.y-fp.y));
			if(r>minRange && r<= sensorRange){
				double azimuth=Util.getAzimuth(p.x, p.y, fp.x,fp.y);
				if(Util.isInSector(azimuth,heading, sensorSpan)){
					if(sd instanceof DefensingAgent){
						Detection g=new Detection(this.errorPercent);
						g.position=sd.getPosition();
						g.azimuth=azimuth;
						g.range=r;
						DefensingAgent a=(DefensingAgent)sd;
						g.speed=a.getSpeed();
						g.heading=a.getHeading();
						g.defensingAgent=a;
						g.agentSerialNumber = ((DefensingAgent) sd).getSerialNumber();
						d.add(g);
					}
				}
			}
		}
		return d;
	}



	@Override
	public void updateHeading(double heading) {
		this.heading=heading+offset;
	}

}