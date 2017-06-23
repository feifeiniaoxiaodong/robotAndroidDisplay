package stonectr.serial.callBackEvent;

/**
 * Created by sunshine on 2017/5/13.
 */

public class UartRobotPoseEvent extends UartBaseEvent {
    private double position_x;
    private double position_y;
    private double rotation;

    public double getPosition_x() {
        return position_x;
    }

    public void setPosition_x(double position_x) {
        this.position_x = position_x;
    }

    public double getPosition_y() {
        return position_y;
    }

    public void setPosition_y(double position_y) {
        this.position_y = position_y;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }


}
