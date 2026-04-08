package com.example.lab1_var2; // змініть на ваш пакет

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;import com.example.lab1_var2.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ініціалізація елементів
        EditText editQuestion = findViewById(R.id.editQuestion);
        RadioGroup radioGroupAnswer = findViewById(R.id.radioGroupAnswer);
        Button btnOk = findViewById(R.id.btnOk);
        TextView txtResult = findViewById(R.id.txtResult);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = editQuestion.getText().toString().trim();
                int checkedId = radioGroupAnswer.getCheckedRadioButtonId();

                // 1. Перевірка: чи введено питання і чи обрана відповідь
                if (question.isEmpty() || checkedId == -1) {
                    // Виводимо спливаюче вікно (Toast)
                    Toast.makeText(MainActivity.this,
                            "Будь ласка, введіть питання та оберіть відповідь!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // 2. Отримуємо текст обраної радіо-кнопки
                    RadioButton selectedButton = findViewById(checkedId);
                    String answer = selectedButton.getText().toString();

                    // 3. Виводимо результат у TextView
                    String resultText = "Питання: " + question + "\nВідповідь: " + answer;
                    txtResult.setText(resultText);
                }
            }
        });
    }
}