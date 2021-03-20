package hr.fer.zemris.z2;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.lang.ProcessHandle.current;
import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllLines;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class Client {

    private static final int REPEAT_COUNT = 50;
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Object lock = new Object();
    private final long pid;
    private final Map<String, RandomAccessFile> outPipesCache = new HashMap<>();
    private final Path databasePath;
    private final List<Long> clientPids;
    private final RandomAccessFile inPipeRequests;
    private final RandomAccessFile inPipeResponses;
    private final RandomAccessFile dbOut;
    private final Set<Long> delayedResponse = new HashSet<>();

    private boolean alive = true;
    private long localClock = 0;
    private long whenIWant = MAX_VALUE;

    public Client(String dbPath, String clientPids) throws FileNotFoundException {
        this.pid = current().pid();
        this.databasePath = Path.of(dbPath);
        this.clientPids = stream(clientPids.split(" ")).map(Long::parseLong).filter(e -> !e.equals(pid)).collect(toList());
        inPipeRequests = new RandomAccessFile("lab1/temp/pipe-" + pid + "-Q", "rw");
        inPipeResponses = new RandomAccessFile("lab1/temp/pipe-" + pid + "-A", "rw");
        dbOut = new RandomAccessFile("lab1/temp/pipe-" + pid + "-DB", "rw");

        new Thread(() -> {
            new Scanner(System.in).nextLine();
            alive = false;
            try {
                inPipeRequests.close();
                inPipeResponses.close();
                for (RandomAccessFile e : outPipesCache.values()) {
                    e.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.err.println("CLIENT PID: " + current().pid());
        var otherClientPids = SCANNER.nextLine();
        sleep(1000);

        new Client(args[0], otherClientPids).start();
    }

    private void start() throws IOException, InterruptedException {
        new Thread(responder()).start();

        for (int j = 0; j < REPEAT_COUNT; j++) {
            synchronized (lock) {
                whenIWant = localClock;
                for (var clientPid : clientPids) {
                    System.err.println(pid + " " + clientPid + " REQ_REQ (" + pid + ", " + localClock + ")");
                    sendMessage(clientPid, "-Q", whoPlusClock());
                }
            }

            for (int i = 0; i < clientPids.size(); i++) {
                var response = receiveResponse().split(" ");
                var when = parseLong(response[1]);
                var who = parseLong(response[0]);
                System.err.println(pid + " " + who + " ANS_FROM " + i);

                synchronized (lock) {
                    localClock = max(when, localClock) + 1;
                    if (i + 1 == clientPids.size()) {

                        System.err.println("\nDatabase dump (" + pid + ")\n" + updateRecord() + "\n");
                        whenIWant = MAX_VALUE;
                        delayedResponse.forEach(delayed -> {
                            System.err.println(pid + " " + delayed + " ANS_DEL (" + pid + ", " + localClock + ")");
                            sendMessage(delayed, "-A", whoPlusClock());
                        });
                        delayedResponse.clear();
                    }
                }
            }
            sleep(randomSleep());
        }
    }

    private Runnable responder() {
        return () -> {
            while (alive) {
                var request = receiveQuestion().split(" ");
                long who = parseLong(request[0]);
                long when = parseLong(request[1]);
                System.err.println(who + " " + pid + " REQ_IN  (" + who + ", " + when + ")");
                synchronized (lock) {
                    for (int i = 0; i < 1000000; i++) ;
                    localClock = max(when, localClock) + 1;
                    if (whenIWant > when || (whenIWant == when && pid > who)) {
                        sendMessage(who, "-A", whoPlusClock());
                        System.err.println(pid + " " + who + " ANS_NOW (" + who + ", " + localClock + ")" + whenIWant);
                    } else {
                        delayedResponse.add(who);
                    }
                }
            }
        };
    }

    /**
     * Writting on disc resulted in inconsisted results because filesystem wasn't persisting on disc.
     *
     * @return
     * @throws IOException
     */
    private String updateRecord() throws IOException {
        String string;
        System.err.println(pid + " READ " + LocalDateTime.now());
        var lines = readAllLines(databasePath);
        for (int i = 0; i < lines.size(); i++) {
            var parsed = lines.get(i).split(" ");
            if (parsed[0].equals(valueOf(pid))) {
                lines.set(i, pid + " " + localClock + " " + (parseLong(parsed[2]) + 1));
                string = join("\n", lines);
                System.err.println(pid + " WRITE " + LocalDateTime.now());
                BufferedWriter writer = new BufferedWriter(new FileWriter(databasePath.toFile(), false));
                writer.write(string);
                writer.flush();
                writer.close();
                return string;
            }
        }
        lines.add(pid + " " + localClock + " " + 1);
        string = join("\n", lines);
        BufferedWriter writer = new BufferedWriter(new FileWriter(databasePath.toFile(), false));
        System.err.println(pid + " WRITE " + LocalDateTime.now());
        writer.write(string);
        writer.flush();
        writer.close();
        return string;
    }

//    private String updateRecord() throws IOException {
//        sendMessage("db", "READ " + pid);
//        var isNew = true;
//        var response = dbOut.readLine();
//        var lines = stream(response.split("\t")).collect(toList());
//        for (int i = 0; i < lines.size(); i++) {
//            var parsed = lines.get(i).split(" ");
//            if (parsed[0].equals(valueOf(pid))) {
//                sendMessage("db", "WRITE " + pid + " " + localClock + " " + (parseLong(parsed[2]) + 1));
//                isNew = false;
//            }
//        }
//        if (isNew) {
//            sendMessage("db", "WRITE " + pid + " " + localClock + " " + 1);
//        }
//        return response.replace("\t", "\n");
//    }

    private Long randomSleep() {
        return 100 + round(random() * 1900);
    }

    private String whoPlusClock() {
        return pid + " " + localClock;
    }

    private void sendMessage(Long pid, String pipe, String message) {
        sendMessage(pid + pipe, message);
    }

    private void sendMessage(String to, String message) {
        try {
            outPipesCache.computeIfAbsent(to, (e) -> {
                try {
                    return new RandomAccessFile("lab1/temp/pipe-" + to, "rw");
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                    return null;
                }
            });
            outPipesCache.get(to).write((message + "\n").getBytes(UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String receiveQuestion() {
        try {
            return inPipeRequests.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    private String receiveResponse() {
        try {
            return inPipeResponses.readLine();
        } catch (IOException e) {
            return "";
        }
    }
}
