package moving2;

import java.util.ArrayList;
import battlecode.common.*;

public class Utility {
	static int[] possibleDirection = new int[]{0,1,-1,2,-2,3,-3,4};//straight, 45degs to the right
	static ArrayList<MapLocation> pastLocations =	new ArrayList<MapLocation>(); 
	static boolean patient = true;
	
	
	
	
	
	public static void forwardish(Direction ahead) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
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


	public static RobotInfo[] joinRobotInfo(RobotInfo[] zombies, RobotInfo[] otherEnemies) {
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