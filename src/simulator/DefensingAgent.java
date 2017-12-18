package simulator;

import java.util.ArrayList;

import org.eclipse.swt.events.PaintEvent;
import simulator.DetectionSensor.Detection;
import utils.Util;

public class DefensingAgent extends Agent {

	public DetectionSensor detectionSensor;
	public boolean myTurnToMove;
	public boolean dangerDetected;
	public AgentType myType;

	protected double rotationSpeed;
	double rotationError;

	public DefensingAgent(double positionX, double positionY, int heading, DetectionSensor sensor, int movementErrorPercent) {
		super(positionX, positionY, heading, movementErrorPercent);
		this.detectionSensor = sensor;
		addHeadingDependent(sensor);
	}

	@Override
	public void step() {
		if(myTurnToMove) {
			super.step();
		}
	}

	public void turnAround() {
		double oldHeading = this.heading;
		super.setHeading(Util.set0to359(oldHeading + 180));
		this.detectionSensor.heading = Util.set0to359(super.getHeading());
		this.addLocationErrorWhenTurnAround();
	}

	private void addLocationErrorWhenTurnAround()
	{
		double x1=p.x+Util.GetRandomAdditionalError(movementErrorPercent);
		double y1=p.y+Util.GetRandomAdditionalError(movementErrorPercent);
		p.x=x1;
		p.y=y1;
	}

	@Override
	public void draw(PaintEvent e, int x, int y, int r, int x0, int y0) {
		super.draw(e, x, y, r, x0, y0);
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
		detectionSensor.sensorRange = range;
	}

	public void setDetectionDeviation(double deviation) {
		detectionSensor.deviation = deviation;
	}

	public void setRotationError(double rotationError) {
		this.rotationError = rotationError;
	}
}
