package simulator;

import java.util.ArrayList;
import static java.lang.System.out;
import org.eclipse.swt.events.PaintEvent;

import simulator.DetectionSensor.Detection;
import view.Position;

enum agentType {
	leading,
	guarding
}

public class DefensingAgent extends Agent {

	DetectionSensor detectionSensor;
	private boolean locked;
	private boolean firing;
	private boolean tracking;
	public boolean myTurnToMove;
	public boolean dangerDetected;
	public agentType myType;

	private Detection target;

	private int targetTon;
	protected double rotationSpeed;
	private boolean azimuthRiched;
	int searchIndex;
	int searchScans;
	private Detection detectedTarget;
	double rotationError;

	public DefensingAgent(double positionX, double positionY, int heading, DetectionSensor sensor) {
		super(positionX, positionY, heading);
		this.detectionSensor = sensor;
		addHeadingDependent(sensor);
	}

	@Override
	public void step() {
		if(myTurnToMove) {
			super.step();
		}
		else
		{
			if (myType == agentType.guarding) {
				ArrayList<Detection> allDetections = detect();
				for (Detection detection : allDetections) {
					if (detectionSensor.range - detection.range < 2) {
						out.println("danger");
						dangerDetected = true;
					}
				}
			}
			else
			{
				ArrayList<Detection> allDetections = detect();
				double maxAzimute = this.heading + ((detectionSensor.span / 2) - 3);
				double minAzimute = 360 - this.heading - ((detectionSensor.span / 2) - 3);
				for (Detection detection : allDetections) {
					if (detection.azimuth >= maxAzimute || detection.azimuth <= minAzimute) {
						out.println("danger");
						dangerDetected = true;
					}
				}
			}
		}
	}

	public void turnAround() {
		double oldHeading = this.heading;
		super.setHeading(180 - oldHeading);
		this.detectionSensor.heading = 180 - oldHeading;
	}

	@Override
	public void draw(PaintEvent e, int x, int y, int r) {
		super.draw(e, x, y, r);
		detectionSensor.draw(e, x, y, r);
	}

	public ArrayList<Detection> detect() {
		ArrayList<Detection> d = new ArrayList<DetectionSensor.Detection>();
		d.addAll(detectionSensor.detect(getPosition()));
		return d;
	}

	public void assign(Detection d, int ton) {
		target = d;
		targetTon = ton;
		tracking = true;
		//d.defensingAgent.setEngaged(true);
		azimuthRiched = false;
		searchIndex = 0;
		searchScans = 0;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setFiring(boolean b) {
		firing = b;
	}

	public boolean isFiring() {
		return firing;
	}

	public void stopTracking() {
		firing = false;
		locked = false;
		tracking = false;
		//target.foeAgent.setEngaged(false);
		azimuthRiched = false;
	}

//	public void track() {
//		if (!target.foeAgent.isStopped() && (firing || target.foeAgent.getThreatNumber() <= targetTon)) {
//
//			double az = target.azimuth + rotationError;
//			if (locked)
//				az = Util.getAzimuth(p.x, p.y, target.foeAgent.p.x, target.foeAgent.p.y);
//
//			double diff = Util.getDegDiff(heading, az);
//			if (Math.abs(diff) < 0.1)
//				azimuthRiched = true;
//
//			if (!azimuthRiched || locked) {
//				double ch = Math.min(rotationSpeed, Math.abs(diff));
//				if (diff < 0)
//					ch = -ch;
//				if (ch != 0)
//					setHeading(heading + ch);
//			}
//
//			if (azimuthRiched) {
//				locked = false;
//				ArrayList<Detection> ds = sensors.get(0).detect(p);
//				if (ds != null && ds.size() > 0) {
//					for (Detection d : ds)
//						if (d.foeAgent == target.foeAgent) {
//							locked = true;
//							detectedTarget = d;
//						}
//				}
//
//				// search
//				if (!locked)
//					search(az);
//			}
//
//		} else {
//			if (target.foeAgent.isStopped() || (!firing && target.foeAgent.getThreatNumber() > targetTon))
//				stopTracking();
//		}
//
//	}

//	private void search(double az) {
//		double s[] = { 0, 0.5, 1, 1.5, 2, 1.5, 1, 0.5, 0, -0.5, -1, -1.5, -2, -1.5, -1, -0.5 };
//		setHeading(az + s[searchIndex]);
//		searchIndex++;
//		if (searchIndex == s.length) {
//			searchIndex = 0;
//			searchScans++;
//		}
//
//		if (searchScans == 3) {
//			stopTracking();
//		}
//	}

	public boolean isTracking() {
		return tracking;
	}

	public void setTracking(boolean tracking) {
		this.tracking = tracking;
	}

	public void setRotationSpeed(double rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public void setDetectionRange(double range) {
		detectionSensor.range = range;
	}

	public void setDetectionDeviation(double deviation) {
		detectionSensor.deviation = deviation;
	}

	public double getTarckedRange() {
		if (detectedTarget != null)
			return detectedTarget.range;
		return -1;
	}

	public double getTrackedAzimuth() {
		if (detectedTarget != null)
			return detectedTarget.azimuth;
		return -1;
	}

	public Position getTargetPosition() {
		if (detectedTarget != null) {
			double x = p.x + detectedTarget.range * Math.cos(Math.toRadians(detectedTarget.azimuth - 90));
			double y = p.y + detectedTarget.range * Math.sin(Math.toRadians(detectedTarget.azimuth - 90));
			return new Position(x, y);
		}
		return null;
	}

	public void setRotationError(double rotationError) {
		this.rotationError = rotationError;
	}

}
