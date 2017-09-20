package team373;

import java.util.ArrayList;
import java.util.Arrays;

import battlecode.common.*;

public class Scout {

	static RobotController rc = RobotPlayer.rc;
	//static MapLocation archonLoc = rc.getInitialArchonLocations(rc.getTeam())[0];
	static int[] possibleDirections = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction movingDirection = Direction.values()[possibleDirections[(int)(Math.random()*8)]];
	static int broadCastRange=0;


	public static MapLocation[] searchParts()
	{
		return rc.sensePartLocations(rc.getType().sensorRadiusSquared);
	}

	public static MapLocation[] dormantRobots()
	{
		RobotInfo[] dormant = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.NEUTRAL);
		MapLocation[] locs =  new MapLocation[dormant.length];
		for(int i = 0; i<dormant.length;i++)
		{
			locs[i] = dormant[i].location;
		}
		return locs;
	}
	public static MapLocation[] searchDens()
	{
		RobotInfo[] hostiles = rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared);
		ArrayList<MapLocation> dens = new ArrayList<MapLocation>();
		for(int i = 0; i<hostiles.length;i++)
		{
			if(hostiles[i].type.equals(RobotType.ZOMBIEDEN))
				dens.add(hostiles[i].location);
			
		}
		MapLocation[] densArray = new MapLocation[dens.size()];
		for(int i=0;i<densArray.length;i++)
		{
			densArray[i] = dens.get(i);
		}
		return densArray;
	}
	public static void bounceMovement() throws GameActionException
	{
		ArrayList<Integer> directions = new ArrayList<Integer>(Arrays.asList(1,-1,2,-2,3,-3,4));//,-3,3,4,2,-2));
		RobotInfo[] allies = rc.senseNearbyRobots(RobotType.ARCHON.sensorRadiusSquared, rc.getTeam());
		boolean archonispresent = false;
		for(int i = 0;i<allies.length;i++)
		{
			if(allies[i].type == RobotType.ARCHON && allies[i].team==rc.getTeam())
				archonispresent = true;
		}
		
		if(rc.isCoreReady())
		{
			if(archonispresent)
			{
				movingDirection = rc.getLocation().directionTo(RobotPlayer.archonLoc).opposite();
			}
			else if(allies.length>12 || !rc.canMove(movingDirection) || rc.getRoundNum()%40==0)//every x rounds or if it collides with a wall or too close to a same team archon
			{
				for(int i=directions.size(); i>=0; i--)
				{
					int index = (int)(Math.random()*directions.size());
					Direction candidateDirection = Direction.values()[(8+directions.get(index))%8];
					directions.remove(index);
					if(rc.canMove(candidateDirection))
					{
						movingDirection = candidateDirection;
						break;
					}
				}
			}
			if(rc.canMove(movingDirection))
			{
				rc.move(movingDirection);
				return;
			}

		}
	}
	

	
	public static void broadcast(int x, int range) throws GameActionException
	{

		MapLocation[] gottaprintemall;
		gottaprintemall= searchParts();
		if(x ==1)
			gottaprintemall= dormantRobots();
		if(x ==2)
		{
			gottaprintemall= searchDens();
		}

		for(int i =0; i<gottaprintemall.length;i++)
		{
			//ASSUMES THAT THE COORDINATES ARE ALWAYS 3 DIGITS LONG
			if(rc.isCoreReady())
			{
				if(x!=2)
					rc.broadcastMessageSignal(x, gottaprintemall[i].x*1000 + gottaprintemall[i].y, range);
				else
					rc.broadcastMessageSignal(-1*x, gottaprintemall[i].x*1000 + gottaprintemall[i].y, range);
			}
		}
	}
	public static void repeat() throws GameActionException
	{
		rc.setIndicatorString(0, " " + broadCastRange);
		//every 7th turn broadcast
		if(rc.getRoundNum()%4==0)
			broadcast(0,broadCastRange);
		if((rc.getRoundNum()+1)%4==0)
			broadcast(1,broadCastRange);
		if((rc.getRoundNum()+2)%4==0)
			broadcast(2,broadCastRange);
		//		if((rc.getRoundNum()+2)%7==0)
		//			broadcast(2,broadCastRange);

		updateBroadCastRange();
		bounceMovement();

	}
	public static void updateBroadCastRange()
	{
		broadCastRange = rc.getLocation().distanceSquaredTo(RobotPlayer.archonLoc);
	}
	
}

