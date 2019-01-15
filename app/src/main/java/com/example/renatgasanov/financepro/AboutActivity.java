package com.example.renatgasanov.financepro;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    public final static String TITLE = "TITLE";
    public final static String TYPE = "TYPE";
    public final static String CONTENT = "CONTENT";
    public final static String AMOUNT = "AMOUNT";
    public TextView title;
    public TextView amount;
    public TextView category;
    Spinner spinner;

    String[] data = {"Доход", "Расход"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        Button buttonDONE = findViewById(R.id.buttonDone);
        buttonDONE.setOnClickListener(done);
        title = findViewById(R.id.editTitle);
        amount = findViewById(R.id.editAmount);
        category = findViewById(R.id.editCategory);

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Title");
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
    private final View.OnClickListener done = new View.OnClickListener(){
        @Override
        public void onClick(View v){

            boolean b1 = false;
            boolean b2 = false;
            boolean b3 = false;

            if(title.getText().toString().equals("")){b1=true;}
            if(amount.getText().toString().equals("")){b2=true;}
            if(category.getText().toString().equals("")){b3=true;}

            if (b1) {category.setError("Введите описание");}
            if (b2) {amount.setError("Введите сумму");}
            if (b1) {title.setError("Введите категорию");}

            if ((!b1) && (!b2) && (!b3)){
                Intent answerIntent = new Intent();
                answerIntent.putExtra(TITLE, title.getText().toString());
                answerIntent.putExtra(CONTENT, category.getText().toString());
                answerIntent.putExtra(AMOUNT, amount.getText().toString());
                answerIntent.putExtra(TYPE, spinner.getSelectedItem().toString());

                setResult(RESULT_OK, answerIntent);
                finish();
            }
        }
    };
}
