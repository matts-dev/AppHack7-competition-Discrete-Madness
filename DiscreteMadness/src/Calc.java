import java.util.BitSet;
import java.util.Random;

/**
 * @author Brenner Harris
 * @author Matt Stone
 *
 *         note: I, Matt, changed the gitBin function after seeing implmentation
 *         to use a loop. We made this game in 10 hours so we didn't make
 *         everything the most efficiently
 */
public class Calc {
	public static int getRandom() {
		Random rand = new Random();
		int randNum = rand.nextInt(10);
		return randNum;
	}

	public static int[] getBin(int num) {

		int[] resultArray = new int[4];
		int mask = 1;
		for (int i = 3; i >= 0; --i) {
			//get lowest bit and store it
			int bit = num & mask;
			resultArray[i] = bit;
			
			//shift bits right by 1
			num >>= 1;
		}

		return resultArray;
	}

	
	/**
	 * Brenner's version
	 * 
	 * @param num
	 * @return
	 */
	/*
	public static int[] getBin(int num) {
		int[] binArray = new int[4];
		int temp = num;
		if (num / 8 == 1) {
			binArray[0] = 1;
			temp -= 8;
		} else {
			binArray[0] = 0;
		}
		if (temp / 4 == 1) {
			binArray[1] = 1;
			temp -= 4;
		} else {
			binArray[1] = 0;
		}
		if (temp / 2 == 1) {
			binArray[2] = 1;
			temp -= 4;
		} else {
			binArray[2] = 0;
		}
		if (temp / 1 == 1) {
			binArray[3] = 1;
			temp -= 1;
		} else {
			binArray[3] = 0;
		}

		return binArray;
	}
	*/
}
