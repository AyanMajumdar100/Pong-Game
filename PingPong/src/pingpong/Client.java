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
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JPanel implements KeyListener {
	// private static final long serialVersionUID = 1L;

	public static void main(String args[]) {

		Client c = new Client();

		JFrame frame = new JFrame("Pong Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(c);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		c.startThreads();
	}

	private static final int WIDTH = 800, HEIGHT = 600;
	private final int TIME_DELAY_MS = 10000;

	private PongGame gameView;
	private Thread commThread;
	private Thread gameThread;

	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	private int clientIndex;
	private int opponentIndex;

	private boolean isWaiting;
	private boolean isPlaying;
	private boolean isGameOver;

	private int timeCount;
	private long startTime;

	private int winner;

	public Client() {
		super();
		setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		setFocusable(true);

		this.isWaiting = false;
		this.isPlaying = false;
		this.isGameOver = false;

		this.winner = -1;

		gameView = new PongGame();
		commThread = new Thread(() -> {// lambda func for thread communication or else write run func
			try {
				socket = new Socket("127.0.0.1", 5000);
				in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				out = new DataOutputStream(socket.getOutputStream());

				clientIndex = in.readInt();
				System.out.println("Connected to server with index: " + clientIndex);
				opponentIndex = clientIndex == 0 ? 1 : 0;

				while (true) {
					String rcv = in.readUTF();

					if (rcv.equals("WAIT")) {
						isWaiting = true;
						timeCount = TIME_DELAY_MS / 1000;
						startTime = System.currentTimeMillis();
					}

					if (rcv.startsWith("BALLPOS")) {
						String split[] = rcv.substring(8).split(" ");
						gameView.setBallPosition(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
					}
					if (rcv.startsWith("PADDLEPOS")) {
						String split = rcv.substring(10);
						gameView.movePaddle(opponentIndex, Integer.parseInt(split));
					}

					if (rcv.startsWith("WINNER")) {
						String split = rcv.substring(7);
						winner = Integer.parseInt(split);
						isGameOver = true;
						isPlaying = false;
					}

					out.writeUTF("PADDLEPOS " + gameView.paddlePositions[clientIndex].y);
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		gameThread = new Thread(() -> {// game thread run lamda func for rendering the game for every frame
			long startTime1 = System.currentTimeMillis();
			int fps = 120;
			while (true) {// frame limiter
				long currentTime1 = System.currentTimeMillis();

				if (currentTime1 - startTime1 >= 1000 / fps) {
					repaint();
					startTime1 = currentTime1;
				}
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		refreshScreen(g);

		if (!isWaiting && !isPlaying && !isGameOver) {
			renderWaitingForPlayers(g);
		}
		if (isWaiting) {
			renderTimer(g);
		}
		if (isPlaying) {
			renderGame(g);
		}
		if (isGameOver) {
			renderGameOver(g);
		}
	}

	private void refreshScreen(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
	}

	private void renderWaitingForPlayers(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.BOLD, 50));
		String toDisplay = "Waiting for Players...";
		g.drawString(toDisplay, 175, 300);
	}

	private void renderGame(Graphics g) {

		g.setColor(Color.YELLOW);
		g.setFont(new Font("Dialog", Font.BOLD, 30));
		g.drawString("Player 1", 25, 30);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Dialog", Font.BOLD, 10));
		g.drawString("Use W and S", 25, 55);

		g.setColor(Color.CYAN);
		g.setFont(new Font("Dialog", Font.BOLD, 30));
		g.drawString("Player 2", 668, 30);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Dialog", Font.BOLD, 10));
		g.drawString("Use W and S", 668, 55);

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(gameView.paddlePositions[0].x, gameView.paddlePositions[0].y, PongGame.PADDLE_WIDTH,
				PongGame.PADDLE_HEIGHT);
		g.fillRect(gameView.paddlePositions[1].x, gameView.paddlePositions[1].y, PongGame.PADDLE_WIDTH,
				PongGame.PADDLE_HEIGHT);
		g.setColor(Color.GREEN);
		g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
		g.setColor(Color.RED);
		g.fillOval(gameView.ballPosition.x - PongGame.BALL_RADIUS, gameView.ballPosition.y - PongGame.BALL_RADIUS,
				PongGame.BALL_RADIUS * 2, PongGame.BALL_RADIUS * 2);
	}

	private void renderTimer(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.BOLD, 100));

		String toDisplay = timeCount + "";

		FontMetrics fm = g.getFontMetrics();
		Rectangle2D stringBounds = fm.getStringBounds(toDisplay, g);

		g.drawString(toDisplay, (int) (WIDTH / 2 - stringBounds.getWidth() / 2),
				(int) (HEIGHT / 2));

		if (System.currentTimeMillis() - startTime >= 1000) {
			timeCount--;
			startTime = System.currentTimeMillis();
		}

		if (timeCount == 0) {
			isWaiting = false;
			isPlaying = true;
			isGameOver = false;
		}
	}

	private void renderGameOver(Graphics g) {
		String toDisplay = winner == clientIndex ? "You Win!" : "You Lose!";
		if (toDisplay.equals("You Win!"))
			g.setColor(Color.GREEN);
		if (toDisplay.equals("You Lose!"))
			g.setColor(Color.RED);
		g.setFont(new Font("Century Gothic", Font.BOLD, 30));
		FontMetrics fm = g.getFontMetrics();// matrix of the current font assigned to the graphics object
		Rectangle2D stringBounds = fm.getStringBounds(toDisplay, g);// turns string into a visual box to get width and
																	// height

		g.drawString(toDisplay, (int) (WIDTH / 2 - stringBounds.getWidth() / 2),
				(int) (HEIGHT / 3 - stringBounds.getHeight() / 2));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			if (gameView.paddlePositions[clientIndex].y > 0) {
				gameView.paddlePositions[clientIndex].y -= 10;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			if (gameView.paddlePositions[clientIndex].y < HEIGHT - PongGame.PADDLE_HEIGHT) {
				gameView.paddlePositions[clientIndex].y += 10;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void startThreads() {
		commThread.start();
		gameThread.start();
		try {
			commThread.join();
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}