package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Mastermind {
    private static ArrayList<String> couleur = new ArrayList<>();
    private static ArrayList<String> code = new ArrayList<>();
    private static HashMap<String, String> indice = new HashMap<>();
    private static int nbEssai;
    private static Fichier file;

    Mastermind() throws FileNotFoundException {
        file = new Fichier("C:\\projetMasterMind\\server\\src\\com\\company\\text.txt");

        couleur.add("rouge");
        couleur.add("jaune");
        couleur.add("bleu");
        couleur.add("vert");
        couleur.add("noir");
        couleur.add("blanc");

        indice.put("OK", "vert");
        indice.put("POK", "orange");
        indice.put("NOK", "rouge");

        nbEssai = 0;
    }

    public void generateNewCode(int difficulty) {
        int tabSizeCouleur = couleur.size();
        if (tabSizeCouleur != 0) code.clear();
        int res = 0;

        for (int i = 0; i < difficulty; i++) {
            res = (int)(Math.random() * (tabSizeCouleur));
            code.add(couleur.get(res));
        }
    }

    public ArrayList<String> getCode() {
        return code;
    }

    public ArrayList<String> getCouleur() {
        return couleur;
    }

    public ArrayList<String> codeClient(ArrayList<String> codeClient, Boolean useCount) {
        ArrayList<String> codeIndice = new ArrayList<>();
        HashMap<String, Integer> presenceCouleurCode = nbPresenceCoucleurCode();
        HashMap<String, Integer> presenceCouleurCodeClient = nbPresenceCoucleurCodeClient(codeClient);
        if (useCount) nbEssai++;

        for (int i = 0; i < codeClient.size(); i++) {
            if (codeClient.get(i).equals(code.get(i))) {
                presenceCouleurCode.replace(codeClient.get(i), presenceCouleurCode.get(codeClient.get(i)) - 1);
            }
        }

        for (int i = 0; i < codeClient.size(); i++) {
            if (codeClient.get(i).equals(code.get(i))) codeIndice.add(indice.get("OK"));
            else if (code.contains(codeClient.get(i)) && presenceCouleurCode.get(codeClient.get(i)) > 0) {
                codeIndice.add(indice.get("POK"));
                presenceCouleurCode.replace(codeClient.get(i), presenceCouleurCode.get(codeClient.get(i)) - 1);
            }
            else codeIndice.add(indice.get("NOK"));

        }
        return codeIndice;
    }

    public HashMap<String, Integer> nbPresenceCoucleurCode() {
        HashMap<String, Integer> presenceCouleur = new HashMap<>();

        for (String s : couleur) presenceCouleur.put(s, 0);
        for (String s : code) presenceCouleur.replace(s, presenceCouleur.get(s) + 1);

        return presenceCouleur;
    }

    private HashMap<String, Integer> nbPresenceCoucleurCodeClient(ArrayList<String> codeClient) {
        HashMap<String, Integer> presenceCouleur = new HashMap<>();

        for (String s : couleur) presenceCouleur.put(s, 0);
        for (String s : codeClient) presenceCouleur.replace(s, presenceCouleur.get(s) + 1);

        return presenceCouleur;
    }

    public boolean isVictory(ArrayList<String> code) {
        Boolean victory = true;

        for (String s : code) if (!s.equals(indice.get("OK"))) {
            victory = false;
            break;
        }

        return victory;
    }

    public int getNbEssai() {
        return nbEssai;
    }

    public void resetEssai() {
        nbEssai = 0;
    }

    public void save(String saveName, int difficulty) {
        System.out.println("test");
        file.writeSave(saveName, difficulty, code);
    }

    public ArrayList<String[]> getSaves() throws IOException {
        String str = file.lire();
        String[] arrStr = str.split("\n");
        ArrayList<String[]> arrs = new ArrayList<>();

        for (String s : arrStr) {
            arrs.add(s.split("[|]"));
        }
        return arrs;
    }
}
