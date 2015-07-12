package it.inserrafesta.iseomap;

/**
 * Created by Andrea on 12/07/2015.
 */
public interface NetworkMonitorListener {

    public void connectionEstablished();
    public void connectionLost();
    public void connectionCheckInProgress();
}
