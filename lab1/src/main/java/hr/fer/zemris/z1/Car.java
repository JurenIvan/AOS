package hr.fer.zemris.z1;

import java.util.Scanner;

import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.Thread.sleep;

public class Car {

    private static final boolean DIRECTION = random() > 0.5;
    private static final long SLEEP_TIME = 100 + round(random() * 1900);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        sleep(SLEEP_TIME);
        System.out.println("UPIT " + args[0] + " " + DIRECTION);

        System.err.println("Automobil " + args[0] + " čeka na prelazak preko mosta " + (DIRECTION ? "Lijevo" : "Desno"));
        scanner.nextLine();
        System.err.println("Automobil " + args[0] + " se popeo na most. " + (DIRECTION ? "Lijevo" : "Desno"));
        scanner.nextLine();
        System.err.println("Automobil " + args[0] + " je prešao most. " + (DIRECTION ? "Lijevo" : "Desno"));
    }
}
