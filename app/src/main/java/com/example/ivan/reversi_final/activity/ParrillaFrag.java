package com.example.ivan.reversi_final.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivan.reversi_final.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ParrillaFrag extends Fragment {
    public static final String PREFS = "com.example.ivan.reversi_final.activity";
    public static Activity activity;
    private static boolean playSounds = true;
    public CountDownTimer t;
    private int GridSize;
    private Cell[] board;
    private ImageAdapter boardAdapter;
    private GridView boardGrid;
    private TextView player1Score, player2Score;
    private TextView player2, player1;
    private Button showMoves;
    private int playerNo = 1, vs, gameMode;
    private String[] playerNames = new String[2];
    private boolean prevPlayerCantMove = false;
    private int[] score = {2, 2};
    private CountDownTimer timer;
    private long[] timeLeft = {60000, 60000};
    private Random random = new Random();
    private boolean timerRunning = false, gameOver = false;
    private long time, savedTime = -1;
    private String nombre;
    private boolean limittime = true;
    private int maxTime = 40;
    private TextView play_timer;
    private Bundle bundle;
    private Handler timerHandler = new Handler();
    private String cells_remaining;
    private int cells_r;
    private TextView t_cells_r;
    private boolean firstTime = true;
    private CountDownTimer times;
    boolean temps_finalitzat=false;
    private String startFormatedTime;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean timepass=true;
    private long time_start;
    private String savedtime;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int totalSecs = (int) (updatedTime / 1000);
            int mins = totalSecs / 60;
            int secs = totalSecs % 60;
            String valor = "" + mins + ":" + String.format("%02d", secs);
            play_timer.setText("" + mins + ":" + String.format("%02d", secs));
            play_timer.getTag((int) timeSwapBuff);
            timerHandler.postDelayed(this, 0);

        }
    };

    private static int segon = 1000;
    private ParrillaListener listener;

    public static void playBeep(Context context) {
        if (playSounds) {
            MediaPlayer mp = MediaPlayer.create(context, R.raw.beep);
            mp.start();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.parrilla_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getConfigurationMenu();  //NEW PART II

        play_timer = (TextView) getView().findViewById(R.id.play_timer);
        t_cells_r = (TextView) getView().findViewById(R.id.cells_remaining);

        // Set up score views
        player1Score = (TextView) getView().findViewById(R.id.player1_score);
        player2Score = (TextView) getView().findViewById(R.id.player2_score);



        activity = getActivity();
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);


        // Get intent from previous Activity with the bundle
        //bundle = activity.getIntent().getExtras();
        //nombre = bundle.getString("Name");
        //GridSize = bundle.getInt("GridSize");
        //vs = bundle.getInt("VS");
        //gameMode = bundle.getInt("GAME_MODE");
        //limittime = activity.getIntent().getExtras().getBoolean("CheckTime");
        if (savedInstanceState == null) {

            bundle = activity.getIntent().getExtras();
            initBoard();
            cellsRemaining(cells_r);
            if (limittime) {
                reverseTimer(maxTime,play_timer);
            } else if (limittime == false) {
                StartTimerNotChecked(timeSwapBuff);
            }

        } else {
            bundle = savedInstanceState.getBundle("Bundle");
            limittime = savedInstanceState.getBoolean("CheckTime");
            //maxTime = savedInstanceState.getInt("Time");
            savedtime =savedInstanceState.getString("Time");

            board = (Cell[]) savedInstanceState.getSerializable("Board");
            cells_r = savedInstanceState.getInt("cells_r");
            score[0] = savedInstanceState.getInt("score0");
            score[1] = savedInstanceState.getInt("score1");
            firstTime = savedInstanceState.getBoolean("first");
            timepass =savedInstanceState.getBoolean("timepass");

            player1Score.setText(getString(R.string.score) + " " + score[0]);
            player2Score.setText(getString(R.string.score) + " " + score[1]);


            cells_remaining = cells_r + " " + getString(R.string.cells_remaining);
            t_cells_r.setText(cells_remaining);


            if (limittime) {
                String second[] = savedtime.split(":");
                reverseTimer(Integer.parseInt(second[1]),play_timer);
            } else if (limittime == false) {
                String second[] = savedtime.split(":");
                StartTimerNotChecked(Integer.parseInt(second[1]));
            }


        }

        // Set up Player name tags
        player1 = (TextView) getView().findViewById(R.id.player1);
        player2 = (TextView) getView().findViewById(R.id.player2);

        // Set up grid view for tiles
        boardGrid = (GridView) getView().findViewById(R.id.gameboard_grid);

        // Set columns of grid view
        boardGrid.setNumColumns(GridSize);

        // Create image adapter passing the Cell[] and length of each side
        boardAdapter = new ImageAdapter(activity, board, GridSize, prefs.getBoolean(ParrillaFrag.PREFS + ".alt", false));

        // Set the grids adapter
        boardGrid.setAdapter(boardAdapter);

        if (vs == 0) {
            playerNames[1] = getString(R.string.cpu);
        }

        player1.setText(nombre);

        playSounds = prefs.getBoolean(ParrillaFrag.PREFS + ".sounds", true);

        showMoves = (Button) getView().findViewById(R.id.show_moves);
        showMoves.setEnabled(true);
        showMoves.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Queue queue = checkCanMove();
                while (!queue.isEmpty()) {
                    board[queue.remove()].setHinting(true);
                }
                boardAdapter.notifyDataSetChanged();
            }

        });

        // Creates an OnItemClickListener
        AdapterView.OnItemClickListener boardClickListener = new AdapterView.OnItemClickListener() {

            // Sets what will happen when an item is clicked
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (firstTime) {
                    firstTime = false;
                    listener.makeLog("LOG...\n");
                    listener.makeLog("Alias=" + nombre + "; Mida graella=" + GridSize + "\n");
                    listener.makeLog((limittime) ? "Control de temps\n" : "Sense control de temps\n");
                }
                if ((vs == 0 && playerNo == 1 && !gameOver)
                        || (vs == 1 && !gameOver)) {

                    playTurn(position);
                    listener.makeLog("Casilla seleccionada(" + position / GridSize + "," + position % GridSize + ")\n");

                    cellsRemaining(cells_r);
                    listener.makeLog("Nº caselles pendents=" + (board.length - (score[0] + score[1])) + "\n");
                    if (limittime)
                       listener.makeLog("Temps restant=" + play_timer.getText().toString() + " secs");
                }
            }

        };
        // Set the grid item listener
        boardGrid.setOnItemClickListener(boardClickListener);
    }

    private void getConfigurationMenu() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        nombre = prefs.getString(getResources().getString(R.string.USER),
                getResources().getString(R.string.DEFAULT));
        GridSize = Integer.valueOf(prefs.getString(getResources().getString(R.string.GRID_SIZE),
                getResources().getString(R.string.DEFAULT_GRID_SIZE)));
        limittime = prefs.getBoolean(getResources().getString(R.string.ACTIVE_TIME), true);
        maxTime = Integer.valueOf(prefs.getString(getResources().getString(R.string.TIME_SECONDS),
                "40"));

    }
    public void initBoard() {
        board = new Cell[GridSize * GridSize];

        // Initialise the Cells
        for (int i = 0; i < board.length; i++) {
            board[i] = new Cell();
            board[i].setPosition(i);
        }

        // Place starting pieces
        board[(board.length / 2) - (GridSize / 2) - 1].setState(2);
        board[(board.length / 2) - (GridSize / 2)].setState(1);
        board[(board.length / 2) + (GridSize / 2) - 1].setState(1);
        board[(board.length / 2) + (GridSize / 2)].setState(2);

        // Set default scores
        player1Score.setText(getString(R.string.score) + " " + score[0]);
        player2Score.setText(getString(R.string.score) + " " + score[1]);

    }

    public void reverseTimer(int Seconds,final TextView time){

        new CountDownTimer(Seconds* 1000+1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                time.setText(String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));


            }

            public void onFinish() {
                    if(timepass!=false) {
                        String log = "Has esgotat el temps!!";
                        sendResults(log);
                    }

            }
        }.start();
    }

    public void cellsRemaining(int cells_r) {
        cells_r = (board.length - (score[0] + score[1]));
        cells_remaining = cells_r + " " + getString(R.string.cells_remaining);
        t_cells_r.setText(cells_remaining);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Name", nombre);
        outState.putInt("GridSize", GridSize);
        outState.putBoolean("CheckTime", limittime);
        outState.putSerializable("Board", board);
        outState.putBoolean("first", firstTime);
        outState.putString("Time", play_timer.getText().toString());
        outState.putInt("cells_r", (board.length - (score[0] + score[1])));
        outState.putInt("score0", score[0]);
        outState.putInt("score1", score[1]);
        outState.putBoolean("timepass",timepass);
    }


    private void StartTimerNotChecked(long updatedTime) {


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        startFormatedTime = sdf.format(new Date());
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);
    }
 /*
    public void StartTimer(long maxTime) {
        CountDownTimer t = new CountDownTimer(maxTime, 1000) {

            public void onTick(long millisUntilFinished) {
                play_timer.setText("Et queden " + (millisUntilFinished / 1000) + " segons");
                play_timer.setTag(millisUntilFinished);
            }

            public void onFinish() {
                if (timepass == false) {
                    String log = "Has esgotat el temps!!";
                    sendResults(log);
                }
            }
        }.start();
    }
*/
    private void sendResults(String log) {
       // play_timer.setText(""+time_start);
        if (limittime) {
            Bundle b = new Bundle();

            b.putString("time", play_timer.getText().toString());
            b.putString("name", nombre);
            b.putInt("gridsize", GridSize);
            b.putInt("jugador1", score[0]);
            b.putInt("jugador2", score[1]);
            b.putString("log", log);
            b.putBoolean("checktime",limittime);


            Intent i = new Intent(activity, ResultsActivity.class);
            i.putExtras(b);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);


        } else {
            Bundle b = new Bundle();

            b.putString("time",  play_timer.getText().toString());
            b.putString("name", nombre);
            b.putInt("gridsize", GridSize);
            b.putInt("jugador1", score[0]);
            b.putInt("jugador2", score[1]);
            b.putString("log", log);

            Intent i = new Intent(activity, ResultsActivity.class);
            i.putExtras(b);
            startActivity(i);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gameMode == 0 && t != null) {
            savedTime = timeLeft[playerNo - 1];
            t.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (gameMode == 0 && t != null) {
            startTimer(savedTime);
            savedTime = -1;
        }
    }

    // Calculates each players core and passes back an array
    private int[] calcScore() {
        score[0] = 0;
        score[1] = 0;
        for (int n = 0; n < board.length; n++) {
            if (board[n].getState() == 1) {
                score[0]++;
            } else if (board[n].getState() == 2) {
                score[1]++;
            }
        }

        return score;
    }

    private void endGo() {
        cellsRemaining(cells_r);
        swapPlayers();
        if (checkCanMove().isEmpty()) {
            if (prevPlayerCantMove) {
                onGameEnd(1);
            } else {
                Toast cantMove = Toast.makeText(activity,
                        playerNames[playerNo - 1] + " cannot play a move",
                        Toast.LENGTH_LONG);
                cantMove.show();
                prevPlayerCantMove = true;
                endGo();
            }
        } else {
            prevPlayerCantMove = false;
        }
        if (vs == 0 && playerNo == 2) {
            cpuGo();
        }
    }

    // Swaps the playerNo and sets colour denoting who's go it is
    private void swapPlayers() {
        if (gameMode == 1 && timerRunning)
            stopTimer();
        if (playerNo == 1) {
            player1.setBackgroundColor(getResources().getColor(R.color.alpha));
            player2.setBackgroundColor(getResources().getColor(R.color.yellow));
            playerNo = 2;
        } else {
            player2.setBackgroundColor(getResources().getColor(R.color.alpha));
            player1.setBackgroundColor(getResources().getColor(R.color.yellow));
            playerNo = 1;
        }
        if (gameMode == 1) {
            timerRunning = true;
            startTimer(time);
        }
    }

    // This method checks if a move is valid and then creates a new board based
    // on the item clicked. It returns the new board, if the move is not valid
    // it returns a null board
    private Cell[] checkMove(int position) {
        // This will hold an array of positions that surround the chosen
        // position
        int[] direction;
        // Creates a new board from original
        Cell[] tempBoard = new Cell[board.length];
        for (int i = 0; i < board.length; i++) {
            board[i].setHinting(false);
            tempBoard[i] = new Cell();
            tempBoard[i].setState(board[i].getState());
        }

        // Sets inverse to enemy player number
        int inverse = 1;
        if (playerNo == 1) {
            inverse = 2;
        }

        boolean moveMade = false;

        // If the position is empty
        if (tempBoard[position].getState() == 0) {
            // Run checkValidMove which returns an array of positions
            // surrounding our position
            direction = checkValidMove(position);

            // Starting at up loop through all 8 positions surrounding chosen
            // one
            for (int i = 0; i < 8; i++) {
                // if the current direction (e.g. above us) is not an edge and
                // is an enemy token
                if (direction[i] != -1
                        && tempBoard[direction[i]].getState() == inverse) {
                    Queue queue = new Queue();
                    // add our starting position to queue
                    queue.add(position);
                    // create a temporary direction so we don't overwrite our
                    // current one
                    int tempDirection = direction[i];
                    // using -1 as an exit state
                    while (tempDirection != -1) {
                        // add next position to queue
                        queue.add(tempDirection);
                        // set tempDirection to checkValidMoveDirection which
                        // check one direction to see if it is a valid direction
                        // (i.e. not out of bounds)
                        tempDirection = checkValidMoveDirection(i + 1,
                                tempDirection);
                        // if not out of bounds and it is our players piece, add
                        // to the queue then exit while loop
                        if (tempDirection != -1
                                && tempBoard[tempDirection].getState() == playerNo) {
                            queue.add(tempDirection);
                            tempDirection = -1;
                        }
                        // if not out of bound but empty cell exit while loop
                        if (tempDirection != -1
                                && tempBoard[tempDirection].getState() == 0) {
                            tempDirection = -1;
                        }
                    }
                    // if the last piece in the queue is ours and the first cell
                    // is empty the set moveMade to true and add this queue of
                    // positions to our temporary game board as our pieces
                    if (tempBoard[queue.get(queue.size() - 1)].getState() == playerNo
                            && tempBoard[queue.remove()].getState() == 0) {
                        moveMade = true;
                        while (!queue.isEmpty()) {
                            tempBoard[queue.remove()].setState(playerNo);
                        }
                    }
                }
            }
        }
        // if no moves were made return a null board
        if (!moveMade) {
            tempBoard = null;
        }
        return tempBoard;
    }

    // Runs through each direction checking if the are an edge. returns array of
    // positions surrounding our position and -1 if that position is out of
    // bounds
    private int[] checkValidMove(int position) {
        int[] array = new int[8];
        for (int i = 0; i < 8; i++) {
            array[i] = checkValidMoveDirection(i + 1, position);
        }
        return array;
    }

    // returns position of our chosen direction and -1 if out of bounds
    private int checkValidMoveDirection(int direction, int position) {
        // 1=Up 2=UpRight 3=Right 4 = DownRight etc.
        switch (direction) {
            case 1:
                if (Math.floor((float) position / GridSize) != 0) {
                    return position - GridSize;
                }
                break;
            case 2:
                if (Math.floor((float) position / GridSize) != 0
                        && position % GridSize != GridSize - 1) {
                    return position - GridSize + 1;
                }
                break;
            case 3:
                if (position % GridSize != GridSize - 1

                        ) {
                    return position + 1;
                }
                break;
            case 4:
                if (position % GridSize != GridSize - 1
                        && Math.floor((float) position / GridSize) != GridSize - 1) {
                    return position + GridSize + 1;
                }
                break;
            case 5:
                if (Math.floor((float) position / GridSize) != GridSize - 1) {
                    return position + GridSize;
                }
                break;
            case 6:
                if (position % GridSize != 0
                        && Math.floor((float) position / GridSize) != GridSize - 1) {
                    return position + GridSize - 1;
                }
                break;
            case 7:
                if (position % GridSize != 0) {
                    return position - 1;
                }
                break;
            case 8:
                if (Math.floor((float) position / GridSize) != 0
                        && position % GridSize != 0) {
                    return position - GridSize - 1;
                }
                break;
        }
        return -1;
    }

    private Queue checkCanMove() {
        Queue positions = new Queue();
        for (int i = 0; i < board.length; i++) {
            if (checkMove(i) != null) {
                positions.add(i);
            }
        }
        return positions;
    }

    private void cpuGo() {
        new CountDownTimer(random.nextInt(1000) + 500, 500) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                Queue queuePos = checkCanMove();

                if (!queuePos.isEmpty()) {
                    int bestMove = 0, bestPos = queuePos.get(0);
                    while (!queuePos.isEmpty()) {
                        int tempMove = 0;
                        int position = queuePos.remove();
                        Cell[] tempBoard = checkMove(position);
                        for (int i = 0; i < tempBoard.length; i++) {
                            if (tempBoard[i].getState() == playerNo
                                    && board[i].getState() != playerNo) {
                                if (i == 0 || i == GridSize - 1
                                        || i == board.length - GridSize - 1
                                        || i == board.length - 1)
                                    tempMove += 10;
                                if (Math.floor(i / GridSize) == 0
                                        || Math.ceil(i / GridSize) == GridSize
                                        || i % GridSize == 0
                                        || i % GridSize == GridSize - 1)
                                    tempMove += 2;
                                tempMove++;
                            }
                        }
                        if (tempMove > bestMove) {
                            bestPos = position;
                        } else if (tempMove == bestMove) {
                            if (random.nextInt(2) == 1) {
                                bestPos = position;
                            }
                        }
                    }
                    if (playerNo == 2)
                        playTurn(bestPos);
                }
            }
        }.start();

    }

    private void playTurn(int position) {
        // Creates a temporary board and runs checkMove passing which
        // Cell has been clicked
        Cell[] tempBoard = checkMove(position);

        // If checkMove does not returns null board then set pieces on
        // real board, swap players and check and set scores

        if (tempBoard != null) {
            for (int i = 0; i < board.length; i++) {
                board[i].setState(tempBoard[i].getState());
            }
            board[position].setState(playerNo);
            calcScore();
            player1Score.setText(getString(R.string.score) + " " + score[0]);
            player2Score.setText(getString(R.string.score) + " " + score[1]);
            boardAdapter.notifyDataSetChanged();
            endGo();
        } else {
            // Creates a Toast
            Toast toast = Toast.makeText(activity,
                    "Not a valid move!", Toast.LENGTH_SHORT);
            // else show "Not a valid move!" toast
            toast.show();
        }


    }

    private void onGameEnd(int endType) {

        ContentResolver cr = activity.getContentResolver();
        ContentValues values = new ContentValues();
        boolean didPlayer1win = false;
        gameOver = true;


        AlertDialog.Builder builder = new AlertDialog.Builder(
                activity);
        switch (endType) {
            case 1:
                didPlayer1win = (score[0] > score[1]);
                builder.setMessage("The Winner is "
                        + (didPlayer1win ? playerNames[0] : playerNames[1]));
                break;
            case 2:
                didPlayer1win = (playerNo == 2);
                builder.setMessage(playerNames[playerNo - 1]
                        + " ran out of time! The Winner is "
                        + (didPlayer1win ? playerNames[0] : playerNames[1]));
                break;
        }


        builder.setTitle("Game Over");
        Dialog dialog = builder.create();

        dialog.show();

        if (score[0] < score[1]) {
            String log = "Has perdut!! ";
            timepass = false;
            sendResults(log);
        } else if (score[0] > score[1]) {
            String log = "Has guanyat!! ";
            timepass = false;
            sendResults(log);
        } else if (score[0] == score[1]) {
            String log = "Heu empatat!! ";
            timepass = false;
            sendResults(log);
        }
    }

    private void startTimer(long timerLength) {

        timer = new CountDownTimer(timerLength, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
            }
        }.start();

    }

    private void stopTimer() {
        timer.cancel();
    }

    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(getString(R.string.back_to_main_message))
                .setTitle(getString(R.string.back_to_main_title))
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                NavUtils.navigateUpFromSameTask(activity);
                            }
                        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void newGame() {
        Intent intent = activity.getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ParrillaListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ParrillaListener");
        }
    }

    void setParrillaListener(ParrillaListener listener) {
        this.listener = listener;
    }

    public interface ParrillaListener {
        void makeLog(String s);
    }
}
