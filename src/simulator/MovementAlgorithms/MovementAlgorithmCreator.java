package simulator.MovementAlgorithms;

/**
 * Created by נועם on 10/4/2017.
 */
public class MovementAlgorithmCreator {

    public MovementAlgorithm Create(String movementAlgorithmName) throws Exception {
        if(movementAlgorithmName.equals("3forward"))
        {
            return new ThreeForwardMovementAlgorithm();
        }
        else if(movementAlgorithmName.equals("3forwardV2"))
        {
            return new ThreeForwardMovementAlgorithmV2();
        }
        else
            throw new Exception("Wrong movement algorithm name");
    }
}
