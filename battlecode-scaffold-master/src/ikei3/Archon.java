package ikei3;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import battlecode.common.*;

public class Archon {
	//Scouts
	static int maxScoutCount = 1;
	static int scoutCount = 0;
	static int counter = 0;
	static RobotController rc = RobotPlayer.rc;
	public static MapLocation go = null;
	static Direction movingDirection = Direction.NORTH_WEST;
	static int timenearden = 0;
	static int broadcasts =0;
	public static ArrayList<ArrayList<MapLocation>> keyLocations = new ArrayList<ArrayList<MapLocation>>();
	//0 - parts
	//1 - reactivated robots in order
	//2 - enemy locations
	//best/latest/closest will be put in front





	public static void repeat() throws GameActionException
	{
		if(RobotPlayer.roundCount ==0)
		{
			roundOne();
			//Move.setTarget(rc.getLocation());
			if(rc.isCoreReady() && rc.canBuild(movingDirection, RobotType.SCOUT))
			{
				rc.build(movingDirection, RobotType.SCOUT);
				scoutCount++;
			}
		}
		else
		{

			if(scoutCount < maxScoutCount)
			{
				
			}


			if(rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared).length>0){
				Move.setTarget(rc.getInitialArchonLocations(RobotPlayer.rc.getTeam())[0]);
				go=Attack.attack();
				if(go!=null){// && rc.senseHostileRobots(rc.getLocation(), rc.getType().sensorRadiusSquared).length>5){
					Move.setTarget(go);
					rc.setIndicatorString(0,"getting attacked");
				}
				
				Move.moveDecision();
			}
			else
			{
				if(rc.getTeamParts()>RobotType.SOLDIER.partCost)
				{
					rc.setIndicatorString(0,"build " + rc.getTeamParts());
					howtobuild(true);
				}

				//if(rc.getRoundNum()%5 == 2)//every 5th round
					selfSense();
				
				collectResources();
				Move.moveDecision();



				recieveBroadCast();
				if(broadcasts < 20 && rc.getRoundNum()%20==4)
				{
					rc.broadcastMessageSignal(9800, -123, 300);
					broadcasts++;
				}
				broadcasts = 0;
			}

		}
		//		if(keyLocations.get(0).size()>0)
		//			rc.setIndicatorString(0," " + keyLocations.get(0).size() + " " + keyLocations.get(0).get(0).x + " " + keyLocations.get(0).get(0).y);
		//		if(keyLocations.get(1).size()>0)
		//			rc.setIndicatorString(1," " + keyLocations.get(1).size() + " " + keyLocations.get(1).get(0).x + " " + keyLocations.get(1).get(0).y);

//		if(keyLocations.get(2).size()>0)
//		{
//			rc.setIndicatorString(0,"timenearden: " + timenearden+ " ");
//			rc.setIndicatorString(1," " + keyLocations.get(2).size() + " "
//		+ keyLocations.get(2).get(0).x + " " + keyLocations.get(2).get(0).y + " .dist: " + keyLocations.get(2).get(0).distanceSquaredTo(rc.getLocation()));
//		}
		
		}

//	public static void goThere() throws GameActionException
//	{
//		if(keyLocations.get(2).size()>0 && 
//				(keyLocations.get(2).get(0).distanceSquaredTo(rc.getLocation())<1600))
//		{
//			rc.broadcastMessageSignal(-1*keyLocations.get(2).get(0).x, -1*keyLocations.get(2).get(0).y, 900);
//		}
//	}

	public static void howtobuild(boolean tobuildornottobuild){
		if(tobuildornottobuild)
		{
			RobotType[] buildplan = new RobotType[]{RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER, 
					RobotType.SOLDIER, RobotType.VIPER};
			counter = counter%5;

			if(rc.getTeamParts()>buildplan[counter].partCost && rc.isCoreReady()){
				builder(buildplan[counter], Direction.EAST);
				//rc.setIndicatorString(0, " "+ counter + " " + buildplan[counter]);
				counter++;

			}
		}
	}


	public static void builder(RobotType type, Direction choice){    //input robot type and choice
		Direction bestbuild = null;
		int[] array = {8, 9, -1, 2, -2, 3, -3, 4};
		if(type.partCost>RobotPlayer.rc.getTeamParts()){
			return;
		}
		for(int x=0; x<8; x++){
			bestbuild =Direction.values()[Math.abs((array[x]+choice.ordinal())%8)];
			if(RobotPlayer.rc.canBuild(bestbuild, type))
				break;
		}
		try {
			if(type.partCost<RobotPlayer.rc.getTeamParts() && (RobotPlayer.rc.canBuild(bestbuild, type) &&RobotPlayer.rc.isCoreReady()))
			{ RobotPlayer.rc.build(bestbuild, type);
			// buildQuota++;
			}
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateLocationsList(MapLocation[] locations, int x) throws GameActionException{
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
					if(( rc.getLocation().distanceSquaredTo(locations[i])<1600))
						tempStorage.add(locations[i]);//writing sort is difficult cause we have to do it continuously, the distances to resources change
				}
			}
			keyLocations.set(x, tempStorage);
			if(rc.getRoundNum()%5==1)
			{
				sortLocs(x);
			}
			checkPartsForRubble();
		}
	}
	
	public static void checkPartsForRubble()
	{
		for(int i=0;i<keyLocations.get(0).size();i++)
		{
			
			MapLocation temp = keyLocations.get(0).get(i);
			if(rc.canSense(temp)&&(rc.senseParts(temp) -rc.senseRubble(temp))<0)
				removeElement(0,i);
				
		}
	}

	public static void collectResources() throws GameActionException
	{
		if(keyLocations.get(2).size()>0 && keyLocations.get(2).get(0).distanceSquaredTo(rc.getLocation())<50)
		{
			Move.setTarget(keyLocations.get(2).get(0));
			if(rc.getLocation().distanceSquaredTo(keyLocations.get(0).get(0))<5)//reached the closest part
			{
				timenearden++;
				if(timenearden>=10)//stayed 10 turns near that loc, probably den being attacked by then, we done boiz
				{
					removeElement(2,0);
					timenearden=0;
				}
				//Move.target = null;
			}
		}
		else if( keyLocations.get(0).size()>0&& keyLocations.get(1).size()>0)//there are recorded parts AND deactivated robots
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
//		else
//			Move.setTarget(rc.getInitialArchonLocations(rc.getTeam())[0]);

	}

	public static void activateRobot(MapLocation robotLoc) throws GameActionException
	{
		Move.setTarget(robotLoc);

		if(rc.getLocation().distanceSquaredTo(robotLoc)<=2)//right next to
		{
			if(rc.isCoreReady())
			{
				if(keyLocations.get(1).size()>0 && rc.canSense(robotLoc))
				{
		
					try{
						rc.activate(robotLoc);
					}
					catch(GameActionException e){
						e.printStackTrace();
					}
				
					removeElement(1,0);//remove first term from the robots
				}

			}
		}
	}

	public static void removeElement(int x, int index)//if location is 0 -123 OR if or rc's location
	{
		ArrayList<MapLocation> oneless = keyLocations.get(x);
		oneless.remove(index);
		keyLocations.set(x, oneless);
	}
	public static void selfSense() throws GameActionException
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
		updateLocationsList(densArray,2);
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


	public static void recieveBroadCast() throws GameActionException
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
							
							if(broadcasts < 20 && messages[i].getMessage()[0]==-2)//den location
							{
								rc.broadcastMessageSignal(2, messages[i].getMessage()[1], 900);
								broadcasts++;
							}
							
							MapLocation[] temp = new MapLocation[1];
							temp[0] = new MapLocation(x,y);

							updateLocationsList(temp, Math.abs(messages[i].getMessage()[0]));
						
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
		//updateLocationsList(rc.getInitialArchonLocations(rc.getTeam().opponent()),2);
	}
	public static int getMaxScoutCount()
	{
		return maxScoutCount;
	}
	public static boolean checkDuplicates(ArrayList<MapLocation> x, MapLocation term)
	{
		if(x.contains(term))
			return false;
		return true;
	}
}
