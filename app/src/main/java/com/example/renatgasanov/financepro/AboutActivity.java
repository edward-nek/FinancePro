package com.example.renatgasanov.financepro;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

        //скрыть панель навигации начало

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility)
                    {
                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });

        //скрыть панель навигации конец

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.header));
        getWindow().setStatusBarColor(getResources().getColor(R.color.header));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textHeader));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.header));
        toolbar.setTitle("Список транзакций");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        return true;
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

    //отслеживание нажатий на экран

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
