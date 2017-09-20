package moving1;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer {
	static boolean patient = true;
	public static Direction dir = Direction.NORTH_EAST;
	public static RobotController rc;
	static int[] possibleDirection = new int[]{0,1,-1,2,-2,3,-3,4};//straight, 45degs to the right
	static ArrayList<MapLocation> pastLocations =	new ArrayList<MapLocation>(); 
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
		RobotInfo[] zombies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.ZOMBIE);
		RobotInfo[] otherEnemies= rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());
		RobotInfo[] allEnemies = joinRobotInfo(zombies, otherEnemies);
		
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
				forwardish(dir);
			}
		}
	}


	private static void forwardish(Direction ahead) throws GameActionException {
		for(int i:possibleDirection)
		{
			Direction candidateDirection = Direction.values()[(ahead.ordinal()+i+8)%8];
			MapLocation candidateLocation = rc.getLocation().add(candidateDirection);
			if(patient)
			{
				if(rc.canMove(candidateDirection) && !pastLocations.contains(candidateLocation))
				{
					pastLocations.add(rc.getLocation());
					if(pastLocations.size()>20)
						pastLocations.remove(0);
					rc.move(candidateDirection);
					return;
				}else{
					if(rc.canMove(candidateDirection))
					{
						rc.move(candidateDirection);
						return;
					}else{//dig
						if(rc.senseRubble(candidateLocation) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
						{
							rc.clearRubble(candidateDirection);
							return;
						}
					}
				}
			}
		}
		patient = false;	
		
	}


	private static RobotInfo[] joinRobotInfo(RobotInfo[] zombies, RobotInfo[] otherEnemies) {
		// TODO Auto-generated method stub
		RobotInfo[] opponents = new RobotInfo[otherEnemies.length + zombies.length];
		for(int i=0;i<zombies.length;i++)
		{
			opponents[i] = zombies[i];
		}
		int index = zombies.length;
		for(int i=0;i<otherEnemies.length;i++)
		{
			opponents[index] = otherEnemies[i];
			index++;
		}
		return opponents;
	}
	
	
}
