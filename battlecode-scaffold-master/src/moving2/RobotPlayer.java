package moving2;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer {
	public static Direction dir = Direction.NORTH_EAST;
	public static RobotController rc;
	
	
	public static void run(RobotController rctemp)
	{
		rc = rctemp;
		if(rc.getTeam() == Team.B)
			dir = Direction.SOUTH_WEST;

		while(true)
		{
			try {
				repeat();
				Clock.yield();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static void repeat() throws GameActionException
	{
		RobotInfo[] allEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
		RobotInfo[] zombies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.ZOMBIE);
		RobotInfo[] otherEnemies= rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());
		//RobotInfo[] allEnemies = Utility.joinRobotInfo(zombies, otherEnemies);
		
		if(allEnemies.length > 0 && rc.getType().canAttack())//there are zombies nearby
		{
			if(rc.isWeaponReady())
			{
				rc.attackLocation(allEnemies[0].location);
			}
		}
		else{
			if(rc.isCoreReady())
			{
				Utility.forwardish(dir);
			}
		}
	}


	
	
}
