package simulator;

import view.Position;

/**
 * Created by נועם on 10/24/2017.
 */
public class AgentViewDetails {
    public AgentViewDetails(Position myPosition, double myHeading, double azimuthToOtherAgent, double distanceToOtherAgent)
    {
        this.myPosition = myPosition;
        this.myHeading = myHeading;
        this.azimuthToOtherAgent = azimuthToOtherAgent;
        this.distanceToOtherAgent = distanceToOtherAgent;
    }

    public Position myPosition;
    public double myHeading;
    public double azimuthToOtherAgent;
    public double distanceToOtherAgent;
}
