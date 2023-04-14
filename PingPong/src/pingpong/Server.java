/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pingpong;

/**
 *
 * @author ayanm
 */
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	public static void main(String args[]) {
		try {
			Server server = new Server();// object of Server class
			server.findClients();// Func to wait for 2 clients
			server.startGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final int PORT = 5000;
	private final int TIME_DELAY_MS = 10000;
	private ServerSocket server;

	private ClientHandler[] clients;// array of objects

	private PongGame gameObj;// holds game state data

	public Server() throws IOException {
		server = new ServerSocket(PORT);
	}

	public void findClients() throws IOException {
		ArrayList<Socket> clientlist = new ArrayList<>();// more flexible than traditional array

		System.out.println("Waiting for 1st Client...");
		clientlist.add(server.accept());
		System.out.println("Client 1 connected");

		System.out.println("Waiting for 2nd Client...");
		clientlist.add(server.accept());
		System.out.println("Client 2 connected...GAME STARTING");

		gameObj = new PongGame();

		clients = new ClientHandler[clientlist.size()];
		for (int i = 0; i < clientlist.size(); i++) {
			clients[i] = new ClientHandler(clientlist.get(i), gameObj.paddlePositions[i]);// parameterized constructor
			clients[i].sendData(i);// sending client index &initial paddlepos to respective clients
		}
	}

	public void startGame() {// only called once when 2 clients are connected
		for (ClientHandler client : clients)
			client.sendData("WAIT");// initial waiting countdown

		try {
			Thread.sleep(TIME_DELAY_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (ClientHandler client : clients)//
			client.start();// start client threads since CH extends to thread
		gameObj.startGame();
		run();
	}

	public void run() {
		while (true) {
			gameObj.update();
			if (!gameObj.isRunning()) {
				for (ClientHandler client : clients)
					client.sendData("WINNER " + gameObj.getWinner());

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (ClientHandler client : clients)
					client.sendData("WAIT");

				try {
					Thread.sleep(TIME_DELAY_MS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				gameObj.startGame();
			}

			ClientHandler anotherClient = clients[1];
			for (int i = 0; i < clients.length; i++) {
				ClientHandler currentClient = clients[i];

				currentClient.sendData("BALLPOS " + gameObj.getBallPositionAsString());

				Point paddlePos = currentClient.getPaddlePos();
				gameObj.movePaddle(i, paddlePos.y);// update paddle position

				anotherClient.sendData("PADDLEPOS " + gameObj.paddlePositions[i].y);// sends paddle pos of current
																					// client to the other client

				anotherClient = currentClient;
			}
		}
	}
}
