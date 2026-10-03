package com.dravix.racing;

import android.app.Activity;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.graphics.*;
import android.view.*;
import android.content.Context;
import java.util.Random;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        setContentView(new GameView(this));
    }

    public static class GameView extends View {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Random random = new Random();

        float playerX;
        float playerY;

        float roadSpeed = 12;
        float score = 0;

        boolean leftPressed = false;
        boolean rightPressed = false;

        float[] enemyX = new float[4];
        float[] enemyY = new float[4];

        int screenW;
        int screenH;

        boolean started = false;

        public GameView(Context context) {
            super(context);

            paint.setTypeface(Typeface.create("sans", Typeface.BOLD));

            for (int i = 0; i < 4; i++) {
                enemyY[i] = -300 - (i * 450);
                enemyX[i] = random.nextFloat();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            screenW = getWidth();
            screenH = getHeight();

            if (playerX == 0) {
                playerX = screenW / 2f;
                playerY = screenH - 170;
            }

            drawBackground(canvas);
            drawRoad(canvas);
            drawEnemies(canvas);
            drawPlayer(canvas);
            drawUI(canvas);
            drawControls(canvas);

            updateGame();

            postInvalidateDelayed(16);
        }

        private void drawBackground(Canvas canvas) {

            LinearGradient sky = new LinearGradient(
                    0,
                    0,
                    0,
                    screenH,
                    Color.rgb(18, 25, 45),
                    Color.rgb(70, 110, 140),
                    Shader.TileMode.CLAMP
            );

            paint.setShader(sky);
            canvas.drawRect(0, 0, screenW, screenH, paint);
            paint.setShader(null);

            // City buildings

            paint.setColor(Color.rgb(25, 30, 45));

            for (int i = 0; i < 12; i++) {

                float x = i * (screenW / 11f);

                float height = 100 + (i % 4) * 45;

                canvas.drawRect(
                        x,
                        screenH * 0.45f - height,
                        x + screenW / 14f,
                        screenH * 0.45f,
                        paint
                );
            }

            // Building lights

            paint.setColor(Color.rgb(255, 210, 80));

            for (int i = 0; i < 30; i++) {

                float x = (i * 83) % screenW;
                float y = 100 + ((i * 47) % 220);

                canvas.drawRect(
                        x,
                        y,
                        x + 8,
                        y + 12,
                        paint
                );
            }
        }

        private void drawRoad(Canvas canvas) {

            float roadLeft = screenW * 0.18f;
            float roadRight = screenW * 0.82f;

            paint.setColor(Color.rgb(35, 35, 40));

            canvas.drawRect(
                    roadLeft,
                    0,
                    roadRight,
                    screenH,
                    paint
            );

            // Road edges

            paint.setColor(Color.WHITE);

            canvas.drawRect(
                    roadLeft,
                    0,
                    roadLeft + 8,
                    screenH,
                    paint
            );

            canvas.drawRect(
                    roadRight - 8,
                    0,
                    roadRight,
                    screenH,
                    paint
            );

            // Lane lines

            paint.setColor(Color.rgb(230, 230, 230));

            float lane1 = roadLeft + (roadRight - roadLeft) / 3;
            float lane2 = roadLeft + (roadRight - roadLeft) * 2 / 3;

            for (int i = -1; i < 10; i++) {

                float y = (i * 130 + (score * 8) % 130);

                canvas.drawRect(
                        lane1 - 5,
                        y,
                        lane1 + 5,
                        y + 70,
                        paint
                );

                canvas.drawRect(
                        lane2 - 5,
                        y,
                        lane2 + 5,
                        y + 70,
                        paint
                );
            }
        }

        private void drawPlayer(Canvas canvas) {

            float carW = 85;
            float carH = 145;

            // Shadow

            paint.setColor(Color.argb(100, 0, 0, 0));

            canvas.drawOval(
                    playerX - 50,
                    playerY + 50,
                    playerX + 50,
                    playerY + 85,
                    paint
            );

            // Car body

            paint.setColor(Color.rgb(220, 35, 45));

            canvas.drawRoundRect(
                    playerX - carW / 2,
                    playerY - carH / 2,
                    playerX + carW / 2,
                    playerY + carH / 2,
                    18,
                    18,
                    paint
            );

            // Windshield

            paint.setColor(Color.rgb(30, 45, 60));

            canvas.drawRoundRect(
                    playerX - 29,
                    playerY - 52,
                    playerX + 29,
                    playerY - 5,
                    10,
                    10,
                    paint
            );

            // Rear glass

            canvas.drawRoundRect(
                    playerX - 29,
                    playerY + 8,
                    playerX + 29,
                    playerY + 48,
                    10,
                    10,
                    paint
            );

            // Lights

            paint.setColor(Color.WHITE);

            canvas.drawCircle(
                    playerX - 27,
                    playerY - 62,
                    7,
                    paint
            );

            canvas.drawCircle(
                    playerX + 27,
                    playerY - 62,
                    7,
                    paint
            );

            // Wheels

            paint.setColor(Color.BLACK);

            canvas.drawRoundRect(
                    playerX - 52,
                    playerY - 42,
                    playerX - 37,
                    playerY + 30,
                    5,
                    5,
                    paint
            );

            canvas.drawRoundRect(
                    playerX + 37,
                    playerY - 42,
                    playerX + 52,
                    playerY + 30,
                    5,
                    5,
                    paint
            );
        }

        private void drawEnemies(Canvas canvas) {

            float roadLeft = screenW * 0.18f;
            float roadWidth = screenW * 0.64f;

            for (int i = 0; i < 4; i++) {

                float x = roadLeft + enemyX[i] * roadWidth;

                float y = enemyY[i];

                paint.setColor(Color.rgb(40 + i * 35, 90, 210));

                canvas.drawRoundRect(
                        x - 42,
                        y - 70,
                        x + 42,
                        y + 70,
                        15,
                        15,
                        paint
                );

                paint.setColor(Color.rgb(25, 35, 50));

                canvas.drawRoundRect(
                        x - 27,
                        y - 50,
                        x + 27,
                        y - 8,
                        9,
                        9,
                        paint
                );

                canvas.drawRoundRect(
                        x - 27,
                        y + 10,
                        x + 27,
                        y + 50,
                        9,
                        9,
                        paint
                );

                paint.setColor(Color.BLACK);

                canvas.drawRect(
                        x - 50,
                        y - 40,
                        x - 38,
                        y + 30,
                        paint
                );

                canvas.drawRect(
                        x + 38,
                        y - 40,
                        x + 50,
                        y + 30,
                        paint
                );
            }
        }

        private void drawUI(Canvas canvas) {

            paint.setColor(Color.WHITE);
            paint.setTextSize(38);

            canvas.drawText(
                    "DRAVIX",
                    35,
                    55,
                    paint
            );

            paint.setTextSize(28);

            canvas.drawText(
                    "SCORE: " + (int) score,
                    35,
                    95,
                    paint
            );

            paint.setTextSize(20);

            paint.setColor(Color.LTGRAY);

            canvas.drawText(
                    "STREET RACING",
                    35,
                    125,
                    paint
            );
        }

        private void drawControls(Canvas canvas) {

            paint.setColor(Color.argb(110, 255, 255, 255));

            canvas.drawCircle(
                    90,
                    screenH - 80,
                    55,
                    paint
            );

            canvas.drawCircle(
                    screenW - 90,
                    screenH - 80,
                    55,
                    paint
            );

            paint.setColor(Color.BLACK);
            paint.setTextSize(45);

            canvas.drawText(
                    "‹",
                    73,
                    screenH - 65,
                    paint
            );

            canvas.drawText(
                    "›",
                    screenW - 107,
                    screenH - 65,
                    paint
            );
        }

        private void updateGame() {

            if (!started) {
                return;
            }

            score += 0.05f;

            if (leftPressed) {
                playerX -= 10;
            }

            if (rightPressed) {
                playerX += 10;
            }

            float minX = screenW * 0.18f + 55;
            float maxX = screenW * 0.82f - 55;

            if (playerX < minX) {
                playerX = minX;
            }

            if (playerX > maxX) {
                playerX = maxX;
            }

            float roadLeft = screenW * 0.18f;
            float roadWidth = screenW * 0.64f;

            for (int i = 0; i < 4; i++) {

                enemyY[i] += roadSpeed;

                float enemyRealX =
                        roadLeft + enemyX[i] * roadWidth;

                if (enemyY[i] > screenH + 150) {

                    enemyY[i] = -200 - random.nextInt(500);

                    enemyX[i] =
                            0.10f + random.nextFloat() * 0.80f;
                }

                // Collision

                if (
                        Math.abs(playerX - enemyRealX) < 65
                        &&
                        Math.abs(playerY - enemyY[i]) < 100
                ) {

                    score = 0;

                    enemyY[i] = -400;

                    playerX = screenW / 2f;
                }
            }
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_MOVE) {

                started = true;

                if (x < screenW / 2f) {
                    leftPressed = true;
                    rightPressed = false;
                } else {
                    rightPressed = true;
                    leftPressed = false;
                }

                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {

                leftPressed = false;
                rightPressed = false;

                return true;
            }

            return true;
        }
    }
                }
