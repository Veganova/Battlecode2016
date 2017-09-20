package ikei3;
import battlecode.common.*;

public class Attack {
    public static Direction kitingway = null;
    static RobotController rc = RobotPlayer.rc;
    static boolean islow = false;
    static RobotType [] types = new RobotType[]{RobotType.SOLDIER, RobotType.GUARD, 
            RobotType.TURRET, RobotType.TTM, RobotType.ARCHON, RobotType.VIPER,
                RobotType.BIGZOMBIE, RobotType.FASTZOMBIE, RobotType.STANDARDZOMBIE,
                RobotType.ZOMBIEDEN};
    static double [] priority = new double[] {1, 0.5, 0.5, 1.5, 0.75, 3, 4, 2, 1.5, 0.1};
    static double [] threat = new double[]{10, 5, 100, 20, 0, 30, 150, 60, 15, 0};
    static int threatlevel = 0;
    
    public static MapLocation attack(){
        
        RobotInfo[] hostiles = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
        RobotInfo[] friendlies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());        //change radius of detection   
        threatlevel=0;
        for(int x=0; x<hostiles.length; x++){
            for(int y=0; y<types.length; y++)
                if(hostiles[x].type==types[y])
                    threatlevel+=threat[y];
        }
        if(rc.getType().canAttack()&&rc.isWeaponReady()){                              //if there are more friendlies than hostiles nearby

            if(hostiles.length>0)
            {//if it's a unit that can attack
                try {
                    if(rc.getType().canAttack()&&rc.isWeaponReady()&&RobotType.SOLDIER.attackRadiusSquared>rc.getLocation().distanceSquaredTo(hostiles[0].location)){
                    rc.attackLocation(hostiles[prioritize(hostiles)].location); //fix this
                   }
                } catch (GameActionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }
       // if(!returnhome(friendlies, 2))
        if(rc.getType().maxHealth/5*2>rc.getHealth())
            islow = true;
        else 
            islow=false;
        if(rc.getType().canAttack() && (!rc.isWeaponReady() || (hostiles.length>1 &&hostiles.length>friendlies.length))){
            return kite(hostiles, friendlies);    
        }
        else if (!rc.getType().canAttack() && hostiles.length>1){
            return kite(hostiles, friendlies);
        }
        return null;
    }
    
    public static MapLocation kite(RobotInfo[] hostiles, RobotInfo[] allies){
        MapLocation swag = null;

        double x=0, y=0;
        for(int indexhostile=0; indexhostile<hostiles.length; indexhostile++){
            x+=hostiles[indexhostile].location.x;
            y+=hostiles[indexhostile].location.y; //averages enemy locations, NEEDS TO BE ROUNDED
        }
        x = x/((double)(hostiles.length));
        y = y/((double)(hostiles.length));
        swag = new MapLocation((int)(x+0.5), (int)(y+0.5));
        
        //swag.add();
        kitingway=rc.getLocation().directionTo(swag).opposite();
        if((rc.getType()!=RobotType.ARCHON && (islow && rc.isInfected()) || threatlevel<5))
            kitingway=kitingway.opposite();
        MapLocation finaloc = rc.getLocation().add(kitingway).add(kitingway);    //should be nearest archon or something
        return finaloc;
    }
    
    public static int prioritize(RobotInfo[] hostiles){    //this NEEDS TO BE STREAMLINED Q.Q
        double bestpoint = 10000;
        double currentpoint =0;
        
        int bestindex = 0;
        for(int x=0; x<hostiles.length; x++){
            currentpoint=hostiles[x].health;
            for(int y=0; y<10;y++){
                if(hostiles[x].type==types[y]){
                    currentpoint=currentpoint/priority[y];
                }
            }
            if(bestpoint>currentpoint){
                bestpoint=currentpoint;
                bestindex=x;
            }
        }
        return bestindex;
    }

//    public static boolean returnhome(RobotInfo[] allies, int pack){
//        if(allies.length<pack){
//            return true;
//        }
//        
//        return false;
//    }
    
}