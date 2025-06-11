package com.iyuba.conceptEnglish.han.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.iyuba.conceptEnglish.han.bean.EvaluationSentenceDataItem

/**
苏州爱语吧科技有限公司
 */
class CorrectEvalHelper(context: Context) : SQLiteOpenHelper(context, "correct_eval_helper.db", null, 1) {
    private val content = "content"
    private val idIndex = "idIndex"
    private val score = "score"
    private val idDelete = "idDelete"
    private val idInsert = "idInsert"
    private val pron = "pron"
    private val pron2 = "pron2"
    private val substituteOrgi = "substitute_orgi"
    private val substituteUser = "substitute_user"
    private val userPron = "user_pron"
    private val userPron2 = "user_pron2"
    private val userId = "userId"
    private val voaId = "voaId"
    private val groupId = "groupId"
    private val tableName = "EvaluationSentenceDataItem"
    private val createEval = "create table $tableName (" +
            "id integer primary key autoincrement," +
            "$content text," +
            "$idIndex integer," +
            "$score text," +
            "$idDelete text," +
            "$idInsert text," +
            "$pron text," +
            "$pron2 text," +
            "$substituteOrgi text," +
            "$substituteUser text," +
            "$userPron text," +
            "$userPron2 text," +
            "$userId integer," +
            "$voaId integer," +
            "$groupId integer)"

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(createEval)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    @SuppressLint("Recycle")
    fun findByContent(currentUserId: String, currentVoaId: String, currentGroupId: String, currentContent: String, flag: Boolean = false): List<EvaluationSentenceDataItem> {
        val list = mutableListOf<EvaluationSentenceDataItem>()
        val selectWhere = "$userId=? and $voaId=? and $groupId=? ${if (flag) "and $content=?" else ""}"
        val selectArray = if (flag) {
            arrayOf(currentUserId, currentVoaId, currentGroupId, currentContent)
        } else {
            arrayOf(currentUserId, currentVoaId, currentGroupId)
        }
        val cursor = writableDatabase.query(tableName, null, selectWhere, selectArray, null, null, null)
        if (cursor.moveToFirst()) {
            val userPronSelect = cursor.getString(cursor.getColumnIndex(userPron))
            val pronSelect = cursor.getString(cursor.getColumnIndex(pron))
            val userIdSelect = cursor.getInt(cursor.getColumnIndex(userId))
            val voaIdSelect = cursor.getInt(cursor.getColumnIndex(voaId))
            val groupIdSelect = cursor.getInt(cursor.getColumnIndex(groupId))
            val item = EvaluationSentenceDataItem()
            item.apply {
                user_pron = userPronSelect
                pron = pronSelect
                userId = userIdSelect
                voaId = voaIdSelect
                groupId = groupIdSelect
            }
            list.add(item)
        }
        return list
    }

    fun findByVoaAndLineN(curUserId: String, curVoaId: String, curLineN: String): List<EvaluationSentenceDataItem> {
        val list = mutableListOf<EvaluationSentenceDataItem>()
        val selectArray = arrayOf(curUserId, curVoaId, curLineN)
        val selectWhere = "$userId=? and $voaId=? and $groupId=?"
        val cursor = writableDatabase.query(tableName, null, selectWhere, selectArray, null, null, null)
        if (cursor.moveToFirst()) {
            val userPronSelect = cursor.getString(cursor.getColumnIndex(userPron))
            val pronSelect = cursor.getString(cursor.getColumnIndex(pron))
            val userIdSelect = cursor.getInt(cursor.getColumnIndex(userId))
            val voaIdSelect = cursor.getInt(cursor.getColumnIndex(voaId))
            val groupIdSelect = cursor.getInt(cursor.getColumnIndex(groupId))
            val item = EvaluationSentenceDataItem()
            item.apply {
                user_pron = userPronSelect
                pron = pronSelect
                userId = userIdSelect
                voaId = voaIdSelect
                groupId = groupIdSelect
            }
            list.add(item)
        }
        return list

        /*val cursor = writableDatabase.rawQuery("select * from $tableName where $userId=? and $voaId=? and $groupId=?",selectArray)
        while (cursor.moveToNext()) {
            val userPronSelect = cursor.getString(cursor.getColumnIndex(userPron))
            val pronSelect = cursor.getString(cursor.getColumnIndex(pron))
            val userIdSelect = cursor.getInt(cursor.getColumnIndex(userId))
            val voaIdSelect = cursor.getInt(cursor.getColumnIndex(voaId))
            val groupIdSelect = cursor.getInt(cursor.getColumnIndex(groupId))
            val wordSelect = cursor.getString(cursor.getColumnIndex(content))
            val scoreSelect = cursor.getFloat(cursor.getColumnIndex(score))

            val item = EvaluationSentenceDataItem()
            item.apply {
                user_pron = userPronSelect
                pron = pronSelect
                userId = userIdSelect
                voaId = voaIdSelect
                groupId = groupIdSelect
                content = wordSelect
                score = scoreSelect
            }
            list.add(item)
        }
        return list*/
    }

        fun insertItem(item: EvaluationSentenceDataItem) {
            val values = ContentValues()
            values.apply {
                put(userId, item.userId)
                put(userPron, item.user_pron)
                put(pron, item.pron)
                put(voaId, item.voaId)
                put(groupId, item.groupId)
                put(content, item.content)
            }
            writableDatabase.insert(tableName, null, values)
        }

        fun updateItem(item: EvaluationSentenceDataItem) {
            val values = ContentValues()
            values.apply {
                put(userPron, item.user_pron)
                put(pron, item.pron)
            }
            val selectWhere = "$userId=? and $voaId=? and $groupId=? and $content=?"
            val selectArray = arrayOf(item.userId.toString(), item.voaId.toString(), item.groupId.toString(), item.content)
            writableDatabase.update(tableName, values, selectWhere, selectArray)
        }

    }