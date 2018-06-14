package com.battlelancer.seriesguide.ui;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.battlelancer.seriesguide.R;
import com.battlelancer.seriesguide.widgets.EmptyViewSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.Unbinder;

public class BaseNowFragment extends Fragment {
    @BindView(R.id.swipeRefreshLayoutNow)
    EmptyViewSwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerViewNow)
    RecyclerView recyclerView;
    @BindView(R.id.emptyViewNow)
    TextView emptyView;
    @BindView(R.id.containerSnackbar)
    View snackbar;
    @BindView(R.id.textViewSnackbar) TextView snackbarText;
    @BindView(R.id.buttonSnackbar)
    Button snackbarButton;

    protected Unbinder unbinder;
    protected boolean isLoadingRecentlyWatched;
    protected boolean isLoadingFriends;
    protected boolean isLoadingReleasedToday;

    public void setupARefreshLayout() {
         swipeRefreshLayout.setSwipeableChildren(R.id.scrollViewNow, R.id.recyclerViewNow);
         swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 refreshStream();
             }
         });
         swipeRefreshLayout.setProgressViewOffset(false,
                 getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_bar_start_margin),
                 getResources().getDimensionPixelSize(R.dimen.swipe_refresh_progress_bar_end_margin));
    }

    public void refreshStream() {
        // Needs to be overwritten
    }

    public void showError(@Nullable String errorText) {
        boolean show = errorText != null;
        if (show) {
            snackbarText.setText(errorText);
        }
        if (snackbar.getVisibility() == (show ? View.VISIBLE : View.GONE)) {
            // already in desired state, avoid replaying animation
            return;
        }
        snackbar.startAnimation(AnimationUtils.loadAnimation(snackbar.getContext(),
                show ? R.anim.fade_in : R.anim.fade_out));
        snackbar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
