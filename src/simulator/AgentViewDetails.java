package simulator;

import view.Position;

/**
 * Created by נועם on 10/24/2017.
 */
public class AgentViewDetails {
    public AgentViewDetails(int agentNumberFrom, int getAgentNumberTo,Position myPosition, double myHeading, double azimuthToOtherAgent, double distanceToOtherAgent,double deviation)
    {
        this.agentNumberFrom = agentNumberFrom;
        this.getAgentNumberTo = getAgentNumberTo;
        this.myPosition = myPosition;
        this.myHeading = myHeading;
        this.azimuthToOtherAgent = azimuthToOtherAgent;
        this.distanceToOtherAgent = distanceToOtherAgent;
        this.deviation= deviation;
    }

    public Position myPosition;
    public double myHeading;
    public double azimuthToOtherAgent;
    public double distanceToOtherAgent;
    public double deviation;
    public int agentNumberFrom;
    public int getAgentNumberTo;
}
