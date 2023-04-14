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
import java.util.*;
import java.awt.*;

public class PongGame {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final int PADDLE_HEIGHT = 60;
	public static final int PADDLE_WIDTH = 10;
	public static final int BALL_RADIUS = 10;

	public Point ballPosition;
	public Point[] paddlePositions;
	private Point ballVelocity;
	private int ballSpeed;

	private long startTime;

	private boolean isRunning;
	private int winner;

	public PongGame() {
		ballPosition = new Point(WIDTH / 2, HEIGHT / 2);
		ballVelocity = new Point();
		paddlePositions = new Point[] { new Point(PADDLE_WIDTH * 2, HEIGHT / 2 - PADDLE_HEIGHT / 2),
				new Point(WIDTH - PADDLE_WIDTH * 2, HEIGHT / 2 - PADDLE_HEIGHT / 2) };
		ballSpeed = 1;
		isRunning = false;
	}

	public void update() {
		if (!isRunning)
			return;// doesnt load physics engine till running phase

		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - startTime;

		if (deltaTime > 5) {
			ballPosition.x += ballVelocity.x;
			ballPosition.y += ballVelocity.y;
			checkBallPaddleCollision();
			checkBallWallCollision();

			startTime = currentTime;
		}
	}

	private void checkBallPaddleCollision() {
		for (int i = 0; i <= 1; i++) {
			if (ballPosition.x - BALL_RADIUS < paddlePositions[i].x + PADDLE_WIDTH
					&& ballPosition.x + BALL_RADIUS > paddlePositions[i].x
					&& ballPosition.y - BALL_RADIUS < paddlePositions[i].y + PADDLE_HEIGHT
					&& ballPosition.y + BALL_RADIUS > paddlePositions[i].y) {
				ballVelocity.x *= -1;
				ballVelocity.y = (int) ((ballPosition.y - paddlePositions[i].y - PADDLE_HEIGHT / 2) * 0.1);
				if (ballVelocity.y > ballSpeed) {
					ballVelocity.y = ballSpeed;
				} else if (ballVelocity.y < -ballSpeed) {
					ballVelocity.y = -ballSpeed;
				}
			}
		}
	}

	private void checkBallWallCollision() {
		if (ballPosition.y - BALL_RADIUS < 0 || ballPosition.y + BALL_RADIUS > HEIGHT) {
			ballVelocity.y *= -1;
		}

		if (ballPosition.x - BALL_RADIUS < 0) {
			winner = 1;
			isRunning = false;
		} else if (ballPosition.x + BALL_RADIUS > WIDTH) {
			winner = 0;
			isRunning = false;
		}
	}

	public void movePaddle(int paddleNumber, int paddlePositionY) {
		paddlePositions[paddleNumber].y = paddlePositionY;
	}

	public void startGame() {
		ballPosition.x = WIDTH / 2;
		ballPosition.y = HEIGHT / 2;
		ballVelocity.x = (new Random().nextBoolean()) ? ballSpeed : -ballSpeed;
		ballVelocity.y = (new Random().nextBoolean()) ? ballSpeed : -ballSpeed;
		startTime = System.currentTimeMillis();
		winner = -1;
		isRunning = true;
	}

	public String getBallPositionAsString() {
		return ballPosition.x + " " + ballPosition.y;
	}

	public void setBallPosition(int x, int y) {
		this.ballPosition.x = x;
		this.ballPosition.y = y;
	}

	public int getWinner() {
		return winner;
	}

	public boolean isRunning() {
		return isRunning;
	}
}
