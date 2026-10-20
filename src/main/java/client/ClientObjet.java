package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Operation;

public class ClientObjet {

    // nombre (entier ou décimal, signe optionnel) + un seul opérateur + nombre
    private static final Pattern OPERATION = Pattern.compile(
            "^\\s*([-+]?\\d+(?:\\.\\d+)?)\\s*([-+*/])\\s*([-+]?\\d+(?:\\.\\d+)?)\\s*$");

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("Je suis un client(objet) pas encore connecté...");
        Socket socket = new Socket("localhost", 1234);
        System.out.println("Je suis un client connecté");

        // Même ordre que le serveur : flux de sortie d'abord, puis flux d'entrée
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Tapez une opération (ex. 55 * 25) ou 'fin' pour quitter : ");
            if (!sc.hasNextLine()) {
                break;
            }
            String saisie = sc.nextLine();
            if (saisie.trim().equalsIgnoreCase("fin")) {
                break;
            }

            // Validation de la syntaxe AVANT l'envoi au serveur
            Matcher m = OPERATION.matcher(saisie);
            if (!m.matches()) {
                System.out.println("Syntaxe invalide. Format attendu : <nombre> <+|-|*|/> <nombre>");
                continue;
            }

            // b) Le client envoie UN objet qui contient les 3 éléments de l'opération
            Operation op = new Operation(
                    Double.parseDouble(m.group(1)), m.group(2), Double.parseDouble(m.group(3)));
            oos.writeObject(op);
            oos.flush();

            // g) Le client lit et affiche le résultat
            String resultat = (String) ois.readObject();
            System.out.println("Résultat reçu du serveur : " + resultat);
        }

        oos.writeObject("FIN");
        oos.flush();
        socket.close();
        sc.close();
        System.out.println("Sockets fermés côté client");
    }
}