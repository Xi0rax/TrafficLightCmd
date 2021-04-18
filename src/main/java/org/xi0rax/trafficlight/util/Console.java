package org.xi0rax.trafficlight.util;

import java.util.Scanner;

public class Console {
    public static void write(String out) {
        System.out.println(out);
    }

    public static String read() {
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }
}
