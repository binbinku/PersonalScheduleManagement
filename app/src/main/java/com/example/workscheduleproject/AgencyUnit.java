package com.example.workscheduleproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 代办单元数据模板
 */
public class AgencyUnit implements Serializable
{
    private int id;
    private String title;
    private String content;
    private int year;
    private int month;
    private int day;
    private boolean isFinished;
    private List<String> tags;

    public AgencyUnit()
    {   id=0;
        isFinished=false;
        title = " ";
        content = "";
        year = 2001;
        month = 10;
        day = 9;
        tags = new ArrayList<>();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public int getDay()
    {
        return day;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public  void addTag(String tag)
    {
        if(!tag.equals(""))
        tags.add(tag);
    }


    public boolean isFinished()
    {
        return isFinished;
    }

    public void setFinished(boolean finished)
    {
        isFinished = finished;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String DateToString(int schema)
    {
        String str;
        switch (schema)
        {
            case 0:str = year+"年";break;
            case 1:str = month+"月";break;
            case 2:str = day+"日";break;
            case 3:str = month+"月"+ day+"日";break;
            case 4:str = year+"年"+month+"月"+day+"日";break;
            default:str = "NULL";break;
        }
        return str;
    }

}
