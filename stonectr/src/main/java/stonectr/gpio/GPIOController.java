package stonectr.gpio;

import java.io.DataOutputStream;
import java.security.PublicKey;

/**
 * Created by sunshine on 2017/6/3.
 */

public class GPIOController {

    public static GPIOController instance;
    public static GPIOController getInstance()
    {
        if(instance == null)
        {
            instance = new GPIOController();
        }
        return instance;
    }

    private  GPIOController()
    {

    }
    public void set_High(GPIOPort port)
    {
         setHigh(port.getPort());
    }
    public void set_Low(GPIOPort port)
    {
        setLow(port.getPort());
    }

    private boolean setLow(int port) {
        boolean FLAG =  rootCommand("echo  0 > /sys/class/backlight/rk28_bl/gpio" + port);
        return FLAG;
    }

    private boolean setHigh(int port) {
        boolean FLAG = rootCommand("echo  1 > /sys/class/backlight/rk28_bl/gpio" + port);
        return FLAG;
    }

    private boolean rootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
