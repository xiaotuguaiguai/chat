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

public class CustomRow extends RelativeLayout {
    private ImageView left_image;
    private TextView left_text;

    public CustomRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custome_row, this, true);
        left_image = (ImageView) findViewById(R.id.left_image);
        left_text = (TextView) findViewById(R.id.left_text);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomRow);
        String leftText = attributes.getString(R.styleable.CustomRow_left_text);
        if(!StringUtils.isEmpty(leftText)){
            left_text.setText(leftText);

        }
        int leftImage = attributes.getResourceId(R.styleable.CustomRow_left_button_drawable,0);
        if(leftImage!=0){
            left_image.setImageResource(leftImage);
        }

        attributes.recycle();
        }


}


