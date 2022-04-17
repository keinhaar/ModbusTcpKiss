package de.exware.modbus;

/**
 * A class that calculates a moving average value.
 */
public class MovingAverageValue
{
    private double[] values;
    private double sum;
    private int index = 0;
    
    public MovingAverageValue()
    {
        this(3);
    }
    
    public MovingAverageValue(int valueCount)
    {
        values = new double[valueCount];
    }
    
    synchronized public double getAverageValue()
    {
        return sum / values.length;
    }
    
    synchronized public void addValue(double value)
    {
        sum -= values[index];
        values[index++] = value;
        sum += value;
        if(index >= values.length)
        {
            index = 0;
        }
    }
    
    public static void main(String[] args)
    {
        MovingAverageValue mv = new MovingAverageValue();
        System.out.println(mv.getAverageValue());
        mv.addValue(10);
        System.out.println(mv.getAverageValue());
        mv.addValue(10);
        System.out.println(mv.getAverageValue());
        mv.addValue(10);
        System.out.println(mv.getAverageValue());
        mv.addValue(20);
        System.out.println(mv.getAverageValue());
        mv.addValue(20);
        System.out.println(mv.getAverageValue());
        mv.addValue(20);
        System.out.println(mv.getAverageValue());
    }
}
