package com.swipecare.payments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.swipecare.payments.Constants.TRANSACTIONS;
import static com.swipecare.payments.R.id.download_mini_statement;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.swipecare.payments.adapters.TransactionsListAdapter;
import com.swipecare.payments.model.Transaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionReciept extends AppCompatActivity {
    LinearLayout ll_title_background;
    CardView cardview_receipt;
    TextView textview_message;
    ImageView mysuccess, myfailed;

    TextView textview_transaction_id, textview_transaction_type, textview_operator, textview_number, textview_price, textview_rrn;

    ImageView imageview_icon;
    String status = "", message = "", transactionid = "", transaction_type = "", operator = "", number = "", price = "", transactionrrn = "";
    File file = null;

    int pageHeight = 1120;
    int pageWidth = 792;
    Bitmap bmp, scaledBmp;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private SharePrfeManager sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_transaction_reciept);
        sharedPreferences = SharePrfeManager.getInstance(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ll_title_background = findViewById(R.id.ll_title_background);
        cardview_receipt = findViewById(R.id.cardview_receipt);
        textview_message = findViewById(R.id.textview_message);
        textview_transaction_id = findViewById(R.id.textview_transaction_id);
        textview_transaction_type = findViewById(R.id.textview_transaction_type);
        textview_operator = findViewById(R.id.textview_operator);
        textview_number = findViewById(R.id.textview_number);
        textview_price = findViewById(R.id.textview_price);
        textview_rrn = findViewById(R.id.textview_rrn);

        mysuccess = findViewById(R.id.mysuccess);
        myfailed = findViewById(R.id.myfailed);

        status = getIntent().getStringExtra("status");
        message = getIntent().getStringExtra("message");
        transactionid = getIntent().getStringExtra("transactionid");
        transactionrrn = getIntent().getStringExtra("transactionrrn");
        transaction_type = getIntent().getStringExtra("transaction_type");
        operator = getIntent().getStringExtra("operator");
        number = getIntent().getStringExtra("number");
        price = getIntent().getStringExtra("price");
        ArrayList<Parcelable> transactions = getIntent().getParcelableArrayListExtra("transactions");

        textview_transaction_id.setText(transactionid);
        textview_rrn.setText(transactionrrn);
        textview_transaction_type.setText(transaction_type);
        textview_operator.setText(operator);
        textview_number.setText(number);
        textview_price.setText("\u20B9 " + price);

        textview_message.setText(message);
        try {
            if (status.equalsIgnoreCase("success")) {
                mysuccess.setVisibility(View.VISIBLE);
                ll_title_background.setBackgroundColor(getResources().getColor(R.color.green));

            } else if (status.equalsIgnoreCase("failed")) {
                myfailed.setVisibility(View.VISIBLE);
                ll_title_background.setBackgroundColor(getResources().getColor(R.color.red));
            } else {
                ll_title_background.setBackgroundColor(getResources().getColor(R.color.orange));
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<Transaction> userTransactions = getIntent().getParcelableArrayListExtra(TRANSACTIONS);
        if (userTransactions != null) {
            getMenuInflater().inflate(R.menu.mini_statement_download_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case download_mini_statement: {
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.swipecare_logo);
                scaledBmp = Bitmap.createScaledBitmap(bmp, 260, 56, false);

                if (checkPermission()) {
                    generatePdf();
                } else {
                    requestPermission();
                }
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void generatePdf() {
        PdfDocument pdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint title = new Paint();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);

        Canvas canvas = myPage.getCanvas();
        canvas.drawBitmap(scaledBmp, canvas.getWidth() / 2 - 100, 30, paint);

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.black));

        // user details
        int userDetailsPositionX = 20;
        int userDetailsPositionY = 140;
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(20);
        canvas.drawText(sharedPreferences.mGetUsername(), userDetailsPositionX, userDetailsPositionY, title);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setTextSize(17);
        canvas.drawText(sharedPreferences.mGetMobile(), userDetailsPositionX, userDetailsPositionY + 30, title);

        // Transactions made by the user
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(20);
        canvas.drawText(getString(R.string.date), userDetailsPositionX, userDetailsPositionY + 80, title);
        canvas.drawText(getString(R.string.description), userDetailsPositionX + 120, userDetailsPositionY + 80, title);
        canvas.drawText(getString(R.string.amountCaps), userDetailsPositionX + canvas.getWidth() / 2 + 100, userDetailsPositionY + 80, title);
        canvas.drawText(getString(R.string.transactionType), (float) (userDetailsPositionX + canvas.getWidth() / 1.2), userDetailsPositionY + 80, title);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(18);

        int yStartPosition = userDetailsPositionY + 160;
        int xStartPosition = userDetailsPositionX + 120;
        List<Transaction> userTransactions = getIntent().getParcelableExtra("transactions");
        if (userTransactions != null) {
            for (int i = 0; i < userTransactions.size(); i++) {
                canvas.drawText(userTransactions.get(i).getDate(), userDetailsPositionX, yStartPosition, title);
                // TODO: for large description we have to do text wrapping.
                TextPaint tp = new TextPaint();
                tp.setColor(Color.BLACK);
                tp.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tp.setTextSize(17);
                int textWidth = (int) ((float) (userDetailsPositionX + canvas.getWidth() / 1.5) - userDetailsPositionX + canvas.getWidth() / 2);
                StaticLayout staticLayout = new StaticLayout(userTransactions.get(i).getDescription(), tp,
                        300, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
                canvas.translate(xStartPosition, yStartPosition - 15);
                staticLayout.draw(canvas);
                canvas.translate(-xStartPosition, -yStartPosition + 15);

//            canvas.drawText(userTransactions.get(i).getDescription(), xStartPosition, yStartPosition, title);
                canvas.drawText(String.valueOf(userTransactions.get(i).getAmount()), userDetailsPositionX + 100 + canvas.getWidth() / 2, yStartPosition, title);
                canvas.drawText(userTransactions.get(i).getTxnType(), (float) (userDetailsPositionX + canvas.getWidth() / 1.2), yStartPosition, title);
                yStartPosition += 80;
            }
        } else {
            Toast.makeText(this, getString(R.string.no_transactions_found), Toast.LENGTH_LONG).show();
        }
        pdfDocument.finishPage(myPage);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Mini Statement.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplication(), getString(R.string.pdf_file_generated_successfully), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED || permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
}
