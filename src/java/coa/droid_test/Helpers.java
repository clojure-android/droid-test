package coa.droid_test;

import org.robolectric.util.ActivityController;

public class Helpers {

    public static Object getActivity(ActivityController controller) {
        return controller.get();
    }

}
