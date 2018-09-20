package wifi.localtion.com.localtionwifi;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static java.lang.Math.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "tag";
    public String provider;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;

    public ImageView imageView;
    public Button button_cal;
    public Button button_ginf;
    public TextView tv_info_1;
    public TextView tv_info_2;
    public TextView tv_res;
    public Spinner spinner;

    public ArrayAdapter arrayAdapter;

    public Handler handler;

    public int count = 0;
    public String[] str;
    public String s[][] = new String[200][4];
    public String MAC = "";
    public String fre = "";
    public String DBM = "";
    public int pos = 0;
    public double lo[] = new double[2];


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imageView = (ImageView)findViewById(R.id.iv);
        button_cal =(Button)findViewById(R.id.bt_cal);
        button_ginf =(Button)findViewById(R.id.bt_ginf);
        tv_info_1 = (TextView)findViewById(R.id.tv_info_1);
        tv_info_2 = (TextView)findViewById(R.id.tv_info_2);
        tv_res = (TextView)findViewById(R.id.tv_res);
        spinner = (Spinner)findViewById(R.id.spinner);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x01:
                        double a[][] = (double[][]) msg.obj;
//                        Log.i(TAG, String.valueOf(a[1].length));
//                        Log.i(TAG, String.valueOf(a[0][1]));
                        Log.i(TAG+" distance:", String.valueOf(calculateDistance(a[0][0],msg.arg1)));
                        Log.i(TAG+" distance:", String.valueOf(calculateDistance(a[0][1],msg.arg1)));
                        Log.i(TAG+" distance:", String.valueOf(calculateDistance(a[0][2],msg.arg1)));
                        double dis[] ={calculateDistance(a[0][0],msg.arg1),calculateDistance(a[0][1],msg.arg1),calculateDistance(a[0][2],msg.arg1)};
                        func_draw(a[1],dis);
                        break;
                    case 0x02:
                        if (msg.arg1==1){
                            Toast.makeText(MainActivity.this,"there's no wifi",Toast.LENGTH_SHORT).show();
                        }else if (msg.arg1==2){
//                            Toast.makeText(MainActivity.this,"scanning wifi...",Toast.LENGTH_SHORT).show();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String[][] str_1 = (String[][]) msg.obj;
                                    tv_info_1.setText("WIFI个数："+str.length);
                                    tv_info_2.setText("MAC:"+MAC+"    DBM:"+str_1[pos][3]);
                                }
                            });


                        }else{
                            Toast.makeText(MainActivity.this,"err",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0x03:
//                        Log.i("ap","12312");
                        break;
                    case 0x04:
                        Log.i("ap", String.valueOf(msg.obj));
                        break;
                }

            }
        };


        button_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lo!=null){
                    tv_res.setTextSize(12);
                    tv_res.setText("经度："+lo[0]+"\n纬度："+lo[1]);
                }else {
                    Toast.makeText(MainActivity.this,"请先采集数据",Toast.LENGTH_SHORT).show();
                }

            }
        });

        button_ginf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Message msg = Message.obtain();
                msg.what = 0x01;
                double d[][] = {{63,64,63},{31.0060146359,103.6296719320,31.0058307179,103.6298167729,31.0056642833,103.6296341813}};
                Log.i(TAG,fre);
                msg.arg1 = Integer.parseInt(fre);
                msg.obj = d;
                handler.handleMessage(msg);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String a = str[position];
                String b = a.substring(0,17);
                String c = a.substring(18,21);
                String d = a.substring(22,26);
                MAC = b;
                DBM = c;
                fre = d;
                pos = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    ScanWifi();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        }).start();

        if (str==null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,str);
            spinner.setAdapter(arrayAdapter);
        }



//        GetPosition();
        Log.i(TAG,"res:"+Distent.algorithm(100,100,100.0001,100));
//        Log.i(TAG,"res:"+Distent.getDistance(100,100,100.000001,100));
//        Log.i(TAG,"res:"+Distent.getAngle1(116.407947,39.926531,116.407947,39.926511));

    }


    @SuppressLint("ClickableViewAccessibility")
    private void func_draw(final double[] points_d,double[] dis) {

        Paint paint = new Paint();

        int w = imageView.getWidth();
        int h = imageView.getHeight();

        Bitmap bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
//        canvas.rotate(-30);
//        canvas.translate(-h,0);
        final float[] points = {(float) points_d[0],(float) points_d[1],(float) points_d[2],(float) points_d[3],(float) points_d[4],(float) points_d[5]};

        float p_2[] = {abs(points[0]-points[2])*1400000, abs(points[1]-points[3])*1400000};
        float p_3[] = {abs(points[0]-points[4])*1400000, abs(points[1]-points[5])*1400000};
        float p_1[] = {0,0};
//        Log.i(TAG,p_2[0]+" "+p_2[1]+" "+p_3[0]+" "+p_3[1]);

        points[0] = p_1[0];
        points[1] = p_1[1];
        points[2] = p_2[0];
        points[3] = p_2[1];
        points[4] = p_3[0];
        points[5] = p_3[1];

        float avg_w = w/2-(points[0]+points[2]+points[4])/3;
        float avg_h = h/2-(points[1]+points[3]+points[5])/3;

        points[0] = points[0] + avg_w;
        points[2] = points[2] + avg_w;
        points[4] = points[4] + avg_w;
        points[1] = points[1] + avg_h;
        points[3] = points[3] + avg_h;
        points[5] = points[5] + avg_h;

        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setAlpha(100);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(points[0],points[1], (float) dis[0]*20,paint);
        canvas.drawCircle(points[2],points[3], (float) dis[1]*20,paint);
        canvas.drawCircle(points[4],points[5], (float) dis[2]*20,paint);

        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(8);
        canvas.drawLines(new float[]{points[0],points[1],points[2],points[3]},paint);
        canvas.drawLines(new float[]{points[4],points[5],points[2],points[3]},paint);
        canvas.drawLines(new float[]{points[4],points[5],points[0],points[1]},paint);

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(35);
        canvas.drawText(Distent.algorithm(points_d[0],points_d[1],points_d[2],points_d[3])+"m",(points[0]+points[2])/2,(points[1]+points[3])/2,paint);
        canvas.drawText(Distent.algorithm(points_d[0],points_d[1],points_d[4],points_d[5])+"m",(points[2]+points[4])/2,(points[3]+points[5])/2,paint);
        canvas.drawText(Distent.algorithm(points_d[2],points_d[3],points_d[4],points_d[5])+"m",(points[4]+points[0])/2,(points[5]+points[1])/2,paint);

        Circle circle_1 = new Circle(points[0],points[1], (float) dis[0]*20);
        Circle circle_2 = new Circle(points[2],points[3], (float) dis[1]*20);
        Circle circle_3 = new Circle(points[4],points[5], (float) dis[2]*20);

        float[] cir_1 = new CirIntersect(circle_1,circle_2).intersect();
        float[] cir_2 = new CirIntersect(circle_2,circle_3).intersect();
        float[] cir_3 = new CirIntersect(circle_3,circle_1).intersect();
        float[] cir = {cir_1[0],cir_1[1],cir_1[2],cir_1[3],cir_2[0],cir_2[1],cir_2[2],cir_2[3],cir_3[0],cir_3[1],cir_3[2],cir_3[3]};

        paint.setStrokeWidth(20);
        paint.setColor(Color.RED);
        canvas.drawPoints(cir,paint);

//        float area_s = 999999999;
        float girth_s = 999999999;
        float area_p[] = {0,0};
        for (int j=0;j<cir.length;j+=2){
            for (int i=j+4;i<cir.length;i+=2){
                float area = cal_area(cir[j],cir[j+1],cir[j+2],cir[j+3],cir[i],cir[i+1]);
                float girth = cal_girth(cir[j],cir[j+1],cir[j+2],cir[j+3],cir[i],cir[i+1]);
//                Log.i(TAG,"area "+area);
//                Log.i(TAG,cir[j]+" "+cir[j+1]+" "+cir[j+2]+" "+cir[j+3]+" "+cir[i]+" "+cir[i+1]);
                if (girth<girth_s){
                    area_p[0] = (cir[j]+cir[j+2]+cir[i])/3;
                    area_p[1] = (cir[j+1]+cir[j+3]+cir[i+1])/3;
                }
            }
        }

        paint.setStrokeWidth(10);
        paint.setColor(Color.BLACK);
        canvas.drawPoints(area_p,paint);

        imageView.setImageBitmap(bitmap);

        double x = (area_p[0]-points[0])*(Distent.algorithm(points_d[0],points_d[1],points_d[2],points_d[3])/Math.sqrt(pow(points[0]-points[2],2)+pow(points[1]-points[3],2)));
        double y = (area_p[1]-points[1])*(Distent.algorithm(points_d[0],points_d[1],points_d[2],points_d[3])/Math.sqrt(pow(points[0]-points[2],2)+pow(points[1]-points[3],2)));

        Log.i(TAG,x+" "+y);
        Log.i(TAG,points_d[0]+" "+points_d[1]);
        double x_i = points_d[0]+x/0.193304*0.00001;
        double y_i = points_d[1]+x/0.193304*0.00001;
        Log.i(TAG,x_i+" "+y_i);
        lo[0] = x_i;
        lo[1] = y_i;


        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float x = event.getX();
                        float y = event.getY();
                        Log.i(TAG, points[0] + "   " + x);
                        if (x > points[0] - 30 && x < points[0] + 30 && y > points[1] - 30 && y < points[1] + 30) {
                            Toast.makeText(MainActivity.this, "longitude:" + points_d[0] + "\nlatitude:" + points_d[1], Toast.LENGTH_SHORT).show();
                        } else if (x > points[2] - 30 && x < points[2] + 30 && y > points[3] - 30 && y < points[3] + 30) {
                            Toast.makeText(MainActivity.this, "longitude:" + points_d[2] + "\nlatitude:" + points_d[3], Toast.LENGTH_SHORT).show();
                        } else if (x > points[4] - 30 && x < points[4] + 30 && y > points[5] - 30 && y < points[5] + 30) {
                            Toast.makeText(MainActivity.this, "longitude:" + points_d[4] + "\nlatitude:" + points_d[5], Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case MotionEvent.ACTION_UP:

                        break;

                }

                return false;
            }
        });

    }

    private float cal_girth(float v, float v1, float v2, float v3, float v4, float v5) {
        float x = (float) sqrt(pow(v2-v,2) + pow(v3-v1,2));
        float y = (float) sqrt(pow(v4-v,2) + pow(v5-v1,2));
        float z = (float) sqrt(pow(v2-v4,2) + pow(v3-v5,2));
        return x+y+z;
    }

    private float cal_area(float v, float v1, float v2, float v3, float v4, float v5) {
        float x = (float) sqrt(pow(v2-v,2) + pow(v3-v1,2));
        float y = (float) sqrt(pow(v4-v,2) + pow(v5-v1,2));
        float z = (float) sqrt(pow(v2-v4,2) + pow(v3-v5,2));
        float s=(x+y+z)/2;
        double res = sqrt(s*(s-x)*(s-y)*(s-z));
        return (float) res;

    }


    private void GetPosition() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
        }
        else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;

        } else {
            Toast.makeText(this, "请检查网络或GPS是否打开",
                    Toast.LENGTH_LONG).show();
            return;
        }


        while(location == null){
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            locationManager.requestLocationUpdates(provider,1000,2, locationListener);

        }


        Log.i(TAG, "纬度："+location.getLatitude());

        Log.i(TAG, "经度："+location.getLongitude());

        Log.i(TAG, "海拔："+location.getAltitude());

        Log.i(TAG, "时间："+location.getTime());

        Log.i(TAG, "getAccuracy:"+location.getAccuracy());
        Log.i(TAG, "getProvider:"+location.getProvider());
        Log.i(TAG, "getBearing:"+location.getBearing());
        Log.i(TAG, "getSpeed:"+location.getSpeed());
        Log.i(TAG, "tostring:"+location.toString());



        Toast.makeText(MainActivity.this,location.getLatitude()+","+location.getLongitude()+","+location.getAltitude(),Toast.LENGTH_SHORT).show();


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "纬度："+location.getLatitude());

                Log.i(TAG, "经度："+location.getLongitude());

                Log.i(TAG, "海拔："+location.getAltitude());

                Log.i(TAG, "时间："+location.getTime());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }


    private void ScanWifi() {

        final String[] mac = {""};
//        Looper.prepare();
        Message msg = Message.obtain();
        msg.what =0x02;

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo =wifiManager.getConnectionInfo();
//        Log.i("tag",wifiInfo.toString());
        int Ip = wifiInfo.getIpAddress();
        String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
//        Log.i("tag",strIp);
//
//        Log.i("tag", String.valueOf(wifiInfo.getRssi()));
//        Log.i("tag", String.valueOf(wifiInfo.getMacAddress()));
//        Log.i("tag", String.valueOf(wifiInfo.getNetworkId()));
//        Log.i("tag", String.valueOf(wifiInfo.getSupplicantState().toString()));

        wifiManager.startScan();
        List<ScanResult> list = (List<ScanResult>) wifiManager.getScanResults();

        if (list==null){
            msg.arg1 = 1;
            handler.handleMessage(msg);
        }else{
            msg.arg1 = 2;
            msg.arg2 = list.size();
            str = new String[list.size()];

            for (int i=0;i<list.size();i++){

                String channelWidth = "";
                switch (list.get(i).channelWidth){
                    case 0:
                        channelWidth = "CHANNEL_WIDTH_20MHZ";
                        break;
                    case 1:
                        channelWidth = "CHANNEL_WIDTH_40MHZ";
                        break;
                    case 2:
                        channelWidth = "CHANNEL_WIDTH_80MHZ";
                        break;
                    case 3:
                        channelWidth = "CHANNEL_WIDTH_160MHZ";
                        break;
                    case 4:
                        channelWidth = "CHANNEL_WIDTH_80MHZ_PLUS_MHZ";
                        break;
                }
//
//                Log.i(TAG,"SSID: "+list.get(i).SSID);
//                Log.i(TAG,"BSSID: "+list.get(i).BSSID);
//                Log.i(TAG,"capabilities: "+list.get(i).capabilities);
//                Log.i(TAG, "frequency: "+list.get(i).frequency);
//                Log.i(TAG, "centerFreq0: "+list.get(i).centerFreq0);
//                Log.i(TAG, "centerFreq1: "+list.get(i).centerFreq1);
//                Log.i(TAG, "channelWidth: "+channelWidth);
//                Log.i(TAG, "level: "+list.get(i).level);
//                Log.i(TAG, "timestamp: "+list.get(i).timestamp);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    Log.i(TAG, "is80211mcResponder: "+list.get(i).is80211mcResponder());
//                    Log.i(TAG, "isPasspointNetwork: "+list.get(i).isPasspointNetwork());
//                }
//                Log.i(TAG, "          ");

                str[i]=list.get(i).BSSID+" "+list.get(i).level+" "+list.get(i).frequency+" "+list.get(i).SSID;
//                Log.i(TAG,str[i]);

                s[i][0] = list.get(i).SSID;
                s[i][1] = list.get(i).BSSID;
                s[i][2] = String.valueOf(list.get(i).frequency);
                s[i][3] = String.valueOf(list.get(i).level);

                msg.obj = s;
            }
        }
        handler.handleMessage(msg);
//        Looper.loop();
    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * log10(freqInMHz)) + abs(signalLevelInDb)) / 20.0;
        return pow(10.0, exp);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }



}
