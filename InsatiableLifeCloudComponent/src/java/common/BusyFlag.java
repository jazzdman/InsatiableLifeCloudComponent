/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

/**
 *
 * @author jazzdman
 */
public class BusyFlag 
{
    private Thread busyFlag;
    
    public BusyFlag()
    {
        busyFlag = null;
    }
    
    public synchronized void getBusyFlag()
    {
        while(tryGetBusyFlag() == false)
        {
            try
            {
                wait();
            }
             catch(Exception e)
            {
                
            }
        }
       
        
    }
    
    public synchronized boolean tryGetBusyFlag()
    {
        if(busyFlag == null)
        {
            busyFlag = Thread.currentThread();
            return true;
        }
        
        if(busyFlag == Thread.currentThread())
        {
            return true;
        }
        
        return false;
    }
    
    public synchronized void freeBusyFlag()
    {
        if(getBusyFlagOwner() == Thread.currentThread())
        {
            busyFlag = null;
            notify();
        }
    }
    
    public synchronized Thread getBusyFlagOwner()
    {
        return busyFlag;
    }
}
