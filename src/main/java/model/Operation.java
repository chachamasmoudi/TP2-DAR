package model;

import java.io.Serializable;

/**
 * Objet échangé entre le client et le serveur.
 * Il contient les arguments nécessaires à l'opération : opérande 1, opérateur, opérande 2.
 * Il implémente Serializable pour pouvoir être transmis dans un ObjectOutputStream.
 */
public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    private double operande1;
    private String operateur;
    private double operande2;

    public Operation(double operande1, String operateur, double operande2) {
        this.operande1 = operande1;
        this.operateur = operateur;
        this.operande2 = operande2;
    }

    public double getOperande1() {
        return operande1;
    }

    public String getOperateur() {
        return operateur;
    }

    public double getOperande2() {
        return operande2;
    }

    @Override
    public String toString() {
        return operande1 + " " + operateur + " " + operande2;
    }
}