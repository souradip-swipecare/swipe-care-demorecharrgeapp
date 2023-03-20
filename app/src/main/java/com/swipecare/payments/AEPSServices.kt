package com.swipecare.payments

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class AEPSServices : AppCompatActivity() {

    lateinit var ll_aeps_enquiry : LinearLayout
    lateinit var ll_aeps_withdrawal : LinearLayout
    lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aepsservices)
        type = intent.getStringExtra("type").toString()
        ll_aeps_enquiry=findViewById(R.id.ll_aeps_enquiry)
        ll_aeps_withdrawal=findViewById(R.id.ll_aeps_withdrawal)

        ll_aeps_enquiry.setOnClickListener {
            var intent=Intent(this@AEPSServices,AEPSWithdrawal::class.java)
            intent.putExtra("type","balance")
            intent.putExtra("myservice",type)
            startActivity( intent)
        }

        ll_aeps_withdrawal.setOnClickListener {
            var intent=Intent(this@AEPSServices,AEPSWithdrawal::class.java)
            intent.putExtra("type","withdrawal")
            intent.putExtra("myservice",type)
            startActivity( intent)
        }
    }
}
