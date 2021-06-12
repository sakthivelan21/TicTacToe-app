package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.view.View;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public int[][] board ={{0,0,0},{0,0,0},{0,0,0}};
    public int COMP=+1;
    public int HUMAN=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                Button button = findViewById(resID);
                button.setOnClickListener(this);
            }
        }
        Button buttonReset = findViewById(R.id.restart);
        buttonReset.setOnClickListener(v -> restartbutton());
    }

    public int evaluate(int[][] state)
    {
        int score=0;
        if(gameOver(state,COMP))
            score=+1;
        else if(gameOver(state,HUMAN))
            score=-1;
        return score;
    }
    public boolean gameOver(int[][] state, int player){
        int[][] fields=new int[][]{
                {state[0][0],state[0][1],state[0][2]},
                {state[1][0],state[1][1],state[1][2]},
                {state[2][0],state[2][1],state[2][2]},
                {state[0][0],state[1][0],state[2][0]},
                {state[0][1],state[1][1],state[2][1]},
                {state[0][2],state[1][2],state[2][2]},
                {state[0][0],state[1][1],state[2][2]},
                {state[2][0],state[1][1],state[0][2]}
        };
        for(int i=0;i<8;i++)
        {
            int c=0;
            for(int j=0;j<3;j++)
            {
                if(fields[i][j]==player)
                    c+=1;
            }
            if(c==3)
                return true;
        }
        return false;
    }
    public boolean gameAllOver(int [][]state)
    {
        return (gameOver(state,COMP)||gameOver(state,HUMAN));
    }
    public int[][] emptycells(int [][]state)
    {   int c=0,r=0;
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(state[i][j]==0)
                    c+=1;
            }
        }
        int [][] cells=new int[c][2];
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(state[i][j]==0)
                {cells[r][0]=i;cells[r][1]=j;r+=1;}
            }
        }
        return cells;
    }
    public boolean validateMove(int x,int y)
    {
        try {
            return board[x][y] == 0;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    public boolean setMove(int x,int y,int player)
    {
        if(validateMove(x,y))
        {
            board[x][y]=player;
            return true;
        }
        else
            return false;
    }
    public int[] minimax(int [][]state, int depth, int player)
    {
        int [] best;
        if(player==COMP)
        {
            best= new int[]{-1, -1, -1000};
        }
        else
            best=new int[]{-1,-1,1000};
        if(depth==0 || gameAllOver(state))
        {
            int score=evaluate(state);
            return new int[] {-1, -1,score};
        }
        int[][] cells =emptycells(state);
        for (int[] cell : cells) {
            int x = cell[0], y = cell[1];
            state[x][y] = player;
            int[] score = minimax(state, depth - 1, -player);
            score[0] = x;
            score[1] = y;
            state[x][y] = 0;
            if(player==COMP)
            { if (score[2] > best[2]) {
                best = score;
            }}
            else {
                if (score[2] < best[2]) {
                    best = score;
                }
            }
        }
        return best;
    }
    public void aiturn()
    {
        int x,y;
        if(emptycells(board).length==9)
        {
            x=(int)(Math.random()*3);
            y=(int)(Math.random()*3);
        }
        else
        {
            int []cell=minimax(board,emptycells(board).length,COMP);
            x=cell[0];
            y=cell[1];
        }
        boolean hi=setMove(x,y,COMP);
        if(hi)
        {
            String buttonID = "button_" + x + y;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            Button but =findViewById(resID);
            but.setText("O");
        }
    }
    @SuppressLint("SetTextI18n")
    private void restartbutton() {
        Button buttonRestart = findViewById(R.id.restart);
        String text=buttonRestart.getText().toString();
        if(text.equals("start AI"))
        {  buttonRestart.setVisibility(View.INVISIBLE);
            aiturn();
            buttonRestart.setEnabled(false);

        }
        else if(text.equals("restart"))
        {
            for(int i=0;i<3;i++)
            {
                for(int j=0;j<3;j++)
                {
                    board[i][j]=0;
                    String buttonID = "button_" + i + j;
                    int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                    Button but =findViewById(resID);
                    but.setText("");
                    but.setTextColor(Color.parseColor("#000000"));
                }
            }
            TextView msg=findViewById(R.id.message);
            msg.setVisibility(TextView.INVISIBLE);
            buttonRestart.setText("start AI");
            buttonRestart.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        Button buttonRestart = findViewById(R.id.restart);
        buttonRestart.setEnabled(false);
        buttonRestart.setVisibility(View.INVISIBLE);
        if((emptycells(board).length>0)&&(!gameAllOver(board)))
        {
            //to get the id name of the button being clicked
            String idName=v.getResources().getResourceName(v.getId());
            String []sam=idName.split("_");
            int x=sam[1].charAt(0)-'0';
            int y=sam[1].charAt(1)-'0';
            if(setMove(x,y,HUMAN))
            {
                ((Button) v).setText("X");
                if((emptycells(board).length>0)&&(!gameAllOver(board)))
                    aiturn();
            }
        }
        if(gameOver(board,COMP))
        {
            int [][]lines = new int[3][3];
            if(board[0][0]==1 && board[0][1]==1 && board[0][2]==1)
                lines= new int[][]{{0, 0}, {0, 1}, {0, 2}};
            else if(board[1][0]==1 && board[1][1]==1 && board[1][2]==1)
                lines=new int[][]{{1,0},{1,1},{1,2}};
            else if(board[2][0]==1 && board[2][1]==1 && board[2][2]==1)
                lines=new int[][]{{2,0},{2,1},{2,2}};
            else if(board[0][0]==1 && board[1][0]==1 && board[2][0]==1)
                lines=new int[][]{{0,0},{1,0},{2,0}};
            else if(board[0][1]==1 && board[1][1]==1 && board[2][1]==1)
                lines=new int [][]{{0,1},{1,1},{2,1}};
            else if(board[0][2]==1 && board[1][2]==1 && board[2][2]==1)
                lines=new int[][]{{0,2},{1,2},{2,2}};
            else if(board[0][0]==1 && board[1][1]==1 && board[2][2]==1)
                lines=new int[][]{{0,0},{1,1},{2,2}};
            else if(board[2][0]==1 && board[1][1]==1 && board[0][2]==1)
                lines=new int[][]{{2,0},{1,1},{0,2}};
            for(int i=0;i<3;i++)
            {
                String buttonID = "button_"+lines[i][0]+lines[i][1];
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                Button button = findViewById(resID);
                button.setTextColor(Color.parseColor("#FF0000"));
            }
            TextView msg=findViewById(R.id.message);
            msg.setVisibility(TextView.VISIBLE);
            msg.setText("AI Wins!!!");
            Toast.makeText(this,"AI wins!!!",Toast.LENGTH_SHORT).show();
        }
        if(!gameAllOver(board) && emptycells(board).length==0)
        {
            TextView msg=findViewById(R.id.message);
            msg.setVisibility(TextView.VISIBLE);
            msg.setText("Draw!!!");
            Toast.makeText(this,"Draw!!!",Toast.LENGTH_SHORT).show();
        }
        if(gameAllOver(board) || emptycells(board).length==0)
        {
            buttonRestart.setText("restart");
            buttonRestart.setVisibility(View.VISIBLE);
            buttonRestart.setEnabled(true);
        }

    }
}