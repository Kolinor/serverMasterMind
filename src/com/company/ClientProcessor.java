package com.company;

import java.io.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

import static com.company.Server.*;

public class ClientProcessor implements Runnable {

    private Socket sock;
    private BufferedInputStream reader = null;
    public String login;
    public int difficulty;
    private PrintStream ecritureEcran;
    static public String toMessage;
    static public String toLogin;
    private Mastermind mastermind;

    public ClientProcessor(Socket pSock, Mastermind mastermind){
        this.mastermind = mastermind;
        sock = pSock;
    }

    //Le traitement lancé dans un thread séparé
        public void run(){
        System.err.println("Traitement du client");
        boolean fisrtConnexion = true;
        boolean closeConnexion = false;
        boolean selectDifficulty = true;

        while(!sock.isClosed()){

            try {
                ecritureEcran = new PrintStream(sock.getOutputStream(), false);
                reader = new BufferedInputStream(sock.getInputStream());
                InputStream inputStream = sock.getInputStream();

                if(fisrtConnexion) {
                    login = read();
                    logins.add(login);
                    fisrtConnexion = false;
                }

                System.out.println(mastermind.getCode());
                String response = read();
                InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();

                String console = "";
                console = "/" + remote.getAddress().getHostAddress() + " ("+ this.login + ")" +  ">" + response;
                System.out.println("\n" + console);

                if(response.substring(0, 2).equals("!1")) {
                    if (selectDifficulty) {
                        difficulty = readDifficulty(response);
                        mastermind.generateNewCode(difficulty);
                        selectDifficulty = false;
                    }

                }
//                else {
//                    send(response);
//                }


                if(response.equals("quit".toLowerCase())){
                    send("Connexion closed");
                    System.err.println("Connexion closed ");
                    deconnexionLogin();
                    reader = null;
                    sock.close();
                    break;
                }
            } catch(SocketException e){
                deconnexionLogin();
                System.err.println("Connexion interrompu ! ");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deconnexionLogin() {
        for (int i = 0; i < logins.size(); i++) {
            if(logins.get(i).equals(login))
                logins.remove(i);
        }
    }

    /*private void speakTo(String login, String message) {
        if(login.equals(this.login)) {
            send(login + "> " +message);
        }
    }*/

    private void send(String message) {
        ecritureEcran.println(message);
        ecritureEcran.flush();
    }

    private String read() throws IOException{
        String response = "";
        InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();
        String host = remote.getAddress().getHostAddress();

        Date date = new Date();
        int stream;
        byte[] b = new byte[4096];
        stream = reader.read(b);
        response = new String(b, 0, stream);
        Fichier file = new Fichier();
        file.ecrire(host, response, date, this.login);
        return response;
    }

    private String read(String response) throws IOException{
        InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();
        String host = remote.getAddress().getHostAddress();

        Date date = new Date();
        int stream;
        Fichier file = new Fichier();
        file.ecrire(host, response, date, this.login);
        return response;
    }

    public int readDifficulty(String response) {
        return Integer.parseInt(response.substring(3));
    }

    public String getLogin() {
        return this.login;
    }

    public void p(String p) {
        System.out.println(p);
    }
}