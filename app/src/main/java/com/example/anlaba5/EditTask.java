package com.example.anlaba5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditTask extends AppCompatActivity {


    Calendar dateAndTime=Calendar.getInstance();
    String[] prior = {"Очень низкий", "Низкий", "Средний", "Высокий", "Очень высокий"};
    int pr;
    int inx=-1;
    Button data, time;
    boolean not=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        data =(Button)findViewById(R.id.dateButton);
        time=(Button)findViewById(R.id.timeButton);

        // адаптер для выпадающего списка приоритетов
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, prior);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Приоритет");
        // выделяем элемент
        spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позицию (важность) приоритета
                pr=position+1;
                Toast.makeText(getBaseContext(), "Выбран приоритет " + pr, Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        String ind=getIntent().getStringExtra("position");
        int inx1= Integer.parseInt(ind);
        if (inx!=inx1)
        {
            inx= Integer.parseInt(ind);
            Open();
        }
    }
    //Если режим редактирования, то устанавливаем в поля тексты и устанавливаем время
    private void Open()
    {
        ((EditText)findViewById(R.id.editText)).setText(MainActivity.task.get(inx).title);
        ((EditText)findViewById(R.id.editText2)).setText(MainActivity.task.get(inx).desc);
        dateAndTime=MainActivity.task.get(inx).getDateAndTime();
        not=!MainActivity.task.get(inx).notif;
        Not((Button)findViewById(R.id.button2));
    }
    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(EditTask.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(EditTask.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            ((Button)findViewById(R.id.timeButton)).setText("Время: "+hourOfDay+" "+minute);
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            ((Button)findViewById(R.id.dateButton)).setText("Дата: "+year+" "+monthOfYear+" "+dayOfMonth);
        }

    };
    //сохраняем отредактированое/новое
    public void save(View v) {
        String title = ((EditText) findViewById(R.id.editText)).getText().toString();
        String desc = ((EditText) findViewById(R.id.editText2)).getText().toString();
        if (inx==-1) { Task t = new Task(title, desc, dateAndTime, pr, not);
           MainActivity.task.add(t);
       }
       else
       {
           MainActivity.task.get(inx).title=title;
           MainActivity.task.get(inx).desc=desc;
           MainActivity.task.get(inx).dateAndTime=dateAndTime;
           MainActivity.task.get(inx).notif=not;
       }
        MainActivity.adapter.notifyDataSetChanged();
        restartNot(title);
        finish();
    }

    //Меняем отправать/не отправлять уведомление
    public  void Not(View v)
    {
        not=!not;
        if(not)
        { ((Button)findViewById(R.id.button2)).setText("Уведомления ВКЛ");}
            else
        {((Button)findViewById(R.id.button2)).setText("Уведомления ВЫКЛ");}
    }

    //Управление уведомлением (запуск/отмена)
    private void restartNot(String title) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Alarm.class).putExtra("title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
        // На случай, если что-то наделали со временем, сперва отклоним уведомление
        am.cancel(pendingIntent);
        // Устанавливаем уведомление, если включили его отправку
        if (not) {
            long when = dateAndTime.getTimeInMillis();
            am.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
        }
    }

}
