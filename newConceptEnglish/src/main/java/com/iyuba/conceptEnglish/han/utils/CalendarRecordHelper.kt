package com.iyuba.conceptEnglish.han.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.iyuba.conceptEnglish.han.bean.LocalCalendarRecord
import com.iyuba.core.lil.user.UserInfoManager
import java.text.SimpleDateFormat
import java.util.*

/**
苏州爱语吧科技有限公司
@Date:  2022/8/25
@Author:  han rong cheng
 */
class CalendarRecordHelper(context: Context): SQLiteOpenHelper(context,"calendar_record_helper.db",null,1) {
    private val uid="uid"
    private val createTime="createTime"
    private val scan="scan"
    private val tableName="LocalCalendarRecord"
    private val createEval = "create table $tableName (" +
            "id integer primary key autoincrement," +
            "$uid integer," +
            "$scan integer," +
            "$createTime text)"
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(createEval)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun insertSingle(){
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val userId = UserInfoManager.getInstance().userId.toString()
        val nowTime = format.format(System.currentTimeMillis())
        val selection="${uid}=? and ${createTime}=?"
        val query = writableDatabase.query(tableName, null, selection, arrayOf(userId, nowTime), null, null, null)
        if (query.moveToFirst()){
            val values = with(ContentValues()){
                put(uid,userId.toString())
                put(createTime,nowTime)
                put(scan,1)
                this
            }
            writableDatabase.insert(tableName,null,values)
        }
        query.close()
    }

    fun findByCreateTime(createTimeOut:String):List<LocalCalendarRecord>{
        val userId = UserInfoManager.getInstance().userId.toString()
        val list= mutableListOf<LocalCalendarRecord>()
        val sql="select * from $tableName where ${uid}='${userId}' and $createTime like '${createTimeOut}'"
        val query = writableDatabase.rawQuery(sql,null)
        while (query.moveToNext()){
            val item= with(LocalCalendarRecord()){
                this.uid=query.getInt(query.getColumnIndex(this@CalendarRecordHelper.uid))
                this.createTime=query.getString(query.getColumnIndex(this@CalendarRecordHelper.createTime))
                this.scan=query.getInt(query.getColumnIndex(this@CalendarRecordHelper.scan))
                this
            }
            list.add(item)
        }
        query.close()
        return list
    }

    fun insertOrReplace(createTimeOut:String,outScan:Int){
        val userId = UserInfoManager.getInstance().userId.toString()
        val list = findByCreateTime(createTimeOut)
        val values= with(ContentValues()){
            put(uid,userId)
            put(createTime,createTimeOut)
            put(scan,outScan)
            this
        }
        if (list.isEmpty()){
            //插入
            writableDatabase.insert(tableName,null,values)
        }else{
            //更新
            val clause="${uid}=? and ${createTime}=?"
            writableDatabase.update(tableName,values,clause, arrayOf(userId,createTimeOut))
        }
    }

}