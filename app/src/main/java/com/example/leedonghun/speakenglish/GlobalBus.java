package com.example.leedonghun.speakenglish;

import com.squareup.otto.Bus;

/**
 * speakenglish
 * Class: GlobalBus.
 * Created by leedonghun.
 * Created On 2019-12-18.
 * Description: 이벤트 버스로 채팅 메세지 이벤트 발생시  컴폰넌트끼리  버스 역할을  할  클래스이다.
 */
public class GlobalBus {

    private static Bus bus_data;//데이터 버스로 보내는 객체

    public static Bus getBus_data(){

        if(bus_data==null){

            bus_data=new Bus();
        }

        return bus_data;
    }//

}//GlobalBus  클래스 끝.
