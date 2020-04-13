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
 * Class: FragmentForSudentClasstype.
 * Created by leedonghun.
 * Created On 2019-01-11.
 * Description:
 */
public class FragmentForSudentClasstype extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView=(ViewGroup)inflater.inflate(R.layout.fragment_for_classtypeinstudent,container,false);


        return rootView;
    }


}
