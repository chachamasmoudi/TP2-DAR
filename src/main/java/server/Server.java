package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket socketServeur = new ServerSocket(1234);
        System.out.println("Je suis un serveur en attente de la connexion d'un client");

        Socket socket = socketServeur.accept();
        System.out.println("Un client est connecté");

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        try {
            while (true) {
                // Le client envoie les 3 éléments de l'opération, ou "FIN" pour terminer
                String op1 = dis.readUTF();
                if (op1.equalsIgnoreCase("FIN")) {
                    System.out.println("Le client a envoyé FIN : fin de la communication");
                    break;
                }
                String operateur = dis.readUTF();
                String op2 = dis.readUTF();
                System.out.println("Opération reçue : " + op1 + " " + operateur + " " + op2);

                String resultat = calculer(op1, operateur, op2);
                dos.writeUTF(resultat);
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
    static String calculer(String s1, String operateur, String s2) {
        double a, b;
        try {
            a = Double.parseDouble(s1.trim());
            b = Double.parseDouble(s2.trim());
        } catch (NumberFormatException e) {
            return "ERREUR : opérande invalide";
        }

        double r;
        switch (operateur.trim()) {
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
                return "ERREUR : opérateur inconnu (" + operateur + ")";
        }

        // Affiche 1375 plutôt que 1375.0 quand le résultat est entier
        if (r == Math.rint(r) && Math.abs(r) < 1e15) {
            return String.valueOf((long) r);
        }
        return String.valueOf(r);
    }
}