package cloudbike.com.materialstuff;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by nongdenchet on 8/11/15.
 */
public class FragmentAnimation extends Fragment {
    private static final long TRANSITION_DURATION = 240;
    private final double INIT_ANGLE = (-90 * Math.PI) / 180;
    private float DISTANCE_ANIM = 200;
    private final int COUNT = 5;
    private final int ANIM_DURATION = 128;
    private final int DELAY_DURATION = 56;
    private final float minScale = 0.0f;
    private final float maxScale = 1.0f;
    private float originX, originY;

    private boolean canPress = false;
    public static final String TAG = FragmentAnimation.class.getName();
    private List<ImageView> animateViews = new ArrayList<>();

    @InjectView(R.id.close)
    ImageView button;

    @InjectView(R.id.container)
    View container;

    @InjectView(R.id.setting)
    ImageButton settingBtn;

    @InjectView(R.id.extension)
    ImageButton extensionBtn;

    @InjectView(R.id.home)
    ImageButton homeBtn;

    @InjectView(R.id.favorite)
    ImageButton favoriteBtn;

    @InjectView(R.id.map)
    ImageButton mapBtn;

    @InjectView(R.id.camera)
    ImageButton cameraBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DISTANCE_ANIM = pxFromDp(getActivity(), 100);
    }

    private void setUpAnimation() {
        // Setup start state of the views
        cameraBtn.setScaleX(minScale);
        cameraBtn.setScaleY(minScale);
        originX = cameraBtn.getX();
        originY = cameraBtn.getY();

        mapBtn.setScaleX(minScale);
        mapBtn.setScaleY(minScale);
        favoriteBtn.setScaleX(minScale);
        favoriteBtn.setScaleX(minScale);
        homeBtn.setScaleX(minScale);
        homeBtn.setScaleY(minScale);
        extensionBtn.setScaleX(minScale);
        extensionBtn.setScaleY(minScale);
        settingBtn.setScaleX(minScale);
        settingBtn.setScaleY(minScale);

        // Store all views in the list
        animateViews.add(mapBtn);
        animateViews.add(favoriteBtn);
        animateViews.add(homeBtn);
        animateViews.add(extensionBtn);
        animateViews.add(settingBtn);
    }

    // Start the animation
    private void startIcAnimation() {
        cameraBtn.animate().setInterpolator(new AccelerateInterpolator())
                .setDuration(ANIM_DURATION)
                .scaleY(maxScale)
                .scaleX(maxScale)
                .start();

        for (int i = 0; i < COUNT; i++) {
            ViewPropertyAnimator animator = animateViews.get(i).animate();
            if (i == COUNT - 1) {
                animator.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        canPress = true;
                    }
                });
            }
            animator.setInterpolator(new AccelerateInterpolator())
                    .translationX(getXAnim(originX, DISTANCE_ANIM, COUNT, i, INIT_ANGLE))
                    .translationY(getYAnim(originY, DISTANCE_ANIM, COUNT, i, INIT_ANGLE))
                    .setDuration(ANIM_DURATION)
                    .setStartDelay(DELAY_DURATION * (i + 1))
                    .scaleX(maxScale)
                    .scaleY(maxScale)
                    .start();

        }
    }

    // Reverse the animation
    private void reverseIcAnimation() {
        for (int i = COUNT - 1; i >= 0; i--) {
            animateViews.get(i).animate()
                    .setInterpolator(new DecelerateInterpolator())
                    .translationX(originX)
                    .translationY(originY)
                    .setDuration(ANIM_DURATION)
                    .setStartDelay(DELAY_DURATION * (COUNT - i - 1))
                    .scaleX(minScale)
                    .scaleY(minScale)
                    .start();

        }

        cameraBtn.animate().setInterpolator(new DecelerateInterpolator())
                .setDuration(ANIM_DURATION)
                .setStartDelay(DELAY_DURATION * COUNT)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            animateCircularHide(button);
                        } else {
                            EventBus.getDefault().post(new ActivityAnimation.BackEvent());
                        }
                    }
                })
                .scaleY(minScale)
                .scaleX(minScale)
                .start();
    }

    /**
     * Start the circular animation
     * This method will create a circular animation with the init radius base on the viewroot
     * And the final radius will cover all the screen of the device
     * In this case the final radius base on the "container"
     * @param viewRoot View : the view from which the animation will start
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateCircularShow(View viewRoot) {
        int x = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int y = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(container.getWidth(), container.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(container, x, y, 0, finalRadius);
        container.setVisibility(View.VISIBLE);
        anim.setDuration(TRANSITION_DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startIcAnimation();
            }
        });
        anim.start();
    }

    /**
     * Start the circular animation
     * This method will create a circular animation with the init radius base on the container that will cover the device
     * And the final radius base on the viewroot
     * @param viewRoot View : the view from which the animation will end
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateCircularHide(final View viewRoot) {
        int x = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int y = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int initialRadius = Math.max(container.getWidth(), container.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(container, x, y, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                container.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(TRANSITION_DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                EventBus.getDefault().post(new ActivityAnimation.BackEvent());
            }
        });
        anim.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anim, container, false);
        ButterKnife.inject(this, rootView);
        setUpAnimation();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Listen when layout all set up and ready for animate
        container.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animateCircularShow(button);
                } else {
                    container.setVisibility(View.VISIBLE);
                    container.removeOnLayoutChangeListener(this);
                    startIcAnimation();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canPress) {
                    button.setOnClickListener(null);
                    reverseIcAnimation();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class ExitEvent {
    }

    public void onEvent(ExitEvent event) {
        reverseIcAnimation();
    }

    /* Utilities */

    /**
     * This will return the x-coordinate of the point in the regular polygon
     * @param centerX float : the x-center-coordinate of the regular polygon
     * @param radius float : the distance from center to one point of the regular polygon
     * @param count float : the number of point on the regular polygon
     * @param step int : the number label to the points of the regular polygon (For example with count = 5, step is 0, 1, 2, 3 or 4
     * @param angle double : the delta angle, change it if you want to rotate the polygon
     * @return float : the x-coordinate of th-step
     */
    private float getXAnim(float centerX, float radius, int count, int step, double angle) {
        return (float) (centerX + radius * Math.cos(((2 * Math.PI) / count) * step + angle));
    }

    /**
     * This will return the y-coordinate of the point in the regular polygon
     * @param centerY float : the y-center-coordinate of the regular polygon
     * @param radius float : the distance from center to one point of the regular polygon
     * @param count float : the number of point on the regular polygon
     * @param step int : the number label to the points of the regular polygon (For example with count = 5, step is 0, 1, 2, 3 or 4
     * @param angle double : the delta angle, change it if you want to rotate the polygon
     * @return float : the y-coordinate of th-step
     */
    private float getYAnim(float centerY, float radius, int count, int step, double angle) {
        return (float) (centerY + radius * Math.sin(((2 * Math.PI) / count) * step + angle));
    }

    /**
     * Convert dp to px
     * @param context Context : the context
     * @param dp float : the input dp
     * @return float : the pixel convert from dp
     */
    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

}
