package com.joyhonest.sports_camera

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.internal.view.SupportMenu
import androidx.fragment.app.DialogFragment


class PrivacyPolicyAgreementDialog : DialogFragment(), View.OnClickListener {
    private var context: Context? = null
    protected var mListener: Listener? = null
    private var messageView: TextView? = null

    interface Listener {
        fun onNo()
        fun onYes()
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        val generateContentView = generateContentView(bundle)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        return generateContentView
    }

    protected fun generateContentView(bundle: Bundle?): View? {
        val inflate = activity!!.layoutInflater.inflate(R.layout.dialog_privacy_policy_agreement_content, null as ViewGroup?)
        if (inflate == null) {
            return inflate
        }
        (inflate.findViewById<View>(R.id.dialog_title) as TextView).text = arguments!!.getString(EXTRA_TITLE)
        val textView = inflate.findViewById<View>(R.id.extra_msg) as TextView
        messageView = textView
        textView.movementMethod = LinkMovementMethod.getInstance()
        messageView!!.text = getClickableSpan(arguments!!.getString(EXTRA_MESSAGE))
        val textView2 = inflate.findViewById<View>(R.id.positive_label) as TextView
        textView2.text = arguments!!.getString(EXTRA_POSITIVE_LABEL)
        textView2.setOnClickListener(this)
        val textView3 = inflate.findViewById<View>(R.id.negative_label) as TextView
        textView3.text = arguments!!.getString(EXTRA_NEGATIVE_LABEL)
        textView3.setOnClickListener(this)
        return inflate
    }

    override fun onStart() {
        super.onStart()
        resizeDialogWindow(resources.configuration)
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        resizeDialogWindow(configuration)
    }

    private fun resizeDialogWindow(configuration: Configuration) {
        val dialog = dialog
        if (dialog != null) {
            val displayMetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            dialog.window!!.setLayout((displayMetrics.widthPixels * if (displayMetrics.widthPixels > displayMetrics.heightPixels) 0.45f else 0.75f).toInt(), -2)
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.positive_label) {
            dismiss()
            mListener!!.onYes()
        } else if (id == R.id.negative_label) {
            dismiss()
            mListener!!.onNo()
        }
    }

    private fun getClickableSpan(str: String?): SpannableString {
        val string = resources.getString(R.string.privacy)
        val indexOf = str!!.indexOf(string)
        val length = string.length + indexOf
        val spannableString = SpannableString(str)
        spannableString.setSpan(UnderlineSpan(), indexOf, length, 33)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                context!!.startActivity(Intent(context, PrivacyPolicyActivity::class.java))
            }
        }, indexOf, length, 33)
        spannableString.setSpan(ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf, length, 33)
        return spannableString
    }

    companion object {
        protected const val EXTRA_MESSAGE = "message"
        protected const val EXTRA_NEGATIVE_LABEL = "negative_label"
        protected const val EXTRA_POSITIVE_LABEL = "positive_label"
        protected const val EXTRA_TITLE = "title"
        fun newInstance(context: Context?, str: String?, str2: String?, str3: String?, str4: String?, listener: Listener?): PrivacyPolicyAgreementDialog {
            val privacyPolicyAgreementDialog = PrivacyPolicyAgreementDialog()
            val bundle = Bundle()
            bundle.putString(EXTRA_TITLE, str)
            bundle.putString(EXTRA_MESSAGE, str2)
            bundle.putString(EXTRA_POSITIVE_LABEL, str3)
            bundle.putString(EXTRA_NEGATIVE_LABEL, str4)
            privacyPolicyAgreementDialog.arguments = bundle
            privacyPolicyAgreementDialog.context = context
            privacyPolicyAgreementDialog.mListener = listener
            return privacyPolicyAgreementDialog
        }
    }
}