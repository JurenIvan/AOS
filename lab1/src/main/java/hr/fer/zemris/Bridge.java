package hr.fer.zemris;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toList;

public class Bridge {

    private static final int NUMBER_OF_CARS_ON_BRIDGE = 3;

    private final List<Process> carProcesses;
    private final List<BufferedReader> brOutList;
    private final List<BufferedReader> brErrList;
    private final List<Writer> brInList;
    private final Queue<Integer> que1 = new LinkedBlockingQueue<>();
    private final Queue<Integer> que2 = new LinkedBlockingQueue<>();
    private boolean direction = false;
    private boolean shouldChange = false;

    public Bridge(List<Process> carProcesses) {
        this.carProcesses = carProcesses;
        this.brOutList = carProcesses.stream().map(e -> new BufferedReader(new InputStreamReader(e.getInputStream()))).collect(toList());
        this.brErrList = carProcesses.stream().map(e -> new BufferedReader(new InputStreamReader(e.getErrorStream()))).collect(toList());
        this.brInList = carProcesses.stream().map(e -> new BufferedWriter(new OutputStreamWriter(e.getOutputStream()))).collect(toList());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Runtime.getRuntime().exec("javac -cp src lab1/src/main/java/hr/fer/zemris/Car.java").waitFor();   //compile car
        sleep(5000);
        List<Process> carProcesses = new ArrayList<>();
        for (int i = 0; i < parseInt(args[0]); i++) {
            carProcesses.add(Runtime.getRuntime().exec("java -cp lab1/src/main/java hr.fer.zemris.Car " + i));
        }

        new Bridge(carProcesses).operate();
    }

    private static long randomNumber(int min, int max) {
        return round(random() * (max - min)) + min;
    }

    private void operate() {
        new Thread(listenForCarMessageRunnable()).start();
        new Thread(listenForCarRequestRunnable()).start();
        new Thread(changeDirectionRunnable()).start();
        new Thread(semaphore()).start();
    }

    private Runnable listenForCarMessageRunnable() {
        return () -> {
            while (isAnyCarAlive()) {
                for (BufferedReader bufferedReader : brErrList) {
                    try {
                        while (bufferedReader.ready()) {
                            System.err.println(bufferedReader.readLine());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private Runnable semaphore() {
        return () -> {
            try {
                while (isAnyCarAlive() || !que1.isEmpty() || !que2.isEmpty()) {
                    if (shouldChange) {
                        shouldChange = false;
//                        System.out.println("Direction change");
                        if (direction && !que1.isEmpty())
                            notifyCarsInCue(que1);
                        else if (!direction && !que2.isEmpty())
                            notifyCarsInCue(que2);
                        direction = !direction;
                    }
                    if (direction && que1.size() >= 3)
                        notifyCarsInCue(que1);
                    else if (!direction && que2.size() >= 3) {
                        notifyCarsInCue(que2);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private void notifyCarsInCue(Queue<Integer> que) throws IOException, InterruptedException {
        List<Integer> carsToNotify = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_CARS_ON_BRIDGE && !que.isEmpty(); i++) {
            var element = que.poll();
//            System.out.println("Auto " + element + " moze uci");
            sendMessageToProcess(element, "GO");
            carsToNotify.add(element);
        }
        sleep(randomNumber(1000, 3000));
        for (Integer integer : carsToNotify) {
//            System.out.println("Auto " + integer + " moze izaci");
            sendMessageToProcess(integer, "GONE");
        }
        sleep(randomNumber(50, 100));
    }

    private void sendMessageToProcess(Integer element, String message) throws IOException {
        brInList.get(element).write(message + "\n");
        brInList.get(element).flush();
    }

    private Runnable changeDirectionRunnable() {
        return () -> {
            try {
                while (isAnyCarAlive()) {
                    sleep(randomNumber(500, 1000));
                    shouldChange = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private Runnable listenForCarRequestRunnable() {
        return () -> {
            while (isAnyCarAlive()) {
                for (int i = 0; i < brOutList.size(); i++) {
                    try {
                        if (brOutList.get(i).ready()) {
                            var message = brOutList.get(i).readLine().split(" ");
//                            System.out.println(message[0] + " " + message[1] + " " + message[2]);
                            if (parseBoolean(message[2])) que1.add(i);
                            else que2.add(i);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private boolean isAnyCarAlive() {
        return carProcesses.stream().anyMatch(Process::isAlive);
    }
}
