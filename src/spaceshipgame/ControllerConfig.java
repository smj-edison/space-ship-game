package spaceshipgame;

public class ControllerConfig {
	public int turnLeftKey;
	public int turnRightKey;
	public int accelerateKey;
	public int shootKey;
	
	public ControllerConfig(int turnLeftKey, int turnRightKey, int accelerateKey, int shootKey) {
		this.turnLeftKey = turnLeftKey;
		this.turnRightKey = turnRightKey;
		this.accelerateKey = accelerateKey;
		this.shootKey = shootKey;
	}
}
