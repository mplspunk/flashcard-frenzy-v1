package com.example.flashcardfrenzy;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        String question = getIntent().getStringExtra("string1");
        String answer = getIntent().getStringExtra("string2");

        // Cancels the activity and sends the user back to the main activity
        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCardActivity.this, MainActivity.class);
                finish();
            }
        });

        // Saves the data entered and returns to the main activity
        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText)findViewById(R.id.question)).getText().toString().isEmpty() || ((EditText)findViewById(R.id.answer)).getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fields can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent data = new Intent();
                data.putExtra("string1", ((EditText) findViewById(R.id.question)).getText().toString());
                data.putExtra("string2", ((EditText) findViewById(R.id.answer)).getText().toString());
                setResult(RESULT_OK, data);
                finish();
            }

        });







    }
}
