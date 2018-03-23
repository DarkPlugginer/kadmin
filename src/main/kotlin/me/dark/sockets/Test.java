/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 21/03/18 16:32
 */

package me.dark.sockets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Test {

    public static void main(String[] args) {
        /*try {
            Socket socket = new Socket("35.199.71.111", 25585);
            System.out.println(socket.isConnected());
            new PrintStream(socket.getOutputStream()).println("*ban joseph");
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }*/

        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        JsonParser jsonParser = new JsonParser();
        String[] members = {"joa", "aif", "dsa"};
        for (String member : members) {
            array.add(jsonParser.parse(member));
        }
        object.add("membros", array);

        System.out.println(object.toString());
    }
}
