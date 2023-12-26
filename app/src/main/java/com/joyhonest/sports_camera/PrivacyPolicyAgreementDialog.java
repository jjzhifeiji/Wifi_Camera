package com.joyhonest.sports_camera;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.internal.view.SupportMenu;
import androidx.fragment.app.DialogFragment;
public class PrivacyPolicyAgreementDialog extends DialogFragment implements View.OnClickListener {
    protected static final String EXTRA_MESSAGE = "message";
    protected static final String EXTRA_NEGATIVE_LABEL = "negative_label";
    protected static final String EXTRA_POSITIVE_LABEL = "positive_label";
    protected static final String EXTRA_TITLE = "title";
    private Context context;
    protected Listener mListener;
    private TextView messageView;

    public interface Listener {
        void onNo();

        void onYes();
    }

    public static PrivacyPolicyAgreementDialog newInstance(Context context, String str, String str2, String str3, String str4, Listener listener) {
        PrivacyPolicyAgreementDialog privacyPolicyAgreementDialog = new PrivacyPolicyAgreementDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, str);
        bundle.putString(EXTRA_MESSAGE, str2);
        bundle.putString(EXTRA_POSITIVE_LABEL, str3);
        bundle.putString(EXTRA_NEGATIVE_LABEL, str4);
        privacyPolicyAgreementDialog.setArguments(bundle);
        privacyPolicyAgreementDialog.context = context;
        privacyPolicyAgreementDialog.mListener = listener;
        return privacyPolicyAgreementDialog;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View generateContentView = generateContentView(bundle);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return generateContentView;
    }

    protected View generateContentView(Bundle bundle) {
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.dialog_privacy_policy_agreement_content, (ViewGroup) null);
        if (inflate == null) {
            return inflate;
        }
        ((TextView) inflate.findViewById(R.id.dialog_title)).setText(getArguments().getString(EXTRA_TITLE));
        TextView textView = (TextView) inflate.findViewById(R.id.extra_msg);
        this.messageView = textView;
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.messageView.setText(getClickableSpan(getArguments().getString(EXTRA_MESSAGE)));
        TextView textView2 = (TextView) inflate.findViewById(R.id.positive_label);
        textView2.setText(getArguments().getString(EXTRA_POSITIVE_LABEL));
        textView2.setOnClickListener(this);
        TextView textView3 = (TextView) inflate.findViewById(R.id.negative_label);
        textView3.setText(getArguments().getString(EXTRA_NEGATIVE_LABEL));
        textView3.setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        resizeDialogWindow(getResources().getConfiguration());
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        resizeDialogWindow(configuration);
    }

    private void resizeDialogWindow(Configuration configuration) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            dialog.getWindow().setLayout((int) (displayMetrics.widthPixels * (displayMetrics.widthPixels > displayMetrics.heightPixels ? 0.45f : 0.75f)), -2);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.positive_label) {
            dismiss();
            this.mListener.onYes();
        } else if (id == R.id.negative_label) {
            dismiss();
            this.mListener.onNo();
        }
    }

    private SpannableString getClickableSpan(String str) {
        String string = getResources().getString(R.string.privacy);
        int indexOf = str.indexOf(string);
        int length = string.length() + indexOf;
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new UnderlineSpan(), indexOf, length, 33);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, PrivacyPolicyActivity.class));
            }
        }, indexOf, length, 33);
        spannableString.setSpan(new ForegroundColorSpan((int) SupportMenu.CATEGORY_MASK), indexOf, length, 33);
        return spannableString;
    }
}
