package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import model.Operation;

public class ServerObjet {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket socketServeur = new ServerSocket(1234);
        System.out.println("Je suis un serveur(objet) en attente de la connexion d'un client");

        Socket socket = socketServeur.accept();
        System.out.println("Un client est connecté");

        // On crée le flux de sortie en premier (et on le vide) pour éviter un blocage
        // entre les en-têtes des deux flux d'objets.
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        try {
            while (true) {
                // c) Le serveur reçoit l'objet
                Object recu = ois.readObject();

                // Le client envoie la chaîne "FIN" pour terminer
                if (recu instanceof String && ((String) recu).equalsIgnoreCase("FIN")) {
                    System.out.println("Le client a envoyé FIN : fin de la communication");
                    break;
                }

                // d) Il récupère les données de l'objet reçu
                Operation op = (Operation) recu;
                System.out.println("Objet reçu : " + op);

                // e) Il effectue le calcul demandé
                String resultat = calculer(op);

                // f) Il renvoie le résultat au client
                oos.writeObject(resultat);
                oos.flush();
                System.out.println("Résultat envoyé : " + resultat);
            }
        } catch (EOFException e) {
            System.out.println("Le client s'est déconnecté brutalement");
        }

        socket.close();
        socketServeur.close();
        System.out.println("Sockets fermés côté serveur");
    }

    /** Calcule l'opération ; renvoie le résultat ou un message "ERREUR : ...". */
    static String calculer(Operation op) {
        double a = op.getOperande1();
        double b = op.getOperande2();
        double r;

        switch (op.getOperateur().trim()) {
            case "+": r = a + b; break;
            case "-": r = a - b; break;
            case "*": r = a * b; break;
            case "/":
                if (b == 0) {
                    return "ERREUR : division par zéro";
                }
                r = a / b;
                break;
            default:
                return "ERREUR : opérateur inconnu (" + op.getOperateur() + ")";
        }

        // Affiche 1375 plutôt que 1375.0 quand le résultat est entier
        if (r == Math.rint(r) && Math.abs(r) < 1e15) {
            return String.valueOf((long) r);
        }
        return String.valueOf(r);
    }
}