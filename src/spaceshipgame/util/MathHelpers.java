package spaceshipgame.util;

import processing.core.PConstants;

public class MathHelpers {
	public static float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
	
	public static float random(float min, float max) {
		return (float) Math.random() * (max - min) + min;
	}
	
	public static float[] randomInAnnulusSector(float x, float y, float rMin, float rMax, float aStart, float aEnd) {
	    float c = 2 / (rMax * rMax - rMin * rMin);
	    float d = (float) Math.sqrt(2 * Math.random() / c + rMin * rMin);
	    float a = random(aStart, aEnd);
	    
	    return new float[] {
	        (float) (Math.cos(a) * d + x),
	        (float) (Math.sin(a) * d + y)
	    };
	};

	/*
	Source: http://stackoverflow.com/questions/9048095/create-random-number-within-an-annulus
	*/
	public static float[] randomInAnnulus(float x, float y, float rMin, float rMax) {
	    return randomInAnnulusSector(x, y, rMin, rMax, 0, PConstants.TWO_PI);
	};

	/*
	Source: http://www.anderswallin.net/2009/05/uniform-random-points-in-a-circle-using-polar-coordinates/
	*/
	public static float[] randomInCircle(float x, float y, float r) {
	    return randomInAnnulus(x, y, 0, r);
	};
}
