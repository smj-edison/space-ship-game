package spaceshipgame;

import processing.core.PGraphics;
import spaceshipgame.physics.TestOverlap;

class Upgrade implements UID {
	long uid;
	
	float x;
	float y;

	UpgradeType type;
	UpgradeCatagories cad;

	public Upgrade(long uid, float x, float y, UpgradeType type) {
		this.uid = uid;

		this.x = x;
		this.y = y;
		this.type = type;

		// Categorize the upgrade type in its respective catagorie
		switch(type) {
			case BIGGER_BULLETS_1:
				cad = UpgradeCatagories.BIGGER;
				break;
			case BIGGER_BULLETS_2:
				cad = UpgradeCatagories.BIGGER;
				break;
			case BIGGER_BULLETS_3:
				cad = UpgradeCatagories.BIGGER;
				break;
			case MACHINE_GUN_1:
				cad = UpgradeCatagories.FASTER;
				break;
			case MACHINE_GUN_2:
				cad = UpgradeCatagories.FASTER;
				break;
			case MACHINE_GUN_3:
				cad = UpgradeCatagories.FASTER;
				break;
			case LASER:
				cad = UpgradeCatagories.OTHER;
				break;
			case GHOST:
				cad = UpgradeCatagories.OTHER;
				break;
			case HEAL:
				cad = UpgradeCatagories.AUTO_DIE;
				break;
			case HOMING:
				cad = UpgradeCatagories.BULLET_CHANGE;
				break;
			case SHIELD:
				cad = UpgradeCatagories.OTHER;
				break;
		}
	}

	public void draw(PGraphics ctx) {
		ctx.fill(255);
		ctx.noStroke();
		ctx.ellipse(this.x, this.y, Constants.UPGRADE_DIAMETER, Constants.UPGRADE_DIAMETER);
		ctx.fill(0);

		switch(type) {
			case BIGGER_BULLETS_1:
				ctx.ellipse(this.x + 5, this.y, 20, 20);
				ctx.fill(0);
				ctx.textSize(15);
				ctx.text("1", this.x - 10, this.y);
				break;
			case BIGGER_BULLETS_2:
				ctx.ellipse(this.x + 5, this.y, 20, 20);
				ctx.fill(0);
				ctx.textSize(15);
				ctx.text("2", this.x - 10, this.y);
				break;
			case BIGGER_BULLETS_3:
				ctx.ellipse(this.x + 5, this.y, 20, 20);
				ctx.fill(0);
				ctx.textSize(15);
				ctx.text("3", this.x - 20, this.y);
				break;
			case MACHINE_GUN_1:
				ctx.ellipse(this.x + 5, this.y, 10, 10);
				ctx.ellipse(this.x + 12, this.y, 10, 10);
				ctx.ellipse(this.x + 19, this.y, 10, 10);
				ctx.fill(0);
				ctx.textSize(15);
				ctx.text("1", this.x - 10, this.y);
				break;
			case MACHINE_GUN_2:
				ctx.ellipse(this.x + 5, this.y, 10, 10);
				ctx.ellipse(this.x + 12, this.y, 10, 10);
				ctx.ellipse(this.x + 19, this.y, 10, 10);
				ctx.fill(0);
				ctx.textSize(15);
				ctx.text("2", this.x - 10, this.y);
				break;
			case MACHINE_GUN_3:
				ctx.ellipse(this.x + 5, this.y, 10, 10);
				ctx.ellipse(this.x + 12, this.y, 10, 10);
				ctx.ellipse(this.x + 19, this.y, 10, 10);
				ctx.fill(0);
				ctx.textSize(15);
				ctx.text("3", this.x - 20, this.y);
				break;
			case HOMING:
				ctx.triangle(this.x - 15, this.y + 15, this.x, this.y - 15, this.x + 15, this.y + 15);
				break;
			case HEAL:
				ctx.rect(this.x - 15, this.y - 5, 30, 10);
				ctx.rect(this.x - 5, this.y - 15, 10, 30);
				break;
			case LASER:
				ctx.triangle(this.x - 7, this.y + 15, this.x, this.y - 10, this.x + 7, this.y + 15);
				ctx.stroke(0);
				ctx.line(this.x, this.y - 5, this.x, this.y - 25);
				break;
			case GHOST:
				//TODO: unimplemented drawing for ghost
				break;
			case SHIELD:
				ctx.noFill();
				ctx.stroke(0);
				ctx.strokeWeight(10);
				ctx.ellipse(this.x, this.y, 35, 35);
			default:
				break;
		}
	}

	// returns whether spaceship `s` is colliding with this upgrade
	public boolean shouldCollide(Spaceship s) {
		Point[] spaceshipPoints = Constants.getNewSpaceshipPoints();

		return TestOverlap.polygonCircleRotateCollide(spaceshipPoints, s.pos.x, s.pos.y, Math.round(x), Math.round(y), Constants.UPGRADE_DIAMETER, s.ang);
	}

	//changes the properties of a spaceship according to the upgrade type
	public void equip(Spaceship s) {
		switch(type) {
			case BIGGER_BULLETS_1:
				s.bulletSize = 3;
				break;
			case BIGGER_BULLETS_2:
				s.bulletSize = 4;
				break;
			case BIGGER_BULLETS_3:
				s.bulletSize = 5;
				break;
			case MACHINE_GUN_1:
				s.reloadTime = 0.2f;
				break;
			case MACHINE_GUN_2:
				s.reloadTime = 0.1f;
				break;
			case MACHINE_GUN_3:
				s.reloadTime = 0.05f;
				break;
			case LASER:
				s.laser = true;
				break;
			case GHOST:
				s.ghost = true;
				break;
			case HOMING:
				s.homing = true;
				break;
			case HEAL:
				s.life = Math.min(Math.max(s.life + 20, 0), s.maxLife);
				break;
			case SHIELD:
				s.shield = true;
				s.shieldTimeLeft = Constants.SHIELD_TIMER;
				break;
		}
	}

	public void unequip(Spaceship s) {
		switch(type) {
			case BIGGER_BULLETS_1:
				s.bulletSize = 2;
				break;
			case BIGGER_BULLETS_2:
				s.bulletSize = 2;
				break;
			case BIGGER_BULLETS_3:
				s.bulletSize = 2;
				break;
			case MACHINE_GUN_1:
				s.reloadTime = 0.3f;
				break;
			case MACHINE_GUN_2:
				s.reloadTime = 0.3f;
				break;
			case MACHINE_GUN_3:
				s.reloadTime = 0.3f;
				break;
			case LASER:
				s.laser = false;
				break;
			case HOMING:
				s.homing = false;
				break;
			case GHOST:
				s.ghost = false;
				break;
			default:
				break;
		}
	}
	
	public long getUID() {
		return uid;
	}
}