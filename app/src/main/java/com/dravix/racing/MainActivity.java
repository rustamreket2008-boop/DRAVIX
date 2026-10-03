package com.dravix.racing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.graphics.*;
import android.view.*;
import android.content.Context;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(new CarMenu(this));
    }

    public static class CarMenu extends View {

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Vibrator vibrator;

        int selectedCar = 0;

        String[] cars = {
                "Sport Red",
                "Sport Blue",
                "Supercar",
                "Racing",
                "Black GT",
                "Street",
                "Police",
                "Green Racer",
                "Neon",
                "DRAVIX X"
        };

        int[] carColors = {
                Color.rgb(225, 30, 45),
                Color.rgb(30, 100, 235),
                Color.rgb(180, 40, 230),
                Color.rgb(245, 150, 20),
                Color.rgb(20, 20, 25),
                Color.rgb(230, 120, 30),
                Color.rgb(30, 80, 220),
                Color.rgb(30, 190, 90),
                Color.rgb(0, 230, 255),
                Color.rgb(255, 190, 20)
        };

        RectF playButton = new RectF();

        public CarMenu(Context context) {
            super(context);

            vibrator = (Vibrator)
                    context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        void vibrate() {
            if (vibrator == null) return;

            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                100,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                );
            } else {
                vibrator.vibrate(100);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {

            int w = getWidth();
            int h = getHeight();

            // BACKGROUND
            p.setShader(new LinearGradient(
                    0, 0,
                    w, h,
                    Color.rgb(4, 7, 18),
                    Color.rgb(35, 55, 85),
                    Shader.TileMode.CLAMP
            ));

            canvas.drawRect(0, 0, w, h, p);
            p.setShader(null);

            // GLOW
            p.setColor(Color.argb(55, 0, 210, 255));
            canvas.drawCircle(
                    w / 2f,
                    h / 2f,
                    260,
                    p
            );

            // TITLE
            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.create(
                    "sans",
                    Typeface.BOLD
            ));

            p.setColor(Color.WHITE);
            p.setTextSize(55);

            canvas.drawText(
                    "DRAVIX",
                    w / 2f,
                    65,
                    p
            );

            p.setTextSize(17);
            p.setColor(Color.LTGRAY);

            canvas.drawText(
                    "SELECT YOUR CAR",
                    w / 2f,
                    92,
                    p
            );

            // CAR
            float cx = w / 2f;
            float cy = h / 2f - 20;

            drawCar(canvas, cx, cy);

            // CAR NAME
            p.setColor(Color.WHITE);
            p.setTextSize(25);

            canvas.drawText(
                    cars[selectedCar],
                    cx,
                    cy + 175,
                    p
            );

            // LEFT BUTTON
            p.setColor(Color.argb(210, 25, 30, 45));

            canvas.drawRoundRect(
                    25,
                    h / 2f - 45,
                    125,
                    h / 2f + 45,
                    20,
                    20,
                    p
            );

            p.setColor(Color.WHITE);
            p.setTextSize(45);

            canvas.drawText(
                    "<",
                    75,
                    h / 2f + 15,
                    p
            );

            // RIGHT BUTTON
            p.setColor(Color.argb(210, 25, 30, 45));

            canvas.drawRoundRect(
                    w - 125,
                    h / 2f - 45,
                    w - 25,
                    h / 2f + 45,
                    20,
                    20,
                    p
            );

            p.setColor(Color.WHITE);

            canvas.drawText(
                    ">",
                    w - 75,
                    h / 2f + 15,
                    p
            );

            // PLAY
            playButton.set(
                    cx - 145,
                    h - 75,
                    cx + 145,
                    h - 15
            );

            p.setColor(Color.rgb(225, 35, 50));

            canvas.drawRoundRect(
                    playButton,
                    22,
                    22,
                    p
            );

            p.setColor(Color.WHITE);
            p.setTextSize(25);

            canvas.drawText(
                    "PLAY",
                    cx,
                    h - 35,
                    p
            );
        }

        void drawCar(Canvas canvas, float cx, float cy) {

            int color = carColors[selectedCar];

            // SHADOW
            p.setColor(Color.argb(100, 0, 0, 0));

            canvas.drawOval(
                    cx - 105,
                    cy + 105,
                    cx + 105,
                    cy + 140,
                    p
            );

            // BODY
            p.setColor(color);

            canvas.drawRoundRect(
                    cx - 82,
                    cy - 120,
                    cx + 82,
                    cy + 120,
                    28,
                    28,
                    p
            );

            // ROOF
            Path roof = new Path();

            roof.moveTo(cx - 60, cy - 105);
            roof.lineTo(cx - 42, cy - 145);
            roof.lineTo(cx + 42, cy - 145);
            roof.lineTo(cx + 60, cy - 105);
            roof.close();

            p.setColor(color);
            canvas.drawPath(roof, p);

            // WINDOWS
            p.setColor(Color.rgb(10, 25, 38));

            canvas.drawRoundRect(
                    cx - 55,
                    cy - 90,
                    cx + 55,
                    cy - 25,
                    15,
                    15,
                    p
            );

            // FRONT WINDOW LINE
            p.setColor(Color.argb(100, 255, 255, 255));
            p.setStrokeWidth(4);

            canvas.drawLine(
                    cx,
                    cy - 88,
                    cx,
                    cy - 28,
                    p
            );

            // LIGHTS
            p.setColor(Color.WHITE);

            canvas.drawRoundRect(
                    cx - 55,
                    cy - 112,
                    cx - 25,
                    cy - 96,
                    8,
                    8,
                    p
            );

            canvas.drawRoundRect(
                    cx + 25,
                    cy - 112,
                    cx + 55,
                    cy - 96,
                    8,
                    8,
                    p
            );

            // REAR LIGHTS
            p.setColor(Color.RED);

            canvas.drawRoundRect(
                    cx - 55,
                    cy + 95,
                    cx - 25,
                    cy + 110,
                    6,
                    6,
                    p
            );

            canvas.drawRoundRect(
                    cx + 25,
                    cy + 95,
                    cx + 55,
                    cy + 110,
                    6,
                    6,
                    p
            );

            // WHEELS
            p.setColor(Color.BLACK);

            canvas.drawRoundRect(
                    cx - 98,
                    cy - 75,
                    cx - 72,
                    cy - 5,
                    10,
                    10,
                    p
            );

            canvas.drawRoundRect(
                    cx + 72,
                    cy - 75,
                    cx + 98,
                    cy - 5,
                    10,
                    10,
                    p
            );

            canvas.drawRoundRect(
                    cx - 98,
                    cy + 20,
                    cx - 72,
                    cy + 90,
                    10,
                    10,
                    p
            );

            canvas.drawRoundRect(
                    cx + 72,
                    cy + 20,
                    cx + 98,
                    cy + 90,
                    10,
                    10,
                    p
            );

            // SPECIAL NEON
            if (selectedCar == 8) {

                p.setColor(Color.CYAN);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(5);

                canvas.drawRoundRect(
                        cx - 88,
                        cy - 128,
                        cx + 88,
                        cy + 128,
                        30,
                        30,
                        p
                );

                p.setStyle(Paint.Style.FILL);
            }

            // POLICE STRIPE
            if (selectedCar == 6) {

                p.setColor(Color.WHITE);

                canvas.drawRect(
                        cx - 75,
                        cy - 15,
                        cx + 75,
                        cy + 15,
                        p
                );
            }

            // DRAVIX X
            if (selectedCar == 9) {

                p.setColor(Color.WHITE);
                p.setTextSize(28);
                p.setTypeface(Typeface.DEFAULT_BOLD);

                canvas.drawText(
                        "X",
                        cx,
                        cy + 65,
                        p
                );
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction() != MotionEvent.ACTION_DOWN) {
                return true;
            }

            float x = event.getX();
            float y = event.getY();

            int w = getWidth();
            int h = getHeight();

            // LEFT
            if (x < 140 &&
                    y > h / 2f - 70 &&
                    y < h / 2f + 70) {

                selectedCar--;

                if (selectedCar < 0) {
                    selectedCar = cars.length - 1;
                }

                vibrate();
                invalidate();

                return true;
            }

            // RIGHT
            if (x > w - 140 &&
                    y > h / 2f - 70 &&
                    y < h / 2f + 70) {

                selectedCar++;

                if (selectedCar >= cars.length) {
                    selectedCar = 0;
                }

                vibrate();
                invalidate();

                return true;
            }

            // PLAY
            if (playButton.contains(x, y)) {

                vibrate();

                // Ҳоло интихобшудаи мошин омода аст.
                // Қадами баъдӣ худи GAME мешавад.

                return true;
            }

            return true;
        }
    }
                        }
