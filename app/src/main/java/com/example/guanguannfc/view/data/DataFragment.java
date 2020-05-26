package com.example.guanguannfc.view.data;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.guanguannfc.R;
import com.example.guanguannfc.controller.dataVisualization.Allactivity;
import com.example.guanguannfc.controller.dataVisualization.datadisplay;
import com.example.guanguannfc.controller.dataVisualization.EchartOptionUtil;
import com.example.guanguannfc.controller.dataVisualization.EchartView;
import com.example.guanguannfc.controller.timeManagement.GetTime;
import com.example.guanguannfc.view.homepage.HomePageActivity;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DataFragment extends Fragment {
    private View view;
    private Spinner spinner_times,spinner_types,spinner_acts,spinner_sorts;
    private ConstraintLayout lay_datashow,lay_actshow,lay_time,lay_personset;
    private LinearLayout ll_container;
    private Button bt_starttime,bt_endtime,bt_acttype,bt_confirmtime,bt_person,bt_manage,bt_quit;
    private TextView tv_prompt,tv_noInfo,tv_noActInfo,tv_acttype;
    private String userName,txt_timeType,txt_showType,txt_startTime,txt_endTime,txt_actType;
    public String txt_showActType,txt_sortType;
    private TextView tv_data,tv_allact;
    private String[] allActName;
    private String[][] actAndTime;
    private Object[] ob_dataShow;
    private Object echart_act[];
    private Object echart_time[];
    private String[][] ob_actShow;
    private String[][] actInfo;
    private EchartView myWebView;
    private datadisplay dd=new datadisplay(getActivity());
    private Allactivity allactivity=new Allactivity(getActivity());
    private GetTime getTime=new GetTime();
    private List<DataShow> dataShowList = new ArrayList<DataShow>();
    private ListView actlist;
    private DataShowAdapter dataShowAdapter;
    private List<ActShow> actShowList = new ArrayList<ActShow>();
    private ListView lv_allactlist = null;
    private ActShowAdapter actShowAdapter;
    private ConstraintLayout.LayoutParams layoutParams;
    private boolean isCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_data, container, false);
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            userName = bundle.getString("username");
//            isCount = bundle.getBoolean("isCount");
        }

        dd=new datadisplay(getActivity());
        allactivity=new Allactivity(getActivity());
        initView();
        actlist.setAdapter(dataShowAdapter);
        lv_allactlist.setAdapter(actShowAdapter);



        initSpinner();
        initWebView();
        checkClick();

//        Toast.makeText(getActivity(),"onCreate",Toast.LENGTH_LONG).show();

        return view;
    }
    private void initView(){
        actlist=view.findViewById(R.id.listview_actlist);
        lv_allactlist=view.findViewById(R.id.lv_allacts);
        dataShowAdapter = new DataShowAdapter(getActivity(),R.layout.item_datashow,dataShowList);
        actShowAdapter = new ActShowAdapter(getActivity(),R.layout.item_actshow,actShowList);
        myWebView=view.findViewById(R.id.webview_acts);
        spinner_times=view.findViewById(R.id.spinner_time);
        spinner_types=view.findViewById(R.id.spinner_type);
        spinner_acts=view.findViewById(R.id.spinner_acttype);
        spinner_sorts=view.findViewById(R.id.spinner_sort);
        lay_datashow=view.findViewById(R.id.layout_show);
        lay_actshow=view.findViewById(R.id.layout_allact);
        lay_actshow.setVisibility(View.INVISIBLE);
        lay_time=view.findViewById(R.id.layout_chosetime);
        lay_time.setVisibility(View.INVISIBLE);
        lay_personset=view.findViewById(R.id.layout_person);
//        lay_personset.setVisibility(View.INVISIBLE);
        bt_starttime=view.findViewById(R.id.button_starttime);
        bt_endtime=view.findViewById(R.id.button_endtime);
        bt_acttype=view.findViewById(R.id.button_acttype);
        bt_confirmtime=view.findViewById(R.id.button_time_confirm);
//        bt_person=view.findViewById(R.id.button_personset);
//        bt_manage=view.findViewById(R.id.button_manage);
        bt_quit=view.findViewById(R.id.button_quit);
        tv_prompt=view.findViewById(R.id.text_prompt);
        tv_noInfo=view.findViewById(R.id.text_noInfo);
        tv_noActInfo=view.findViewById(R.id.text_noActInfo);
        tv_data=view.findViewById(R.id.text_show);
        tv_allact =view.findViewById(R.id.text_allact);
        ll_container = view.findViewById(R.id.ll_container);
        layoutParams = (ConstraintLayout.LayoutParams) lay_actshow.getLayoutParams();
//        userName="aaa";
        txt_actType="";
        txt_showActType="全部";
        txt_sortType="最新活动在前";
        txt_timeType="今日";
        txt_showType="列表";
        txt_startTime=getTime.getBeginTime("本日");
        txt_endTime=txt_startTime;


        allActName=allactivity.allacttype(userName);
        ArrayAdapter<String> actAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,allActName);
        spinner_acts.setAdapter(actAdapter);



    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getActivity(),"onResume"+isCount,Toast.LENGTH_LONG).show();
        isCount= HomePageActivity.isCount;
        if (isCount){
            tv_prompt.setVisibility(View.VISIBLE);
            layoutParams.setMargins(0, 200, 0, 0);
        }
        else {
            tv_prompt.setVisibility(View.GONE);
            layoutParams.setMargins(0, 100, 0, 0);
        }
    }

    private void refreshEChart(){
        Log.i("gy","刷新图表");
        echart_time=new Object[0];
        echart_act=new Object[0];

    }



    private void initDataShow(String[][] array){
        tv_noInfo.setVisibility(View.GONE);
        dataShowList.clear();
        for(int i=0;i<array.length;i++){
            DataShow dataShow = new DataShow(array[i][0],array[i][1]);
            dataShowList.add(dataShow);
        }
    }

    private void initActShow(String[][] array){
        actShowList.clear();
        for(int i=0;i<array.length;i++){
            ActShow actShow = new ActShow(array[i]);
            actShowList.add(actShow);
        }
    }

    private void initWebView(){

//设置载入页面自适应手机屏幕，居中显示
        WebSettings mWebSettings = myWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
    }
    private void initSpinner(){

        spinner_times.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeType = parent.getItemAtPosition(position).toString();
                txt_timeType=timeType;
//                tv_noInfo.setVisibility(View.GONE);
//                actlist.setVisibility(View.VISIBLE);

                if (position==3){
                    lay_time.setVisibility(View.VISIBLE);
                    ll_container.setVisibility(View.VISIBLE);
                }
                else {
                    if(position==0){
                        txt_startTime=getTime.getBeginTime("本日");
                        txt_endTime=getTime.getBeginTime("本日");
//                        Toast.makeText(Data.this,txt_startTime,Toast.LENGTH_LONG).show();

                    }
                    else if(position==1){
                        txt_startTime=getTime.getBeginTime("本周");
                        txt_endTime=getTime.getBeginTime("本日");
//                        Toast.makeText(Data.this,txt_startTime,Toast.LENGTH_LONG).show();
                    }
                    else if(position==2){
                        txt_startTime=getTime.getBeginTime("本月");
                        txt_endTime=getTime.getBeginTime("本日");
//                        Toast.makeText(Data.this,txt_startTime,Toast.LENGTH_LONG).show();
                    }
//                    Toast.makeText(Data.this,txt_startTime+txt_endTime,Toast.LENGTH_LONG).show();

                    ob_dataShow=dd.Datadplay(userName,txt_startTime,txt_endTime,txt_actType,txt_showType);

//                    Log.i("gy",ob_dataShow[0].toString());
//                    Log.i("gy","time获取到数据"+txt_timeType+""+txt_showType);



                    if (ob_dataShow!=null){

                        tv_noInfo.setVisibility(View.GONE);
                        actAndTime=(String[][])ob_dataShow[0];

                        if (txt_showType.equals("列表")) {
                            actlist.setVisibility(View.VISIBLE);
                            Log.i("gy","获取到数据"+txt_timeType+""+txt_showType);

                            initDataShow(actAndTime);
                            actlist.setAdapter(dataShowAdapter);
                        }
                        else {
                            myWebView.setVisibility(View.VISIBLE);
                            refreshEChart();
                            int len = actAndTime.length;
                            echart_act = new Object[len];
                            echart_time= new Object[len];
                            for(int i =0;i<len;i++){
                                echart_act[i]=actAndTime[i][0];
                                echart_time[i]=((float[]) ob_dataShow[1])[i];
                            }
                            if (txt_showType.equals("柱状图")){
                                Log.i("gy","获取到数据"+txt_timeType+""+txt_showType);
                                myWebView.refreshEchartsWithOption(EchartOptionUtil.getBarChartOptions(echart_act,echart_time));
                            }
                            else if (txt_showType.equals("折线图")){
                                Log.i("gy","获取到数据"+txt_timeType+""+txt_showType);
                                myWebView.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(echart_act, echart_time));
                            }
                        }



                    }
                    else{
                        Log.i("gy","没有获取到数据");
                        actlist.setVisibility(View.INVISIBLE);
                        myWebView.setVisibility(View.INVISIBLE);
                        tv_noInfo.setVisibility(View.VISIBLE);
//                        actlist.setVisibility(View.INVISIBLE);
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String showType = parent.getItemAtPosition(position).toString();
//                Toast.makeText(Data.this,""+position,Toast.LENGTH_LONG).show();
                txt_showType=showType;
                Log.i("gy","showType:"+txt_showType);

                if (position==0){
                    myWebView.setVisibility(View.INVISIBLE);
                    actlist.setVisibility(View.VISIBLE);

                }
                else{
                    myWebView.setVisibility(View.VISIBLE);
                    actlist.setVisibility(View.INVISIBLE);

                }
                ob_dataShow=dd.Datadplay(userName,txt_startTime,txt_endTime,txt_actType,txt_showType);
                if (ob_dataShow!=null){
                    if (position==0){
                        actAndTime=(String[][])ob_dataShow[0];
                        initDataShow(actAndTime);
                        actlist.setAdapter(dataShowAdapter);
                    }
                    else {
                        refreshEChart();
                        int len = actAndTime.length;
                        echart_act = new Object[len];
                        echart_time= new Object[len];
                        for(int i =0;i<len;i++){
                            echart_act[i]=actAndTime[i][0];
                            echart_time[i]=((float[]) ob_dataShow[1])[i];
                        }

                        Log.i("gy","act:"+echart_act[0]+"time:"+echart_time[0]);

                        if(position == 1){
                            myWebView.refreshEchartsWithOption(EchartOptionUtil.getBarChartOptions(echart_act,echart_time));

                        }
                        if(position == 2){
                            myWebView.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(echart_act, echart_time));

                        }
                    }

                }
                else{
                    actlist.setVisibility(View.INVISIBLE);
                    myWebView.setVisibility(View.INVISIBLE);
                    tv_noInfo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_acts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String showActType = parent.getItemAtPosition(position).toString();
                txt_showActType=showActType;
//                Toast.makeText(Data.this,txt_showActType+txt_sortType,Toast.LENGTH_LONG).show();

                ob_actShow=allactivity.sortedactivity(userName,txt_showActType,txt_sortType);

                if (ob_actShow !=null){
                    lv_allactlist.setVisibility(View.VISIBLE);
                    tv_noActInfo.setVisibility(View.GONE);
                    initActShow(ob_actShow);
                    lv_allactlist.setAdapter(actShowAdapter);
                }
                else {
                    lv_allactlist.setVisibility(View.GONE);
                    tv_noActInfo.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_sorts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortType = parent.getItemAtPosition(position).toString();
                txt_sortType=sortType;
//                Toast.makeText(Data.this,txt_sortType,Toast.LENGTH_LONG).show();

                ob_actShow=allactivity.sortedactivity(userName,txt_showActType,txt_sortType);

                if (ob_actShow !=null){
                    initActShow(ob_actShow);
                    lv_allactlist.setAdapter(actShowAdapter);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        处理两次点击同一个选项
        spinner_times.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Class<?> clazz = AdapterView.class;
                    Field field = clazz.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);
                    field.setInt(spinner_times,AdapterView.INVALID_POSITION);
                } catch(Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        spinner_types.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Class<?> clazz = AdapterView.class;
                    Field field = clazz.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);
                    field.setInt(spinner_types,AdapterView.INVALID_POSITION);
                } catch(Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        spinner_acts.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Class<?> clazz = AdapterView.class;
                    Field field = clazz.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);
                    field.setInt(spinner_acts,AdapterView.INVALID_POSITION);
                } catch(Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });
        spinner_sorts.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Class<?> clazz = AdapterView.class;
                    Field field = clazz.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);
                    field.setInt(spinner_sorts,AdapterView.INVALID_POSITION);
                } catch(Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });

    }


    public void checkClick(){
        view.findViewById(R.id.text_show).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                lay_datashow.setVisibility(View.VISIBLE);
                lay_actshow.setVisibility(View.INVISIBLE);
                tv_data.setTextColor(Color.RED);
                tv_allact.setTextColor(R.color.colorgray);
            }
        });
        view.findViewById(R.id.text_allact).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                lay_datashow.setVisibility(View.INVISIBLE);
                lay_actshow.setVisibility(View.VISIBLE);
                tv_data.setTextColor(R.color.colorgray);
                tv_allact.setTextColor(Color.RED);
            }
        });
        view.findViewById(R.id.button_starttime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String starttime =  year + "-" + (month + 1) + "-" + dayOfMonth ;
//                        txt_startTime=starttime;
                        bt_starttime.setText(starttime);
                    }
                }
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        view.findViewById(R.id.button_endtime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar1 = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String endtime =  year + "-" + (month + 1) + "-" + dayOfMonth ;
//                        txt_endTime=endtime;
                        bt_endtime.setText(endtime);
                    }
                }
                        , calendar1.get(Calendar.YEAR)
                        , calendar1.get(Calendar.MONTH)
                        , calendar1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        view.findViewById(R.id.button_acttype).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu actmenu=new PopupMenu(getActivity(),view);
                actmenu.inflate(R.menu.actmenu);
                actmenu.show();
                actmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String acttype=(String)menuItem.getTitle();
                        bt_acttype.setText(acttype);
                        if(acttype.equals("全部")){
                            txt_actType="";
                        }
                        else {
                            txt_actType=acttype;

                        }

                        return false;
                    }
                });
            }
        });


        view.findViewById(R.id.text_prompt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(), ClockActivity.class);
                intent3.putExtra("username",userName);
//                intent3.putExtra("isfirst","false");
                startActivityForResult (intent3, 1);
            }
        });


        view.findViewById(R.id.button_time_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_startTime=(String) bt_starttime.getText();
                txt_endTime=(String) bt_endtime.getText();

                lay_time.setVisibility(View.INVISIBLE);
                ll_container.setVisibility(View.GONE);

                ob_dataShow=dd.Datadplay(userName,txt_startTime,txt_endTime,txt_actType,txt_showType);
//                Toast.makeText(Data.this,userName+txt_startTime+txt_endTime+txt_actType+txt_showType,Toast.LENGTH_LONG).show();

                if (ob_dataShow!=null){

                    actlist.setVisibility(View.VISIBLE);
                    tv_noInfo.setVisibility(View.GONE);
                    actAndTime=(String[][])ob_dataShow[0];
                    if (txt_showType.equals("列表")) {
                        Log.i("gy","获取到数据"+txt_timeType+""+txt_showType);

                        initDataShow(actAndTime);
                        actlist.setAdapter(dataShowAdapter);
                    }
                    else {
                        refreshEChart();
                        int len = actAndTime.length;
                        echart_act = new Object[len];
                        echart_time= new Object[len];
                        for(int i =0;i<len;i++){
                            echart_act[i]=actAndTime[i][0];
                            echart_time[i]=((float[]) ob_dataShow[1])[i];
                        }
                        if (txt_showType.equals("柱状图")){
                            Log.i("gy","获取到数据"+txt_timeType+""+txt_showType);
                            myWebView.refreshEchartsWithOption(EchartOptionUtil.getBarChartOptions(echart_act,echart_time));
                        }
                        else if (txt_showType.equals("折线图")){
                            Log.i("gy","获取到数据"+txt_timeType+""+txt_showType);
                            myWebView.refreshEchartsWithOption(EchartOptionUtil.getLineChartOptions(echart_act, echart_time));
                        }
                    }


                }
                else{
                    tv_noInfo.setVisibility(View.VISIBLE);
                    actlist.setVisibility(View.INVISIBLE);
                    myWebView.setVisibility(View.INVISIBLE);
                }
            }
        });
        ll_container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                lay_time.setVisibility(View.INVISIBLE);
                ll_container.setVisibility(View.GONE);
                return false;
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(getActivity(),isCount+"",Toast.LENGTH_LONG).show();

        switch (requestCode){

            case 1:
                String result = data.getStringExtra("result");
                if (result.equals("计时继续")){
                    tv_prompt.setVisibility(View.VISIBLE);
                    layoutParams.setMargins(0, 200, 0, 0);
                }
                else {
                    tv_prompt.setVisibility(View.GONE);
                    layoutParams.setMargins(0, 100, 0, 0);
                }
                break;
        }
    }

    public void WriteSysFile() {
        String startTime = getTime.getNowTime();
        Long lstartTime = getTime.getStartTime();
//        String write ="写入数据";

        //public void save(String EditText){//inputText为传入的要保存的数据
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = getActivity().openFileOutput("data", Context.MODE_APPEND);//"data"为文件名，第二个参数为文件操作模式：文件已经存在，就往文件里面追加类容，不从新创建文件。
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(startTime+","+lstartTime+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // }

    }


}
