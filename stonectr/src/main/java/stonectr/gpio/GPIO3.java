package stonectr.gpio;

import java.io.DataOutputStream;

/**
 * Created by sunshine on 2017/6/3.
 */

public class GPIO3 implements GPIOPort {
    private int port = 3;

    @Override
    public int getPort() {
        return port;
    }
}
