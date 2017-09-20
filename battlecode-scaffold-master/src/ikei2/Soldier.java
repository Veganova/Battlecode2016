package ikei2;


import battlecode.common.*;

public class Soldier {
	
	public static RobotController rc = RobotPlayer.rc;
	public static MapLocation go = null;
	
	
	public static void repeat() throws GameActionException{
		//Move.setTarget(rc.getInitialArchonLocations(RobotPlayer.rc.getTeam())[0]);
		go=Attack.attack();
		if(go!=null){ 
			Move.setTarget(go);
			//if distance to archon location IS BIG, settarget archolocation (MUST BE PLACED HERE)
			if(rc.getLocation().distanceSquaredTo(RobotPlayer.archonLoc)<500)
				Move.setTarget(RobotPlayer.archonLoc);
		}
		if(RobotType.TURRET!=rc.getType())
			Move.moveDecision();
		
	}
}