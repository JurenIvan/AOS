package hr.fer.zemris.z2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

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

    private static final int REPEAT_COUNT = 5;
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final long PID = current().pid();
    private static final Map<String, RandomAccessFile> outPipesCache = new HashMap<>();
    private static final String DELIMITER = " ";
    private static final ReaderIfNonePresent READER_FUCTION = new ReaderIfNonePresent();

    private final Path databasePath;
    private final List<Long> clientPids;
    private final RandomAccessFile inPipeRequests;
    private final RandomAccessFile inPipeResponses;
    private final Set<Long> delayedResponse = new HashSet<>();

    private long localClock = 0;
    private long whenIWant = MAX_VALUE;
    private boolean alive = true;

    public Client(String dbPath, String allClientPids) throws FileNotFoundException {
        this.databasePath = Path.of(dbPath);
        this.clientPids = stream(allClientPids.split(DELIMITER)).map(Long::parseLong).filter(e -> !e.equals(PID)).collect(toList());
        inPipeRequests = new RandomAccessFile("lab1/temp/pipe-" + PID + "-Q", "rw");
        inPipeResponses = new RandomAccessFile("lab1/temp/pipe-" + PID + "-A", "rw");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.err.printf("CLIENT PID: %d%n", PID);
        var otherClientPids = SCANNER.nextLine();

        new Client(args[0], otherClientPids).start();
    }

    private void start() throws IOException, InterruptedException {
        new Thread(new KillerRunner()).start();
        new Thread(responder()).start();

        for (int j = 0; j < REPEAT_COUNT; j++) {
            synchronized (Client.class) {
                whenIWant = localClock;
                for (var clientPid : clientPids) {
                    System.err.printf("%d %d REQ_REQ (%d, %d)%n", PID, clientPid, PID, whenIWant);
                    sendMessage(clientPid, "-Q", pidPlusClock(whenIWant));
                }
            }

            for (int i = 0; i < clientPids.size(); i++) {
                var parsedResponse = receiveResponse().split(DELIMITER);
                var when = parseLong(parsedResponse[1]);

                synchronized (Client.class) {
                    localClock = max(when, localClock) + 1;
                    if (i + 1 == clientPids.size()) {

                        System.err.printf("\nDatabase dump (%d)\n%s\n%n", PID, updateRecord());
                        whenIWant = MAX_VALUE;
                        delayedResponse.forEach(delayed -> {
                            System.err.printf("%d %d ANS_DEL (%d, %d)%n", PID, delayed, PID, localClock);
                            sendMessage(delayed, "-A", pidPlusClock(localClock));
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
                var request = receiveQuestion().split(DELIMITER);

                long who = parseLong(request[0]);
                long when = parseLong(request[1]);
                System.err.printf("%d %d REQ_IN  (%d, %d)%n", PID, who, who, when);
                synchronized (Client.class) {
                    localClock = max(when, localClock) + 1;
                    if (whenIWant > when || (whenIWant == when && PID > who)) {
                        sendMessage(who, "-A", pidPlusClock(localClock));
                        System.err.printf("%d %d ANS_NOW (%d, %d)%n", PID, who, who, localClock);
                    } else {
                        delayedResponse.add(who);
                    }
                }
            }
        };
    }

    private String updateRecord() throws IOException {
        var lines = readAllLines(databasePath);
        var isNotPresent = true;
        for (int i = 0; i < lines.size() && isNotPresent; i++) {
            var parsed = lines.get(i).split(DELIMITER);
            if (parsed[0].equals(valueOf(PID))) {
                lines.set(i, "%d %d %d".formatted(PID, localClock, parseLong(parsed[2]) + 1));
                isNotPresent = false;
            }
        }
        if (isNotPresent)
            lines.add("%d %d %d".formatted(PID, localClock, 1));

        String string = join("\n", lines);
        Files.writeString(databasePath, string);
        return string;
    }

    private String pidPlusClock(long clock) {
        return PID + " " + clock;
    }

    private void sendMessage(Long pid, String pipe, String message) {
        sendMessage(pid + pipe, message);
    }

    private void sendMessage(String to, String message) {
        try {
            outPipesCache.computeIfAbsent(to, READER_FUCTION);
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

    private String receiveResponse() throws IOException {
        return inPipeResponses.readLine();
    }

    private static Long randomSleep() {
        return 100 + round(random() * 1900);
    }

    private class KillerRunner implements Runnable {
        @Override
        public void run() {
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
        }
    }

    private static class ReaderIfNonePresent implements Function<String, RandomAccessFile> {

        @Override
        public RandomAccessFile apply(String key) {
            try {
                return new RandomAccessFile("lab1/temp/pipe-" + key, "rw");
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
                return null;
            }
        }
    }
}
