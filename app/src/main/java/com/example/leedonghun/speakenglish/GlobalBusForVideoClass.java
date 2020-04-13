package com.example.leedonghun.speakenglish;

import com.squareup.otto.Bus;

/**
 * speakenglish
 * Class: GlobalBusForVideoClass.
 * Created by leedonghun.
 * Created On 2020-02-21.
 * Description: 비디오 수업 관련 해서  이벤트 발생시  컴포넌트 끼리  값을 주고 받을  이벤트 버스 클래스이다.
 */
public class GlobalBusForVideoClass {

    private static Bus bus_data;//데이터 버스로 보내는 객체

    public static Bus getBus_data(){

        if(bus_data==null){

            bus_data=new Bus();
        }

        return bus_data;
    }//

}//GlobalBus  클래스 끝.