package bunker;
import battlecode.common.*;

public class turret {
static RobotController rc = RobotPlayer.rc;

public static void shootone(){
RobotInfo[] info = rc.senseHostileRobots(rc.getLocation(), 24);
if(info.length>0){
seeshoot(info);
}

}

public static void  seeshoot(RobotInfo[] info){
    int target = (int)(Math.random()*info.length);
        try {
            if(rc.isWeaponReady() && 
                    Math.abs(info[target].location.distanceSquaredTo(rc.getLocation()))
                    <= RobotType.TURRET.attackRadiusSquared){
            rc.attackLocation(info[target].location);}
        } catch (GameActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
}


}


