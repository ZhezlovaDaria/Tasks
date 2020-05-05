package com.example.anlaba5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    static public List<Task> task = new ArrayList<>();
    RecyclerView recyclerView;
    static DataAdapter adapter;
    static boolean color=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        // создаем и устанавливаем адаптер рекурсера
        adapter = new DataAdapter(this, task);
        recyclerView.setAdapter(adapter);
        //используем fab для кнопки создания нового уведомления
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, EditTask.class);
                    intent.putExtra("position","-1");
                    startActivity(intent);
            }
        });
        //открываем что было сохранено
        openList();
    }
    //при закрытии и паузе(т.к. в паузе тоже могу закрыть приложение) сериализуем наши таск-объекты
    @Override
    public  void onDestroy() {
        super.onDestroy();
        saveList();
    }
    @Override
    public  void onPause() {
        super.onPause();
        saveList();
    }
    //устанавливаем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    //пункт меню "удалить всё". Чистим лист, обновляем рекурсер
    public void clearAll(MenuItem menuItem) {
        task.clear();
        adapter.notifyDataSetChanged();
    }
    //пункт меню сортировка по дате. Реализуем Comparator для наших тасков (по полю дата,  Calendar), обновляем рекурсер
    public void dateSort(MenuItem menuItem) {
        Collections.sort(task, new Comparator<Task>() {
            public int compare(Task item1, Task item2) {
                if (item1.getDateAndTime().before(item2.getDateAndTime())) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }
    //Пункт меню про цвет. Меняем флажок колора на красить/не красить, обновляем рекурсер (покраска в адаптере)
    public void colorSort(MenuItem menuItem) {
        color=!color;
        adapter.notifyDataSetChanged();
    }
    //Удаление одного пункта. Получаем позицию, удаляем, обновляем рекурсер
    public void removeSingleItem(View v) {
        int removeIndex=recyclerView.findContainingViewHolder(v).getPosition();
        task.remove(removeIndex);
        adapter.notifyDataSetChanged();
    }
    //Редактирование пункта. Получаем позицию, переходим на экран создания (и редактирования)
    public void editItem(View v) {
        int ind=recyclerView.findContainingViewHolder(v).getPosition();
        Intent intent = new Intent(MainActivity.this, EditTask.class);
        intent.putExtra("position",String.valueOf(ind));
        startActivity(intent);
    }
    //выход. Сохраняем таски и завершаем активность
    public void Exit(MenuItem menuItem) {
        saveList();
        finish();
    }
    //сериализация списка тасков.
    private void saveList()
    {
        FileOutputStream fos;
        try {//открываем в поток файл для записи
            fos = openFileOutput("sample.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(task);//записываем в файл и закрываем его
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    //десериализация списка тасков.
    private void openList()
    {
        try{//открываем в поток наш файл
            FileInputStream fis = openFileInput("sample.txt");
            if (fis!=null) {//если файл существует, десириализуем текст обратно в лист тасков, но пока в другой
                ObjectInputStream ois = new ObjectInputStream(fis);
                List<Task> task1 = (ArrayList<Task>) ois.readObject();
                task.clear();//основной лист тасков чистим полностью и переписываем из нового таски по одному. Костыль, но что поделать
                // ("спасибо" рекурсеру за то, что нельзя заменить лист полностью, не сломав адаптер)
                for(int i=0; i<task1.size();i++)
                {
                    task.add(task1.get(i));
                }
                adapter.notifyDataSetChanged();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

}
