package com.example.mycanva;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.mycanva.databinding.ActivitySignInBinding;
import com.example.mycanva.databinding.ActivitySignUpBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.inputEmail.addTextChangedListener(emailWatcher);

        binding.signUp.setOnClickListener(v -> {
            if(isValidEmail(binding.inputEmail.getText().toString())){
                performSignIn();
            }
        });
    }

    private TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!isValidEmail(s.toString())) {
                binding.inputEmailLayout.setBoxStrokeColor(getResources().getColor(R.color.red));
            }
            else {
                binding.inputPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.green));
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    public static boolean isValidEmail(String email) {
        String expression = "^[\\w\\.]+@([\\w]+\\.)+[A-Z]{2,7}$";
        CharSequence inputString = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputString);

        return matcher.matches();

    }

    public void performSignIn(){

    }
}