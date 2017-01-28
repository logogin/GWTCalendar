package logogin.gwt.user.ui.client.widget.datepicker;

import java.util.Date;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import logogin.gwt.logger.client.util.DateHelper;

/**
 * DatePicker
 * @author Pavel Danchenko
 * Feb 1, 2007
 *
 */
public class DatePicker extends HorizontalPanel {

    private CalendarPanel calendar;
    private Widget m_target;
    private Widget m_sender;
    
    public DatePicker() {
        super();
        m_target = new TextBox();
        m_target.setStyleName("DatePicker-Target");
        m_sender = new Image("img/datepicker/calendar.gif");
        m_sender.setStyleName("DatePicker-Sender");
        add(m_target);
        add(m_sender);
        calendar = new CalendarPanel(true, (HasText)m_target);
        ((SourcesClickEvents)m_sender).addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                showCalendar();
            }
        });
    }

    public DatePicker(Widget target, Widget sender) {
        this();
        m_target = target;
        setSender(sender);
    }
    
    public void showCalendar() {
        Date date = DateHelper.stringToDate(((HasText)m_target).getText());
        if ( null != date ) {
            calendar.setSelectedDate(date);
        }
        calendar.setPopupPosition(m_sender.getAbsoluteLeft()
                + m_sender.getOffsetWidth() + 10,
                m_sender.getAbsoluteTop() + 10);
        calendar.show();
    }
    
    public void setTarget(Widget target) {
        m_target = target;
    }

    public Widget getTarget() {
        return m_target;
    }
    public void setSender(Widget sender) {
        m_sender = sender;
        ((SourcesClickEvents)m_sender).addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                showCalendar();
            }
        });
    }

    public Widget getSender() {
        return m_sender;
    }

    public void setDate(Date date) {
        ((HasText)m_target).setText(DateHelper.dateToString(date));
    }

    public Date getDate() {
        return DateHelper.stringToDate(((HasText)m_target).getText());
    }
}
