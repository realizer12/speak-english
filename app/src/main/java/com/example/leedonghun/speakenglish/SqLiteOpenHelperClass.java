package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.sql.Date;

/**
 * speakenglish
 * Class: SqLiteOpenHelperClass.
 * Created by leedonghun.
 * Created On 2019-12-12.
 * Description: 기기에서  sqlite부분을 접근 하기 위해서  필요한 클래스이다.
 *  이 클래스를 통해서  채팅 내용을  받을 chatting_data 테이블을  -> 생성한다.->  각 포지션마다  로그인시  실행되어 해당 table을 만든다.
 *
 */
public class SqLiteOpenHelperClass extends SQLiteOpenHelper {



    //채팅 내용을  저장하는  sqlite 테이블의 이름이다.
    public static String chatting_data_tablename="chatting_data_store";

    //SqLiteOpenHelperClass  생성자
    public SqLiteOpenHelperClass(Context context, String name,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
          Log.v("check", "SqLiteOpenHelperClass생성자 실행됨.");


    }//본 클래스 생성자 끝


    //sqliteopenhelper  db create 부분
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("check", "SqLiteOpenHelperClass onCreate 실행됨.");

         create_chatting_data_Table(db);//아래  chatting데이터 저장하는 -> 메소드 실행.
    }

    //이부분은 나중에 따로 조사
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //sqlite 테이블을 생성해줌. -> chatting_data_save  의  테이블을 생성해준다.
    public void create_chatting_data_Table(SQLiteDatabase db) {
        Log.v("check", "create_chattingdata_table 실행됨");

        //sql ->  create table 이름  ->  chatting_data_save
        String sql = "CREATE TABLE  if not exists " + chatting_data_tablename +
                "(uid INTEGER not null PRIMARY KEY AUTOINCREMENT," +
                "senderuid TEXT not null default 0,"+
                "roomnamespace TEXT not null default 0," +
                "roomnumber TEXT not null default 0," +
                "teachername TEXT," +
                "userposition TEXT not null, " +
                "sendername TEXT not null, " +
                "profilepath TEXT not null," +
                "viewtype TEXT not null default -1," +
                "date TEXT not null default 0," +
                "chatorder TEXT not null default -1," +
                "message TEXT," +
                "readornot INTEGER not null default 0)";

        try{

            db.execSQL(sql);

        }catch(SQLException e){
           Log.v("check", "SqliteOpenHelperClass에서  create_chatting_data_Table에서 테이블 create 도중  에러남");

        }
    }//onCreateTable 끝


    //sqlite에 채팅 데이터를 넣어준다.
    public void inserChattingData(SQLiteDatabase db, String senderuid, String roomnamespace, String roomnumber, String teachername, String senderposition, String sendername, String profilepath, String viewtype, String date, String chatorder, String message, int read_or_not){

      db.beginTransaction();//디비에  트랜제션 시작을 알림..


        try{

            //채팅 테이블에  다음 쿼리 값들을 보내준다.
            String sql="insert into "+ chatting_data_tablename+"(senderuid,roomnamespace,roomnumber,teachername,userposition,sendername,profilepath,viewtype,date,chatorder,message,readornot)"
                    +"values('"+senderuid+"','"+roomnamespace+"','"+roomnumber+"','"+teachername+"','"+senderposition+"','"+sendername+"','"+profilepath+"','"+viewtype+"','"+date+"','"+chatorder+"','"+message+"','"+read_or_not+"')";

            db.execSQL(sql);//쿼리문 날림/
            db.setTransactionSuccessful();

        }catch (Exception e){
            Log.v("부디나오거라", e+"ddd");
           e.printStackTrace();

        }

        finally {
            db.endTransaction();//디비에  트랜젝션  끝남을 알림.
        }



    }//inserChattingData() 끝

}//
