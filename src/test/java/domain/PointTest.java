package domain;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

//This is a stupid and Trivial test, but it is here for CI testing
public class PointTest {
    private Point p;

    @Before
    public void Setup() {
         p = new Point(10, 5);
    }

    @Test
    public void testGetX() throws Exception {
        assertEquals(10, p.getX());
    }

    @Test
    public void testSetX() throws Exception {
        p.setX(9);
        assertEquals(9, p.getX());
    }

    @Test
    public void testGetY() throws Exception {
        assertEquals(5, p.getY());
    }

    @Test
    public void testSetY() throws Exception {
        p.setY(9);
        assertEquals(9, p.getY());
    }
}