package com.company;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private int port;
    private String host;
    private ServerSocket server;
    private boolean isRunning;
    private int nbConnexionRunning;
    private int nbConnexionSim;
    static public ArrayList<Thread> threads = new ArrayList<Thread>();
    static public ArrayList<ClientProcessor> clients = new ArrayList<ClientProcessor>();
    private boolean change;
    static public ArrayList<String> logins = new ArrayList<String>();

    public Server(int nbConnexionSimultane) {
        this.isRunning = false;
        this.nbConnexionSim = nbConnexionSimultane;
        this.nbConnexionRunning = 0;
    }

    public boolean connexion(int port, String host) {
        this.port = port;
        this.host = host;

        boolean val;
        try {
            server = new ServerSocket(this.port, 100, InetAddress.getByName(this.host));
            val = true;
        } catch (IOException e) {
            e.printStackTrace();
            val = false;
        }
        return val;
    }

    public void run() {
        this.isRunning = true;
        this.change = false;
        Mastermind mastermind = new Mastermind();
//        mastermind.generateNewCode(4);
//        System.out.println("code : " + mm.getCode());
//        ArrayList<String> pp = new ArrayList<>();
//        pp.add("jaune");
//        pp.add("bleu");
//        pp.add("rouge");
//        pp.add("vert");
//
//        System.out.println("code envoyé par client : " + pp);
//        System.out.println("code renvoyé au client : " + mm.codeClient(pp));
//        System.out.println(mm.nbPresenceCoucleurCode());

        Thread t = new Thread(new Runnable() {
            public void run(){
                int saveConnexionRunning = nbConnexionRunning;
                while(isRunning) {
                    nbConnexionRunning = 0;
                    for (Thread thread : threads) {
                        if (thread.isAlive())
                            nbConnexionRunning++;
                    }
                    if(nbConnexionRunning != saveConnexionRunning) {
                        change = true;
                    }
                    saveConnexionRunning = nbConnexionRunning;
                    if(nbConnexionRunning < nbConnexionSim) {
                        try {
                            Socket client = server.accept();
                            nbConnexionRunning++;
                            System.out.println("Connexion cliente reçue.");
                            ClientProcessor tempClient = new ClientProcessor(client, mastermind);
                            threads.add(new Thread(tempClient));
                            clients.add(tempClient);
                            tempClient = null;
                            threads.get(threads.size()-1).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(change && nbConnexionRunning >= nbConnexionSim) {
                        System.out.println("Nombre de connexion max ateint !");
                        change = !change;
                    }

                }

                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    server = null;
                }
            }
        });

        t.start();
    }
}
