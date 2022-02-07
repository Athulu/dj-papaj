import djpapaj.DJpapaj;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class klasatestowa {
    @Test
    public void test1() {
        assertEquals(3, 3);
    }

    @Test
    public void test2() {
        int k = 3;
        int r = 5;
        assertEquals(DJpapaj.testowe(k, r), 15);
    }

    @Test
    public void test3() {
        int k = 2;
        int r = 5;
        assertEquals(DJpapaj.testowe(k, r), 10);
    }
}
