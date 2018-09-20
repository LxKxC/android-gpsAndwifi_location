package wifi.localtion.com.localtionwifi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by LZY on 18.9.13.
 */

public class Draw extends View{

    public Draw(Context context) {
        super(context);

        ImageView imageView = (ImageView)findViewById(R.id.iv);
        Button button =(Button)findViewById(R.id.bt_cal);
//        TextView tv_count = (TextView)findViewById(R.id.tv_count_wifi);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);


        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        imageView.measure(w,h);
        int height = imageView.getMeasuredHeight();
        int width = imageView.getMeasuredWidth();

//        int width = imageView.getWidth();
//        int height = imageView.getHeight();
        Log.i("tag",width+"");

        Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas();

        canvas.drawLine(1,10,200,400,paint);

        imageView.draw(canvas);
    }
}
