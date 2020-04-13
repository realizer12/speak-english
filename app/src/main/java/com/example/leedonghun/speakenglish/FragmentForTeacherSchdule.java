package com.example.leedonghun.speakenglish;

import android.app.Fragment;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * speakenglish
 * Class: FragmentForTeacherSchdule.
 * Created by leedonghun.
 * Created On 2019-01-12.
 * Description:
 */
public class FragmentForTeacherSchdule extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView=(ViewGroup)inflater.inflate(R.layout.fragment_for_scheduledteacher,container,false);


        return rootView;
    }
}
