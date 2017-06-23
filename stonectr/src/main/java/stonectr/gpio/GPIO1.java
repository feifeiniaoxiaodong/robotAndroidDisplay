package stonectr.gpio;

import java.io.DataOutputStream;

/**
 * Created by sunshine on 2017/6/3.
 */

public class GPIO1 implements GPIOPort {
    private int port = 1;

    @Override
    public int getPort() {
        return port;
    }
}
