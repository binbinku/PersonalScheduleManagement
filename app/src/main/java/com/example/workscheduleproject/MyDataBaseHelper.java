package com.example.workscheduleproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton DataBase
 */
public class MyDataBaseHelper extends SQLiteOpenHelper
{
    private static MyDataBaseHelper instance;

    private static final String MYDATABASENAME = "mydb";

    public static MyDataBaseHelper getInstance()
    {
        return instance;
    }

    private static final int INITID = 0;

    public MyDataBaseHelper(@Nullable Context context)
    {
        super(context, MYDATABASENAME, null, 6);
        if (instance==null)
        {
            instance =this;
        }
    }
    /**
     * 第一次创建数据库时调用
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table AgencyList (id integer primary key autoincrement, isfinished integer,title text,content text,year integer ,month integer,day integer,tags text );");
    }
    /**
     * 数据库更新时调用
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
    }
    /**
     * 插入一条数据
     * @param agencyUnit 代办单元数据
     */
    public void insertData(AgencyUnit agencyUnit) throws Exception
    {
        if (agencyUnit != null)
        {
            ContentValues values = new ContentValues();
            values.put("id", agencyUnit.getId());
            values.put("isfinished", agencyUnit.isFinished());
            values.put("title", agencyUnit.getTitle());
            values.put("content", agencyUnit.getContent());
            values.put("year", agencyUnit.getYear());
            values.put("month", agencyUnit.getMonth());
            values.put("day", agencyUnit.getDay());
            values.put("tags", "");
            long column = this.getWritableDatabase().insert("AgencyList", null, values);
            Log.d("DataBase", "插入到了第" + column + "行");
        } else
        {
            Log.d("DataBase", "插入失败。。。");
        }
    }
    /**
     * 查询所有代办数据
     * @return 代办数据列表
     */
    public List<AgencyUnit> queryAllAgency()throws Exception
    {
        List<AgencyUnit> agencyUnitList = new ArrayList<>();

        Cursor cursor = this.getWritableDatabase().rawQuery("select * from AgencyList ", null);
        while (cursor.moveToNext())
        {
            AgencyUnit agencyUnit = new AgencyUnit();
            agencyUnit.setId(cursor.getInt(0));
            if (cursor.getInt(1) != 0)
                agencyUnit.setFinished(true);

            agencyUnit.setTitle(cursor.getString(2));
            agencyUnit.setContent(cursor.getString(3));
            agencyUnit.setYear(cursor.getInt(4));
            agencyUnit.setMonth(cursor.getInt(5));
            agencyUnit.setDay(cursor.getInt(6));

            agencyUnitList.add(agencyUnit);
        }
        return agencyUnitList;
    }
    /**
     * 查询一个代办数据
     * @return 一个代办数据
     */
    public AgencyUnit queryAAgency(int id) throws Exception
    {

        Cursor cursor = this.getWritableDatabase().rawQuery("select * from AgencyList where id="+id, null);
            cursor.move(1);
            AgencyUnit agencyUnit = new AgencyUnit();
            agencyUnit.setId(cursor.getInt(0));
            if (cursor.getInt(1) != 0)
                agencyUnit.setFinished(true);

            agencyUnit.setTitle(cursor.getString(2));
            agencyUnit.setContent(cursor.getString(3));
            agencyUnit.setYear(cursor.getInt(4));
            agencyUnit.setMonth(cursor.getInt(5));
            agencyUnit.setDay(cursor.getInt(6));

        return agencyUnit;
    }
    /**
     * 查询最大ID
     *
     * @return 最大ID 查不到返回 0
     */
    public int queryMaxID() throws Exception
    {
        try
        {
            Cursor cursor = this.getWritableDatabase().rawQuery("select id from AgencyList order by id desc ", null);
            cursor.move(1);
            return cursor.getInt(0);
        } catch (Exception ex)
        {
            return INITID;
        }

    }
    /**
     * *删除一条数据
     */
    public void deleteAAgency(int id) throws Exception
    {
        this.getWritableDatabase().execSQL("delete from AgencyList where id=" + id);
    }

    public void RefreshAAgency(AgencyUnit agencyUnit)
    {
        Object[] values = new Object[]{agencyUnit.isFinished(),agencyUnit.getTitle(),agencyUnit.getContent(),agencyUnit.getYear(),agencyUnit.getMonth(),agencyUnit.getDay(),agencyUnit.getId()};
        this.getWritableDatabase().execSQL("Update AgencyList set isfinished= ? , title= ? ,content=?,year=?,month=?,day=?  where id= ? ",values );
    }

}
