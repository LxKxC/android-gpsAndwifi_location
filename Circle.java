package wifi.localtion.com.localtionwifi;

/**
 * Created by LZY on 18.9.18.
 */

public class Circle{
    private double x;
    private double y;
    private double r;
    public Circle(double X,double Y,double R){
        x=X;
        y=Y;
        r=R;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getR(){
        return r;
    }
}