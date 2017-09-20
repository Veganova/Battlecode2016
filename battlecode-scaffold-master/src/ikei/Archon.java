package ikei;

import java.util.ArrayList;
import java.util.Arrays;

import battlecode.common.*;

public class Archon {
	//Scouts
	static int maxScoutCount = 1;
	static int scoutCount = 0;
	
	static RobotController rc = RobotPlayer.rc;

	static Direction movingDirection = Direction.NORTH_WEST;

	public static ArrayList<ArrayList<MapLocation>> keyLocations = new ArrayList<ArrayList<MapLocation>>();
	//0 - parts
	//1 - reactivated robots in order
	//2 - enemy locations
	//best/latest/closest will be put in front

	public static int getMaxScoutCount()
	{
		return maxScoutCount;
	}



	public static void repeat() throws GameActionException
	{
		
		if(RobotPlayer.roundCount ==0)
		{
			roundOne();
			//Move.setTarget(rc.getLocation());
		}
		else
		{
			if(rc.getRoundNum()%5 == 2)//every 5th round
				selfSense();
			collectResources();
			Move.moveDecision();
		}
		if(scoutCount < maxScoutCount)
		{
			if(rc.isCoreReady() && rc.canBuild(movingDirection, RobotType.SCOUT))
			{
				rc.build(movingDirection, RobotType.SCOUT);
				scoutCount++;
			}
		}
		
		recieveBroadCast();
		rc.broadcastMessageSignal(9800, -123, 200);
		
		if(keyLocations.get(0).size()>0)
			rc.setIndicatorString(0," " + keyLocations.get(0).size() + " " + keyLocations.get(0).get(0).x + " " + keyLocations.get(0).get(0).y);
		if(keyLocations.get(1).size()>0)
			rc.setIndicatorString(1," " + keyLocations.get(1).size() + " " + keyLocations.get(1).get(0).x + " " + keyLocations.get(1).get(0).y);
		if(Move.target!=null)
			rc.setIndicatorString(2," " + Move.target.x + " " + Move.target.y);
		
	}

	public static void updateLocationsList(MapLocation[] locations, int x){
		//x values
		//0 - parts
		//1 - reactivated robots in order
		//2 - enemy locations
		if(keyLocations.get(x).size() + locations.length<20)//dont store more than 9
		{
			ArrayList<MapLocation> tempStorage = keyLocations.get(x);
			for(int i=0; i<locations.length; i++)
			{
				if(checkDuplicates(keyLocations.get(x),locations[i]))
				{
					if(( rc.getLocation().distanceSquaredTo(locations[i])<1500))
						tempStorage.add(locations[i]);//writing sort is difficult cause we have to do it continuously, the distances to resources change
				}
			}
		keyLocations.set(x, tempStorage);
		//sort the array with closest resource at the front
		if(rc.getRoundNum()%5==0)
			sortLocs(x);
		}
	}

	public static void collectResources() throws GameActionException
	{
		//
		if( keyLocations.get(0).size()>0&& keyLocations.get(1).size()>0)//there are recorded parts AND deactivated robots
		{
			int closestPart = keyLocations.get(0).get(0).distanceSquaredTo(rc.getLocation());
			int closestDrobot = keyLocations.get(1).get(0).distanceSquaredTo(rc.getLocation());
			if(closestPart<closestDrobot)//part is closer
			{
				Move.setTarget(keyLocations.get(0).get(0));
				if(rc.getLocation().distanceSquaredTo(keyLocations.get(0).get(0))==0)//reached the closest part
				{
					removeElement(0,0);
					//Move.target = null;
				}
			}
			else//ROBOT is closer
			{
				Move.setTarget(keyLocations.get(1).get(0));
				activateRobot(keyLocations.get(1).get(0));//also removes upon reaching
			}
		}
		else if(keyLocations.get(0).size()>0)//there are only parts recorded
		{
			Move.setTarget(keyLocations.get(0).get(0));
			if(rc.getLocation().distanceSquaredTo(keyLocations.get(0).get(0))==0)//reached the closest part
			{
				removeElement(0,0);
				//Move.target = null;
			}
		}
		else if(keyLocations.get(1).size()>0)//there are only robots recorded
		{
			Move.setTarget(keyLocations.get(1).get(0));
			activateRobot(keyLocations.get(1).get(0));//also removes upon reaching
		}

	}

	public static void activateRobot(MapLocation robotLoc) throws GameActionException
	{
		Move.setTarget(robotLoc);

		if(rc.getLocation().distanceSquaredTo(robotLoc)<=2)//right next to
		{
			if(rc.isCoreReady())
			{
				if(rc.canSense(keyLocations.get(1).get(0)) && rc.senseRobotAtLocation(keyLocations.get(1).get(0)).team==Team.NEUTRAL)
					rc.activate(robotLoc);
				removeElement(1,0);//remove first term from the robots
			//Move.target = null;
			}
		}
	}
	
	public static void removeElement(int x, int index)//if location is 0 -123 OR if or rc's location
	{
		ArrayList<MapLocation> oneless = keyLocations.get(x);
		oneless.remove(index);
		keyLocations.set(x, oneless);
	}
	public static void selfSense()
	{
		//parts
		updateLocationsList(rc.sensePartLocations(rc.getType().sensorRadiusSquared),0);
		
		//robots
		RobotInfo[] dormant = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.NEUTRAL);
		MapLocation[] neutralRobots =  new MapLocation[dormant.length];
		for(int i = 0; i<dormant.length;i++)
		{
			neutralRobots[i] = dormant[i].location;
		}
		updateLocationsList(neutralRobots,1);
	}

	public static void sortLocs(int x)
	{//selection sort
		int[] array = new int[keyLocations.get(x).size()];
		for(int i=0;i<keyLocations.get(x).size();i++)
		{
			array[i] = keyLocations.get(x).get(i).distanceSquaredTo(rc.getLocation());
		}
		//the sort
		for (int i = 0; i < array.length-1; i++)
		{
			int min = i;
			for (int j = i+1; j < array.length; j++)
			{
				if (array[j] < array[min]) 
					min = j;
			}
			//Swaps
			int temp = array[i];
			array[i] = array[min];
			array[min] = temp;

			MapLocation tempMapLocation = keyLocations.get(x).get(i);
			keyLocations.get(x).set(i,keyLocations.get(x).get(min));
			keyLocations.get(x).set(min ,tempMapLocation);
		}
	}


	public static void recieveBroadCast()
	{
		Signal[] messages = rc.emptySignalQueue();

		if(messages.length>0)
		{
			//rc.setIndicatorString(0, " " + messages[0].getMessage()[1]);
			for(int i=0;i<messages.length;i++){//go through all the messages
				if(messages[i].getTeam()==rc.getTeam())//same team ... Perhaps also include if the message if form a scou
				{
					if(messages[i].getMessage()[0]<1000)
					{
					int x = messages[i].getMessage()[1]/1000;//get the x and y out of the broadcast
					int y = messages[i].getMessage()[1]%1000;
					MapLocation[] temp = new MapLocation[1];
					temp[0] = new MapLocation(x,y);
					
					updateLocationsList(temp, messages[i].getMessage()[0]);
					}
				}
			}
		}
	}

	public static void roundOne()
	{
		keyLocations.add(new ArrayList<MapLocation>());
		keyLocations.add(new ArrayList<MapLocation>());
		keyLocations.add(new ArrayList<MapLocation>());
		updateLocationsList(rc.getInitialArchonLocations(rc.getTeam().opponent()),2);
	}

	public static boolean checkDuplicates(ArrayList<MapLocation> x, MapLocation term)
	{
		if(x.contains(term))
			return false;
		return true;
	}
}
