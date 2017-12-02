//package boot;
//
//import simulator.PerimeterDefenseSimTask;
//import utils.Util;
//import view.SimulatorWindow;
//
//import java.io.*;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class RobotExperiment {
//    public enum DeviationType{
//        movementDeviation,
//        sensorDeviation
//    }
//
//    public static void main(String[] args) {
//        DeviationType deviationType = DeviationType.sensorDeviation;
//        int distanceOfExperiment=1000;
//        int numofExperimentsPerDeviation=40;
//        int numOfDeviations=20;
//        int minDeviation=1;
//        int deviationInterval=1;
//        double c=1;
//        String results="results\\results.csv";
//        List<String> fileNames= getFilesNames();
//
//        BufferedWriter  writer = null;
//        BufferedReader reader = null;
//        DecimalFormat df = new DecimalFormat("#.###");
//        try {
//            writer = new BufferedWriter(new FileWriter(results));
//            writer.write("Deviation   Distance   stdDev\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        for(String file: fileNames) {
//            String scenarioFile="scenarios/" + file;
//            try {
//                reader= new BufferedReader(new FileReader(scenarioFile));
//                String position= reader.readLine();
//                String span= reader.readLine();
//                String distance= reader.readLine();
//                for(int i=0; i<6; i++)
//                    reader.readLine();
//                String movement= reader.readLine();
//                String localization= reader.readLine();
//                writer.write(file + "\n");
//                writer.write(position + "\n");
//                writer.write(span + "\n");
//                writer.write(distance + "\n");
//                writer.write(movement + "\n");
//                writer.write(localization+ "\n");
//                reader.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int deviation= minDeviation;
//            int deviationSensor = 0;
//            int deviationMovement = 0;
//            for (int i = 0; i < numOfDeviations; i++) {
//                List<Double> distances = new ArrayList<>();
//                for (int j = 0; j < numofExperimentsPerDeviation; j++) {
//                    switch (deviationType) {
//                        case movementDeviation: {
//                            deviationMovement = deviation;
//                            break;
//                        }
//                        case sensorDeviation: {
//                            deviationSensor = deviation;
//                            break;
//                        }
//                    }
//
//                    SimulatorWindow sw = new SimulatorWindow(800, 500, "my simulator", new PerimeterDefenseSimTask(scenarioFile, deviationSensor, deviationMovement,c), 100, true, true, distanceOfExperiment);
//
//                    sw.isExpiramentMode = true;
//                    sw.start();
//                    try {
//                        sw.join();
//                        double distance = sw.getSumDistanceFromLocation();
//                        distances.add(new Double(distance));
//                        //sw=null;
//                        //System.gc();
//                        sw.close();
//                    } catch (InterruptedException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    }
//
//                }
//                double averageDistance = getAverageList(distances);
//                double stdDev = getStdDev(distances);
//                try {
//                    writer.write(df.format(deviation) + "    " + df.format(averageDistance) + "    " + df.format(stdDev) + "\n");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                deviation += deviationInterval;
//
//            }
//            try {
//                writer.write("\n\n");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        try {
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//static double getAverageList(List<Double> list){
//        double sum=0;
//    for (Double d: list) {
//        sum+= d;
//    }
//    return sum/list.size();
//}
//static double getStdDev(List<Double> list){
//    double average=getAverageList(list);
//    double temp = 0;
//    for(double a :list)
//        temp += (a-average)*(a-average);
//    double s= Math.sqrt(temp/(list.size()-1));
//    return s;
//
//}
//static List<String> getFilesNames(){
//    List<String> fileNames = new ArrayList<String>();
//
//
//    File[] files = new File("scenarios").listFiles();
////If this pathname does not denote a directory, then listFiles() returns null.
//
//    for (File file : files) {
//        if (file.isFile()) {
//            fileNames.add(file.getName());
//        }
//    }
//    return fileNames;
//}
//
//
//}
//
