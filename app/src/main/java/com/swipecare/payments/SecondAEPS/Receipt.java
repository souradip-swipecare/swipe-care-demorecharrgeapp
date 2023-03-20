package com.swipecare.payments.SecondAEPS;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.swipecare.payments.R;

public class Receipt extends AppCompatActivity {


    ImageView imageview_status_image;
    TextView textview_status,textview_balanceamount,textview_ackno,textview_message,textview_bankrrn;
    Button button_exit;

    String status="",message="",amount="",bankrrn="",ackno="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        status=getIntent().getStringExtra("status");
        message=getIntent().getStringExtra("message");
        amount=getIntent().getStringExtra("amount");
        bankrrn=getIntent().getStringExtra("bankrrn");
        ackno=getIntent().getStringExtra("ackno");

        imageview_status_image=findViewById(R.id.imageview_status_image);
        textview_status=findViewById(R.id.textview_status);
        textview_balanceamount=findViewById(R.id.textview_balanceamount);
        textview_ackno=findViewById(R.id.textview_ackno);
        textview_message=findViewById(R.id.textview_message);
        textview_bankrrn=findViewById(R.id.textview_bankrrn);
        button_exit=findViewById(R.id.button_exit);

        if (status.equalsIgnoreCase("txn"))
        {
            imageview_status_image.setImageDrawable(getResources().getDrawable(R.drawable.success));
            textview_status.setText("Success");
            textview_status.setTextColor(getResources().getColor(R.color.green));
        }
        else if (status.equalsIgnoreCase("err"))
        {
            imageview_status_image.setImageDrawable(getResources().getDrawable(R.drawable.fail_icon));
            textview_status.setText("Failed");
            textview_status.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            imageview_status_image.setImageDrawable(getResources().getDrawable(R.drawable.error_icon));
            textview_status.setText(status);
            textview_status.setTextColor(getResources().getColor(R.color.orange));
        }

        textview_balanceamount.setText("Amount : Rs "+amount);
        textview_ackno.setText("Ackno : "+ackno);
        textview_message.setText("Message : "+message);
        textview_bankrrn.setText("Bankrrn : "+bankrrn);

        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
