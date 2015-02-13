package mri.socket.tcp.thread;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServeurTCP {

    public static void main(String[] args) {
        int socketPort = 9999;
        ServerSocket serverSocket = null;
        Executor pool = Executors.newFixedThreadPool(2);//Pool de threads


        try {
            serverSocket = new ServerSocket(socketPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean run = true;
        while(run){
            try {

                Socket socket = serverSocket.accept();
                TraiteUnClient traiteUnClient = new TraiteUnClient(socket);
                //new Thread(traiteUnClient).start(); Exercice 4 - Question 3
                pool.execute(traiteUnClient);//Exercice 4 - Question 5
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void traiterSocketCliente(Socket socket){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(reader != null && writer != null){
            String clientName = null;
            try {
                clientName = avoirNom(reader);
                if(clientName == null){
                    envoyerMessage(writer, "BAD NAME");
                    return;
                }else{
                    envoyerMessage(writer, "NAME OK");
                }
             }
             catch (IOException e) {
                e.printStackTrace();
            }
            String s;
            try {
                while ((s = recevoirMessage(reader)) != null){
                    System.out.println(clientName + " > " + s);
                    envoyerMessage(writer, s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static BufferedReader creerReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter creerWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream());
    }

    public static String recevoirMessage(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    public static void envoyerMessage(PrintWriter printWriter, String message){
        printWriter.println(message);
        printWriter.flush();
    }

    public static String avoirNom(BufferedReader reader) throws IOException {
        String s = reader.readLine();
        if(!s.matches("NAME:.+"))
            return null;
        return s.substring(5);
    }
}
