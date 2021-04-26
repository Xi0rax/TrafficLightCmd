package org.xi0rax.trafficlight.viewmodel;

import org.xi0rax.trafficlight.util.Console;

public class TrafficView {
    private TrafficViewModel trafficViewModel;
    private boolean flag;

    public TrafficView() {
        this.trafficViewModel = new TrafficViewModel();
        this.flag = true;
    }

    public void waitForInput() {
        System.out.println("Welcome to traffic control system");
        System.out.println("Use >>help<< to get help about commands");
        while (flag) {
            System.out.print(">>>");
            String buffer = trafficViewModel.execute(Console.read());
            Console.write(buffer);
            if (buffer.equals("halted")) {
                this.flag = !this.flag;
            }
            if (buffer.equals("CLEAR")) {
                try {
                    if (System.getProperty("os.name").contains("Windows")) {
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    } else {
                        Runtime.getRuntime().exec("clear");
                    }
                } catch (java.io.IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
