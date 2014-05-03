package common;


/**
 * 
 *   Purpose : A helper class to enable thread safety with RecipeManager.
 *           Allows for an a mutex lock to be maintained over multiple
 *           method calls.
 *           This class is copied directly from Chapter 4 of Java Threads, 
 *           2nd edition, O'Reilly Media Inc., January 1999. 
 * 
 */
public class BusyFlag 
{
    /**
     * A reference to the thread that holds the flag
     */
    private Thread busyflag;

    /**
     * 
     * Constructor - Initializes member variables.
     *  
     */
    public BusyFlag()
    {
	busyflag = null;
    }

    /**  
     * 
     * Attempt to get the busy flag. Call tryGetBusyFlag, wait while the
     * method returns false. 
     * 
     */
    public synchronized void getBusyFlag() 
    {
        while (tryGetBusyFlag() == false) 
	{
            try 
            {
                wait();
            } 
	    catch (InterruptedException e) 
	    {
	    }
        }
    }

    /**  
     *
     * Check to see if the current Thread is the one holding the flag.
     * 
     */
    public synchronized boolean tryGetBusyFlag() 
    {
        if (busyflag == null) 
        {
            busyflag = Thread.currentThread();
            return true;
        }

        if (busyflag == Thread.currentThread()) 
	{
            return true;
        }
        
        return false;
    }

    /** 
     * 
     * If the calling thread is the one calling this method, release the flag 
     * and alert any other waiting Threads.
     * 
     */
    public synchronized void freeBusyFlag() 
    {
        if (getBusyFlagOwner() == Thread.currentThread()) 
	{            
            busyflag = null;
            notify();
            
        }
    }

    /**
     * 
     * Method  : Return a reference to the Thread currently holding the busy flag.
     * 
     */
    public synchronized Thread getBusyFlagOwner() 
    {
        return busyflag;
    }

}
