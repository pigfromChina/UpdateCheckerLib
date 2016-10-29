package kh.android.updatechecker;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import kh.android.updatecheckerlib.UpdateChecker;

public class MainActivity extends AppCompatActivity implements UpdateChecker.OnCheckListener{
    EditText mEditTextPackageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextPackageName = (EditText)findViewById(R.id.editText_packagename);
        findViewById(R.id.button_coolapk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateChecker.checkSync(UpdateChecker.Market.MARKET_COOLAPK, mEditTextPackageName.getText().toString(), MainActivity.this, MainActivity.this);
            }
        });
        findViewById(R.id.button_googleplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateChecker.checkSync(UpdateChecker.Market.MARKET_GOOGLEPLAY, mEditTextPackageName.getText().toString(), MainActivity.this, MainActivity.this);
            }
        });
    }

    @Override
    public void onStartCheck() {
        Toast.makeText(this, "正在检查", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void done(UpdateChecker.UpdateInfo info, Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("检查结果");
        if (e == null) {
            builder.setMessage(info.getVersionName() + "\n" + info.getChangeLog());
        } else {
            builder.setMessage(e.getMessage());
        }
        builder.show();
    }
}
