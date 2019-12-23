package spaceshipgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import spaceshipgame.util.MathHelpers;

public class Game {
	boolean welcomeMessageOn = true;
	
	private long currentUID = 0;

	ArrayList<Spaceship> ships = new ArrayList<>();
	ArrayList<Bullet> bullets = new ArrayList<>();
	ArrayList<Upgrade> upgrades = new ArrayList<>();

	static Map<UpgradeType, Float> upgradeWeights = new HashMap<>();

	ControllerConfig default_controls = new ControllerConfig(37, 39, 38, 32);
	Spaceship player1;
	Spaceship player2;

	boolean[] keys = new boolean[512];

	PGraphics screenLeft;
	PGraphics minimapLeft;
	
	PGraphics screenRight;
	PGraphics minimapRight;

	static {
		upgradeWeights.put(UpgradeType.BIGGER_BULLETS_1, 1F); // TODO: adjust the weights
		upgradeWeights.put(UpgradeType.BIGGER_BULLETS_2, 1F);
		upgradeWeights.put(UpgradeType.BIGGER_BULLETS_3, 1F);
		upgradeWeights.put(UpgradeType.GHOST, 0F);
		upgradeWeights.put(UpgradeType.HOMING, 1F);
		upgradeWeights.put(UpgradeType.HEAL, 0.3F);
		upgradeWeights.put(UpgradeType.LASER, 0F);
		upgradeWeights.put(UpgradeType.MACHINE_GUN_1, 1F);
		upgradeWeights.put(UpgradeType.MACHINE_GUN_2, 1F);
		upgradeWeights.put(UpgradeType.MACHINE_GUN_3, 1F);

		// normalize weights
		float sum = 0;

		for(float value : upgradeWeights.values()) {
			sum += value;
		}

		for(Map.Entry<UpgradeType, Float> i : upgradeWeights.entrySet()) {
			upgradeWeights.put(i.getKey(), i.getValue() / sum);
		}
	}

	public Game() {
		
	}

	public void init(Main main) {
		main.frameRate(Constants.FPS);
		player1 = new Spaceship(getNewUID(), main, main.width / 2, main.height / 2, new ControllerConfig(65, 68, 87, 16), 1, main.color(255, 0, 0), "Mason");
		player2 = new Spaceship(getNewUID(), main, main.width / 2, main.height / 2, default_controls, 1, main.color(0, 0, 255), "Sammy");
		ships.add(player1);
		ships.add(player2);
		
		screenLeft = main.createGraphics(main.width / 2, main.height);
		screenRight = main.createGraphics(main.width / 2, main.height);
		
		minimapLeft = main.createGraphics(Constants.MINIMAP_WIDTH, Constants.MINIMAP_HEIGHT);
		minimapRight = main.createGraphics(Constants.MINIMAP_WIDTH, Constants.MINIMAP_HEIGHT);
	}
	
	private void drawGame(PGraphics ctx, PVector center) {
		ctx.beginDraw();
		ctx.pushMatrix();
		ctx.translate(-center.x + ctx.width / 2, -center.y + ctx.height / 2);

		// draw game circle
		ctx.background(Constants.OUTSIDE_ARENA_BACKGROUND);
		ctx.fill(0);
		ctx.ellipse(0, 0, Constants.PLAY_SIZE * 2, Constants.PLAY_SIZE * 2);


		for(int i = ships.size() - 1; i >= 0; i--) {
			Spaceship spaceship = ships.get(i); // use iterator

			spaceship.updateKeys(keys);
			spaceship.draw(ctx);
		}

		for(int i = bullets.size() - 1; i >= 0; i--) {
			ctx.fill(Constants.BULLET_COLOR);
			
			bullets.get(i).draw(ctx);
		}

		for(int i = upgrades.size() - 1; i >= 0; i--) {
			Upgrade u = upgrades.get(i); // use iterator
			ctx.fill(255);

			if(u == null) {
				continue;
			}

			u.draw(ctx);
		}

		ctx.popMatrix();
		ctx.endDraw();
	}
	
	private void drawMinimap(PGraphics minimap, Spaceship ship, int screenWidth, int screenHeight) {
		minimap.beginDraw();
		minimap.pushMatrix();

		minimap.background(25);

		for(Upgrade u : upgrades) {
			minimap.fill(255, 255, 0);
			minimap.ellipse(((u.x - (ship.pos.x - (screenWidth / 2))) / Constants.MINIMAP_SCALE) + Constants.MINIMAP_WIDTH / 2,
					((u.y - (ship.pos.y - (screenHeight / 2))) / Constants.MINIMAP_SCALE) + Constants.MINIMAP_HEIGHT / 2, 10, 10);
		}

		minimap.translate(((ship.pos.x - (ship.pos.x - (screenWidth / 2))) / Constants.MINIMAP_SCALE) + Constants.MINIMAP_WIDTH / 2,
						  ((ship.pos.y - (ship.pos.y - (screenHeight / 2))) / Constants.MINIMAP_SCALE) + Constants.MINIMAP_HEIGHT / 2);
		minimap.rotate(ship.ang);

		minimap.fill(255, 0, 0);
		minimap.triangle(-5, 0, 0, -10, 5, 0);
		minimap.ellipse(0, 0, 10, 10);

		minimap.popMatrix();

		minimap.fill(0, 0, 255);
		for(Spaceship ts : ships) {
			minimap.translate(((ts.pos.x - (ship.pos.x - (screenWidth / 2))) / Constants.MINIMAP_SCALE) + Constants.MINIMAP_WIDTH / 2,
					((ts.pos.y - (ship.pos.y - (screenHeight / 2))) / Constants.MINIMAP_SCALE) + Constants.MINIMAP_HEIGHT / 2);
			minimap.rotate(ts.ang);

			if(ts != ship) {
				minimap.triangle(-5, 0, 0, -10, 5, 0);
				minimap.ellipse(0, 0, 10, 10);
			}

			minimap.resetMatrix();
		}

		minimap.endDraw();
	}
	
	int frameCount = 0;
	
	private void update(Game game) {
		frameCount++;
		
		if(frameCount % 10 == 0) {
			Constants.PLAY_SIZE -= 0.000001;
		}
		
		Constants.PLAY_SIZE = Math.max(50, Constants.PLAY_SIZE);
		
		if(Math.random() * 50 <= 1) {
			float[] upgradeLocation = MathHelpers.randomInCircle(0, 0, Constants.PLAY_SIZE);

			int rand = weightedRandom(upgradeWeights);
			UpgradeType upgradeType = UpgradeType.LASER;

			int i = 0;

			for(Map.Entry<UpgradeType, Float> val : upgradeWeights.entrySet()) {
				if(i == rand) {
					upgradeType = val.getKey();
				}

				i++;
			}

			upgrades.add(new Upgrade(getNewUID(), upgradeLocation[0], upgradeLocation[1], upgradeType));
		}

		for(int i = ships.size() - 1; i >= 0; i--) {
			Spaceship spaceship = ships.get(i); // use iterator

			if(spaceship.life <= 0) {
				ships.remove(i);
				continue;
			}

			spaceship.updateKeys(keys);
			spaceship.update(game);
		}

		bullet_loop: for(int i = bullets.size() - 1; i >= 0; i--) {
			Bullet b = bullets.get(i); // use itrator

			if(b == null) {
				continue;
			}

			for(int j = ships.size() - 1; j >= 0; j--) {
				Spaceship spaceship = ships.get(j);

				if(/* !ts.shield && */b.isColliding(spaceship)) {
					spaceship.life -= Constants.BULLET_DAMAGE_MULTIPLIER * b.damage;
					bullets.remove(i);
					continue bullet_loop;
				}
			}

			if(PApplet.mag(b.pos.x, b.pos.y) > Constants.PLAY_SIZE) {
				bullets.remove(i);
				continue;
			}

			b.update(game);
		}

		upgrade_loop: for(int i = upgrades.size() - 1; i >= 0; i--) {
			Upgrade u = upgrades.get(i); // use iterator

			if(u == null) {
				continue;
			}

			for(int j = ships.size() - 1; j >= 0; j--) {
				Spaceship ship = ships.get(j);

				if(u.shouldCollide(ship)) {
					ship.equipUpgrade(u);
					upgrades.remove(i);
					continue upgrade_loop;
				}
			}
		}
	}
	
	public void draw(PApplet ctx) {
		update(this);
		
		drawGame(screenLeft, player1.pos);
		drawMinimap(minimapLeft, player1, ctx.width / 2, ctx.height);
		
		drawGame(screenRight, player2.pos);
		drawMinimap(minimapRight, player2, ctx.width / 2, ctx.height);
		
		ctx.image(screenLeft, 0, 0);
		ctx.image(minimapLeft, 0, 0);
		ctx.image(screenRight, ctx.width / 2, 0);
		ctx.image(minimapRight, ctx.width / 2, 0);
		
		if(welcomeMessageOn) {
			ctx.fill(255, 255, 255);
			ctx.textSize(60);
			ctx.textAlign(PConstants.CENTER, PConstants.CENTER);
			ctx.text("WELCOME!\n Left player controls are: WAD and shift\n Right player controls are: Arrow keys and space bar", ctx.width / 2, ctx.height / 2);
		}
	}

	int weightedRandom(Map<UpgradeType, Float> spec) {
		float sum = 0;
		int i = 0;
		float r = (float) Math.random();

		for(Map.Entry<UpgradeType, Float> val : spec.entrySet()) {
			sum += val.getValue();

			if(r <= sum) {
				return i;
			}

			i++;
		}

		return 0;
	}

	void keyPressed(int keyCode) {
		welcomeMessageOn = false;
		keys[keyCode] = true;
	}

	void keyReleased(int keyCode) {
		keys[keyCode] = false;
	}

	public long getNewUID() {
		return currentUID++;
	}
}
