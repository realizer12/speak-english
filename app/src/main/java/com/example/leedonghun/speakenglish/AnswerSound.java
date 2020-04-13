package com.example.leedonghun.speakenglish;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * speakenglish
 * Class: AnswerSound.
 * Created by leedonghun.
 * Created On 2020-01-24.
 *
 *
 * Description:  단어 시험 에서 -> 틀리고 맞았을때 나오는 효과음 을  나오게 하는  클래스이다.
 * 틀렸을때와  맞았을때 소리를 나누어서 제공한다.
 */


public class AnswerSound {

        private static AnswerSound mAnsewerClickSound;//현재 클래스 객체
        private int mRightAnswerSound;//맞은소리 담을 객체
        private int mWrongAnswerSound;//틀린 소리 담을 객체
        private SoundPool mSoundPool;//오디오 파일 플레이할 sound pool  객체

        public AnswerSound(Context context) {//클래스 생성자
            //사운드 풀  ->  로드함.
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0 );
            mRightAnswerSound =  mSoundPool.load(context, R.raw.correct, 1);//맞은  소리
            mWrongAnswerSound =  mSoundPool.load(context, R.raw.wrong, 1);//틀린 소리
        }

        public static AnswerSound getInstance(Context context) {
            if(mAnsewerClickSound == null)//본 클래스 객체가 null이면
                mAnsewerClickSound = new AnswerSound(context);//새롭게 선언해준다.

            return mAnsewerClickSound;
        }

        public void playWrongAnswerSound() {//틀렸을ㄸ 내는 소리

            //틀린 소리  사운드풀 플레이함.
            mSoundPool.play(mWrongAnswerSound,0.5f,0.5f,1,0,1);
        }

        public void playRightAnswerSound() {//맞았을때 내는 소리

            //맞은 소리 사운드플 플레이함.
            mSoundPool.play(mRightAnswerSound,0.5f,0.5f,1,0,1);
        }


}
