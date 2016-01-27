package cubesystem.vn.notifyschedule.view;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class SetTime implements View.OnTouchListener, View.OnClickListener, View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

   private EditText editText;
   private Calendar myCalendar;
   private Context ctx;
   private boolean isShow;

   public SetTime(EditText editText, Context ctx){
       this.editText = editText;
       this.editText.setOnFocusChangeListener(this);
       this.editText.setOnTouchListener(this);
       this.editText.setOnClickListener(this);
       this.myCalendar = Calendar.getInstance();
       this.ctx = ctx;
       this.isShow = false;
   }

    private void update(){
        if (!isShow){
            int hour;
            int minute;
            try {
                String text = this.editText.getText().toString().trim();
                String[] pieces = text.split(":");
                hour = Integer.parseInt(pieces[0]);
                minute = Integer.parseInt(pieces[1]);
            }
            catch (Exception e){
                hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                minute = myCalendar.get(Calendar.MINUTE);
            }
            new TimePickerDialog(ctx, this, hour, minute, true).show();
            this.isShow = true;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        if(hasFocus){
            update();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        this.editText.setText(String.format("%02d:%02d", hourOfDay, minute));
        this.isShow = false;
    }

    @Override
    public void onClick(View v) {
        update();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        update();
        return true;
    }
}