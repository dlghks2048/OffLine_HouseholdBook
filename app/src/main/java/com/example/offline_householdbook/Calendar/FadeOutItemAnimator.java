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
    public boolean animateRemove(@NonNull RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
        animator.setDuration(300); // 300ms 동안 애니메이션 실행
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 애니메이션이 끝난 후 실제 아이템을 제거
                view.setAlpha(1.0f); // 다시 초기화
                dispatchRemoveFinished(holder);
            }
        });
        animator.start();
        return true;
    }

    @Override
    public void runPendingAnimations() {
        // 실행 대기 중인 애니메이션 처리
        super.runPendingAnimations();
    }
}
