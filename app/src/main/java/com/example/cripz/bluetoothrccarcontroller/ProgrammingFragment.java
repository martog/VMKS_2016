package com.example.cripz.bluetoothrccarcontroller;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ProgrammingFragment extends Fragment {
    private View rootView;
    private boolean eventHappenFlag;

    private void sendMessage(String msg) {
        ((Main) (getActivity())).sendMessage(msg);
    }

    public void showFilesInDirectory(String filesType) {
        int jsonCounter = 0;
        File programs[] = new File(getActivity().getFilesDir().getPath()).listFiles();
        for (File program : programs) {
            if (program.getName().contains(filesType)) {
                jsonCounter++;
            }
        }

        if (jsonCounter > 0) {
            final String prList[] = new String[jsonCounter];
            int arrayCounter = 0;
            for (File program : programs) {
                if (program.getName().contains(filesType)) {
                    prList[arrayCounter] = program.getName();
                    arrayCounter++;
                }
            }
            new MaterialDialog.Builder(getActivity())
                    .title("Programs")
                    .items(prList)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            runProgram(prList[which]);
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title("No available programs.")
                    .positiveText("OK")
                    .show();
        }

        /*
            final String prList[] = new String[programs.length];
            for (int i = 0; i < programs.length; i++) {
                if (programs[i].getName().contains(filesType)) {
                    prList[i] = programs[i].getName();
                }
            }
            new MaterialDialog.Builder(getActivity())
                    .title("Programs")
                    .items(prList)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            Log.d("err", prList[which]);
                            fileRead(prList[which]);
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title("No available programs.")
                    .neutralText("OK")
                    .show();
        }
        */
    }

    public String fileRead(String fileName) {
        File file = new File(getActivity().getFilesDir().getPath(), fileName);

        //Read text from file
        String text = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text = text + line.toString();
                text = text + "\n";
            }
            br.close();

            return text;

        } catch (IOException e) {
            Toast.makeText(getActivity(), "Can't open file", Toast.LENGTH_SHORT).show();
        }


        return text;

    }

    private void doAction(String action) {
        switch (action) {
            case "go_forward":
                sendMessage("f"); //go forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                break;
            case "go_backward":
                sendMessage("k"); //stop forward
                sendMessage("b"); //go backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                break;
            case "go_forward_right":
                sendMessage("f"); //go forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("r"); //go right
                break;
            case "go_forward_left":
                sendMessage("f"); //go forward
                sendMessage("g"); //stop backward
                sendMessage("l"); //go left
                sendMessage("j"); //stop right
                break;
            case "go_backward_right":
                sendMessage("k"); //stop forward
                sendMessage("b"); //go backward
                sendMessage("h"); //stop left
                sendMessage("r"); //go right
                break;
            case "go_backward_left":
                sendMessage("k"); //stop forward
                sendMessage("b"); //go backward
                sendMessage("l"); //go left
                sendMessage("j"); //stop right
                break;
            case "stay":
                sendMessage("k"); //stop forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                break;
            case "lights_on":
                sendMessage("k"); //stop forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                sendMessage("n"); //lights_on
                break;
            case "lights_off":
                sendMessage("k"); //stop forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                sendMessage("v"); //lights_off

        }


    }

    public void eventHappen(String sSign, String sType, String sValue) {
        // int realTimeDistance = ((Main)getActivity()).getDistanceValue();
        //int realTimeLight = ((Main)getActivity()).getLightValue();
        if (sType.equals("distance")) {
            switch (sSign) {
                case "<":
                    if (Main.distanceInt < Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
                case ">":
                    if (Main.distanceInt > Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
                case "=":
                    if (Main.distanceInt == Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
            }
        } else if (sType.equals("light")) {
            switch (sSign) {
                case "<":
                    if (Main.lightInt < Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
                case ">":
                    if (Main.lightInt > Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
                case "=":
                    if (Main.lightInt == Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
            }
        }
    }


    private void runProgram(String file_string) {
        try {
            JSONObject obj = new JSONObject(fileRead(file_string));
            String action = (String) obj.get("action");
            String condition = (String) obj.get("condition");
            String sensorSign = (String) obj.get("sensorSign");
            String sensorType = (String) obj.get("sensorType");
            String sensorValue = (String) obj.get("sensorValue");

            //Do action until event happens - 1
            //Wait for event to happen and then do action - 2

            if (condition.equals("Do action until event happens")) {
                Log.d("dsa", "Do action...");
                doAction(action);
                Log.d("dsa", "event: False");
                while (!eventHappenFlag) {
                    eventHappen(sensorSign, sensorType, sensorValue);
                }
                doAction("stay");
                Log.d("dsa", "event: True");

            } else if (condition.equals("Wait for event to happen and then do action")) {
                Log.d("dsa", "Wait for event...");
                doAction("stay");
                Log.d("dsa", "event: False");
                while (!eventHappenFlag) {
                    eventHappen(sensorSign, sensorType, sensorValue);
                }
                doAction(action);
                Log.d("dsa", "event: True");
                Log.d("dsa", "action: " + action);
            }
//                if(eventHappen(sensorSign, sensorType, sensorValue)){
//                    doAction(action);
//                   Log.d("asd", "event: True");
//                }else{
//                    Log.d("asd", "event: False");
//                }
            // Log.d("asd","distanceInt:  " + Main.distanceInt);
            //Log.d("asd","lightInt:  " + Main.lightInt);

//            Log.d("fr","condition: " + condition);
//            Log.d("fr","action: " + action);
//            Log.d("fr","sensorSign: " + sensorSign);
//            Log.d("fr","sensorType: " + sensorType);
//            Log.d("fr","sensorValue: " + sensorValue);
//            Log.d("fr","-------DistanceInt: "  + ((Main)getActivity()).getDistanceValue() + " ---------");
//            Log.d("fr","-------LightInt: "  + ((Main)getActivity()).getLightValue() + " ---------");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void buttonInit() {
        Button buttonNew = (Button) rootView.findViewById(R.id.new_btn);
        Button buttonOpen = (Button) rootView.findViewById(R.id.open_btn);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupBlocksFragment setupBlocksFragment = new SetupBlocksFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, setupBlocksFragment)
                        .commit();
            }
        });
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilesInDirectory(".json");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.programming_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buttonInit();
        /*
        programsConfig.put("action", "go_forward");
        programsConfig.put("another_option", "test");
        programsConfig.put("state", "asd");
        programsConfig.put("condition", "light");

        JSONObject jsonObject = new JSONObject(programsConfig);
        String jsonString = jsonObject.toString();
        fileWrite("newProgram.json", jsonString);

        String condition = "";
        String action = "";
        JSONObject obj = null;

        try {
            obj = new JSONObject(fileRead("newProgram.json"));
            action = (String) obj.get("action");
            condition = (String) obj.get("condition");
            switch (condition) {
                case "light":
                    sendMessage("n");
                    Main.carLights.setBackgroundResource(R.drawable.short_on);
                    Main.shortLightsFlag = true;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

*/
    }

}