package simulator;

import java.util.ArrayList;
import static java.lang.System.out;
import org.eclipse.swt.events.PaintEvent;
import simulator.DetectionSensor.Detection;

public class DefensingAgent extends Agent {

	DetectionSensor detectionSensor;
	public boolean myTurnToMove;
	public boolean dangerDetected;
	public AgentType myType;

	protected double rotationSpeed;
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
			if (myType == AgentType.guarding) {
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
				double maxAzimuth = this.heading + ((detectionSensor.span / 2) - 3);
				double minAzimuth = 360 - this.heading - ((detectionSensor.span / 2) - 3);
				for (Detection detection : allDetections) {
					if (detection.azimuth >= maxAzimuth || detection.azimuth <= minAzimuth) {
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

	public void setRotationSpeed(double rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public void setDetectionRange(double range) {
		detectionSensor.range = range;
	}

	public void setDetectionDeviation(double deviation) {
		detectionSensor.deviation = deviation;
	}

	public void setRotationError(double rotationError) {
		this.rotationError = rotationError;
	}
}
