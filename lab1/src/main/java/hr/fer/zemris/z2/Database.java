package hr.fer.zemris.z2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Long.parseLong;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

public class Database {

    private static final int WORKING_THREADS_POOL = 1;
    private final Map<Long, RandomAccessFile> outPipesCache = new HashMap<>();
    private final RandomAccessFile inPipeResponses;
    private final Map<Long, Long[]> dataStore = new HashMap<>();
    private static boolean alive = true;

    public Database() throws FileNotFoundException {
        inPipeResponses = new RandomAccessFile("lab1/temp/pipe-db", "rw");
    }

    public static void main(String[] args) throws FileNotFoundException {
        new Database().start();

        new Thread(() -> {
            new Scanner(System.in).nextLine();
            alive = false;
        }).start();
    }

    private static String serializeRecord(Long key, Long[] values) {
        return key + " " + values[0] + " " + values[1];
    }

    private static Long[] createRecord(long localTime, long number) {
        Long[] record = new Long[2];
        record[0] = localTime;
        record[1] = number;
        return record;
    }

    private void sendMessage(long pid, String message) {
        try {
            outPipesCache.putIfAbsent(pid, new RandomAccessFile("lab1/temp/pipe-" + pid + "-DB", "rw"));
            outPipesCache.get(pid).write((message + "\n").getBytes(UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Runnable workingThread() {
        return () -> {
            while (alive) {
                try {
                    var parsed = inPipeResponses.readLine().split(" ");
                    if ("READ".equals(parsed[0])) handleReadAll(parsed);
                    else if ("WRITE".equals(parsed[0])) handleWrite(parsed);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void start() {
        for (int i = 0; i < WORKING_THREADS_POOL; i++) {
            new Thread(workingThread()).start();
        }
        System.out.println("POKRENUTA BAZA");
    }

    private void handleReadAll(String[] parsed) {
        var pid = parseLong(parsed[1]);
        var body = dataStore.entrySet().stream().map(e -> serializeRecord(e.getKey(), e.getValue())).collect(joining("\t"));
        sendMessage(pid, body);
    }

    private void handleWrite(String[] parsed) {
        var pid = parseLong(parsed[1]);
        var clock = parseLong(parsed[2]);
        var number = parseLong(parsed[3]);
        dataStore.put(pid, createRecord(clock, number));
    }
}
