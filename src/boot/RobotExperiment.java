package boot;

import simulator.PerimeterDefenseSimTask;
import utils.Util;
import view.SimulatorWindow;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RobotExperiment {

    public static void main(String[] args) {
        int numOfSteps=2000;
        int numofExperimentsPerDeviation=10;
        int numOfDeviations=20;
        double minDeviation=0;
        double deviationInterval=0.01;
        String results="scenarios\\results.txt";
        String scenarioFile="scenarios/sc0.txt";
        BufferedWriter  writer = null;
        DecimalFormat df = new DecimalFormat("#.###");
        try {
            writer = new BufferedWriter(new FileWriter(results));
            writer.write("Deviation   Distance   stdDev\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        double deviation= minDeviation;
        for(int i=0;i<numOfDeviations;i++) {
            List<Double> distances= new ArrayList<>();
            for (int j = 0; j < numofExperimentsPerDeviation; j++) {
                SimulatorWindow sw = new SimulatorWindow(800, 500, "my simulator", new PerimeterDefenseSimTask(scenarioFile, deviation), 100, true, true, numOfSteps);
                sw.start();
                try {
                    sw.join();
                    double distance = sw.getSumDistanceFromLocation();
                    distances.add(new Double(distance));
                    //sw=null;
                    //System.gc();
                    sw.close();
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
            double averageDistance = getAverageList(distances);
            double stdDev= getStdDev(distances);
            try {
                writer.write(  df.format(deviation)+ "    " + df.format(averageDistance) + "    "+ df.format(stdDev) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            deviation+=deviationInterval;

        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


static double getAverageList(List<Double> list){
        double sum=0;
    for (Double d: list) {
        sum+= d;
    }
    return sum/list.size();
}
static double getStdDev(List<Double> list){
    double average=getAverageList(list);
    double temp = 0;
    for(double a :list)
        temp += (a-average)*(a-average);
    double s= Math.sqrt(temp/(list.size()-1));
    return s;

}

}

