package com.swipecare.payments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swipecare.payments.TransactionReports.TransactionReports
import com.swipecare.payments.databinding.ReportActivityBinding
import com.swipecare.payments.menuReports.AccountStatementTransactionsActivity
import kotlinx.android.synthetic.main.report_activity.*
import kotlinx.coroutines.CoroutineScope

class Reportactivity: AppCompatActivity() {
    private lateinit var binding: ReportActivityBinding
    private lateinit var sharedPreferences: SharePrfeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReportActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recharge.setOnClickListener{
            val intent = Intent(this@Reportactivity, TransactionReports::class.java)
            intent.putExtra("type", "rechargestatement")
            startActivity(intent)
        }
        sharedPreferences = SharePrfeManager.getInstance(this)
        this.setSupportActionBar(toolbar)
        window.statusBarColor = this.resources.getColor(R.color.colorPrimary)


        binding.mainwallet.setOnClickListener{
            startActivity(Intent(applicationContext, AccountStatementTransactionsActivity::class.java))

        }

        // Set Home selected
        binding.bottomNavigationView.selectedItemId = R.id.my_reports

        // Perform item selected listener

        // Perform item selected listener
        binding.run {
            bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener setOnNavigationItemSelectedListener@{ item: MenuItem ->
                when (item.itemId) {
                    R.id.home -> {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.my_reports -> return@setOnNavigationItemSelectedListener true
                    R.id.profile ->{
                        startActivity(Intent(applicationContext, Profileactivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.help ->{
                        startActivity(Intent(applicationContext, HelpActtivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.actionLogout ->{
                        SharePrfeManager.getInstance(this@Reportactivity).mLogout()
                        startActivity(Intent(applicationContext, Login::class.java))
                        finish()
                        return@setOnNavigationItemSelectedListener true
                    }
                }
                false
            })
        }
    }
}