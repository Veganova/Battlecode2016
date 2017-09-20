package ikei2;

import battlecode.common.*;

public class RobotPlayer {
	static int roundCount = 0;
	static RobotController rc;
	static MapLocation archonLoc;
	public static void run(RobotController rcIn) throws GameActionException{
		//test = new MapLocation(rc.getLocation().x,rc.getLocation().y);
		rc=rcIn;
		archonLoc = rc.getInitialArchonLocations(rc.getTeam())[0];
		while(true) {
			try {

				repeat();
				Clock.yield();
				
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	private static void setClosestArchon()
	{
		Signal[] messages = rc.emptySignalQueue();
		MapLocation closestArchon = rc.getLocation().add(Direction.NORTH, 100000);
		for(int i=0;i<messages.length;i++)
		{
			if(messages[i].getTeam() == rc.getTeam() && messages[i].getMessage()[1] == -123)//is an archon
			{
				if(messages[i].getLocation().distanceSquaredTo(rc.getLocation())<rc.getLocation().distanceSquaredTo(closestArchon))
				{
					closestArchon = messages[i].getLocation();
					archonLoc = closestArchon;
				}
				//closest
			}
		}
	}
	private static void repeat() throws GameActionException {
		// TODO Auto-generated method stub
		
		if(rc.getType()!=RobotType.ARCHON)
		{
			setClosestArchon();

			if(rc.getLocation().distanceSquaredTo(archonLoc)<10000)
			{
				rc.setIndicatorString(1, " " + archonLoc.x + " " + archonLoc.y);
			}
		}
		if(RobotType.ARCHON == rc.getType())
			Archon.repeat();
		else if(RobotType.SCOUT == rc.getType())
			Scout.repeat();
		else if(rc.getType()!=RobotType.TURRET)
			Soldier.repeat();
		roundCount++;
		
		//		Move.setTarget(rc.getInitialArchonLocations(RobotPlayer.rc.getTeam().opponent())[0]);
		//		Move.moveDecision();

	}

}
