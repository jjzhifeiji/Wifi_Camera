package com.joyhonest.sports_camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import org.simple.eventbus.Subscriber

class DispPhotoActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private val adapter: PagerAdapter = object : PagerAdapter() {
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun getCount(): Int {
            return nodes!!.size
        }

        override fun destroyItem(viewGroup: ViewGroup, i: Int, obj: Any) {
            viewGroup.removeView(obj as View)
        }

        override fun instantiateItem(viewGroup: ViewGroup, i: Int): MyImageView {
            val myImageView = MyImageView(this@DispPhotoActivity)
            myImageView.bCanScal = true
            myImageView.maxZoom = 3.5f
            myImageView.scaleType = ImageView.ScaleType.FIT_XY
            myImageView.setImageDrawable(BitmapDrawable(resources, LoadBitmap(nodes!![i])))
            viewGroup.addView(myImageView)
            return myImageView
        }
    }
    private var btn_back: Button? = null
    private var index_view: TextView? = null
    private var nodes: List<String?>? = null
    private var photo_vp: ViewPager? = null
    override fun onPageScrollStateChanged(i: Int) {}
    override fun onPageScrolled(i: Int, f: Float, i2: Int) {}
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        MyApp.F_makeFullScreen(this)
        setContentView(R.layout.activity_disp_photo)
        btn_back = findViewById<View>(R.id.btn_back) as Button
        photo_vp = findViewById<View>(R.id.photo_vp) as ViewPager
        val textView = findViewById<View>(R.id.index_view) as TextView
        index_view = textView
        textView.setText((MyApp.dispListInx + 1).toString() + "/" + MyApp.dispList.size)
        nodes = MyApp.dispList
        photo_vp!!.adapter = adapter
        photo_vp!!.addOnPageChangeListener(this)
        photo_vp!!.currentItem = MyApp.dispListInx
        btn_back!!.setOnClickListener {
            MyApp.PlayBtnVoice()
            onBackPressed()
        }
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
    }

    override fun onPageSelected(i: Int) {
        val textView = index_view
        textView!!.text = (i + 1).toString() + "/" + MyApp.dispList.size
    }

    fun LoadBitmap(str: String?): Bitmap? {
        return try {
            val openFileDescriptor = contentResolver.openFileDescriptor(Uri.parse(str), "r")
            val fileDescriptor = openFileDescriptor!!.fileDescriptor
            val options = BitmapFactory.Options()
            var i = 1
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            options.inJustDecodeBounds = false
            val i2 = (options.outHeight / 1280.0f).toInt()
            if (i2 > 0) {
                i = i2
            }
            options.inSampleSize = i
            val decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            openFileDescriptor.close()
            decodeFileDescriptor
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        finish()
    }
}