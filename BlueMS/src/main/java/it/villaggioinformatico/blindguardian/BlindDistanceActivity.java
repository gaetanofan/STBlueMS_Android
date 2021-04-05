package it.villaggioinformatico.blindguardian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.st.BlueMS.R;

public class BlindDistanceActivity extends Activity {

    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";

    NumberPicker picker = null;
    Button button = null;

    protected void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.activity_blind_distance);
        picker = findViewById(R.id.blind_picker);
        picker.setMaxValue(99);
        picker.setMinValue(1);
        picker.setValue(10);
        button = findViewById(R.id.blind_button);
        button.setOnClickListener((View v) -> sendData());
    }

    void sendData(){
        int mScore = picker.getValue();
        Intent intent = new Intent();
        intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
        setResult(RESULT_OK, intent);
        finish();
    }

}
