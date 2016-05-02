package com.example.cripz.bluetoothrccarcontroller;

import android.os.Bundle;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SetupBlocksFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    View rootView;

    String selectedAction = "no_action";
    String selectedCondition = "no_condition";
    EditText et;
    HashMap<String, String> programsConfig = new HashMap<>();
    List<String>sensor = new ArrayList<>();
    List<String>signs = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.setup_blocks_fragment, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initButtons();
    }

    private void initButtons() {
        MyClickListener clickListener = new MyClickListener();
        Button action = (Button) rootView.findViewById(R.id.btn_action);
        Button condition = (Button) rootView.findViewById(R.id.btn_condition);
        Button event = (Button) rootView.findViewById(R.id.btn_event);
        Button play = (Button) rootView.findViewById(R.id.btn_play);
        Button stop = (Button) rootView.findViewById(R.id.btn_stop);
        Button save = (Button) rootView.findViewById(R.id.btn_save);
        action.setOnClickListener(clickListener);
        condition.setOnClickListener(clickListener);
        event.setOnClickListener(clickListener);
        play.setOnClickListener(clickListener);
        stop.setOnClickListener(clickListener);
        save.setOnClickListener(clickListener);
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
                    }
                })
                .show();
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

    public class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_action:
                    setAction();
                    break;
                case R.id.btn_event:
                    setEvent();
                    break;
                case R.id.btn_condition:
                    setCondition();
                    break;
                case R.id.btn_save:
                    new MaterialDialog.Builder(getActivity())
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input("Program name", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    JSONObject jsonObject = new JSONObject(programsConfig);
                                    String jsonString = jsonObject.toString();
                                    fileWrite(input.toString() + ".json", jsonString);
                                }
                            }).show();
                    break;
            }
        }
    }
}
