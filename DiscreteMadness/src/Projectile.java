
public class Projectile extends Actor{
	public Projectile(String texName){
		super(texName);
	}
	
	public void fireStart(int[] loc, float[] startPos){
		//set position
		thisSprite.setPosition(startPos[0], startPos[1]);
		updateTargetLoc(loc);
		updateMvInc();
		setAlive(true);		
	}
	
	//move to Location
	public void fire(){
		goToTargetLocation();
	}
	
	
}
