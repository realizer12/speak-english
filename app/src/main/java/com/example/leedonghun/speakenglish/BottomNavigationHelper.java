package com.example.leedonghun.speakenglish;

import android.annotation.SuppressLint;
//import android.support.design.internal.BottomNavigationItemView;
//import android.support.design.internal.BottomNavigationMenuView;
//import android.support.design.widget.BottomNavigationView;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;


/**
 * speakenglish
 * Class: BottomNavigationHelper.
 * Created by leedonghun.
 * Created On 2019-01-16.
 * Description:btottm 네비게ㅅ이션이
 * 아이템 목록이 4개 이상이 되면 쉬프트 모드가 진행되어
 * 한 아이템 목록이 나오면 다른 목록들은 작아지는 애니메이션? 같은 효과 진행된다.
 * 그래서 그효과를 지워주고 싶어서 만들어진 클래스이다.
 *
 */
public class BottomNavigationHelper {
    @SuppressLint("RestrictedApi")
    static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);

                item.setShifting(false);
                // item.setShiftingMode(false);
                // set once again checked value, so view will be updated

                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
}
