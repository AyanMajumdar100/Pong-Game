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

public class ClientHandler extends Thread {
	private DataInputStream in;
	private DataOutputStream out;

	private Point paddlePos;

	public ClientHandler(Socket client, Point paddlePos) throws IOException {
		this.in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
		this.out = new DataOutputStream(client.getOutputStream());

		this.paddlePos = paddlePos;
	}

	public void sendData(String data) {
		try {
			out.writeUTF(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendData(int data) {
		try {
			out.writeInt(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Point getPaddlePos() {
		return paddlePos;// returns paddle position received from client down below
	}

	public void run() {// run threads that receive data from clients
		while (true) {
			String rcv = "";
			try {
				rcv = in.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (rcv.startsWith("PADDLEPOS")) {
				String split = rcv.substring(10);
				paddlePos.y = Integer.parseInt(split);
			}
		}
	}
}

