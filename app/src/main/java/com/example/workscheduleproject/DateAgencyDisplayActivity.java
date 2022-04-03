package com.example.workscheduleproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 代办查询页面逻辑
 */
public class DateAgencyDisplayActivity extends AppCompatActivity
{
    private List<AgencyUnit> agencyUnitList;        //当前页面代办数据缓存列表
    private List<LinearLayout> agentUnitLinearLayouts;      //当前页面所有动态生成的代办单元列表
    private Calendar currentTime;       //事件处理类
    private LinearLayout mainContent;       //主容器
    private DatePicker datePicker;      //日期选择器
    private AgencyUnit currentAgencyUnitData;       //在日期选择器中当前选择的代办单元数据
    private AgencyUnit bufferAgencyUnitData;        //缓存代办单元数据

    //日期缓存
    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_agency_display);

        TransparentStatusBar();     //美化状态栏
        InitDate();         //初始化数据
        InitComponents();      //初始化组件
        EventHandler();     //事件处理

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        RefreshUnit();  //动态刷新代办单元

    }
    /**
     * 刷新 视图单元
     */
    private void RefreshUnit()
    {
        if (currentAgencyUnitData != null)//当前选择的代办数据为空说明是第一次跳转到当前页面，会初始化数据，不需要刷新
        {
            LinearLayout bufferLayout=null;
            try
            {
                //调用数据库，根据ID查询最新代办数据，存到缓存里
                bufferAgencyUnitData= MyDataBaseHelper.getInstance().queryAAgency(currentAgencyUnitData.getId());

            } catch (Exception ex)
            {
                Log.e("DateAgencyActivity", ex.getMessage());
            }

            //------------更新代办数据缓存列表里的数据[Start]---------------
            for (AgencyUnit au : agencyUnitList)
            {
                if (au.getId()==bufferAgencyUnitData.getId())
                {
                    agencyUnitList.set(agencyUnitList.indexOf(au),bufferAgencyUnitData);
                    break;
                }
            }
            //------------更新代办数据缓存列表里的数据[End]---------------

            // -------------查找当前缓存代办数据所承载的代办单元[Start]----------------------
            for(LinearLayout linearLayout :agentUnitLinearLayouts)
            {
                if(linearLayout.getId()==bufferAgencyUnitData.getId())
                {
                    bufferLayout = linearLayout;
                }
            }
            // -------------查找当前缓存代办数据所承载的代办单元[Start]----------------------



            //------------------根据最新数据生成代办单元[Start]-----------------------
            if (bufferAgencyUnitData.getYear() == year && bufferAgencyUnitData.getMonth() == month && bufferAgencyUnitData.getDay() == day)
            {

                mainContent.removeView(bufferLayout);   //先移除旧的
                agentUnitLinearLayouts.add(GenerateAgentUnit(bufferAgencyUnitData));//再生成新的
                Log.d("DateAgencyActivity", "刷新了数据");
            }
            else
            {
                mainContent.removeView(bufferLayout);

            }
            //------------------根据最新数据生成代办单元[End]-----------------------


        }
    }

    @SuppressLint("NewApi")
    private void EventHandler()
    {
        //--------------------日期选择器点击事件处理[Start]--------------------------
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener()
        {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2)
            {
                mainContent.removeAllViews();//清空代办生成容器
                agentUnitLinearLayouts.clear();//清空缓存代办单元布局

                //生成选择日期当天所有代办
                for (AgencyUnit au : agencyUnitList)
                {
                    if (au.getYear() == i && (au.getMonth() - 1) == i1 && au.getDay() == i2)
                    {
                        agentUnitLinearLayouts.add(GenerateAgentUnit(au));
                    }
                }
                //更新缓存日期
                year = i;
                month = i1 + 1;
                day = i2;
                Log.d("DateAgencyActivity", i + "年" + (i1 + 1) + "月" + i2 + "日");

            }
        });
        //--------------------日期选择器点击事件处理[End]--------------------------
    }

    /**
     * 初始化组件
     */
    private void InitComponents()
    {
        mainContent = findViewById(R.id.agency_content_display);
        datePicker = findViewById(R.id.dp_dickdate);

        //初始化生成代办单元
        for (AgencyUnit au : agencyUnitList)
        {
            if (au.getYear() == currentTime.get(Calendar.YEAR) && (au.getMonth() - 1) == currentTime.get(Calendar.MONTH) && au.getDay() == currentTime.get(Calendar.DAY_OF_MONTH))
            {
                agentUnitLinearLayouts.add(GenerateAgentUnit(au));
            }
        }
    }

    /**
     * 初始化数据
     */
    private void InitDate()
    {
        currentTime = Calendar.getInstance();

        try
        {
            //调用数据库，查询所有代办数据
            agencyUnitList = MyDataBaseHelper.getInstance().queryAllAgency();

        }
        catch (Exception ex)
        {
            Log.e("DateAgencyActivity", ex.getMessage());
        }
        //初始化缓存代办单元布局列表
        agentUnitLinearLayouts = new ArrayList<>();
    }

    //美化状态栏
    protected void TransparentStatusBar()
    {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#ec4141"));
        window.setNavigationBarColor(Color.parseColor("#00ffffff"));
        //状态栏中的文字颜色和图标颜色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    /**
     * 动态生成代办单元布局
     * @param aloneAgencyUnit   代办数据
     * @return  生成的代办单元
     */
    private LinearLayout GenerateAgentUnit(AgencyUnit aloneAgencyUnit)
    {
        //-----------------------单元布局[Start]-----------------------

        LinearLayout unitLayout = new LinearLayout(DateAgencyDisplayActivity.this);//创建布局
        LinearLayout.LayoutParams unitLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MainActivity.UNITHEIGHT);//创建布局参数
        unitLayout.setId(aloneAgencyUnit.getId());//设置ID
        unitLayoutParams.setMargins(20, 20, 20, 20);//设置布局参数：外边距
        unitLayout.setOrientation(LinearLayout.HORIZONTAL);//设置线性布局排列方向
        unitLayout.setLayoutParams(unitLayoutParams);//给布局设置布局参数
        unitLayout.setElevation(20);//设置20大小的外阴影
        unitLayout.setGravity(Gravity.CENTER_VERTICAL);//设置对齐：垂直居中
        unitLayout.setBackground(getResources().getDrawable(R.drawable.unitlayout_background));//设置背景:自定义图形


        //------------------------点击单元布局事件[Start]---------------------------
        unitLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AgencyUnit syncAgencyUnit = null;

                //在代办缓存中查询当前代办数据
                for (AgencyUnit agencyUnit : agencyUnitList)
                {
                    if (agencyUnit.getId() == unitLayout.getId())
                    {
                        syncAgencyUnit = agencyUnit;
                    }
                }

                if (syncAgencyUnit != null)
                {
                    //跳转到代办编辑页面
                    currentAgencyUnitData = syncAgencyUnit;
                    Intent toAgencyEditActivity = new Intent(DateAgencyDisplayActivity.this, AgencyEditActivity.class);
                    toAgencyEditActivity.putExtra("AgencyUnit", syncAgencyUnit);//传送当前代办数据
                    startActivityForResult(toAgencyEditActivity, 3);

                } else
                {
                    Log.d("DateAgencyActivity", "点击单元跳转失败！数据未获取到！");
                }
            }
        });
        //------------------------点击单元布局事件[End]---------------------------


        //-----------------------单元布局[End]-----------------------



        //-----------------------生成内部控件[Start]-----------------------

        //[1]CheckBox

        CheckBox unitCheckBox = new CheckBox(DateAgencyDisplayActivity.this);
        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        checkBoxParams.setMargins(20, 0, 0, 0);
        unitCheckBox.setGravity(Gravity.CENTER);
        unitCheckBox.setChecked(aloneAgencyUnit.isFinished());
        unitCheckBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        unitCheckBox.setLayoutParams(checkBoxParams);
        unitLayout.addView(unitCheckBox);



        //[2]TextView-content
        TextView unitContentTextView = new TextView(this);
        LinearLayout.LayoutParams unitContentTextViewParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 5.0f);
        unitContentTextView.setLayoutParams(unitContentTextViewParams);
        unitContentTextView.setText(aloneAgencyUnit.getTitle());
        unitContentTextView.setTextSize(20);
        unitContentTextView.setMaxLines(1);
        unitContentTextView.setTextColor(Color.parseColor("#ffffff"));
        unitContentTextView.setGravity(Gravity.CENTER_VERTICAL);
        unitLayout.addView(unitContentTextView);

        //[3]TextView-Date
        TextView unitDateTextView = new TextView(this);
        LinearLayout.LayoutParams unitDateTextViewParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2.0f);
        unitDateTextView.setLayoutParams(unitDateTextViewParams);
        unitDateTextView.setText(aloneAgencyUnit.DateToString(3));
        unitDateTextView.setTextSize(20);
        unitDateTextView.setTextColor(Color.parseColor("#ffffff"));
        unitDateTextView.setGravity(Gravity.CENTER_VERTICAL);
        unitLayout.addView(unitDateTextView);

        //---------------初始化完成代办Text效果变化[Start]-----------------
        if (aloneAgencyUnit.isFinished())
        {
            unitContentTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            unitContentTextView.setTextColor(Color.parseColor("#aaaaaa"));
        } else
        {
            unitContentTextView.getPaint().setFlags(0);
            unitContentTextView.setTextColor(Color.parseColor("#ffffff"));
        }
        //---------------初始化完成代办Text效果变化[Start]-----------------


        //-----------------------生成内部控件[End]-----------------------




        //-----------------------单元内部事件处理[Start]------------------

        unitCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            //---------------点击完成代办布局效果变化[Start]-----------------
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                for (AgencyUnit agencyUnit : agencyUnitList)
                {
                    if (agencyUnit.getId() == aloneAgencyUnit.getId())
                    {
                        agencyUnit.setFinished(b);//设置checkbox是否打勾
                        if (b == true)
                        {
                            unitContentTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);  //设置textView划线
                            unitContentTextView.setTextColor(Color.parseColor("#aaaaaa"));//设置字体颜色
                        } else
                        {
                            unitContentTextView.getPaint().setFlags(0);//设置textView无划线
                            unitContentTextView.setTextColor(Color.parseColor("#ffffff"));
                        }

                        MyDataBaseHelper.getInstance().RefreshAAgency(agencyUnit);  //调用数据库，刷新数据
                    }
                }
            }
            //---------------点击完成代办布局效果变化[End]-----------------

        });



        //-----------------------单元内部事件处理[End]------------------




        //-----------------------添加到MidContent[Start]-----------------------
        if (aloneAgencyUnit != null)
        {
            mainContent.addView(unitLayout);
        }
        //-----------------------添加到MidContent[End]-----------------------

        return unitLayout;//返回当前布局
    }

    /**
     * 跳转回传【待开发】
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity", "回传数据！");
        //删除回传
        if (requestCode == 3 && resultCode == 2)
        {
            Log.d("MainActivity", "删除回传" + data.getIntExtra("ID", -1));
        }
        //保存回传
        if (requestCode == 3 && resultCode == 3)
        {

            Log.d("MainActivity", "保存回传" + data.getStringExtra("title"));
        }
    }
}