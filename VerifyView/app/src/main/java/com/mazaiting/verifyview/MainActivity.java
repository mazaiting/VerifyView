package com.mazaiting.verifyview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.mazaiting.VerifyView;

public class MainActivity extends AppCompatActivity {
  VerifyView mVerifyView;
  EditText mEditText;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mVerifyView = (VerifyView) findViewById(R.id.verifyView);
    mEditText = (EditText) findViewById(R.id.editText);
  }

  public void verify(View view){
    String text = mEditText.getText().toString().trim();
    if (mVerifyView.isEqualsIgnoreCase(text)){
      Toast.makeText(this, "验证成功", Toast.LENGTH_SHORT).show();
    }
  }
}
