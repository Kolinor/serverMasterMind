package com.company;

import java.io.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;

import static com.company.Server.*;

public class ClientProcessor implements Runnable {

    private Socket sock;
    private BufferedInputStream reader;
    public String login;
    public int difficulty;
    private PrintStream ecritureEcran;
    static public String toMessage;
    static public String toLogin;
    private Mastermind mastermind;
    private ArrayList<String> retourClient;
    boolean selectDifficulty;
    ArrayList<String[]> arrSave;

    public ClientProcessor(Socket pSock, Mastermind mastermind){
        this.mastermind = mastermind;
        sock = pSock;
        reader = null;
        retourClient = new ArrayList<>();
        selectDifficulty = true;
        arrSave = new ArrayList<>();
    }

    //Le traitement lancé dans un thread séparé
    public void run(){
        System.err.println("Traitement du client");
        boolean fisrtConnexion = true;
        boolean closeConnexion = false;

        while(!sock.isClosed()){

            try {
                ecritureEcran = new PrintStream(sock.getOutputStream(), true);
                reader = new BufferedInputStream(sock.getInputStream());
                InputStream inputStream = sock.getInputStream();
                if(fisrtConnexion) {
                    sendCouleur();
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
                if (response.length() > 3 && response.substring(0, 4).equals("stop")) clearGame();
                if(response.substring(0, 2).equals("!1")) {
                    if (selectDifficulty) {
                        difficulty = readDifficulty(response);
                        mastermind.generateNewCode(difficulty);
                        selectDifficulty = false;
                    } else {
                        game(response);
                    }
                } else if (response.substring(0, 2).equals("!3")) {
                    if (response.length() >= 7 && response.substring(3, 7).equals("load")) {
                        arrSave = mastermind.getSaves();
                        StringBuilder saveNames = new StringBuilder();

                        for (String[] strings : arrSave) saveNames.append(strings[0]).append("\n");
                        send("!saveName " + saveNames);

                    } else {
                        if (selectDifficulty) {
                            int dif = Integer.parseInt(parseResponse(response)[1]);
                            send("!start " + dif);
                            mastermind.generateNewCode(dif);
                            selectDifficulty = false;
                        } else {
                            game(response);
                        }
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
            if (logins.get(i).equals(login))
                logins.remove(i);
        }
    }

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
//        Fichier file = new Fichier();
//        file.ecrire(host, response, date, this.login);
        return response;
    }

//    private String read(String response) throws IOException{
//        InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();
//        String host = remote.getAddress().getHostAddress();
//
//        Date date = new Date();
//        int stream;
//        Fichier file = new Fichier();
//        file.ecrire(host, response, date, this.login);
//        return response;
//    }

    public int readDifficulty(String response) {
        return Integer.parseInt(response.substring(3));
    }

    public String getLogin() {
        return this.login;
    }

    public void sendCouleur() {
        ArrayList<String> tabCouleur = mastermind.getCouleur();
        String couleurs = "!couleur ";
        for (String s : tabCouleur) couleurs = couleurs + s + " ";
        send(couleurs);
    }

    private ArrayList<String> parseCouleur(String str) {
        ArrayList<String> couleur = new ArrayList<>();
        String[] arrOfStr = str.split(" ");

        for (int i = 1; i <= mastermind.getCode().size(); i++) {
            couleur.add(mastermind.getCouleur().get(Integer.parseInt(arrOfStr[i])));
        }

        return couleur;
    }

    public void sendIndiceCouleur(ArrayList couleurs) {
        String str = "!indice ";

        for (int i = 0; i < couleurs.size(); i++) {
            str += couleurs.get(i) + " ";
        }
        send(str);
    }

    private void clearGame() {
        mastermind.resetEssai();
        selectDifficulty = true;
    }

    private void game(String response) throws FileNotFoundException {
        String[] s = parseResponse(response);

        if (s.length >= 3 && s[1].equals("save")) {
            mastermind.save(s[2], Integer.parseInt(s[3]));
            return;
        }

        retourClient = mastermind.codeClient(parseCouleur(response), true);
        if (mastermind.isVictory(retourClient) && mastermind.getNbEssai() < 10) {
            send("!win " + mastermind.getNbEssai());
            clearGame();
        } else if (mastermind.getNbEssai() == 10) {
            send("!loose " + mastermind.getNbEssai());
            clearGame();
        } else {
            sendIndiceCouleur(retourClient);
        }
    }

    private String[] parseResponse(String response) {
        return response.split(" ");
    }
}