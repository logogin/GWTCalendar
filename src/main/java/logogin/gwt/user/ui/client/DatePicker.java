package logogin.gwt.user.ui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DatePicker
 * @author Pavel Danchenko
 * Jan 26, 2007
 *
 */
public class DatePicker implements EntryPoint {

    public void onModuleLoad() {
        RootPanel.get().add(new logogin.gwt.user.ui.client.widget.datepicker.DatePicker());
    }

}
