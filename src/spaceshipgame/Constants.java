package spaceshipgame;

public class Constants {
	public static final int FPS = 25;
	
	public static final int PLAY_SIZE = 2000;
	
	public static final float MAX_TURN_SPEED = 0.8f;
	public static final int MAX_LIFE = 700;
	public static final float DEFAULT_RELOAD_TIME = 0.3f;
	public static final float SHIP_RADIUS = 20;
	public static final float TURN_ACCELERATION = 0.3f; //in radians
	public static final float MOVE_ACCELERATION = 250;
	public static final float MAX_SPEED = 2500;
	
	public static final int UPGRADE_DIAMETER = 50;	
	
	public static final float BULLET_SIZE_MULTIPLIER = 10;
	
	public static final float LASER_DAMAGE_PER_SECOND = 500000;
	
	public static final int OUTSIDE_ARENA_BACKGROUND = 0xFF444444;
	public static final int BULLET_COLOR = 0xFFFFFFFF;
	public static final int BULLET_DAMAGE_MULTIPLIER = 5;
	
	public static final int MINIMAP_WIDTH = 300;
	public static final int MINIMAP_HEIGHT = 200;
	
	public static final int MINIMAP_SCALE = 20;
	
	public static final float HOMING_SPEED = 10;
	
	public static final int SHIELD_DIAMETER = 150;
	
	public static Point[] getNewSpaceshipPoints() {
		return new Point[] {
			new Point(-16, 20),
			new Point(0, -20),
			new Point(16, 20)
		};
	}
	
	public static Point[] spaceshipPoints = getNewSpaceshipPoints();
}
