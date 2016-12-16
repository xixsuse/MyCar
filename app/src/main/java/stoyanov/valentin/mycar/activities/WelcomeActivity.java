package stoyanov.valentin.mycar.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import stoyanov.valentin.mycar.PreferenceManager;
import stoyanov.valentin.mycar.R;

public class WelcomeActivity extends BaseActivity
                        implements ViewPager.OnPageChangeListener{

    private PreferenceManager preferenceManager;
    private ViewPager viewPager;
    private Button buttonNext;
    private View tab1, tab2, tab3;
    private int[] sliders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext(), MODE_PRIVATE);
        if (!preferenceManager.isFirstLaunch()) {
            preferenceManager.setFirstTimeLaunch(false);
            launchMainActivity();
            finish();
        }
        setContentView(R.layout.activity_welcome);
        setStatusBarColor(ResourcesCompat.getColor(getResources(), R.color.colorWelcome1, null));
        initComponents();
        setComponentListeners();
        WelcomeViewPagerAdapter viewPagerAdapter = new WelcomeViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        addTabColors(0);
    }

    @Override
    protected void initComponents() {
        viewPager = (ViewPager) findViewById(R.id.vp_welcome);
        buttonNext = (Button) findViewById(R.id.btn_welcome_next);
        tab1 = findViewById(R.id.view_welcome1);
        tab2 = findViewById(R.id.view_welcome2);
        tab3 = findViewById(R.id.view_welcome3);
        sliders = new int[] {
                R.layout.slide_welcome1,
                R.layout.slide_welcome2,
                R.layout.slide_welcome3
        };
    }

    @Override
    protected void setComponentListeners() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewPagerCurrentSlider = viewPager.getCurrentItem() + 1;
                if (viewPagerCurrentSlider < sliders.length) {
                    viewPager.setCurrentItem(viewPagerCurrentSlider);
                }else {
                    launchMainActivity();
                    finish();
                }
            }
        });
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void addTabColors(int currentSlider) {
        int[] activeColors = getResources().getIntArray(R.array.activeColors);
        int[] inactiveColors = getResources().getIntArray(R.array.inactiveColors);
        switch (currentSlider) {
            case 0:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome1, null));
                tab1.setBackgroundColor(activeColors[currentSlider]);
                tab2.setBackgroundColor(inactiveColors[currentSlider]);
                tab3.setBackgroundColor(inactiveColors[currentSlider]);
                break;
            case 1:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome2, null));
                tab1.setBackgroundColor(inactiveColors[currentSlider]);
                tab2.setBackgroundColor(activeColors[currentSlider]);
                tab3.setBackgroundColor(inactiveColors[currentSlider]);
                break;
            case 2:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome3, null));
                tab1.setBackgroundColor(inactiveColors[currentSlider]);
                tab2.setBackgroundColor(inactiveColors[currentSlider]);
                tab3.setBackgroundColor(activeColors[currentSlider]);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        addTabColors(position);
        int currentPosition = position + 1;
        if (currentPosition == sliders.length) {
            buttonNext.setText(getString(R.string.got_it));
        }else {
            buttonNext.setText(getString(R.string.next));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class WelcomeViewPagerAdapter extends PagerAdapter{

        private LayoutInflater inflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(sliders[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return sliders.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}