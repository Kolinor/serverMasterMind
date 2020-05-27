package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class Mastermind {
    private static ArrayList<String> couleur = new ArrayList<>();
    private static ArrayList<String> code = new ArrayList<>();
    private static HashMap<String, String> indice = new HashMap<>();

    Mastermind() {
        couleur.add("rouge");
        couleur.add("jaune");
        couleur.add("bleu");
        couleur.add("vert");
        couleur.add("noir");
        couleur.add("blanc");

        indice.put("OK", "vert");
        indice.put("POK", "orange");
        indice.put("NOK", "rouge");
    }

    public void generateNewCode() {
        int tabSizeCouleur = couleur.size();
        if (tabSizeCouleur != 0) code.clear();
        int res = 0;

        for (int i = 0; i < 4; i++) {
            res = (int)(Math.random() * (tabSizeCouleur));
//            code.add(couleur.get(res));

        }
        code.add("jaune");
        code.add("bleu");
        code.add("rouge");
        code.add("vert");
    }

    public ArrayList<String> getCode() {
        return code;
    }

    public ArrayList<String> codeClient(ArrayList<String> codeClient) {
        ArrayList<String> codeIndice = new ArrayList<>();
        HashMap<String, Integer> presenceCouleur = nbPresenceCoucleur();

        for (int i = 0; i < codeClient.size(); i++) {
            if (codeClient.get(i).equals(code.get(i))) codeIndice.add(indice.get("OK"));
            else if (code.contains(codeClient.get(i)) && presenceCouleur.get(codeClient.get(i)) > 0) codeIndice.add(indice.get("POK"));
            else codeIndice.add(indice.get("NOK"));
            presenceCouleur.replace(codeClient.get(i), presenceCouleur.get(codeClient.get(i)) - 1);
        }
        return codeIndice;
    }

    public HashMap<String, Integer> nbPresenceCoucleur() {
        HashMap<String, Integer> presenceCouleur = new HashMap<>();

        for (String s : couleur) presenceCouleur.put(s, 0);
        for (String s : code) presenceCouleur.replace(s, presenceCouleur.get(s) + 1);

        return presenceCouleur;
    }
}
