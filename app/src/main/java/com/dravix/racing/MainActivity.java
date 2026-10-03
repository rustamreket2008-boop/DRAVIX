package com.dravix.racing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.Build;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.view.WindowManager;
import android.media.AudioManager;
import android.media.ToneGenerator;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    private DRAVIXGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        game = new DRAVIXGame(this);
        setContentView(game);
    }

    @Override
    public void onBackPressed() {
        if (game != null) {
            game.handleBack();
        } else {
            super.onBackPressed();
        }
    }

    public static class DRAVIXGame extends View {

        // =========================
        // SCREENS
        // =========================

        private static final int MENU = 0;
        private static final int GARAGE = 1;
        private static final int GAME = 2;
        private static final int PAUSE = 3;
        private static final int RESULT = 4;
        private static final int SETTINGS = 5;
        private static final int MISSIONS = 6;

        private int screen = MENU;

        // =========================
        // BASIC
        // =========================

        private final Context context;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random random = new Random();

        private Vibrator vibrator;
        private ToneGenerator tone;

        // =========================
        // PLAYER
        // =========================

        private float playerX;
        private float playerY;

        private float playerWidth = 82;
        private float playerHeight = 145;

        private int selectedCar = 0;

        private int lives = 3;

        private float speed = 0;
        private float nitro = 100;

        private boolean nitroPressed = false;

        // =========================
        // SCORE
        // =========================

        private int score = 0;
        private int bestScore = 0;
        private int coins = 0;

        private float distance = 0;

        private int level = 1;
        private int xp = 0;

        private int combo = 0;
        private int maxCombo = 0;

        // =========================
        // UPGRADES
        // =========================

        private int engineLevel = 1;
        private int brakeLevel = 1;
        private int tireLevel = 1;
        private int armorLevel = 1;
        private int nitroLevel = 1;

        // =========================
        // WORLD
        // =========================

        private static final int CITY = 0;
        private static final int NIGHT = 1;
        private static final int RAIN = 2;
        private static final int DESERT = 3;
        private static final int SNOW = 4;

        private int weather = CITY;
        private int roadType = 0;

        // =========================
        // RACE
        // =========================

        private boolean raceStarted = false;
        private boolean raceFinished = false;

        private int countdown = 3;
        private long countdownStart;

        private long lastTime;

        // =========================
        // CARS
        // =========================

        private final String[] carNames = {
                "Sport Red",
                "Sport Blue",
                "Supercar",
                "Racing",
                "Black GT",
                "Street",
                "Police",
                "Green Racer",
                "Neon",
                "DRAVIX X",
                "Shadow",
                "Thunder",
                "Inferno",
                "Velocity",
                "Phantom",
                "Cyber GT",
                "Road King",
                "Lightning",
                "Storm",
                "Titan",
                "Falcon",
                "Dragon",
                "Night Rider",
                "X-Racer",
                "Ultimate"
        };

        private final int[] carColors = {
                Color.RED,
                Color.BLUE,
                Color.YELLOW,
                Color.rgb(255, 90, 0),
                Color.BLACK,
                Color.GRAY,
                Color.rgb(40, 40, 40),
                Color.GREEN,
                Color.CYAN,
                Color.MAGENTA,
                Color.DKGRAY,
                Color.rgb(70, 70, 120),
                Color.rgb(180, 40, 20),
                Color.rgb(50, 150, 255),
                Color.rgb(120, 20, 180),
                Color.rgb(20, 220, 220),
                Color.rgb(220, 220, 220),
                Color.rgb(120, 220, 255),
                Color.rgb(80, 80, 80),
                Color.rgb(170, 170, 170),
                Color.rgb(255, 200, 50),
                Color.rgb(150, 30, 30),
                Color.rgb(20, 20, 60),
                Color.rgb(100, 255, 100),
                Color.WHITE
        };

        // =========================
        // TRAFFIC CAR
        // =========================

        private static class TrafficCar {

            float x;
            float y;

            float width;
            float height;

            float speed;

            int color;
            int lane;

            boolean police;
            boolean truck;

            boolean damaged;
        }

        private final ArrayList<TrafficCar> traffic =
                new ArrayList<>();

        private int trafficCount = 16;

        // =========================
        // COIN
        // =========================

        private static class Coin {

            float x;
            float y;

            float radius;

            int value;

            boolean collected;
        }

        private final ArrayList<Coin> coinList =
                new ArrayList<>();

        // =========================
        // PARTICLE
        // =========================

        private static class Particle {

            float x;
            float y;

            float vx;
            float vy;

            float size;
            float life;

            int color;
        }

        private final ArrayList<Particle> particles =
                new ArrayList<>();

        // =========================
        // STAR
        // =========================

        private static class Star {

            float x;
            float y;

            float size;
            float speed;
        }

        private final ArrayList<Star> stars =
                new ArrayList<>();

        // =========================
        // BUILDING
        // =========================

        private static class Building {

            float x;
            float width;
            float height;

            int color;
        }

        private final ArrayList<Building> buildings =
                new ArrayList<>();

        // =========================
        // MISSION
        // =========================

        private static class Mission {

            String title;

            int target;
            int progress;
            int reward;

            boolean completed;

            Mission(
                    String title,
                    int target,
                    int reward
            ) {
                this.title = title;
                this.target = target;
                this.reward = reward;
            }
        }

        private final ArrayList<Mission> missions =
                new ArrayList<>();

        // =========================
        // BUTTONS
        // =========================

        private final RectF playButton =
                new RectF();

        private final RectF garageButton =
                new RectF();

        private final RectF missionsButton =
                new RectF();

        private final RectF settingsButton =
                new RectF();

        private final RectF leftButton =
                new RectF();

        private final RectF rightButton =
                new RectF();

        private final RectF nitroButton =
                new RectF();

        private final RectF pauseButton =
                new RectF();

        private final RectF backButton =
                new RectF();

        private final RectF upgradeButton =
                new RectF();

        private final RectF restartButton =
                new RectF();

        private final RectF menuButton =
                new RectF();

        // =========================
        // TOUCH
        // =========================

        private float touchX;
        private float touchY;

        private boolean leftPressed;
        private boolean rightPressed;

        // =========================
        // CONSTRUCTOR
        // =========================

        public DRAVIXGame(Context context) {

            super(context);

            this.context = context;

            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.NORMAL
                    )
            );

            vibrator =
                    (Vibrator)
                            context.getSystemService(
                                    Context.VIBRATOR_SERVICE
                            );

            try {

                tone = new ToneGenerator(
                        AudioManager.STREAM_MUSIC,
                        70
                );

            } catch (Exception e) {

                tone = null;
            }

            createMissions();
            createStars();
            createBuildings();

            setFocusable(true);
        }

        // =========================
        // MISSIONS
        // =========================

        private void createMissions() {

            missions.clear();

            missions.add(
                    new Mission(
                            "Drive 1000 meters",
                            1000,
                            100
                    )
            );

            missions.add(
                    new Mission(
                            "Collect 20 coins",
                            20,
                            150
                    )
            );

            missions.add(
                    new Mission(
                            "Reach level 5",
                            5,
                            250
                    )
            );

            missions.add(
                    new Mission(
                            "Score 5000 points",
                            5000,
                            300
                    )
            );

            missions.add(
                    new Mission(
                            "Use Nitro 10 times",
                            10,
                            200
                    )
            );

            missions.add(
                    new Mission(
                            "Drive 10000 meters",
                            10000,
                            500
                    )
            );

            missions.add(
                    new Mission(
                            "Collect 100 coins",
                            100,
                            600
                    )
            );
        }

        // =========================
        // STARS
        // =========================

        private void createStars() {

            stars.clear();

            for (int i = 0; i < 120; i++) {

                Star s = new Star();

                s.x = random.nextFloat();
                s.y = random.nextFloat();

                s.size =
                        1f +
                        random.nextFloat() * 3f;

                s.speed =
                        0.5f +
                        random.nextFloat() * 2f;

                stars.add(s);
            }
        }

        // =========================
        // BUILDINGS
        // =========================

        private void createBuildings() {

            buildings.clear();

            for (int i = 0; i < 50; i++) {

                Building b = new Building();

                b.x = i * 150;

                b.width =
                        80 +
                        random.nextInt(100);

                b.height =
                        120 +
                        random.nextInt(450);

                b.color =
                        Color.rgb(
                                25 + random.nextInt(45),
                                30 + random.nextInt(45),
                                45 + random.nextInt(60)
                        );

                buildings.add(b);
            }
        }

        // =========================
        // START GAME
        // =========================

        private void startGame() {

            screen = GAME;

            raceStarted = false;
            raceFinished = false;

            countdown = 3;

            countdownStart =
                    System.currentTimeMillis();

            score = 0;
            distance = 0;

            lives = 3;

            combo = 0;
            maxCombo = 0;

            speed = 0;

            nitro = 100;

            xp = 0;
            level = 1;

            weather =
                    random.nextInt(5);

            roadType =
                    random.nextInt(5);

            playerX =
                    getWidth() / 2f
                            - playerWidth / 2f;

            playerY =
                    getHeight() * 0.70f;

            traffic.clear();
            coinList.clear();
            particles.clear();

            createTraffic();
            createCoins();

            lastTime =
                    System.currentTimeMillis();

            beep(
                    ToneGenerator.TONE_PROP_BEEP
            );

            invalidate();
        }

        // =========================
        // CREATE TRAFFIC
        // =========================

        private void createTraffic() {

            traffic.clear();

            for (int i = 0;
                 i < trafficCount;
                 i++) {

                TrafficCar car =
                        new TrafficCar();

                car.width =
                        60 +
                        random.nextInt(30);

                car.height =
                        100 +
                        random.nextInt(50);

                car.lane =
                        random.nextInt(3);

                car.x =
                        laneX(
                                car.lane,
                                car.width
                        );

                car.y =
                        -200 -
                        random.nextInt(2600);

                car.speed =
                        2.5f +
                        random.nextFloat() * 5f;

                car.color =
                        carColors[
                                random.nextInt(
                                        carColors.length
                                )
                        ];

                car.police =
                        random.nextInt(15) == 0;

                car.truck =
                        random.nextInt(12) == 0;

                car.damaged = false;

                if (car.truck) {

                    car.width = 90;
                    car.height = 155;
                }

                traffic.add(car);
            }
        }

        // =========================
        // CREATE COINS
        // =========================

        private void createCoins() {

            coinList.clear();

            for (int i = 0; i < 14; i++) {

                Coin c = new Coin();

                c.radius = 14;

                int lane =
                        random.nextInt(3);

                c.x =
                        laneX(
                                lane,
                                c.radius * 2
                        ) +
                        c.radius;

                c.y =
                        -250 -
                        random.nextInt(3000);

                c.value =
                        5 +
                        random.nextInt(20);

                c.collected = false;

                coinList.add(c);
            }
        }

        // =========================
        // LANE X
        // =========================

        private float laneX(
                int lane,
                float objectWidth
        ) {

            float roadLeft =
                    getWidth() * 0.20f;

            float roadWidth =
                    getWidth() * 0.60f;

            float laneWidth =
                    roadWidth / 3f;

            return roadLeft
                    +
                    lane * laneWidth
                    +
                    laneWidth / 2f
                    -
                    objectWidth / 2f;
        }

        // =========================
        // DRAW
        // =========================

        @Override
        protected void onDraw(
                Canvas canvas
        ) {

            super.onDraw(canvas);

            if (screen == MENU) {

                drawMenu(canvas);

            } else if (screen == GARAGE) {

                drawGarage(canvas);

            } else if (screen == GAME) {

                drawGame(canvas);

            } else if (screen == PAUSE) {

                drawGame(canvas);
                drawPause(canvas);

            } else if (screen == RESULT) {

                drawResult(canvas);

            } else if (screen == SETTINGS) {

                drawSettings(canvas);

            } else if (screen == MISSIONS) {

                drawMissions(canvas);
            }

            invalidate();
        }

        // =========================
        // MENU
        // =========================

        private void drawMenu(
                Canvas canvas
        ) {

            drawMenuBackground(canvas);

            int w = getWidth();
            int h = getHeight();

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );

            paint.setColor(
                    Color.WHITE
            );

            paint.setTextSize(
                    Math.min(w, h) * 0.13f
            );

            canvas.drawText(
                    "DRAVIX",
                    w / 2f,
                    h * 0.17f,
                    paint
            );

            paint.setTextSize(
                    Math.min(w, h) * 0.035f
            );

            paint.setColor(
                    Color.rgb(
                            80,
                            210,
                            255
                    )
            );

            canvas.drawText(
                    "STREET RACING",
                    w / 2f,
                    h * 0.23f,
                    paint
            );

            drawButton(
                    canvas,
                    playButton,
                    w * 0.34f,
                    h * 0.30f,
                    w * 0.66f,
                    h * 0.41f,
                    "PLAY"
            );

            drawButton(
                    canvas,
                    garageButton,
                    w * 0.34f,
                       h * 0.73f,
                    w * 0.66f,
                    h * 0.83f,
                    "SETTINGS"
            );

            paint.setTextAlign(
                    Paint.Align.LEFT
            );

            paint.setTextSize(20);

            paint.setColor(
                    Color.YELLOW
            );

            canvas.drawText(
                    "COINS: " + coins,
                    25,
                    35,
                    paint
            );

            paint.setTextAlign(
                    Paint.Align.RIGHT
            );

            canvas.drawText(
                    "BEST: " + bestScore,
                    w - 25,
                    35,
                    paint
            );
        }

        // =========================
        // MENU BACKGROUND
        // =========================

        private void drawMenuBackground(
                Canvas canvas
        ) {

            int w = getWidth();
            int h = getHeight();

            paint.setShader(
                    new LinearGradient(
                            0,
                            0,
                            0,
                            h,
                            Color.rgb(
                                    5,
                                    8,
                                    25
                            ),
                            Color.rgb(
                                    20,
                                    50,
                                    90
                            ),
                            Shader.TileMode.CLAMP
                    )
            );

            canvas.drawRect(
                    0,
                    0,
                    w,
                    h,
                    paint
            );

            paint.setShader(null);

            paint.setColor(
                    Color.rgb(
                            20,
                            30,
                            45
                    )
            );

            canvas.drawRect(
                    0,
                    h * 0.70f,
                    w,
                    h,
                    paint
            );

            paint.setColor(
                    Color.rgb(
                            45,
                            55,
                            70
                    )
            );

            for (int i = 0; i < 12; i++) {

                float bx =
                        i * w / 11f;

                float bh =
                        80 +
                        randomBuildingHeight(i);

                canvas.drawRect(
                        bx,
                        h * 0.70f - bh,
                        bx + 70,
                        h * 0.70f,
                        paint
                );
            }

            paint.setColor(
                    Color.WHITE
            );

            for (Star s : stars) {

                canvas.drawCircle(
                        s.x * w,
                        s.y * h * 0.65f,
                        s.size,
                        paint
                );
            }
        }

        private float randomBuildingHeight(
                int i
        ) {

            return 40 +
                    (i * 37) % 150;
        }

        // =========================
        // BUTTON
        // =========================

        private void drawButton(
                Canvas canvas,
                RectF rect,
                float left,
                float top,
                float right,
                float bottom,
                String text
        ) {

            rect.set(
                    left,
                    top,
                    right,
                    bottom
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.argb(
                            225,
                            15,
                            25,
                            40
                    )
            );

            canvas.drawRoundRect(
                    rect,
                    22,
                    22,
                    paint
            );

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(3);

            paint.setColor(
                    Color.rgb(
                            50,
                            190,
                            255
                    )
            );

            canvas.drawRoundRect(
                    rect,
                    22,
                    22,
                    paint
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );

            paint.setTextSize(
                    (bottom - top) * 0.34f
            );

            paint.setColor(
                    Color.WHITE
            );

            float cy =
                    (top + bottom) / 2f
                            -
                            (
                                    paint.ascent()
                                            +
                                    paint.descent()
                            ) / 2f;

            canvas.drawText(
                    text,
                    (left + right) / 2f,
                    cy,
                    paint
            );
        }

        // =========================
        // SOUND
        // =========================
                // =========================
        // GARAGE SCREEN
        // =========================

        private void drawGarage(Canvas canvas) {

            int w = getWidth();
            int h = getHeight();

            paint.setStyle(Paint.Style.FILL);

            paint.setColor(Color.rgb(8, 12, 22));

            canvas.drawRect(
                    0, 0, w, h, paint
            );

            // Header
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );
            paint.setTextSize(42);

            canvas.drawText(
                    "GARAGE",
                    w / 2f,
                    55,
                    paint
            );

            // Coins
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(20);
            paint.setColor(Color.YELLOW);

            canvas.drawText(
                    "COINS: " + coins,
                    25,
                    40,
                    paint
            );

            // Car preview
            float cx = w / 2f;
            float cy = h * 0.35f;

            drawCar(
                    canvas,
                    cx - 45,
                    cy - 80,
                    90,
                    160,
                    carColors[selectedCar],
                    false
            );

            // Car name
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(28);
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    carNames[selectedCar],
                    cx,
                    h * 0.58f,
                    paint
            );

            // Car number
            paint.setTextSize(18);
            paint.setColor(Color.LTGRAY);

            canvas.drawText(
                    "CAR " +
                    (selectedCar + 1) +
                    " / " +
                    carNames.length,
                    cx,
                    h * 0.63f,
                    paint
            );

            // Stats
            drawStatBar(
                    canvas,
                    "ENGINE",
                    engineLevel,
                    10,
                    w * 0.08f,
                    h * 0.69f,
                    w * 0.38f,
                    h * 0.73f
            );

            drawStatBar(
                    canvas,
                    "BRAKES",
                    brakeLevel,
                    10,
                    w * 0.08f,
                    h * 0.76f,
                    w * 0.38f,
                    h * 0.80f
            );

            drawStatBar(
                    canvas,
                    "TIRES",
                    tireLevel,
                    10,
                    w * 0.62f,
                    h * 0.69f,
                    w * 0.92f,
                    h * 0.73f
            );

            drawStatBar(
                    canvas,
                    "ARMOR",
                    armorLevel,
                    10,
                    w * 0.62f,
                    h * 0.76f,
                    w * 0.92f,
                    h * 0.80f
            );

            // Upgrade button
            drawButton(
                    canvas,
                    upgradeButton,
                    w * 0.38f,
                    h * 0.83f,
                    w * 0.62f,
                    h * 0.91f,
                    "UPGRADE"
            );

            // Back
            drawButton(
                    canvas,
                    backButton,
                    20,
                    h * 0.83f,
                    w * 0.25f,
                    h * 0.91f,
                    "BACK"
            );

            // Next car
            paint.setColor(Color.WHITE);
            paint.setTextSize(42);

            canvas.drawText(
                    "<",
                    w * 0.12f,
                    h * 0.48f,
                    paint
            );

            canvas.drawText(
                    ">",
                    w * 0.88f,
                    h * 0.48f,
                    paint
            );
        }

        // =========================
        // STAT BAR
        // =========================

        private void drawStatBar(
                Canvas canvas,
                String name,
                int value,
                int max,
                float left,
                float top,
                float right,
                float bottom
        ) {

            paint.setTextAlign(
                    Paint.Align.LEFT
            );

            paint.setTextSize(16);
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    name,
                    left,
                    top - 7,
                    paint
            );

            paint.setColor(
                    Color.rgb(
                            35,
                            45,
                            60
                    )
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    bottom,
                    8,
                    8,
                    paint
            );

            float width =
                    (right - left)
                            * value
                            / (float) max;

            paint.setColor(
                    Color.rgb(
                            40,
                            200,
                            255
                    )
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    left + width,
                    bottom,
                    8,
                    8,
                    paint
            );

            paint.setTextAlign(
                    Paint.Align.RIGHT
            );

            paint.setColor(Color.WHITE);

            canvas.drawText(
                    value + "/" + max,
                    right,
                    top - 7,
                    paint
            );
        }

        // =========================
        // GAME SCREEN
        // =========================

        private void drawGame(Canvas canvas) {

            updateGame();

            drawWorld(canvas);

            drawRoad(canvas);

            drawTraffic(canvas);

            drawCoins(canvas);

            drawPlayer(canvas);

            drawParticles(canvas);

            drawRain(canvas);

            drawHUD(canvas);

            drawControls(canvas);

            if (!raceStarted) {

                drawCountdown(canvas);
            }
        }

        // =========================
        // WORLD
        // =========================

        private void drawWorld(Canvas canvas) {

            int w = getWidth();
            int h = getHeight();

            int topColor;
            int bottomColor;

            if (weather == NIGHT) {

                topColor =
                        Color.rgb(
                                3,
                                7,
                                25
                        );

                bottomColor =
                        Color.rgb(
                                20,
                                18,
                                50
                        );

            } else if (weather == DESERT) {

                topColor =
                        Color.rgb(
                                230,
                                175,
                                95
                        );

                bottomColor =
                        Color.rgb(
                                190,
                                130,
                                60
                        );

            } else if (weather == SNOW) {

                topColor =
                        Color.rgb(
                                150,
                                190,
                                220
                        );

                bottomColor =
                        Color.rgb(
                                210,
                                225,
                                235
                        );

            } else {

                topColor =
                        Color.rgb(
                                50,
                                140,
                                190
                        );

                bottomColor =
                        Color.rgb(
                                100,
                                170,
                                130
                        );
            }

            paint.setShader(
                    new LinearGradient(
                            0,
                            0,
                            0,
                            h,
                            topColor,
                            bottomColor,
                            Shader.TileMode.CLAMP
                    )
            );

            canvas.drawRect(
                    0,
                    0,
                    w,
                    h,
                    paint
            );

            paint.setShader(null);

            // Horizon
            paint.setColor(
                    Color.argb(
                            100,
                            255,
                            255,
                            255
                    )
            );

            canvas.drawRect(
                    0,
                    h * 0.30f,
                    w,
                    h * 0.31f,
                    paint
            );

            if (weather == NIGHT) {

                drawNightSky(canvas);

            } else if (weather == CITY ||
                       weather == RAIN) {

                drawCity(canvas);
            }

            if (weather == DESERT) {

                drawDesertScene(canvas);
            }

            if (weather == SNOW) {

                drawSnowScene(canvas);
            }
        }

        // =========================
        // NIGHT SKY
        // =========================

        private void drawNightSky(Canvas canvas) {

            int w = getWidth();
            int h = getHeight();

            paint.setColor(Color.WHITE);

            for (Star star : stars) {

                float x =
                        star.x * w;

                float y =
                        star.y * h * 0.45f;

                canvas.drawCircle(
                        x,
                        y,
                        star.size,
                        paint
                );
            }

            // Moon
            paint.setColor(
                    Color.rgb(
                            245,
                            245,
                            210
                    )
            );

            canvas.drawCircle(
                    w * 0.82f,
                    h * 0.13f,
                    35,
                    paint
            );

            paint.setColor(
                    Color.rgb(
                            3,
                            7,
                            25
                    )
            );

            canvas.drawCircle(
                    w * 0.84f,
                    h * 0.115f,
                    30,
                    paint
            );
        }

        // =========================
        // CITY
        // =========================

        private void drawCity(Canvas canvas) {

            int w = getWidth();
            int h = getHeight();

            paint.setStyle(
                    Paint.Style.FILL
            );

            for (int i = 0;
                 i < buildings.size();
                 i++) {

                Building b =
                        buildings.get(i);

                float x =
                        b.x
                        -
                        (distance * 0.25f)
                        %
                        (w * 2);

                while (x < -200) {
                    x += w * 2;
                }

                while (x > w + 200) {
                    x -= w * 2;
                }

                float bottom =
                        h * 0.48f;

                float top =
                        bottom
                        -
                        b.height * 0.45f;

                paint.setColor(
                        weather == NIGHT
                                ? Color.rgb(
                                        25,
                                        30,
                                        55
                                )
                                : b.color
                );

                canvas.drawRect(
                        x,
                        top,
                        x + b.width,
                        bottom,
                        paint
                );

                // Windows
                if (weather == NIGHT) {

                    paint.setColor(
                            Color.rgb(
                                    255,
                                    210,
                                    80
                            )
                    );

                    for (
                            float wy = top + 15;
                            wy < bottom - 10;
                            wy += 30
                    ) {

                        for (
                                float wx = x + 12;
                                wx < x + b.width - 10;
                                wx += 25
                        ) {

                            if (
                                    random.nextInt(4)
                                            != 0
                            ) {

                                canvas.drawRect(
                                        wx,
                                        wy,
                                        wx + 8,
                                        wy + 12,
                                        paint
                                );
                            }
                        }
                    }
                }
            }

            // Trees
            for (int i = 0; i < 12; i++) {

                float x =
                        i * 170
                        -
                        (distance * 0.35f)
                        %
                        2000;

                while (x < -100) {
                    x += 2000;
                }

                drawTree(
                        canvas,
                        x,
                        h * 0.47f,
                        45
                );
            }
        }

        // =========================
        // TREE
        // =========================

        private void drawTree(
                Canvas canvas,
                float x,
                float y,
                float size
        ) {

            paint.setColor(
                    Color.rgb(
                            90,
                            55,
                            30
                    )
            );

            canvas.drawRect(
                    x - 7,
                    y,
                    x + 7,
                    y + size,
                    paint
            );

            paint.setColor(
                    Color.rgb(
                            30,
                            120,
                            55
                    )
            );

            canvas.drawCircle(
                    x,
                    y - 15,
                    size * 0.55f,
                    paint
            );

            canvas.drawCircle(
                    x - 20,
                    y + 5,
                    size * 0.40f,
                    paint
            );

            canvas.drawCircle(
                    x + 20,
                    y + 5,
                    size * 0.40f,
                    paint
            );
        }

        // =========================
        // DESERT SCENE
        // =========================

        private void drawDesertScene(
                Canvas canvas
        ) {

            int w = getWidth();
            int h = getHeight();

            paint.setColor(
                    Color.rgb(
                            220,
                            175,
                            95
                    )
            );

            canvas.drawRect(
                    0,
                    h * 0.40f,
                    w,
                    h,
                    paint
            );

            for (int i = 0; i < 8; i++) {

                float x =
                        i * 170
                        -
                        (distance * 0.2f)
                        %
                        1200;

                drawCactus(
                        canvas,
                        x,
                        h * 0.42f
                );
            }

            // Mountains
            Path mountain =
                    new Path();

            mountain.moveTo(
                    0,
                    h * 0.40f
            );

            mountain.lineTo(
                    w * 0.18f,
                    h * 0.25f
            );

            mountain.lineTo(
                    w * 0.34f,
                    h * 0.40f
            );

            mountain.lineTo(
                    w * 0.52f,
                    h * 0.22f
            );

            mountain.lineTo(
                    w * 0.70f,
                    h * 0.40f
            );

            mountain.lineTo(
                    w * 0.86f,
                    h * 0.28f
            );

            mountain.lineTo(
                    w,
                    h * 0.40f
            );

            mountain.close();

            paint.setColor(
                    Color.rgb(
                            150,
                            100,
                            60
                    )
            );

            canvas.drawPath(
                    mountain,
                    paint
            );
        }

        // =========================
        // CACTUS
        // =========================

        private void drawCactus(
                Canvas canvas,
                float x,
                float y
        ) {

            paint.setColor(
                    Color.rgb(
                            35,
                            110,
                            50
                    )
            );

            canvas.drawRoundRect(
                    x - 8,
                    y,
                    x + 8,
                    y + 65,
                    8,
                    8,
                    paint
            );

            canvas.drawRoundRect(
                    x - 28,
                    y + 20,
                    x - 8,
                    y + 32,
                    6,
                    6,
                    paint
            );

            canvas.drawRoundRect(
                    x + 8,
                    y + 35,
                    x + 28,
                    y + 47,
                    6,
                    6,
                    paint
            );
        }

        // =========================
        // SNOW SCENE
        // =========================

        private void drawSnowScene(
                Canvas canvas
        ) {

            int w = getWidth();
            int h = getHeight();

            paint.setColor(
                    Color.rgb(
                            235,
                            245,
                            250
                    )
   );

            canvas.drawRect(
                    0,
                    h * 0.40f,
                    w,
                    h,
                    paint
            );

            paint.setColor(
                    Color.rgb(
                            120,
                            145,
                            165
                    )
            );

            for (int i = 0; i < 7; i++) {

                float x =
                        i * 190
                        -
                        (distance * 0.15f)
                        % 1300;

                canvas.drawCircle(
                        x,
                        h * 0.42f,
                        65,
                        paint
                );
            }

            for (int i = 0; i < 40; i++) {

                float x =
                        random.nextFloat()
                                * w;

                float y =
                        random.nextFloat()
                                * h * 0.7f;

                paint.setColor(
                        Color.WHITE
                );

                canvas.drawCircle(
                        x,
                        y,
                        2 + random.nextFloat() * 3,
                        paint
                );
            }
        }

        // =========================
        // ROAD
        // =========================

        private void drawRoad(Canvas canvas) {

            int w = getWidth();
            int h = getHeight();

            float left =
                    w * 0.20f;

            float right =
                    w * 0.80f;

            // Grass / side
            paint.setColor(
                    weather == DESERT
                            ? Color.rgb(
                                    190,
                                    130,
                                    55
                            )
                            : Color.rgb(
                                    30,
                                    100,
                                    50
                            )
            );

            canvas.drawRect(
                    0,
                    h * 0.30f,
                    left,
                    h,
                    paint
            );

            canvas.drawRect(
                    right,
                    h * 0.30f,
                    w,
                    h,
                    paint
            );

            // Road
            paint.setColor(
                    Color.rgb(
                            45,
                            45,
                            48
                    )
            );

            canvas.drawRect(
                    left,
                    h * 0.30f,
                    right,
                    h,
                    paint
            );

            // Edge lines
            paint.setColor(
                    Color.WHITE
            );

            paint.setStrokeWidth(5);

            canvas.drawLine(
                    left,
                    h * 0.30f,
                    left,
                    h,
                    paint
            );

            canvas.drawLine(
                    right,
                    h * 0.30f,
                    right,
                    h,
                    paint
            );

            // Lane lines
            float laneWidth =
                    (right - left) / 3f;

            float offset =
                    (distance * 2.5f)
                            % 100;

            paint.setStrokeWidth(4);

            for (int lane = 1;
                 lane < 3;
                 lane++) {

                float x =
                        left
                        +
                        lane * laneWidth;

                for (
                        float y =
                                h * 0.30f
                                        -
                                100
                                        +
                                offset;

                        y < h;

                        y += 100
                ) {

                    canvas.drawLine(
                            x,
                            y,
                            x,
                            y + 45,
                            paint
                    );
                }
            }

            // Road shoulder
            paint.setColor(
                    Color.rgb(
                            255,
                            210,
                            30
                    )
            );

            paint.setStrokeWidth(6);

            canvas.drawLine(
                    left + 8,
                    h * 0.30f,
                    left + 8,
                    h,
                    paint
            );

            canvas.drawLine(
                    right - 8,
                    h * 0.30f,
                    right - 8,
                    h,
                    paint
            );
                        }
                // =========================
        // TRAFFIC DRAWING
        // =========================

        private void drawTraffic(Canvas canvas) {

            for (TrafficCar car : traffic) {

                drawTrafficCar(
                        canvas,
                        car
                );
            }
        }

        // =========================
        // TRAFFIC CAR
        // =========================

        private void drawTrafficCar(
                Canvas canvas,
                TrafficCar car
        ) {

            paint.setStyle(
                    Paint.Style.FILL
            );

            // Shadow
            paint.setColor(
                    Color.argb(
                            90,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawOval(
                    car.x - 8,
                    car.y + car.height - 8,
                    car.x + car.width + 8,
                    car.y + car.height + 10,
                    paint
            );

            // Body
            paint.setColor(
                    car.damaged
                            ? Color.DKGRAY
                            : car.color
            );

            canvas.drawRoundRect(
                    car.x,
                    car.y,
                    car.x + car.width,
                    car.y + car.height,
                    14,
                    14,
                    paint
            );

            // Roof
            paint.setColor(
                    Color.rgb(
                            30,
                            35,
                            45
                    )
            );

            canvas.drawRoundRect(
                    car.x + car.width * 0.18f,
                    car.y + car.height * 0.18f,
                    car.x + car.width * 0.82f,
                    car.y + car.height * 0.58f,
                    12,
                    12,
                    paint
            );

            // Windows
            paint.setColor(
                    Color.rgb(
                            80,
                            150,
                            190
                    )
            );

            canvas.drawRoundRect(
                    car.x + car.width * 0.25f,
                    car.y + car.height * 0.23f,
                    car.x + car.width * 0.75f,
                    car.y + car.height * 0.48f,
                    7,
                    7,
                    paint
            );

            // Wheels
            paint.setColor(
                    Color.BLACK
            );

            canvas.drawRoundRect(
                    car.x - 5,
                    car.y + car.height * 0.18f,
                    car.x + 7,
                    car.y + car.height * 0.42f,
                    5,
                    5,
                    paint
            );

            canvas.drawRoundRect(
                    car.x + car.width - 7,
                    car.y + car.height * 0.18f,
                    car.x + car.width + 5,
                    car.y + car.height * 0.42f,
                    5,
                    5,
                    paint
            );

            canvas.drawRoundRect(
                    car.x - 5,
                    car.y + car.height * 0.68f,
                    car.x + 7,
                    car.y + car.height * 0.92f,
                    5,
                    5,
                    paint
            );

            canvas.drawRoundRect(
                    car.x + car.width - 7,
                    car.y + car.height * 0.68f,
                    car.x + car.width + 5,
                    car.y + car.height * 0.92f,
                    5,
                    5,
                    paint
            );

            // Front lights
            paint.setColor(
                    Color.WHITE
            );

            canvas.drawRoundRect(
                    car.x + 10,
                    car.y + 8,
                    car.x + 25,
                    car.y + 18,
                    4,
                    4,
                    paint
            );

            canvas.drawRoundRect(
                    car.x + car.width - 25,
                    car.y + 8,
                    car.x + car.width - 10,
                    car.y + 18,
                    4,
                    4,
                    paint
            );

            // Back lights
            paint.setColor(
                    Color.RED
            );

            canvas.drawRect(
                    car.x + 10,
                    car.y + car.height - 18,
                    car.x + 25,
                    car.y + car.height - 8,
                    paint
            );

            canvas.drawRect(
                    car.x + car.width - 25,
                    car.y + car.height - 18,
                    car.x + car.width - 10,
                    car.y + car.height - 8,
                    paint
            );

            // Police lights
            if (car.police) {

                paint.setColor(
                        Color.BLUE
                );

                canvas.drawRect(
                        car.x + car.width * 0.30f,
                        car.y + car.height * 0.10f,
                        car.x + car.width * 0.50f,
                        car.y + car.height * 0.15f,
                        paint
                );

                paint.setColor(
                        Color.RED
                );

                canvas.drawRect(
                        car.x + car.width * 0.50f,
                        car.y + car.height * 0.10f,
                        car.x + car.width * 0.70f,
                        car.y + car.height * 0.15f,
                        paint
                );
            }

            // Truck marking
            if (car.truck) {

                paint.setColor(
                        Color.rgb(
                                240,
                                200,
                                50
                        )
                );

                paint.setTextAlign(
                        Paint.Align.CENTER
                );

                paint.setTextSize(13);

                canvas.drawText(
                        "TRUCK",
                        car.x + car.width / 2f,
                        car.y + car.height * 0.65f,
                        paint
                );
            }
        }

        // =========================
        // COINS DRAWING
        // =========================

        private void drawCoins(Canvas canvas) {

            for (Coin coin : coinList) {

                if (coin.collected) {
                    continue;
                }

                drawCoin(
                        canvas,
                        coin
                );
            }
        }

        // =========================
        // COIN
        // =========================

        private void drawCoin(
                Canvas canvas,
                Coin coin
        ) {

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.rgb(
                            255,
                            190,
                            20
                    )
            );

            canvas.drawCircle(
                    coin.x,
                    coin.y,
                    coin.radius,
                    paint
            );

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(3);

            paint.setColor(
                    Color.rgb(
                            255,
                            235,
                            100
                    )
            );

            canvas.drawCircle(
                    coin.x,
                    coin.y,
                    coin.radius - 3,
                    paint
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            paint.setTextSize(
                    coin.radius
            );

            paint.setTypeface(
                    Typeface.DEFAULT_BOLD
            );

            paint.setColor(
                    Color.rgb(
                            120,
                            80,
                            0
                    )
            );

            canvas.drawText(
                    "$",
                    coin.x,
                    coin.y
                            -
                            (
                                    paint.ascent()
                                            +
                                    paint.descent()
                            ) / 2f,
                    paint
            );
        }

        // =========================
        // PLAYER
        // =========================

        private void drawPlayer(
                Canvas canvas
        ) {

            drawCar(
                    canvas,
                    playerX,
                    playerY,
                    playerWidth,
                    playerHeight,
                    carColors[selectedCar],
                    true
            );

            if (nitroPressed &&
                    nitro > 0) {

                drawNitroFlame(
                        canvas
                );
            }
        }

        // =========================
        // PLAYER CAR
        // =========================

        private void drawCar(
                Canvas canvas,
                float x,
                float y,
                float width,
                float height,
                int color,
                boolean player
        ) {

            // Shadow
            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.argb(
                            100,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawOval(
                    x - 8,
                    y + height - 8,
                    x + width + 8,
                    y + height + 12,
                    paint
            );

            // Main body
            paint.setColor(
                    color
            );

            canvas.drawRoundRect(
                    x,
                    y,
                    x + width,
                    y + height,
                    18,
                    18,
                    paint
            );

            // Hood
            paint.setColor(
                    Color.argb(
                            90,
                            255,
                            255,
                            255
                    )
            );

            canvas.drawRoundRect(
                    x + width * 0.16f,
                    y + height * 0.05f,
                    x + width * 0.84f,
                    y + height * 0.30f,
                    12,
                    12,
                    paint
            );

            // Roof
            paint.setColor(
                    Color.rgb(
                            25,
                            30,
                            40
                    )
            );

            canvas.drawRoundRect(
                    x + width * 0.18f,
                    y + height * 0.25f,
                    x + width * 0.82f,
                    y + height * 0.65f,
                    14,
                    14,
                    paint
            );

            // Front glass
            paint.setColor(
                    Color.rgb(
                            90,
                            170,
                            210
                    )
            );

            canvas.drawRoundRect(
                    x + width * 0.27f,
                    y + height * 0.29f,
                    x + width * 0.73f,
                    y + height * 0.47f,
                    7,
                    7,
                    paint
            );

            // Rear glass
            paint.setColor(
                    Color.rgb(
                            55,
                            115,
                            150
                    )
            );

            canvas.drawRoundRect(
                    x + width * 0.29f,
                    y + height * 0.49f,
                    x + width * 0.71f,
                    y + height * 0.61f,
                    6,
                    6,
                    paint
            );

            // Wheels
            paint.setColor(
                    Color.BLACK
            );

            canvas.drawRoundRect(
                    x - 7,
                    y + height * 0.16f,
                    x + 8,
                    y + height * 0.39f,
                    6,
                    6,
                    paint
            );

            canvas.drawRoundRect(
                    x + width - 8,
                    y + height * 0.16f,
                    x + width + 7,
                    y + height * 0.39f,
                    6,
                    6,
                    paint
            );

            canvas.drawRoundRect(
                    x - 7,
                    y + height * 0.67f,
                    x + 8,
                    y + height * 0.90f,
                    6,
                    6,
                    paint
            );

            canvas.drawRoundRect(
                    x + width - 8,
                    y + height * 0.67f,
                    x + width + 7,
                    y + height * 0.90f,
                    6,
                    6,
                    paint
            );

            // Headlights
            paint.setColor(
                    Color.WHITE
            );

            canvas.drawRoundRect(
                    x + 10,
                    y + 8,
                    x + 29,
                    y + 22,
                    6,
                    6,
                    paint
            );

            canvas.drawRoundRect(
                    x + width - 29,
                    y + 8,
                    x + width - 10,
                    y + 22,
                    6,
                    6,
                    paint
            );

            // Rear lights
            paint.setColor(
                    Color.RED
            );

            canvas.drawRoundRect(
                    x + 10,
                    y + height - 22,
                    x + 29,
                    y + height - 8,
                    5,
                    5,
                    paint
            );

            canvas.drawRoundRect(
                    x + width - 29,
                    y + height - 22,
                    x + width - 10,
                    y + height - 8,
                    5,
                    5,
                    paint
            );

            // Center stripe
            paint.setColor(
                    Color.argb(
                            150,
                            255,
                            255,
                            255
                    )
            );

            canvas.drawRect(
                    x + width * 0.45f,
                    y + 5,
                    x + width * 0.55f,
                    y + height - 5,
                    paint
            );

            // Player glow
            if (player) {

                paint.setStyle(
                        Paint.Style.STROKE
                );

                paint.setStrokeWidth(3);

                paint.setColor(
                        Color.argb(
                                150,
                                50,
                                220,
                                255
                        )
                );

                canvas.drawRoundRect(
                        x - 3,
                        y - 3,
                        x + width + 3,
                        y + height + 3,
                        20,
                        20,
                        paint
                );

                paint.setStyle(
                        Paint.Style.FILL
                );
            }
        }

        // =========================
        // NITRO FLAME
        // =========================

        private void drawNitroFlame(
                Canvas canvas
        ) {

            float center =
                    playerX
                    +
                    playerWidth / 2f;

            float bottom =
                    playerY
                    +
                    playerHeight;

            Path flame =
                    new Path();

            flame.moveTo(
                    center - 18,
                    bottom - 2
            );

            flame.lineTo(
                    center - 8,
                    bottom + 45
            );

            flame.lineTo(
                    center,
                    bottom + 25
            );

            flame.lineTo(
                    center + 8,
                    bottom + 45
            );

            flame.lineTo(
                    center + 18,
                    bottom - 2
            );

            flame.close();

            paint.setColor(
                    Color.rgb(
                            255,
                            90,
                            10
                    )
            );

            canvas.drawPath(
                    flame,
                    paint
            );

            Path inner =
                    new Path();

            inner.moveTo(
                    center - 8,
                    bottom
            );

            inner.lineTo(
                    center,
                    bottom + 28
            );

            inner.lineTo(
                    center + 8,
                    bottom
            );

            inner.close();

            paint.setColor(
                    Color.YELLOW
            );

            canvas.drawPath(
                    inner,
                    paint
            );
        }

        // =========================
        // PARTICLES
        // =========================

        private void drawParticles(
                Canvas canvas
        ) {

            paint.setStyle(
                    Paint.Style.FILL
            );

            for (Particle p : particles) {

                if (p.life <= 0) {
                    continue;
                }

                paint.setColor(
                        p.color
                );

                canvas.drawCircle(
                        p.x,
                        p.y,
                        p.size,
                        paint
                );
            }
        }

        // =========================
        // RAIN
        // =========================

        private void drawRain(
                Canvas canvas
        ) {

            if (weather != RAIN) {
                return;
            }

            paint.setColor(
                    Color.argb(
                            130,
                            180,
                            220,
                            255
                    )
            );

            paint.setStrokeWidth(2);

            int w = getWidth();
            int h = getHeight();

            for (int i = 0; i < 100; i++) {

                float x =
                        (i * 97
                                +
                                distance * 4)
                                % w;

                float y =
                        (i * 53
                                +
                                distance * 7)
               % h;

                canvas.drawLine(
                        x,
                        y,
                        x - 8,
                        y + 25,
                        paint
                );
            }
        }

        // =========================
        // UPDATE GAME
        // =========================

        private void updateGame() {

            long now =
                    System.currentTimeMillis();

            float dt =
                    (now - lastTime)
                            / 16.0f;

            if (dt < 0.5f) {
                dt = 0.5f;
            }

            if (dt > 3f) {
                dt = 3f;
            }

            lastTime = now;

            if (!raceStarted) {

                updateCountdown();

                return;
            }

            if (raceFinished) {
                return;
            }

            updatePlayer(dt);

            updateTraffic(dt);

            updateCoins(dt);

            updateParticles(dt);

            checkTrafficCollisions();

            checkCoinCollisions();

            updateLevel();

            updateMissions();

            distance +=
                    speed * dt * 0.55f;

            score +=
                    (int)
                            (speed * dt);

            if (score > bestScore) {
                bestScore = score;
            }

            if (distance >= 10000) {

                finishRace();
            }
        }

        // =========================
        // COUNTDOWN
        // =========================

        private void updateCountdown() {

            long elapsed =
                    System.currentTimeMillis()
                            -
                    countdownStart;

            int current =
                    3 -
                    (int)
                            (elapsed / 1000);

            if (current > 0) {

                if (current != countdown) {

                    countdown = current;

                    beep(
                            ToneGenerator
                                    .TONE_PROP_BEEP
                    );
                }

            } else {

                countdown = 0;

                raceStarted = true;

                beep(
                        ToneGenerator
                                .TONE_PROP_ACK
                );
            }
        }

        // =========================
        // COUNTDOWN DRAW
        // =========================

        private void drawCountdown(
                Canvas canvas
        ) {

            int w = getWidth();
            int h = getHeight();

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );

            if (countdown > 0) {

                paint.setTextSize(
                        Math.min(w, h) * 0.20f
                );

                paint.setColor(
                        Color.WHITE
                );

                canvas.drawText(
                        String.valueOf(
                                countdown
                        ),
                        w / 2f,
                        h / 2f,
                        paint
                );

            } else {

                paint.setTextSize(
                        Math.min(w, h) * 0.12f
                );

                paint.setColor(
                        Color.GREEN
                );

                canvas.drawText(
                        "GO!",
                        w / 2f,
                        h / 2f,
                        paint
                );
            }
        }
                // =========================
        // PLAYER MOVEMENT
        // =========================

        private void updatePlayer(float dt) {

            float moveSpeed =
                    14f
                    +
                    engineLevel * 1.2f;

            if (leftPressed) {

                playerX -=
                        moveSpeed * dt;
            }

            if (rightPressed) {

                playerX +=
                        moveSpeed * dt;
            }

            // Nitro
            if (nitroPressed && nitro > 0) {

                speed =
                        Math.min(
                                28f + nitroLevel * 2f,
                                speed + 0.35f
                        );

                nitro -=
                        0.65f * dt;

                if (nitro < 0) {
                    nitro = 0;
                }

                if (Math.random() < 0.45) {

                    addNitroParticle();
                }

            } else {

                float normalSpeed =
                        12f
                        +
                        engineLevel * 1.5f;

                if (speed < normalSpeed) {

                    speed +=
                            0.12f * dt;
                }

                if (speed > normalSpeed) {

                    speed -=
                            0.18f * dt;
                }

                nitro +=
                        0.10f * dt;

                float maxNitro =
                        100f;

                if (nitro > maxNitro) {
                    nitro = maxNitro;
                }
            }

            // Keep car inside road
            float roadLeft =
                    getWidth() * 0.18f;

            float roadRight =
                    getWidth() * 0.82f
                    -
                    playerWidth;

            if (playerX < roadLeft) {

                playerX = roadLeft;
            }

            if (playerX > roadRight) {

                playerX = roadRight;
            }
        }

        // =========================
        // TRAFFIC UPDATE
        // =========================

        private void updateTraffic(float dt) {

            for (TrafficCar car : traffic) {

                float trafficSpeed =
                        speed * 0.55f
                        +
                        car.speed;

                car.y +=
                        trafficSpeed * dt;

                // Small lane movement
                if (!car.police &&
                        !car.truck) {

                    car.x +=
                            Math.sin(
                                    (
                                            car.y
                                            +
                                            car.lane * 100
                                    ) / 180.0
                            ) * 0.12f * dt;
                }

                // Traffic passed screen
                if (car.y >
                        getHeight() + 220) {

                    resetTrafficCar(
                            car
                    );

                    score += 10;

                    combo++;

                    if (combo >
                            maxCombo) {

                        maxCombo =
                                combo;
                    }

                    createPassParticles(
                            car.x
                            +
                            car.width / 2f,
                            getHeight()
                            - 100
                    );
                }
            }
        }

        // =========================
        // RESET TRAFFIC
        // =========================

        private void resetTrafficCar(
                TrafficCar car
        ) {

            int lane =
                    random.nextInt(4);

            car.lane =
                    lane;

            car.width =
                    car.truck
                            ? 105
                            : 78;

            car.height =
                    car.truck
                            ? 165
                            : 135;

            car.x =
                    laneX(
                            lane,
                            car.width
                    );

            car.y =
                    -180
                    -
                    random.nextInt(500);

            car.speed =
                    2f
                    +
                    random.nextFloat()
                    * 4f;

            car.damaged =
                    false;
        }

        // =========================
        // COIN UPDATE
        // =========================

        private void updateCoins(
                float dt
        ) {

            for (Coin coin : coinList) {

                if (coin.collected) {
                    continue;
                }

                coin.y +=
                        speed * 0.65f * dt;

                if (coin.y >
                        getHeight() + 80) {

                    resetCoin(
                            coin
                    );
                }
            }
        }

        // =========================
        // RESET COIN
        // =========================

        private void resetCoin(
                Coin coin
        ) {

            int lane =
                    random.nextInt(4);

            coin.x =
                    laneX(
                            lane,
                            0
                    )
                    +
                    40;

            coin.y =
                    -100
                    -
                    random.nextInt(500);

            coin.radius =
                    18;

            coin.value =
                    10;

            coin.collected =
                    false;
        }

        // =========================
        // PARTICLE UPDATE
        // =========================

        private void updateParticles(
                float dt
        ) {

            for (int i =
                    particles.size() - 1;
                    i >= 0;
                    i--) {

                Particle p =
                        particles.get(i);

                p.x +=
                        p.vx * dt;

                p.y +=
                        p.vy * dt;

                p.vy +=
                        0.25f * dt;

                p.life -=
                        0.035f * dt;

                p.size *=
                        0.992f;

                if (p.life <= 0 ||
                        p.size < 0.5f) {

                    particles.remove(i);
                }
            }
        }

        // =========================
        // NITRO PARTICLE
        // =========================

        private void addNitroParticle() {

            Particle p =
                    new Particle();

            p.x =
                    playerX
                    +
                    playerWidth / 2f
                    +
                    (
                            random.nextFloat()
                            - 0.5f
                    ) * 25;

            p.y =
                    playerY
                    +
                    playerHeight;

            p.vx =
                    (
                            random.nextFloat()
                            - 0.5f
                    ) * 2;

            p.vy =
                    3f
                    +
                    random.nextFloat()
                    * 4f;

            p.size =
                    5f
                    +
                    random.nextFloat()
                    * 7f;

            p.life =
                    1f;

            p.color =
                    random.nextBoolean()
                            ? Color.YELLOW
                            : Color.rgb(
                                    255,
                                    80,
                                    10
                            );

            particles.add(p);
        }

        // =========================
        // PASS PARTICLES
        // =========================

        private void createPassParticles(
                float x,
                float y
        ) {

            for (int i = 0;
                    i < 8;
                    i++) {

                Particle p =
                        new Particle();

                p.x = x;
                p.y = y;

                p.vx =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 5f;

                p.vy =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 5f;

                p.size =
                        3f
                        +
                        random.nextFloat()
                        * 5f;

                p.life =
                        1f;

                p.color =
                        Color.WHITE;

                particles.add(p);
            }
        }

        // =========================
        // CRASH PARTICLES
        // =========================

        private void createCrashParticles(
                float x,
                float y
        ) {

            for (int i = 0;
                    i < 28;
                    i++) {

                Particle p =
                        new Particle();

                p.x = x;
                p.y = y;

                p.vx =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 12f;

                p.vy =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 12f;

                p.size =
                        3f
                        +
                        random.nextFloat()
                        * 8f;

                p.life =
                        1.2f;

                int type =
                        random.nextInt(3);

                if (type == 0) {

                    p.color =
                            Color.YELLOW;

                } else if (type == 1) {

                    p.color =
                            Color.rgb(
                                    255,
                                    100,
                                    20
                            );

                } else {

                    p.color =
                            Color.LTGRAY;
                }

                particles.add(p);
            }
        }

        // =========================
        // TRAFFIC COLLISION
        // =========================

        private void checkTrafficCollisions() {

            RectF playerRect =
                    new RectF(
                            playerX + 8,
                            playerY + 10,
                            playerX
                                    +
                                    playerWidth
                                    - 8,
                            playerY
                                    +
                                    playerHeight
                                    - 10
                    );

            for (TrafficCar car :
                    traffic) {

                if (car.damaged) {
                    continue;
                }

                RectF trafficRect =
                        new RectF(
                                car.x + 7,
                                car.y + 8,
                                car.x
                                        +
                                        car.width
                                        - 7,
                                car.y
                                        +
                                        car.height
                                        - 8
                        );

                if (RectF.intersects(
                        playerRect,
                        trafficRect
                )) {

                    crash(
                            car
                    );

                    break;
                }
            }
        }

        // =========================
        // CRASH
        // =========================

        private void crash(
                TrafficCar car
        ) {

            car.damaged =
                    true;

            lives--;

            combo = 0;

            score =
                    Math.max(
                            0,
                            score - 100
                    );

            createCrashParticles(
                    playerX
                            +
                            playerWidth / 2f,
                    playerY
                            +
                            playerHeight / 2f
            );

            vibrate(
                    180
            );

            beep(
                    ToneGenerator
                            .TONE_PROP_NACK
            );

            // Armor reduces crash penalty
            if (armorLevel >= 3) {

                lives++;

                armorLevel = 2;
            }

            if (lives <= 0) {

                raceFinished =
                        true;

                screen =
                        RESULT;

                return;
            }

            // Move traffic away
            car.y =
                    -300;

            // Reset player
            playerX =
                    getWidth() / 2f
                    -
                    playerWidth / 2f;

            speed =
                    Math.max(
                            7f,
                            speed - 3f
                    );
        }

        // =========================
        // COIN COLLISION
        // =========================

        private void checkCoinCollisions() {

            RectF playerRect =
                    new RectF(
                            playerX,
                            playerY,
                            playerX
                                    +
                                    playerWidth,
                            playerY
                                    +
                                    playerHeight
                    );

            for (Coin coin :
                    coinList) {

                if (coin.collected) {
                    continue;
                }

                RectF coinRect =
                        new RectF(
                                coin.x
                                        -
                                        coin.radius,
                                coin.y
                                        -
                                        coin.radius,
                                coin.x
                                        +
                                        coin.radius,
                                coin.y
                                        +
                                        coin.radius
                        );

                if (RectF.intersects(
                        playerRect,
                        coinRect
                )) {

                    collectCoin(
                            coin
                    );
                }
            }
        }

        // =========================
        // COLLECT COIN
        // =========================

        private void collectCoin(
                Coin coin
        ) {

            coin.collected =
                    true;

            coins +=
                    coin.value;

            score +=
                    coin.value * 5;

            xp +=
                    5;

            combo++;

            if (combo >
                    maxCombo) {

                maxCombo =
                        combo;
            }

            createCoinParticles(
                    coin.x,
                    coin.y
            );

            beep(
                    ToneGenerator
                            .TONE_PROP_BEEP2
            );
        }

        // =========================
        // COIN PARTICLES
        // =========================

        private void createCoinParticles(
                float x,
                float y
        ) {

            for (int i = 0;
                    i < 12;
                    i++) {

                Particle p =
                        new Particle();

                p.x = x;
                p.y = y;

                p.vx =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 7f;

                p.vy =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 7f;

                p.size =
                        3f
                        +
                        random.nextFloat()
                        * 4f;

                p.life =
                        1f;

                p.color =
                        Color.rgb(
                                255,
                                210,
                                30
                        );

                particles.add(p);
            }
        }

        // =========================
        // LEVEL SYSTEM
        // =========================

        private void updateLevel() {

            int needed =
                    level * 100;

            while (xp >= needed) {

                xp -=
                        needed;

                level++;

                coins +=
                        50;

                nitro =
                        100;

                createLevelParticles();

                beep(
                        ToneGenerator
                                .TONE_PROP_ACK
                );

                needed =
                        level * 100;
            }
        }

        // =========================
        // LEVEL PARTICLES
        // =========================

        private void createLevelParticles() {

            float cx =
                    getWidth() / 2f;

            float cy =
                    getHeight() / 2f;

            for (int i = 0;
                    i < 35;
                    i++) {

                Particle p =
                        new Particle();

                p.x = cx;
                p.y = cy;

                p.vx =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 15f;

                p.vy =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 15f;

                p.size =
                        4f
                        +
                        random.nextFloat()
                        * 6f;

                p.life =
                        1.5f;

                p.color =
                        random.nextBoolean()
                                ? Color.YELLOW
                                : Color.WHITE;

                particles.add(p);
            }
        }

        // =========================
        // MISSIONS UPDATE
        // =========================

        private void updateMissions() {

            for (Mission mission :
                    missions) {

                if (mission.completed) {
                    continue;
                }

                String title =
                        mission.title
                                .toLowerCase();

                if (title.contains(
                        "1000"
      )) {

                    mission.progress =
                            Math.min(
                                    mission.target,
                                    (int) distance
                            );

                } else if (
                        title.contains(
                                "20"
                        )
                        &&
                        title.contains(
                                "coin"
                        )
                ) {

                    mission.progress =
                            Math.min(
                                    mission.target,
                                    coins
                            );

                } else if (
                        title.contains(
                                "level 5"
                        )
                ) {

                    mission.progress =
                            Math.min(
                                    mission.target,
                                    level
                            );

                } else if (
                        title.contains(
                                "5000"
                        )
                ) {

                    mission.progress =
                            Math.min(
                                    mission.target,
                                    score
                            );

                } else if (
                        title.contains(
                                "10000"
                        )
                ) {

                    mission.progress =
                            Math.min(
                                    mission.target,
                                    (int) distance
                            );
                }

                if (mission.progress >=
                        mission.target) {

                    mission.completed =
                            true;

                    coins +=
                            mission.reward;

                    xp +=
                            25;

                    beep(
                            ToneGenerator
                                    .TONE_PROP_ACK
                    );
                }
            }
        }

        // =========================
        // FINISH RACE
        // =========================

        private void finishRace() {

            if (raceFinished) {
                return;
            }

            raceFinished =
                    true;

            raceStarted =
                    false;

            speed = 0;

            int reward =
                    100
                    +
                    level * 25
                    +
                    combo * 5;

            coins +=
                    reward;

            xp +=
                    50;

            createFinishParticles();

            beep(
                    ToneGenerator
                            .TONE_PROP_ACK
            );

            vibrate(
                    120
            );

            screen =
                    RESULT;
        }

        // =========================
        // FINISH PARTICLES
        // =========================

        private void createFinishParticles() {

            float cx =
                    getWidth() / 2f;

            float cy =
                    getHeight() / 2f;

            for (int i = 0;
                    i < 60;
                    i++) {

                Particle p =
                        new Particle();

                p.x = cx;
                p.y = cy;

                p.vx =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 18f;

                p.vy =
                        (
                                random.nextFloat()
                                - 0.5f
                        ) * 18f;

                p.size =
                        3f
                        +
                        random.nextFloat()
                        * 7f;

                p.life =
                        2f;

                p.color =
                        random.nextBoolean()
                                ? Color.YELLOW
                                : Color.WHITE;

                particles.add(p);
            }
        }

        // =========================
        // FORCE REDRAW
        // =========================

        private void gameRefresh() {

            if (Build.VERSION.SDK_INT >= 16) {

                postInvalidateOnAnimation();

            } else {

                invalidate();
            }
                     // =========================
        // GAME HUD
        // =========================

        private void drawHUD(Canvas canvas) {

            int w = getWidth();

            // Top dark panel
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(
                    Color.argb(
                            175,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawRoundRect(
                    15,
                    15,
                    w - 15,
                    105,
                    20,
                    20,
                    paint
            );

            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );

            paint.setTextAlign(
                    Paint.Align.LEFT
            );

            // SCORE
            paint.setTextSize(18);
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    "SCORE",
                    30,
                    42,
                    paint
            );

            paint.setTextSize(25);
            paint.setColor(
                    Color.rgb(
                            255,
                            220,
                            60
                    )
            );

            canvas.drawText(
                    String.valueOf(score),
                    30,
                    73,
                    paint
            );

            // COINS
            paint.setTextSize(18);
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    "COINS",
                    165,
                    42,
                    paint
            );

            paint.setTextSize(25);
            paint.setColor(
                    Color.rgb(
                            255,
                            200,
                            30
                    )
            );

            canvas.drawText(
                    String.valueOf(coins),
                    165,
                    73,
                    paint
            );

            // LEVEL
            paint.setTextSize(18);
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    "LVL",
                    285,
                    42,
                    paint
            );

            paint.setTextSize(25);
            paint.setColor(
                    Color.rgb(
                            80,
                            220,
                            255
                    )
            );

            canvas.drawText(
                    String.valueOf(level),
                    285,
                    73,
                    paint
            );

            // LIVES
            paint.setTextSize(18);
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    "LIVES",
                    365,
                    42,
                    paint
            );

            paint.setTextSize(24);
            paint.setColor(
                    Color.RED
            );

            String hearts = "";

            for (int i = 0;
                    i < lives;
                    i++) {

                hearts += "♥";
            }

            canvas.drawText(
                    hearts,
                    365,
                    74,
                    paint
            );

            // DISTANCE
            paint.setTextAlign(
                    Paint.Align.RIGHT
            );

            paint.setTextSize(17);
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    "DISTANCE",
                    w - 180,
                    42,
                    paint
            );

            paint.setTextSize(22);
            paint.setColor(
                    Color.rgb(
                            100,
                            255,
                            150
                    )
            );

            canvas.drawText(
                    ((int) distance)
                            + " m",
                    w - 180,
                    73,
                    paint
            );

            // SPEED
            paint.setTextSize(17);
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    "SPEED",
                    w - 80,
                    42,
                    paint
            );

            paint.setTextSize(22);
            paint.setColor(
                    Color.rgb(
                            255,
                            100,
                            80
                    )
            );

            canvas.drawText(
                    ((int)
                            (speed * 10))
                            + " KM/H",
                    w - 20,
                    73,
                    paint
            );

            // XP BAR
            float xpNeeded =
                    Math.max(
                            1,
                            level * 100
                    );

            float xpProgress =
                    Math.min(
                            1f,
                            xp / xpNeeded
                    );

            float barLeft = 30;
            float barRight = 220;
            float barTop = 84;
            float barBottom = 96;

            paint.setColor(
                    Color.argb(
                            100,
                            255,
                            255,
                            255
                    )
            );

            canvas.drawRoundRect(
                    barLeft,
                    barTop,
                    barRight,
                    barBottom,
                    8,
                    8,
                    paint
            );

            paint.setColor(
                    Color.rgb(
                            80,
                            180,
                            255
                    )
            );

            canvas.drawRoundRect(
                    barLeft,
                    barTop,
                    barLeft
                            +
                            (
                                    barRight
                                    -
                                    barLeft
                            )
                            * xpProgress,
                    barBottom,
                    8,
                    8,
                    paint
            );

            // COMBO
            if (combo > 1) {

                paint.setTextAlign(
                        Paint.Align.CENTER
                );

                paint.setTextSize(24);

                paint.setColor(
                        Color.rgb(
                                255,
                                180,
                                30
                        )
                );

                canvas.drawText(
                        "COMBO x"
                                +
                                combo,
                        w / 2f,
                        135,
                        paint
                );
            }

            // Nitro bar
            drawNitroBar(
                    canvas
            );
        }

        // =========================
        // NITRO BAR
        // =========================

        private void drawNitroBar(
                Canvas canvas
        ) {

            int w = getWidth();
            int h = getHeight();

            float left =
                    w - 245;

            float right =
                    w - 30;

            float top =
                    h - 92;

            float bottom =
                    h - 76;

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.argb(
                            160,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawRoundRect(
                    left - 8,
                    top - 24,
                    right + 8,
                    bottom + 30,
                    12,
                    12,
                    paint
            );

            paint.setTextAlign(
                    Paint.Align.LEFT
            );

            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );

            paint.setTextSize(16);

            paint.setColor(
                    Color.WHITE
            );

            canvas.drawText(
                    "NITRO",
                    left,
                    top - 7,
                    paint
            );

            paint.setColor(
                    Color.DKGRAY
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    bottom,
                    8,
                    8,
                    paint
            );

            float progress =
                    Math.max(
                            0f,
                            Math.min(
                                    1f,
                                    nitro / 100f
                            )
                    );

            paint.setColor(
                    Color.rgb(
                            0,
                            210,
                            255
                    )
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    left
                            +
                            (
                                    right
                                    -
                                    left
                            )
                            * progress,
                    bottom,
                    8,
                    8,
                    paint
            );

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            paint.setTextSize(14);

            paint.setColor(
                    Color.WHITE
            );

            canvas.drawText(
                    ((int) nitro)
                            + "%",
                    (left + right) / 2f,
                    top + 13,
                    paint
            );
        }

        // =========================
        // GAME CONTROLS
        // =========================

        private void drawControls(
                Canvas canvas
        ) {

            int w = getWidth();
            int h = getHeight();

            // LEFT BUTTON
            drawControlButton(
                    canvas,
                    35,
                    h - 130,
                    125,
                    h - 35,
                    "◀"
            );

            // RIGHT BUTTON
            drawControlButton(
                    canvas,
                    145,
                    h - 130,
                    235,
                    h - 35,
                    "▶"
            );

            // NITRO BUTTON
            drawNitroButton(
                    canvas,
                    w - 150,
                    h - 150,
                    w - 35,
                    h - 35
            );

            // PAUSE BUTTON
            drawPauseSmallButton(
                    canvas,
                    w - 70,
                    120,
                    w - 20,
                    170
            );
        }

        // =========================
        // CONTROL BUTTON
        // =========================

        private void drawControlButton(
                Canvas canvas,
                float left,
                float top,
                float right,
                float bottom,
                String text
        ) {

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.argb(
                            155,
                            10,
                            15,
                            25
                    )
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    bottom,
                    22,
                    22,
                    paint
            );

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(3);

            paint.setColor(
                    Color.argb(
                            220,
                            255,
                            255,
                            255
                    )
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    bottom,
                    22,
                    22,
                    paint
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            paint.setTypeface(
                    Typeface.DEFAULT_BOLD
            );

            paint.setTextSize(42);

            paint.setColor(
                    Color.WHITE
            );

            canvas.drawText(
                    text,
                    (left + right) / 2f,
                    (top + bottom) / 2f
                            -
                            (
                                    paint.ascent()
                                    +
                                    paint.descent()
                            ) / 2f,
                    paint
            );
        }

        // =========================
        // NITRO BUTTON
        // =========================

        private void drawNitroButton(
                Canvas canvas,
                float left,
                float top,
                float right,
                float bottom
        ) {

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.argb(
                            190,
                            0,
                            100,
                            180
                    )
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    bottom,
                    25,
                    25,
                    paint
            );

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(4);

            paint.setColor(
                    Color.CYAN
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    bottom,
                    25,
                    25,
                    paint
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            paint.setTypeface(
                    Typeface.DEFAULT_BOLD
            );

            paint.setTextSize(20);

            paint.setColor(
                    Color.WHITE
            );

            canvas.drawText(
                    "NITRO",
                    (left + right) / 2f,
                    top + 36,
                    paint
            );

            paint.setTextSize(32);

            paint.setColor(
                    Color.YELLOW
            );

            canvas.drawText(
                    "⚡",
                    (left + right) / 2f,
                    top + 78,
                    paint
            );
        }

        // =========================
        // PAUSE SMALL BUTTON
        // =========================

        private void drawPauseSmallButton(
                Canvas canvas,
                float left,
                float top,
                float right,
                float bottom
        ) {

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.argb(
                            180,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    bottom,
                    12,
                    12,
                    paint
            );

            paint.setColor(
                    Color.WHITE
            );

            canvas.drawRoundRect(
                    left + 13,
                    top + 12,
                    left + 19,
                    bottom - 12,
                    3,
                    3,
                    paint
            );

            canvas.drawRoundRect(
                    left + 31,
                    top + 12,
                    left + 37,
                    bottom - 12,
                    3,
                    3,
                    paint
            );
        }

        // =========================
        // FINISH LINE
        // =========================

        private void drawFinishLine(
                Canvas canvas
        ) {

            int w = getWidth();
            int h = getHeight();

            float roadLeft =
                    w * 0.18f;

            float roadRight =
                    w * 0.82f;

            float y =
                    170;

            float square =
                    28;

            for (
                    int row = 0;
                    row < 2;
                    row++
            ) {

                for (
                        int col = 0;
                        col < 12;
                        col++
                ) {

                    if (
                            (row + col)
                                    % 2
                                    == 0
                    ) {

                        paint.setColor(
                                Color.WHITE
                        );

                    } else {

                        paint.setColor(
                                Color.BLACK
                        );
                    }

                    float x =
                            roadLeft
                            +
                            col
                            *
                            (
                                    (
                                            roadRight
                                            -
                                            roadLeft
                                    )
                                    / 12f
                            );

                    canvas.drawRect(
                            x,
                            y
                                    +
                                    row
                                    * square,
                            x
                                    +
                                    (
                                            (
                                                    roadRight
                                                    -
                                                    roadLeft
                                            )
                                            / 12f
                                    ),
                            y
                                    +
                                  (
                                            row + 1
                                    )
                                    * square,
                            paint
                    );
                }
            }
        }
        private void beep(
                int toneType
        ) {

            if (tone == null) {
                return;
            }

            try {

                tone.startTone(
                        toneType,
                        100
                );

            } catch (Exception ignored) {
            }
        }

        // =========================
        // VIBRATION
        // =========================

        private void vibrate(
                long duration
        ) {

            if (vibrator == null) {
                return;
            }

            try {

                if (Build.VERSION.SDK_INT >= 26) {

                    vibrator.vibrate(
                            android.os.VibrationEffect
                                    .createOneShot(
                                            duration,
                                            android.os.VibrationEffect
                                                    .DEFAULT_AMPLITUDE
                                    )
                    );

                } else {

                    vibrator.vibrate(
                            duration
                    );
                }

            } catch (Exception ignored) {
            }
        }

        // =========================
        // BACK
        // =========================

        public void handleBack() {

            if (screen == GAME) {

                screen = PAUSE;

            } else if (screen == PAUSE) {

                screen = GAME;

            } else if (screen != MENU) {

                screen = MENU;
            }
        }
    }
                }
