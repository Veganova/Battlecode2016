package bunker;

import java.util.ArrayList;

import battlecode.common.*;
import trialplayer2Messaging.LeaderMethods;

public class RobotPlayer {
	static RobotController rc;
	static int ID = 0;
	public static void run(RobotController rcIn) throws GameActionException{
		//test = new MapLocation(rc.getLocation().x,rc.getLocation().y);
		rc=rcIn;
		
	while(true)
		try {
			
			repeat();
			Clock.yield();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void report() throws GameActionException
	{
		if(rc.getRoundNum() >0)
			rc.broadcastSignal(100);
	}
	


	public static void setLeader() throws GameActionException{
		
		if(rc.getRoundNum() == 0 && rc.getType() == RobotType.ARCHON)
		{
			Signal[] incomingMessages = rc.emptySignalQueue();
			rc.broadcastMessageSignal(0, 0, 7000);
			int own = 0;
			boolean first_own = false;
			int first_own_loc = 0;
			for(int i=0;i<incomingMessages.length;i++)
			{
				if(incomingMessages[i].getTeam()==rc.getTeam())
					own++;
				if(!first_own&&own==1)
				{
					first_own_loc = i;
				}
					
			}
			if(own == 0)
			{
				ID = 1;//LEADER
				
			}
			else
			{
				Archon.starting = incomingMessages[first_own_loc].getLocation();
			}
		}
	}
	public static void repeat() throws GameActionException{
		
		setLeader();
		if(rc.getType() == RobotType.ARCHON && ID == 1)
			Archon.startingArchonLoc();
		if(rc.getType() == RobotType.GUARD)
			Guards.startingGuardLoc(25);
		
		turret.shootone();
		
		
		if(rc.getRoundNum()>0)
		{
			if(ID==1)
			{
				Archon.Archonrepeat();
			}
			else if(rc.getType() ==RobotType.ARCHON){
				
				Archon.movingDirection = rc.getLocation().directionTo(Archon.starting);
				if(rc.canMove(Archon.movingDirection)&&rc.isCoreReady())
				{
					rc.move(Archon.movingDirection);
				}
				else
				{
					Archon.movingDirection = Direction.values()[(int)Math.random()*8];
				}
			}
		
		else if(rc.getType() ==RobotType.GUARD&&rc.getRoundNum()>0)
		{
			Guards.Guardrepeat();
		}
		}
		
	}
	

	

	}



	
	
	
	


