package utils;

import IGradable.IGradable;
import simulator.LocationAlgorithms.EvaluationShapes.EvaluationShape;
import view.Position;

import java.util.List;

/**
 * Created by נועם on 4/2/2018.
 */
public class CoordinateGradeCalculator {

    public static double GetGrade(List<? extends IGradable> gradables, Position position) throws Exception {

        if (gradables.size() == 0)
        {
            return 0;
        }

        double gradeSum = 0;
        for (IGradable gradable: gradables) {
            gradeSum += gradable.getGrade(position);
        }

        double gradeAvg = gradeSum / gradables.size();
        return gradeAvg;
    }
}
