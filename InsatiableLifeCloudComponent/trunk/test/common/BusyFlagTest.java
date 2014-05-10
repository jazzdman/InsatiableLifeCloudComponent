/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jazzdman
 */
public class BusyFlagTest {
    
    public BusyFlagTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getBusyFlag method, of class BusyFlag.
     */
    @Test
    public void testGetBusyFlag() {
        System.out.println("getBusyFlag");
        BusyFlag instance = new BusyFlag();
        instance.getBusyFlag();
        
        assertSame("Threads not the same.", instance.getBusyFlagOwner(), Thread.currentThread());
    }

    /**
     * Test of freeBusyFlag method, of class BusyFlag.
     */
    @Test
    public void testFreeBusyFlag() {
        System.out.println("freeBusyFlag");
        BusyFlag instance = new BusyFlag();
        instance.getBusyFlag();
        instance.freeBusyFlag();
        assertNull(instance.getBusyFlagOwner());
    }
    
    
    /**
     * Test of tryGetBusyFlag method, of class BusyFlag.
     *
    @Test
    public void testTryGetBusyFlag() {
        System.out.println("tryGetBusyFlag");
        BusyFlag instance = new BusyFlag();
        boolean expResult = false;
        boolean result = instance.tryGetBusyFlag();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    

    /**
     * Test of getBusyFlagOwner method, of class BusyFlag.
     *
    @Test
    public void testGetBusyFlagOwner() {
        System.out.println("getBusyFlagOwner");
        BusyFlag instance = new BusyFlag();
        Thread expResult = null;
        Thread result = instance.getBusyFlagOwner();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */
}
