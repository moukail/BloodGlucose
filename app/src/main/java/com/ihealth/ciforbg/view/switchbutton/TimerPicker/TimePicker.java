package com.ihealth.ciforbg.view.switchbutton.TimerPicker;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;

import com.ihealth.ciforbg.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lynn on 15-7-9.
 */
public class TimePicker extends FrameLayout {

    void callOnTimeChanged() { onTimeChanged(); }
    class OnMinuteChangeListener implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            callOnTimeChanged();
        }
    }

    private OnTimeChangedListener mOnTimeChangedListener;

    private final NumberPicker mHourSpinner;

    private final NumberPicker mMinuteSpinner;

    private Locale mCurrentLocale;
    private Calendar mTempCalendar;

    /**
     * A no-op callback used in the constructor to avoid null checks later in
     * the code.
     */
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() {
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        }
    };

    /**
     * The callback interface used to indicate the time has been adjusted.
     */
    public interface OnTimeChangedListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The current hour.
         * @param minute The current minute.
         */
        void onTimeChanged(TimePicker view, int hourOfDay, int minute);
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // initialization based on locale
        setCurrentLocale(Locale.getDefault());

        // process style attributes
        int layoutResourceId = R.layout.time_picker;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResourceId, this, true);

        // hour
        mHourSpinner = (NumberPicker) findViewById(R.id.hour);
        mHourSpinner.setMinValue(0);
        mHourSpinner.setMaxValue(23);
        mHourSpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        mHourSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                onTimeChanged();
            }
        });
        // minute
        mMinuteSpinner = (NumberPicker) findViewById(R.id.minute);
        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setMaxValue(59);
        mMinuteSpinner.setOnLongPressUpdateInterval(100);
        mMinuteSpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        mMinuteSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                int minValue = mMinuteSpinner.getMinValue();
                int maxValue = mMinuteSpinner.getMaxValue();
                if (oldVal == maxValue && newVal == minValue) {
                    int newHour = mHourSpinner.getValue() + 1;
                    mHourSpinner.setValue(newHour);
                } else if (oldVal == minValue && newVal == maxValue) {
                    int newHour = mHourSpinner.getValue() - 1;
                    mHourSpinner.setValue(newHour);
                }
                onTimeChanged();
            }
        });
        mMinuteSpinner.setOnValueChangedListener(new OnMinuteChangeListener()); // miui add

        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);

        // set to current time
//        setCurrentHour(mTempCalendar.get(Calendar.HOUR_OF_DAY));
//        setCurrentMinute(mTempCalendar.get(Calendar.MINUTE));
        setCurrentHour(0);
        setCurrentMinute(30);

        if (!isEnabled()) {
            setEnabled(false);
        }

        // If not explicitly specified this view is important for accessibility.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            if (getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
                setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            }
        }
    }

    private void setCurrentLocale(Locale locale) {
        if (locale.equals(mCurrentLocale)) {
            return;
        }
        mCurrentLocale = locale;
        mTempCalendar = Calendar.getInstance(locale);
    }

    /**
     * Set the current hour.
     */
    public void setCurrentHour(Integer currentHour) {
        // why was Integer used in the first place?
        if (currentHour == null || currentHour == getCurrentHour()) {
            return;
        }
        mHourSpinner.setValue(currentHour);
        onTimeChanged();
    }

    /**
     * @return The current hour in the range (0-23).
     */
    public Integer getCurrentHour() {
        int currentHour = mHourSpinner.getValue();
        return currentHour;
    }

    /**
     * Set the current minute (0-59).
     */
    public void setCurrentMinute(Integer currentMinute) {
        if (currentMinute == getCurrentMinute()) {
            return;
        }
        mMinuteSpinner.setValue(currentMinute);
        onTimeChanged();
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentMinute() {
        return mMinuteSpinner.getValue();
    }

    private void onTimeChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (mOnTimeChangedListener != null) {
            mOnTimeChangedListener.onTimeChanged(this, getCurrentHour(), getCurrentMinute());
        }
    }

    /**
     * Set the callback that indicates the time has been adjusted by the user.
     *
     * @param onTimeChangedListener the callback, should not be null.
     */
    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }
}
