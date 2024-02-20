package com.example.ossigenow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.CalendarView;

import java.util.Calendar;

public class CustomCalendarView extends CalendarView {
    private int specialDayYear;
    private int specialDayMonth;
    private int specialDayOfMonth;
    private Paint specialDayBackgroundPaint;

    public CustomCalendarView(Context context) {
        super(context);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        specialDayBackgroundPaint = new Paint();
        specialDayBackgroundPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Disegna lo sfondo per il giorno speciale non selezionato
        drawSpecialDayBackground(canvas);
    }

    private void drawSpecialDayBackground(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Controlla se il giorno speciale non selezionato è visibile
        if (currentYear == specialDayYear && currentMonth == specialDayMonth && currentDayOfMonth != specialDayOfMonth) {
            int cellX = calculateCellX(specialDayOfMonth);
            int cellY = calculateCellY();

            // Disegna lo sfondo per il giorno speciale non selezionato
            canvas.drawCircle(cellX, cellY, 20, specialDayBackgroundPaint);
        }
    }

    private int calculateCellX(int dayOfMonth) {
        // Calcola la larghezza della cella
        int cellWidth = getWidth() / 7; // Dividi la larghezza del calendario per il numero di giorni nella settimana

        // Calcola l'indice della colonna del giorno nel calendario
        int column = dayOfMonth - 1; // Sottrai 1 perché gli indici dei giorni iniziano da 1

        // Calcola la coordinata X del centro della cella
        return cellWidth * column + cellWidth / 2;
    }

    private int calculateCellY() {
        // In questo esempio, assumiamo che tutte le celle abbiano la stessa altezza
        // Calcola l'altezza della cella
        int cellHeight = getHeight() / 6; // Dividi l'altezza del calendario per il numero di righe

        // Assumendo che vogliamo posizionare il cerchio al centro della cella
        return cellHeight / 2;
    }


    public void setSpecialDay(int year, int month, int dayOfMonth) {
        specialDayYear = year;
        specialDayMonth = month;
        specialDayOfMonth = dayOfMonth;
        invalidate(); // Ridisegna la vista per riflettere i cambiamenti
    }
}
