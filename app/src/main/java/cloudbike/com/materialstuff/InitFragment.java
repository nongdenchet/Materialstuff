package cloudbike.com.materialstuff;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nongdenchet on 8/11/15.
 */
public class InitFragment extends Fragment {

    @InjectView(R.id.check)
    ImageView button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_init, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentAnimation();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment, FragmentAnimation.TAG)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

}
