package com.virtualino.pins;

/**
 *
 * @author lemrey
 */
public interface GenericPin {

    public int number();
    public void assign();
    public void release();
    public boolean isFree();
    public boolean isAnalog();
    
}
