# Pong Game

## Overview

The Pong Game is a networked version of the classic Pong game, developed in Java. The game uses TCP for client-server communication, with a graphical interface built using Swing. The game supports two players, each controlling a paddle to keep the ball in play and score points by getting the ball past the opponent's paddle. 

## Tech Stack

- **Java**: Core programming language
- **Swing**: For graphical user interface
- **TCP/IP**: For network communication

## Project Structure

The project consists of the following main components:

- **Client**: Handles key events and rendering, maintains socket connection.
- **Server**: Listens for client connections, maintains game state, handles client communication.
- **PongGame**: Manages game state and physics.
- **ClientHandler**: Manages data exchange between server and clients.

### Client
- **Client.java**: Sets up the JFrame window, handles key events, maintains socket connection, sends and receives paddle and ball positions.
- **Paddle.java**: Represents the paddle, with methods for movement and rendering.
- **Ball.java**: Represents the ball, with methods for movement and rendering.

### Server
- **Server.java**: Sets up ServerSocket, accepts client connections, maintains game state, starts the game, updates game state.
- **PongGame.java**: Manages ball and paddle positions, handles collisions, tracks running state, determines the winner.
- **ClientHandler.java**: Manages data exchange with a client, updates paddle position based on client input.

## Environment Variables

No specific environment variables are required for this project.

## Running the Project

1. **Clone the repository:**
    ```bash
    git clone https://github.com/AyanMajumdar100/Pong-Game.git
    cd Pong-Game
    ```

2. **Compile the project:**
    ```bash
    javac *.java
    ```

3. **Run the server:**
    ```bash
    java Server
    ```

4. **Run the client:**
    ```bash
    java Client
    ```

Repeat step 4 on another machine or terminal to connect a second client.

## How to Play

1. Start the server by running the `Server` class.
2. Start two clients by running the `Client` class on different machines or terminals.
3. The server will wait until both clients are connected.
4. Use `W` and `S` keys to move your paddle up and down.
5. The game will start automatically after both clients are connected.
6. Keep the ball in play by hitting it with your paddle.
7. Score points by getting the ball past your opponent's paddle.
8. The game restarts after a winner is determined.

## Game States

- **Waiting**: The game waits for both clients to connect.
- **Playing**: The game is in progress.
- **Game Over**: A winner is determined, and the game restarts after a short delay.

## Classes and Methods

### Client

#### Client.java

- `public void keyPressed(KeyEvent e)`: Handles key press events for paddle movement.
- `public void keyReleased(KeyEvent e)`: Handles key release events.
- `public void run()`: Manages game rendering and updates display based on game state.

#### Paddle.java

- `public void move()`: Moves the paddle based on current input.
- `public void draw(Graphics g)`: Renders the paddle.

#### Ball.java

- `public void move()`: Moves the ball and checks for collisions.
- `public void draw(Graphics g)`: Renders the ball.

### Server

#### Server.java

- `public void run()`: Listens for client connections, starts the game, and updates game state.

#### PongGame.java

- `public void update()`: Updates ball and paddle positions, checks for collisions, determines winner.
- `public String getBallPosition()`: Returns the ball's position as a string.
- `public void setBallPosition(String position)`: Sets the ball's position.
- `public String getWinner()`: Returns the winner of the game.

#### ClientHandler.java

- `public void run()`: Manages data exchange with the client.
- `public void sendData(String data)`: Sends data to the client.
- `public int getPaddlePos()`: Returns the paddle position.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add new feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.


---

Feel free to reach out if you have any questions or need further assistance with the project. Enjoy playing the Pong Game!
