package hr.fer.zemris.z2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Runner {

    private static boolean alive = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        cleanTemp(new File("lab1/temp"));

        Runtime.getRuntime().exec("mkdir lab1/temp").waitFor();
        Runtime.getRuntime().exec("touch lab1/temp/db.txt").waitFor();
        Runtime.getRuntime().exec("mkfifo lab1/temp/pipe-db").waitFor();
        Runtime.getRuntime().exec("javac -cp src lab1/src/main/java/hr/fer/zemris/z2/Client.java").waitFor();
        Runtime.getRuntime().exec("javac -cp src lab1/src/main/java/hr/fer/zemris/z2/Database.java").waitFor();
        var db = Runtime.getRuntime().exec("java -cp lab1/src/main/java hr.fer.zemris.z2.Database");

        sleep(1000);

        List<Process> clients = new ArrayList<>();
        for (int i = 0; i < parseInt(args[0]); i++) {
            clients.add(Runtime.getRuntime().exec("java -cp lab1/src/main/java hr.fer.zemris.z2.Client lab1/temp/db.txt"));
            Runtime.getRuntime().exec("mkfifo lab1/temp/pipe-" + clients.get(i).pid() + "-Q");
            Runtime.getRuntime().exec("mkfifo lab1/temp/pipe-" + clients.get(i).pid() + "-A");
            Runtime.getRuntime().exec("mkfifo lab1/temp/pipe-" + clients.get(i).pid() + "-DB");
        }


        sleep(2000);

        String clientPids = clients.stream().map(Process::pid).map(String::valueOf).collect(joining(" "));
        System.out.println(clientPids);
        clients.stream().map(e -> (new OutputStreamWriter(e.getOutputStream()))).forEach(e -> sendMessage(clientPids, e));
        clients.add(db);
        var clientReadersOut = clients.stream().map(e -> new BufferedReader(new InputStreamReader(e.getInputStream()))).collect(toList());
        var clientReadersErr = clients.stream().map(e -> new BufferedReader(new InputStreamReader(e.getErrorStream()))).collect(toList());

        new Thread(() -> {
            new Scanner(System.in).nextLine();
            alive = false;
            clients.stream().map(e -> (new OutputStreamWriter(e.getOutputStream()))).forEach(e -> sendMessage("DIE", e));
        }).start();

        while (alive) {
            for (var reader : clientReadersOut)
                while (reader.ready())
                    System.out.println(reader.readLine());
            for (var reader : clientReadersErr)
                while (reader.ready())
                    System.err.println(reader.readLine());
        }
    }

    private static void sendMessage(String message, Writer e) {
        try {
            e.write(message + "\n");
            e.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static void cleanTemp(File folder) {
        stream(requireNonNull(folder.listFiles())).forEach(File::delete);
    }
}
