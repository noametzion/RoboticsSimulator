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
        else if(movementAlgorithmName.equals("3forwardV3"))
        {
            return new ThreeForwardMovementAlgorithmV3();
        }
        else if(movementAlgorithmName.equals("NagentsMove"))
        {
            return new NAgentsMovementAlgorithem();
        }
        else if(movementAlgorithmName.equals("NagentsMove2Row"))
        {
            return new NAgentsMovementAlgorithem2Rows();
        }
        else
            throw new Exception("Wrong movement algorithm name");
    }
}
