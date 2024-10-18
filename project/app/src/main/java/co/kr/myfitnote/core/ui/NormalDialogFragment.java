package co.kr.myfitnote.core.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import co.kr.myfitnote.R;

public class NormalDialogFragment extends DialogFragment {

    private String message;
    private TextView titleTV, messageTV;
    private Button negativeButton, positiveButton;
    private View.OnClickListener positiveButtonEvent;
    private View.OnClickListener negativeButtonEvent;

    private boolean usePositiveButton = true;
    private boolean useNegativeButton = true;

    public NormalDialogFragment(String message,
                                View.OnClickListener positiveButtonEvent,
                                View.OnClickListener negativeButtonEvent) {
        this.message = message;
        this.positiveButtonEvent = positiveButtonEvent;
        this.negativeButtonEvent = negativeButtonEvent;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set View
        View view = inflater.inflate(R.layout.dialog_normal, null);
        titleTV = view.findViewById(R.id.title);
        messageTV = view.findViewById(R.id.message);
        negativeButton = view.findViewById(R.id.negative_button);
        positiveButton = view.findViewById(R.id.positive_button);

        if (!usePositiveButton)
            positiveButton.setVisibility(View.GONE);

        if (!useNegativeButton)
            negativeButton.setVisibility(View.GONE);

        builder.setView(view);

        titleTV.setText("알림");
        messageTV.setText(message);

        if (positiveButtonEvent != null)
            positiveButton.setOnClickListener(positiveButtonEvent);
        else {
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        if (negativeButtonEvent != null)
            negativeButton.setOnClickListener(negativeButtonEvent);
        else{
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.radius_30);

        return dialog;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setTitle(String title){
        titleTV.setText(title);
    }

    public void setUseNegativeButton(boolean useNegativeButton) {
        this.useNegativeButton = useNegativeButton;
    }

    public void setUsePositiveButton(boolean usePositiveButton) {
        this.usePositiveButton = usePositiveButton;
    }
}
