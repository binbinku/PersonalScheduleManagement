package com.example.workscheduleproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * 代办编辑页面逻辑
 */
public class AgencyEditActivity extends AppCompatActivity
{
    private CheckBox agencyEditCheckBox;    //是否完成代办
    private TextView agencyEditDateTextView;        //日期文字
    private EditText agencyEditTitleEditText;       //标题文字
    private EditText agencyEditContentEditText;     //内容编辑
    private Button agencyEditDeleteButton;      //删除按钮
    private Button agencyEditSaveButton;        //保存按钮
    private Button agencyEditDateButton;        //日期按钮

    private AgencyUnit agencyUnit;  //代办数据


    private Calendar currentTime;   //日期处理类

    //缓存
    private int id;
    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_edit);
        transparencyBar(AgencyEditActivity.this);

        InitComponents();//初始化组件
        SyncData();//同步数据
        EventHandler();//事件处理
    }

    /**
     * 事件处理
     */
    private void EventHandler()
    {
        //-----------------------删除按钮事件处理[Start]-------------------------
        agencyEditDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //跳转回传，从哪来跳哪儿去
                Intent backMainActivity = new Intent();

                backMainActivity.putExtra("DELETE", true);//跳转回传删除标志
                backMainActivity.putExtra("ID", id);//跳转回传ID

                setResult(2, backMainActivity);//设置回传数据
                finish();//关闭当前Activity
            }
        });
        //-----------------------删除按钮事件处理[End]-------------------------

        //-----------------------保存按钮事件处理[Start]-------------------------
        agencyEditSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //更新代办单元和数据为最新编辑数据
                agencyUnit.setTitle(agencyEditTitleEditText.getText().toString());
                agencyUnit.setFinished(agencyEditCheckBox.isChecked());
                agencyUnit.setContent(agencyEditContentEditText.getText().toString());
                agencyUnit.setYear(year);
                agencyUnit.setMonth(month);
                agencyUnit.setDay(day);

                Intent backMainActivity = new Intent();
                backMainActivity.putExtra("AgencyUnit", agencyUnit);//设置回传数据

                MyDataBaseHelper.getInstance().RefreshAAgency(agencyUnit);      //调用数据库，更新数据

                setResult(3, backMainActivity);//设置回传
                finish();//关闭当前页面
            }
        });
        //-----------------------保存按钮事件处理[End]-------------------------

        //-----------------------日期按钮事件处理[Start]-------------------------
        agencyEditDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //生成一个日期选择器弹框
                new DatePickerDialog(AgencyEditActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {
                        //更新缓存日期
                        year = i;
                        month = i1 + 1;
                        day = i2;
                        String date = year+ "年" + month + "月" + day + "日";
                        //设置日期显示
                        agencyEditDateTextView.setText(date);
                    }

                }, currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        //-----------------------日期按钮事件处理[End]-------------------------

    }

    /**
     * 同步数据
     */
    private void SyncData()
    {
        //-------------接受传递来的代办数据更新当前页面显示数据[Start]----------------------
        agencyUnit = (AgencyUnit) getIntent().getSerializableExtra("AgencyUnit");//接受序列化数据

        this.id = agencyUnit.getId();//缓存ID

        agencyEditCheckBox.setChecked(agencyUnit.isFinished());//更新是否完成代办

        String date = agencyUnit.DateToString(4);

        agencyEditDateTextView.setText(date);//更新日期

        //设置缓存
        this.year = agencyUnit.getYear();

        this.month = agencyUnit.getMonth();

        this.day = agencyUnit.getDay();

        agencyEditTitleEditText.setText(agencyUnit.getTitle());//设置标题

        agencyEditContentEditText.setText(agencyUnit.getContent());//设置内容

        //-------------接受传递来的代办数据更新当前页面显示数据[End]----------------------

    }

    /**
     * 初始化组件
     */
    private void InitComponents()
    {
        agencyEditCheckBox = findViewById(R.id.agency_edit_checkBox);
        agencyEditDateTextView = findViewById(R.id.agency_edit_Date);
        agencyEditTitleEditText = findViewById(R.id.agency_edit_Title);
        agencyEditContentEditText = findViewById(R.id.agency_edit_Content);
        agencyEditDeleteButton = findViewById(R.id.agency_edit_deleteButton);
        agencyEditSaveButton = findViewById(R.id.agency_edit_saveButton);
        agencyEditDateButton = findViewById(R.id.agency_edit_dateButton);
        currentTime = Calendar.getInstance();
    }

    /**
     * 美化状态栏
     * @param activity
     */
    public static void transparencyBar(Activity activity)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = activity.getWindow();

            ((Window) window).clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.TRANSPARENT);

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


}