package com.project.diary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

//dialog fragment that will show the emojis to choose feeling
public class EmojiDialog extends AppCompatDialogFragment {
    private RadioGroup rg1, rg2, rg3;
    String feeling = "";
    private EmojiDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        //inflate layout
        View view = inflater.inflate(R.layout.layout_emoji_dialog, null);

        rg1 = view.findViewById(R.id.rdgroup1);

        builder.setView(view)
                .setTitle("What do you feel?")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (rg1.getCheckedRadioButtonId() == R.id.happy)
                            feeling = "happy";
                        else if (rg1.getCheckedRadioButtonId() == R.id.crazy)
                            feeling = "crazy";
                        else if (rg1.getCheckedRadioButtonId() == R.id.love)
                            feeling = "love";
                        else if (rg1.getCheckedRadioButtonId() == R.id.sad)
                            feeling = "sad";

                        else if (rg1.getCheckedRadioButtonId() == R.id.sick)
                            feeling = "sick";

                        else if (rg1.getCheckedRadioButtonId() == R.id.angry)
                            feeling = "angry";
/*
                        else if (rg2.getCheckedRadioButtonId() == R.id.surprised)
                            feeling = "surprised";
                        else if (rg2.getCheckedRadioButtonId() == R.id.cool)
                            feeling = "cool";
                        else if (rg2.getCheckedRadioButtonId() == R.id.worried)
                            feeling = "worried";
                        else if (rg2.getCheckedRadioButtonId() == R.id.nevermind)
                            feeling = "nevermind";
                        else if (rg3.getCheckedRadioButtonId() == R.id.sad)
                            feeling = "sad";
                        else if (rg3.getCheckedRadioButtonId() == R.id.crying)
                            feeling = "crying";
                        else if (rg3.getCheckedRadioButtonId() == R.id.sick)
                            feeling = "sick";
                        else if (rg3.getCheckedRadioButtonId() == R.id.angry)
                            feeling = "angry";
 */

                        //pass String feeling to interface
                        listener.applyFeeling(feeling);

                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EmojiDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement EmojiDialogListener");
        }
    }

    //interface to pass information from fragment to underlying Activity
    public interface EmojiDialogListener {
        void applyFeeling(String uFeeling);

    }
}
