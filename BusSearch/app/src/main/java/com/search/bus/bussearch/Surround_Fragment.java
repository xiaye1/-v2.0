package com.search.bus.bussearch;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.Text;
import com.amap.api.maps2d.model.TextOptions;

/**
 * 作者 夏晔         修改者：李越     修改者：汪仑
 *  2016/11/29.      2016.11.29       2016.12.1
 *  编写             添加地图控件     添加定位功能
 */
public class Surround_Fragment extends Fragment implements LocationSource,
        AMapLocationListener {

    private Context context;
    private FragmentManager fm;
    private MapView mapView;
    private AMap aMap;
    private MarkerOptions markerOption,markerOption1,markerOption1l,markerOption2l,markerOption3l;
    private Marker marker,marker1,marker1l,marker2l,marker3l;
    private Button btn1,btn2,btn3;
    //天津景点覆盖物添加 -汪仑
    private MarkerOptions markerOption1w;
    private Marker marker1w;
    //夏晔 添加覆盖物
    private MarkerOptions markerOption1x,markerOption2x,markerOption3x,markerOption4x,markerOption5x,markerOption6x,markerOption7x,markerOption8x,markerOption9x;
    private Marker marker1x,marker2x,marker3x,marker4x,marker5x,marker6x,marker7x,marker8x,marker9x;

    //添加定位组件
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.surround_fragment, container, false);
        btn1=(Button)view.findViewById(R.id.clearMap);//获取控件
        btn2=(Button)view.findViewById(R.id.resetMap);
        btn3=(Button)view.findViewById(R.id.Location);
        setListener();
        mapView = (MapView)view.findViewById(R.id.map);//获取控件
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        return view;
    }

    /**
     * 作者：李越                修改者：汪仑
     * 2016.12.1                 2016.12.6
     * 对button添加点击事件      添加定位按钮点击事件
     */
    private void setListener() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aMap != null) {
                    aMap.clear();//清除覆盖物
                    setLocation();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aMap != null) {

                    addMarkersToMap();//添加覆盖物
                }
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation(); //定位
            }
        });
    }
    /**
     * 作者：汪仑
     * 2016.12.1
     * 设置定位属性
     */
    private void setLocation() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }
    
    /**
     * 作者：汪仑
     * 2016.12.1
     * 定位成功后回调函数
     */
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    /**
     * 作者：汪仑
     * 2016.12.1
     * 激活定位
     */

    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 作者：汪仑
     * 2016.12.1
     * 停止定位
     */

    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    

    /**
     * 作者：李越
     * 2016.11.30
     * 在地图中初步添加点击事件以及添加覆盖物
     */
    private void setUpMap() {
        context=getActivity().getApplicationContext();
        //点击infowindow提示
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(context,"你点击了"+marker.getTitle(),Toast.LENGTH_SHORT).show();
            }
        });
        //点击标注覆盖物显示infowindow
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (aMap != null) {
                    jumpPoint(marker);
                }
                marker.showInfoWindow();
                return true;
            }
        });
        //点击地图隐藏infowindow
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                xyAddHide();
                wlAddHide();
                marker.hideInfoWindow();
                marker1.hideInfoWindow();
                marker1l.hideInfoWindow();
                marker2l.hideInfoWindow();
                marker3l.hideInfoWindow();
            }
        });
    }

    private void xyAddHide(){
        marker1x.hideInfoWindow();
        marker2x.hideInfoWindow();
        marker3x.hideInfoWindow();
        marker4x.hideInfoWindow();
        marker5x.hideInfoWindow();
        marker6x.hideInfoWindow();
        marker7x.hideInfoWindow();
        marker8x.hideInfoWindow();
        marker9x.hideInfoWindow();
    }

    private void wlAddHide(){
        marker1w.hideInfoWindow();
    }

    /**
     * 作者：李越
     * marker点击时跳动一下
     * 2016.12.6
     */
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * markerLatlng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
    
    /**
     * 作者：李越
     * 2016.11.30
     * 在地图中添加覆盖物
     */
    private void addMarkersToMap() {
        xyAddMarkers();
        wlAddMarkers();

        //--------------------示例添加
        //标注覆盖物
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.918058, 116.397026))
                .title("故宫")
                .snippet("AAAAA级\n北京故宫是中国明清两代的皇家宫殿，旧称为紫禁城，位于北京中轴线的中心，是中国古代宫廷建筑之精华。")
                .draggable(true);
        marker = aMap.addMarker(markerOption);

        //文本覆盖物
        TextOptions textOptions1 = new TextOptions().position(new LatLng(39.908692, 116.397477))
                .text("天安门").fontColor(Color.WHITE)
                .backgroundColor(Color.BLACK).fontSize(30).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD)
                ;
        aMap.addText(textOptions1);
        //标注覆盖物
        markerOption1 = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.908692, 116.397477))
                .title("天安门")
                .snippet("天安门广场，位于北京市中心\n天安门广场记载了中国人民不屈不挠的革命精神和大无畏的英雄气概")
                .draggable(true);
        marker1 = aMap.addMarker(markerOption1);
        //----------------正式添加
        //------------------------------石家庄
        markerOption1l = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(37.722482, 114.768948))
                .title("赵州桥")
                .snippet("AAAA级景区\n又称安济桥，俗名大石桥")
                .draggable(true);
        marker1l = aMap.addMarker(markerOption1l);

        markerOption2l = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(37.74921, 114.784331))
                .title("柏林禅寺")
                .snippet("中国著名佛教禅寺，北方佛教的一座重镇，赵州祖庭所在地")
                .draggable(true);
        marker2l = aMap.addMarker(markerOption2l);

        markerOption3l = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(38.076899, 114.458284))
                .title("水上公园")
                .snippet("AAA级景区\n水上公园由三湖九岛构成，有国内外建筑艺术、园林艺术、雕塑艺术精品")
                .draggable(true);
        marker3l = aMap.addMarker(markerOption3l);
    }

    private void wlAddMarkers() {
        //天津覆盖物添加 -汪仑
        //天津之眼
        markerOption1w = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.160042,117.193405))
                .title("天津之眼")
                .snippet("天津的标志，是世界上唯一建在桥上的摩天轮")
                .draggable(true);
        marker1w = aMap.addMarker(markerOption1w);

    }

    private void xyAddMarkers() {
        //夏晔        添加北京景点覆盖物    2016/12/8
        markerOption1x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.91073, 116.394351))
                .title("中山公园")
                .snippet("国家AAAA级旅游景区\n占地23万平方米，是—座纪念性的古典坛庙园林。\n 它原是明清两代的社稷坛，与太庙（今劳动人民文化宫）一起沿袭周代以来“左祖右社”的礼制建造。")
                .draggable(true);
        marker1x = aMap.addMarker(markerOption1x);


               markerOption2x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.92819, 116.388717))
                .title("北海公园")
                .snippet("国家AAAA级旅游景区\n城内景山西侧，在故宫的西北面，与中海、南海合称三海。\n 是中国现存最古老、最完整、最具综合性和代表性的皇家园林之一")
                .draggable(true);
        marker2x = aMap.addMarker(markerOption2x);


        markerOption3x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(40.35619, 116.016727))
                .title("八达岭长城")
                .snippet("是中国古代伟大的防御工程万里长城的重要组成部分\n明长城的八达岭段被称作“玉关天堑”，为明代居庸关八景之一。\n 是举世闻名的旅游胜地。")
                .draggable(true);
        marker3x = aMap.addMarker(markerOption3x);

        markerOption4x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.496439, 116.334665))
                .title("北京野生动物园")
                .snippet("是经原国家林业局批准，北京市政府立项、\n北京绿野晴川有限公司投资建设的集动物保护、\n野生动物驯养繁殖及科普教育为一体的大型自然生态公园。")
                .draggable(true);
        marker4x = aMap.addMarker(markerOption4x);

        markerOption5x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(40.014992, 116.392515))
                .title("北京奥林匹克公园")
                .snippet("是包含体育赛事、会展中心、科教文化、休闲购物等多种功能在内的综合性市民公共活动中心。\n集中体现了“科技、绿色、人文”三大理念\n融合了办公、商业、酒店、文化、体育、会议、居住多种功能的新型城市区域")
                .draggable(true);
        marker5x = aMap.addMarker(markerOption5x);
        markerOption6x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.989042, 116.190236))
                .title("香山公园")
                .snippet("是一座具有山林特色的皇家园林\n香山公园有香山寺、洪光寺等著名旅游景点\n2001年被国家旅游局评为AAAA景区，2002年被评为首批北京市精品公园。")
                .draggable(true);
        marker6x = aMap.addMarker(markerOption6x);
        markerOption7x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.984671, 116.493609))
                .title("北京798艺术区")
                .snippet("因当代艺术和798生活方式闻名于世。\n原为原国营798厂等电子工业的老厂区所在地\n")
                .draggable(true);
        marker7x = aMap.addMarker(markerOption7x);
        markerOption8x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.936251, 116.416704))
                .title("南锣鼓巷")
                .snippet("南锣鼓巷是一条胡同\n它是北京最古老的街区之一，是我国唯一完整保存着元代胡同院落肌理、\n规模最大、品级最高、资源最丰富的棋盘式传统民居区，也是最赋有老北京风情的街巷。")
                .draggable(true);
        marker8x = aMap.addMarker(markerOption8x);
        markerOption9x = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(39.866719, 116.490519))
                .title("欢乐谷")
                .snippet("北京欢乐谷是国家4A级旅游景区\n北京欢乐谷设置了120余项体验项目\n获得过\"中国文化创意产业高成长企业百强\"\"首都旅游紫禁杯先进集体\"\"首都文明旅游景区\"等荣誉。")
                .draggable(true);
        marker9x = aMap.addMarker(markerOption9x);
    }

    /**
     * 作者：李越
     * 2016.11.29
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 作者：李越
     * 2016.11.29
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 作者：李越
     * 2016.11.29
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 作者:李越
     * 2016.11.29
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}