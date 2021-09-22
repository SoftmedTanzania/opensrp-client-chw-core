package org.smartregister.chw.core.contract;

import android.app.Activity;

import org.smartregister.chw.core.model.NavigationModel;
import org.smartregister.chw.core.model.NavigationOption;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface NavigationContract {

    interface Presenter {

        NavigationContract.View getNavigationView();

        void refreshNavigationCount(Activity activity);

        void refreshLastSync();

        void displayCurrentUser();

        void sync(Activity activity);

        List<NavigationOption> getOptions();

        void updateTableMap(HashMap<String, String> tableMap);

        void checkSynced(Activity activity);
    }

    interface View {

        void prepareViews(Activity activity);

        void refreshLastSync(Date lastSync);

        void refreshCurrentUser(String name);

        void logout(Activity activity);

        void refreshCount();

        void displayToast(Activity activity, String message);

        void toggleProgressBarView(boolean syncing);

        void updateSyncStatusDisplay(Activity activity, boolean synced);
    }

    interface Model {

        void setNavigationFlavor(NavigationModel.Flavor flavor);

        List<NavigationOption> getNavigationItems();

        String getCurrentUser();
    }

    interface Interactor {

        Date getLastSync();

        void getRegisterCount(String tableName, InteractorCallback<Integer> callback);

        Date sync();

        void setApplication(CoreApplication coreApplication);

        void checkSynced(InteractorCallback<Boolean> callback);
    }

    interface InteractorCallback<T> {
        void onResult(T result);

        void onError(Exception e);
    }

}
