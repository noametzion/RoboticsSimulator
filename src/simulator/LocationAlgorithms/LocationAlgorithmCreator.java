package simulator.LocationAlgorithms;

import javax.xml.stream.Location;

/**
 * Created by נועם on 10/24/2017.
 */
public class LocationAlgorithmCreator {

    public ILocationAlgorithm Create(String locationAlgorithmName) throws Exception {
        if(locationAlgorithmName.equals("average"))
        {
            return new AverageLocationAlgorithm();
        }
        else
            throw new Exception("Wrong location algorithm name");
    }
}