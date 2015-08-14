package cloudbike.com.materialstuff;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;

/**
 * Created by nongdenchet on 8/11/15.
 */
public class ActivityAnimation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);
        getSupportActionBar().hide();
        Fragment fragment = new InitFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentAnimation.TAG);
        if (fragment != null)
            EventBus.getDefault().post(new FragmentAnimation.ExitEvent());
        else
            super.onBackPressed();
    }

    public void onEvent(BackEvent event) {
        getSupportFragmentManager().popBackStack();
    }

    public static class BackEvent {
    }
}
