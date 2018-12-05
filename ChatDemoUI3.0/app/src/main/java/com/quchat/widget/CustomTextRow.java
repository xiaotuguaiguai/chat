package com.quchat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quchat.R;
import com.quchat.utils.StringUtils;

public class CustomTextRow extends RelativeLayout {
    private TextView left_text;
    private ImageView line;
    public CustomTextRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custome_text_row, this, true);

        left_text = (TextView) findViewById(R.id.left_text);
        line= (ImageView) findViewById(R.id.line);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomTextRow);
        String leftText = attributes.getString(R.styleable.CustomTextRow_text_left_text);
        boolean isshowLine = attributes.getBoolean(R.styleable.CustomTextRow_boolean_line,true);
        if(!StringUtils.isEmpty(leftText)){
            left_text.setText(leftText);
        }
        if(!isshowLine){
            line.setVisibility(GONE);
        }else{
            line.setVisibility(VISIBLE);
        }
        attributes.recycle();
        }


}


