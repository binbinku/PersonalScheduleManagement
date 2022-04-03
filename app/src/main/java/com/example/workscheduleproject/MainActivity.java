package com.example.workscheduleproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 系统主逻辑
 */
public class MainActivity extends AppCompatActivity
{
    public MyDataBaseHelper dataBaseHelperInstance;    //数据库单例
    private List<AgencyUnit> agencyUnitList;    //当前页面缓存代办数据列表
    private List<LinearLayout> agentUnitLinearLayouts;      //当前页面所有代办单元布局
    private Calendar currentTime;       //时间操作类
    private LinearLayout midContentToday;       //今天代办单元容器
    private LinearLayout midContentTomorrow;    //明天代办单元容器
    private LinearLayout midContentNextWeek;    //下周代办单元容器
    private LinearLayout midContentFuture;      //未来代办单元容器
    private LinearLayout midContentEarlier;     //更早代办单元容器
    private DrawerLayout drawerLayout;          //侧边栏布局
    private Button dateAgencyDisplayButton;     //跳转代办查询按钮
    private Button settingButton;       //跳转设置页面按钮
    private Button orderButton;     //显示侧边栏按钮
    private int year;       //当前页面日期缓存
    private int month;
    private int day;
    public static final int UNITHEIGHT = 130;   //代办单元布局高度
    //  private ScrollView midContentScrollView;
    //  private LinearLayout midContentPanel;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TransparentStatusBar();     //设置状态栏
        InitValues();       //初始化变量
        InitComponents();       //初始化组件
        EventHandler();     //事件处理

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        InitData();     //初始化数据
    }

    /**
     * 数据初始化 读取数据库数据 生成单元
     */
    private void InitData()
    {
        //----------清除所有代办单元[Start]--------------
        midContentToday.removeAllViews();
        midContentTomorrow.removeAllViews();
        midContentNextWeek.removeAllViews();
        midContentFuture.removeAllViews();
        midContentEarlier.removeAllViews();
        //----------清除所有代办单元[End]--------------

        agentUnitLinearLayouts = new ArrayList<>();     //初始化布局列表

        try
        {
            agencyUnitList = dataBaseHelperInstance.queryAllAgency();       //调用数据库，查询所有代办

        } catch (Exception ex)
        {
            Log.e("MainActivity", ex.getMessage());

        }

        //根据代办数据生成代办单元布局
        for (AgencyUnit agencyUnit : agencyUnitList)
        {
            String date;
            if (agencyUnit.getYear() == currentTime.get(Calendar.YEAR))
            {
                agentUnitLinearLayouts.add(GenerateAgentUnit(agencyUnit));
            } else
            {
                agentUnitLinearLayouts.add(GenerateAgentUnit(agencyUnit));
            }
        }

        Log.d("DataBase", "查询成功！");
    }

    /**
     * 变量初始化
     */
    private void InitValues()
    {
        //----------日期初始化[Start]--------------
        currentTime = Calendar.getInstance();
        year = currentTime.get(Calendar.YEAR);
        month = currentTime.get(Calendar.MONTH) + 1;
        day = currentTime.get(Calendar.DAY_OF_MONTH);
        //----------日期初始化[End]--------------



        //-----------数据库实例化[Start]-------------
        if (dataBaseHelperInstance == null)
        {
            dataBaseHelperInstance = new MyDataBaseHelper(MainActivity.this);
        }
        dataBaseHelperInstance.getWritableDatabase();
        //-----------数据库实例化[End]-------------



    }

    /**
     * 组件初始化
     */
    private void InitComponents()
    {
        //---------------获取所需界面组件[Start]---------------------
        drawerLayout = findViewById(R.id.drawerLayout);
        orderButton = findViewById(R.id.main_order);
        midContentToday = findViewById(R.id.mid_content_today);
        midContentTomorrow = findViewById(R.id.mid_content_tomorrow);
        midContentNextWeek = findViewById(R.id.mid_content_nextweek);
        midContentFuture = findViewById(R.id.mid_content_future);
        midContentEarlier = findViewById(R.id.mid_content_earlier);
        dateAgencyDisplayButton = findViewById(R.id.date_agency_display);
        settingButton = findViewById(R.id.setting_button);
        //---------------获取所需界面组件[End]---------------------

    }

    /**
     * 动态生成一个代办单元布局
     * @param aloneAgencyUnit 代办数据
     * @return 返回一个线性布局对象
     */
    private LinearLayout GenerateAgentUnit(AgencyUnit aloneAgencyUnit)
    {

        //---------------------动态生成代办单元布局----------------------
        //-----------------------单元布局[Start]-----------------------
        LinearLayout unitLayout = new LinearLayout(this);
        LinearLayout.LayoutParams unitLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UNITHEIGHT);
        unitLayout.setId(aloneAgencyUnit.getId());
        unitLayoutParams.setMargins(20, 20, 20, 20);
        unitLayout.setOrientation(LinearLayout.HORIZONTAL);
        unitLayout.setLayoutParams(unitLayoutParams);
        unitLayout.setElevation(20);
        unitLayout.setGravity(Gravity.CENTER_VERTICAL);
        unitLayout.setBackground(getResources().getDrawable(R.drawable.unitlayout_background));

        //------------------------点击单元布局事件[Start]---------------------------
        unitLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AgencyUnit syncAgencyUnit = null;
                for (AgencyUnit agencyUnit : agencyUnitList)
                {
                    if (agencyUnit.getId() == unitLayout.getId())
                    {
                        syncAgencyUnit = agencyUnit;
                    }
                }

                if (syncAgencyUnit != null)
                {
                    Intent toAgencyEditActivity = new Intent(MainActivity.this, AgencyEditActivity.class);
                    toAgencyEditActivity.putExtra("AgencyUnit", syncAgencyUnit);
                    startActivityForResult(toAgencyEditActivity, 2);
                } else
                {
                    Log.d("MainActivity", "点击单元跳转失败！数据未获取到！");
                }
            }
        });
        //------------------------点击单元布局事件[End]---------------------------


        //-----------------------单元布局[End]-----------------------

        //-----------------------生成内部控件[Start]-----------------------

        //[1]CheckBox

        CheckBox unitCheckBox = new CheckBox(this);
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


        //---------------------点击完成代办按钮效果变化[Start]----------------------
        if (aloneAgencyUnit.isFinished())
        {
            unitContentTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            unitContentTextView.setTextColor(Color.parseColor("#aaaaaa"));
        } else
        {
            unitContentTextView.getPaint().setFlags(0);
            unitContentTextView.setTextColor(Color.parseColor("#ffffff"));
        }
        //---------------获取所需界面组件[End]---------------------



        //-----------------------生成内部控件[End]-----------------------




        //-----------------------单元内部事件处理[Start]------------------
        unitCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                for (AgencyUnit agencyUnit : agencyUnitList)
                {
                    if (agencyUnit.getId() == aloneAgencyUnit.getId())
                    {
                        agencyUnit.setFinished(b);
                        if (b == true)
                        {
                            unitContentTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            unitContentTextView.setTextColor(Color.parseColor("#aaaaaa"));
                        } else
                        {
                            unitContentTextView.getPaint().setFlags(0);
                            unitContentTextView.setTextColor(Color.parseColor("#ffffff"));
                        }
                        dataBaseHelperInstance.RefreshAAgency(agencyUnit);
                    }
                }
            }
        });
        //-----------------------单元内部事件处理[End]------------------


        //-----------------------添加到相应布局容器(今天，明天，最近一周，未来，更早)[Start]-----------------------
        AgencyUnit agencyUnit = aloneAgencyUnit;
        if (agencyUnit != null)
        {
            if (agencyUnit.getYear() == currentTime.get(Calendar.YEAR))
            {
                if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) < 0)
                {
                    midContentEarlier.addView(unitLayout);
                }
                if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) == DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)))
                {
                    midContentToday.addView(unitLayout);
                }
                if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) == 1)
                {
                    midContentTomorrow.addView(unitLayout);
                }
                if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) > 1 &&

                        DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) <= 7)
                {
                    midContentNextWeek.addView(unitLayout);
                }
                if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) > 7)
                {
                    midContentFuture.addView(unitLayout);
                }
            } else
            {
                if (agencyUnit.getYear() >= currentTime.get(Calendar.YEAR))
                {
                    midContentFuture.addView(unitLayout);
                } else
                {
                    midContentEarlier.addView(unitLayout);
                }
            }

            //-----------------------添加到相应布局容器(今天，明天，最近一周，未来，更早)[End]-----------------------


        }

        return unitLayout;      //返回当前生成的代办单元布局

    }

    /**
     * 美化状态栏
     */
    protected void TransparentStatusBar()
    {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#eeeeee"));
        window.setNavigationBarColor(Color.parseColor("#00ffffff"));

        //状态栏中的文字颜色和图标颜色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    /**
     * 事件处理
     */
    private void EventHandler()
    {
        //----------------------------悬浮按钮事件处理[Start]----------------------------
        findViewById(R.id.main_FAB).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Toast.makeText(MainActivity.this, "新建代办", Toast.LENGTH_SHORT).show();

                //-----------------------自定义 Dialog [Start]----------------------------
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_dialog, null);
                dialogView.setBackgroundColor(Color.TRANSPARENT);
                dialogView.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                Dialog dialog = new Dialog(MainActivity.this);
                DisplayMetrics dm = MainActivity.this.getResources().getDisplayMetrics();
                int displayWidth = dm.widthPixels;
                int displayHeight = dm.heightPixels;
                dialog.setContentView(dialogView);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
                p.width = (int) (displayWidth * 0.95f);
                p.height = (int) (displayHeight * 0.28f);
                dialog.getWindow().setAttributes(p);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.show();

                //设置新建按钮点事件处理
                dialogView.findViewById(R.id.customDialog_SetupButton).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        String str = ((EditText) dialogView.findViewById(R.id.customDialog_EdieText)).getText().toString();
                        if (!str.equals(""))
                        {
                            if (year == currentTime.get(Calendar.YEAR))
                            {
                                try
                                {
                                    AgencyUnit agencyUnit = new AgencyUnit();
                                    agencyUnit.setId(dataBaseHelperInstance.queryMaxID() + 1);
                                    agencyUnit.setTitle(str);
                                    agencyUnit.setYear(year);
                                    agencyUnit.setMonth(month);
                                    agencyUnit.setDay(day);
                                    agencyUnitList.add(agencyUnit);
                                    dataBaseHelperInstance.insertData(agencyUnit);
                                    agentUnitLinearLayouts.add(GenerateAgentUnit(agencyUnit));

                                } catch (Exception ex)
                                {
                                    Log.e("MainActivity", ex.getMessage());
                                }

                            } else
                            {
                                try
                                {
                                    AgencyUnit agencyUnit = new AgencyUnit();
                                    agencyUnit.setId(dataBaseHelperInstance.queryMaxID() + 1);
                                    agencyUnit.setTitle(str);
                                    agencyUnit.setYear(year);
                                    agencyUnit.setMonth(month);
                                    agencyUnit.setDay(day);
                                    agencyUnitList.add(agencyUnit);
                                    dataBaseHelperInstance.insertData(agencyUnit);
                                    agentUnitLinearLayouts.add(GenerateAgentUnit(agencyUnit));
                                } catch (Exception ex)
                                {
                                    Log.e("MainActivity", ex.getMessage());
                                }
                            }

                            dialog.cancel();

                        }
                    }
                });

                //日期时间按钮事件处理
                dialogView.findViewById(R.id.customDialog_DateButton).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                            {
                                year = i;
                                month = i1 + 1;
                                day = i2;
                            }

                        }, currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                //-----------------------自定义 Dialog [End]----------------------------


            }
        });
        //----------------------------悬浮按钮事件处理[End]----------------------------




        //----------------------------侧滑菜单事件处理[Start]--------------------------

        /**
         * 打开侧边栏
         */
        orderButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                drawerLayout.openDrawer(findViewById(R.id.main_Left_LinearLayout));
            }
        });

        /**
         * 跳转到代办查询页面
         */
        dateAgencyDisplayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent toDateAgencyDisplayActivity = new Intent(MainActivity.this, DateAgencyDisplayActivity.class);
                startActivity(toDateAgencyDisplayActivity);
            }
        });

        /**
         * 跳转到设置页面
         */
        settingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent toSettingActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(toSettingActivity);
            }
        });
    }

    /**
     * Activity跳转回传
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        回传数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity", "回传数据！");
        //删除回传
        if (requestCode == 2 && resultCode == 2)
        {
            Log.d("MainActivity", "删除回传" + data.getIntExtra("ID", -1));
            DeleteAgencyUnit(data.getIntExtra("ID", -1));
        }
//        //保存回传
//        if (requestCode == 2 && resultCode == 3)
//        {
//
//            Log.d("MainActivity", "保存回传" + data.getStringExtra("title"));
//            RefreshAgencyUnit((AgencyUnit) data.getSerializableExtra("AgencyUnit"));
//        }
    }

    /**
     * 删除一个代办布局单元
     *
     * @param id 删除的单元id
     */
    private void DeleteAgencyUnit(int id)
    {
        AgencyUnit agencyUnit = null;
        if (id > 0)
        {
            for (AgencyUnit m_agencyUnit : agencyUnitList)
            {
                if (m_agencyUnit.getId() == id)
                {
                    agencyUnit = m_agencyUnit;
                    agencyUnitList.remove(m_agencyUnit);
                    break;
                }
            }

            for (LinearLayout linearLayout : agentUnitLinearLayouts)
            {
                if (linearLayout.getId() == id)
                {
                    agentUnitLinearLayouts.remove(linearLayout);
                    if (agencyUnit != null)
                    {

                        //-----------------------选择对应容器移除组件[Start]-----------------------
                        if (agencyUnit.getYear() == currentTime.get(Calendar.YEAR))
                        {
                            if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) < 0)
                            {
                                midContentEarlier.removeView(linearLayout);
                            }
                            if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) == DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)))
                            {
                                midContentToday.removeView(linearLayout);

                            }
                            if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) == 1)
                            {
                                midContentTomorrow.removeView(linearLayout);

                            }
                            if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) > 1 &&

                                    DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) <= 7)
                            {
                                midContentNextWeek.removeView(linearLayout);

                            }
                            if (DateUtil.outDay(agencyUnit.getYear(), agencyUnit.getMonth(), agencyUnit.getDay()) - DateUtil.outDay(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH) + 1, currentTime.get(Calendar.DAY_OF_MONTH)) > 7)
                            {
                                midContentFuture.removeView(linearLayout);

                            }
                        } else
                        {
                            if (agencyUnit.getYear() >= currentTime.get(Calendar.YEAR))
                            {
                                midContentFuture.removeView(linearLayout);
                            } else
                            {
                                midContentEarlier.removeView(linearLayout);
                            }
                        }
                        //-----------------------选择对应容器移除组件[End]-----------------------
                    }

                    break;

                }
            }

            try
            {
                dataBaseHelperInstance.deleteAAgency(id);

            } catch (Exception ex)
            {
                Log.e("MainActivity", ex.getMessage());
            }



        } else
        {
            Log.d("MainActivity", "删除AgencyUnit失败");
        }



    }



    /**
     * 刷新布局单元
     * @param agencyUnit 需要更新的数据
     */
    //    private void RefreshAgencyUnit(AgencyUnit agencyUnit)
//    {
//        int id = agencyUnit.getId();
//        if (id > 0)
//        {
//
////            for (AgencyUnit m_agencyUnit : agencyUnitList)
////            {
////                if (m_agencyUnit.getId() == id)
////                {
////                    //修改代办数据
////                    m_agencyUnit.setTitle(agencyUnit.getTitle());
////                    m_agencyUnit.setContent(agencyUnit.getContent());
////                    m_agencyUnit.setFinished(agencyUnit.isFinished());
////                    m_agencyUnit.setYear(agencyUnit.getYear());
////                    m_agencyUnit.setMonth(agencyUnit.getMonth());
////                    m_agencyUnit.setDay(agencyUnit.getDay());
////                    break;
////                }
////            }
////            for (LinearLayout linearLayout : agentUnitLinearLayouts)
////            {
////                if (linearLayout.getId() == id)
////                {
////                    ((CheckBox) linearLayout.getChildAt(0)).setChecked(agencyUnit.isFinished());
////                    ((TextView) linearLayout.getChildAt(1)).setText(agencyUnit.getTitle());
////                    ((TextView) linearLayout.getChildAt(2)).setText(agencyUnit.DateToString(3));
////                    //修改代办布局单元
////                    break;
////                }
////            }
//            dataBaseHelperInstance.RefreshAAgency(agencyUnit);
//        }
//    }

}