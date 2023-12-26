package com.joyhonest.sports_camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.simple.eventbus.Subscriber;

import java.io.FileDescriptor;
import java.util.List;
public class DispPhotoActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private PagerAdapter adapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public int getCount() {
            return nodes.size();
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            MyImageView myImageView = new MyImageView(DispPhotoActivity.this);
            myImageView.bCanScal = true;
            myImageView.setMaxZoom(3.5f);
            myImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            myImageView.setImageDrawable(new BitmapDrawable(getResources(), LoadBitmap((String) nodes.get(i))));
            viewGroup.addView(myImageView);
            return myImageView;
        }
    };
    private Button btn_back;
    private TextView index_view;
    private List<String> nodes;
    private ViewPager photo_vp;

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageScrolled(int i, float f, int i2) {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MyApp.F_makeFullScreen(this);
        setContentView(R.layout.activity_disp_photo);
        this.btn_back = (Button) findViewById(R.id.btn_back);
        this.photo_vp = (ViewPager) findViewById(R.id.photo_vp);
        TextView textView = (TextView) findViewById(R.id.index_view);
        this.index_view = textView;
        textView.setText((MyApp.dispListInx + 1) + "/" + MyApp.dispList.size());
        this.nodes = MyApp.dispList;
        this.photo_vp.setAdapter(this.adapter);
        this.photo_vp.addOnPageChangeListener(this);
        this.photo_vp.setCurrentItem(MyApp.dispListInx);
        this.btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.PlayBtnVoice();
                onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }

    @Override
    public void onPageSelected(int i) {
        TextView textView = this.index_view;
        textView.setText((i + 1) + "/" + MyApp.dispList.size());
    }

    public Bitmap LoadBitmap(String str) {
        try {
            ParcelFileDescriptor openFileDescriptor = getContentResolver().openFileDescriptor(Uri.parse(str), "r");
            FileDescriptor fileDescriptor = openFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            options.inJustDecodeBounds = false;
            int i2 = (int) (options.outHeight / 1280.0f);
            if (i2 > 0) {
                i = i2;
            }
            options.inSampleSize = i;
            Bitmap decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            openFileDescriptor.close();
            return decodeFileDescriptor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Subscriber(tag = "Go2Background")
    private void Go2Background(String str) {
        finish();
    }
}
