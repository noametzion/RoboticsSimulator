package simulator.LocationAlgorithms;

import javafx.util.Pair;
import simulator.AgentViewDetails;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationPoint;
import utils.Util;
import view.Position;

import java.util.*;

/**
 * Created by נועם on 1/16/2018.
 */
public class CalibrationLocationAlgorithm implements ILocationAlgorithm {

    final int lowAngleBorder = 15;
    final int highAngleBorder = 345;
    List<Pair<EvaluationPoint, EvaluationPoint>> evaluatedPoints = null;

    private int numberOfAgents;

    public HashMap<Integer,Double> additionalEvaluatedRangeErrorByAgent; //=0;
    public HashMap<Integer,Double> additionalEvaluatedAzimuthErrorByAgent;//= 0;
    public HashMap<Integer,Integer> additionalEvaluatedRangeErrorByAgentCounter; //=0;
    public HashMap<Integer,Integer> additionalEvaluatedAzimuthErrorByAgentCounter ;//= 0;
    public HashMap<Integer,Double> finalEvaluatedRangeErrorByAgent; //=0;
    public HashMap<Integer,Double> finalEvaluatedAzimuthErrorByAgent ;//= 0;

    @Override
    public EvaluatedLocationResult CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {

        InitFinalErrorLists(numberOfAgents);
        evaluatedPoints = GetEvaluatedPositionsFromAllAgents(viewDetailPairs);

        for (int i = 0; i < 100; i++) {
            // Split  error - between pair
            SplitErrorBetweenPair(numberOfAgents);

            // Recalculate
            RecalculateEvaluatedPoints();
        }

        // Split error - between agents
        Position finalPosition;
        try {
            finalPosition = SplitErrorBetweenAgents(numberOfAgents);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Recalculate
        RecalculateEvaluatedPoints();

        EvaluatedLocationResult result = new EvaluatedLocationResult();
        result.position = evaluatedPoints.get(1).getKey().center;
        result.evaluationShapes = new ArrayList<>();
        for (Pair<EvaluationPoint, EvaluationPoint> pair:
             evaluatedPoints) {
            result.evaluationShapes.add(pair.getKey());
            result.evaluationShapes.add(pair.getValue());
        }
        return result;
    }

    private void RecalculateEvaluatedPoints() {
        for (Pair<EvaluationPoint, EvaluationPoint> pair:
             evaluatedPoints) {
            pair.getKey().recalculateEvaluationPointsWithError(finalEvaluatedRangeErrorByAgent.get(pair.getKey().measuringAgentNumber),
                    finalEvaluatedAzimuthErrorByAgent.get(pair.getKey().measuringAgentNumber));
            pair.getValue().recalculateEvaluationPointsWithError(finalEvaluatedRangeErrorByAgent.get(pair.getValue().measuringAgentNumber),
                    finalEvaluatedAzimuthErrorByAgent.get(pair.getValue().measuringAgentNumber));

        }
    }

    private void InitFinalErrorLists(int numberOfAgents) {

        finalEvaluatedRangeErrorByAgent = new HashMap<>();
        finalEvaluatedAzimuthErrorByAgent = new HashMap<>();
        for (int i = 1 ; i <= numberOfAgents ; i++)
        {
            finalEvaluatedRangeErrorByAgent.put(i, 0.0);
            finalEvaluatedAzimuthErrorByAgent.put(i, 0.0);
        }
    }

    private void InitAdditionalErrorLists(int numberOfAgents) {
        additionalEvaluatedRangeErrorByAgent = new HashMap<>();
        additionalEvaluatedAzimuthErrorByAgent = new HashMap<>();
        additionalEvaluatedRangeErrorByAgentCounter = new HashMap<>();
        additionalEvaluatedAzimuthErrorByAgentCounter = new HashMap<>();
        for (int i = 1 ; i <= numberOfAgents ; i++)
        {
            additionalEvaluatedRangeErrorByAgent.put(i, 0.0);
            additionalEvaluatedAzimuthErrorByAgent.put(i, 0.0);
            additionalEvaluatedRangeErrorByAgentCounter.put(i, 0);
            additionalEvaluatedAzimuthErrorByAgentCounter.put(i, 0);
        }
    }


    private List<Pair<EvaluationPoint, EvaluationPoint>> GetEvaluatedPositionsFromAllAgents(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {
        List<Pair<EvaluationPoint, EvaluationPoint>> evaluatedOvals = new ArrayList<>();
        for (Pair<AgentViewDetails, AgentViewDetails> pair : viewDetailPairs) {

            EvaluationPoint evaluatedPoint = null;
            EvaluationPoint oppositeEvaluationPoint = null;

            if (pair.getKey() != null) {
                // To avoid evaluation by unknown position;
                if(pair.getKey().myPosition != null)
                {
                    evaluatedPoint = new EvaluationPoint(pair.getKey().agentNumberFrom,
                            pair.getKey().myPosition, pair.getKey().azimuthToOtherAgent, pair.getKey().distanceToOtherAgent);
                }
            }
            if (pair.getValue() != null){
                // To avoid evaluation by unknown position;
                if(pair.getValue().otherPosition != null)
                {
                    double correctAzimuth = Util.set0to359(pair.getValue().azimuthToOtherAgent + 180);
                    oppositeEvaluationPoint = new EvaluationPoint(pair.getValue().agentNumberFrom,
                            pair.getValue().otherPosition, correctAzimuth, pair.getValue().distanceToOtherAgent);
                }
            }

            evaluatedOvals.add(new Pair<>(evaluatedPoint, oppositeEvaluationPoint));
        }

        return evaluatedOvals;
    }

    private void SplitErrorBetweenPair(int numberOfAgents) {
        InitAdditionalErrorLists(numberOfAgents);
        for (Pair<EvaluationPoint, EvaluationPoint> pair:
             evaluatedPoints) {
            if(pair.getKey() != null && pair.getValue() != null)
            {
                // Range
                double rangeError = calculateRangeError(pair);

                boolean isKeyRangeBigger = (pair.getKey().featureRangeToCenter > pair.getValue().featureRangeToCenter);

                if(isKeyRangeBigger) {
                    additionalEvaluatedRangeErrorByAgent.put(
                            pair.getKey().measuringAgentNumber, additionalEvaluatedRangeErrorByAgent.get(pair.getKey().measuringAgentNumber)
                                    - (rangeError / 2));
                    additionalEvaluatedRangeErrorByAgent.put(
                            pair.getValue().measuringAgentNumber, additionalEvaluatedRangeErrorByAgent.get(pair.getValue().measuringAgentNumber)
                                    + (rangeError / 2));
                }
                else {
                    additionalEvaluatedRangeErrorByAgent.put(
                            pair.getKey().measuringAgentNumber, additionalEvaluatedRangeErrorByAgent.get(pair.getKey().measuringAgentNumber)
                                    + (rangeError / 2));
                    additionalEvaluatedRangeErrorByAgent.put(
                            pair.getValue().measuringAgentNumber, additionalEvaluatedRangeErrorByAgent.get(pair.getValue().measuringAgentNumber)
                                    - (rangeError / 2));
                }

                additionalEvaluatedRangeErrorByAgentCounter.put(pair.getKey().measuringAgentNumber,
                        additionalEvaluatedRangeErrorByAgentCounter.get(pair.getKey().measuringAgentNumber) + 1);
                additionalEvaluatedRangeErrorByAgentCounter.put(pair.getValue().measuringAgentNumber,
                        additionalEvaluatedRangeErrorByAgentCounter.get(pair.getValue().measuringAgentNumber) + 1);


                // Azimuth
                double azimuthError = calculateSpanErrorInDegrees(pair);

                boolean isKeyAzimuthBigger = (pair.getKey().featureAzimuthToCenter > pair.getValue().featureAzimuthToCenter);

                double keyAngle = pair.getKey().featureAzimuthToCenter;
                double valueAngle = Util.set0to359(pair.getValue().featureAzimuthToCenter); //+ 180

                if ( 0 <= keyAngle && keyAngle <= lowAngleBorder && highAngleBorder <= valueAngle && valueAngle <= 360 ){
                    isKeyAzimuthBigger = true;
                }
                else if ( 0 <= valueAngle && valueAngle <= lowAngleBorder && highAngleBorder <= keyAngle && keyAngle <= 360 ){
                    isKeyAzimuthBigger = false;
                }

                if (isKeyAzimuthBigger)
                {
                    additionalEvaluatedAzimuthErrorByAgent.put(
                            pair.getKey().measuringAgentNumber,
                           additionalEvaluatedAzimuthErrorByAgent.get(pair.getKey().measuringAgentNumber)
                                    - (azimuthError / 2));
                    additionalEvaluatedAzimuthErrorByAgent.put(
                    pair.getValue().measuringAgentNumber,
                            additionalEvaluatedAzimuthErrorByAgent.get(pair.getValue().measuringAgentNumber)
                            + (azimuthError / 2));
                }
                else {
                    additionalEvaluatedAzimuthErrorByAgent.put(
                            pair.getKey().measuringAgentNumber,
                            additionalEvaluatedAzimuthErrorByAgent.get(pair.getKey().measuringAgentNumber)
                                    + (azimuthError / 2));
                    additionalEvaluatedAzimuthErrorByAgent.put(
                            pair.getValue().measuringAgentNumber,
                            additionalEvaluatedAzimuthErrorByAgent.get(pair.getValue().measuringAgentNumber)
                                    - (azimuthError / 2));
                }

                additionalEvaluatedAzimuthErrorByAgentCounter.put(pair.getKey().measuringAgentNumber,
                        additionalEvaluatedAzimuthErrorByAgentCounter.get(pair.getKey().measuringAgentNumber) + 1);
                additionalEvaluatedAzimuthErrorByAgentCounter.put(pair.getValue().measuringAgentNumber,
                        additionalEvaluatedAzimuthErrorByAgentCounter.get(pair.getValue().measuringAgentNumber) + 1);
            }
        }

        // Take average
        CalculateAverageErrors(numberOfAgents);
    }

    private Position SplitErrorBetweenAgents(int numberOfAgents) throws Exception{
        InitAdditionalErrorLists(numberOfAgents);
        // Take firstPosition
        EvaluationPoint ep;
        if (evaluatedPoints.get(0).getKey()!= null)
        {
            ep = evaluatedPoints.get(0).getKey();
        }
        else if (evaluatedPoints.get(0).getValue() != null){
            ep = evaluatedPoints.get(0).getValue();
        }
        else {
            throw new Exception("Key and Value both null - ERROR!!!");
        }

        for (int i = 0; i < 100; i++) {
            for (Pair<EvaluationPoint, EvaluationPoint> pair :
                    evaluatedPoints) {
                EvaluationPoint evaluationPointToChange;
                boolean isKey = false;
                if (pair.getKey() != null) {
                    evaluationPointToChange = pair.getKey();
                    isKey = true;
                } else if (pair.getValue() != null) {
                    evaluationPointToChange = pair.getValue();
                } else {
                    throw new Exception("Key and Value both null - ERROR!!!");
                }

                // Range
                // TODO: check if featureAgentNumberIsCorrect!!
                double rangeError = CalculateRangeErrorByPoints(ep.center, evaluationPointToChange);
                additionalEvaluatedRangeErrorByAgent.put(
                        evaluationPointToChange.measuringAgentNumber, additionalEvaluatedRangeErrorByAgent.get(evaluationPointToChange.measuringAgentNumber)
                                + (rangeError / numberOfAgents));
                additionalEvaluatedRangeErrorByAgentCounter.put(
                        evaluationPointToChange.measuringAgentNumber, additionalEvaluatedRangeErrorByAgentCounter.get(evaluationPointToChange.measuringAgentNumber) + 1);

                // Span
                double spanError = CalculateSpanErrorByPoints(ep.center, evaluationPointToChange);
                additionalEvaluatedAzimuthErrorByAgent.put(
                        evaluationPointToChange.measuringAgentNumber, additionalEvaluatedAzimuthErrorByAgent.get(evaluationPointToChange.measuringAgentNumber)
                                + (spanError / numberOfAgents));
                additionalEvaluatedAzimuthErrorByAgentCounter.put(
                        evaluationPointToChange.measuringAgentNumber, additionalEvaluatedAzimuthErrorByAgentCounter.get(evaluationPointToChange.measuringAgentNumber) + 1);

                // TODO: check if modifying the position here helps..
            }

            // Take average
            CalculateAverageErrors(numberOfAgents);
        }

        return ep.center;

    }

    private void CalculateAverageErrors(int numberOfAgents) {
        // Take average
        for (int i = 1; i <= numberOfAgents; i++)
        {
            if(additionalEvaluatedRangeErrorByAgentCounter.get(i) != 0) {
                finalEvaluatedRangeErrorByAgent.put(i,
                        additionalEvaluatedRangeErrorByAgent.get(i) /
                                additionalEvaluatedRangeErrorByAgentCounter.get(i));
            }
            if(additionalEvaluatedAzimuthErrorByAgentCounter.get(i) != 0)
                finalEvaluatedAzimuthErrorByAgent.put(i,
                        additionalEvaluatedAzimuthErrorByAgent.get(i) /
                                additionalEvaluatedAzimuthErrorByAgentCounter.get(i));
        }
    }

    private double CalculateSpanErrorByPoints(Position p, EvaluationPoint evaluationPoint) {
        Position fp = evaluationPoint.featurePosition;
        double spanFromFeatureToPosition = Util.getAzimuth(fp.x, fp.y, p.x, p.y);
        double spanFromFeatureToCenter = evaluationPoint.featureAzimuthToCenter;

        double error = spanFromFeatureToPosition - spanFromFeatureToCenter;

        if(error < -180){
            error = error + 360;
        }
        else if (error > 180){
            error = error - 360;
        }
        return  error;
    }

    private double CalculateRangeErrorByPoints(Position p, EvaluationPoint evaluationPoint) {
        Position fp = evaluationPoint.featurePosition;
        double rangeFromFeatureToPosition = Math.sqrt((p.x-fp.x)*(p.x-fp.x) + (p.y-fp.y)*(p.y-fp.y));
        double rangeFromFeatureToCenter = evaluationPoint.featureRangeToCenter;
        double error = rangeFromFeatureToPosition - rangeFromFeatureToCenter;
        return error;
    }


    private double calculateRangeError(Pair<EvaluationPoint, EvaluationPoint> evaluationPointsPair){
        double error = 0;
        if(evaluationPointsPair.getKey() != null && evaluationPointsPair.getValue() != null)
        {
            error = Math.abs(evaluationPointsPair.getKey().featureRangeToCenter - evaluationPointsPair.getValue().featureRangeToCenter);
        }

        return error;
    }

    private double calculateSpanErrorInDegrees(Pair<EvaluationPoint, EvaluationPoint> evaluationPointsPair){
        // default
        double error = 0;

        if (evaluationPointsPair.getValue() != null && evaluationPointsPair.getKey() != null) {
            // TODO: check this very good!!!
            double keyAngle = evaluationPointsPair.getKey().featureAzimuthToCenter;
            double valueAngle = Util.set0to359(evaluationPointsPair.getValue().featureAzimuthToCenter);

            error = Math.abs(keyAngle - valueAngle);

            if ( 0 <= keyAngle && keyAngle <= lowAngleBorder && highAngleBorder <= valueAngle && valueAngle <= 360 ){
                error = keyAngle + ( 360 - valueAngle );
            }
            else if ( 0 <= valueAngle && valueAngle <= lowAngleBorder && highAngleBorder <= keyAngle && keyAngle <= 360 ){
                error = valueAngle + ( 360 - keyAngle );
            }
        }

        return error;
    }


    @Override
    public void setCVariableInFunction(double c) {
    }

    @Override
    public void setMeasurementDeviation(double deviation) {
    }

    @Override
    public void setNumberOfAgents(int numberOfAgents) {
        this.numberOfAgents = numberOfAgents;
    }
}
