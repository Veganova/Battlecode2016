package trialplayer2Messaging;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer {
	public static Direction movingDirection = Direction.EAST;
	static RobotController rc;
	static int ID = 0;
	static int viperCount = 0;
	static MapLocation test;
	static MapLocation[] testarray;
	static MapLocation[] visitedPositions;
	public static ArrayList<ArrayList<Double>> leadervision = new ArrayList<ArrayList<Double>>();
	//static double leadervision; 
	public static boolean oneMove = true;
	static int count = 0;
	static int count2 = 0;
	public static void run(RobotController rcIn) throws GameActionException{
		//test = new MapLocation(rc.getLocation().x,rc.getLocation().y);
		rc=rcIn;
		if(rc.getTeam()==Team.B){
			movingDirection=Direction.WEST;
		}
		setLeader();
	while(true)
		try {
			
			//0, 
			
			repeat();
			Clock.yield();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void setLeader() throws GameActionException{
		
		if(rc.getRoundNum() == 0 && rc.getType() == RobotType.ARCHON)
		{
			Signal[] incomingMessages = rc.emptySignalQueue();
			rc.broadcastMessageSignal(0, 0, 100);
			if(incomingMessages.length == 0)
			{
				ID = 1;//LEADER
				leadervision = LeaderMethods.initialize(leadervision);	
			}
		}
	}
	
	
	public static void report() throws GameActionException
	{
		if(rc.getRoundNum() >0)
			rc.broadcastSignal(100);
	}
	
	public static void leader() throws GameActionException
	{
		if(rc.getRoundNum() >0)
		{
			rc.setIndicatorString(0, " " + leadervision.get(0).get(leadervision.get(0).size()-1));
			if(oneMove && rc.canMove(Direction.EAST) && rc.isCoreReady())
			{
					
				rc.move(Direction.EAST);
				LeaderMethods.move(Direction.EAST);
				
				oneMove = false;
			}
			//rc.setIndicatorString(0, " "  + LeaderMethods.getValue(new MapLocation(rc.getLocation().x+3,rc.getLocation().y+5)));
			//rc.setIndicatorString(0, " "  + LeaderMethods.ArraytoMapCoords(0,0).y + " " + LeaderMethods.ArraytoMapCoords(0,0).x + " ");
//			if(RobotType.GUARD.partCost<=rc.getTeamParts()){
//				build2(RobotType.GUARD,Direction.WEST);
//				
//			}
			
		}
			//rc.setIndicatorString(0, " " + LeaderMethods.getValue(new MapLocation(rc.getLocation().x-3, rc.getLocation().y-5)) + " ") ;
		
	}
	
	public static void updateMap()
	{
		
	}
	
	public static void repeat() throws GameActionException{
		if(ID == 1)
			leader();
		
		else if(rc.getType() !=RobotType.ARCHON)
		{
			//if(rc.canMove(movingDirection) && rc.isCoreReady())
			//rc.move(movingDirection);
		}
		//attack and kitting stuff
//		if(Attack.attack()){
//			return;
//		}
//		else if (rc.isCoreReady()){
//			if(Attack.kitingway!=null)
//				movingDirection=Attack.kitingway;//run away see an enemy, never go back (need to implement some kind of STOP running away, elected leader)
//			if(rc.canMove(movingDirection))
//				rc.move(movingDirection);
//		}
	}
	
	public static void build2(RobotType type, Direction choice){
		Direction bestbuild = null;
		int [] array = {0, 1, -1, 2, -2, 3, -3, 4};
		int [] prices = {};
		rc.setIndicatorString(0, " " + rc.getTeamParts());
		if(type.partCost>rc.getTeamParts()){
			return;
		}
		for(int x=0; x<8; x++){
			bestbuild =Direction.values()[(array[x]+choice.ordinal())%8];
			if(rc.canBuild(bestbuild, type))
				break;
		}
		if(bestbuild==null){                                                                                        
			searchspot(rc.getLocation(), choice);
			//issue a move command to this location?
		}
		else{
			try {
				if(type.partCost<rc.getTeamParts() && (rc.canBuild(bestbuild, type) &&rc.isCoreReady()))
					rc.build(bestbuild, type);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static MapLocation searchspot(MapLocation center, Direction choice){
		MapLocation check = null;
		MapLocation free = center;
		int relativemovement = 0, decidedmovement=0, directionscaler=10; //directionscaler sets the weight of the same direction value
		for(int x=-5; x<6; x++) //unsure about this bounds but I think they fit into the "35" sight range
			for(int y=-5; y<6; y++){
				check = new MapLocation(center.x+x, center.y+y); 
				decidedmovement=Math.abs(choice.ordinal()-center.directionTo(free).ordinal())*directionscaler; //* might be inefficient: this just scales the difference in direction to include in calcs
				relativemovement=Math.abs(choice.ordinal()-center.directionTo(check).ordinal())*directionscaler;
				if(rc.canSense(check)&&rc.senseRubble(check)<100 
						&& center.distanceSquaredTo(free)+decidedmovement>center.distanceSquaredTo(check)+relativemovement){ 
					//this checks if the square is on the map, if it's traversible, and if it's a better square than what's saved
					free = check;
				}
			}
		return free;    //returns ideal location to build (move towards)
	}


	
	
	
	

}
