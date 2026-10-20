package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    // nombre (entier ou décimal, signe optionnel) + un seul opérateur + nombre
    private static final Pattern OPERATION = Pattern.compile(
            "^\\s*([-+]?\\d+(?:\\.\\d+)?)\\s*([-+*/])\\s*([-+]?\\d+(?:\\.\\d+)?)\\s*$");

    public static void main(String[] args) throws IOException {
        System.out.println("Je suis un client pas encore connecté...");
        Socket socket = new Socket("localhost", 1234);
        System.out.println("Je suis un client connecté");

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
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

            // Envoi des 3 éléments : opérande 1, opérateur, opérande 2
            dos.writeUTF(m.group(1));
            dos.writeUTF(m.group(2));
            dos.writeUTF(m.group(3));

            String resultat = dis.readUTF();
            System.out.println("Résultat reçu du serveur : " + resultat);
        }

        dos.writeUTF("FIN");
        socket.close();
        sc.close();
        System.out.println("Sockets fermés côté client");
    }
}