package com.example.cripz.bluetoothrccarcontroller;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProgrammingFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private View rootView;

    public static boolean runProgramFlag = false;
    public static String action = "";
    public static String condition = "";
    public static String sensorSign = "";
    public static String sensorType = "";
    public static String sensorValue = "";
    private int selectedProgram;
    private String selectedAction = "no_action";
    private String selectedCondition = "no_condition";
    private EditText et;
    private HashMap<String, String> programsConfig = new HashMap<>();
    private List<String> sensor = new ArrayList<>();
    private List<String> signs = new ArrayList<>();


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

    private void sendMessage(String msg) {
        ((Main)(getActivity())).sendMessage(msg);
    }

    public void showFilesInDirectory(String filesType) {

        int jsonCounter = 0;
        //checks how many json files are in the directory
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
                    .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            selectedProgram = which;
                            return true;
                        }
                    })
                    .alwaysCallSingleChoiceCallback()
                    .positiveText("Run")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            runProgram(prList[selectedProgram]);
                        }
                    })
                    .negativeText("Delete")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            File file = new File(getActivity().getFilesDir().getPath(), prList[selectedProgram]);
                            file.delete();
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

    private void fileWrite(String fileName, String text) {
        try {
            File file = new File(getActivity().getFilesDir().getPath(), fileName);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runProgram(String file_string) {
        try {
            runProgramFlag = true;
            JSONObject obj = new JSONObject(fileRead(file_string));
            action = (String) obj.get("action");
            condition = (String) obj.get("condition");
            sensorSign = (String) obj.get("sensorSign");
            sensorType = (String) obj.get("sensorType");
            sensorValue = (String) obj.get("sensorValue");

//            eventHappen(sensorSign, sensorType, sensorValue);
//            Log.d("fr", "eventFlag: " + eventHappen(sensorSign, sensorType, sensorValue));
//
//
//
//            //Do action until event happens - 1
//            //Wait for event to happen and then do action - 2
//
//
//            if (condition.equals("Do action until event happens")) {
//                Log.d("dsa", "Do action...");
//                doAction(action);
//                if(eventHappen(sensorSign,sensorType,sensorValue)){
//                    doAction("stay");
//                }
//
//            } else if (condition.equals("Wait for event to happen and then do action")) {
//                Log.d("dsa", "Wait for event...");
//                if (eventHappen(sensorSign, sensorType, sensorValue)) {
//                    doAction(action);
//                    Log.d("asd", "event: True");
//                } else {
//                    Log.d("asd", "event: False");
//                }
//                // Log.d("asd","distanceInt:  " + Main.distanceInt);
//                //Log.d("asd","lightInt:  " + Main.lightInt);
//
////            Log.d("fr","condition: " + condition);
////            Log.d("fr","action: " + action);
////            Log.d("fr","sensorSign: " + sensorSign);
////            Log.d("fr","sensorType: " + sensorType);
////            Log.d("fr","sensorValue: " + sensorValue);
//
////            Log.d("fr", "-------DistanceInt: " + ((Main) getActivity()).getDistanceValue() + " ---------");
//
////            Log.d("fr","-------LightInt: "  + ((Main)getActivity()).getLightValue() + " ---------");
//
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void buttonInit() {
        Button buttonNew = (Button) rootView.findViewById(R.id.new_btn);
        Button buttonOpen = (Button) rootView.findViewById(R.id.open_btn);
        Button buttonStop = (Button) rootView.findViewById(R.id.stop_btn);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("v"); //lights_off
                sendMessage("k"); //stop forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
            }
        });

        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCondition();
            }
        });
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilesInDirectory(".json");
            }
        });
    }

    private void setCondition() {
        //Do action until event happens - 1
        //Wait for event to happen and then do action - 2
        final String[] conditions = {"Do action until event happens", "Wait for event to happen and then do action"};
        new MaterialDialog.Builder(getActivity())
                .title("Select Condition")
                .items(conditions)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        selectedCondition = conditions[which];
                        programsConfig.put("condition", selectedCondition);
                        setAction();
                    }
                })
                .show();
    }

    private void setAction() {
        String[] actions = {"go_forward", "go_backward", "go_forward_right", "go_backward_right", "go_forward_left",
                "go_backward_left", "lights_on", "lights_off", "stay"};
        new MaterialDialog.Builder(getActivity())
                .title("Select Action")
                .items(actions)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                selectedAction = "go_forward";
                                break;
                            case 1:
                                selectedAction = "go_backward";
                                break;
                            case 2:
                                selectedAction = "go_forward_right";
                                break;
                            case 3:
                                selectedAction = "go_backward_right";
                                break;
                            case 4:
                                selectedAction = "go_forward_left";
                                break;
                            case 5:
                                selectedAction = "go_backward_left";
                                break;
                            case 6:
                                selectedAction = "lights_on";
                                break;
                            case 7:
                                selectedAction = "lights_off";
                                break;
                            case 8:
                                selectedAction = "stay";
                                break;
                        }
                        programsConfig.put("action", selectedAction);
                        setEvent();
                    }
                })
                .show();
    }

    private void setEvent() {
        // String[] sensors = {"distance", "light"};
        // String[] signs = {"<", ">", "="};
        boolean wrapInScrollView = true;

        MaterialDialog.Builder md=new MaterialDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View stdView = factory.inflate(R.layout.events_view, null);
        Spinner spinner = (Spinner)stdView.findViewById(R.id.spinner);
        Spinner spinner2 = (Spinner)stdView.findViewById(R.id.spinner2);
        et = (EditText)stdView.findViewById(R.id.editText);

        spinner.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);

        sensor.add("distance");
        sensor.add("light");
        signs.add("=");
        signs.add("<");
        signs.add(">");



        ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sensor);
        sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sensorAdapter);

        ArrayAdapter<String> signAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, signs);
        signAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(signAdapter);

        md.title("Event options")
                .customView(stdView, wrapInScrollView)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        programsConfig.put("sensorValue", et.getText().toString());
                        saveFile();
                    }
                })
                .show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spinner:
                programsConfig.put("sensorType",sensor.get(position) );
                break;
            case R.id.spinner2:
                programsConfig.put("sensorSign",signs.get(position) );
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void saveFile() {
        new MaterialDialog.Builder(getActivity())
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Program name", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        JSONObject jsonObject = new JSONObject(programsConfig);
                        String jsonString = jsonObject.toString();
                        fileWrite(input.toString() + ".json", jsonString);
                        Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

}