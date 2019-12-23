package spaceshipgame;

import processing.core.PApplet;

public class Main extends PApplet {
	public Game game;
	
	
	public static void main(String[] args) {
		PApplet.main("spaceshipgame.Main");
	}

	public void settings() {
		fullScreen(P2D);
	}

	public void setup() {
		game = new Game();
		game.init(this);
	}

	public void draw() {
		game.draw(this);
	}
	
	public void keyPressed() {
		game.keyPressed(keyCode);
	}
	
	public void keyReleased() {
		game.keyReleased(keyCode);
	}
}
