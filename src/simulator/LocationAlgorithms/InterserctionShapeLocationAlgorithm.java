package simulator.LocationAlgorithms;

import IGradable.IGradable;
import com.vividsolutions.jts.geom.Geometry;
import javafx.util.Pair;
import simulator.AgentViewDetails;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationArcPolygon;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationShape;
import utils.CoordinateGradeCalculator;
import utils.Util;
import view.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by נועם on 3/5/2018.
 */
public class InterserctionShapeLocationAlgorithm implements ILocationAlgorithm{

    private double measurementDeviation;
    private double spanError;
    private CoordinateGradeCalculator coordinateGradeCalculator = new CoordinateGradeCalculator();

    @Override
    public EvaluatedLocationResult CalculateEvaluatedPosition(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {

        spanError = Util.ConvertBetweenPercentageDeviationAndTrueDeviationByDefinition(measurementDeviation);

        List<EvaluationShape> evaluationShapes = CreateAllEvaluationPolygons(viewDetailPairs);

        List<Position> randomPositions = this.getRandomPositions(evaluationShapes);

        Position evaluatedPosition = null;
        try {
            evaluatedPosition = FindBestPosition(randomPositions, evaluationShapes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        EvaluatedLocationResult result = new EvaluatedLocationResult();
        result.evaluationShapes = evaluationShapes;
        result.position = evaluatedPosition;

        return result;
    }

    private List<Position> getRandomPositions(List<EvaluationShape> evaluationShapes) {
        List <Position> positions = new ArrayList<>();
        for (EvaluationShape eShape : evaluationShapes ) {
            positions.addAll(eShape.GetRandomPositions());
        }
        return positions;
    }

    private Position FindBestPosition(List<Position> randomPositions, List<EvaluationShape> evaluationShapes) throws Exception {
        double bestGrade = 0;
        Position bestPosition = null;
        List<IGradable> gradableShapes = this.convertToGradables(evaluationShapes);
        for (Position randomPosition : randomPositions) {
            double curentGrade = this.coordinateGradeCalculator.GetGrade(gradableShapes, randomPosition);
            if(curentGrade > bestGrade){
                bestGrade = curentGrade;
                bestPosition = randomPosition;
            }
        }
        return bestPosition;
        //return evaluationShapes.get(0).getPosition();
    }

    private List<IGradable> convertToGradables(List<EvaluationShape> evaluationShapes) {
        List<IGradable> gradables = new ArrayList<IGradable>();
        for (EvaluationShape shape :evaluationShapes) {
            gradables.add((EvaluationArcPolygon)shape);
        }
        return gradables;
    }

    private Geometry UnionEvaluationArcs(List<EvaluationShape> evaluationShapes) {
        Geometry geometry = ((EvaluationArcPolygon)evaluationShapes.get(0)).getEvaluationPolygon();
        for (int i =1 ; i<evaluationShapes.size(); i++){
            geometry.union(((EvaluationArcPolygon)evaluationShapes.get(i)).getEvaluationPolygon());
        }
        return geometry;
    }

    private List<EvaluationShape> CreateAllEvaluationPolygons(List<Pair<AgentViewDetails, AgentViewDetails>> viewDetailPairs) {
        List<EvaluationShape> evaluationShapes = new ArrayList<>();
        for (Pair<AgentViewDetails, AgentViewDetails> viewDetailPair:
             viewDetailPairs) {

            AgentViewDetails key = viewDetailPair.getKey();
            AgentViewDetails value = viewDetailPair.getValue();

            if (key != null) {
                // To avoid evaluation by unknown position;
                if(key.myPosition != null)
                {
                    double rangeError = measurementDeviation * key.distanceToOtherAgent / 100;
                    evaluationShapes.add(new EvaluationArcPolygon(key.otherPosition, key.myPosition, key.azimuthToOtherAgent, key.distanceToOtherAgent, rangeError, spanError));
                }
            }

            if (value != null){
                // To avoid evaluation by unknown position;
                if(key.otherPosition != null)
                {
                    double correctAzimuth = Util.set0to359(value.azimuthToOtherAgent + 180);
                    double rangeError = measurementDeviation * key.distanceToOtherAgent / 100;
                    evaluationShapes.add(new EvaluationArcPolygon(value.myPosition, value.otherPosition, correctAzimuth, value.distanceToOtherAgent, rangeError, spanError));
                }
            }
        }
        return evaluationShapes;
    }

    @Override
    public void setCVariableInFunction(double c) {

    }

    @Override
    public void setMeasurementDeviation(double deviation) {
        this.measurementDeviation = deviation;
    }

    @Override
    public void setNumberOfAgents(int numberOfAgents) {

    }
}
