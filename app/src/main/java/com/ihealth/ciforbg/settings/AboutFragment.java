package com.ihealth.ciforbg.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihealth.ciforbg.R;

/**
 * Created by lynn on 15-7-1.
 */
public class AboutFragment extends Fragment {
    private View mContentView;
    private TextView Tv_Content;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_about, null);
        initWidget();
        initView();
        return mContentView;
    }

    private void initWidget() {
        Tv_Content = (TextView) mContentView.findViewById(R.id.user_about_us_txt);
    }

    private void initView() {
        String orginal = getString(R.string.about_us_txt);
        String website = getString(R.string.home_web_site);
        String result = String.format(orginal, website);
        int locaitonStart = websiteLocation(result, website);
        SpannableStringBuilder spannableString1 = new SpannableStringBuilder(result);
        spannableString1.setSpan(new ClickableSpan() {
            // 在onClick方法中可以编写单击链接时要执行的动作
            @Override
            public void onClick(View widget) {
                Uri uri = Uri.parse("https://www.ihealthlabs.com");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        }, locaitonStart, locaitonStart + website.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置字体前景色
        spannableString1.setSpan(new ForegroundColorSpan(0xff41b5e8), locaitonStart, locaitonStart + website.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为浅蓝
        // 设置字体背景色
        spannableString1.setSpan(new BackgroundColorSpan(0xfff8f8f8), locaitonStart,
                locaitonStart + website.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// 设置背景色为浅灰色
        //spannableString1.append(text,0,text.length());
        Tv_Content.setText(spannableString1);
        Tv_Content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * return the  location of website
     *
     * @return
     */
    public int websiteLocation(String str, String str2) {
        int index = str.indexOf(str2);
        return index;
    }
}
