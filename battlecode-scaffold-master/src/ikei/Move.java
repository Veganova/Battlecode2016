package ikei;
import battlecode.common.*;
import java.util.ArrayList;

public class Move {
    static ArrayList<MapLocation> history = new ArrayList<MapLocation>();
    static Direction movingDirection = Direction.EAST;
    static MapLocation target =null;
    static int patience= 0;
    static int historyMax = 16;
    static int minDistance = 100000;
    private static void store(){
    	if(history.size()>historyMax){
            history.remove(0);
        }
    	if(movingDirection == null)
    		history.clear();
    	if(!history.contains(RobotPlayer.rc.getLocation()))
    		history.add(RobotPlayer.rc.getLocation());
        
    }
    
    public static void setTarget(MapLocation location)
    {
    	
    	if(target == null || !target.equals(location))
    	{
    	target = location;
    	patience = 0;
    	minDistance = 100000;
    	}
    }
    
    public static void moveDecision() throws GameActionException
    {
    	
//    	RobotPlayer.rc.setIndicatorString(1, " "+ minDistance);
//    	RobotPlayer.rc.setIndicatorString(0, " "+ patience);
    	if(target!=null)
    	{
    		if(RobotPlayer.rc.getLocation().distanceSquaredTo(target) ==0)
    			target = null;
    		else if(RobotPlayer.rc.getLocation().distanceSquaredTo(target) < minDistance)
    		{
    			minDistance = RobotPlayer.rc.getLocation().distanceSquaredTo(target);
    			if(patience-3>=0)
    				patience -=3;
    			
    		}
    		else
    		{

    			patience+=2;

    		}

    		if(patience >= historyMax*2)
    		{
    			rubbleTime();
    		}
    		else
    		{
    			move();
    		}
    	}
    }
    
    public static void rubbleTime() throws GameActionException
    {
    	
    	movingDirection = RobotPlayer.rc.getLocation().directionTo(target);
    	if(RobotPlayer.rc.isCoreReady())
    	{
    		if(RobotPlayer.rc.canMove(movingDirection))
    		{
    			if(movingDirection!= null)
    				RobotPlayer.rc.move(movingDirection);
    		}
    		else if(RobotPlayer.rc.senseRubble((RobotPlayer.rc.getLocation().add(movingDirection)))>0)
    		{
    			RobotPlayer.rc.clearRubble(movingDirection);
    		}
    		else
    			move();
    	}
    		
    }
    
    public static void move() throws GameActionException
    {
    	if(target!=null)
    	{
    	movingDirection = getdirection(target, 8);
    	store();
    	if(movingDirection!= null && RobotPlayer.rc.isCoreReady() && RobotPlayer.rc.canMove(movingDirection))
    		RobotPlayer.rc.move(movingDirection);
    	}
//    	RobotPlayer.rc.setIndicatorString(0, " "+  RobotPlayer.rc.getInitialArchonLocations(RobotPlayer.rc.getTeam().opponent())[0].x
//    			+ " "+  RobotPlayer.rc.getInitialArchonLocations(RobotPlayer.rc.getTeam().opponent())[0].y);
    }
    public static Direction getdirection(MapLocation ikei, int motionrange){
        int[] array = new int[]{0, 1, -1, 2, -2, 3, -3, 4};
        //Direction check = movingDirection;
        Direction check = RobotPlayer.rc.getLocation().directionTo(ikei);
       
        for(int x=0; x<motionrange; x++){
            check = Direction.values()[(RobotPlayer.rc.getLocation().directionTo(ikei).ordinal()+array[x]+8)%8];
//            if(x==0)
//            	 RobotPlayer.rc.setIndicatorString(0, " "+ check + " " + check.ordinal());
//            if(x==6)
//           	 RobotPlayer.rc.setIndicatorString(1, " "+ check + " " + check.ordinal());
            if(RobotPlayer.rc.canMove(check)){
            	
                if(!history.contains(RobotPlayer.rc.getLocation().add(check))){
                	//RobotPlayer.rc.setIndicatorString(0, " "+ check);
                	return check;
                }

            }
        }
       // RobotPlayer.rc.setIndicatorString(0, " "+ RobotPlayer.rc.canMove(check));
    	
        return null;

    }

    public static MapLocation simulation(MapLocation ending){            //PROBLEM: IF YOU CAN'T GET THERE...
        MapLocation simulLocation = RobotPlayer.rc.getLocation();
        while(RobotPlayer.rc.getLocation().distanceSquaredTo(ending)>1){
            ArrayList<MapLocation> path = new ArrayList<MapLocation>();
            while(simulLocation!=ending){
                path.add(simulLocation);
                simulLocation = simulLocation.add(getdirection(ending, 8));
            }
            ending = path.get(path.size()/2);
        }
        return ending;
    }

}


