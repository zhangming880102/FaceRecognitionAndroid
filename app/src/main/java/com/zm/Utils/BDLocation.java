package com.zm.Utils;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.zm.Bean.PositionBean;

import java.util.List;

public class BDLocation implements BDLocationListener {
    static double EARTH_RADIUS = 6378.137;
    static Activity ac;
    static View v;

    public LocationClient mLocationClient;
    public int code;
    public String province;
    public String city;
    public String country;
    public String bdmsg;
    public String address;
    public double latitude;
    public double lontitude;
    public boolean recieved=false;
    public int timeoutCount=0;
    PositionBean pos;

    Handler handler;
    Runnable runnable;
    boolean bdstart=false;

    public BDLocation(Activity ac){
        mLocationClient = new LocationClient(ac);     //声明LocationClient
        initLocation();
        bdstart=false;
        recieved=false;
        mLocationClient.registerLocationListener( this );
    }
    static double rad(double lat){
        return lat*Math.PI/180;
    }
    public static double computeDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }
    public void checkPos(Activity ac){
        pos=new PositionBean();
        pos.setReceived(false);
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                if( !bdstart) {
                    mLocationClient.start();
                    bdstart=true;
                    handler.postDelayed(runnable,100);
                    timeoutCount=0;
                }else if(!recieved){
                    timeoutCount++;
                    if(timeoutCount<=10) {
                        handler.postDelayed(runnable, 300);
                    }else{
                        mLocationClient.stop();
                    }
                }else{
                    pos.setAddress(address);
                    pos.setCity(city);
                    pos.setLatitude(latitude);
                    pos.setLongtitude(lontitude);
                    pos.setProvince(province);
                    pos.setReceived(true);
                    mLocationClient.stop();
                }
            }
        };

        handler.post(runnable);
    }

    public void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(com.baidu.location.BDLocation location) {

        Log.i("start", "start");
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        sb.append("\n"+location.getAddress());

        code = location.getLocType();
        province = location.getProvince();
        city = location.getCity();
        country = location.getCountry();
        latitude=location.getLatitude();
        lontitude=location.getLongitude();
        address=location.getAddrStr();


        if (location.getLocType() == com.baidu.location.BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());

            sb.append("\ndescribe : ");
            sb.append("gps定位成功");
            recieved=true;
        } else if (location.getLocType() == com.baidu.location.BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());

            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
            recieved=true;
        } else if (location.getLocType() == com.baidu.location.BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
            recieved=true;
        } else if (location.getLocType() == com.baidu.location.BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == com.baidu.location.BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == com.baidu.location.BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        code = location.getLocType();
        bdmsg = sb.toString();
        Log.i("BaiduLocationApiDem", sb.toString());
    }

    public PositionBean getPos() {
        return pos;
    }

}
