package xml_mngr;


/* Purpose : A helper class to enable thread safety with our XMLManagers.
 *           Allows for an a mutex lock to be maintained over multiple
 *           method calls.
 *           This class is copied directly from Chapter 4 of Java Threads, 
 *           2nd edition, O'Reilly Media Inc., January 1999. 
 */
public class BusyFlag 
{
    //A reference to the thread that holds the flag
    private Thread busyflag;

    /* Purpose : Constructor
     * Method  : Initialize member variables
     * Returns : Nothing
     */
    public BusyFlag()
    {
	busyflag = null;
    }

    /* Purpose : Attempt to get the busy flag, 
     * Method  : Call tryGetBusyFlag, wait while the
     *           method returns false
     * Returns : Nothing
     */
    public synchronized void getBusyFlag() 
    {
        while (tryGetBusyFlag() == false) 
	{
            try 
            {
                wait();
            } 
	    catch (Exception e) 
	    {
	    }
        }
    }

    /* Purpose : Attempt to take possession of the busyFlag
     * Method  : Check to see if the current Thread is the one
     *           holding the flag
     * Returns : Whether the calling Thread is the holder of
     *           the busy flag.
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

    /* Purpose : Attempt to free the busy flag
     * Method  : If the calling thread is the one calling
     *           this method, release the flag and alert 
     *           any other waiting Threads.
     * Returns : Nothing
     */
    public synchronized void freeBusyFlag() 
    {
        if (getBusyFlagOwner() == Thread.currentThread()) 
	{            
            busyflag = null;
            notify();
            
        }
    }

    /* Purpose : Show which Thread has the busy flag
     * Method  : Return a member variable
     * Returns : A reference to the Thread currently
     *           holding the busy flag.
     */
    public synchronized Thread getBusyFlagOwner() 
    {
        return busyflag;
    }

}
