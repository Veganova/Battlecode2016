package bunker;

import battlecode.common.*;

public class Archon {

	static int wait = 0;
	static MapLocation starting;
	static Direction movingDirection = Direction.EAST;
	
	
	public static void startingArchonLoc()
	{
		if(RobotPlayer.rc.getRoundNum() ==0)
		{
			starting = RobotPlayer.rc.getLocation();
		}
	}
	
	public static void Archonrepeat() throws GameActionException
	{
		if(RobotPlayer.rc.getRoundNum()%6!=0)
		{
			if(wait<40 )
			{
				build2(RobotType.TURRET, movingDirection.opposite());
				wait++;
				
			}
			else
			{
				if(RobotPlayer.rc.hasBuildRequirements(RobotType.GUARD))	
					build2(RobotType.GUARD, movingDirection.opposite());
				wait = 0;
			}
				
		}
		else
		{
			wait++;
			RobotPlayer.rc.setIndicatorString(0, " "  + RobotPlayer.rc.getTeamParts());
			movingDirection = Direction.values()[(int)(Math.random()*8)];
			stayInRange(25);
			
			if(RobotPlayer.rc.isCoreReady() && RobotPlayer.rc.canMove(movingDirection))
			{
				RobotPlayer.rc.move(movingDirection);
			}
		}
//		MapLocation[] parts = RobotPlayer.rc.sensePartLocations(RobotType.ARCHON.sensorRadiusSquared);
//		if(parts.length >0 )
//		{
//			for(int i=0;i<parts.length;i++)
//			{
//				if(parts[i].distanceSquaredTo(RobotPlayer.rc.getLocation())<=1)
//					RobotPlayer.rc.
//			}
//		}
			
		
		
	}
		
	public static void stayInRange(int range)
	{
		if(starting.distanceSquaredTo(RobotPlayer.rc.getLocation())>=range)
		{
			movingDirection = RobotPlayer.rc.getLocation().directionTo(starting);
		}
	}
	
	
	public static void build2(RobotType type, Direction choice) throws GameActionException{
		Direction bestbuild = choice;
		int [] array = {0, 1, -1, 2, -2, 3, -3, 4};
		
		if(type.partCost>RobotPlayer.rc.getTeamParts()){
			return;
		}
		for(int x=0; x<8; x++){
			//RobotPlayer.rc.setIndicatorString(0, " " + bestbuild);
			bestbuild =Direction.values()[Math.abs((array[x]+choice.ordinal())%8)];
			if(RobotPlayer.rc.canBuild(bestbuild, type))//&&(null == RobotPlayer.rc.senseRobotAtLocation(RobotPlayer.rc.getLocation().add(bestbuild))))
				break;
		}
		try {
				if(type.partCost<RobotPlayer.rc.getTeamParts() && (RobotPlayer.rc.canBuild(bestbuild, type) &&RobotPlayer.rc.isCoreReady()))
					RobotPlayer.rc.build(bestbuild, type);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
	
	

