package trialplayer;
import battlecode.common.*;

public class Attack {
	public static Direction kitingway = null;
	static RobotController rc = RobotPlayer.rc;
	public static boolean attack(){
		
		RobotInfo[] hostiles = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
		RobotInfo[] friendlies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());		//change radius of detection					
		rc.setIndicatorString(0, "" + hostiles.length + " " + friendlies.length+ " " + RobotPlayer.movingDirection) ;
		if(rc.getType().canAttack()&&rc.isWeaponReady()&&hostiles.length<=friendlies.length){                      		//if there are more friendlies than hostiles nearby

			if(hostiles.length>0)
			{//if it's a unit that can attack
				try {
					rc.attackLocation(hostiles[0].location); //fix this
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		}
		else if(hostiles.length>1){
			kite(hostiles);	
		}
		
		return false;
	}
	
	public static void kite(RobotInfo[] hostiles){
		MapLocation swag = null;

		double x=0, y=0;
		for(int indexhostile=0; indexhostile<hostiles.length; indexhostile++){
			x+=hostiles[indexhostile].location.x;
			y+=hostiles[indexhostile].location.y; //averages enemy locations, NEEDS TO BE ROUNDED
		}
		x = x/((double)(hostiles.length));
		y = y/((double)(hostiles.length));
		swag = new MapLocation((int)(x+0.5), (int)(y+0.5));
		
		//swag.add();
		kitingway=rc.getLocation().directionTo(swag).opposite();
		rc.setIndicatorString(0,"\n " + x + " " + y + " " + kitingway);
	}
	

	
}
