package com.example.offline_householdbook.Calendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class FadeOutItemAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateRemove(@NonNull final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;

        // 현재 뷰의 알파값을 저장
        final float prevAlpha = view.getAlpha();

        // fade out 애니메이션 생성
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, View.ALPHA, prevAlpha, 0f);
        fadeOut.setDuration(300);

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchRemoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(prevAlpha); // 원래 알파값으로 복구
                dispatchRemoveFinished(holder);
            }
        });

        fadeOut.start();
        return true;
    }

    @Override
    public boolean animateAdd(@NonNull RecyclerView.ViewHolder holder) {
        // 기본 추가 애니메이션 비활성화
        dispatchAddFinished(holder);
        return false;
    }

    @Override
    public long getRemoveDuration() {
        return 300; // fade out 지속 시간
    }
}