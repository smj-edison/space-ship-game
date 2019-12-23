package spaceshipgame;

public class Point {
	public float x;
	public float y;

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void rotate(float angle) {
		float oldX = this.x;
		
		float n = (float) Math.cos(angle);
		float r = (float) Math.sin(angle);
		
		this.x = n * this.x - r * this.y;
		this.y = r * oldX + n * this.y;
	}
}