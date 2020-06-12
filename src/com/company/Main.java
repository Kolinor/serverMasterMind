package com.company;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
	// write your code here
        Server t = new Server();
//        t.connexion(6001, "192.168.1.131");
        t.connexion(6002, "192.168.1.25");
        t.run();

    }
}
