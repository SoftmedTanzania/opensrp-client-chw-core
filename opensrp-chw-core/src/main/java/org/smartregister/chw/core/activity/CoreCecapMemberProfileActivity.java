package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.cecap.activity.BaseCecapProfileActivity;
import org.smartregister.chw.core.R;
import org.smartregister.chw.sbc.util.Constants;

public class CoreCecapMemberProfileActivity extends BaseCecapProfileActivity {
    protected RecyclerView notificationAndReferralRecyclerView;

    protected RelativeLayout notificationAndReferralLayout;

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, CoreCecapMemberProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    protected void initializeNotificationReferralRecyclerView() {
        notificationAndReferralLayout = findViewById(R.id.notification_and_referral_row);
        notificationAndReferralRecyclerView = findViewById(R.id.notification_and_referral_recycler_view);
        notificationAndReferralRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
