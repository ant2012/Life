package ru.ant.life;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    public static final String ORG_INITIAL_COUNT = "ru.ant.life.INITIAL_COUNT";
    public static final String DIE_COUNT = "ru.ant.life.DIE_COUNT";
    public static final String SPLIT_COUNT = "ru.ant.life.SPLIT_COUNT";
    public static final String REFRESH_INTERVAL = "ru.ant.life.REFRESH_INTERVAL";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linkEditTextToSeekBar(R.id.organismCount, R.id.initialCountSeekBar);
        linkEditTextToSeekBar(R.id.dieCountEditText, R.id.dieCountSeekBar);
        linkEditTextToSeekBar(R.id.splitCountEditText, R.id.splitCountSeekBar);

        prefs = getPreferences(MODE_PRIVATE);

        restoreEditTextValueFromPrefs(prefs, R.id.organismCount, ORG_INITIAL_COUNT, 10);
        restoreEditTextValueFromPrefs(prefs, R.id.dieCountEditText, DIE_COUNT, 5);
        restoreEditTextValueFromPrefs(prefs, R.id.splitCountEditText, SPLIT_COUNT, 4);
    }

    private void restoreEditTextValueFromPrefs(SharedPreferences prefs, int editTextId, String prefKey, int defaultValue) {
        EditText editText = findViewById(editTextId);
        int value = prefs.getInt(prefKey, defaultValue);
        editText.setText(String.valueOf(value));
    }

    private void linkEditTextToSeekBar(int editTextId, int seekBarId) {
        EditText editText = findViewById(editTextId);
        SeekBar seekBar = findViewById(seekBarId);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int value = getNumericFromStringDefaultZero(s.toString());
                int seekBarValue = seekBar.getProgress();
                if (value != seekBarValue)
                    seekBar.setProgress(value);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!fromUser) return;
                editText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private int getNumericFromStringDefaultZero(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public void goLive(View view){
        Intent intent = new Intent(this, LifeSurfaceFullscreenActivity.class);

        EditText organismCountEditText = findViewById(R.id.organismCount);
        int organismCount = getNumericFromStringDefaultZero(organismCountEditText.getText().toString());
        intent.putExtra(ORG_INITIAL_COUNT, organismCount);

        EditText dieCountEditText = findViewById(R.id.dieCountEditText);
        int dieCount = getNumericFromStringDefaultZero(dieCountEditText.getText().toString());
        intent.putExtra(DIE_COUNT, dieCount);

        EditText splitCountEditText = findViewById(R.id.splitCountEditText);
        int splitCount = getNumericFromStringDefaultZero(splitCountEditText.getText().toString());
        intent.putExtra(SPLIT_COUNT, splitCount);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(ORG_INITIAL_COUNT, organismCount);
        editor.putInt(DIE_COUNT, dieCount);
        editor.putInt(SPLIT_COUNT, splitCount);
        editor.apply();

        startActivity(intent);
    }
}
