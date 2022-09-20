package org.smartregister.chw.core.fragment;

import android.view.View;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.kvp.fragment.BaseKvpRegisterFragment;

import timber.log.Timber;

public class CoreKvpRegisterFragment extends BaseKvpRegisterFragment {
    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        try {
            NavigationMenu.getInstance(getActivity(), null, toolbar);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            NavigationMenu.getInstance(getActivity(), null, toolbar);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }
}
