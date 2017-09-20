package trialplayer;

import battlecode.common.*;

public class RobotPlayer {
	public static Direction movingDirection = Direction.EAST;
	static RobotController rc;
	
	public static void run(RobotController rcIn){
		rc=rcIn;
		if(rc.getTeam()==Team.B){
			movingDirection=Direction.WEST;
		}
		
	while(true)
		try {
			//0, 
			//rc.setIndicatorString(0, "" + movingDirection.ordinal() + " ") ;
			repeat();
			
			Clock.yield();
			
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void repeat() throws GameActionException{
		if(Attack.attack()){
			return;
		}
		else if (rc.isCoreReady()){
			if(Attack.kitingway!=null)
				movingDirection=Attack.kitingway;//run away see an enemy, never go back (need to implement some kind of STOP running away, elected leader)
			if(rc.canMove(movingDirection))
				rc.move(movingDirection);
		}
	}
	
	
	
	

}
