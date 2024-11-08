package com.napier.devops;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws IOException {
        // Create new Application
        App a = new App();

        if (args.length < 1) {
            //local
            a.connect("localhost:33060", 0);
        } else {
            //docker parameters passed from Dockerfile
            a.connect(args[0], Integer.parseInt(args[1]));
        }

        a.report1();

        // Disconnect from database
        a.disconnect();
    }
}