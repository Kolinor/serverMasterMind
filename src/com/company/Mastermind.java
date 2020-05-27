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
            code.add(couleur.get(res));

        }
//        code.add("jaune");
//        code.add("bleu");
//        code.add("rouge");
//        code.add("vert");
    }

    public ArrayList<String> getCode() {
        return code;
    }

    public ArrayList<String> codeClient(ArrayList<String> codeClient) {
        ArrayList<String> codeIndice = new ArrayList<>();
        HashMap<String, Integer> presenceCouleurCode = nbPresenceCoucleurCode();
        HashMap<String, Integer> presenceCouleurCodeClient = nbPresenceCoucleurCodeClient(codeClient);

        for (int i = 0; i < codeClient.size(); i++) {

            boolean test = presenceCouleurCodeClient.size() > presenceCouleurCode.size();
            if (codeClient.get(i).equals(code.get(i))) codeIndice.add(indice.get("OK"));
            else if (code.contains(codeClient.get(i)) && presenceCouleurCode.get(codeClient.get(i)) > 0 && test) codeIndice.add(indice.get("POK"));
            else codeIndice.add(indice.get("NOK"));
            presenceCouleurCode.replace(codeClient.get(i), presenceCouleurCode.get(codeClient.get(i)) - 1);
        }
        return codeIndice;
    }

    public HashMap<String, Integer> nbPresenceCoucleurCode() {
        HashMap<String, Integer> presenceCouleur = new HashMap<>();

        for (String s : couleur) presenceCouleur.put(s, 0);
        for (String s : code) presenceCouleur.replace(s, presenceCouleur.get(s) + 1);

        return presenceCouleur;
    }

    public HashMap<String, Integer> nbPresenceCoucleurCodeClient(ArrayList<String> codeClient) {
        HashMap<String, Integer> presenceCouleur = new HashMap<>();

        for (String s : couleur) presenceCouleur.put(s, 0);
        for (String s : codeClient) presenceCouleur.replace(s, presenceCouleur.get(s) + 1);

        return presenceCouleur;
    }
}
