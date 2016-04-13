package com.example.cripz.bluetoothrccarcontroller;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ControlsFragment extends Fragment {
    private View rootView;
    private ToggleButton a_o;
    private ImageButton forwardButton;
    private ImageButton reverseButton;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private SeekBar speedBar;
    private String speedState = "3";
    private boolean a_o_flag = false;

    private void sendMessage(String msg) {
        ((Main)(getActivity())).sendMessage(msg);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.controls_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeButtonVariables();
        initializeSpeedBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        sendMessage("k");
        sendMessage("g");
        sendMessage("j");
        sendMessage("h");
        sendMessage("v");
    }

    private void initializeButtonVariables() {
        MyTouchListener touchListener = new MyTouchListener();
        forwardButton = (ImageButton) rootView.findViewById(R.id.forward_btn);
        reverseButton = (ImageButton) rootView.findViewById(R.id.reverse_btn);
        leftButton = (ImageButton) rootView.findViewById(R.id.left_btn);
        rightButton = (ImageButton) rootView.findViewById(R.id.right_btn);
        a_o = (ToggleButton) rootView.findViewById(R.id.a_o_id);
        a_o.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sendMessage("p");
                    a_o_flag = true;
                }else{
                    sendMessage("o");
                    sendMessage("k");
                    sendMessage("g");
                    a_o_flag = false;
                }
            }
        });
        forwardButton.setOnTouchListener(touchListener);
        reverseButton.setOnTouchListener(touchListener);
        leftButton.setOnTouchListener(touchListener);
        rightButton.setOnTouchListener(touchListener);

    }

    private void initializeSpeedBar(){
        speedBar = (SeekBar) rootView.findViewById(R.id.speed_bar);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0) {
                    Toast.makeText(getActivity(), "low!", Toast.LENGTH_SHORT).show();
                    speedState = "1";
                    sendMessage(speedState);
                } else if(progress == 50) {
                    Toast.makeText(getActivity(), "medium!", Toast.LENGTH_SHORT).show();
                    speedState = "2";
                    sendMessage(speedState);
                } else if(progress == 100){
                    Toast.makeText(getActivity(), "high!", Toast.LENGTH_SHORT).show();
                    speedState = "3";
                    sendMessage(speedState);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int mProgress = seekBar.getProgress();
                if (mProgress > 0 && mProgress < 26) {
                    seekBar.setProgress(0);
                } else if (mProgress > 25 && mProgress < 76) {
                    seekBar.setProgress(50);
                } else {
                    seekBar.setProgress(100);
                }
            }
        });
    }

    public class MyTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!a_o_flag) {
                switch (v.getId()) {
                    case R.id.forward_btn:
                        //forward button is called
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            forwardButton.setImageResource(R.drawable.btn_forward_clicked);
                            sendMessage("f");
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            forwardButton.setImageResource(R.drawable.btn_forward);
                            sendMessage("k");
                        }
                        break;

                    case R.id.right_btn:
                        //right button is called
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            rightButton.setImageResource(R.drawable.btn_right_clicked);
                            sendMessage("r");
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            rightButton.setImageResource(R.drawable.btn_right);
                            sendMessage("j");
                        }
                        break;

                    case R.id.left_btn:
                        //left button is called
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            leftButton.setImageResource(R.drawable.btn_left_clicked);
                            sendMessage("l");
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            leftButton.setImageResource(R.drawable.btn_left);
                            sendMessage("h");
                        }
                        break;

                    case R.id.reverse_btn:
                        //backward button is called
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            reverseButton.setImageResource(R.drawable.btn_reverse_clicked);
                            sendMessage("b");
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            reverseButton.setImageResource(R.drawable.btn_reverse);
                            sendMessage("g");
                        }
                        break;
                }
            }
            return true;
        }
    }

}
