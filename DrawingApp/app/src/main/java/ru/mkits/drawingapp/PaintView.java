package ru.mkits.drawingapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class PaintView extends View {
    ArrayList<Stroke> strokes = new ArrayList<>();
    Stack<Stroke> undoneStrokes = new Stack<>();
    Stroke currentStroke;
    int currentColor = Color.BLACK;
    float strokeWidth = 15f;


    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (Stroke stroke : strokes) {
            canvas.drawPath(stroke.path, stroke.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                undoneStrokes.clear();
                currentStroke = new Stroke(currentColor, strokeWidth);
                currentStroke.path.moveTo(x, y);
                strokes.add(currentStroke);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (currentStroke != null) {
                    currentStroke.path.lineTo(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    public void changeColor(Button btn) {
        Random random = new Random();
        currentColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        btn.setBackgroundColor(currentColor);
    }

    public void clearCanvas() {
        strokes.clear();
        invalidate();
    }

    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
    }

    public void undo() {
        if (!strokes.isEmpty()) {                                           // 1. Проверяем, есть ли что отменять
            undoneStrokes.push(                                             // 3. Помещаем в стек "отменённых"
                    strokes.remove(strokes.size() - 1));              // 2. Удаляем ПОСЛЕДНИЙ штрих из текущих
            invalidate();                                                   // 4. Перерисовываем холст
        }
    }

    public void redo() {
        if (!undoneStrokes.isEmpty()) {                                     // 1. Проверяем, есть ли что вернуть
            strokes.add(undoneStrokes.pop());                               // 3. Добавляем в конец текущих штрихов
            invalidate();                                                   // 2. Извлекаем ПОСЛЕДНИЙ отменённый штрих
        }                                                                   // 4. Перерисовываем холст
    }

    @SuppressLint("WrongThread")
    public void saveToGallery(Context context) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyDrawings");

        ContentResolver resolver = context.getContentResolver();
        try {
            Uri uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri imageUri = resolver.insert(uri, values);
            if (imageUri != null) {
                try (OutputStream outputStream = resolver.openOutputStream(imageUri)){
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                }
            }
            Toast.makeText(context, "Сохранено в галерею", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}
